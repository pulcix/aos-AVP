// Copyright 2017 Archos SA
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

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.DetailsOverviewRow;
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import androidx.leanback.widget.HorizontalGridView;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.RowPresenter;

import com.archos.mediacenter.video.R;

/**
 * Created by alexandre on 20/02/17.
 */

public class ArchosDetailsOverviewRowPresenter extends FullWidthDetailsOverviewRowPresenter {


    private final Presenter mDetailsPresenter;
    private ViewHolder mViewHolder;
    private boolean mHideActions;

    /**
     * Constructor for a DetailsOverviewRowPresenter.
     *
     * @param detailsPresenter The {@link Presenter} used to render the detailed
     *                         description of the row.
     */
    public ArchosDetailsOverviewRowPresenter(Presenter detailsPresenter) {
        this(detailsPresenter, false);
    }

    public ArchosDetailsOverviewRowPresenter(Presenter detailsPresenter, boolean hideActions) {
        super(detailsPresenter);
        mDetailsPresenter = detailsPresenter;
        mHideActions = hideActions;
    }

    @Override
    protected void onBindRowViewHolder(RowPresenter.ViewHolder holder, Object item) {
        super.onBindRowViewHolder(holder, item);

        mViewHolder = (ViewHolder)holder;
        
        updateBackgroundColor(getBackgroundColor());
        updateActionsBackgroundColor(getActionsBackgroundColor());

        // BEGIN PULCIX CUSTOM THEME
        if (com.archos.mediacenter.video.utils.ThemeManager.getInstance(holder.view.getContext()).isInfuseTheme()) {
            setFacet(androidx.leanback.widget.ItemAlignmentFacet.class, null);
            applyInfuseZeroMargins(mViewHolder);
            if (item instanceof DetailsOverviewRow) {
                DetailsOverviewRow row = (DetailsOverviewRow) item;
                bindInfuseActions(mViewHolder, row.getActionsAdapter());
            }
        }
        // END PULCIX CUSTOM THEME
    }

