// BEGIN PULCIX CUSTOM THEME
package com.archos.mediacenter.video.leanback.presenter;

import android.view.View;
import android.view.ViewGroup;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.BaseCardView;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.utils.ThemeManager;
import androidx.core.content.ContextCompat;

public class InfuseCardStyler {

    public static void applyInfuseCardStyle(View cardView) {
        if (cardView == null) return;
        if (!ThemeManager.getInstance(cardView.getContext()).isInfuseTheme()) return;

        // RIMOZIONE RIMASUGLI GRIGI AGLI ANGOLI
        cardView.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        cardView.setBackground(null);

        View targetView = cardView;

        if (cardView instanceof ImageCardView) {
            ImageCardView imageCardView = (ImageCardView) cardView;
            imageCardView.setCardType(BaseCardView.CARD_TYPE_INFO_UNDER);
            imageCardView.setInfoVisibility(BaseCardView.CARD_REGION_VISIBLE_ALWAYS);
            imageCardView.setInfoAreaBackground(null);
            imageCardView.setInfoAreaBackgroundColor(android.graphics.Color.TRANSPARENT);
            if (imageCardView.getMainImageView() != null) {
                targetView = imageCardView.getMainImageView();
                android.widget.ImageView mainImageView = imageCardView.getMainImageView();
                final float radius = 14f * cardView.getResources().getDisplayMetrics().density;
                mainImageView.setOutlineProvider(new android.view.ViewOutlineProvider() {
                    @Override
                    public void getOutline(View view, android.graphics.Outline outline) {
                        outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
                    }
                });
                mainImageView.setClipToOutline(true);
            }
        }
        
        // RIMOZIONE FASCIA GRIGIA SULLE ALTRE CARD E OTTIMIZZAZIONE SPAZI INFO_FIELD
        View infoField = cardView.findViewById(androidx.leanback.R.id.info_field);
        if (infoField != null) {
            infoField.setVisibility(View.VISIBLE);
            infoField.setBackgroundColor(android.graphics.Color.TRANSPARENT);
            infoField.setBackground(null);
            
            // Riduciamo gli spazi vuoti e padding laterali tra la locandina e il titolo (Stile Infuse)
            infoField.setMinimumHeight(0);
            float density = cardView.getResources().getDisplayMetrics().density;
            int paddingHorizontalPx = (int) (0 * density); // <--- Imposta qui il padding Left/Right in dp solo per Infuse (es. 0dp)
            infoField.setPadding(paddingHorizontalPx, (int)(6 * density), paddingHorizontalPx, (int)(2 * density));
            
            // Personalizzazione del Titolo della Card (Film / Serie TV)
            View titleText = infoField.findViewById(androidx.leanback.R.id.title_text);
            if (titleText instanceof android.widget.TextView tv) {
                tv.setTextColor(0xFFFFFFFF); // Bianco puro per il titolo film/serie
                tv.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 17);
                tv.setGravity(android.view.Gravity.CENTER);
                tv.setTextAlignment(android.view.View.TEXT_ALIGNMENT_CENTER);
            }

            // Rimuoviamo l'ingombro vuoto in altezza del content_text secondario se vuoto
            View contentText = infoField.findViewById(androidx.leanback.R.id.content_text);
            if (contentText instanceof android.widget.TextView tv) {
                CharSequence text = tv.getText();
                if (text == null || text.toString().trim().isEmpty()) {
                    tv.setVisibility(View.GONE);
                } else {
                    tv.setVisibility(View.VISIBLE);
                    // Colore metadato grigio scuro (#777777) stile Infuse
                    tv.setTextColor(0xFF777777);
                    tv.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 15);
                    tv.setGravity(android.view.Gravity.CENTER);
                    tv.setTextAlignment(android.view.View.TEXT_ALIGNMENT_CENTER);
                }
            }
        }
        
        // RIMOZIONE FASCIA GRIGIA E STILE PER BOX ITEM PRESENTER ("Movies by Genre", etc)
        View titleView = cardView.findViewById(R.id.name);
        if (titleView == null) {
            titleView = cardView.findViewById(R.id.title);
        }
        if (titleView != null) {
            if (titleView.getParent() instanceof ViewGroup) {
                ViewGroup titleParent = (ViewGroup) titleView.getParent();
                titleParent.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                titleParent.setBackground(null);
            }
            if (titleView instanceof android.widget.TextView tv) {
                tv.setTextColor(0xFF999999); // Grigio #999999 per le cartelle/categorie
                tv.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 16); // font size
                tv.setGravity(android.view.Gravity.CENTER);
                tv.setTextAlignment(android.view.View.TEXT_ALIGNMENT_CENTER);
            }
        }

        // STILE PER LEANBACK SMB ITEM (Network Shares, UPnP, Network Shortcuts)
        View textviews = cardView.findViewById(R.id.textviews);
        if (textviews != null) {
            textviews.setBackgroundColor(android.graphics.Color.TRANSPARENT);
            textviews.setBackground(null);

            View primary = textviews.findViewById(R.id.primary);
            if (primary instanceof android.widget.TextView tv) {
                tv.setTextColor(0xFFFFFFFF); // Bianco #FFFFFF
                tv.setTypeface(null, android.graphics.Typeface.BOLD);
                tv.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 15);
                tv.setGravity(android.view.Gravity.CENTER);
            }

            View secondary = textviews.findViewById(R.id.secondary);
            if (secondary instanceof android.widget.TextView tv) {
                tv.setTextColor(0xFF8E8E93); // Grigio #8E8E93
                tv.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 13);
                tv.setGravity(android.view.Gravity.CENTER);
            }

            View smbImage = cardView.findViewById(R.id.image);
            if (smbImage != null) {
                smbImage.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                smbImage.setBackground(null);
                if (smbImage instanceof android.widget.ImageView iv) {
                    float density = cardView.getResources().getDisplayMetrics().density;
                    iv.setPadding(0, (int) (16 * density), 0, 0); // paddingTop = 16dp
                    iv.setScaleType(android.widget.ImageView.ScaleType.CENTER);
                }
            }

            float density = cardView.getResources().getDisplayMetrics().density;
            android.graphics.drawable.GradientDrawable cardBg = new android.graphics.drawable.GradientDrawable();
            cardBg.setColor(0xFF161618);
            cardBg.setCornerRadius(14 * density);
            cardBg.setStroke(Math.max(2, (int) (1.5f * density)), 0xFF38383C);
            cardView.setBackground(cardBg);
            cardView.setClipToOutline(true);

            if (cardView instanceof ViewGroup vg && vg.getChildCount() > 0) {
                View inner = vg.getChildAt(0);
                inner.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                inner.setBackground(null);
            }
        }

        // ARROTONDAMENTO (14dp) SULLA TARGET VIEW
        // Cerca R.id.image_container (usato da BoxItemPresenter / IconItemPresenter)
        View imageContainer = cardView.findViewById(R.id.image_container);
        if (imageContainer != null) {
            targetView = imageContainer;
        } else if (cardView instanceof com.archos.mediacenter.video.leanback.presenter.CustomImageCardview) {
            if (((com.archos.mediacenter.video.leanback.presenter.CustomImageCardview) cardView).getMainImageView() != null) {
                targetView = (View) ((com.archos.mediacenter.video.leanback.presenter.CustomImageCardview) cardView).getMainImageView().getParent();
            }
        }

        // RIMOZIONE ELEVAZIONE ED EFFETTO DROP SHADOW
        cardView.setStateListAnimator(null);
        cardView.setElevation(0f);
        cardView.setTranslationZ(0f);
        androidx.core.view.ViewCompat.setElevation(cardView, 0f);
        androidx.core.view.ViewCompat.setTranslationZ(cardView, 0f);

        targetView.setStateListAnimator(null);
        targetView.setElevation(0f);
        targetView.setTranslationZ(0f);
        androidx.core.view.ViewCompat.setElevation(targetView, 0f);
        androidx.core.view.ViewCompat.setTranslationZ(targetView, 0f);

        targetView.setOutlineProvider(new android.view.ViewOutlineProvider() {
            @Override
            public void getOutline(View view, android.graphics.Outline outline) {
                float radius = view.getResources().getDimension(R.dimen.infuse_card_corner_radius);
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
            }
        });
        targetView.setClipToOutline(true);

        View finalTargetView = targetView;
        
        // Imposta inizialmente il bordo inset (unfocused state)
        finalTargetView.setForeground(ContextCompat.getDrawable(cardView.getContext(), R.drawable.bg_card_stroke_inset));

        // EFFETTO ZOOM FOCUS SENZA DROP SHADOW
        cardView.setOnFocusChangeListener((v, hasFocus) -> {
            v.setStateListAnimator(null);
            v.setElevation(0f);
            v.setTranslationZ(0f);
            androidx.core.view.ViewCompat.setElevation(v, 0f);
            androidx.core.view.ViewCompat.setTranslationZ(v, 0f);

            finalTargetView.setStateListAnimator(null);
            finalTargetView.setElevation(0f);
            finalTargetView.setTranslationZ(0f);
            androidx.core.view.ViewCompat.setElevation(finalTargetView, 0f);
            androidx.core.view.ViewCompat.setTranslationZ(finalTargetView, 0f);

            if (hasFocus) {
                // Reset siblings scale to 1.0f if they do not have focus
                if (v.getParent() instanceof ViewGroup) {
                    ViewGroup parent = (ViewGroup) v.getParent();
                    for (int i = 0; i < parent.getChildCount(); i++) {
                        View sibling = parent.getChildAt(i);
                        if (sibling != v && !sibling.hasFocus()) {
                            sibling.animate().cancel();
                            sibling.setScaleX(1.0f);
                            sibling.setScaleY(1.0f);
                            sibling.setTranslationZ(0f);
                        }
                    }
                }

                finalTargetView.setForeground(ContextCompat.getDrawable(v.getContext(), R.drawable.bg_card_stroke_inset));
                v.animate()
                    .scaleX(1.15f)
                    .scaleY(1.15f)
                    .translationZ(0f)
                    .setDuration(200)
                    .setInterpolator(new android.view.animation.OvershootInterpolator(1.2f))
                    .start();
            } else {
                finalTargetView.setForeground(ContextCompat.getDrawable(v.getContext(), R.drawable.bg_card_stroke_inset));
                v.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .translationZ(0f)
                    .setDuration(160)
                    .setInterpolator(new android.view.animation.DecelerateInterpolator())
                    .start();
            }
        });

        if (cardView.getTag(R.id.root) == null) {
            cardView.setTag(R.id.root, Boolean.TRUE);
            cardView.getViewTreeObserver().addOnGlobalFocusChangeListener((oldFocus, newFocus) -> {
                bindCardState(cardView);
            });
        }

        bindCardState(cardView);
    }

    public static void bindCardState(View cardView) {
        if (cardView == null) return;
        if (!ThemeManager.getInstance(cardView.getContext()).isInfuseTheme()) return;

        cardView.animate().cancel();
        boolean hasFocus = cardView.hasFocus() || cardView.isSelected();
        float scale = hasFocus ? 1.15f : 1.0f;
        cardView.setScaleX(scale);
        cardView.setScaleY(scale);
        cardView.setTranslationZ(0f);

        if (hasFocus && cardView.getParent() instanceof ViewGroup parent) {
            for (int i = 0; i < parent.getChildCount(); i++) {
                View sibling = parent.getChildAt(i);
                if (sibling != cardView && !sibling.hasFocus()) {
                    sibling.animate().cancel();
                    sibling.setScaleX(1.0f);
                    sibling.setScaleY(1.0f);
                    sibling.setTranslationZ(0f);
                }
            }
        }
    }
}
// END PULCIX CUSTOM THEME