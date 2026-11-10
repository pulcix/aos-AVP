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


    public interface OnBindInfuseViewListener {
        void onBindInfuseView(View rowView);
    }

    private final Presenter mDetailsPresenter;
    private ViewHolder mViewHolder;
    private boolean mHideActions;
    private OnBindInfuseViewListener mOnBindInfuseViewListener;

    public void setOnBindInfuseViewListener(OnBindInfuseViewListener listener) {
        mOnBindInfuseViewListener = listener;
    }

    public View getOverviewRowView() {
        return mViewHolder != null ? mViewHolder.view : null;
    }

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
            if (mOnBindInfuseViewListener != null) {
                mOnBindInfuseViewListener.onBindInfuseView(holder.view);
                holder.view.post(() -> {
                    if (mOnBindInfuseViewListener != null) {
                        mOnBindInfuseViewListener.onBindInfuseView(holder.view);
                    }
                });
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
                    float density = res.getDisplayMetrics().density;
                    int topSpace = (int) (56 * density);
                    int bottomSpace = (int) (16 * density);
                    frame.setPadding(frame.getPaddingLeft(), topSpace, frame.getPaddingRight(), bottomSpace);
                    frame.setMinimumHeight(0);
                    if (frame.getLayoutParams() != null) {
                        frame.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                        frame.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        if (frame.getLayoutParams() instanceof MarginLayoutParams) {
                            ((MarginLayoutParams) frame.getLayoutParams()).leftMargin = 0;
                            ((MarginLayoutParams) frame.getLayoutParams()).rightMargin = 0;
                            ((MarginLayoutParams) frame.getLayoutParams()).topMargin = 0;
                        }
                    }
                    if (frame instanceof ViewGroup && ((ViewGroup) frame).getChildCount() > 0) {
                        View relativeLayout = ((ViewGroup) frame).getChildAt(0);
                        if (relativeLayout != null) {
                            relativeLayout.setPadding(0, 0, 0, 0);
                            if (relativeLayout.getLayoutParams() != null) {
                                relativeLayout.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            }
                        }
                    }
                }

                if (description != null) {
                    description.setPadding(0, 0, 0, 0);
                    if (description.getLayoutParams() != null) {
                        description.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    }
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
            frame.setMinimumHeight(0);
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
            if (actionsBg.getLayoutParams() != null) {
                actionsBg.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
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
        View infuseContainer = actionsBg != null ? actionsBg.findViewById(R.id.infuse_actions_container) : null;
        View iconRow = infuseContainer != null ? infuseContainer.findViewById(R.id.infuse_icon_actions) : null;
        boolean hasInfuseActions = iconRow instanceof ViewGroup && ((ViewGroup) iconRow).getChildCount() > 0;
        boolean hasNativeActions = actionsGrid != null && actionsGrid.getAdapter() != null && actionsGrid.getAdapter().getItemCount() > 0;

        if (!hasInfuseActions && !hasNativeActions) {
            if (actionsBg != null) {
                actionsBg.setVisibility(View.GONE);
                if (actionsBg.getLayoutParams() != null) {
                    actionsBg.getLayoutParams().height = 0;
                }
                if (actionsBg.getLayoutParams() instanceof MarginLayoutParams) {
                    MarginLayoutParams mlp = (MarginLayoutParams) actionsBg.getLayoutParams();
                    mlp.topMargin = 0;
                    mlp.bottomMargin = 0;
                    actionsBg.setLayoutParams(mlp);
                }
            }
            if (infuseContainer != null) {
                infuseContainer.setVisibility(View.GONE);
                if (infuseContainer.getLayoutParams() != null) {
                    infuseContainer.getLayoutParams().height = 0;
                }
                if (infuseContainer.getLayoutParams() instanceof MarginLayoutParams) {
                    MarginLayoutParams mlp = (MarginLayoutParams) infuseContainer.getLayoutParams();
                    mlp.topMargin = 0;
                    mlp.bottomMargin = 0;
                    infuseContainer.setLayoutParams(mlp);
                }
            }
        } else {
            if (actionsBg != null && actionsBg.getLayoutParams() != null) {
                actionsBg.setVisibility(View.VISIBLE);
                actionsBg.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                actionsBg.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            if (infuseContainer != null && infuseContainer.getLayoutParams() != null) {
                infuseContainer.setVisibility(View.VISIBLE);
                infuseContainer.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                infuseContainer.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                infuseContainer.setPadding((int) (6 * density), 0, 0, 0);
                if (infuseContainer.getLayoutParams() instanceof MarginLayoutParams) {
                    MarginLayoutParams lp = (MarginLayoutParams) infuseContainer.getLayoutParams();
                    lp.setMarginStart(0);
                    lp.leftMargin = 0;
                    lp.setMarginEnd(0);
                    lp.rightMargin = 0;
                    infuseContainer.setLayoutParams(lp);
                }
                if (iconRow != null && iconRow.getLayoutParams() != null) {
                    iconRow.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                }
                View primaryRow = infuseContainer.findViewById(R.id.infuse_primary_actions);
                if (primaryRow != null && primaryRow.getLayoutParams() != null) {
                    primaryRow.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                }
            }
        }
    }
}

    // END PULCIX CUSTOM THEME
}
