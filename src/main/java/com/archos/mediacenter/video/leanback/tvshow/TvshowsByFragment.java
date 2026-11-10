package com.archos.mediacenter.video.leanback.tvshow;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.leanback.app.BackgroundManager;
import androidx.leanback.app.BrowseSupportFragment;
import androidx.leanback.database.CursorMapper;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.CursorObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.RowHeaderPresenter;
import androidx.leanback.widget.RowPresenter;
import androidx.leanback.widget.SearchOrbView;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.preference.PreferenceManager;

import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.browser.adapters.mappers.TvshowCursorMapper;
import com.archos.mediacenter.video.browser.loader.TvshowsByAlphaLoader;
// BEGIN PULCIX DPAD INTEGRATION
import com.archos.mediacenter.video.leanback.presenter.CustomDpadListRowPresenter;
// END PULCIX DPAD INTEGRATION
import com.archos.mediacenter.video.utils.ThemeManager;
import com.archos.mediacenter.video.utils.VideoPreferencesCommon;
import com.archos.mediacenter.video.browser.loader.TvshowsSelectionLoader;
import com.archos.mediacenter.video.leanback.CompatibleCursorMapperConverter;
import com.archos.mediacenter.video.leanback.VideoViewClickedListener;
import com.archos.mediacenter.video.leanback.overlay.Overlay;
import com.archos.mediacenter.video.leanback.presenter.PosterImageCardPresenter;
import com.archos.mediacenter.video.player.PrivateMode;
import com.archos.mediacenter.video.utils.PrivateModeUIHelper;
import com.archos.mediacenter.video.tvshow.TvshowSortOrderEntries;
import com.archos.mediacenter.video.utils.VideoPreferencesCommon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;


