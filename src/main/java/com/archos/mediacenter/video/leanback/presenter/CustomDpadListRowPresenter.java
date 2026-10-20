// Copyright 2025 Nova Video Player / Pulcix Custom Integration

package com.archos.mediacenter.video.leanback.presenter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowHeaderPresenter;
import androidx.leanback.widget.RowPresenter;
import androidx.recyclerview.widget.RecyclerView;

import com.archos.mediacenter.video.R;
import com.rubensousa.dpadrecyclerview.ChildAlignment;
import com.rubensousa.dpadrecyclerview.DpadRecyclerView;
import com.rubensousa.dpadrecyclerview.OnViewHolderSelectedListener;
import com.rubensousa.dpadrecyclerview.ParentAlignment;

// BEGIN PULCIX CUSTOM THEME
public class CustomDpadListRowPresenter extends RowPresenter {

    private OnItemViewClickedListener mOnItemViewClickedListener;
    private OnItemViewSelectedListener mOnItemViewSelectedListener;

    public static class ViewHolder extends RowPresenter.ViewHolder {
        private final DpadRecyclerView mDpadRecyclerView;
        private final TextView mHeaderTitleView;
        private ItemBridgeAdapter mItemBridgeAdapter;

        public ViewHolder(View view, DpadRecyclerView dpadRecyclerView, TextView headerTitleView) {
            super(view);
            this.mDpadRecyclerView = dpadRecyclerView;
            this.mHeaderTitleView = headerTitleView;
        }

        public DpadRecyclerView getDpadRecyclerView() {
            return mDpadRecyclerView;
        }

        public TextView getHeaderTitleView() {
            return mHeaderTitleView;
        }
    }

    public CustomDpadListRowPresenter() {
        super();
        // Bypass Leanback's native RowHeaderPresenter so custom TextView in custom_dpad_list_row.xml is used
        setHeaderPresenter(null);
    }

    @Override
    protected void initializeRowViewHolder(RowPresenter.ViewHolder vh) {
        super.initializeRowViewHolder(vh);
        if (vh.getHeaderViewHolder() != null && vh.getHeaderViewHolder().view != null) {
            vh.getHeaderViewHolder().view.setVisibility(View.GONE);
        }
    }

    public void setOnItemViewClickedListener(OnItemViewClickedListener listener) {
        this.mOnItemViewClickedListener = listener;
    }

    public OnItemViewClickedListener getOnItemViewClickedListener() {
        return mOnItemViewClickedListener;
    }

    public void setOnItemViewSelectedListener(OnItemViewSelectedListener listener) {
        this.mOnItemViewSelectedListener = listener;
    }

    public OnItemViewSelectedListener getOnItemViewSelectedListener() {
        return mOnItemViewSelectedListener;
    }

    @Override
    protected RowPresenter.ViewHolder createRowViewHolder(ViewGroup parent) {
        View rowView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_dpad_list_row, parent, false);

        // BEGIN PULCIX CUSTOM THEME
        int screenWidth = parent.getResources().getDisplayMetrics().widthPixels;