    @Override
    protected void onLayoutLogo(ViewHolder viewHolder, int oldState, boolean logoChanged) {
        View v = viewHolder.getLogoViewHolder().view;
        // BEGIN PULCIX CUSTOM THEME
        if (com.archos.mediacenter.video.utils.ThemeManager.getInstance(v.getContext()).isInfuseTheme()) {
            v.setVisibility(View.GONE);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            lp.width = 0;
            lp.setMarginStart(0);
            lp.topMargin = 0;
            v.setLayoutParams(lp);
            return;
        }
        // END PULCIX CUSTOM THEME

        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)
                v.getLayoutParams();
        switch (getAlignmentMode()) {
            case ALIGN_MODE_START:
            default:
                lp.setMarginStart(v.getResources().getDimensionPixelSize(
                        androidx.leanback.R.dimen.lb_details_v2_logo_margin_start));
                break;
            case ALIGN_MODE_MIDDLE:
                lp.setMarginStart(v.getResources().getDimensionPixelSize(androidx.leanback.R.dimen.lb_details_v2_left)
                        - lp.width);
                break;
        }
        lp.topMargin = v.getResources().getDimensionPixelSize(
                    androidx.leanback.R.dimen.lb_details_v2_blank_height) + v
                    .getResources().getDimensionPixelSize(
                    androidx.leanback.R.dimen.lb_details_v2_description_margin_top);
        v.setLayoutParams(lp);
    }

    @Override
    protected void onLayoutOverviewFrame(ViewHolder viewHolder, int oldState, boolean logoChanged) {
        boolean wasBanner = oldState == STATE_SMALL;
        boolean isBanner = viewHolder.getState() == STATE_SMALL;
        if (wasBanner != isBanner || logoChanged) {
            Resources res = viewHolder.view.getResources();
            int frameMarginStart;
            int descriptionMarginStart = 0;
            int logoWidth = viewHolder.getLogoViewHolder().view.getLayoutParams().width;
            
            // BEGIN PULCIX CUSTOM THEME
            boolean isInfuse = com.archos.mediacenter.video.utils.ThemeManager.getInstance(viewHolder.view.getContext()).isInfuseTheme();
            if (isInfuse) {
                descriptionMarginStart = 0; // Padding is handled by details_root
                frameMarginStart = 0;
            } else {
            // END PULCIX CUSTOM THEME
            switch (getAlignmentMode()) {
                case ALIGN_MODE_START:
                default:
                    if (isBanner) {
                        frameMarginStart = res.getDimensionPixelSize(
                                androidx.leanback.R.dimen.lb_details_v2_logo_margin_start);
                        descriptionMarginStart = logoWidth;
                    } else {
                        frameMarginStart = 0;
                        descriptionMarginStart = logoWidth + res.getDimensionPixelSize(
                                androidx.leanback.R.dimen.lb_details_v2_logo_margin_start);
                    }
                    break;
                case ALIGN_MODE_MIDDLE:
                    if (isBanner) {
                        frameMarginStart = res.getDimensionPixelSize(androidx.leanback.R.dimen.lb_details_v2_left)
                                - logoWidth;
                        descriptionMarginStart = logoWidth;
                    } else {
                        frameMarginStart = 0;
                        descriptionMarginStart = res.getDimensionPixelSize(
                                androidx.leanback.R.dimen.lb_details_v2_left);
                    }
                    break;
            }
            } // PULCIX CUSTOM THEME
            MarginLayoutParams lpRoot =
                    (MarginLayoutParams) ((ViewGroup)viewHolder.getOverviewView().getParent()).getLayoutParams();
            // BEGIN PULCIX CUSTOM THEME
            if (isInfuse) {
                lpRoot.width = ViewGroup.LayoutParams.MATCH_PARENT;
                lpRoot.leftMargin = 0;
                lpRoot.rightMargin = 0;
            } else {
                lpRoot.leftMargin = res.getDimensionPixelSize(androidx.leanback.R.dimen.lb_details_overview_margin_start);
                lpRoot.rightMargin = res.getDimensionPixelSize(androidx.leanback.R.dimen.lb_details_overview_margin_end);
            }
            // END PULCIX CUSTOM THEME

            MarginLayoutParams lpFrame =
                    (MarginLayoutParams) viewHolder.getOverviewView().getLayoutParams();
            lpFrame.topMargin = isBanner ? 0
                    : res.getDimensionPixelSize(androidx.leanback.R.dimen.lb_details_v2_blank_height);
            // BEGIN PULCIX CUSTOM THEME
            if (isInfuse) {

                lpFrame.topMargin = 0;
                lpFrame.width = ViewGroup.LayoutParams.MATCH_PARENT;
                lpFrame.leftMargin = 0;
                lpFrame.rightMargin = 0;
                int pad48 = (int) (48 * res.getDisplayMetrics().density);
                viewHolder.view.setPadding(pad48, 0, pad48, viewHolder.view.getPaddingBottom());
            } else {
                lpFrame.leftMargin = lpFrame.rightMargin = frameMarginStart;
            }
            // END PULCIX CUSTOM THEME
            if (mHideActions) {
                lpFrame.height = res.getDimensionPixelSize(androidx.leanback.R.dimen.lb_details_v2_card_height)
                        - res.getDimensionPixelSize(androidx.leanback.R.dimen.lb_details_v2_actions_height);
            }
            viewHolder.getOverviewView().setLayoutParams(lpFrame);
            View description = viewHolder.getDetailsDescriptionFrame();
            MarginLayoutParams lpDesc = (MarginLayoutParams) description.getLayoutParams();
            lpDesc.setMarginStart(descriptionMarginStart);
            description.setLayoutParams(lpDesc);
            View action = viewHolder.getActionsRow();
            MarginLayoutParams lpActions = (MarginLayoutParams) action.getLayoutParams();
            // BEGIN PULCIX CUSTOM THEME
            if (isInfuse) {
                lpActions.setMarginStart(0);
                lpActions.setMarginEnd(0);
            } else {
                lpActions.setMarginStart(res.getDimensionPixelSize(androidx.leanback.R.dimen.lb_details_v2_logo_margin_start));
                lpActions.setMarginEnd(res.getDimensionPixelSize(androidx.leanback.R.dimen.lb_details_v2_description_margin_end));
            }
            // END PULCIX CUSTOM THEME
            lpActions.height =
                    isBanner ? 0 : res.getDimensionPixelSize(androidx.leanback.R.dimen.lb_details_v2_actions_height);
            if (mHideActions) {
                lpActions.height = 0;
            } else {
                action.setVisibility(View.VISIBLE);
            }
            action.setLayoutParams(lpActions);

            // BEGIN PULCIX CUSTOM THEME
            if (isInfuse) {
                action.setVisibility(View.GONE);
                lpActions.height = 0;
                action.setLayoutParams(lpActions);
                
                View frame = viewHolder.getOverviewView();
                if (frame != null) {
                    frame.setClipToOutline(false);
                    frame.setOutlineProvider(null);
                    frame.setPadding(0, 0, 0, 0);
                    frame.setMinimumHeight((int) (320 * res.getDisplayMetrics().density));
                    if (frame.getLayoutParams() != null) {
                        frame.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                        frame.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        if (frame.getLayoutParams() instanceof MarginLayoutParams) {
                            ((MarginLayoutParams) frame.getLayoutParams()).leftMargin = 0;
                            ((MarginLayoutParams) frame.getLayoutParams()).rightMargin = 0;
                        }
                    }
                    if (frame instanceof ViewGroup && ((ViewGroup) frame).getChildCount() > 0) {
                        View relativeLayout = ((ViewGroup) frame).getChildAt(0);
                        if (relativeLayout != null) {
                            relativeLayout.setPadding(0, 0, 0, 0);
                        }
                    }
                }

                if (description != null) {
                    description.setPadding(0, 0, 0, 0);
                    if (description.getLayoutParams() instanceof MarginLayoutParams) {
                        MarginLayoutParams lp = (MarginLayoutParams) description.getLayoutParams();
                        lp.setMarginStart(0);
                        lp.leftMargin = 0;
                        description.setLayoutParams(lp);
                    }
                }

                View actionsBg = frame != null ? frame.findViewById(androidx.leanback.R.id.details_overview_actions_background) : null;
                if (actionsBg != null) {
                    actionsBg.setPadding(0, 0, 0, 0);
                    if (actionsBg.getLayoutParams() instanceof android.widget.RelativeLayout.LayoutParams) {
                        android.widget.RelativeLayout.LayoutParams rlp = (android.widget.RelativeLayout.LayoutParams) actionsBg.getLayoutParams();
                        rlp.removeRule(android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM);
                        rlp.addRule(android.widget.RelativeLayout.BELOW, androidx.leanback.R.id.details_overview_description);
                        rlp.topMargin = (int) (18 * res.getDisplayMetrics().density);
                        rlp.setMarginStart(0);
                        rlp.leftMargin = 0;
                        rlp.setMarginEnd(0);
                        rlp.rightMargin = 0;
                        actionsBg.setLayoutParams(rlp);
                    } else if (actionsBg.getLayoutParams() instanceof MarginLayoutParams) {
                        MarginLayoutParams lp = (MarginLayoutParams) actionsBg.getLayoutParams();
                        lp.setMarginStart(0);
                        lp.leftMargin = 0;
                        lp.setMarginEnd(0);
                        lp.rightMargin = 0;
                        actionsBg.setLayoutParams(lp);
                    }
                    View infuseContainer = actionsBg.findViewById(R.id.infuse_actions_container);
                    if (infuseContainer != null) {
                        infuseContainer.setPadding(0, 0, 0, 0);
                        if (infuseContainer.getLayoutParams() instanceof MarginLayoutParams) {
                            MarginLayoutParams lp = (MarginLayoutParams) infuseContainer.getLayoutParams();
                            lp.setMarginStart(0);
                            lp.leftMargin = 0;
                            lp.setMarginEnd(0);
                            lp.rightMargin = 0;
                            infuseContainer.setLayoutParams(lp);
                        }
                    }
                }

                if (viewHolder.view instanceof ViewGroup) {
                    disableClipping((ViewGroup) viewHolder.view);
                }
            }
            // END PULCIX CUSTOM THEME
        }
    }

    // BEGIN PULCIX CUSTOM THEME
    private void disableClipping(ViewGroup vg) {
        if (vg == null) return;
        vg.setClipChildren(false);
        vg.setClipToPadding(false);
        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            if (child instanceof ViewGroup) {
                disableClipping((ViewGroup) child);
            }
        }
    }
    // END PULCIX CUSTOM THEME

    @Override
    protected int getLayoutResourceId() {
        return R.layout.leanback_details_overview;
    }

    public void updateBackgroundColor(int color) {
        setBackgroundColor(color);

        if (mViewHolder != null) {
            if (com.archos.mediacenter.video.utils.ThemeManager.getInstance(mViewHolder.view.getContext()).isInfuseTheme()) {
                mViewHolder.getOverviewView().setBackgroundColor(android.graphics.Color.TRANSPARENT);
                mViewHolder.getOverviewView().setElevation(0f);
            } else {
                mViewHolder.getOverviewView().setBackgroundColor(color);
            }
        }
    }

    public void updateActionsBackgroundColor(int color) {
        setActionsBackgroundColor(color);

        if (mViewHolder != null) {
            View actionsBg = mViewHolder.getOverviewView().findViewById(androidx.leanback.R.id.details_overview_actions_background);
            if (actionsBg != null) {
                if (com.archos.mediacenter.video.utils.ThemeManager.getInstance(mViewHolder.view.getContext()).isInfuseTheme()) {
                    actionsBg.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                } else {
                    actionsBg.setBackgroundColor(color);
                }
            }
        }
    }

    public void moveSelectedPosition(int offset) {
        if (mViewHolder != null) {
            int position = ((HorizontalGridView)mViewHolder.getActionsRow()).getSelectedPosition();

            ((HorizontalGridView)mViewHolder.getActionsRow()).setSelectedPosition(position + offset);
        }
    }

    public int getState() {
        return mViewHolder.getState();
    }

    // BEGIN PULCIX CUSTOM THEME
    private void applyInfuseZeroMargins(ViewHolder viewHolder) {
        if (viewHolder == null || viewHolder.view == null) return;
        float density = viewHolder.view.getResources().getDisplayMetrics().density;
        int pad48 = (int) (48 * density);
        int padLeft = (int) (42 * density);

        if (viewHolder.view instanceof ViewGroup) {
            disableClipping((ViewGroup) viewHolder.view);
        }

        // 1. Details root has 48dp left and right padding
        viewHolder.view.setPadding(padLeft, 0, pad48, viewHolder.view.getPaddingBottom());
        if (viewHolder.view.getLayoutParams() != null) {
            viewHolder.view.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            if (viewHolder.view.getLayoutParams() instanceof MarginLayoutParams) {
                ((MarginLayoutParams) viewHolder.view.getLayoutParams()).leftMargin = 0;
                ((MarginLayoutParams) viewHolder.view.getLayoutParams()).rightMargin = 0;
            }
        }

        // 2. Details frame
        View frame = viewHolder.getOverviewView();
        if (frame != null) {
            frame.setClipToOutline(false);
            frame.setOutlineProvider(null);
            frame.setPadding(0, 0, 0, 0);
            frame.setMinimumHeight((int) (320 * density));
            if (frame.getLayoutParams() != null) {
                frame.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                frame.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                if (frame.getLayoutParams() instanceof MarginLayoutParams) {
                    ((MarginLayoutParams) frame.getLayoutParams()).topMargin = 0;
                    ((MarginLayoutParams) frame.getLayoutParams()).leftMargin = 0;
                    ((MarginLayoutParams) frame.getLayoutParams()).rightMargin = 0;
                }
            }
            if (frame instanceof ViewGroup && ((ViewGroup) frame).getChildCount() > 0) {
                View relativeLayout = ((ViewGroup) frame).getChildAt(0);
                if (relativeLayout != null) {
                    relativeLayout.setPadding(0, 0, 0, 0);
                }
            }
        }

        // 3. Description frame
        View description = viewHolder.getDetailsDescriptionFrame();
        if (description != null) {
            description.setPadding((int) (6 * density), 0, 0, 0);
            if (description.getLayoutParams() instanceof MarginLayoutParams) {
                MarginLayoutParams lp = (MarginLayoutParams) description.getLayoutParams();
                lp.setMarginStart(0);
                lp.leftMargin = 0;
                description.setLayoutParams(lp);
            }
        }

        // 4. Actions background and grid
        View actionsBg = frame != null ? frame.findViewById(androidx.leanback.R.id.details_overview_actions_background) : null;
        if (actionsBg != null) {
            actionsBg.setPadding(0, 0, 0, 0);
            if (actionsBg.getLayoutParams() instanceof android.widget.RelativeLayout.LayoutParams) {
                android.widget.RelativeLayout.LayoutParams rlp = (android.widget.RelativeLayout.LayoutParams) actionsBg.getLayoutParams();
                rlp.removeRule(android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM);
                rlp.addRule(android.widget.RelativeLayout.BELOW, androidx.leanback.R.id.details_overview_description);
                rlp.topMargin = (int) (18 * density);
                rlp.setMarginStart(0);
                rlp.leftMargin = 0;
                rlp.setMarginEnd(0);
                rlp.rightMargin = 0;
                actionsBg.setLayoutParams(rlp);
            } else if (actionsBg.getLayoutParams() instanceof MarginLayoutParams) {
                MarginLayoutParams lp = (MarginLayoutParams) actionsBg.getLayoutParams();
                lp.setMarginStart(0);
                lp.leftMargin = 0;
                lp.setMarginEnd(0);
                lp.rightMargin = 0;
                actionsBg.setLayoutParams(lp);
            }
            HorizontalGridView actionsGrid = actionsBg.findViewById(androidx.leanback.R.id.details_overview_actions);
            if (actionsGrid != null) {
                int pad6 = (int) (6 * density);
                actionsGrid.setPadding(pad6, pad6, pad6, pad6);
                actionsGrid.setFadingLeftEdge(false);
                actionsGrid.setFadingRightEdge(false);
                actionsGrid.setClipToPadding(false);
                actionsGrid.setClipChildren(false);
                actionsGrid.setWindowAlignment(HorizontalGridView.WINDOW_ALIGN_NO_EDGE);
                actionsGrid.setWindowAlignmentOffset(0);
                actionsGrid.setWindowAlignmentOffsetPercent(HorizontalGridView.WINDOW_ALIGN_OFFSET_PERCENT_DISABLED);
                actionsGrid.setItemAlignmentOffset(0);
                actionsGrid.setItemAlignmentOffsetPercent(HorizontalGridView.ITEM_ALIGN_OFFSET_PERCENT_DISABLED);
                if (actionsGrid.getLayoutParams() instanceof MarginLayoutParams) {
                    MarginLayoutParams lp = (MarginLayoutParams) actionsGrid.getLayoutParams();
                    lp.setMarginStart(0);
                    lp.leftMargin = 0;
                    lp.setMarginEnd(0);
                    lp.rightMargin = 0;
                    actionsGrid.setLayoutParams(lp);
                }
            }
            View infuseContainer = actionsBg.findViewById(R.id.infuse_actions_container);
            if (infuseContainer != null) {
                infuseContainer.setPadding((int) (6 * density), 0, 0, 0);
                if (infuseContainer.getLayoutParams() instanceof MarginLayoutParams) {
                    MarginLayoutParams lp = (MarginLayoutParams) infuseContainer.getLayoutParams();
                    lp.setMarginStart(0);
                    lp.leftMargin = 0;
                    lp.setMarginEnd(0);
                    lp.rightMargin = 0;
                    infuseContainer.setLayoutParams(lp);
                }
            }
        }
    }

    private void bindInfuseActions(ViewHolder viewHolder, ObjectAdapter adapter) {
        if (viewHolder == null || viewHolder.view == null) return;

        View nativeGrid = viewHolder.view.findViewById(androidx.leanback.R.id.details_overview_actions);
        if (nativeGrid != null) {
            nativeGrid.setVisibility(View.GONE);
        }

        View infuseContainer = viewHolder.view.findViewById(R.id.infuse_actions_container);
        if (infuseContainer == null) return;

        infuseContainer.setVisibility(View.VISIBLE);

        ViewGroup iconRow = infuseContainer.findViewById(R.id.infuse_icon_actions);
        ViewGroup primaryRow = infuseContainer.findViewById(R.id.infuse_primary_actions);

        if (iconRow == null || primaryRow == null) return;
        if (iconRow.getChildCount() > 0) return; // Managed by VideoDetailsFragment

        iconRow.removeAllViews();
        primaryRow.removeAllViews();

        if (adapter == null) return;

        Context context = viewHolder.view.getContext();
        float density = context.getResources().getDisplayMetrics().density;

        int iconWidth = (int) (54 * density);
        int iconHeight = (int) (38 * density);
        int iconMarginEnd = (int) (14 * density);

        int primaryWidth = (int) (200 * density);
        int primaryHeight = (int) (46 * density);
        int primaryMarginEnd = (int) (12 * density);

        LayoutInflater inflater = LayoutInflater.from(context);

        View firstPrimaryView = null;
        View firstIconView = null;

        for (int i = 0; i < adapter.size(); i++) {
            Object item = adapter.get(i);
            if (item instanceof Action) {
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
                    MarginLayoutParams lp = new MarginLayoutParams(primaryWidth, primaryHeight);
                    lp.setMarginEnd(primaryMarginEnd);
                    v.setLayoutParams(lp);

                    TextView tv = v.findViewById(R.id.text);
                    if (tv != null) tv.setText(action.getLabel1());
                    v.setTag(action);
                    v.setFocusable(true);
                    v.setFocusableInTouchMode(false);
                    v.setOnClickListener(v1 -> {
                        if (getOnActionClickedListener() != null) {
                            getOnActionClickedListener().onActionClicked(action);
                        }
                    });
                    v.setOnFocusChangeListener((view, hasFocus) -> {
                        if (hasFocus) {
                            view.animate().scaleX(1.05f).scaleY(1.05f).setDuration(150).start();
                        } else {
                            view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start();
                        }
                    });
                    primaryRow.addView(v);
                    if (firstPrimaryView == null) {
                        firstPrimaryView = v;
                    }
                } else {
                    View v = inflater.inflate(R.layout.infuse_action_icon_pill, iconRow, false);
                    MarginLayoutParams lp = new MarginLayoutParams(iconWidth, iconHeight);
                    lp.setMarginEnd(iconMarginEnd);
                    v.setLayoutParams(lp);

                    ImageView iv = v.findViewById(R.id.icon);
                    if (iv != null) {
                        if (id == VideoActionAdapter.ACTION_MARK_AS_WATCHED) {
                            iv.setImageResource(R.drawable.trakt_watched);
                        } else if (id == VideoActionAdapter.ACTION_MARK_AS_NOT_WATCHED) {
                            iv.setImageResource(R.drawable.ic_eye_outline);
                        } else if (id == VideoActionAdapter.ACTION_UNSCRAP) {
                            iv.setImageResource(R.drawable.ic_infuse_unscrap);
                        } else if (id == VideoActionAdapter.ACTION_SCRAP) {
                            iv.setImageResource(R.drawable.ic_infuse_refresh);
                        } else if (id == VideoActionAdapter.ACTION_ADD_TO_LIST) {
                            iv.setImageResource(R.drawable.ic_infuse_bookmark);
                        } else if (id == VideoActionAdapter.ACTION_UNINDEX || id == VideoActionAdapter.ACTION_HIDE || id == VideoActionAdapter.ACTION_REMOVE_FROM_LIST) {
                            iv.setImageResource(R.drawable.ic_infuse_minus_circle);
                        } else if (id == VideoActionAdapter.ACTION_DELETE || id == VideoActionAdapter.ACTION_CONFIRM_DELETE) {
                            iv.setImageResource(R.drawable.ic_infuse_delete);
                        } else if (id == VideoActionAdapter.ACTION_INDEX) {
                            iv.setImageResource(R.drawable.ic_infuse_info);
                        } else {
                            iv.setImageResource(R.drawable.ic_infuse_settings);
                        }
                    }
                    v.setTag(action);
                    v.setFocusable(true);
                    v.setFocusableInTouchMode(false);
                    v.setOnClickListener(v1 -> {
                        if (getOnActionClickedListener() != null) {
                            getOnActionClickedListener().onActionClicked(action);
                        }
                    });
                    v.setOnFocusChangeListener((view, hasFocus) -> {
                        if (hasFocus) {
                            view.animate().scaleX(1.08f).scaleY(1.08f).setDuration(150).start();
                        } else {
                            view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start();
                        }
                    });
                    iconRow.addView(v);
                    if (firstIconView == null) {
                        firstIconView = v;
                    }
                }
            }
        }

        // D-Pad navigation links between rows
        if (firstIconView != null) {
            final int iconId = firstIconView.getId() != View.NO_ID ? firstIconView.getId() : View.generateViewId();
            firstIconView.setId(iconId);
            for (int j = 0; j < primaryRow.getChildCount(); j++) {
                primaryRow.getChildAt(j).setNextFocusUpId(iconId);
            }
        }
        if (firstPrimaryView != null) {
            final int primaryId = firstPrimaryView.getId() != View.NO_ID ? firstPrimaryView.getId() : View.generateViewId();
            firstPrimaryView.setId(primaryId);
            for (int j = 0; j < iconRow.getChildCount(); j++) {
                iconRow.getChildAt(j).setNextFocusDownId(primaryId);
            }
        }

        // Focus first primary button
        if (firstPrimaryView != null) {
            final View toFocus = firstPrimaryView;
            toFocus.post(toFocus::requestFocus);
        }
    }
    // END PULCIX CUSTOM THEME
}