public abstract class TvshowsByFragment extends BrowseSupportFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final Logger log = LoggerFactory.getLogger(TvshowsByFragment.class);

    public boolean mSeparateAnimeFromShowMovie;

    private ArrayObjectAdapter mRowsAdapter;
    private Overlay mOverlay;
    private SharedPreferences mPrefs;
    protected TextView mEmptyView;

    private int mSortOrderItem;
    private String mSortOrder;

    /**
     * We can have a single instance of presenter and mapper used for all the subset rows created
     */
    private Presenter mTvshowPresenter;
    private CursorMapper mTvshowMapper;

    /**
     * keep a reference of the cursor containing the categories to check if there is actually an update when we get a new one
     */
    private Cursor mCurrentCategoriesCursor;

    private String mDefaultSort;

    /**
     * Map to update the adapter when we get the onLoadFinished() callback
     */
    SparseArray<CursorObjectAdapter> mAdaptersMap = new SparseArray<>();

    BackgroundManager bgMngr = null;
    private SharedPreferences.OnSharedPreferenceChangeListener mThemeChangeListener;

    abstract protected Loader<Cursor> getSubsetLoader(Context context);

    abstract protected CharSequence[] getSortOrderEntries();
    abstract protected String item2SortOrder(int item);
    abstract protected int sortOrder2Item(String sortOrder);
    abstract protected String getSortOrderParamKey();

    public TvshowsByFragment() {
        this(TvshowSortOrderEntries.DEFAULT_SORT);
    }

    public TvshowsByFragment(String defaultSort) {
        mDefaultSort = defaultSort;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Add private mode indicator overlay
        PrivateModeUIHelper.addPrivateModeIndicator(getActivity(), view);

        mOverlay = new Overlay(this);

        SearchOrbView searchOrbView = (SearchOrbView) getView().findViewById(R.id.title_orb);
        if (searchOrbView != null) {
            searchOrbView.setOrbIcon(ContextCompat.getDrawable(getActivity(), R.drawable.orb_sort));
        } else {
            throw new IllegalArgumentException("Did not find R.id.title_orb in BrowseFragment! Need to update the orbview hack!");
        }

        ViewGroup container = (ViewGroup) getView().findViewById(R.id.browse_frame);
        if (container != null) {
            LayoutInflater.from(getActivity()).inflate(R.layout.leanback_empty_view, container, true);
            mEmptyView = (TextView) container.findViewById(R.id.empty_view);
            mEmptyView.setText(R.string.you_have_no_tv_shows);
        } else {
            throw new IllegalArgumentException("Did not find R.id.browse_frame in BrowseFragment! Need to update the emptyview hack!");
        }

        // BEGIN PULCIX CUSTOM THEME
        view.post(() -> {
            if (ThemeManager.getInstance(getContext()).isInfuseTheme()) {
                setBrandColor(android.graphics.Color.TRANSPARENT);

                View fadeEdge = view.findViewById(androidx.leanback.R.id.fade_out_edge);
                if (fadeEdge != null) fadeEdge.setVisibility(View.GONE);

                View headersRoot = view.findViewById(androidx.leanback.R.id.browse_headers_root);
                View headersGrid = view.findViewById(androidx.leanback.R.id.browse_headers);
                View headersDock = view.findViewById(androidx.leanback.R.id.browse_headers_dock);

                View containerDock = view.findViewById(androidx.leanback.R.id.browse_container_dock);
                if (containerDock != null) {
                    float density = view.getResources().getDisplayMetrics().density;
                    containerDock.setPadding(0, containerDock.getPaddingTop(), containerDock.getPaddingRight(), containerDock.getPaddingBottom());
                }

                if (headersDock != null) headersDock.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                if (headersGrid != null) headersGrid.setBackgroundColor(android.graphics.Color.TRANSPARENT);

                float density = view.getResources().getDisplayMetrics().density;
                View targetPanel = (headersRoot != null) ? headersRoot : headersGrid;
                if (targetPanel != null) {
                    int insetLeft = (int) (16 * density);
                    int insetTop = (int) (14 * density);
                    int insetRight = (int) (12 * density);
                    int insetBottom = (int) (14 * density);

                    android.graphics.drawable.GradientDrawable cardShape = new android.graphics.drawable.GradientDrawable();
                    cardShape.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
                    cardShape.setColor(0xFF161618); // Opaco al 100%!
                    cardShape.setCornerRadius(14 * density);
                    cardShape.setStroke(Math.max(2, (int) (1.5f * density)), 0xFF38383C);

                    android.graphics.drawable.InsetDrawable insetBg = new android.graphics.drawable.InsetDrawable(
                            cardShape, insetLeft, insetTop, insetRight, insetBottom);

                    targetPanel.setClipToOutline(false);
                    targetPanel.setWillNotDraw(false);
                    targetPanel.setBackground(insetBg);
                    targetPanel.setElevation(16 * density);

                    if (targetPanel.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) targetPanel.getLayoutParams();
                        lp.width = (int) (248 * density); // Più compatto così non copre mai i titoli a destra!
                        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                        lp.topMargin = 0;
                        lp.leftMargin = 0;
                        lp.setMarginStart(0);
                        targetPanel.setLayoutParams(lp);
                    }
                    targetPanel.setPadding(0, 0, 0, 0);
                }

                if (headersGrid instanceof androidx.leanback.widget.VerticalGridView) {
                    androidx.leanback.widget.VerticalGridView vgv = (androidx.leanback.widget.VerticalGridView) headersGrid;

                    if (vgv.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                        ViewGroup.MarginLayoutParams vgvLp = (ViewGroup.MarginLayoutParams) vgv.getLayoutParams();
                        vgvLp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        vgvLp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                        vgvLp.topMargin = (int) (148 * density);
                        vgvLp.bottomMargin = (int) (8 * density);
                        vgvLp.leftMargin = 0;
                        vgvLp.rightMargin = 0;
                        vgvLp.setMarginStart(0);
                        vgvLp.setMarginEnd(0);
                        vgv.setLayoutParams(vgvLp);
                    }

                    vgv.setItemSpacing((int) (2 * density)); // Azzera o compatta lo spazio tra le row header verticali
                    vgv.setPadding(0, (int) (8 * density), 0, (int) (12 * density));
                    vgv.setClipToPadding(true);
                    vgv.setClipChildren(false);
                    vgv.setWindowAlignment(androidx.leanback.widget.VerticalGridView.WINDOW_ALIGN_BOTH_EDGE);
                    vgv.setWindowAlignmentOffset(0);
                    vgv.setWindowAlignmentOffsetPercent(androidx.leanback.widget.VerticalGridView.WINDOW_ALIGN_OFFSET_PERCENT_DISABLED);
                    vgv.setItemAlignmentOffset(0);
                    vgv.setItemAlignmentOffsetPercent(androidx.leanback.widget.VerticalGridView.ITEM_ALIGN_OFFSET_PERCENT_DISABLED);

                    for (int i = 0; i < vgv.getChildCount(); i++) {
                        updateHeaderItemStyle(vgv.getChildAt(i), false, density);
                    }

                    vgv.addOnChildViewHolderSelectedListener(new androidx.leanback.widget.OnChildViewHolderSelectedListener() {
                        @Override
                        public void onChildViewHolderSelected(androidx.recyclerview.widget.RecyclerView parent, androidx.recyclerview.widget.RecyclerView.ViewHolder child, int position, int subposition) {
                            for (int i = 0; i < vgv.getChildCount(); i++) {
                                View childView = vgv.getChildAt(i);
                                updateHeaderItemStyle(childView, childView == child.itemView, density);
                            }
                        }
                    });

                    vgv.addOnChildAttachStateChangeListener(new androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener() {
                        @Override
                        public void onChildViewAttachedToWindow(View child) {
                            // When attached, check if it's the currently selected one
                            androidx.recyclerview.widget.RecyclerView.ViewHolder holder = vgv.getChildViewHolder(child);
                            boolean isSelected = holder != null && holder.getAbsoluteAdapterPosition() == vgv.getSelectedPosition();
                            updateHeaderItemStyle(child, isSelected, density);
                        }

                        @Override
                        public void onChildViewDetachedFromWindow(View child) {
                        }
                    });
                }
            }
        });
        // END PULCIX CUSTOM THEME
    }

    // BEGIN PULCIX CUSTOM THEME
    private void updateHeaderItemStyle(View itemView, boolean isSelected, float density) {
        if (itemView == null) return;

        // 1. Blocca permanentemente il TimeAnimator di HeaderItemFocusHighlight
        lockScaleToOneRecursive(itemView);
        if (itemView.getTag(R.id.browse_headers) == null) {
            itemView.setTag(R.id.browse_headers, Boolean.TRUE);
            itemView.getViewTreeObserver().addOnPreDrawListener(() -> {
                lockScaleToOneRecursive(itemView);
                return true;
            });
        }

        ViewGroup.LayoutParams itemLp = itemView.getLayoutParams();
        if (itemLp != null) {
            itemLp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            itemLp.height = (int) (42 * density); // Ridotto per avvicinare le voci!
            itemView.setLayoutParams(itemLp);
        }

        // 2. Imposta PRIMA il background
        if (isSelected) {
            android.graphics.drawable.GradientDrawable pill = new android.graphics.drawable.GradientDrawable();
            pill.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
            pill.setColor(0xFFFF7F02); // Arancione Infuse
            pill.setCornerRadius(10 * density);

            // Inset inferiore e superiore a 2dp per avvicinare le voci!
            android.graphics.drawable.InsetDrawable pillInset = new android.graphics.drawable.InsetDrawable(
                    pill, (int) (28 * density), (int) (2 * density), (int) (24 * density), (int) (2 * density));
            itemView.setBackground(pillInset);
        } else {
            itemView.setBackground(null);
        }

        // 3. Imposta il padding di itemView DOPO setBackground
        // 48dp da sinistra = 28dp (inizio pillola) + 16dp di margine interno del testo -> allineato al pixel con l'icona "Search"!
        itemView.setPadding((int) (48 * density), 0, (int) (36 * density), 0);

        TextView tv = findHeaderTextView(itemView);
        if (tv != null) {
            tv.setBackground(null);
            tv.setPadding(0, 0, 0, 0);
            tv.setGravity(android.view.Gravity.CENTER_VERTICAL | android.view.Gravity.START);
            tv.setIncludeFontPadding(false);
            tv.setTextColor(isSelected ? 0xFFFFFFFF : 0xFFD1D1D6);
            tv.setTypeface(null, isSelected ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
            tv.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 15);
            if (tv.getLayoutParams() != null) {
                tv.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                tv.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            }
        }
    }

    private void lockScaleToOneRecursive(View v) {
        if (v == null) return;
        if (v.getScaleX() != 1.0f) v.setScaleX(1.0f);
        if (v.getScaleY() != 1.0f) v.setScaleY(1.0f);
        if (v.getAlpha() != 1.0f) v.setAlpha(1.0f);
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            vg.setClipChildren(false);
            vg.setClipToPadding(false);
            for (int i = 0; i < vg.getChildCount(); i++) {
                lockScaleToOneRecursive(vg.getChildAt(i));
            }
        }
    }

    private TextView findHeaderTextView(View v) {
        if (v instanceof TextView) return (TextView) v;
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                TextView found = findHeaderTextView(vg.getChildAt(i));
                if (found != null) return found;
            }
        }
        return null;
    }
    // END PULCIX CUSTOM THEME

    @Override
    public void onDestroyView() {
        mOverlay.destroy();
        // Unregister theme change listener
        if (mThemeChangeListener != null) {
            ThemeManager.getInstance(getActivity()).unregisterThemeChangeListener(mThemeChangeListener);
        }
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        mOverlay.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mOverlay.pause();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSeparateAnimeFromShowMovie = mPrefs.getBoolean(VideoPreferencesCommon.KEY_SEPARATE_ANIME_MOVIE_SHOW, VideoPreferencesCommon.SEPARATE_ANIME_MOVIE_SHOW_DEFAULT);
        if (log.isDebugEnabled()) log.debug("onActivityCreated: mSeparateAnimeFromShowMovie={}", mSeparateAnimeFromShowMovie);

        mSortOrder = mPrefs.getString(getSortOrderParamKey(), mDefaultSort);

        Resources r = getResources();
        updateBackground();

        // BEGIN PULCIX CUSTOM THEME
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
        // END PULCIX CUSTOM THEME

        // Apply theme-aware colors
        ThemeManager themeManager = ThemeManager.getInstance(getActivity());
        // set fastLane (or headers) background color based on theme
        setBrandColor(themeManager.getLeanbackHeaderColor());

        // set search icon color
        setSearchAffordanceColor(ThemeManager.getInstance(getActivity()).getSearchAffordanceColor());

        setupEventListeners();

        // BEGIN PULCIX CUSTOM THEME
        boolean isInfuse = ThemeManager.getInstance(getActivity()).isInfuseTheme();
        RowPresenter rowPresenter;
        if (isInfuse) {
            CustomDpadListRowPresenter dpadPresenter = new CustomDpadListRowPresenter();
            if (getActivity() != null) {
                dpadPresenter.setOnItemViewClickedListener(new VideoViewClickedListener(getActivity()));
            }
            dpadPresenter.setHeaderPresenter(new RowHeaderPresenter());
            rowPresenter = dpadPresenter;
        } else {
            ListRowPresenter standardPresenter = new ListRowPresenter();
            standardPresenter.setHeaderPresenter(new RowHeaderPresenter());
            rowPresenter = standardPresenter;
        }
        // END PULCIX CUSTOM THEME
        mRowsAdapter = new ArrayObjectAdapter(rowPresenter);
        setAdapter(mRowsAdapter);

        mTvshowPresenter = new PosterImageCardPresenter(getActivity());
        mTvshowMapper = new CompatibleCursorMapperConverter(new TvshowCursorMapper());

        LoaderManager.getInstance(this).initLoader(-1, null, this);

        // Setup theme change listener
        setupThemeListener();
    }

    private void setupEventListeners() {
        setOnSearchClickedListener(new View.OnClickListener() {
            public void onClick(View view) {
                mSortOrderItem = sortOrder2Item(mSortOrder);
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (mSortOrderItem != which) {
                            mSortOrderItem = which;
                            mSortOrder = item2SortOrder(mSortOrderItem);
                            // Save the sort mode
                            mPrefs.edit().putString(getSortOrderParamKey(), mSortOrder).commit();
                            loadCategoriesRows(mCurrentCategoriesCursor);
                        }
                        dialog.dismiss();
                    }
                };

                if (ThemeManager.getInstance(getActivity()).isInfuseTheme()) {
                    com.archos.mediacenter.video.utils.InfuseDialogHelper.showInfuseSingleChoiceDialog(getActivity(), null, getSortOrderEntries(), mSortOrderItem, listener);
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setSingleChoiceItems(getSortOrderEntries(), mSortOrderItem, listener)
                            .create().show();
                }
            }
        });
        setOnItemViewClickedListener(new VideoViewClickedListener(getActivity()));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == -1) {
            // List of categories
            return getSubsetLoader(getActivity());
        } else {
            // One of the row
            return new TvshowsSelectionLoader(getActivity(), args.getString("ids"), args.getString("sort"));
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor c) {
        if (getActivity() == null) return;
        // List of categories
        if (cursorLoader.getId() == -1) {

            // Empty view visibility
            mEmptyView.setVisibility(c.getCount() > 0 ? View.GONE : View.VISIBLE);

            if (mCurrentCategoriesCursor != null) {
                if (!isCategoriesListModified(mCurrentCategoriesCursor, c)) {
                    // no actual modification, no need to rebuild all the rows
                    mCurrentCategoriesCursor = c; // keep the reference to the new cursor because the old one won't be valid anymore
                    return;
                }
            }
            mCurrentCategoriesCursor = c;
            loadCategoriesRows(c);
        }
        // One of the row
        else {
            CursorObjectAdapter adapter = mAdaptersMap.get(cursorLoader.getId());
            if (adapter != null) {
                adapter.changeCursor(c);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }


    private boolean isCategoriesListModified(Cursor oldCursor, Cursor newCursor) {

        // Modified for sure if has different length
        if (oldCursor.getCount() != newCursor.getCount()) {
            if (log.isDebugEnabled()) log.debug("Difference found in the category list (size changed)");
            return true;
        }

        // these two column index are the same but it looks nicer like this :-)
        final int oldSubsetNameColumn = oldCursor.getColumnIndex(TvshowsByAlphaLoader.COLUMN_SUBSET_NAME);
        final int newSubsetNameColumn = newCursor.getColumnIndex(TvshowsByAlphaLoader.COLUMN_SUBSET_NAME);

        // Check all names
        oldCursor.moveToFirst();
        newCursor.moveToFirst();
        while (!oldCursor.isAfterLast() && !newCursor.isAfterLast()) {
            final String oldName = oldCursor.getString(oldSubsetNameColumn);
            final String newName = newCursor.getString(newSubsetNameColumn);
            if (oldName != null && !oldName.equals(newName)) {
                // difference found
                if (log.isDebugEnabled()) log.debug("Difference found in the category list ({} vs {})", oldName, newName);
                return true;
            }
            oldCursor.moveToNext();
            newCursor.moveToNext();
        }
        // no difference found
        if (log.isDebugEnabled()) log.debug("No difference found in the category list");
        return false;
    }

    private void loadCategoriesRows(Cursor c) {
        if (c == null) return;
        int subsetIdColumn = c.getColumnIndex(TvshowsByAlphaLoader.COLUMN_SUBSET_ID);
        int subsetNameColumn = c.getColumnIndex(TvshowsByAlphaLoader.COLUMN_SUBSET_NAME);
        int listOfTvshowIdsColumn = c.getColumnIndex(TvshowsByAlphaLoader.COLUMN_LIST_OF_TVSHOWS_IDS);

        mRowsAdapter.clear();
        mAdaptersMap.clear();

        // NOTE: A first version was using a CursorObjectAdapter for the rows.
        // The problem was that when any DB update occurred (resume point...) I found no way
        // to not update all the rows. Hence the selection position on the current row was lost.
        // I tried to not update but the older cursor was closed by the LoaderManager (I think), leading to crashes.
        // Solution implemented here is to "convert" the cursor into an array. No performance issue since the
        // number of categories is always quite limited (~100 max)

        // Build the array of categories from the cursor
        ArrayList<ListRow> rows = new ArrayList<>(c.getCount());
        c.moveToFirst();
        while(!c.isAfterLast())
        {
            int subsetId = (int) c.getLong(subsetIdColumn);
            String subsetName = c.getString(subsetNameColumn);
            String listOfTvshowIds = c.getString(listOfTvshowIdsColumn);

            // Build the row
            CursorObjectAdapter subsetAdapter = new CursorObjectAdapter(mTvshowPresenter);
            subsetAdapter.setMapper(mTvshowMapper);
            rows.add(new ListRow(subsetId, new HeaderItem(subsetName), subsetAdapter));
            mAdaptersMap.append(subsetId, subsetAdapter);

            // Start the loader manager for this row
            Bundle args = new Bundle();
            args.putString("ids", listOfTvshowIds);
            args.putString("sort", mSortOrder);
            // cf. https://github.com/nova-video-player/aos-AVP/issues/141
            try {
                LoaderManager.getInstance(this).restartLoader(subsetId, args, this);
            } catch (Exception e) {
                log.warn("caught exception in loadCategoriesRows ",e);
            }

            c.moveToNext();
        }

        mRowsAdapter.addAll(0,rows);
    }

    private void updateBackground() {
        // Update private mode indicator visibility
        PrivateModeUIHelper.updatePrivateModeIndicator(getView());

        bgMngr = BackgroundManager.getInstance(getActivity());
        if(!bgMngr.isAttached())
            bgMngr.attach(getActivity().getWindow());

        if (PrivateMode.isActive()) {
            int privateModeColor = ThemeManager.getInstance(getActivity()).getPrivateModeColor();
            bgMngr.setColor(privateModeColor);
            bgMngr.setDrawable(new ColorDrawable(privateModeColor));
        } else {
            // Use ThemeManager to get the appropriate background color for the current theme
            int backgroundColor = ThemeManager.getInstance(getActivity()).getLeanbackBackgroundColor();
            bgMngr.setColor(backgroundColor);
            bgMngr.setDrawable(new ColorDrawable(backgroundColor));
        }
    }

    private void setupThemeListener() {
        // Guard against duplicate registration
        if (mThemeChangeListener != null) {
            return;
        }
        ThemeManager themeManager = ThemeManager.getInstance(getActivity());
        mThemeChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (VideoPreferencesCommon.KEY_APP_THEME.equals(key)) {
                    if (log.isDebugEnabled()) log.debug("Theme changed, updating background and colors");
                    updateBackground();
                    // Update brand color
                    setBrandColor(themeManager.getLeanbackHeaderColor());
                }
            }
        };
        themeManager.registerThemeChangeListener(mThemeChangeListener);
    }

}
