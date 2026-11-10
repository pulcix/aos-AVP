// BEGIN PULCIX CUSTOM THEME
package com.archos.mediacenter.video.leanback.presenter;

import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.VerticalGridPresenter;
import androidx.leanback.widget.VerticalGridView;
import androidx.recyclerview.widget.RecyclerView;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.utils.ThemeManager;

public class InfuseGridHelper {

    private static final RecyclerView.RecycledViewPool sSharedViewPool = new RecyclerView.RecycledViewPool();

    static {
        sSharedViewPool.setMaxRecycledViews(0, 30);
    }

    public static VerticalGridPresenter createInfuseGridPresenter(int numColumns) {
        VerticalGridPresenter vgp = new VerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_NONE, false) {
            @Override
            protected void initializeGridViewHolder(ViewHolder vh) {
                super.initializeGridViewHolder(vh);
                VerticalGridView gridView = vh.getGridView();
                if (gridView != null) {
                    setupInfuseGridView(gridView);
                }
            }
        };
        vgp.setShadowEnabled(false);
        vgp.setNumberOfColumns(numColumns);
        return vgp;
    }

    public static void setupInfuseGridView(VerticalGridView gridView) {
        if (gridView == null) return;
        if (!ThemeManager.getInstance(gridView.getContext()).isInfuseTheme()) return;

        float density = gridView.getResources().getDisplayMetrics().density;
        int spacingPx = gridView.getResources().getDimensionPixelSize(R.dimen.infuse_row_item_spacing);
        int edgeSpacingPx = (int) (48 * density); // Allineato a 48dp esattamente come Search e la Home!

        // Forza larghezza MATCH_PARENT su browse_grid e browse_grid_dock così anche con pochi elementi parte da sinistra (48dp)
        if (gridView.getLayoutParams() != null) {
            gridView.getLayoutParams().width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
        }
        if (gridView.getParent() instanceof android.view.View parentView && parentView.getLayoutParams() != null) {
            parentView.getLayoutParams().width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
        }

        gridView.setHorizontalSpacing(spacingPx);
        gridView.setVerticalSpacing((int) (24 * density));
        gridView.setClipChildren(false);
        gridView.setClipToPadding(false);

        // Padding superiore di 104dp per stare ben sotto la Top Bar (Search + icone), e 48dp ai lati come in Home
        int topPadding = (int) (104 * density);
        int bottomPadding = (int) (48 * density);
        gridView.setPadding(edgeSpacingPx, topPadding, edgeSpacingPx, bottomPadding);

        // Ottimizzazioni performance RecyclerView per scorrimento 60fps fluido
        gridView.setItemAnimator(null);
        gridView.setHasFixedSize(true);
        gridView.setItemViewCacheSize(24);
        gridView.setRecycledViewPool(sSharedViewPool);

        // Configurazione allineamento morbido stile Home (pivot al 104dp senza scatti ai bordi)
        gridView.setWindowAlignment(VerticalGridView.WINDOW_ALIGN_LOW_EDGE);
        gridView.setWindowAlignmentOffset((int) (104 * density));
        gridView.setWindowAlignmentOffsetPercent(VerticalGridView.WINDOW_ALIGN_OFFSET_PERCENT_DISABLED);
        gridView.setItemAlignmentOffset(0);
        gridView.setItemAlignmentOffsetPercent(VerticalGridView.ITEM_ALIGN_OFFSET_PERCENT_DISABLED);

        gridView.setFocusDrawingOrderEnabled(true);
        gridView.setAnimateChildLayout(false);
    }
}
// END PULCIX CUSTOM THEME