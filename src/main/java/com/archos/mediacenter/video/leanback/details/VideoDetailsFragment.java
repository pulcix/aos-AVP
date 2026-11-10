// Copyright 2017 Archos SA
// Copyright 2020 Courville Software
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.archos.mediacenter.video.leanback.details;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.transition.Slide;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.leanback.app.BackgroundManager;
import androidx.leanback.app.DetailsFragmentWithLessTopOffset;
import androidx.leanback.transition.TransitionHelper;
import androidx.leanback.transition.TransitionListener;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ClassPresenterSelector;
import androidx.leanback.widget.DetailsOverviewRow;
import androidx.leanback.widget.FullWidthDetailsOverviewSharedElementHelper;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.HorizontalGridView;
import androidx.leanback.widget.ItemAlignmentFacet;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.OnActionClickedListener;
import androidx.leanback.widget.OnChildViewHolderSelectedListener;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.palette.graphics.Palette;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.archos.environment.ArchosFeatures;
import com.archos.environment.ArchosUtils;
import com.archos.environment.NetworkState;
import com.archos.filecorelibrary.FileUtils;
import com.archos.filecorelibrary.FileUtilsQ;
import com.archos.mediacenter.filecoreextension.UriUtils;
import com.archos.mediacenter.utils.trakt.TraktService;
import com.archos.mediacenter.utils.videodb.VideoDbInfo;
import com.archos.mediacenter.utils.videodb.XmlDb;
import com.archos.mediacenter.video.CustomApplication;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.browser.BootupRecommandationService;
import com.archos.mediacenter.video.browser.BrowserByIndexedVideos.lists.ListDialog;
import com.archos.mediacenter.video.browser.Delete;
import com.archos.mediacenter.video.browser.adapters.mappers.TvshowCursorMapper;
import com.archos.mediacenter.video.browser.adapters.mappers.VideoCursorMapper;
import com.archos.mediacenter.video.browser.adapters.object.Episode;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import com.archos.mediacenter.video.browser.adapters.object.NonIndexedVideo;
import com.archos.mediacenter.video.browser.adapters.object.Tvshow;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediacenter.video.browser.loader.EpisodesLoader;
import com.archos.mediacenter.video.browser.loader.ListsLoader;
import com.archos.mediacenter.video.browser.loader.MoviesLoader;
import com.archos.mediacenter.video.browser.loader.NextEpisodeLoader;
import com.archos.mediacenter.video.browser.loader.TvshowLoader;
import com.archos.mediacenter.video.browser.loader.VideoLoader;
import com.archos.mediacenter.video.browser.subtitlesmanager.SubtitleManager;
import com.archos.mediacenter.video.info.MultipleVideoLoader;
import com.archos.mediacenter.video.info.SortByFavoriteSources;
import com.archos.mediacenter.video.info.VideoInfoActivity;
import com.archos.mediacenter.video.info.VideoInfoCommonClass;
import com.archos.mediacenter.video.leanback.BackdropTask;
import com.archos.mediacenter.video.leanback.CompatibleCursorMapperConverter;
import com.archos.mediacenter.video.leanback.adapter.object.WebPageLink;
import com.archos.mediacenter.video.leanback.channels.ChannelManager;
import com.archos.mediacenter.video.leanback.filebrowsing.ListingActivity;
import com.archos.mediacenter.video.leanback.movies.AllMoviesGridFragment;
import com.archos.mediacenter.video.leanback.overlay.Overlay;
import com.archos.mediacenter.video.leanback.presenter.CustomDpadListRowPresenter;
import com.archos.mediacenter.video.leanback.presenter.PosterImageCardPresenter;
import com.archos.mediacenter.video.leanback.presenter.PresenterUtils;
import com.archos.mediacenter.video.leanback.presenter.ScraperImageBackdropPresenter;
import com.archos.mediacenter.video.leanback.presenter.ScraperImagePosterPresenter;
import com.archos.mediacenter.video.leanback.presenter.TrailerPresenter;
import com.archos.mediacenter.video.leanback.presenter.VideoBadgePresenter;
import com.archos.mediacenter.video.leanback.presenter.WebLinkPresenter;
import com.archos.mediacenter.video.leanback.scrapping.ManualVideoScrappingActivity;
import com.archos.mediacenter.video.leanback.tvshow.TvshowActivity;
import com.archos.mediacenter.video.leanback.tvshow.TvshowFragment;
import com.archos.mediacenter.video.leanback.wizard.SubtitlesWizardActivity;
import com.archos.mediacenter.video.picasso.ThumbnailRequestHandler;
import com.archos.mediacenter.video.player.PlayerActivity;
import com.archos.mediacenter.video.player.PrivateMode;
import com.archos.mediacenter.video.ui.NovaProgressDialog;
import com.archos.mediacenter.video.utils.DbUtils;
import com.archos.mediacenter.video.utils.ExternalPlayerResultListener;
import com.archos.mediacenter.video.utils.ExternalPlayerWithResultStarter;
import com.archos.mediacenter.video.utils.PlayUtils;
import com.archos.mediacenter.video.utils.StoreRatingDialogBuilder;
import com.archos.mediacenter.video.utils.SubtitlesDownloaderActivity2;
import com.archos.mediacenter.video.utils.ThemeManager;
import com.archos.mediacenter.video.utils.VideoMetadata;
import com.archos.mediacenter.video.utils.VideoPreferencesCommon;
import com.archos.mediacenter.video.utils.WebUtils;
import com.archos.mediaprovider.video.ListTables;
import com.archos.mediaprovider.video.VideoStore;
import com.archos.mediaprovider.video.VideoStoreImportImpl;
import com.archos.mediaprovider.video.VideoStoreInternal;
import com.archos.mediascraper.BaseTags;
import com.archos.mediascraper.EpisodeTags;
import com.archos.mediascraper.MovieTags;
import com.archos.mediascraper.Scraper;
import com.archos.mediascraper.ScraperImage;
import com.archos.mediascraper.ScraperTrailer;
import com.archos.mediascraper.ShowTags;
import com.archos.mediascraper.VideoTags;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class VideoDetailsFragment extends DetailsFragmentWithLessTopOffset implements LoaderManager.LoaderCallbacks<Cursor>, PlayUtils.SubtitleDownloadListener, SubtitleInterface, Delete.DeleteListener, XmlDb.ResumeChangeListener, ExternalPlayerWithResultStarter {

    private static final Logger log = LoggerFactory.getLogger(VideoDetailsFragment.class);

    /** A serialized com.archos.mediacenter.video.leanback.adapter.object.Video */
    public static final String EXTRA_VIDEO = "VIDEO";
    public static final String EXTRA_LIST_ID = "list_id";
    public static final String EXTRA_FORCE_VIDEO_SELECTION = "force_video_selection";
    /** The id of the video in the MediaDB (long) */
    public static final String EXTRA_VIDEO_ID = VideoInfoActivity.EXTRA_VIDEO_ID;
    public static final String EXTRA_LAUNCHED_FROM_PLAYER = VideoInfoActivity.EXTRA_LAUNCHED_FROM_PLAYER;
    public static final String EXTRA_SHOULD_LOAD_BACKDROP = "should_load_backdrop";

    public static final int REQUEST_CODE_LOCAL_RESUME_AFTER_ADS_ACTIVITY = 985;
    public static final int REQUEST_CODE_REMOTE_RESUME_AFTER_ADS_ACTIVITY = 986;
    public static final int REQUEST_CODE_SUBTITLES_ACTIVITY = 987;
    public static final int REQUEST_CODE_RESUME_AFTER_ADS_ACTIVITY = 988;
    public static final int REQUEST_CODE_PLAY_FROM_BEGIN_AFTER_ADS_ACTIVITY = 989;
    public static final int PLAY_ACTIVITY_REQUEST_CODE = 990;
    public static final int REQUEST_CODE_SCRAP = 991;

    private static final int INDEX_MAIN = 0;
    private static final int INDEX_FILELIST = 1;
    private int INDEX_SUBTITLES = 1;
    private int INDEX_FILEDETAILS = 2;

    /** pre-play subtitle download dialog is displayed only in case the wait is long than DIALOG_LAUNCH_DELAY_MS */
    private static final int DIALOG_LAUNCH_DELAY_MS = 2000;
    private static final String DEFAULT_TMDB_API_KEY = "051012651ba326cf5b1e2f482342eaa2";
    private static final String TMDB_IMAGE_W185_URL = "https://image.tmdb.org/t/p/w185";
    private static final String TMDB_IMAGE_W500_URL = "https://image.tmdb.org/t/p/w500";
    private static final int MAX_RELATED_CANDIDATES = 40;
    private static final int MAX_RELATED_RESULTS = 5;

    /** The video for which we are displaying the details. This object is updated each time we have a DB update */
    private Video mVideo;
    private static boolean mIsVideoWatched = false; // TOFIX: adding internal state since trakt sync can occur much later

    /** DB id of the video to display (in case we do not get the video object directly)*/
    private long mVideoIdFromPlayer;

    /** If we don't have the video object and we don't have the video ID, we must at least have the file path (non indexed case)*/
    private String mVideoPathFromPlayer;

    /** given by PlayerActivity when we are launched by it */
    private boolean mLaunchedFromPlayer;
    private boolean mRepeatModeDetected = false;

    /** given by PlayerActivity when we are launched by it */
    private VideoMetadata mVideoMetadataFromPlayer;

    /** given by PlayerActivity when we are launched by it */
    private int mPlayerType;

    private long mOnlineId = -1;

    /**
     * Flag to update all when back from player, because a lot of things may have been changed in the Video Details
     * launched from the player (VideoDetailsOverlayActivity)
     * In that case we did not get the update from the Loader because we were in background
     */
    private boolean mResumeFromPlayer;
    private boolean mFirstOnResume = true;

    private Overlay mOverlay;

    private ArchosDetailsOverviewRowPresenter mOverviewRowPresenter;
    private VideoDetailsDescriptionPresenter mDescriptionPresenter;
    private ArrayObjectAdapter mAdapter;
    private FileDetailsRow mFileDetailsRow;
    private SubtitlesDetailsRow mSubtitlesDetailsRow;
    private Row mPlotAndGenresRow;
    private Row mCastRow;
    private Row mPostersRow;
    private Row mBackdropsRow;

    private DetailsOverviewRow mDetailsOverviewRow;
    private View mInfuseDarkOverlay;

    private AsyncTask mDetailRowBuilderTask;
    private AsyncTask mBackdropTask;
    private AsyncTask mVideoInfoTask;
    private AsyncTask mFullScraperTagsTask;
    private AsyncTask mSubtitleFilesListerTask;
    private AsyncTask mPosterSaverTask;
    private AsyncTask mBackdropSaverTask;
    private DialogRetrieveSubtitles mDialogRetrieveSubtitles;
    private boolean mDownloadingSubs;

    List<SubtitleManager.SubtitleFile> mExternalSubtitles;

    /** the next episode, if there is one */
    Episode mNextEpisode;
    private boolean mIsTvEpisode = false;
    private boolean mHasRetrievedDetails;
    private Handler mHandler;
    private ListRow mTrailersRow;
    private List<ScraperTrailer> mTrailers;
    private List<ScraperImage> mAvailablePosters = new ArrayList<>();
    private List<ScraperImage> mAvailableBackdrops = new ArrayList<>();
    private ListRow mRelatedRow;
    private ListRow mSeasonEpisodesRow;
    private OnItemViewClickedListener mItemViewClickedListener;
    private AsyncTask<Video, Void, Pair<Bitmap, Video>> mThumbnailAsyncTask;
    private Bitmap mThumbnail;
    private boolean mAnimationIsRunning;

    private int mColor;
    private static int dominantColor = 0;
    private ArrayList<Video> mVideoList;
    private ArrayObjectAdapter mFileListAdapter;
    private SelectableListRow mFileListRow;
    private boolean giveOldVideo;
    private SelectableListRowPresenter mFileListRowPresenter;
    private boolean mSelectCurrentVideo;
    private Bitmap mPoster;
    private HashMap<Uri, List<SubtitleManager.SubtitleFile>> mSubtitleListCache;
    private HashMap<String, VideoMetadata> mVideoMetadateCache;
    private VideoBadgePresenter mVideoBadgePresenter;
    private boolean mShouldLoadBackdrop;
    private Uri mLastIndexed;
    private boolean mShouldUpdateRemoteResume;
    private boolean mShouldDisplayRemoveFromList;
    private boolean mShouldDisplayConfirmDelete = false;

    private boolean isFilePlayable = true;
    private int oldPos = 0;
    private int oldSelectedSubPosition = 0;

    // need to be static otherwise ActivityResultLauncher find them null
    private static Delete delete;
    private static List<Uri> deleteUrisList = null;

    private final ActivityResultLauncher<IntentSenderRequest> deleteLauncher = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
            result -> { // result can be RESULT_OK, RESULT_CANCELED
                if (log.isDebugEnabled()) log.debug("ActivityResultLauncher deleteLauncher: result {}", result);
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (log.isDebugEnabled()) log.debug("ActivityResultLauncher deleteLauncher: OK, deleteUris {}", (deleteUrisList != null ? Arrays.toString(deleteUrisList.toArray()) : null));
                    if (delete != null && deleteUrisList != null && !deleteUrisList.isEmpty()) {
                        if (log.isDebugEnabled()) log.debug("ActivityResultLauncher deleteLauncher: calling delete.deleteOK on {}", deleteUrisList.get(0));
                        delete.deleteOK(deleteUrisList.get(0));
                    }
                } else {
                    if (log.isDebugEnabled()) log.debug("ActivityResultLauncher deleteLauncher: NO, deleteUris {}", (deleteUrisList != null ? Arrays.toString(deleteUrisList.toArray()) : null));
                    if (delete != null && deleteUrisList != null && deleteUrisList.size() > 1) {
                        delete.deleteNOK(deleteUrisList.get(0));
                    }
                }
            });

    private boolean isInfuseThemeActive() {
        Context context = getContext();
        if (context == null) context = getActivity();
        return context != null && ThemeManager.getInstance(context).isInfuseTheme();
    }

    private static String getTmdbApiKey(Context context) {
        if (context == null) return DEFAULT_TMDB_API_KEY;
        try {
            int resId = context.getResources().getIdentifier("tmdb_api_key", "string", context.getPackageName());
            if (resId != 0) {
                String key = context.getString(resId);
                if (key != null && !key.trim().isEmpty()) {
                    return key.trim();
                }
            }
        } catch (Exception ignored) {
        }
        return DEFAULT_TMDB_API_KEY;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (log.isDebugEnabled()) log.debug("onCreate");
        FileUtilsQ.setDeleteLauncher(deleteLauncher);
        CustomApplication.resetLastVideoPlayed();

        final Activity activity = getActivity();
        if (activity == null) return;

        final Intent intent = activity.getIntent();
        final boolean isInfuse = ThemeManager.getInstance(activity).isInfuseTheme();
        final int defaultPrimaryColor = ThemeManager.getInstance(activity).getDetailsPrimaryColor();

        mSubtitleListCache = new HashMap<>();
        mVideoMetadateCache = new HashMap<>();
        mShouldDisplayRemoveFromList = intent.getLongExtra(EXTRA_LIST_ID, -1) != -1;

        Object transition = TransitionHelper.getEnterTransition(activity.getWindow());
        if (transition != null) {
            mAnimationIsRunning = false;
            TransitionHelper.addTransitionListener(transition, new TransitionListener() {
                @Override
                public void onTransitionStart(Object transition) {
                    mAnimationIsRunning = true;
                    if (mOverlay != null) mOverlay.hide();
                }

                @Override
                public void onTransitionEnd(Object transition) {
                    mAnimationIsRunning = false;
                    Activity currentActivity = getActivity();
                    if (mThumbnail != null && currentActivity != null && mDetailsOverviewRow != null) {
                        mDetailsOverviewRow.setImageBitmap(currentActivity, mThumbnail);
                        mDetailsOverviewRow.setImageScaleUpAllowed(true);
                    }
                    if (mOverlay != null) mOverlay.show();
                }
            });
        }

        mVideoList = new ArrayList<>();
        mHandler = new Handler();
        XmlDb.getInstance().addResumeChangeListener(this);

        mDescriptionPresenter = new VideoDetailsDescriptionPresenter();
        mOverviewRowPresenter = new ArchosDetailsOverviewRowPresenter(mDescriptionPresenter);
        mOverviewRowPresenter.setOnBindInfuseViewListener(rowView -> {
            if (mDetailsOverviewRow != null && mDetailsOverviewRow.getActionsAdapter() instanceof VideoActionAdapter) {
                updateInfuseActions((VideoActionAdapter) mDetailsOverviewRow.getActionsAdapter());
            }
        });

        // BEGIN PULCIX CUSTOM THEME
        if (isInfuse) {
            mOverviewRowPresenter.setFacet(ItemAlignmentFacet.class, null);
            setTopOffsetRatio(0.425f);
            mColor = Color.BLACK;
        } else {
            setTopOffsetRatio(0.5f);
            mColor = defaultPrimaryColor;
        }
        // END PULCIX CUSTOM THEME

        //be aware of a hack to avoid fullscreen overview : cf onSetRowStatus
        FullWidthDetailsOverviewSharedElementHelper helper = new FullWidthDetailsOverviewSharedElementHelper();
        helper.setSharedElementEnterTransition(activity, VideoDetailsActivity.SHARED_ELEMENT_NAME, 1000);
        mOverviewRowPresenter.setListener(helper);
        mOverviewRowPresenter.setBackgroundColor(defaultPrimaryColor);
        mOverviewRowPresenter.setActionsBackgroundColor(getDarkerColor(defaultPrimaryColor));
        mOverviewRowPresenter.setOnActionClickedListener(mOnActionClickedListener);
        mVideoBadgePresenter = new VideoBadgePresenter(activity);
        mFileListAdapter = new ArrayObjectAdapter(mVideoBadgePresenter);
        mFileListRow = new SelectableListRow(new HeaderItem(getString(R.string.video_sources)), mFileListAdapter);

        mSelectCurrentVideo = intent.getBooleanExtra(EXTRA_FORCE_VIDEO_SELECTION, false);

        // Easiest case when called from the leanback browser
        mVideo = (Video) intent.getSerializableExtra(EXTRA_VIDEO);

        // When called from the player we don't have the Video object, but we may have the video id if it is indexed
        if (mVideo == null) {
            mVideoIdFromPlayer = intent.getLongExtra(EXTRA_VIDEO_ID, -1);
            if (mVideoIdFromPlayer == -1 && intent.getData() != null) {
                mVideoPathFromPlayer = intent.getData().toString();
            }
        } else { // initialization of watched state
            mIsVideoWatched = mVideo.isWatched();
            if (log.isDebugEnabled()) log.debug("onCreate: init mIsVideoWatched={}", mIsVideoWatched);
        }

        mLaunchedFromPlayer = intent.getBooleanExtra(EXTRA_LAUNCHED_FROM_PLAYER, false);
        mShouldLoadBackdrop = intent.getBooleanExtra(EXTRA_SHOULD_LOAD_BACKDROP, true);
        mPlayerType = intent.getIntExtra(VideoInfoActivity.EXTRA_PLAYER_TYPE, -1);
        mVideoMetadataFromPlayer = (VideoMetadata) intent.getSerializableExtra(VideoInfoActivity.EXTRA_USE_VIDEO_METADATA);

        // WORKAROUND: at least one instance of BackdropTask must be created soon in the process (onCreate ?)
        // else it does not work later.
        // --> This instance of BackdropTask() will not be used but it must be created here!
        mBackdropTask = new BackdropTask(activity, VideoInfoCommonClass.getDarkerColor(mColor));

        mItemViewClickedListener = new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
                Activity currentActivity = getActivity();
                if (currentActivity == null) return;

                if (item instanceof ScraperTrailer) {
                    Uri trailerUrl = ((ScraperTrailer) item).getUrl();
                    if (trailerUrl == null) return;
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, trailerUrl);
                    ActivityInfo activityInfo = browserIntent.resolveActivityInfo(currentActivity.getPackageManager(), browserIntent.getFlags());
                    if (log.isDebugEnabled()) {
                        log.debug("onCreate.onItemClicked: activity identified {}", activityInfo != null ? activityInfo.processName : "null");
                    }
                    if (activityInfo != null) {
                        if (log.isDebugEnabled()) log.debug("onCreate.onItemClicked: browserintent on {} with activityInfo {}", trailerUrl, activityInfo.processName);
                        startActivity(browserIntent);
                    } else {
                        String url = trailerUrl.toString().replace("https://www.youtube.com/watch", "https://www.youtube.com/tv#/watch");
                        if (log.isDebugEnabled()) log.debug("onCreate.onItemClicked: open url {}", url);
                        WebUtils.openWebLink(currentActivity, url);
                    }
                } else if (item instanceof ScraperImage) {
                    if (row == mPostersRow) {
                        int season = (mVideo instanceof Episode) ? ((Episode) mVideo).getSeasonNumber() : -1;
                        mPosterSaverTask = new PosterSaverTask(currentActivity, season).execute((ScraperImage) item);
                    } else if (row == mBackdropsRow) {
                        mBackdropSaverTask = new BackdropSaverTask(currentActivity).execute((ScraperImage) item);
                    }
                } else if (item instanceof Video) {
                    if (row == mFileListRow) {
                        Video old = mVideo;
                        mVideo = (Video) item;
                        if (log.isDebugEnabled()) log.debug("Video selected");
                        mShouldUpdateRemoteResume = true;
                        if (!smoothUpdateVideo(mVideo, old)) {
                            if (mDetailRowBuilderTask != null) {
                                mDetailRowBuilderTask.cancel(true);
                            }
                            fullyReloadVideo(mVideo, mPoster);
                        }
                        LoaderManager.getInstance(VideoDetailsFragment.this).restartLoader(1, null, VideoDetailsFragment.this);
                    } else if (row != mFileListRow) {
                        Video clickedVideo = (Video) item;
                        if (mVideo != null && clickedVideo.getId() == mVideo.getId()) return;
                        Intent relatedIntent = new Intent(currentActivity, VideoDetailsActivity.class);
                        relatedIntent.putExtra(VideoDetailsFragment.EXTRA_VIDEO, clickedVideo);
                        relatedIntent.putExtra(VideoDetailsFragment.EXTRA_SHOULD_LOAD_BACKDROP, true);
                        startActivity(relatedIntent);
                        currentActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        if (row == mSeasonEpisodesRow) {
                            currentActivity.finish();
                        }
                    }
                } else if (item instanceof WebPageLink) {
                    WebUtils.openWebLink(currentActivity, ((WebPageLink) item).getUrl());
                }
            }
        };
        setOnItemViewClickedListener(mItemViewClickedListener);
    }

    private int getDarkerColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }

    //hack to avoid fullscreen overview
    @Override
    protected void onSetRowStatus(RowPresenter presenter, RowPresenter.ViewHolder viewHolder, int
            adapterPosition, int selectedPosition, int selectedSubPosition) {
        super.onSetRowStatus(presenter, viewHolder, adapterPosition, selectedPosition, selectedSubPosition);
        if (selectedPosition == 0 && selectedSubPosition != 0) {
            if (oldPos == 0 && oldSelectedSubPosition == 0) {
                mHandler.post(() -> setSelectedPosition(1));
            } else if (oldPos == 1) {
                setSelectedPosition(1);
                mHandler.post(() -> setSelectedPosition(0));
            }
        }
        oldPos = selectedPosition;
        oldSelectedSubPosition = selectedSubPosition;

        // BEGIN PULCIX CUSTOM THEME
        if (isInfuseThemeActive() && mInfuseDarkOverlay != null) {
            float targetAlpha = (selectedPosition > 0) ? 1.0f : 0.0f;
            mInfuseDarkOverlay.animate().alpha(targetAlpha).setDuration(250).start();
        }
        // END PULCIX CUSTOM THEME
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }

        // Invalidate subtitle cache when fragment is destroyed to ensure fresh enumeration on next browse
        if (mVideo != null) {
            SubtitleManager.invalidateCache(mVideo.getFileUri());
        }

        // Clear the static launcher to prevent memory leak
        FileUtilsQ.setDeleteLauncher(null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mOverlay = new Overlay(this);
        // BEGIN PULCIX CUSTOM THEME
        final Context context = getContext();
        if (context == null || !ThemeManager.getInstance(context).isInfuseTheme()) return;

        View detailsFragmentRoot = view.findViewById(androidx.leanback.R.id.details_fragment_root);
        View targetBgView = (detailsFragmentRoot != null) ? detailsFragmentRoot : view;
        targetBgView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_infuse_details_vignette));

        if (view instanceof ViewGroup) {
            ViewGroup root = (ViewGroup) view;
            if (mInfuseDarkOverlay != null && mInfuseDarkOverlay.getParent() instanceof ViewGroup) {
                ((ViewGroup) mInfuseDarkOverlay.getParent()).removeView(mInfuseDarkOverlay);
            }
            mInfuseDarkOverlay = new View(context);
            mInfuseDarkOverlay.setBackgroundColor(0xFF000000); // 100% black
            mInfuseDarkOverlay.setAlpha(0f);
            ViewGroup.LayoutParams overlayLp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            int insertIndex = detailsFragmentRoot != null ? root.indexOfChild(detailsFragmentRoot) : -1;
            if (insertIndex != -1) {
                root.addView(mInfuseDarkOverlay, insertIndex, overlayLp);
            } else {
                root.addView(mInfuseDarkOverlay, overlayLp);
            }
        }

        showTitle(false);
        view.post(() -> {
            if (!isAdded()) return;
            showTitle(false);
            if (mDetailsOverviewRow != null && mDetailsOverviewRow.getActionsAdapter() instanceof VideoActionAdapter) {
                updateInfuseActions((VideoActionAdapter) mDetailsOverviewRow.getActionsAdapter());
            }
            applyInfuseTopAlignment();
            if (getRowsSupportFragment() != null && getRowsSupportFragment().getVerticalGridView() != null) {
                getRowsSupportFragment().getVerticalGridView().setOnChildViewHolderSelectedListener(
                        new OnChildViewHolderSelectedListener() {
                            @Override
                            public void onChildViewHolderSelected(RecyclerView parent,
                                                                  RecyclerView.ViewHolder child, int position, int subposition) {
                                if (mInfuseDarkOverlay != null) {
                                    float targetAlpha = (position > 0) ? 1.0f : 0.0f;
                                    mInfuseDarkOverlay.animate().alpha(targetAlpha).setDuration(250).start();
                                }
                            }
                        });
            }
        });
        // END PULCIX CUSTOM THEME
    }

    // BEGIN PULCIX CUSTOM THEME
    private void applyInfuseTopAlignment() {
        View root = getView();
        if (root == null || !isInfuseThemeActive()) return;

        View titleGroup = root.findViewById(androidx.leanback.R.id.browse_title_group);
        if (titleGroup != null) {
            titleGroup.setVisibility(View.GONE);
            if (titleGroup.getLayoutParams() != null) {
                titleGroup.getLayoutParams().height = 0;
                titleGroup.requestLayout();
            }
        }
    }

    private void loadInfuseRelatedMovies(BaseTags tags) {
        final Activity activity = getActivity();
        if (activity == null || !ThemeManager.getInstance(activity).isInfuseTheme()) return;

        new Thread(() -> {
            Cursor c = null;
            try {
                if (Looper.myLooper() == null) Looper.prepare();
                Set<String> currentGenres = new HashSet<>();
                Set<String> currentDirectors = new HashSet<>();
                if (tags instanceof VideoTags) {
                    VideoTags vt = (VideoTags) tags;
                    if (vt.getGenresFormatted() != null) {
                        for (String g : vt.getGenresFormatted().split(",")) {
                            String trimmed = g.trim();
                            if (!trimmed.isEmpty()) currentGenres.add(trimmed.toLowerCase());
                        }
                    }
                    if (vt.getDirectors() != null) {
                        for (String d : vt.getDirectors()) {
                            if (d != null && !d.trim().isEmpty()) {
                                currentDirectors.add(d.trim().toLowerCase());
                            }
                        }
                    }
                }

                CursorLoader loader = new MoviesLoader(activity, MoviesLoader.DEFAULT_SORT, true, true, false, 0);
                c = loader.loadInBackground();
                List<Pair<Video, Integer>> scoredList = new ArrayList<>();
                if (c != null) {
                    VideoCursorMapper mapper = new VideoCursorMapper();
                    mapper.publicBindColumns(c);
                    final long currentVideoId = (mVideo != null) ? mVideo.getId() : -1;
                    int checked = 0;
                    while (c.moveToNext() && checked < MAX_RELATED_CANDIDATES) {
                        Video v = (Video) mapper.publicBind(c);
                        if (v == null || v.getId() == currentVideoId) continue;
                        checked++;
                        int genreMatches = 0;
                        int score = 0;
                        BaseTags vTags = v.getFullScraperTags(activity);
                        if (vTags instanceof VideoTags) {
                            VideoTags vVt = (VideoTags) vTags;
                            if (vVt.getGenresFormatted() != null && !currentGenres.isEmpty()) {
                                for (String g : vVt.getGenresFormatted().split(",")) {
                                    if (currentGenres.contains(g.trim().toLowerCase())) {
                                        genreMatches++;
                                        score += 4;
                                    }
                                }
                            }
                            if (vVt.getDirectors() != null && !currentDirectors.isEmpty()) {
                                for (String d : vVt.getDirectors()) {
                                    if (d != null && currentDirectors.contains(d.trim().toLowerCase())) {
                                        score += 3;
                                    }
                                }
                            }
                        }
                        // Aggiungi SOLO se condivide davvero almeno un genere (o se il film corrente non ha generi ma ha lo stesso regista)
                        if (genreMatches > 0 || (currentGenres.isEmpty() && score > 0)) {
                            scoredList.add(new Pair<>(v, score));
                        }
                    }
                }

                // Ordina per affinità decrescente (prima i film con generi/registi in comune, poi fallback sugli altri in catalogo)
                Collections.sort(scoredList, (a, b) -> Integer.compare(b.second, a.second));

                final List<Video> relatedList = new ArrayList<>();
                for (Pair<Video, Integer> item : scoredList) {
                    relatedList.add(item.first);
                    if (relatedList.size() >= MAX_RELATED_RESULTS) break;
                }

                if (!relatedList.isEmpty() && mHandler != null) {
                    mHandler.post(() -> {
                        Activity currentActivity = getActivity();
                        if (currentActivity == null || currentActivity.isDestroyed() || !isAdded() || mAdapter == null) return;
                        if (mRelatedRow != null && mAdapter.indexOf(mRelatedRow) != -1) {
                            mAdapter.remove(mRelatedRow);
                        }
                        PosterImageCardPresenter posterPresenter = new PosterImageCardPresenter(currentActivity);
                        ArrayObjectAdapter relatedAdapter = new ArrayObjectAdapter(posterPresenter);
                        for (Video v : relatedList) {
                            relatedAdapter.add(v);
                        }
                        // Usiamo l'ID ROW_ID_RELATED_MOVIES oppure uno fittizio in modo che onItemClicked riceva una ListRow nota (e non re-instanziata ogni volta su cui la referenza "row == mRelatedRow" fallirebbe se non estratta prima del bind)
                        mRelatedRow = new ListRow(300, new HeaderItem(getString(R.string.infuse_related_header)), relatedAdapter);
                        mAdapter.add(mRelatedRow);
                    });
                }
            } catch (Exception e) {
                if (log.isDebugEnabled()) log.debug("loadInfuseRelatedMovies failed", e);
            } finally {
                if (c != null) c.close();
            }
        }).start();
    }

    private void loadInfuseSeasonEpisodes(EpisodeTags epTags) {
        final Activity activity = getActivity();
        if (activity == null || epTags == null || !ThemeManager.getInstance(activity).isInfuseTheme()) return;

        final long showId = epTags.getShowId();
        final int seasonNumber = epTags.getSeason();
        if (showId <= 0 || seasonNumber < 0) return;

        new Thread(() -> {
            Cursor c = null;
            try {
                if (Looper.myLooper() == null) Looper.prepare();
                CursorLoader loader = new EpisodesLoader(activity, showId, seasonNumber, true);
                c = loader.loadInBackground();
                final List<Episode> seasonEpisodes = new ArrayList<>();
                if (c != null && c.getCount() > 0) {
                    VideoCursorMapper mapper = new VideoCursorMapper();
                    mapper.publicBindColumns(c);
                    while (c.moveToNext()) {
                        Object obj = mapper.publicBind(c);
                        if (obj instanceof Episode) {
                            seasonEpisodes.add((Episode) obj);
                        }
                    }
                }

                if (!seasonEpisodes.isEmpty() && mHandler != null) {
                    mHandler.post(() -> {
                        Activity currentActivity = getActivity();
                        if (currentActivity == null || currentActivity.isDestroyed() || !isAdded() || mAdapter == null) return;

                        if (mSeasonEpisodesRow != null && mAdapter.indexOf(mSeasonEpisodesRow) != -1) {
                            mAdapter.remove(mSeasonEpisodesRow);
                        }

                        PosterImageCardPresenter episodePresenter = new PosterImageCardPresenter(currentActivity);
                        ArrayObjectAdapter episodesAdapter = new ArrayObjectAdapter(episodePresenter);
                        for (Episode ep : seasonEpisodes) {
                            episodesAdapter.add(ep);
                        }

                        String headerTitle = getString(R.string.episode_season) + " " + seasonNumber;
                        mSeasonEpisodesRow = new ListRow(new HeaderItem(headerTitle), episodesAdapter);

                        // Inserisci la riga degli episodi PRIMA di Cast and Crew (se presente), altrimenti in coda
                        int castIdx = (mCastRow != null) ? mAdapter.indexOf(mCastRow) : -1;
                        if (castIdx != -1) {
                            mAdapter.add(castIdx, mSeasonEpisodesRow);
                        } else {
                            mAdapter.add(mSeasonEpisodesRow);
                        }
                    });
                }
            } catch (Exception e) {
                if (log.isDebugEnabled()) log.debug("loadInfuseSeasonEpisodes failed", e);
            } finally {
                if (c != null) c.close();
            }
        }).start();
    }

    @Override
    public void showTitle(boolean show) {
        if (isInfuseThemeActive()) {
            super.showTitle(false);
            View titleView = getTitleView();
            if (titleView != null) {
                titleView.setVisibility(View.GONE);
                if (titleView.getLayoutParams() != null) titleView.getLayoutParams().height = 0;
            }
            applyInfuseTopAlignment();
        } else {
            super.showTitle(show);
        }
    }
    // END PULCIX CUSTOM THEME

    @Override
    public void onDestroyView() {
        if (mOverlay != null) {
            mOverlay.destroy();
        }
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        // Cancel all the async tasks
        // please be aware that even after stopping, the async task continues until background task has finished
        for (AsyncTask task : new AsyncTask[]{mDetailRowBuilderTask, mBackdropTask, mVideoInfoTask, mFullScraperTagsTask,
                mSubtitleFilesListerTask, mPosterSaverTask, mBackdropSaverTask, mThumbnailAsyncTask}) {
            if (task != null) {
                task.cancel(true);
            }
        }
        //do not update remote resume
        if (log.isDebugEnabled()) log.debug("removeParseListener");
        XmlDb.getInstance().removeParseListener(mRemoteDbObserver);
        XmlDb.getInstance().removeResumeChangeListener(this);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (log.isDebugEnabled()) log.debug("onResume");
        final Activity activity = getActivity();
        if (activity == null) return;

        // BEGIN PULCIX CUSTOM THEME
        if (isInfuseThemeActive()) {
            showTitle(false);
            View rootView = getView();
            if (rootView != null) {
                rootView.post(() -> {
                    if (isAdded()) showTitle(false);
                });
            }
        }
        // END PULCIX CUSTOM THEME

        mShouldUpdateRemoteResume = true;
        if (mOverlay != null) mOverlay.resume();
        if (mResumeFromPlayer && ArchosUtils.isAmazonApk()) {
            mHandler.postDelayed(() -> {
                Activity currentActivity = getActivity();
                if (currentActivity != null && !currentActivity.isFinishing()) {
                    StoreRatingDialogBuilder.displayStoreRatingDialogIfNeeded(currentActivity);
                }
            }, 2000);
        }

        // update video in case of binge watching or repeat mode
        if (log.isDebugEnabled()) log.debug("onResume: mFirstOnResume {}, mResumeFromPlayer {}", mFirstOnResume, mResumeFromPlayer);
        long playerVideoId = CustomApplication.getLastVideoPlayedId();
        Uri playerVideoUri = CustomApplication.getLastVideoPlayedUri();
        if (log.isDebugEnabled()) {
            if (mVideo != null) {
                log.debug("onResume: current mVideo {}({}), playerVideo {}({}), mVideoIdFromPlayer {}, mVideoFromPlayer {}({})",
                        mVideo.getFileUri(), mVideo.getId(), playerVideoUri, playerVideoId, mVideoIdFromPlayer, mVideoPathFromPlayer, mVideoIdFromPlayer);
            } else {
                log.debug("onResume: current mVideo is null");
            }
        }
        if (mVideo != null && ((playerVideoId != -42 && mVideo.getId() != playerVideoId) ||
                (playerVideoUri != null && !playerVideoUri.equals(mVideo.getFileUri())))) {
            if (log.isDebugEnabled()) log.debug("onResume: different playerVideo and mVideo detected!");
            if (playerVideoUri != null) {
                mVideoPathFromPlayer = playerVideoUri.toString();
            }
            mVideoIdFromPlayer = playerVideoId;
            if (log.isDebugEnabled()) log.debug("onResume: not the same video than before (repeat mode?) target is {}", mVideoPathFromPlayer);
            // get mVideo set to new video
            CursorLoader loader = new MultipleVideoLoader(activity, mVideoPathFromPlayer);
            Cursor c = loader.loadInBackground();
            if (c != null) {
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    mVideo = (Video) new CompatibleCursorMapperConverter(new VideoCursorMapper()).convert(c);
                    if (log.isDebugEnabled()) log.debug("onResume: yay we get a new video {}", mVideo.getFilePath());
                    mRepeatModeDetected = true; // to signal smoothUpdateVideo that it should all details since video is different from initial one
                } else {
                    if (log.isDebugEnabled()) log.debug("onResume: oops no video found");
                }
                c.close();
            }
            mFirstOnResume = true; // trigger reload of the info
        }

        // Launch the first details task
        if (mFirstOnResume) {
            if (log.isDebugEnabled()) log.debug("onResume: mFirstOnResume");
            if (mVideo != null && mThumbnailAsyncTask != null) {
                mThumbnailAsyncTask.cancel(true);
            }
            // We start the loader in all cases to get DB updates that will trigger details update if need be
            LoaderManager.getInstance(this).restartLoader(1, null, this);
        } else if (mResumeFromPlayer) {
            // Update the details when back from player (we may have miss some DB updates while in background)
            if (log.isDebugEnabled()) log.debug("onResume: mResumeFromPlayer");
            LoaderManager.getInstance(this).restartLoader(1, null, this);

            if (mSubtitleFilesListerTask != null) {
                mSubtitleFilesListerTask.cancel(true);
            }
            mSubtitleFilesListerTask = new SubtitleFilesListerTask(activity).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mVideo);
        }

        // reset flags
        mResumeFromPlayer = false;
        mFirstOnResume = false;

        if (mBackdropTask != null) {
            mBackdropTask.cancel(true);
        }
        if (!mLaunchedFromPlayer) { // in player case the player is displayed in the background, not the backdrop
            mBackdropTask = new BackdropTask(activity, VideoInfoCommonClass.getDarkerColor(mColor)).execute(mVideo);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (log.isDebugEnabled()) log.debug("onPause");
        if (mOverlay != null) mOverlay.pause();
    }

    final OnActionClickedListener mOnActionClickedListener = new OnActionClickedListener() {
        @Override
        public void onActionClicked(Action action) {
            final Activity activity = getActivity();
            if (activity == null || mVideo == null) return;

            VideoMetadata mMetadata = mVideo.getMetadata();
            isFilePlayable = !(mMetadata != null && mMetadata.getFileSize() == 0 && mMetadata.getVideoTrack() == null && mMetadata.getAudioTrackNb() == 0);

            final long actionId = action.getId();
            if (actionId == VideoActionAdapter.ACTION_LOCAL_RESUME) {
                if (isFilePlayable) {
                    startAds(REQUEST_CODE_LOCAL_RESUME_AFTER_ADS_ACTIVITY);
                } else {
                    Toast.makeText(activity, R.string.player_err_cantplayvideo, Toast.LENGTH_SHORT).show();
                }
            } else if (actionId == VideoActionAdapter.ACTION_RESUME) {
                if (isFilePlayable) {
                    startAds(REQUEST_CODE_RESUME_AFTER_ADS_ACTIVITY);
                } else {
                    Toast.makeText(activity, R.string.player_err_cantplayvideo, Toast.LENGTH_SHORT).show();
                }
            } else if (actionId == VideoActionAdapter.ACTION_REMOTE_RESUME) {
                startAds(REQUEST_CODE_REMOTE_RESUME_AFTER_ADS_ACTIVITY);
            } else if (actionId == VideoActionAdapter.ACTION_PLAY_FROM_BEGIN || actionId == VideoActionAdapter.ACTION_PLAY) {
                if (isFilePlayable) {
                    startAds(REQUEST_CODE_PLAY_FROM_BEGIN_AFTER_ADS_ACTIVITY);
                } else {
                    Toast.makeText(activity, R.string.player_err_cantplayvideo, Toast.LENGTH_SHORT).show();
                }
            } else if (actionId == VideoActionAdapter.ACTION_LIST_EPISODES) {
                if (!(mVideo instanceof Episode)) return;
                EpisodeTags tagsE = (EpisodeTags) mVideo.getFullScraperTags(activity);
                if (tagsE == null) return;
                TvshowLoader mTvshowLoader = new TvshowLoader(activity, tagsE.getShowId());
                Cursor mCursor = mTvshowLoader.loadInBackground();
                if (mCursor != null) {
                    if (mCursor.getCount() > 0) {
                        mCursor.moveToFirst();
                        TvshowCursorMapper mTvShowCursorMapper = new TvshowCursorMapper();
                        mTvShowCursorMapper.bindColumns(mCursor);
                        Tvshow mTvshow = (Tvshow) mTvShowCursorMapper.bind(mCursor);
                        final Intent intent = new Intent(activity, TvshowActivity.class);
                        intent.putExtra(TvshowFragment.EXTRA_TVSHOW, mTvshow);
                        if (mOverlay != null) mOverlay.hide();
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
                        mHandler.postDelayed(() -> {
                            Activity a = getActivity();
                            if (a != null) a.finish();
                        }, 1000);
                    }
                    mCursor.close();
                }
            } else if (actionId == VideoActionAdapter.ACTION_NEXT_EPISODE) {
                final Intent intent = new Intent(activity, VideoDetailsActivity.class);
                intent.putExtra(VideoDetailsFragment.EXTRA_VIDEO, mNextEpisode);
                intent.putExtra(VideoDetailsActivity.SLIDE_TRANSITION_EXTRA, true);
                if (mOverlay != null) mOverlay.hide();
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
                mHandler.postDelayed(() -> {
                    Activity a = getActivity();
                    if (a != null) a.finish();
                }, 1000);
            } else if (actionId == VideoActionAdapter.ACTION_INDEX) {
                VideoStore.requestIndexing(mVideo.getFileUri(), activity);
            } else if (actionId == VideoActionAdapter.ACTION_UNINDEX || actionId == VideoActionAdapter.ACTION_HIDE) {
                DbUtils.markAsHiddenByUser(activity, mVideo);
            } else if (actionId == VideoActionAdapter.ACTION_DELETE) {
                mShouldDisplayConfirmDelete = true;
                if (mDetailsOverviewRow != null && mDetailsOverviewRow.getActionsAdapter() instanceof VideoActionAdapter) {
                    VideoActionAdapter actionAdapter = (VideoActionAdapter) mDetailsOverviewRow.getActionsAdapter();
                    actionAdapter.update(mVideo, mLaunchedFromPlayer, mShouldDisplayRemoveFromList, mShouldDisplayConfirmDelete, mNextEpisode, mIsTvEpisode);
                    if (isInfuseThemeActive()) {
                        updateInfuseActions(actionAdapter);
                    }
                }
            } else if (actionId == VideoActionAdapter.ACTION_CONFIRM_DELETE) {
                deleteFile_async(mVideo);
            } else if (actionId == VideoActionAdapter.ACTION_SCRAP) {
                if (!NetworkState.isNetworkConnected(activity)) {
                    Toast.makeText(activity, R.string.scrap_no_network, Toast.LENGTH_SHORT).show();
                } else {
                    if (mVideo != null && (!mVideo.isIndexed() || mVideo.getId() <= 0)) {
                        com.archos.mediaprovider.video.VideoStore.requestIndexing(mVideo.getFileUri(), activity);
                    }
                    Intent intent = new Intent(activity, ManualVideoScrappingActivity.class);
                    intent.putExtra(ManualVideoScrappingActivity.EXTRA_VIDEO, mVideo);
                    startActivityForResult(intent, REQUEST_CODE_SCRAP);
                }
            } else if (actionId == VideoActionAdapter.ACTION_UNSCRAP) {
                DbUtils.deleteScraperInfo(activity, mVideo);
            } else if (actionId == VideoActionAdapter.ACTION_ADD_TO_LIST) {
                if (ThemeManager.getInstance(activity).isInfuseTheme()) {
                    showInfuseBookmarkDialog();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(ListDialog.EXTRA_VIDEO, mVideo);
                    ListDialog dialog = new ListDialog();
                    dialog.setArguments(bundle);
                    dialog.show(getParentFragmentManager(), "list_dialog");
                }
            } else if (actionId == VideoActionAdapter.ACTION_REMOVE_FROM_LIST) {
                BaseTags metadata = mVideo.getFullScraperTags(activity);
                if (metadata != null) {
                    boolean isEpisode = metadata instanceof EpisodeTags;
                    int onlineId = (int) metadata.getOnlineId();
                    VideoStore.VideoList.VideoItem videoItem = new VideoStore.VideoList.VideoItem(-1, !isEpisode ? onlineId : -1, isEpisode ? onlineId : -1, VideoStore.List.SyncStatus.STATUS_DELETED);
                    activity.getContentResolver().update(VideoStore.List.getListUri(activity.getIntent().getLongExtra(EXTRA_LIST_ID, -1)), videoItem.toContentValues(), videoItem.getDBWhereString(), videoItem.getDBWhereArgs());
                }
                mShouldDisplayRemoveFromList = false;
                if (mDetailsOverviewRow != null) {
                    mDetailsOverviewRow.setActionsAdapter(new VideoActionAdapter(activity, mVideo, mLaunchedFromPlayer, mShouldDisplayRemoveFromList, mShouldDisplayConfirmDelete, mNextEpisode, mIsTvEpisode));
                }
                TraktService.sync(ArchosUtils.getGlobalContext(), TraktService.FLAG_SYNC_AUTO);
            } else if (actionId == VideoActionAdapter.ACTION_UNHIDE) {
                DbUtils.markAsNotHiddenByUser(activity, mVideo);
            } else if (actionId == VideoActionAdapter.ACTION_MARK_AS_WATCHED || actionId == VideoActionAdapter.ACTION_MARK_AS_NOT_WATCHED) {
                int offset = 0;
                if (mVideo.getResumeMs() > 0) offset--;
                if (mVideo.getRemoteResumeMs() > 0 && mVideo.getRemoteResumeMs() != mVideo.getResumeMs()) offset--;

                mOverviewRowPresenter.moveSelectedPosition(offset);
                if (actionId == VideoActionAdapter.ACTION_MARK_AS_WATCHED) {
                    DbUtils.markAsRead(activity, mVideo);
                } else {
                    DbUtils.markAsNotRead(activity, mVideo);
                }
                activity.setResult(Activity.RESULT_OK);
                if (isInfuseThemeActive() && mDetailsOverviewRow != null && mDetailsOverviewRow.getActionsAdapter() instanceof VideoActionAdapter) {
                    updateInfuseActions((VideoActionAdapter) mDetailsOverviewRow.getActionsAdapter());
                }
            }
        }
    };

    //--------------------------------------------------------------------
    // Implements LoaderCallbacks<Cursor>
    // We use a SingleVideoLoader to get updated from the DB using the Loader framework
    //--------------------------------------------------------------------
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        final Activity activity = getActivity();
        if (mVideo == null) {
            if (mVideoIdFromPlayer >= 0) {
                if (log.isDebugEnabled()) log.debug("onCreateLoader: mVideo is null, working from mVideoIdFromPlayer {}", mVideoIdFromPlayer);
                return new MultipleVideoLoader(activity, mVideoIdFromPlayer);
            } else {
                if (log.isDebugEnabled()) log.debug("onCreateLoader: mVideo is null, working from mVideoPathFromPlayer {}", mVideoPathFromPlayer);
                return new MultipleVideoLoader(activity, mVideoPathFromPlayer);
            }
        } else if (mVideo.isIndexed()) {
            if (log.isDebugEnabled()) log.debug("onCreateLoader: mVideo is known and indexed with id {}", mVideo.getId());
            return new MultipleVideoLoader(activity, mVideo.getId());
        } else {
            if (log.isDebugEnabled()) log.debug("onCreateLoader: mVideo is known not indexed with path {}", mVideo.getFilePath());
            return new MultipleVideoLoader(activity, mVideo.getFilePath());
        }
    }

    private final XmlDb.ParseListener mRemoteDbObserver = new XmlDb.ParseListener() {
        @Override
        public void onParseFail(XmlDb.ParseResult parseResult) {
            XmlDb.getInstance().removeParseListener(this);
        }

        @Override
        public void onParseOk(XmlDb.ParseResult result) {
            if (log.isDebugEnabled()) log.debug("onParseOk");
            Activity activity = getActivity();
            if (activity == null || mVideo == null) {
                if (log.isDebugEnabled()) log.debug("getActivity or mVideo is null, leaving");
                return;
            }
            if (!result.success) return;

            if (log.isDebugEnabled()) log.debug("result.success");
            VideoDbInfo videoInfo = XmlDb.getInstance().getEntry(mVideo.getFileUri());
            if (videoInfo == null) return;

            if (log.isDebugEnabled()) log.debug("videoInfo!=null {}", videoInfo.resume);
            mVideo.setRemoteResumeMs(videoInfo.resume);
            if (mDetailsOverviewRow != null) {
                ObjectAdapter actionsAdapter = mDetailsOverviewRow.getActionsAdapter();
                if (actionsAdapter instanceof VideoActionAdapter) {
                    ((VideoActionAdapter) actionsAdapter).updateRemoteResume(activity, mVideo);
                } else if (actionsAdapter != null) {
                    log.warn("onParseOk: actionsAdapter is not a VideoActionAdapter it is a {}", actionsAdapter.getClass().getName());
                }
            }
        }
    };

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (getActivity() == null || cursor == null) return;
        Video oldVideoObject = mVideo;
        List<Video> oldVideoList = new ArrayList<>(mVideoList);
        mVideoList.clear();

        // Getting an empty cursor here means that the video is not indexed
        if (cursor.getCount() < 1) {
            if (log.isDebugEnabled()) log.debug("onLoadFinished: cursor is empty, video is not indexed");
            if (oldVideoObject != null) {
                mVideo = new NonIndexedVideo(oldVideoObject.getStreamingUri(), oldVideoObject.getFileUri(), oldVideoObject.getName(), oldVideoObject.getPosterUri());
                if (oldVideoObject.isIndexed()) {
                    LoaderManager.getInstance(this).restartLoader(1, null, this);
                }
            } else {
                mVideo = new NonIndexedVideo(mVideoPathFromPlayer);
            }
        } else {
            if (log.isDebugEnabled()) log.debug("onLoadFinished: found {} videos", cursor.getCount());
            mVideoBadgePresenter.setDisplay3dBadge(false);
            cursor.moveToFirst();
            mVideo = null;
            VideoCursorMapper cursorMapper = new VideoCursorMapper();
            cursorMapper.publicBindColumns(cursor);
            final int onlineIdCol = cursor.getColumnIndex(VideoStore.Video.VideoColumns.SCRAPER_ONLINE_ID);
            do {
                Video video = (Video) cursorMapper.publicBind(cursor);
                if (video.is3D()) {
                    mVideoBadgePresenter.setDisplay3dBadge(true);
                }
                if (onlineIdCol >= 0) {
                    mOnlineId = cursor.getLong(onlineIdCol);
                }
                if (log.isDebugEnabled()) log.debug("onLoadFinished: online id {}", mOnlineId);
                mVideoList.add(video);
                if (log.isDebugEnabled()) log.debug("onLoadFinished: found video : {}", video.getFileUri());
                if (!mSelectCurrentVideo) {
                    if (video.getLastPlayed() > 0 && (mVideo == null || video.getLastPlayed() > mVideo.getLastPlayed())) {
                        mVideo = video;
                    }
                } else if (oldVideoObject != null && video.getFileUri().equals(oldVideoObject.getFileUri())) {
                    mVideo = video;
                }
            } while (cursor.moveToNext());

            Collections.sort(mVideoList, new SortByFavoriteSources(oldVideoList));
            mSelectCurrentVideo = true;
            if (mVideo == null) {
                mVideo = mVideoList.get(0);
            }
            if (mVideoList.size() > 1) {
                int i = 0;
                for (Video video : mVideoList) {
                    if (mFileListAdapter.size() > i) {
                        mFileListAdapter.replace(i, video);
                    } else {
                        mFileListAdapter.add(i, video);
                    }
                    i++;
                }
                if (i < mFileListAdapter.size()) {
                    mFileListAdapter.removeItems(i, mFileListAdapter.size() - i);
                }
            }
        }

        // Keep the video decoder metadata if we already have it (we don't want to compute it again, it can be long)
        VideoMetadata alreadyComputedVideoMetadata = mVideoMetadateCache.get(mVideo.getFilePath());
        if (alreadyComputedVideoMetadata != null) {
            mVideo.setMetadata(alreadyComputedVideoMetadata);
        }
        mVideoBadgePresenter.setSelectedUri(mVideo.getFileUri());

        if (!smoothUpdateVideo(mVideo, giveOldVideo ? oldVideoObject : null) || (mVideoList.size() > 1 && mAdapter != null && mAdapter.indexOf(mFileListRow) == -1)) {
            if (mDetailRowBuilderTask != null) {
                mDetailRowBuilderTask.cancel(true);
            }
            if (mThumbnailAsyncTask != null) {
                mThumbnailAsyncTask.cancel(true);
            }
            mDetailRowBuilderTask = new DetailRowBuilderTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mVideo);
        }

        giveOldVideo = true;
    }

    public void requestIndexAndScrap() {
        if (PrivateMode.isActive() || mVideo == null) return;
        final Uri fileUri = mVideo.getFileUri();
        if (mVideo.getId() == -1 && fileUri != null && !fileUri.equals(mLastIndexed)) {
            mLastIndexed = fileUri;
            if (UriUtils.isIndexable(fileUri)) {
                final Activity activity = getActivity();
                new Thread(() -> {
                    if (activity != null && !VideoStoreImportImpl.isNoMediaPath(fileUri)) {
                        VideoStore.requestIndexing(fileUri, activity, false);
                    }
                }).start();
            }
        }
    }

    private void updateDetailsActionsAdapter(Activity activity, Video video) {
        if (mDetailsOverviewRow == null || activity == null) return;
        ObjectAdapter currentAdapter = mDetailsOverviewRow.getActionsAdapter();
        if (!(currentAdapter instanceof VideoActionAdapter)) {
            mDetailsOverviewRow.setActionsAdapter(new VideoActionAdapter(activity, video, mLaunchedFromPlayer, mShouldDisplayRemoveFromList, mShouldDisplayConfirmDelete, mNextEpisode, mIsTvEpisode));
        } else {
            ((VideoActionAdapter) currentAdapter).update(video, mLaunchedFromPlayer, mShouldDisplayRemoveFromList, mShouldDisplayConfirmDelete, mNextEpisode, mIsTvEpisode);
        }
    }

    /**
     * display current video and returns whether we should or shouldn't rebuild full rows
     * @param currentVideo
     * @param oldVideoObject
     * @return
     */
    private boolean smoothUpdateVideo(Video currentVideo, Video oldVideoObject) {
        if (log.isDebugEnabled()) log.debug("smoothUpdateVideo");
        final Activity activity = getActivity();
        if (activity == null || currentVideo == null) return false;

        boolean smoothUpdate = false;
        boolean needToUpdateDetailsOverview;
        if ((mDetailsOverviewRow == null && mDetailRowBuilderTask == null) || mRepeatModeDetected) {
            needToUpdateDetailsOverview = true;
            mRepeatModeDetected = false;
        } else {
            needToUpdateDetailsOverview = foundDifferencesRequiringDetailsUpdate(oldVideoObject, currentVideo);
            if (log.isDebugEnabled()) log.debug("smoothUpdateVideo: needToUpdateDetailsOverview {}", needToUpdateDetailsOverview);
        }

        if (needToUpdateDetailsOverview) {
            requestIndexAndScrap();
            if (mDetailsOverviewRow == null) {
                mDetailsOverviewRow = new DetailsOverviewRow(currentVideo);
            }

            // smooth update when unscrapping
            if (oldVideoObject != null && oldVideoObject.hasScraperData() && !currentVideo.hasScraperData()) {
                if (mAdapter != null) {
                    if (mAdapter.indexOf(mFileListRow) >= 0) {
                        mAdapter.remove(mFileListRow);
                        INDEX_SUBTITLES--;
                        INDEX_FILEDETAILS--;
                    }
                    mAdapter.remove(mPlotAndGenresRow);
                    mAdapter.remove(mCastRow);
                    mAdapter.remove(mTrailersRow);
                    mAdapter.remove(mPostersRow);
                    mAdapter.remove(mBackdropsRow);
                    if (mRelatedRow != null) mAdapter.remove(mRelatedRow);
                    if (mSeasonEpisodesRow != null) mAdapter.remove(mSeasonEpisodesRow);
                }
                mDescriptionPresenter.update(currentVideo);
                updateDetailsActionsAdapter(activity, currentVideo);
                mDetailsOverviewRow.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.filetype_new_video));
                mDetailsOverviewRow.setImageScaleUpAllowed(false);
                smoothUpdate = true;
            }

            // smooth update when unindexing
            if (oldVideoObject != null && oldVideoObject.isIndexed() && !currentVideo.isIndexed()) {
                mDescriptionPresenter.update(currentVideo);
                updateDetailsActionsAdapter(activity, currentVideo);
                mDetailsOverviewRow.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.filetype_new_video));
                mDetailsOverviewRow.setImageScaleUpAllowed(false);
                smoothUpdate = true;
            }

            if (smoothUpdate) {
                // BEGIN PULCIX CUSTOM THEME
                mColor = ThemeManager.getInstance(activity).isInfuseTheme()
                        ? Color.BLACK
                        : ThemeManager.getInstance(activity).getDetailsPrimaryColor();
                // END PULCIX CUSTOM THEME

                mVideoBadgePresenter.setSelectedBackgroundColor(mColor);
                mOverviewRowPresenter.updateBackgroundColor(mColor);
                mOverviewRowPresenter.updateActionsBackgroundColor(getDarkerColor(mColor));
                if (mAdapter != null) {
                    for (Presenter pres : mAdapter.getPresenterSelector().getPresenters()) {
                        if (pres instanceof BackgroundColorPresenter) {
                            ((BackgroundColorPresenter) pres).setBackgroundColor(mColor);
                        }
                    }
                }
                mBackdropTask = new BackdropTask(activity, VideoInfoCommonClass.getDarkerColor(mColor)).execute(currentVideo);
            }
        } else {
            smoothUpdate = true;
        }

        if (mShouldUpdateRemoteResume) {
            currentVideo.setRemoteResumeMs(-1);
            if (!mLaunchedFromPlayer && !FileUtils.isLocal(currentVideo.getFileUri()) && UriUtils.isCompatibleWithRemoteDB(currentVideo.getFileUri())) {
                if (log.isDebugEnabled()) log.debug("addParseListener");
                XmlDb.getInstance().addParseListener(mRemoteDbObserver);
                XmlDb.getInstance().parseXmlLocation(currentVideo.getFileUri());
            }
            mShouldUpdateRemoteResume = false;
        }

        mHasRetrievedDetails = true;
        // BEGIN PULCIX CUSTOM THEME
        if (ThemeManager.getInstance(activity).isInfuseTheme()) {
            if (mDetailsOverviewRow != null && mDetailsOverviewRow.getActionsAdapter() instanceof VideoActionAdapter) {
                updateInfuseActions((VideoActionAdapter) mDetailsOverviewRow.getActionsAdapter());
            }
            applyInfuseTopAlignment();
        }
        // END PULCIX CUSTOM THEME
        return smoothUpdate;
    }

    private boolean foundDifferencesRequiringDetailsUpdate(Video v1, Video v2) {
        if (v1 == null || v2 == null) {
            if (log.isDebugEnabled()) log.debug("foundDifferencesRequiringDetailsUpdate null");
            mShouldLoadBackdrop = true;
            return true;
        }
        if (v1.getClass() != v2.getClass()) {
            if (log.isDebugEnabled()) log.debug("foundDifferencesRequiringDetailsUpdate class");
            mShouldLoadBackdrop = true;
            return true;
        }
        if (v1.getId() != v2.getId()) {
            if (log.isDebugEnabled()) log.debug("foundDifferencesRequiringDetailsUpdate id");
            mShouldLoadBackdrop = true;
            return true;
        }
        if (v1.hasScraperData() != v2.hasScraperData()) {
            if (log.isDebugEnabled()) log.debug("foundDifferencesRequiringDetailsUpdate hasScraperData");
            mShouldLoadBackdrop = true;
            return true;
        }
        if (v1.getResumeMs() != v2.getResumeMs()) {
            if (log.isDebugEnabled()) log.debug("foundDifferencesRequiringDetailsUpdate resumeMs");
            return true;
        }
        if (v1.isWatched() != v2.isWatched()) {
            if (log.isDebugEnabled()) log.debug("foundDifferencesRequiringDetailsUpdate isWatched");
            return true;
        }
        if (v1.isUserHidden() != v2.isUserHidden()) {
            if (log.isDebugEnabled()) log.debug("foundDifferencesRequiringDetailsUpdate isUserHidden");
            return true;
        }
        return false;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    @Override
    public void onResumeChange(Uri videoFile, int resumePercent) {
        if (log.isDebugEnabled()) log.debug("onResumeChange()");
        if (!mHasRetrievedDetails || !isAdded() || isDetached() || mVideo == null || !videoFile.equals(mVideo.getFileUri())) {
            return;
        }
        VideoDbInfo info = XmlDb.getEntry(videoFile);
        if (info != null) {
            mVideo.setRemoteResumeMs(info.resume);
            if (mDetailsOverviewRow != null && mHandler != null) {
                mHandler.post(() -> {
                    Activity activity = getActivity();
                    if (activity != null && isAdded()) {
                        updateDetailsActionsAdapter(activity, mVideo);
                    }
                });
            }
        }
    }

    @Override
    public void startActivityWithResultListener(Intent intent) {
        if (isAdded()) {
            startActivityForResult(intent, PLAY_ACTIVITY_REQUEST_CODE);
        } else {
            log.error("startActivityWithResultListener: fragment not added");
        }
    }

    //putting in thread to avoid async tasks to be locked
    private class ThumbnailAsyncTask extends AsyncTask<Video, Void, Pair<Bitmap, Video>> {

        @Override
        protected Pair<Bitmap, Video> doInBackground(Video... videos) {
            Video video = videos[0];
            Bitmap bitmap = null;
            try {
                Uri imageUri = null;
                if (video.isIndexed()) {
                    if (video.hasScraperData() && video.getPosterUri() != null) {
                        imageUri = video.getPosterUri();
                    } else {
                        imageUri = ThumbnailRequestHandler.buildUri(video.getId());
                    }
                }
                if (imageUri != null && isAdded()) {
                    bitmap = Picasso.get()
                            .load(imageUri)
                            .resize(getResources().getDimensionPixelSize(R.dimen.poster_width), getResources().getDimensionPixelSize(R.dimen.poster_height))
                            .centerCrop()
                            .get();
                }
            } catch (IOException e) {
                log.error("DetailsOverviewRow Picasso load exception", e);
            }
            return new Pair<>(bitmap, video);
        }

        @Override
        protected void onPostExecute(Pair<Bitmap, Video> result) {
            if (isCancelled()) return;
            Activity activity = getActivity();
            if (activity == null || !isAdded() || mVideo == null || mDetailsOverviewRow == null) return;

            if (mVideo.getPosterUri() == null || mVideo.getPosterUri().equals(result.second.getPosterUri())) {
                Bitmap bitmap = result.first;
                if (bitmap != null) {
                    if (mVideo.isWatched() || mIsVideoWatched) {
                        bitmap = PresenterUtils.addWatchedMark(bitmap, getContext());
                    }
                    if (!mAnimationIsRunning) {
                        mDetailsOverviewRow.setImageBitmap(activity, bitmap);
                        mDetailsOverviewRow.setImageScaleUpAllowed(true);
                    } else {
                        mThumbnail = bitmap;
                    }
                } else {
                    mDetailsOverviewRow.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.filetype_new_video));
                    mDetailsOverviewRow.setImageScaleUpAllowed(false);
                }
            }
        }
    }

    /**
     * will reload every row with video params EXCEPT poster
     * @param video
     */
    private void fullyReloadVideo(Video video, Bitmap poster) {
        if (log.isDebugEnabled()) log.debug("fullyReloadVideo: mShouldLoadBackdrop={}", mShouldLoadBackdrop);
        final Activity activity = getActivity();
        if (activity == null || video == null) return;

        final boolean isInfuse = ThemeManager.getInstance(activity).isInfuseTheme();
        if (mShouldLoadBackdrop) {
            BackgroundManager.getInstance(activity).setDrawable(new ColorDrawable(VideoInfoCommonClass.getDarkerColor(mColor)));
        }
        mSubtitlesDetailsRow = new SubtitlesDetailsRow(activity, video, null);
        mFileDetailsRow = new FileDetailsRow(activity, video, mPlayerType);
        if (mDetailsOverviewRow == null) {
            mDetailsOverviewRow = new DetailsOverviewRow(video);
        } else {
            mDetailsOverviewRow.setItem(video);
        }

        mIsTvEpisode = video instanceof Episode;
        updateDetailsActionsAdapter(activity, video);

        if (mAdapter == null) {
            mFileListRowPresenter = new SelectableListRowPresenter();

            ClassPresenterSelector ps = new ClassPresenterSelector();
            ps.addClassPresenter(DetailsOverviewRow.class, mOverviewRowPresenter);
            ps.addClassPresenter(SubtitlesDetailsRow.class, new SubtitlesDetailsRowPresenter(VideoDetailsFragment.this, mColor));
            ps.addClassPresenter(FileDetailsRow.class, new FileDetailsRowPresenter(mColor));
            // BEGIN PULCIX CUSTOM THEME
            if (isInfuse) {
                CustomDpadListRowPresenter dpadPresenter = new CustomDpadListRowPresenter();
                dpadPresenter.setSelectEffectEnabled(false);
                dpadPresenter.setOnItemViewClickedListener((itemViewHolder, item, rowViewHolder, row) -> {
                    Activity currentActivity = getActivity();
                    if (currentActivity == null) return;
                    if (item instanceof Video && row != mFileListRow) {
                        Video clickedVideo = (Video) item;
                        if (mVideo != null && clickedVideo.getId() == mVideo.getId()) return;
                        Intent relatedIntent = new Intent(currentActivity, VideoDetailsActivity.class);
                        relatedIntent.putExtra(VideoDetailsFragment.EXTRA_VIDEO, clickedVideo);
                        relatedIntent.putExtra(VideoDetailsFragment.EXTRA_SHOULD_LOAD_BACKDROP, true);
                        startActivity(relatedIntent);
                        currentActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        if (row == mSeasonEpisodesRow) {
                            currentActivity.finish();
                        }
                    } else if (mItemViewClickedListener != null) {
                        mItemViewClickedListener.onItemClicked(itemViewHolder, item, rowViewHolder, row);
                    }
                });
                ps.addClassPresenter(ListRow.class, dpadPresenter);
            } else {
                ps.addClassPresenter(ListRow.class, new ListRowPresenter());
            }
            // END PULCIX CUSTOM THEME
            ps.addClassPresenter(SelectableListRow.class, mFileListRowPresenter);
            ps.addClassPresenter(PlotAndGenresRow.class, new PlotAndGenresRowPresenter(14, mColor)); // 14 lines max to fit on screen
            ps.addClassPresenter(CastRow.class, new CastRowPresenter(14, mColor)); // 14 lines max to fit on screen
            mAdapter = new ArrayObjectAdapter(ps);
            setAdapter(mAdapter);

            mAdapter.add(INDEX_MAIN, mDetailsOverviewRow);
            if (mVideoList.size() > 1) {
                mAdapter.add(INDEX_FILELIST, mFileListRow);
                INDEX_SUBTITLES++;
                INDEX_FILEDETAILS++;
            }
            if (!isInfuse) {
                mAdapter.add(INDEX_SUBTITLES, mSubtitlesDetailsRow);
                mAdapter.add(INDEX_FILEDETAILS, mFileDetailsRow);
            }
        } else {
            if (mVideoList.size() > 1 && mAdapter.indexOf(mFileListRow) == -1) {
                mAdapter.replace(INDEX_FILELIST, mFileListRow);
                INDEX_SUBTITLES++;
                INDEX_FILEDETAILS++;
            }
            if (!isInfuse) {
                if (mAdapter.size() > INDEX_SUBTITLES) {
                    mAdapter.replace(INDEX_SUBTITLES, mSubtitlesDetailsRow);
                } else {
                    mAdapter.add(INDEX_SUBTITLES, mSubtitlesDetailsRow);
                }

                if (mAdapter.size() > INDEX_FILEDETAILS) {
                    mAdapter.replace(INDEX_FILEDETAILS, mFileDetailsRow);
                } else {
                    mAdapter.add(INDEX_FILEDETAILS, mFileDetailsRow);
                }
            }
            if (mAdapter.size() > INDEX_FILEDETAILS + 1) {
                mAdapter.removeItems(INDEX_FILEDETAILS + 1, mAdapter.size() - INDEX_FILEDETAILS - 1);
            }
        }

        mVideoBadgePresenter.setSelectedBackgroundColor(mColor);
        mOverviewRowPresenter.updateBackgroundColor(mColor);
        mOverviewRowPresenter.updateActionsBackgroundColor(getDarkerColor(mColor));

        for (Presenter pres : mAdapter.getPresenterSelector().getPresenters()) {
            if (pres instanceof BackgroundColorPresenter) {
                ((BackgroundColorPresenter) pres).setBackgroundColor(mColor);
            }
        }

        if (mVideoList.size() > 1) {
            mFileListRow.setStartingSelectedPosition(mVideoList.indexOf(video));
        }

        if (isInfuse && mDetailsOverviewRow != null && mDetailsOverviewRow.getActionsAdapter() instanceof VideoActionAdapter) {
            final VideoActionAdapter actionAdapter = (VideoActionAdapter) mDetailsOverviewRow.getActionsAdapter();
            updateInfuseActions(actionAdapter);
            if (mHandler != null) {
                mHandler.post(() -> updateInfuseActions(actionAdapter));
            }
        }

        if (isInfuse && mHandler != null) {
            mHandler.postDelayed(() -> {
                if (!isAdded() || getActivity() == null) return;
                if (video.hasScraperData()) {
                    if (mFullScraperTagsTask != null && !mFullScraperTagsTask.isCancelled()) {
                        mFullScraperTagsTask.cancel(true);
                    }
                    mFullScraperTagsTask = new FullScraperTagsTask(getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, video);
                } else {
                    mBackdropTask = new BackdropTask(getActivity(), VideoInfoCommonClass.getDarkerColor(mColor)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, video);
                }
                if (mSubtitleListCache.get(video.getFileUri()) == null) {
                    mSubtitleFilesListerTask = new SubtitleFilesListerTask(getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, video);
                } else {
                    updateSubtitleRowWhenReady();
                }
                String filePath = video.getFilePath();
                if (mVideoInfoTask != null) mVideoInfoTask.cancel(true);
                if (mVideoMetadateCache.containsKey(filePath)) {
                    video.setMetadata(mVideoMetadateCache.get(filePath));
                    updateMetadataWhenReady();
                    updateSubtitleRowWhenReady();
                }
                if (video.getFileUri() == null || mLaunchedFromPlayer || !FileUtils.getName(video.getFileUri()).endsWith("torrent")) {
                    mVideoInfoTask = new VideoInfoTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, video);
                }
            }, 140);
        } else {
            // Start the scraper related task (backdrop, poster list, backdrop list, web links)
            if (video.hasScraperData()) {
                if (mFullScraperTagsTask != null && !mFullScraperTagsTask.isCancelled()) {
                    mFullScraperTagsTask.cancel(true);
                }
                mFullScraperTagsTask = new FullScraperTagsTask(activity).execute(video);
            } else {
                mBackdropTask = new BackdropTask(activity, VideoInfoCommonClass.getDarkerColor(mColor)).execute(video);
            }

            // Check subtitles. Better to do it before VideoInfoTask because it should be quicker and it is displayed higher in the Fragment
            if (mSubtitleListCache.get(video.getFileUri()) == null) {
                mSubtitleFilesListerTask = new SubtitleFilesListerTask(activity).execute(video);
            } else {
                updateSubtitleRowWhenReady();
            }

            // Start the video info task only now that the DB UI is ready to setup
            String path = video.getFilePath();
            if (mVideoInfoTask != null) {
                mVideoInfoTask.cancel(true);
            }
            if (mVideoMetadateCache.containsKey(path)) {
                video.setMetadata(mVideoMetadateCache.get(path));
                updateMetadataWhenReady();
                updateSubtitleRowWhenReady();
            }
            //do not execute file info task when torrent file
            if (video.getFileUri() == null || mLaunchedFromPlayer || !FileUtils.getName(video.getFileUri()).endsWith("torrent")) {
                mVideoInfoTask = new VideoInfoTask().execute(video);
            }
        }

        if (poster == null) {
            if (log.isDebugEnabled()) log.debug("fullyReloadVideo: no poster, generate it");
            mDetailsOverviewRow.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.filetype_new_video));
            mDetailsOverviewRow.setImageScaleUpAllowed(false);
            mThumbnailAsyncTask = new ThumbnailAsyncTask().execute(mVideo);
        } else {
            if (log.isDebugEnabled()) log.debug("fullyReloadVideo: should put watched mark on poster {}", (mVideo.isWatched() || mIsVideoWatched));
            if (mVideo.isWatched() || mIsVideoWatched) {
                poster = PresenterUtils.addWatchedMark(poster, getContext());
            }
            mDetailsOverviewRow.setImageBitmap(activity, poster);
            mDetailsOverviewRow.setImageScaleUpAllowed(true);
        }

        // BEGIN PULCIX CUSTOM THEME
        applyInfuseTopAlignment();
        // END PULCIX CUSTOM THEME
    }

    //---------------------------------------------------
    private class DetailRowBuilderTask extends AsyncTask<Video, Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(Video... videos) {
            Activity activity = getActivity();
            if (activity == null || !isAdded()) return null;

            Video video = videos[0];
            // BEGIN PULCIX CUSTOM THEME
            mColor = ThemeManager.getInstance(activity).isInfuseTheme()
                    ? Color.BLACK
                    : ThemeManager.getInstance(activity).getDetailsPrimaryColor();
            // END PULCIX CUSTOM THEME
            mVideoBadgePresenter.setSelectedBackgroundColor(mColor);

            Uri imageUri = null;
            if (video.getPosterUri() != null) {
                imageUri = video.getPosterUri();
            } else if (video.isIndexed()) {
                imageUri = ThumbnailRequestHandler.buildUriNoThumbCreation(video.getId());
            }
            if (imageUri != null) {
                try {
                    return Picasso.get().load(imageUri)
                            .noFade()
                            .resize(getResources().getDimensionPixelSize(R.dimen.poster_width), getResources().getDimensionPixelSize(R.dimen.poster_height))
                            .centerCrop()
                            .get();
                } catch (IOException ignored) {
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (isCancelled()) return;
            Activity activity = getActivity();
            if (activity == null || !isAdded()) return;

            mPoster = result;
            if (result != null) {
                // BEGIN PULCIX CUSTOM THEME
                if (ThemeManager.getInstance(activity).isInfuseTheme()) {
                    mColor = Color.BLACK;
                } else {
                    Palette palette = Palette.from(result).generate();
                    if (palette.getDarkVibrantSwatch() != null) {
                        mColor = palette.getDarkVibrantSwatch().getRgb();
                    } else if (palette.getDarkMutedSwatch() != null) {
                        mColor = palette.getDarkMutedSwatch().getRgb();
                    } else {
                        mColor = ThemeManager.getInstance(activity).getDetailsPrimaryColor();
                    }
                }
                // END PULCIX CUSTOM THEME
                dominantColor = mColor;
                mVideoBadgePresenter.setSelectedBackgroundColor(mColor);
                mOverviewRowPresenter.updateBackgroundColor(mColor);
                mOverviewRowPresenter.updateActionsBackgroundColor(getDarkerColor(mColor));
            }
            fullyReloadVideo(mVideo, result);
        }
    }

    private class VideoInfoTask extends AsyncTask<Video, Integer, VideoMetadata> {
        @Override
        protected VideoMetadata doInBackground(Video... videos) {
            Video video = videos[0];
            if (video == null) return null;
            String startingPath = video.getFilePath();
            if (mLaunchedFromPlayer && mVideoMetadataFromPlayer != null && mVideoMetadataFromPlayer.getVideoTrack() != null) {
                return mVideoMetadataFromPlayer;
            } else if (mVideoMetadateCache.containsKey(startingPath)) {
                if (log.isDebugEnabled()) log.debug("metadata retrieved from cache {}", startingPath);
                return mVideoMetadateCache.get(startingPath);
            } else {
                Activity activity = getActivity();
                if (activity == null) return null;
                Bundle headersBundle = activity.getIntent().getBundleExtra("headers");
                Map<String, String> headers = null;
                if (headersBundle != null && !headersBundle.isEmpty()) {
                    headers = new HashMap<>();
                    for (String key : headersBundle.keySet()) {
                        Object val = headersBundle.get(key);
                        if (val instanceof String) headers.put(key, (String) val);
                    }
                    if (log.isDebugEnabled()) log.debug("VideoInfoTask: using {} HTTP headers from intent", headers.size());
                } else {
                    if (log.isDebugEnabled()) log.debug("VideoInfoTask: no HTTP headers in activity intent");
                }
                VideoMetadata videoMetaData = VideoInfoCommonClass.retrieveMetadata(video, activity, headers);
                if (video.isIndexed()) {
                    videoMetaData.save(activity, startingPath);
                }
                mVideoMetadateCache.put(startingPath, videoMetaData);
                return videoMetaData;
            }
        }

        @Override
        protected void onPostExecute(VideoMetadata videoInfo) {
            if (isCancelled() || !isAdded()) return;
            if (mVideo != null && videoInfo != null) {
                mVideo.setMetadata(videoInfo);
            }
            updateSubtitleRowWhenReady();
            updateMetadataWhenReady();
        }
    }

    private void updateMetadataWhenReady() {
        Activity activity = getActivity();
        if (activity == null || mVideo == null || mVideo.getMetadata() == null) return;

        mDescriptionPresenter.displayActualVideoBadges(mVideo);
        mFileDetailsRow = new FileDetailsRow(activity, mVideo, mPlayerType);
        if (!ThemeManager.getInstance(activity).isInfuseTheme() && mAdapter != null) {
            mAdapter.replace(INDEX_FILEDETAILS, mFileDetailsRow);
        }
    }

    private class FullScraperTagsTask extends AsyncTask<Video, Void, BaseTags> {
        private final Activity mActivity;
        List<ScraperImage> mPosters;
        List<ScraperImage> mBackdrops;
        private List<ScraperTrailer> mTaskTrailers;

        public FullScraperTagsTask(Activity activity) {
            mActivity = activity;
        }

        private Activity getActivity() {
            return mActivity;
        }

        @Override
        protected BaseTags doInBackground(Video... videos) {
            Video video = videos[0];
            BaseTags tags = video.getFullScraperTags(getActivity());
            if (tags == null || isCancelled()) {
                mPosters = null;
                mBackdrops = null;
                mTaskTrailers = null;
                return tags;
            }

            mPosters = tags.getAllPostersInDb(getActivity());
            mBackdrops = tags.getAllBackdropsInDb(getActivity());
            mTaskTrailers = tags.getAllTrailersInDb(getActivity());

            if (tags instanceof EpisodeTags) {
                if (Looper.myLooper() == null) Looper.prepare();
                CursorLoader loader = new NextEpisodeLoader(getActivity(), (EpisodeTags) tags);
                Cursor c = loader.loadInBackground();
                if (c != null) {
                    if (c.getCount() > 0) {
                        c.moveToFirst();
                        mNextEpisode = (Episode) new CompatibleCursorMapperConverter(new VideoCursorMapper()).convert(c);
                    }
                    c.close();
                }
            }
            return tags;
        }

        @Override
        protected void onPostExecute(BaseTags tags) {
            if (log.isDebugEnabled()) log.debug("onPostExecute");
            final Activity activity = getActivity();
            if (isCancelled() || activity == null || activity.isDestroyed() || !isAdded()) return;

            final boolean isInfuse = ThemeManager.getInstance(activity).isInfuseTheme();
            if (mDetailsOverviewRow != null && mDetailsOverviewRow.getActionsAdapter() instanceof VideoActionAdapter) {
                VideoActionAdapter actionAdapter = (VideoActionAdapter) mDetailsOverviewRow.getActionsAdapter();
                actionAdapter.setNextEpisodeStatus(mNextEpisode != null);
                actionAdapter.setListEpisodesStatus(mIsTvEpisode);
            }

            if (mBackdropTask != null) {
                mBackdropTask.cancel(true);
            }
            if (tags != null && !mLaunchedFromPlayer) {
                if (mShouldLoadBackdrop) {
                    if (log.isDebugEnabled()) log.debug("onPostExecute: loading backdrop");
                    mBackdropTask = new BackdropTask(activity, VideoInfoCommonClass.getDarkerColor(mColor)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, tags);
                    mShouldLoadBackdrop = false;
                } else {
                    if (log.isDebugEnabled()) log.debug("onPostExecute: should not load backdrop");
                }
            }

            if (tags != null) {
                // BEGIN PULCIX CUSTOM THEME
                if (isInfuse) {
                    loadInfuseClearLogo(tags);
                }
                // END PULCIX CUSTOM THEME

                final String plot = tags.getPlot();
                final String genres = (tags instanceof VideoTags) ? ((VideoTags) tags).getGenresFormatted() : null;

                // BEGIN PULCIX CUSTOM THEME
                if (isInfuse) {
                    if (mPlotAndGenresRow != null && mAdapter.indexOf(mPlotAndGenresRow) != -1) {
                        mAdapter.remove(mPlotAndGenresRow);
                    }
                    View root = getView();
                    if (root != null) {
                        TextView infuseGenresTv = root.findViewById(R.id.infuse_genres);
                        if (infuseGenresTv != null) {
                            if (genres != null && !genres.isEmpty()) {
                                infuseGenresTv.setText(genres);
                                infuseGenresTv.setVisibility(View.VISIBLE);
                            } else {
                                infuseGenresTv.setVisibility(View.GONE);
                            }
                        }
                    }
                } else if (plot != null && !plot.isEmpty()) {
                    // END PULCIX CUSTOM THEME
                    if (mPlotAndGenresRow != null && mAdapter.indexOf(mPlotAndGenresRow) != -1) {
                        mAdapter.remove(mPlotAndGenresRow);
                    }
                    mPlotAndGenresRow = new PlotAndGenresRow(getString(R.string.scrap_plot), plot, genres);
                    mAdapter.add(mPlotAndGenresRow);
                }

                // Cast
                SpannableString cast = tags.getSpannableActorsFormatted();
                if (cast == null && tags instanceof EpisodeTags) {
                    ShowTags showTags = ((EpisodeTags) tags).getShowTags();
                    cast = showTags != null ? showTags.getSpannableActorsFormatted() : null;
                }
                if (cast != null && cast.length() > 0) {
                    if (mCastRow != null && mAdapter.indexOf(mCastRow) != -1) {
                        mAdapter.remove(mCastRow);
                    }
                    // BEGIN PULCIX CUSTOM THEME
                    if (isInfuse) {
                        ArrayObjectAdapter castAdapter = new ArrayObjectAdapter(new InfuseCastPresenter());
                        BaseTags castSourceTags = (tags instanceof EpisodeTags) ? ((EpisodeTags) tags).getShowTags() : tags;
                        if (castSourceTags != null) {
                            if (castSourceTags.getDirectors() != null) {
                                for (String dir : castSourceTags.getDirectors()) {
                                    castAdapter.add(new InfuseCastMember(dir, "Director", null));
                                }
                            }
                            if (castSourceTags.getActors() != null) {
                                for (Map.Entry<String, String> entry : castSourceTags.getActors().entrySet()) {
                                    castAdapter.add(new InfuseCastMember(entry.getKey(), entry.getValue(), null));
                                }
                            }
                        }
                        if (castAdapter.size() > 0) {
                            mCastRow = new ListRow(new HeaderItem(getString(R.string.infuse_cast_crew_header)), castAdapter);
                            mAdapter.add(mCastRow);
                        }
                    } else {
                        mCastRow = new CastRow(getString(R.string.scrap_cast), cast, tags.getDirectorsFormatted());
                        mAdapter.add(mCastRow);
                    }
                    // END PULCIX CUSTOM THEME
                }
            }

            VideoDetailsFragment.this.mTrailers = mTaskTrailers;
            mAvailablePosters = (mPosters != null) ? mPosters : new ArrayList<>();
            mAvailableBackdrops = (mBackdrops != null) ? mBackdrops : new ArrayList<>();

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
            boolean hideTrailerRow = prefs.getBoolean(VideoPreferencesCommon.KEY_HIDE_TRAILER_ROW, VideoPreferencesCommon.HIDE_TRAILER_ROW_DEFAULT);

            if (mTrailers != null && !mTrailers.isEmpty() && !hideTrailerRow) {
                if (isInfuse) {
                    if (mDetailsOverviewRow != null && mDetailsOverviewRow.getActionsAdapter() instanceof VideoActionAdapter) {
                        updateInfuseActions((VideoActionAdapter) mDetailsOverviewRow.getActionsAdapter());
                    }
                } else {
                    ArrayObjectAdapter trailersRowAdapter = new ArrayObjectAdapter(new TrailerPresenter(activity));
                    trailersRowAdapter.addAll(0, mTrailers);
                    if (mTrailersRow != null && mAdapter.indexOf(mTrailersRow) != -1) {
                        mAdapter.remove(mTrailersRow);
                    }
                    mTrailersRow = new ListRow(new HeaderItem(getString(R.string.scrap_trailer)), trailersRowAdapter);
                    mAdapter.add(mTrailersRow);
                }
            }

            if (!isInfuse) {
                // Posters
                if (mPosters != null && !mPosters.isEmpty()) {
                    ArrayObjectAdapter postersRowAdapter = new ArrayObjectAdapter(new ScraperImagePosterPresenter());
                    postersRowAdapter.addAll(0, mPosters);
                    if (mPostersRow != null && mAdapter.indexOf(mPostersRow) != -1) {
                        mAdapter.remove(mPostersRow);
                    }
                    mPostersRow = new ListRow(
                            new HeaderItem(getString(!mIsTvEpisode ? R.string.leanback_posters_header : R.string.leanback_season_posters_header)),
                            postersRowAdapter);
                    mAdapter.add(mPostersRow);
                }

                // Backdrops
                if (mBackdrops != null && !mBackdrops.isEmpty()) {
                    ArrayObjectAdapter backdropsRowAdapter = new ArrayObjectAdapter(new ScraperImageBackdropPresenter());
                    backdropsRowAdapter.addAll(0, mBackdrops);
                    if (mBackdropsRow != null && mAdapter.indexOf(mBackdropsRow) != -1) {
                        mAdapter.remove(mBackdropsRow);
                    }
                    mBackdropsRow = new ListRow(
                            new HeaderItem(getString(!mIsTvEpisode ? R.string.leanback_backdrops_header : R.string.leanback_tvshow_backdrops_header)),
                            backdropsRowAdapter);
                    mAdapter.add(mBackdropsRow);
                }

                // Web links
                List<String> links = getWebLinks(tags);
                if (!links.isEmpty()) {
                    ArrayObjectAdapter rowAdapter = new ArrayObjectAdapter(new WebLinkPresenter(mColor));
                    for (String link : links) {
                        rowAdapter.add(new WebPageLink(link));
                    }
                    mAdapter.add(new ListRow(new HeaderItem(getString(R.string.leanback_weblinks_header)), rowAdapter));
                }
            }

            // BEGIN PULCIX CUSTOM THEME
            applyInfuseTopAlignment();
            if (isInfuse) {
                if (mDetailsOverviewRow != null && mDetailsOverviewRow.getActionsAdapter() instanceof VideoActionAdapter) {
                    updateInfuseActions((VideoActionAdapter) mDetailsOverviewRow.getActionsAdapter());
                }
                if (tags instanceof EpisodeTags) {
                    if (mHandler != null) {
                        mHandler.postDelayed(() -> loadInfuseSeasonEpisodes((EpisodeTags) tags), 150);
                    } else {
                        loadInfuseSeasonEpisodes((EpisodeTags) tags);
                    }
                } else {
                    if (mHandler != null) {
                        mHandler.postDelayed(() -> loadInfuseRelatedMovies(tags), 150);
                    } else {
                        loadInfuseRelatedMovies(tags);
                    }
                }
            }
            // END PULCIX CUSTOM THEME
        }
    }

    private class SubtitleFilesListerTask extends AsyncTask<Video, Void, List<SubtitleManager.SubtitleFile>> {

        private final Activity mActivity;

        public SubtitleFilesListerTask(Activity activity) {
            mActivity = activity;
        }

        private Activity getActivity() {
            return mActivity;
        }

        @Override
        protected List<SubtitleManager.SubtitleFile> doInBackground(Video... videos) {
            Video video = videos[0];
            if (video == null || video.getFileUri() == null) return new ArrayList<>();
            if (log.isDebugEnabled()) {
                log.debug("SubtitleFilesListerTask:doInBackground starting for: {}", video.getFileUri());
                log.debug("SubtitleFilesListerTask:doInBackground file name: {}", FileUtils.getName(video.getFileUri()));
            }

            try {
                SubtitleManager lister = new SubtitleManager(getActivity(), null);
                if (log.isDebugEnabled()) log.debug("SubtitleFilesListerTask:doInBackground calling listLocalAndRemotesSubtitles");
                List<SubtitleManager.SubtitleFile> list = lister.listLocalAndRemotesSubtitles(video.getFileUri(), true);
                if (log.isDebugEnabled()) log.debug("SubtitleFilesListerTask:doInBackground completed, found {} subtitles", (list != null ? list.size() : 0));
                mSubtitleListCache.put(video.getFileUri(), list);
                return list;
            } catch (Exception e) {
                log.error("SubtitleFilesListerTask:doInBackground exception", e);
                return new ArrayList<>();
            }
        }

        @Override
        protected void onPostExecute(List<SubtitleManager.SubtitleFile> subtitleFiles) {
            if (isCancelled()) {
                if (log.isDebugEnabled()) log.debug("SubtitleFilesListerTask: onPostExecute cancelled");
                return;
            }
            if (log.isDebugEnabled()) log.debug("SubtitleFilesListerTask: onPostExecute with {} subtitles", (subtitleFiles != null ? subtitleFiles.size() : 0));
            mExternalSubtitles = subtitleFiles;

            if (subtitleFiles != null && mVideo != null && FileUtils.isLocal(mVideo.getFileUri())) {
                SubtitleManager.cacheSubtitleFiles(mVideo.getFileUri(), subtitleFiles);
                if (log.isDebugEnabled()) log.debug("SubtitleFilesListerTask: cached {} subtitles for local file {}", subtitleFiles.size(), mVideo.getFileUri());
            } else if (subtitleFiles != null && mVideo != null) {
                if (log.isDebugEnabled()) log.debug("SubtitleFilesListerTask: skipping cache for remote file (requires local copy): {}", mVideo.getFileUri());
            }

            if (log.isDebugEnabled()) log.debug("SubtitleFilesListerTask: onPostExecute calling updateSubtitleRowWhenReady");
            updateSubtitleRowWhenReady();
        }
    }

    /**
     * Update subtitles details when both metadata (for integrated subs) and external sub checking is done
     */
    private void playInfuseTrailer(ScraperTrailer trailer) {
        final Activity activity = getActivity();
        if (trailer == null || trailer.getUrl() == null || activity == null) return;
        Uri trailerUri = trailer.getUrl();
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, trailerUri);
        ActivityInfo activityInfo = browserIntent.resolveActivityInfo(activity.getPackageManager(), browserIntent.getFlags());
        if (activityInfo != null) {
            startActivity(browserIntent);
        } else {
            WebUtils.openWebLink(activity, trailerUri.toString());
        }
    }

    private static GradientDrawable createInfuseDialogBackground(float density) {
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF141416); // Black AMOLED Infuse
        bg.setCornerRadius(14 * density);
        bg.setStroke((int) (1 * density), 0xFF2C2C2E);
        return bg;
    }

    private void showInfuseBookmarkDialog() {
        final Activity activity = getActivity();
        if (activity == null || mVideo == null) return;

        BaseTags metadata = mVideo.getFullScraperTags(activity);
        final boolean isEpisode = (mVideo instanceof Episode) || (metadata instanceof EpisodeTags);
        final int onlineId = (metadata != null && metadata.getOnlineId() > 0) ? (int) metadata.getOnlineId() : (mOnlineId > 0 ? (int) mOnlineId : -1);

        if (onlineId <= 0) {
            Toast.makeText(activity, R.string.scrap_no_info, Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor c = new ListsLoader(activity).loadInBackground();
        List<String> items = new ArrayList<>();
        List<Integer> listIds = new ArrayList<>();
        List<Boolean> inListFlags = new ArrayList<>();

        items.add("+ " + getString(R.string.add_to_list));
        listIds.add(-1);
        inListFlags.add(false);

        if (c != null) {
            int titleIdx = c.getColumnIndex(VideoStore.List.Columns.TITLE);
            int idIdx = c.getColumnIndex(VideoStore.List.Columns.ID);
            while (c.moveToNext()) {
                String listTitle = c.getString(titleIdx);
                int listId = c.getInt(idIdx);
                boolean isInList = isVideoInList(activity, listId, onlineId, isEpisode);

                items.add(isInList ? ("✓  " + listTitle + "  —  " + getString(R.string.remove_from_list)) : listTitle);
                listIds.add(listId);
                inListFlags.add(isInList);
            }
            c.close();
        }

        List<Runnable> actions = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            final int idx = i;
            actions.add(() -> {
                if (idx == 0) {
                    showInfuseNewListDialog();
                    return;
                }
                Activity currentActivity = getActivity();
                if (currentActivity == null || mVideo == null) return;
                int listId = listIds.get(idx);
                boolean alreadyInList = inListFlags.get(idx);
                Uri listUri = VideoStore.List.getListUri(listId);

                if (alreadyInList) {
                    VideoStore.VideoList.VideoItem videoItem = new VideoStore.VideoList.VideoItem(
                            -1,
                            !isEpisode ? onlineId : -1,
                            isEpisode ? onlineId : -1,
                            VideoStore.List.SyncStatus.STATUS_DELETED);
                    currentActivity.getContentResolver().update(
                            listUri,
                            videoItem.toContentValues(),
                            videoItem.getDBWhereString(),
                            videoItem.getDBWhereArgs());
                    currentActivity.getContentResolver().delete(
                            listUri,
                            videoItem.getDBWhereString(),
                            videoItem.getDBWhereArgs());
                    deleteListIfEmpty(currentActivity, listId);
                    if (activity.getIntent().getLongExtra(EXTRA_LIST_ID, -1) == listId) {
                        mShouldDisplayRemoveFromList = false;
                    }
                    TraktService.sync(ArchosUtils.getGlobalContext(), TraktService.FLAG_SYNC_AUTO);
                    Toast.makeText(currentActivity, R.string.remove_from_list, Toast.LENGTH_SHORT).show();
                } else {
                    VideoStore.VideoList.VideoItem videoItem = new VideoStore.VideoList.VideoItem(
                            -1,
                            !isEpisode ? onlineId : -1,
                            isEpisode ? onlineId : -1,
                            VideoStore.List.SyncStatus.STATUS_NOT_SYNC);
                    currentActivity.getContentResolver().insert(listUri, videoItem.toContentValues());
                    TraktService.sync(ArchosUtils.getGlobalContext(), TraktService.FLAG_SYNC_AUTO);
                    Toast.makeText(currentActivity, R.string.add_to_list, Toast.LENGTH_SHORT).show();
                }
            });
        }

        showInfuseListDialog(getString(R.string.add_to_list), items, actions);
    }

    private boolean isVideoInList(Context context, int listId, int onlineId, boolean isEpisode) {
        if (onlineId <= 0 || listId <= 0) return false;
        boolean found = false;

        // 1. Query diretta via RAW_QUERY sulla vera tabella video_list_table di SQLite
        Cursor rawCursor = null;
        try {
            String col = isEpisode ? VideoStore.VideoList.Columns.E_ONLINE_ID : VideoStore.VideoList.Columns.M_ONLINE_ID;
            String sql = "SELECT * FROM " + ListTables.VIDEO_LIST_TABLE + " WHERE "
                    + VideoStore.VideoList.Columns.LIST_ID + " = ? AND "
                    + col + " = ? AND "
                    + VideoStore.List.Columns.SYNC_STATUS + " != ?";
            String[] args = new String[]{
                    String.valueOf(listId),
                    String.valueOf(onlineId),
                    String.valueOf(VideoStore.List.SyncStatus.STATUS_DELETED)
            };
            rawCursor = context.getContentResolver().query(VideoStore.RAW_QUERY, null, sql, args, null);
            if (rawCursor != null && rawCursor.getCount() > 0) {
                found = true;
            }
        } catch (Exception e) {
            log.error("isVideoInList rawQuery error", e);
        } finally {
            if (rawCursor != null) rawCursor.close();
        }

        if (found) {
            if (log.isDebugEnabled()) log.debug("isVideoInList: listId={}, onlineId={}, isEpisode={}, found={}", listId, onlineId, isEpisode, true);
            return true;
        }

        // 2. Fallback: iterazione del cursor
        Cursor cursor = null;
        try {
            Uri listUri = VideoStore.List.getListUri(listId);
            cursor = context.getContentResolver().query(listUri, null, null, null, null);
            if (cursor != null) {
                String targetCol = isEpisode ? VideoStore.VideoList.Columns.E_ONLINE_ID : VideoStore.VideoList.Columns.M_ONLINE_ID;
                int onlineIdIdx = cursor.getColumnIndex(targetCol);
                int syncStatusIdx = cursor.getColumnIndex(VideoStore.List.Columns.SYNC_STATUS);
                int videoOnlineIdIdx = cursor.getColumnIndex(VideoStore.Video.VideoColumns.SCRAPER_ONLINE_ID);

                while (cursor.moveToNext()) {
                    if (syncStatusIdx != -1 && cursor.getInt(syncStatusIdx) == VideoStore.List.SyncStatus.STATUS_DELETED) {
                        continue;
                    }
                    if (onlineIdIdx != -1 && cursor.getInt(onlineIdIdx) == onlineId) {
                        found = true;
                        break;
                    }
                    if (videoOnlineIdIdx != -1 && cursor.getInt(videoOnlineIdIdx) == onlineId) {
                        found = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("isVideoInList query error", e);
        } finally {
            if (cursor != null) cursor.close();
        }

        if (log.isDebugEnabled()) log.debug("isVideoInList: listId={}, onlineId={}, isEpisode={}, found={}", listId, onlineId, isEpisode, found);
        return found;
    }

    private void deleteListIfEmpty(Context context, int listId) {
        if (context == null || listId <= 0) return;
        Cursor c = null;
        boolean hasActiveVideos = false;
        try {
            String sql = "SELECT * FROM " + ListTables.VIDEO_LIST_TABLE + " WHERE "
                    + VideoStore.VideoList.Columns.LIST_ID + " = ? AND "
                    + VideoStore.List.Columns.SYNC_STATUS + " != ?";
            String[] rawArgs = new String[]{
                    String.valueOf(listId),
                    String.valueOf(VideoStore.List.SyncStatus.STATUS_DELETED)
            };
            c = context.getContentResolver().query(VideoStore.RAW_QUERY, null, sql, rawArgs, null);
            if (c != null && c.getCount() > 0) {
                hasActiveVideos = true;
            }
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }

        if (!hasActiveVideos) {
            try {
                Uri listUri = VideoStore.List.getListUri(listId);
                c = context.getContentResolver().query(listUri, null, null, null, null);
                if (c != null) {
                    int syncStatusIdx = c.getColumnIndex(VideoStore.List.Columns.SYNC_STATUS);
                    while (c.moveToNext()) {
                        if (syncStatusIdx != -1) {
                            int status = c.getInt(syncStatusIdx);
                            if (status != VideoStore.List.SyncStatus.STATUS_DELETED) {
                                hasActiveVideos = true;
                                break;
                            }
                        } else {
                            hasActiveVideos = true;
                            break;
                        }
                    }
                }
            } catch (Exception ignored) {
            } finally {
                if (c != null) c.close();
            }
        }

        if (!hasActiveVideos) {
            try {
                String where = VideoStore.List.Columns.ID + " = ?";
                String[] args = new String[]{String.valueOf(listId)};
                int deleted = context.getContentResolver().delete(VideoStore.List.LIST_CONTENT_URI, where, args);
                if (deleted == 0) {
                    ContentValues cv = new ContentValues();
                    cv.put(VideoStore.List.Columns.SYNC_STATUS, VideoStore.List.SyncStatus.STATUS_DELETED);
                    context.getContentResolver().update(VideoStore.List.LIST_CONTENT_URI, cv, where, args);
                }
            } catch (Exception ignored) {
            }
        }
    }

    private void showInfuseNewListDialog() {
        final Activity activity = getActivity();
        if (activity == null || mVideo == null) return;

        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        float density = getResources().getDisplayMetrics().density;
        int pad24 = (int) (24 * density);

        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(pad24, pad24, pad24, pad24);
        root.setBackground(createInfuseDialogBackground(density));

        TextView titleTv = new TextView(activity);
        titleTv.setText(R.string.list_title);
        titleTv.setTextColor(0xFFFFFFFF);
        titleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        titleTv.setTypeface(null, Typeface.BOLD);
        titleTv.setPadding(0, 0, 0, (int) (16 * density));
        root.addView(titleTv);

        EditText inputEt = new EditText(activity);
        inputEt.setHint(R.string.list_title);
        inputEt.setHintTextColor(0xFF8E8E93);
        inputEt.setTextColor(0xFFFFFFFF);
        inputEt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        inputEt.setPadding((int) (14 * density), (int) (12 * density), (int) (14 * density), (int) (12 * density));

        GradientDrawable etBg = new GradientDrawable();
        etBg.setColor(0xFF1C1C1E);
        etBg.setCornerRadius(10 * density);
        etBg.setStroke((int) (1 * density), 0xFF3A3A3C);
        inputEt.setBackground(etBg);
        root.addView(inputEt);

        LinearLayout btnRow = new LinearLayout(activity);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setGravity(Gravity.END);
        LinearLayout.LayoutParams btnRowLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnRowLp.topMargin = (int) (20 * density);
        btnRow.setLayoutParams(btnRowLp);

        TextView okBtn = new TextView(activity);
        okBtn.setText(android.R.string.ok);
        okBtn.setTextColor(0xFFFFFFFF);
        okBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        okBtn.setTypeface(null, Typeface.BOLD);
        okBtn.setGravity(Gravity.CENTER);
        okBtn.setPadding((int) (24 * density), (int) (10 * density), (int) (24 * density), (int) (10 * density));
        okBtn.setFocusable(true);

        GradientDrawable btnBg = new GradientDrawable();
        btnBg.setCornerRadius(10 * density);
        btnBg.setColor(0xFFFF7F02);
        okBtn.setBackground(btnBg);

        okBtn.setOnClickListener(v -> {
            Activity currentActivity = getActivity();
            String titleText = inputEt.getText().toString().trim();
            if (currentActivity != null && mVideo != null && !titleText.isEmpty()) {
                VideoStore.List.ListObj list = new VideoStore.List.ListObj(titleText, -1, VideoStore.List.SyncStatus.STATUS_NOT_SYNC);
                Uri uri = currentActivity.getContentResolver().insert(VideoStore.List.LIST_CONTENT_URI, list.toContentValues());
                BaseTags metadata = mVideo.getFullScraperTags(currentActivity);
                if (metadata != null && uri != null) {
                    boolean isEpisode = metadata instanceof EpisodeTags;
                    int onlineId = (int) metadata.getOnlineId();
                    VideoStore.VideoList.VideoItem videoItem = new VideoStore.VideoList.VideoItem(
                            -1,
                            !isEpisode ? onlineId : -1,
                            isEpisode ? onlineId : -1,
                            VideoStore.List.SyncStatus.STATUS_NOT_SYNC);
                    currentActivity.getContentResolver().insert(uri, videoItem.toContentValues());
                    TraktService.sync(ArchosUtils.getGlobalContext(), TraktService.FLAG_SYNC_AUTO);
                    Toast.makeText(currentActivity, R.string.add_to_list, Toast.LENGTH_SHORT).show();
                }
            }
            dialog.dismiss();
        });

        btnRow.addView(okBtn);
        root.addView(btnRow);

        dialog.setContentView(root);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout((int) (420 * density), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        dialog.show();
        inputEt.requestFocus();
    }

    private void showInfuseListDialog(String title, List<String> items, List<Runnable> actions) {
        final Activity activity = getActivity();
        if (activity == null || items == null || items.isEmpty()) return;
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        float density = getResources().getDisplayMetrics().density;
        int pad20 = (int) (20 * density);
        int pad12 = (int) (12 * density);

        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(pad20, pad20, pad20, pad20);
        root.setBackground(createInfuseDialogBackground(density));

        if (title != null && !title.isEmpty()) {
            TextView titleTv = new TextView(activity);
            titleTv.setText(title);
            titleTv.setTextColor(0xFF8E8E93);
            titleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            titleTv.setTypeface(null, Typeface.BOLD);
            titleTv.setPadding(pad12, 0, pad12, pad12);
            root.addView(titleTv);
        }

        final int itemWidth = (int) (340 * density);
        final int padH = (int) (16 * density);
        final int padV = (int) (12 * density);
        final int topMargin = (int) (4 * density);

        for (int i = 0; i < items.size(); i++) {
            final int idx = i;
            TextView itemTv = new TextView(activity);
            itemTv.setText(items.get(i));
            itemTv.setTextColor(0xFFFFFFFF);
            itemTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            itemTv.setPadding(padH, padV, padH, padV);
            itemTv.setFocusable(true);
            itemTv.setFocusableInTouchMode(false);

            GradientDrawable itemBg = new GradientDrawable();
            itemBg.setCornerRadius(10 * density);
            itemBg.setColor(Color.TRANSPARENT);
            itemTv.setBackground(itemBg);

            itemTv.setOnFocusChangeListener((v, hasFocus) -> {
                itemBg.setColor(hasFocus ? 0xFFFF7F02 : Color.TRANSPARENT);
                itemTv.setTypeface(null, hasFocus ? Typeface.BOLD : Typeface.NORMAL);
            });

            itemTv.setOnClickListener(v -> {
                dialog.dismiss();
                if (actions != null && idx < actions.size() && actions.get(idx) != null) {
                    actions.get(idx).run();
                }
            });

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(itemWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (i > 0) lp.topMargin = topMargin;
            itemTv.setLayoutParams(lp);
            root.addView(itemTv);
        }

        dialog.setContentView(root);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();
    }

    private void showInfuseMessageDialog(String title, String message) {
        final Activity activity = getActivity();
        if (activity == null) return;
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        float density = getResources().getDisplayMetrics().density;
        int pad24 = (int) (24 * density);

        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(pad24, pad24, pad24, pad24);
        root.setBackground(createInfuseDialogBackground(density));

        if (title != null && !title.isEmpty()) {
            TextView titleTv = new TextView(activity);
            titleTv.setText(title);
            titleTv.setTextColor(0xFFFFFFFF);
            titleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            titleTv.setTypeface(null, Typeface.BOLD);
            titleTv.setPadding(0, 0, 0, (int) (12 * density));
            root.addView(titleTv);
        }

        TextView msgTv = new TextView(activity);
        msgTv.setText(message);
        msgTv.setTextColor(0xFFD1D1D6);
        msgTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        msgTv.setPadding(0, 0, 0, (int) (20 * density));
        root.addView(msgTv);

        TextView okBtn = new TextView(activity);
        okBtn.setText(android.R.string.ok);
        okBtn.setTextColor(0xFFFFFFFF);
        okBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        okBtn.setTypeface(null, Typeface.BOLD);
        okBtn.setGravity(Gravity.CENTER);
        okBtn.setPadding((int) (24 * density), (int) (10 * density), (int) (24 * density), (int) (10 * density));
        okBtn.setFocusable(true);

        GradientDrawable btnBg = new GradientDrawable();
        btnBg.setCornerRadius(10 * density);
        btnBg.setColor(0xFFFF7F02);
        okBtn.setBackground(btnBg);
        okBtn.setOnClickListener(v -> dialog.dismiss());

        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(
                (int) (120 * density), ViewGroup.LayoutParams.WRAP_CONTENT);
        btnLp.gravity = Gravity.END;
        okBtn.setLayoutParams(btnLp);
        root.addView(okBtn);

        dialog.setContentView(root);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout((int) (460 * density), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        dialog.show();
        okBtn.requestFocus();
    }

    private void showInfuseImagePickerDialog(String title, List<ScraperImage> images, boolean isPoster) {
        final Activity activity = getActivity();
        if (activity == null) return;
        if (images == null || images.isEmpty()) {
            Toast.makeText(activity, R.string.error, Toast.LENGTH_SHORT).show();
            return;
        }
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        float density = getResources().getDisplayMetrics().density;
        int pad20 = (int) (20 * density);
        int pad8 = (int) (8 * density);

        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(pad20, pad20, pad20, pad20);
        root.setBackground(createInfuseDialogBackground(density));

        TextView titleTv = new TextView(activity);
        titleTv.setText(title);
        titleTv.setTextColor(0xFFFFFFFF);
        titleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        titleTv.setTypeface(null, Typeface.BOLD);
        titleTv.setPadding(0, 0, 0, (int) (16 * density));
        root.addView(titleTv);

        HorizontalScrollView hsv = new HorizontalScrollView(activity);
        hsv.setHorizontalScrollBarEnabled(false);
        hsv.setClipToPadding(false);
        hsv.setClipChildren(false);

        LinearLayout row = new LinearLayout(activity);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(pad8, pad8, pad8, pad8);
        row.setClipToPadding(false);
        row.setClipChildren(false);

        final int itemW = isPoster ? (int) (120 * density) : (int) (220 * density);
        final int itemH = isPoster ? (int) (180 * density) : (int) (124 * density);
        final int marginEnd = (int) (14 * density);
        final int strokeWidth = (int) (3 * density);

        for (int i = 0, limit = Math.min(images.size(), 25); i < limit; i++) {
            final ScraperImage img = images.get(i);
            FrameLayout card = new FrameLayout(activity);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(itemW, itemH);
            lp.setMarginEnd(marginEnd);
            card.setLayoutParams(lp);

            GradientDrawable cardBg = new GradientDrawable();
            cardBg.setColor(0xFF1C1C1E);
            cardBg.setCornerRadius(14 * density);
            card.setBackground(cardBg);
            card.setClipToOutline(true);
            card.setFocusable(true);

            ImageView iv = new ImageView(activity);
            iv.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            card.addView(iv);

            String thumbUrl = img.getThumbUrl() != null ? img.getThumbUrl() : img.getLargeUrl();
            if (thumbUrl != null) {
                Picasso.get().load(thumbUrl).resize(itemW, itemH).centerCrop().into(iv);
            }

            card.setOnFocusChangeListener((v, hasFocus) -> {
                v.animate().scaleX(hasFocus ? 1.07f : 1.0f).scaleY(hasFocus ? 1.07f : 1.0f).setDuration(150).start();
                cardBg.setStroke(hasFocus ? strokeWidth : 0, 0xFFFF7F02);
            });

            card.setOnClickListener(v -> {
                dialog.dismiss();
                Activity currentActivity = getActivity();
                if (currentActivity == null) return;
                if (isPoster) {
                    int season = (mVideo instanceof Episode) ? ((Episode) mVideo).getSeasonNumber() : -1;
                    mPosterSaverTask = new PosterSaverTask(currentActivity, season).execute(img);
                } else {
                    mBackdropSaverTask = new BackdropSaverTask(currentActivity).execute(img);
                }
            });

            row.addView(card);
        }

        hsv.addView(row);
        root.addView(hsv);

        dialog.setContentView(root);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout((int) (720 * density), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        dialog.show();
        if (row.getChildCount() > 0) row.getChildAt(0).requestFocus();
    }

    // BEGIN PULCIX CUSTOM THEME
    private void scheduleInfuseActionsRetry(final int attemptsLeft) {
        if (attemptsLeft <= 0 || mHandler == null) return;
        mHandler.postDelayed(() -> {
            if (!isAdded() || getActivity() == null || mDetailsOverviewRow == null) return;
            ObjectAdapter actAdapter = mDetailsOverviewRow.getActionsAdapter();
            if (actAdapter instanceof VideoActionAdapter) {
                updateInfuseActions((VideoActionAdapter) actAdapter);
            } else {
                scheduleInfuseActionsRetry(attemptsLeft - 1);
            }
        }, 50);
    }

    private void updateInfuseActions(VideoActionAdapter adapter) {
        final Activity activity = getActivity();
        if (activity == null || mVideo == null) return;

        View searchRoot = (mOverviewRowPresenter != null && mOverviewRowPresenter.getOverviewRowView() != null)
                ? mOverviewRowPresenter.getOverviewRowView()
                : getView();
        if (searchRoot == null) return;

        final View root = getView() != null ? getView() : searchRoot;

        View nativeGrid = searchRoot.findViewById(androidx.leanback.R.id.details_overview_actions);
        if (nativeGrid != null) {
            nativeGrid.setVisibility(View.GONE);
        }

        LinearLayout infuseActionsContainer = searchRoot.findViewById(R.id.infuse_actions_container);
        if (infuseActionsContainer == null && getView() != null && getView() != searchRoot) {
            infuseActionsContainer = getView().findViewById(R.id.infuse_actions_container);
        }
        if (infuseActionsContainer == null) {
            scheduleInfuseActionsRetry(50);
            return;
        }

        infuseActionsContainer.setVisibility(View.VISIBLE);
        LinearLayout iconRow = infuseActionsContainer.findViewById(R.id.infuse_icon_actions);
        LinearLayout primaryRow = infuseActionsContainer.findViewById(R.id.infuse_primary_actions);
        if (iconRow != null && primaryRow != null) {
            if (iconRow.getLayoutParams() != null) {
                iconRow.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            if (primaryRow.getLayoutParams() != null) {
                primaryRow.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            boolean hadIconFocus = iconRow.hasFocus();
            int focusedIconIndex = -1;
            for (int c = 0; c < iconRow.getChildCount(); c++) {
                if (iconRow.getChildAt(c).hasFocus()) {
                    focusedIconIndex = c;
                    break;
                }
            }

            iconRow.removeAllViews();
            primaryRow.removeAllViews();

            float density = getResources().getDisplayMetrics().density;
            int iconWidth = (int) (54 * density);
            int iconHeight = (int) (38 * density);
            int iconMarginEnd = (int) (14 * density);

            int primaryWidth = (int) (200 * density);
            int primaryHeight = (int) (46 * density);
            int primaryMarginEnd = (int) (12 * density);

            LayoutInflater inflater = LayoutInflater.from(activity);
            View firstPrimaryView = null;

            // 1. PRIMARY ROW (Play / Resume / Play from beginning)
            if (adapter != null) {
                for (int i = 0; i < adapter.size(); i++) {
                    Object item = adapter.get(i);
                    if (!(item instanceof Action)) continue;
                    Action action = (Action) item;
                    long id = action.getId();
                    if (id == VideoActionAdapter.ACTION_PLAY ||
                            id == VideoActionAdapter.ACTION_RESUME ||
                            id == VideoActionAdapter.ACTION_LOCAL_RESUME ||
                            id == VideoActionAdapter.ACTION_REMOTE_RESUME ||
                            id == VideoActionAdapter.ACTION_PLAY_FROM_BEGIN ||
                            id == VideoActionAdapter.ACTION_NEXT_EPISODE ||
                            id == VideoActionAdapter.ACTION_LIST_EPISODES) {

                        View v = inflater.inflate(R.layout.infuse_action_primary_pill, primaryRow, false);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(primaryWidth, primaryHeight);
                        lp.setMarginEnd(primaryMarginEnd);
                        v.setLayoutParams(lp);

                        TextView tv = v.findViewById(R.id.text);
                        if (tv != null) tv.setText(action.getLabel1());
                        v.setFocusable(true);
                        v.setFocusableInTouchMode(false);
                        v.setOnClickListener(v1 -> mOnActionClickedListener.onActionClicked(action));
                        v.setOnFocusChangeListener((view, hasFocus) -> {
                            float scale = hasFocus ? 1.05f : 1.0f;
                            view.animate().scaleX(scale).scaleY(scale).setDuration(150).start();
                        });
                        primaryRow.addView(v);
                        if (firstPrimaryView == null) firstPrimaryView = v;
                    }
                }
            }

            // 2. ICON ROW: DYNAMIC BUTTONS
            List<Integer> iconDrawables = new ArrayList<>();
            List<Integer> iconActionIds = new ArrayList<>();

            // 0: Segna come visto / non visto
            iconDrawables.add((mVideo.isWatched() || mIsVideoWatched) ? R.drawable.trakt_watched : R.drawable.ic_eye_outline);
            iconActionIds.add(0);

            // 2: Menu Sottotitoli
            iconDrawables.add(R.drawable.ic_infuse_subtitles);
            iconActionIds.add(2);

            // 5: Riproduci Trailer (se disponibile)
            if (mTrailers != null && !mTrailers.isEmpty()) {
                iconDrawables.add(R.drawable.ic_infuse_trailer);
                iconActionIds.add(5);
            }

            // 1: Aggiungi a elenco / Segnalibro
            iconDrawables.add(R.drawable.ic_infuse_bookmark);
            iconActionIds.add(1);

            // 3: Info sul file
            iconDrawables.add(R.drawable.ic_infuse_info);
            iconActionIds.add(3);

            // 4: Menu "Altro" (...)
            iconDrawables.add(R.drawable.ic_infuse_more);
            iconActionIds.add(4);

            for (int idx = 0; idx < iconDrawables.size(); idx++) {
                final int buttonActionId = iconActionIds.get(idx);
                View v = inflater.inflate(R.layout.infuse_action_icon_pill, iconRow, false);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(iconWidth, iconHeight);
                lp.setMarginEnd(iconMarginEnd);
                v.setLayoutParams(lp);

                ImageView iv = v.findViewById(R.id.icon);
                if (iv != null) iv.setImageResource(iconDrawables.get(idx));

                v.setFocusable(true);
                v.setFocusableInTouchMode(false);
                v.setOnFocusChangeListener((view, hasFocus) -> {
                    float scale = hasFocus ? 1.08f : 1.0f;
                    view.animate().scaleX(scale).scaleY(scale).setDuration(150).start();
                });

                v.setOnClickListener(v1 -> {
                    Activity currentActivity = getActivity();
                    if (currentActivity == null || mVideo == null) return;

                    switch (buttonActionId) {
                        case 0: {
                            boolean currentlyWatched = mVideo.isWatched() || mIsVideoWatched;
                            mIsVideoWatched = !currentlyWatched;
                            if (currentlyWatched) {
                                DbUtils.markAsNotRead(currentActivity, mVideo);
                                if (iv != null) iv.setImageResource(R.drawable.ic_eye_outline);
                            } else {
                                DbUtils.markAsRead(currentActivity, mVideo);
                                if (iv != null) iv.setImageResource(R.drawable.trakt_watched);
                            }
                            currentActivity.setResult(Activity.RESULT_OK);
                            break;
                        }
                        case 1: {
                            if (mVideo.hasScraperData()) {
                                showInfuseBookmarkDialog();
                            } else {
                                Toast.makeText(currentActivity, R.string.scrap_no_info, Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }
                        case 2: {
                            List<String> subItems = Arrays.asList(getString(R.string.get_subtitles_online), getString(R.string.get_subtitles_on_drive));
                            List<Runnable> subActions = Arrays.asList(this::performSubtitleDownload, this::performSubtitleChoose);
                            showInfuseListDialog(getString(R.string.category_languages), subItems, subActions);
                            break;
                        }
                        case 3: {
                            StringBuilder infoMsg = new StringBuilder(mVideo.getFilePath() != null ? mVideo.getFilePath() : "");
                            if (mVideo.getMetadata() != null) {
                                infoMsg.append("\n\nSize: ").append(Formatter.formatFileSize(currentActivity, mVideo.getMetadata().getFileSize()));
                            }
                            showInfuseMessageDialog(getString(R.string.info_tab_file), infoMsg.toString());
                            break;
                        }
                        case 4: {
                            List<String> moreLabels = new ArrayList<>();
                            List<Runnable> moreActions = new ArrayList<>();

                            if (mShouldDisplayRemoveFromList) {
                                moreLabels.add(getString(R.string.remove_from_list));
                                moreActions.add(() -> mOnActionClickedListener.onActionClicked(new Action(VideoActionAdapter.ACTION_REMOVE_FROM_LIST)));
                            }

                            if (mVideo.hasScraperData()) {
                                if (mAvailablePosters != null && !mAvailablePosters.isEmpty()) {
                                    final String posterHeader = getString(!mIsTvEpisode ? R.string.leanback_posters_header : R.string.leanback_season_posters_header);
                                    moreLabels.add(posterHeader);
                                    moreActions.add(() -> showInfuseImagePickerDialog(posterHeader, mAvailablePosters, true));
                                }
                                if (mAvailableBackdrops != null && !mAvailableBackdrops.isEmpty()) {
                                    final String backdropHeader = getString(!mIsTvEpisode ? R.string.leanback_backdrops_header : R.string.leanback_tvshow_backdrops_header);
                                    moreLabels.add(backdropHeader);
                                    moreActions.add(() -> showInfuseImagePickerDialog(backdropHeader, mAvailableBackdrops, false));
                                }
                                moreLabels.add(getString(R.string.scrap_remove));
                                moreActions.add(() -> {
                                    Activity a = getActivity();
                                    if (a == null) return;
                                    DbUtils.deleteScraperInfo(a, mVideo);
                                    View logoView = root.findViewById(R.id.infuse_clear_logo);
                                    View titleView = root.findViewById(androidx.leanback.R.id.lb_details_description_title);
                                    if (logoView != null) logoView.setVisibility(View.GONE);
                                    if (titleView != null) titleView.setVisibility(View.VISIBLE);
                                });
                            } else {
                                moreLabels.add(getString(R.string.leanback_scrap));
                                moreActions.add(() -> mOnActionClickedListener.onActionClicked(new Action(VideoActionAdapter.ACTION_SCRAP)));
                            }

                            moreLabels.add(getString(R.string.video_browser_unindex_file));
                            moreActions.add(() -> {
                                Activity a = getActivity();
                                if (a != null) DbUtils.markAsHiddenByUser(a, mVideo);
                            });

                            moreLabels.add(getString(R.string.delete));
                            moreActions.add(() -> deleteFile_async(mVideo));

                            showInfuseListDialog(null, moreLabels, moreActions);
                            break;
                        }
                        case 5: {
                            ScraperTrailer bestTrailer = null;
                            if (mTrailers != null && !mTrailers.isEmpty()) {
                                for (ScraperTrailer t : mTrailers) {
                                    if (t != null && t.mName != null) {
                                        String nameLower = t.mName.toLowerCase();
                                        if (nameLower.contains("official") || nameLower.contains("trailer")) {
                                            bestTrailer = t;
                                            break;
                                        }
                                    }
                                }
                                if (bestTrailer == null) {
                                    bestTrailer = mTrailers.get(0);
                                }
                            }
                            playInfuseTrailer(bestTrailer);
                            break;
                        }
                    }
                });
                iconRow.addView(v);
            }

        iconRow.setVisibility(View.VISIBLE);
        primaryRow.setVisibility(View.VISIBLE);
        if (iconRow.getLayoutParams() != null) {
            iconRow.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
            iconRow.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        if (primaryRow.getLayoutParams() != null) {
            primaryRow.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
            primaryRow.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        iconRow.requestLayout();
        primaryRow.requestLayout();
        infuseActionsContainer.requestLayout();

        // Link D-Pad Up/Down
        View firstIconView = iconRow.getChildCount() > 0 ? iconRow.getChildAt(0) : null;
        if (firstIconView != null) {
            int iconId = firstIconView.getId() != View.NO_ID ? firstIconView.getId() : View.generateViewId();
            firstIconView.setId(iconId);
            for (int j = 0; j < primaryRow.getChildCount(); j++) {
                primaryRow.getChildAt(j).setNextFocusUpId(iconId);
            }
        }
        if (firstPrimaryView != null) {
            int primaryId = firstPrimaryView.getId() != View.NO_ID ? firstPrimaryView.getId() : View.generateViewId();
            firstPrimaryView.setId(primaryId);
            for (int j = 0; j < iconRow.getChildCount(); j++) {
                iconRow.getChildAt(j).setNextFocusDownId(primaryId);
            }
        }

        if (hadIconFocus && focusedIconIndex >= 0 && focusedIconIndex < iconRow.getChildCount()) {
            final View toRestore = iconRow.getChildAt(focusedIconIndex);
            toRestore.post(toRestore::requestFocus);
        } else if (firstPrimaryView != null) {
            final View toFocus = firstPrimaryView;
            toFocus.post(toFocus::requestFocus);
            toFocus.postDelayed(toFocus::requestFocus, 120);
        }

        root.requestLayout();
        }
        if (mOverviewRowPresenter != null) {
            mOverviewRowPresenter.updateBackgroundColor(Color.TRANSPARENT);
        }
    }
    // END PULCIX CUSTOM THEME

    private void updateSubtitleRowWhenReady() {
        if (log.isDebugEnabled()) {
            log.debug("updateSubtitleRowWhenReady: called, metadata={}, cache entry={}",
                    (mVideo != null && mVideo.getMetadata() != null),
                    (mVideo != null && mSubtitleListCache.get(mVideo.getFileUri()) != null));
        }
        Activity activity = getActivity();
        if (activity == null || mVideo == null || mVideo.getMetadata() == null || mSubtitleListCache.get(mVideo.getFileUri()) == null) {
            return;
        }

        if (log.isDebugEnabled()) log.debug("updateSubtitleRowWhenReady: updating row");
        try {
            mSubtitlesDetailsRow = new SubtitlesDetailsRow(activity, mVideo, mSubtitleListCache.get(mVideo.getFileUri()));
            if (!ThemeManager.getInstance(activity).isInfuseTheme() && mAdapter != null) {
                mAdapter.replace(INDEX_SUBTITLES, mSubtitlesDetailsRow);
            }
            if (log.isDebugEnabled()) log.debug("updateSubtitleRowWhenReady: row updated successfully");
        } catch (Exception e) {
            log.error("updateSubtitleRowWhenReady: error updating row", e);
        }
    }

    /** Implements SubtitleInterface */
    @Override
    public void performSubtitleDownload() {
        Activity activity = getActivity();
        if (activity == null || mVideo == null) return;
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClass(activity, SubtitlesDownloaderActivity2.class);
        intent.putExtra(SubtitlesDownloaderActivity2.FILE_URL, mVideo.getFilePath());
        startActivityForResult(intent, REQUEST_CODE_SUBTITLES_ACTIVITY);
    }

    /** Implements SubtitleInterface */
    @Override
    public void performSubtitleChoose() {
        Activity activity = getActivity();
        if (activity == null || mVideo == null) return;
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClass(activity, SubtitlesWizardActivity.class);
        intent.setData(mVideo.getFileUri());
        startActivityForResult(intent, REQUEST_CODE_SUBTITLES_ACTIVITY);
    }

    private void startAds(int requestCode) {
        Activity activity = getActivity();
        if (activity == null || mVideo == null) return;
        int resume = PlayerActivity.RESUME_FROM_LAST_POS;
        int resumePos = -1;
        switch (requestCode) {
            case REQUEST_CODE_LOCAL_RESUME_AFTER_ADS_ACTIVITY:
                resume = PlayerActivity.RESUME_FROM_LOCAL_POS;
                resumePos = mVideo.getResumeMs();
                break;
            case REQUEST_CODE_RESUME_AFTER_ADS_ACTIVITY:
                resume = PlayerActivity.RESUME_FROM_LAST_POS;
                resumePos = mVideo.getResumeMs();
                break;
            case REQUEST_CODE_REMOTE_RESUME_AFTER_ADS_ACTIVITY:
                resume = PlayerActivity.RESUME_FROM_REMOTE_POS;
                resumePos = mVideo.getRemoteResumeMs();
                break;
            case REQUEST_CODE_PLAY_FROM_BEGIN_AFTER_ADS_ACTIVITY:
                resume = PlayerActivity.RESUME_NO;
                break;
        }
        mResumeFromPlayer = true;
        PlayUtils.startVideo(activity, mVideo, resume, false, resumePos, this, activity.getIntent().getLongExtra(EXTRA_LIST_ID, -1));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SUBTITLES_ACTIVITY && resultCode == Activity.RESULT_OK) {
            if (log.isDebugEnabled()) log.debug("Get RESULT_OK from SubtitlesDownloaderActivity/SubtitlesWizardActivity");
            if (mSubtitleFilesListerTask != null) {
                mSubtitleFilesListerTask.cancel(true);
            }
            Activity activity = getActivity();
            if (activity != null) {
                mSubtitleFilesListerTask = new SubtitleFilesListerTask(activity).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mVideo);
            }
        } else if (requestCode == PLAY_ACTIVITY_REQUEST_CODE) {
            ExternalPlayerResultListener.getInstance().onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == REQUEST_CODE_SCRAP) {
            if (log.isDebugEnabled()) log.debug("Returned from ManualVideoScrappingActivity, reloading video details");
            mFirstOnResume = true;
            mShouldUpdateRemoteResume = true;
            Activity activity = getActivity();
            if (activity != null) {
                LoaderManager.getInstance(this).restartLoader(1, null, this);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /** Saves a Poster as default poster for a video and update the current poster */
    private class PosterSaverTask extends AsyncTask<ScraperImage, Void, Bitmap> {
        private final int mSeason;
        private final Activity mActivity;

        public PosterSaverTask(Activity activity, int season) {
            mActivity = activity;
            mSeason = season;
        }

        private Activity getActivity() {
            return mActivity;
        }

        @Override
        protected Bitmap doInBackground(ScraperImage... params) {
            ScraperImage poster = params[0];
            if (mVideo instanceof Movie) {
                poster.setOnlineId(((Movie) mVideo).getOnlineId());
            } else if (mVideo instanceof Episode) {
                poster.setOnlineId(((Episode) mVideo).getOnlineId());
            }
            if (poster.download(getActivity())) {
                poster.setAsDefault(getActivity(), mSeason);
            }
            Bitmap bitmap = null;
            if (isAdded()) {
                try {
                    bitmap = Picasso.get()
                            .load(poster.getLargeFileF())
                            .noFade()
                            .resize(getResources().getDimensionPixelSize(R.dimen.poster_width), getResources().getDimensionPixelSize(R.dimen.poster_height))
                            .centerCrop()
                            .get();
                } catch (IOException e) {
                    log.error("PosterSaverTask Picasso load exception", e);
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (isCancelled()) return;
            Activity activity = getActivity();
            if (activity == null || activity.isDestroyed() || !isAdded()) return;

            if (result != null) {
                mPoster = result;
                if (mVideo != null && (mVideo.isWatched() || mIsVideoWatched)) {
                    result = PresenterUtils.addWatchedMark(result, getContext());
                }
                if (mDetailsOverviewRow != null) {
                    mDetailsOverviewRow.setImageBitmap(activity, result);
                    mDetailsOverviewRow.setImageScaleUpAllowed(true);
                }

                // BEGIN PULCIX CUSTOM THEME
                int color;
                if (ThemeManager.getInstance(activity).isInfuseTheme()) {
                    color = Color.BLACK;
                } else {
                    Palette palette = Palette.from(result).generate();
                    if (palette.getDarkVibrantSwatch() != null) {
                        color = palette.getDarkVibrantSwatch().getRgb();
                    } else if (palette.getDarkMutedSwatch() != null) {
                        color = palette.getDarkMutedSwatch().getRgb();
                    } else {
                        color = ThemeManager.getInstance(activity).getDetailsPrimaryColor();
                    }
                }
                // END PULCIX CUSTOM THEME

                if (color != mColor) {
                    mColor = color;
                    mVideoBadgePresenter.setSelectedBackgroundColor(color);
                    mOverviewRowPresenter.updateBackgroundColor(color);
                    mOverviewRowPresenter.updateActionsBackgroundColor(getDarkerColor(color));

                    if (mAdapter != null) {
                        for (Presenter pres : mAdapter.getPresenterSelector().getPresenters()) {
                            if (pres instanceof BackgroundColorPresenter) {
                                ((BackgroundColorPresenter) pres).setBackgroundColor(color);
                            }
                        }
                    }
                }
                Toast.makeText(activity, R.string.leanback_poster_changed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, R.string.error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /** Saves a Backdrop as default for a video and update the current backdrop */
    private class BackdropSaverTask extends AsyncTask<ScraperImage, Void, Void> {

        private final Activity mActivity;

        public BackdropSaverTask(Activity activity) {
            mActivity = activity;
        }

        private Activity getActivity() {
            return mActivity;
        }

        @Override
        protected Void doInBackground(ScraperImage... params) {
            ScraperImage backdrop = params[0];
            if (backdrop.setAsDefault(getActivity())) {
                backdrop.download(getActivity());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (isCancelled()) return;
            Activity activity = getActivity();
            if (activity == null || activity.isDestroyed() || !isAdded()) return;

            if (mBackdropTask != null) {
                mBackdropTask.cancel(true);
            }
            if (!mLaunchedFromPlayer) {
                mBackdropTask = new BackdropTask(activity, VideoInfoCommonClass.getDarkerColor(mColor)).execute(mVideo);
            }
            Toast.makeText(activity, R.string.leanback_backdrop_changed, Toast.LENGTH_SHORT).show();
            activity.setResult(Activity.RESULT_OK);
        }
    }

    //---------------------------------------------------

    /**
     * Implements PlayUtils.SubtitleDownloadListener
     */
    @Override
    public void onDownloadStart(final SubtitleManager downloader) {
        mDownloadingSubs = true;
        mHandler.postDelayed(() -> {
            if (mDownloadingSubs && isAdded()) {
                showSubtitleDialog(downloader);
            }
        }, DIALOG_LAUNCH_DELAY_MS);
    }

    /**
     * Implements PlayUtils.SubtitleDownloadListener
     */
    @Override
    public void onDownloadEnd() {
        mDownloadingSubs = false;
        if (mDialogRetrieveSubtitles != null) {
            mDialogRetrieveSubtitles.dismiss();
        }
    }

    public void showSubtitleDialog(SubtitleManager downloader) {
        mDialogRetrieveSubtitles = new DialogRetrieveSubtitles();
        mDialogRetrieveSubtitles.show(getParentFragmentManager(), null);
        mDialogRetrieveSubtitles.setDownloader(downloader);
    }

    public static class DialogRetrieveSubtitles extends DialogFragment {
        private SubtitleManager mDownloader;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            NovaProgressDialog npd = new NovaProgressDialog(getActivity());
            npd.setMessage(getString(R.string.dialog_subloader_copying));
            npd.setIndeterminate(true);
            npd.setCancelable(true);
            npd.setCanceledOnTouchOutside(false);
            return npd;
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            if (mDownloader != null) {
                mDownloader.abort();
            }
        }

        public void setDownloader(SubtitleManager downloader) {
            mDownloader = downloader;
        }
    }

    @Override
    public void onVideoFileRemoved(final Uri videoFile, boolean askForFolderRemoval, final Uri folder) {
        if (log.isDebugEnabled()) log.debug("onVideoFileRemoved: {}", videoFile);
        final Activity activity = getActivity();
        if (activity == null) {
            sendDeleteResult(videoFile);
            return;
        }

        Toast.makeText(activity, R.string.delete_done, Toast.LENGTH_SHORT).show();
        if (!askForFolderRemoval) {
            sendDeleteResult(videoFile);
            return;
        }

        if (ThemeManager.getInstance(activity).isInfuseTheme()) {
            List<String> items = Arrays.asList(getString(R.string.yes), getString(R.string.no));
            List<Runnable> actions = Arrays.asList(
                    () -> {
                        Activity a = getActivity();
                        if (a != null) {
                            delete = new Delete(VideoDetailsFragment.this, a);
                            deleteUrisList = Collections.singletonList(folder);
                            delete.deleteFolder(folder);
                        }
                    },
                    () -> sendDeleteResult(videoFile)
            );
            showInfuseListDialog(getString(R.string.confirm_delete_parent_folder), items, actions);
        } else {
            AlertDialog.Builder b = new AlertDialog.Builder(activity).setTitle("");
            b.setIcon(R.drawable.filetype_new_folder);
            b.setMessage(R.string.confirm_delete_parent_folder);
            b.setNegativeButton(R.string.no, (dialogInterface, i) -> sendDeleteResult(videoFile))
                    .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                        Activity a = getActivity();
                        if (a != null) {
                            delete = new Delete(VideoDetailsFragment.this, a);
                            deleteUrisList = Collections.singletonList(folder);
                            delete.deleteFolder(folder);
                        }
                    });
            b.setOnCancelListener(dialogInterface -> sendDeleteResult(videoFile));
            b.create().show();
        }
    }

    // BEGIN PULCIX CUSTOM THEME
    private void loadInfuseClearLogo(BaseTags tags) {
        if (tags == null) return;
        final Activity activity = getActivity();
        if (activity == null || activity.isDestroyed() || activity.isFinishing()) return;

        String type = null;
        long tmdbId = -1;

        if (tags instanceof MovieTags) {
            type = "movie";
            tmdbId = tags.getOnlineId();
        } else if (tags instanceof EpisodeTags) {
            type = "tv";
            EpisodeTags epTags = (EpisodeTags) tags;
            tmdbId = mOnlineId > 0 ? mOnlineId : epTags.getShowId();
        }

        if (type == null || tmdbId <= 0) return;

        final File cacheFile = new File(activity.getCacheDir(), "clearlogo_" + type + "_" + tmdbId + ".png");
        if (cacheFile.exists() && cacheFile.length() > 0) {
            displayClearLogoFile(cacheFile);
            return;
        }

        final String finalType = type;
        final long finalTmdbId = tmdbId;
        final String apiKey = getTmdbApiKey(activity);

        new Thread(() -> {
            HttpURLConnection conn = null;
            HttpURLConnection dlConn = null;
            try {
                String urlString = "https://api.themoviedb.org/3/" + finalType + "/" + finalTmdbId + "/images?include_image_language=en,it,null&api_key=" + apiKey;
                conn = (HttpURLConnection) new URL(urlString).openConnection();
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(8000);
                conn.setRequestMethod("GET");

                if (conn.getResponseCode() != 200) return;

                StringBuilder sb = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                }

                JSONObject json = new JSONObject(sb.toString());
                JSONArray logos = json.optJSONArray("logos");
                String selectedFilePath = null;
                if (logos != null) {
                    for (int i = 0; i < logos.length(); i++) {
                        JSONObject logoObj = logos.optJSONObject(i);
                        if (logoObj == null) continue;
                        String path = logoObj.optString("file_path");
                        if (path != null && !path.isEmpty()) {
                            if (selectedFilePath == null || path.endsWith(".png")) {
                                selectedFilePath = path;
                                if (path.endsWith(".png")) break;
                            }
                        }
                    }
                }

                if (selectedFilePath == null) return;

                dlConn = (HttpURLConnection) new URL(TMDB_IMAGE_W500_URL + selectedFilePath).openConnection();
                dlConn.setConnectTimeout(8000);
                dlConn.setReadTimeout(8000);
                if (dlConn.getResponseCode() == 200) {
                    try (InputStream inStream = dlConn.getInputStream();
                         FileOutputStream outStream = new FileOutputStream(cacheFile)) {
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = inStream.read(buffer)) != -1) {
                            outStream.write(buffer, 0, bytesRead);
                        }
                    }
                    if (cacheFile.exists() && cacheFile.length() > 0 && mHandler != null) {
                        mHandler.post(() -> displayClearLogoFile(cacheFile));
                    }
                }
            } catch (Exception e) {
                if (log.isDebugEnabled()) log.debug("loadInfuseClearLogo failed", e);
            } finally {
                if (dlConn != null) dlConn.disconnect();
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    private void displayClearLogoFile(File file) {
        if (!isAdded()) return;
        View root = getView();
        if (root == null || file == null || !file.exists()) return;
        ImageView logoView = root.findViewById(R.id.infuse_clear_logo);
        View titleView = root.findViewById(androidx.leanback.R.id.lb_details_description_title);

        if (logoView != null) {
            Picasso.get().load(file).into(logoView, new Callback() {
                @Override
                public void onSuccess() {
                    logoView.setVisibility(View.VISIBLE);
                    if (titleView != null) {
                        titleView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onError(Exception e) {
                    logoView.setVisibility(View.GONE);
                    if (titleView != null) {
                        titleView.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }
    // END PULCIX CUSTOM THEME

    private void sendDeleteResult(Uri file) {
        if (log.isDebugEnabled()) log.debug("sendDeleteResult: {}", file);
        Intent intent = new Intent();
        intent.setData(file);
        Activity activity = getActivity();
        if (activity != null) {
            activity.setResult(ListingActivity.RESULT_FILE_DELETED, intent);
        }
        slightlyDelayedFinish();
    }

    @Override
    public void onDeleteVideoFailed(Uri videoFile) {
        if (log.isDebugEnabled()) log.debug("onDeleteVideoFailed: {}", videoFile);
        Activity activity = getActivity();
        if (activity != null) {
            Toast.makeText(activity, R.string.delete_error, Toast.LENGTH_SHORT).show();
        }
        slightlyDelayedFinish();
    }

    @Override
    public void onFolderRemoved(Uri folder) {
        if (log.isDebugEnabled()) log.debug("onFolderRemoved: {}", folder);
        Activity activity = getActivity();
        if (activity != null) {
            Toast.makeText(activity, R.string.delete_done, Toast.LENGTH_SHORT).show();
        }
        sendDeleteResult(folder);
    }

    @Override
    public void onDeleteSuccess() {
        if (log.isDebugEnabled()) log.debug("onDeleteSuccess");
    }

    //---------------------------------------------------

    private void deleteFile_async(Video video) {
        Activity activity = getActivity();
        if (video == null || activity == null) return;
        if (log.isDebugEnabled()) log.debug("deleteFile_async: {}", video.getFileUri());
        delete = new Delete(this, activity);
        deleteUrisList = new ArrayList<>(Collections.singletonList(video.getFileUri()));
        delete.startDeleteProcess(video.getFileUri());
    }

    private void deleteScraperInfo(Video video) {
        Activity activity = getActivity();
        if (video == null || activity == null) return;
        if (log.isDebugEnabled()) log.debug("deleteScraperInfo: {}", video.getFileUri());
        ContentValues values = new ContentValues(2);
        values.put(VideoStore.Video.VideoColumns.ARCHOS_MEDIA_SCRAPER_ID, "-1");
        values.put(VideoStore.Video.VideoColumns.ARCHOS_MEDIA_SCRAPER_TYPE, "-1");
        final String selection = VideoStore.MediaColumns._ID + "=?";
        final String[] selectionArgs = new String[]{Long.toString(video.getId())};

        activity.getContentResolver().update(VideoStore.Video.Media.EXTERNAL_CONTENT_URI, values, selection, selectionArgs);
        delete = new Delete(null, activity);
        deleteUrisList = Collections.singletonList(video.getFileUri());
        delete.deleteAssociatedNfoFiles(video.getFileUri());
    }

    private void unindexRemoteVideo(Video video) {
        Activity activity = getActivity();
        if (activity == null || mVideo == null) return;
        int numberDeleted = activity.getContentResolver().delete(VideoStoreInternal.FILES_SCANNED,
                VideoStore.MediaColumns.DATA + " = ?",
                new String[]{mVideo.getFilePath()});
        if (ArchosFeatures.isAndroidTV(getContext())) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                Intent intent = new Intent(BootupRecommandationService.UPDATE_ACTION);
                intent.setPackage(ArchosUtils.getGlobalContext().getPackageName());
                activity.sendBroadcast(intent);
            } else {
                ChannelManager.refreshChannels(getContext());
            }
        }
        if (numberDeleted != 1) {
            Toast.makeText(activity, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

    private List<String> getWebLinks(BaseTags tags) {
        List<String> list = new LinkedList<>();
        Activity activity = getActivity();
        if (activity == null || tags == null) return list;

        // TMDB
        if (tags instanceof MovieTags) {
            final long onlineId = tags.getOnlineId();
            if (onlineId > 0) {
                final String language = Scraper.getLanguage(activity);
                list.add(String.format(getResources().getString(R.string.tmdb_movie_title_url), onlineId, language));
            }
        } else if (tags instanceof EpisodeTags) {
            if (mOnlineId > 0) {
                final String language = Scraper.getLanguage(activity);
                list.add(String.format(getResources().getString(R.string.tmdb_tvshow_title_url), mOnlineId, language));
            }
        }

        // IMDB (valid for both movies and episodes)
        String imdbId = tags.getImdbId();
        if (imdbId != null && !imdbId.isEmpty()) {
            list.add(getResources().getString(R.string.imdb_title_url) + imdbId);
        }

        return list;
    }

    private void slightlyDelayedFinish() {
        if (log.isDebugEnabled()) log.debug("slightlyDelayedFinish");
        if (mHandler != null) {
            mHandler.postDelayed(() -> {
                Activity activity = getActivity();
                if (activity != null) {
                    activity.finish();
                }
            }, 200);
        }
    }

    public void onKeyDown(int keyCode) {
        final Activity activity = getActivity();
        if (activity == null || mVideo == null) return;

        int direction = -1;

        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                setSelectedPosition(0);
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                VideoMetadata mMetadata = mVideo.getMetadata();
                isFilePlayable = !(mMetadata != null && mMetadata.getFileSize() == 0 && mMetadata.getVideoTrack() == null && mMetadata.getAudioTrackNb() == 0);
                if (isFilePlayable) {
                    startAds(REQUEST_CODE_RESUME_AFTER_ADS_ACTIVITY);
                } else {
                    Toast.makeText(activity, R.string.player_err_cantplayvideo, Toast.LENGTH_SHORT).show();
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                direction = Gravity.RIGHT;
                break;
            case KeyEvent.KEYCODE_MEDIA_REWIND:
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                direction = Gravity.LEFT;
                break;
        }

        if (direction == -1) return;

        CursorLoader loader = null;
        if (mVideo instanceof Movie) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
            String sortOrder = prefs.getString(AllMoviesGridFragment.SORT_PARAM_KEY, MoviesLoader.DEFAULT_SORT);
            boolean showWatched = prefs.getBoolean(AllMoviesGridFragment.SHOW_WATCHED_KEY, true);
            loader = new MoviesLoader(activity, sortOrder, showWatched, true, VideoLoader.GRIDVIDEO_THROTTLE, VideoLoader.GRIDVIDEO_THROTTLE_DELAY);
        } else if (mVideo instanceof Episode) {
            EpisodeTags tags = (EpisodeTags) mVideo.getFullScraperTags(activity);
            if (tags != null) {
                loader = new EpisodesLoader(activity, tags.getShowId(), -1, true);
            }
        }

        if (loader == null) return;

        if (Looper.myLooper() == null) Looper.prepare();
        Cursor c = loader.loadInBackground();
        Video video = null;
        if (c != null) {
            CompatibleCursorMapperConverter converter = new CompatibleCursorMapperConverter(new VideoCursorMapper());
            final int count = c.getCount();
            for (int i = 0; i < count; i++) {
                c.moveToPosition(i);
                Video v = (Video) converter.convert(c);
                if (v.getId() == mVideo.getId()) {
                    if (direction == Gravity.LEFT) {
                        c.moveToPosition(i - 1 >= 0 ? i - 1 : count - 1);
                    } else {
                        c.moveToPosition(i + 1 <= count - 1 ? i + 1 : 0);
                    }
                    Video nv = (Video) converter.convert(c);
                    if (nv.getId() != v.getId()) {
                        video = nv;
                    }
                    break;
                }
            }
            c.close();
        }

        if (video != null) {
            activity.getWindow().setExitTransition(new Slide(direction == Gravity.LEFT ? Gravity.RIGHT : Gravity.LEFT));
            final Intent intent = new Intent(activity, VideoDetailsActivity.class);
            intent.putExtra(VideoDetailsFragment.EXTRA_VIDEO, video);
            intent.putExtra(VideoDetailsActivity.SLIDE_TRANSITION_EXTRA, true);
            intent.putExtra(VideoDetailsActivity.SLIDE_DIRECTION_EXTRA, direction);
            if (mOverlay != null) mOverlay.hide();
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
            mHandler.postDelayed(() -> {
                Activity a = getActivity();
                if (a != null) a.finish();
            }, 1000);
        }
    }

    public static void setWatchState(Boolean isVideoWatched) {
        if (log.isDebugEnabled()) log.debug("setWatchState to {}", isVideoWatched);
        mIsVideoWatched = isVideoWatched;
    }

    public static int getDominantColor() {
        return dominantColor;
    }

    // BEGIN PULCIX CUSTOM THEME
    private static final Map<String, String> sActorPhotoCache = new ConcurrentHashMap<>();

    private static class InfuseCastMember {
        final String name;
        final String role;
        String profileUrl;
        boolean fetchAttempted = false;

        InfuseCastMember(String name, String role, String profileUrl) {
            this.name = name;
            this.role = role;
            this.profileUrl = profileUrl;
        }
    }

    private static class InfuseCastPresenter extends Presenter {
        @Override
        public Presenter.ViewHolder onCreateViewHolder(ViewGroup parent) {
            Context context = parent.getContext();
            float density = context.getResources().getDisplayMetrics().density;
            int width = (int) (130 * density);
            int height = (int) (175 * density);
            int imgHeight = (int) (115 * density);

            LinearLayout card = new LinearLayout(context);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setGravity(Gravity.CENTER_HORIZONTAL);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
            lp.setMargins(0, 0, 0, 0);
            lp.setMarginStart(0);
            lp.setMarginEnd(0);
            card.setLayoutParams(lp);

            GradientDrawable bg = new GradientDrawable();
            bg.setColor(0xFF1C1C1E);
            bg.setCornerRadius(14 * density);
            card.setBackground(bg);
            card.setClipToOutline(true);

            card.setFocusable(true);
            card.setFocusableInTouchMode(false);
            final int strokeWidth = (int) (2 * density);
            card.setOnFocusChangeListener((v, hasFocus) -> {
                float scale = hasFocus ? 1.08f : 1.0f;
                v.animate().scaleX(scale).scaleY(scale).setDuration(150).start();
                bg.setStroke(hasFocus ? strokeWidth : 0, hasFocus ? 0xFFFF7F02 : 0);
            });

            FrameLayout topFrame = new FrameLayout(context);
            topFrame.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, imgHeight));

            ImageView photoIv = new ImageView(context);
            photoIv.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            photoIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ColorMatrix bwMatrix = new ColorMatrix();
            bwMatrix.setSaturation(0f);
            photoIv.setColorFilter(new ColorMatrixColorFilter(bwMatrix));

            TextView initialsTv = new TextView(context);
            int avatarSize = (int) (48 * density);
            FrameLayout.LayoutParams avatarLp = new FrameLayout.LayoutParams(avatarSize, avatarSize);
            avatarLp.gravity = Gravity.CENTER;
            initialsTv.setLayoutParams(avatarLp);
            initialsTv.setGravity(Gravity.CENTER);
            initialsTv.setTextColor(0xFFFFFFFF);
            initialsTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            initialsTv.setTypeface(null, Typeface.BOLD);

            GradientDrawable avatarBg = new GradientDrawable();
            avatarBg.setShape(GradientDrawable.OVAL);
            avatarBg.setColor(0xFF2C2C2E);
            initialsTv.setBackground(avatarBg);

            topFrame.addView(initialsTv);
            topFrame.addView(photoIv);

            LinearLayout textGroup = new LinearLayout(context);
            textGroup.setOrientation(LinearLayout.VERTICAL);
            textGroup.setGravity(Gravity.CENTER_HORIZONTAL);
            int pad6 = (int) (6 * density);
            textGroup.setPadding(pad6, pad6, pad6, pad6);
            textGroup.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            TextView nameTv = new TextView(context);
            nameTv.setTextColor(0xFFFFFFFF);
            nameTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            nameTv.setTypeface(null, Typeface.BOLD);
            nameTv.setGravity(Gravity.CENTER);
            nameTv.setMaxLines(1);
            nameTv.setEllipsize(TextUtils.TruncateAt.END);

            TextView roleTv = new TextView(context);
            roleTv.setTextColor(0xFF8E8E93);
            roleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            roleTv.setGravity(Gravity.CENTER);
            roleTv.setMaxLines(1);
            roleTv.setEllipsize(TextUtils.TruncateAt.END);

            textGroup.addView(nameTv);
            textGroup.addView(roleTv);

            card.addView(topFrame);
            card.addView(textGroup);

            return new Presenter.ViewHolder(card);
        }

        @Override
        public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
            if (!(item instanceof InfuseCastMember)) return;
            InfuseCastMember member = (InfuseCastMember) item;
            ViewGroup card = (ViewGroup) viewHolder.view;
            card.setTag(member.name);

            FrameLayout topFrame = (FrameLayout) card.getChildAt(0);
            TextView initialsTv = (TextView) topFrame.getChildAt(0);
            ImageView photoIv = (ImageView) topFrame.getChildAt(1);

            LinearLayout textGroup = (LinearLayout) card.getChildAt(1);
            TextView nameTv = (TextView) textGroup.getChildAt(0);
            TextView roleTv = (TextView) textGroup.getChildAt(1);

            nameTv.setText(member.name != null ? member.name : "");
            roleTv.setText(member.role != null && !member.role.isEmpty() ? member.role : "");

            if (member.name != null && !member.name.trim().isEmpty()) {
                String trimmedName = member.name.trim();
                String[] parts = trimmedName.split("\\s+");
                if (parts.length >= 2) {
                    initialsTv.setText(String.valueOf(new char[]{
                            Character.toUpperCase(parts[0].charAt(0)),
                            Character.toUpperCase(parts[parts.length - 1].charAt(0))
                    }));
                } else {
                    initialsTv.setText(String.valueOf(Character.toUpperCase(trimmedName.charAt(0))));
                }
            } else {
                initialsTv.setText("?");
            }

            photoIv.setImageDrawable(null);
            photoIv.setVisibility(View.GONE);
            initialsTv.setVisibility(View.VISIBLE);

            if (member.profileUrl == null && member.name != null && sActorPhotoCache.containsKey(member.name)) {
                member.profileUrl = sActorPhotoCache.get(member.name);
            }

            if (member.profileUrl != null && !member.profileUrl.isEmpty()) {
                loadPhotoIntoView(card, photoIv, initialsTv, member.profileUrl);
                return;
            }

            if (member.fetchAttempted || member.name == null || member.name.trim().isEmpty()) {
                return;
            }

            member.fetchAttempted = true;
            final String actorName = member.name.trim();
            final Context ctx = card.getContext().getApplicationContext();
            final String apiKey = getTmdbApiKey(ctx);

            new Thread(() -> {
                HttpURLConnection conn = null;
                try {
                    String encodedName = URLEncoder.encode(actorName, "UTF-8");
                    String urlStr = "https://api.themoviedb.org/3/search/person?query=" + encodedName + "&api_key=" + apiKey;
                    conn = (HttpURLConnection) new URL(urlStr).openConnection();
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setConnectTimeout(6000);
                    conn.setReadTimeout(6000);
                    if (conn.getResponseCode() != 200) return;

                    StringBuilder sb = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                    }
                    JSONObject json = new JSONObject(sb.toString());
                    JSONArray results = json.optJSONArray("results");
                    if (results != null && results.length() > 0) {
                        String profilePath = results.optJSONObject(0).optString("profile_path");
                        if (profilePath != null && !profilePath.equals("null") && !profilePath.isEmpty()) {
                            final String fullUrl = TMDB_IMAGE_W185_URL + profilePath;
                            member.profileUrl = fullUrl;
                            sActorPhotoCache.put(actorName, fullUrl);
                            photoIv.post(() -> {
                                if (actorName.equals(card.getTag())) {
                                    loadPhotoIntoView(card, photoIv, initialsTv, fullUrl);
                                }
                            });
                        }
                    }
                } catch (Exception ignored) {
                } finally {
                    if (conn != null) conn.disconnect();
                }
            }).start();
        }

        private void loadPhotoIntoView(View card, ImageView photoIv, TextView initialsTv, String url) {
            float density = card.getContext().getResources().getDisplayMetrics().density;
            photoIv.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(url)
                    .resize((int) (130 * density), (int) (115 * density))
                    .centerCrop()
                    .into(photoIv, new Callback() {
                        @Override
                        public void onSuccess() {
                            photoIv.setVisibility(View.VISIBLE);
                            initialsTv.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            photoIv.setVisibility(View.GONE);
                            initialsTv.setVisibility(View.VISIBLE);
                        }
                    });
        }

        @Override
        public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        }
    }
    // END PULCIX CUSTOM THEME
}