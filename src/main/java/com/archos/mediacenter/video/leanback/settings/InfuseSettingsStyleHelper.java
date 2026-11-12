package com.archos.mediacenter.video.leanback.settings;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.widget.CompoundButtonCompat;
import androidx.leanback.preference.LeanbackSettingsFragmentCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.archos.mediacenter.video.utils.ThemeManager;

public class InfuseSettingsStyleHelper {

    public static void styleSettingsFragment(LeanbackSettingsFragmentCompat fragment) {
        if (fragment == null || fragment.getContext() == null) return;
        if (!ThemeManager.getInstance(fragment.getContext()).isInfuseTheme()) return;

        View view = fragment.getView();
        if (view == null) return;

        // Dimmer background: entire screen behind the floating panel
        view.setBackgroundColor(0x00000000);
        view.setClipToOutline(false);
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            lp.topMargin = 0;
            lp.bottomMargin = 0;
            lp.leftMargin = 0;
            lp.rightMargin = 0;
            view.setLayoutParams(lp);
        }
    }

    public static void stylePreferenceFragment(PreferenceFragmentCompat fragment) {
        if (fragment == null || fragment.getContext() == null) return;
        if (!ThemeManager.getInstance(fragment.getContext()).isInfuseTheme()) return;

        View view = fragment.getView();
        if (view == null) return;

        float density = fragment.getContext().getResources().getDisplayMetrics().density;

        view.post(() -> {
            float d = view.getResources().getDisplayMetrics().density;

            // 1. Individua il vero contenitore del pannello destro (main_frame o la root del PreferenceFragment)
            View panel = view.findViewById(androidx.leanback.preference.R.id.main_frame);
            if (panel == null) {
                int id = view.getResources().getIdentifier("main_frame", "id", view.getContext().getPackageName());
                if (id != 0) panel = view.findViewById(id);
            }
            if (panel == null) panel = view;

            // 2. Costruisci il GradientDrawable opaco #161618 con bordino #38383C e raggio 14dp
            GradientDrawable panelBg = new GradientDrawable();
            panelBg.setShape(GradientDrawable.RECTANGLE);
            panelBg.setColor(0xFF161618); // Opaco al 100%!
            panelBg.setCornerRadius(14 * d);
            panelBg.setStroke(Math.max(2, (int) (1.5f * d)), 0xFF38383C);

            panel.setWillNotDraw(false);
            panel.setBackground(panelBg);
            panel.setClipToOutline(true);
            panel.setElevation(24 * d);

            if (panel.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) panel.getLayoutParams();
                int m = (int) (20 * d);
                lp.topMargin = m;
                lp.bottomMargin = m;
                lp.rightMargin = m;
                lp.setMarginEnd(m);
                panel.setLayoutParams(lp);
            }

            // 3. Assicurati che i figli interni siano trasparenti così si vede lo sfondo #161618 di panel!
            View decorTitleContainer = panel.findViewById(androidx.leanback.preference.R.id.decor_title_container);
            if (decorTitleContainer != null) {
                decorTitleContainer.setBackgroundColor(Color.TRANSPARENT);
                decorTitleContainer.setBackground(null);
            }
            
            View decorTitle = panel.findViewById(androidx.leanback.preference.R.id.decor_title);
            if (decorTitle != null) {
                decorTitle.setBackgroundColor(Color.TRANSPARENT);
                decorTitle.setBackground(null);
                if (decorTitle instanceof TextView) {
                    TextView titleTv = (TextView) decorTitle;
                    titleTv.setTextColor(0xFFFFFFFF);
                    titleTv.setTypeface(null, Typeface.BOLD);
                }
            }

            View listContainer = panel.findViewById(android.R.id.list_container);
            if (listContainer != null) listContainer.setBackgroundColor(Color.TRANSPARENT);
            if (fragment.getListView() != null) fragment.getListView().setBackgroundColor(Color.TRANSPARENT);
        });

        RecyclerView listView = fragment.getListView();
        if (listView != null) {
            // Style current attached children
            for (int i = 0; i < listView.getChildCount(); i++) {
                stylePreferenceChild(fragment, listView, listView.getChildAt(i), density);
            }

            // Listen for attached children
            listView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                @Override
                public void onChildViewAttachedToWindow(View child) {
                    stylePreferenceChild(fragment, listView, child, density);
                }

                @Override
                public void onChildViewDetachedFromWindow(View child) {
                }
            });
        }
    }

    public static void stylePreferenceChild(PreferenceFragmentCompat fragment, RecyclerView listView, View child, float density) {
        if (child == null || listView == null) return;

        int position = listView.getChildAdapterPosition(child);
        Preference preference = null;

        RecyclerView.Adapter<?> adapter = listView.getAdapter();
        if (adapter != null && position != RecyclerView.NO_POSITION) {
            try {
                java.lang.reflect.Method getItemMethod = adapter.getClass().getDeclaredMethod("getItem", int.class);
                getItemMethod.setAccessible(true);
                Object item = getItemMethod.invoke(adapter, position);
                if (item instanceof Preference) {
                    preference = (Preference) item;
                }
            } catch (Exception ignored) {
            }
        }

        TextView title = child.findViewById(android.R.id.title);
        TextView summary = child.findViewById(android.R.id.summary);

        boolean isCategory = (preference instanceof PreferenceCategory);

        if (isCategory) {
            // Category Header (e.g., PreferenceCategory: "About", "Video")
            child.setBackgroundColor(Color.TRANSPARENT);
            if (title != null) {
                title.setTextColor(0xFFFF7F02); // Infuse Orange
                title.setTypeface(null, Typeface.BOLD);
                title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            }
            if (summary != null) {
                summary.setTextColor(0xFFFF7F02);
            }
        } else if (!child.isFocusable()) {
            // Non-focusable informative preference (e.g., "Nova v6.4.29")
            child.setBackgroundColor(Color.TRANSPARENT);
            if (title != null) {
                title.setTextColor(0xFFFFFFFF); // White
                title.setTypeface(null, Typeface.NORMAL);
            }
            if (summary != null) {
                summary.setTextColor(0xFF8E8E93);
            }
        } else {
            // Selectable Preference Item (focusable)
            if (child.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
                int margin8 = (int) (8 * density);
                lp.leftMargin = margin8;
                lp.rightMargin = margin8;
                child.setLayoutParams(lp);
            }

            GradientDrawable focusedBg = new GradientDrawable();
            focusedBg.setColor(0xFFFF7F02); // Infuse Orange
            focusedBg.setCornerRadius(10 * density);

            GradientDrawable normalBg = new GradientDrawable();
            normalBg.setColor(Color.TRANSPARENT);
            normalBg.setCornerRadius(10 * density);

            applyPreferenceFocusState(child, child.hasFocus(), focusedBg, normalBg);

            child.setOnFocusChangeListener((v, hasFocus) -> {
                applyPreferenceFocusState(v, hasFocus, focusedBg, normalBg);
            });
        }
    }

    private static void applyPreferenceFocusState(View child, boolean hasFocus, GradientDrawable focusedBg, GradientDrawable normalBg) {
        child.setBackground(hasFocus ? focusedBg : normalBg);

        TextView title = child.findViewById(android.R.id.title);
        if (title != null) {
            title.setTextColor(hasFocus ? 0xFFFFFFFF : (child.isEnabled() ? 0xFFFFFFFF : 0xFF8E8E93));
            title.setTypeface(null, hasFocus ? Typeface.BOLD : Typeface.NORMAL);
        }

        TextView summary = child.findViewById(android.R.id.summary);
        if (summary != null) {
            summary.setTextColor(hasFocus ? 0xFFF2F2F7 : 0xFF8E8E93);
        }

        colorCompoundButtons(child, hasFocus);
    }

    private static void colorCompoundButtons(View view, boolean rowHasFocus) {
        if (view == null) return;
        if (view instanceof CompoundButton) {
            CompoundButton button = (CompoundButton) view;
            int color = rowHasFocus ? 0xFFFFFFFF : 0xFFFF7F02;
            CompoundButtonCompat.setButtonTintList(button, ColorStateList.valueOf(color));
            if (button instanceof SwitchCompat) {
                SwitchCompat switchCompat = (SwitchCompat) button;
                switchCompat.setThumbTintList(ColorStateList.valueOf(color));
                switchCompat.setTrackTintList(ColorStateList.valueOf(rowHasFocus ? 0x80FFFFFF : 0x80FF7F02));
            } else if (button instanceof Switch) {
                Switch switchView = (Switch) button;
                switchView.setThumbTintList(ColorStateList.valueOf(color));
                switchView.setTrackTintList(ColorStateList.valueOf(rowHasFocus ? 0x80FFFFFF : 0x80FF7F02));
            }
        } else if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                colorCompoundButtons(vg.getChildAt(i), rowHasFocus);
            }
        }
    }
}