        // Force full screen width on row root view
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) rowView.getLayoutParams();
        if (lp != null) {
            lp.width = screenWidth;
            lp.bottomMargin = (int) (32 * parent.getResources().getDisplayMetrics().density);
            rowView.setLayoutParams(lp);
        }

        DpadRecyclerView dpadRecyclerView = rowView.findViewById(R.id.custom_dpad_row_content);
        TextView headerTitleView = rowView.findViewById(R.id.custom_row_header_title);

        if (dpadRecyclerView != null) {
            // Force full screen width on DpadRecyclerView
            ViewGroup.LayoutParams rvLp = dpadRecyclerView.getLayoutParams();
            if (rvLp != null) {
                rvLp.width = screenWidth;
                dpadRecyclerView.setLayoutParams(rvLp);
            }

            dpadRecyclerView.setOrientation(RecyclerView.HORIZONTAL);

            // Add horizontal spacing between cards (infuse_row_item_spacing) and edge spacing
            int itemSpacingPx = parent.getResources().getDimensionPixelSize(R.dimen.infuse_row_item_spacing);
            int edgeSpacingPx = parent.getResources().getDimensionPixelSize(R.dimen.infuse_row_edge_spacing);
            if (dpadRecyclerView.getItemDecorationCount() == 0) {
                dpadRecyclerView.addItemDecoration(
                    com.rubensousa.dpadrecyclerview.spacing.DpadLinearSpacingDecoration.create(itemSpacingPx, edgeSpacingPx, 0)
                );
            }

            // Left Magnetic Alignment (Infuse Style)
            ParentAlignment parentAlignment = new ParentAlignment(ParentAlignment.Edge.NONE, edgeSpacingPx, 0.0f, false, false);
            ChildAlignment childAlignment = new ChildAlignment(0, 0.0f, true, false, false);
            dpadRecyclerView.setAlignments(parentAlignment, childAlignment, true);
        }
        // END PULCIX CUSTOM THEME

        return new ViewHolder(rowView, dpadRecyclerView, headerTitleView);
    }

    @Override
    protected void onBindRowViewHolder(RowPresenter.ViewHolder holder, Object item) {
        super.onBindRowViewHolder(holder, item);
        ViewHolder vh = (ViewHolder) holder;

        // Hide native Leanback header view if present
        if (vh.getHeaderViewHolder() != null && vh.getHeaderViewHolder().view != null) {
            vh.getHeaderViewHolder().view.setVisibility(View.GONE);
        }

        // Bind custom row header title from Row.getHeaderItem()
        if (item instanceof Row && ((Row) item).getHeaderItem() != null) {
            String title = ((Row) item).getHeaderItem().getName();
            if (vh.mHeaderTitleView != null) {
                vh.mHeaderTitleView.setText(title);
                vh.mHeaderTitleView.setVisibility(View.VISIBLE);
            }
        } else if (vh.mHeaderTitleView != null) {
            vh.mHeaderTitleView.setVisibility(View.GONE);
        }

        if (item instanceof ListRow) {
            ListRow listRow = (ListRow) item;
            ObjectAdapter listAdapter = listRow.getAdapter();

            if (listAdapter != null) {
                ItemBridgeAdapter bridgeAdapter = new ItemBridgeAdapter(listAdapter);

                bridgeAdapter.setAdapterListener(new ItemBridgeAdapter.AdapterListener() {
                    
                    private void applyInfuseCardStyle(View cardView) {
                        // RIMOZIONE RIMASUGLI GRIGI AGLI ANGOLI
                        cardView.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                        cardView.setBackground(null);

                        View targetView = cardView;

                        if (cardView instanceof androidx.leanback.widget.ImageCardView) {
                            androidx.leanback.widget.ImageCardView imageCardView = (androidx.leanback.widget.ImageCardView) cardView;
                            imageCardView.setInfoAreaBackground(null);
                            imageCardView.setInfoAreaBackgroundColor(android.graphics.Color.TRANSPARENT);
                            if (imageCardView.getMainImageView() != null) {
                                targetView = imageCardView.getMainImageView();
                            }
                        }
                        
                        // RIMOZIONE FASCIA GRIGIA SULLE ALTRE CARD
                        View infoField = cardView.findViewById(androidx.leanback.R.id.info_field);
                        if (infoField != null) {
                            infoField.setVisibility(View.VISIBLE);
                            infoField.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                            infoField.setBackground(null);
                        }

                        // ARROTONDAMENTO (14dp) SULLA TARGET VIEW (es. MainImageView per i poster)
                        targetView.setOutlineProvider(new android.view.ViewOutlineProvider() {
                            @Override
                            public void getOutline(View view, android.graphics.Outline outline) {
                                float radius = view.getResources().getDimension(R.dimen.infuse_card_corner_radius);
                                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
                            }
                        });
                        targetView.setClipToOutline(true);
                    }

                    @Override
                    public void onCreate(ItemBridgeAdapter.ViewHolder ibvh) {
                        if (ibvh != null && ibvh.getViewHolder() != null && ibvh.getViewHolder().view != null) {
                            View cardView = ibvh.getViewHolder().view;
                            
                            applyInfuseCardStyle(cardView);

                            View targetViewLocal = cardView;
                            if (cardView instanceof androidx.leanback.widget.ImageCardView) {
                                android.widget.ImageView mainImage = ((androidx.leanback.widget.ImageCardView) cardView).getMainImageView();
                                if (mainImage != null) targetViewLocal = mainImage;
                            }
                            final View finalTargetView = targetViewLocal;

                            // EFFETTO ZOOM FOCUS E BORDO AMBRA DINAMICO
                            cardView.setOnFocusChangeListener((v, hasFocus) -> {
                                if (hasFocus) {
                                    finalTargetView.setForeground(androidx.core.content.ContextCompat.getDrawable(v.getContext(), R.drawable.selector_infuse_card_foreground));
                                    v.animate()
                                        .scaleX(1.15f)
                                        .scaleY(1.15f)
                                        .translationZ(16f)
                                        .setDuration(200)
                                        .setInterpolator(new android.view.animation.OvershootInterpolator(1.2f))
                                        .start();
                                } else {
                                    finalTargetView.setForeground(null);
                                    v.animate()
                                        .scaleX(1.0f)
                                        .scaleY(1.0f)
                                        .translationZ(0f)
                                        .setDuration(160)
                                        .setInterpolator(new android.view.animation.DecelerateInterpolator())
                                        .start();
                                }
                            });
                        }
                    }

                    @Override
                    public void onBind(ItemBridgeAdapter.ViewHolder ibvh) {
                        if (ibvh != null && ibvh.getViewHolder() != null && ibvh.getViewHolder().view != null) {
                            // Riapplica lo stile trasparente perché i Presenter di Nova reimpostano i background grigi su onBind!
                            applyInfuseCardStyle(ibvh.getViewHolder().view);

                            ibvh.getViewHolder().view.setOnClickListener(v -> {
                                if (mOnItemViewClickedListener != null) {
                                    mOnItemViewClickedListener.onItemClicked(
                                        ibvh.getViewHolder(),
                                        ibvh.getItem(),
                                        vh,
                                        listRow
                                    );
                                }
                            });
                        }
                    }
                });

                // Selection (Focus) handling: Forward selection listener to Leanback's OnItemViewSelectedListener
                vh.mDpadRecyclerView.addOnViewHolderSelectedListener(new OnViewHolderSelectedListener() {
                    @Override
                    public void onViewHolderSelected(@Nullable RecyclerView parent, @Nullable RecyclerView.ViewHolder viewHolder, int position, int subPosition) {
                        if (mOnItemViewSelectedListener != null) {
                            if (viewHolder instanceof ItemBridgeAdapter.ViewHolder) {
                                ItemBridgeAdapter.ViewHolder ibvh = (ItemBridgeAdapter.ViewHolder) viewHolder;
                                mOnItemViewSelectedListener.onItemSelected(
                                    ibvh.getViewHolder(),
                                    ibvh.getItem(),
                                    vh,
                                    listRow
                                );
                            } else {
                                mOnItemViewSelectedListener.onItemSelected(null, null, vh, listRow);
                            }
                        }
                    }

                    @Override
                    public void onViewHolderSelectedAndAligned(@Nullable RecyclerView parent, @Nullable RecyclerView.ViewHolder viewHolder, int position, int subPosition) {
                    }
                });

                vh.mItemBridgeAdapter = bridgeAdapter;
                vh.mDpadRecyclerView.setAdapter(bridgeAdapter);
            }
        }
    }

    @Override
    protected void onRowViewSelected(RowPresenter.ViewHolder holder, boolean selected) {
        super.onRowViewSelected(holder, selected);
        ViewHolder vh = (ViewHolder) holder;
        if (vh.mHeaderTitleView != null) {
            vh.mHeaderTitleView.setAlpha(selected ? 1.0f : 0.6f);
        }
    }

    @Override
    protected void onUnbindRowViewHolder(RowPresenter.ViewHolder holder) {
        ViewHolder vh = (ViewHolder) holder;
        if (vh.mDpadRecyclerView != null) {
            vh.mDpadRecyclerView.clearOnViewHolderSelectedListeners();
            vh.mDpadRecyclerView.setAdapter(null);
        }
        vh.mItemBridgeAdapter = null;
        super.onUnbindRowViewHolder(holder);
    }
}
// END PULCIX CUSTOM THEME
