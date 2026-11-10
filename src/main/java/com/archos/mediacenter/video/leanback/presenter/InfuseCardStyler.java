// BEGIN PULCIX CUSTOM THEME
package com.archos.mediacenter.video.leanback.presenter;

import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.leanback.widget.BaseCardView;
import androidx.leanback.widget.ImageCardView;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.utils.ThemeManager;

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
                imageCardView.getMainImageView().setClipToOutline(false);
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
            int paddingHorizontalPx = (int) (0 * density);
            infoField.setPadding(paddingHorizontalPx, (int)(6 * density), paddingHorizontalPx, (int)(2 * density));
            
            // Personalizzazione del Titolo della Card (Film / Serie TV)
            View titleText = infoField.findViewById(androidx.leanback.R.id.title_text);
            if (titleText instanceof TextView tv) {
                if (tv.getLayoutParams() != null) {
                    tv.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                }
                tv.setTextColor(0xFFFFFFFF); // Bianco puro per il titolo film/serie
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                tv.setMaxLines(1);
                tv.setEllipsize(TextUtils.TruncateAt.END);
                tv.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
                tv.setTextAlignment(android.view.View.TEXT_ALIGNMENT_CENTER);
            }

            // Usiamo INVISIBLE invece di GONE quando il testo è vuoto per mantenere altezza uniforme a tutte le card
            View contentText = infoField.findViewById(androidx.leanback.R.id.content_text);
            if (contentText instanceof TextView tv) {
                if (tv.getLayoutParams() != null) {
                    tv.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                }
                tv.setTextColor(0xFF777777); // Colore metadato grigio scuro (#777777) stile Infuse
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                tv.setMaxLines(1);
                tv.setEllipsize(TextUtils.TruncateAt.END);
                tv.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
                tv.setTextAlignment(android.view.View.TEXT_ALIGNMENT_CENTER);
                CharSequence text = tv.getText();
                tv.setVisibility((text == null || text.toString().trim().isEmpty()) ? View.INVISIBLE : View.VISIBLE);
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
            if (titleView instanceof TextView tv) {
                tv.setTextColor(0xFF999999); // Grigio #999999 per le cartelle/categorie
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                tv.setMaxLines(1);
                tv.setEllipsize(TextUtils.TruncateAt.END);
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
            if (primary instanceof TextView tv) {
                tv.setTextColor(0xFFFFFFFF); // Bianco #FFFFFF
                tv.setTypeface(null, android.graphics.Typeface.BOLD);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                tv.setMaxLines(1);
                tv.setEllipsize(TextUtils.TruncateAt.END);
                tv.setGravity(android.view.Gravity.CENTER);
            }

            View secondary = textviews.findViewById(R.id.secondary);
            if (secondary instanceof TextView tv) {
                tv.setTextColor(0xFF8E8E93); // Grigio #8E8E93
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                tv.setMaxLines(1);
                tv.setEllipsize(TextUtils.TruncateAt.END);
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
        
        // Imposta una sola volta il bordo inset (unfocused/focused gestito da selector)
        finalTargetView.setForeground(ContextCompat.getDrawable(cardView.getContext(), R.drawable.bg_card_stroke_inset));

        // EFFETTO ZOOM FOCUS HARDWARE ACCELERATED CON FASTOUTSLOWIN
        cardView.setOnFocusChangeListener((v, hasFocus) -> {
            View infoFieldView = v.findViewById(androidx.leanback.R.id.info_field);
            if (infoFieldView != null) {
                View contentTv = infoFieldView.findViewById(androidx.leanback.R.id.content_text);
                if (contentTv instanceof TextView tv) {
                    tv.setTextColor(hasFocus ? 0xFFD1D1D6 : 0xFF777777);
                }
            }

            v.animate().cancel();
            float targetScale = hasFocus ? 1.12f : 1.0f;
            v.animate()
                .scaleX(targetScale)
                .scaleY(targetScale)
                .setDuration(180)
                .setInterpolator(new FastOutSlowInInterpolator())
                .withLayer()
                .start();
        });

        bindCardStateFast(cardView);
    }

    public static void bindCardStateFast(View cardView) {
        if (cardView == null) return;
        if (!ThemeManager.getInstance(cardView.getContext()).isInfuseTheme()) return;

        boolean hasFocus = cardView.hasFocus() || cardView.isSelected();
        float targetScale = hasFocus ? 1.12f : 1.0f;
        if (cardView.getScaleX() != targetScale) {
            cardView.animate().cancel();
            cardView.setScaleX(targetScale);
            cardView.setScaleY(targetScale);
        }
        cardView.setTranslationZ(0f);

        View infoField = cardView.findViewById(androidx.leanback.R.id.info_field);
        if (infoField != null) {
            View titleText = infoField.findViewById(androidx.leanback.R.id.title_text);
            if (titleText instanceof TextView tv) {
                if (tv.getLayoutParams() != null && tv.getLayoutParams().width != ViewGroup.LayoutParams.MATCH_PARENT) {
                    tv.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                }
                tv.setMaxLines(1);
                tv.setEllipsize(TextUtils.TruncateAt.END);
                tv.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
                tv.setTextAlignment(android.view.View.TEXT_ALIGNMENT_CENTER);
            }

            View contentText = infoField.findViewById(androidx.leanback.R.id.content_text);
            if (contentText instanceof TextView tv) {
                if (tv.getLayoutParams() != null && tv.getLayoutParams().width != ViewGroup.LayoutParams.MATCH_PARENT) {
                    tv.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                }
                tv.setMaxLines(1);
                tv.setEllipsize(TextUtils.TruncateAt.END);
                tv.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
                tv.setTextAlignment(android.view.View.TEXT_ALIGNMENT_CENTER);
                CharSequence text = tv.getText();
                if (text == null || text.toString().trim().isEmpty()) {
                    tv.setVisibility(View.INVISIBLE);
                } else {
                    tv.setVisibility(View.VISIBLE);
                    tv.setTextColor(hasFocus ? 0xFFD1D1D6 : 0xFF777777);
                }
            }
        }
    }

    public static void bindCardState(View cardView) {
        bindCardStateFast(cardView);
    }
}
// END PULCIX CUSTOM THEME