// Copyright 2025 Nova Video Player / Pulcix Custom Integration

package com.archos.mediacenter.video.leanback.presenter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.RowHeaderPresenter;
import androidx.leanback.widget.RowPresenter;
import androidx.recyclerview.widget.RecyclerView;

import com.archos.mediacenter.video.R;
import com.rubensousa.dpadrecyclerview.DpadRecyclerView;
import com.rubensousa.dpadrecyclerview.OnViewHolderSelectedListener;

// BEGIN PULCIX DPAD INTEGRATION
public class CustomDpadListRowPresenter extends RowPresenter {

    private OnItemViewClickedListener mOnItemViewClickedListener;
    private OnItemViewSelectedListener mOnItemViewSelectedListener;

    public static class ViewHolder extends RowPresenter.ViewHolder {
        private final DpadRecyclerView mDpadRecyclerView;
        private ItemBridgeAdapter mItemBridgeAdapter;

        public ViewHolder(View view, DpadRecyclerView dpadRecyclerView) {
            super(view);
            this.mDpadRecyclerView = dpadRecyclerView;
        }

        public DpadRecyclerView getDpadRecyclerView() {
            return mDpadRecyclerView;
        }
    }

    public CustomDpadListRowPresenter() {
        super();
        setHeaderPresenter(new RowHeaderPresenter());
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

        // 1. Set vertical spacing between rows (32dp bottom margin)
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) rowView.getLayoutParams();
        if (lp != null) {
            lp.bottomMargin = (int) (32 * parent.getResources().getDisplayMetrics().density);
            rowView.setLayoutParams(lp);
        }

        DpadRecyclerView dpadRecyclerView = rowView.findViewById(R.id.custom_dpad_row_content);

        // Explicitly set horizontal orientation for DpadRecyclerView
        dpadRecyclerView.setOrientation(RecyclerView.HORIZONTAL);

        // Add 16dp horizontal spacing between cards
        int itemSpacingPx = (int) (16 * parent.getResources().getDisplayMetrics().density);
        if (dpadRecyclerView.getItemDecorationCount() == 0) {
            dpadRecyclerView.addItemDecoration(
                com.rubensousa.dpadrecyclerview.spacing.DpadLinearSpacingDecoration.create(itemSpacingPx, 0, 0)
            );
        }

        return new ViewHolder(rowView, dpadRecyclerView);
    }

    @Override
    protected void onBindRowViewHolder(RowPresenter.ViewHolder holder, Object item) {
        super.onBindRowViewHolder(holder, item);
        ViewHolder vh = (ViewHolder) holder;

        if (item instanceof ListRow) {
            ListRow listRow = (ListRow) item;
            ObjectAdapter listAdapter = listRow.getAdapter();
            if (listAdapter != null) {
                ItemBridgeAdapter bridgeAdapter = new ItemBridgeAdapter(listAdapter);

                // 2. Click (ENTER key) handling: Forward click listener to Leanback's OnItemViewClickedListener
                bridgeAdapter.setAdapterListener(new ItemBridgeAdapter.AdapterListener() {
                    @Override
                    public void onBind(ItemBridgeAdapter.ViewHolder ibvh) {
                        if (ibvh != null && ibvh.getViewHolder() != null && ibvh.getViewHolder().view != null) {
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
                    public void onViewHolderSelected(RecyclerView parent, RecyclerView.ViewHolder viewHolder, int position, int subPosition) {
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
                    public void onViewHolderSelectedAndAligned(RecyclerView parent, RecyclerView.ViewHolder viewHolder, int position, int subPosition) {
                    }
                });

                vh.mItemBridgeAdapter = bridgeAdapter;
                vh.mDpadRecyclerView.setAdapter(bridgeAdapter);
            }
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
// END PULCIX DPAD INTEGRATION
