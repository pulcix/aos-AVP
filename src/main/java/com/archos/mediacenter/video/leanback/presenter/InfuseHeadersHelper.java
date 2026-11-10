// BEGIN PULCIX CUSTOM THEME
package com.archos.mediacenter.video.leanback.presenter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.leanback.app.BrowseSupportFragment;
import androidx.leanback.widget.VerticalGridView;
import androidx.recyclerview.widget.RecyclerView;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.utils.ThemeManager;

public class InfuseHeadersHelper {

    public static void setupInfuseHeaders(BrowseSupportFragment fragment, View view) {
        if (fragment == null || view == null) return;
        if (!ThemeManager.getInstance(view.getContext()).isInfuseTheme()) return;

        view.post(() -> {
            if (fragment.getContext() == null) return;
            fragment.setBrandColor(android.graphics.Color.TRANSPARENT);

            View fadeEdge = view.findViewById(androidx.leanback.R.id.fade_out_edge);
            if (fadeEdge != null) fadeEdge.setVisibility(View.GONE);

            View headersRoot = view.findViewById(androidx.leanback.R.id.browse_headers_root);
            View headersGrid = view.findViewById(androidx.leanback.R.id.browse_headers);
            View headersDock = view.findViewById(androidx.leanback.R.id.browse_headers_dock);

            View containerDock = view.findViewById(androidx.leanback.R.id.browse_container_dock);
            if (containerDock != null) {
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

                targetPanel.setClipToOutline(true);
                if (targetPanel instanceof ViewGroup vg) {
                    vg.setClipChildren(true);
                }
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

            if (headersGrid instanceof VerticalGridView vgv) {
                if (vgv.getLayoutParams() instanceof ViewGroup.MarginLayoutParams vgvLp) {
                    vgvLp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    vgvLp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    vgvLp.topMargin = (int) (92 * density);
                    vgvLp.bottomMargin = (int) (18 * density);
                    vgvLp.leftMargin = 0;
                    vgvLp.rightMargin = 0;
                    vgvLp.setMarginStart(0);
                    vgvLp.setMarginEnd(0);
                    vgv.setLayoutParams(vgvLp);
                }

                vgv.setItemSpacing((int) (2 * density));
                vgv.setPadding(0, (int) (24 * density), 0, (int) (20 * density));
                vgv.setClipToPadding(true); // Ritaglia pulito le voci al bordo inferiore della card
                vgv.setClipChildren(true);

                vgv.setWindowAlignment(VerticalGridView.WINDOW_ALIGN_NO_EDGE);
                vgv.setWindowAlignmentOffset((int) (110 * density));
                vgv.setWindowAlignmentOffsetPercent(VerticalGridView.WINDOW_ALIGN_OFFSET_PERCENT_DISABLED);
                vgv.setItemAlignmentOffset(0);
                vgv.setItemAlignmentOffsetPercent(VerticalGridView.ITEM_ALIGN_OFFSET_PERCENT_DISABLED);

                for (int i = 0; i < vgv.getChildCount(); i++) {
                    updateHeaderItemStyle(vgv.getChildAt(i), false, density);
                }

                vgv.addOnChildViewHolderSelectedListener(new androidx.leanback.widget.OnChildViewHolderSelectedListener() {
                    @Override
                    public void onChildViewHolderSelected(RecyclerView parent, RecyclerView.ViewHolder child, int position, int subposition) {
                        for (int i = 0; i < vgv.getChildCount(); i++) {
                            View childView = vgv.getChildAt(i);
                            updateHeaderItemStyle(childView, childView == child.itemView, density);
                        }
                    }
                });

                vgv.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                    @Override
                    public void onChildViewAttachedToWindow(View child) {
                        RecyclerView.ViewHolder holder = vgv.getChildViewHolder(child);
                        boolean isSelected = holder != null && holder.getAbsoluteAdapterPosition() == vgv.getSelectedPosition();
                        updateHeaderItemStyle(child, isSelected, density);
                    }

                    @Override
                    public void onChildViewDetachedFromWindow(View child) {
                    }
                });
            }
        });
    }

    private static void updateHeaderItemStyle(View itemView, boolean isSelected, float density) {
        if (itemView == null) return;

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
            itemLp.height = (int) (48 * density);
            if (itemLp instanceof ViewGroup.MarginLayoutParams marginLp) {
                marginLp.topMargin = (int) (2 * density);
                marginLp.bottomMargin = (int) (2 * density);
            }
            itemView.setLayoutParams(itemLp);
        }

        if (isSelected) {
            android.graphics.drawable.GradientDrawable pill = new android.graphics.drawable.GradientDrawable();
            pill.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
            pill.setColor(0xFFFF7F02); // Arancione Infuse
            pill.setCornerRadius(10 * density);

            android.graphics.drawable.InsetDrawable pillInset = new android.graphics.drawable.InsetDrawable(
                    pill, (int) (28 * density), (int) (4 * density), (int) (24 * density), (int) (4 * density));
            itemView.setBackground(pillInset);
        } else {
            itemView.setBackground(null);
        }

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

    private static void lockScaleToOneRecursive(View v) {
        if (v == null) return;
        if (v.getScaleX() != 1.0f) v.setScaleX(1.0f);
        if (v.getScaleY() != 1.0f) v.setScaleY(1.0f);
        if (v.getAlpha() != 1.0f) v.setAlpha(1.0f);
        if (v instanceof ViewGroup vg) {
            vg.setClipChildren(false);
            vg.setClipToPadding(false);
            for (int i = 0; i < vg.getChildCount(); i++) {
                lockScaleToOneRecursive(vg.getChildAt(i));
            }
        }
    }

    private static TextView findHeaderTextView(View v) {
        if (v instanceof TextView) return (TextView) v;
        if (v instanceof ViewGroup vg) {
            for (int i = 0; i < vg.getChildCount(); i++) {
                TextView found = findHeaderTextView(vg.getChildAt(i));
                if (found != null) return found;
            }
        }
        return null;
    }
}
// END PULCIX CUSTOM THEME