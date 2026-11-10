package com.archos.mediacenter.video.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InfuseDialogHelper {

    public static void applyInfuseStyleToAlertDialog(Dialog dialog, Context context) {
        if (dialog == null || context == null || !ThemeManager.getInstance(context).isInfuseTheme()) return;
        float density = context.getResources().getDisplayMetrics().density;

        if (dialog.getWindow() != null) {
            GradientDrawable windowBg = new GradientDrawable();
            windowBg.setColor(0xFF161618); // Antracite scuro Infuse
            windowBg.setCornerRadius(14 * density);
            windowBg.setStroke(Math.max(2, (int) (1.5f * density)), 0xFF38383C);
            InsetDrawable insetBg = new InsetDrawable(
                    windowBg, (int) (16 * density), (int) (16 * density), (int) (16 * density), (int) (16 * density));
            dialog.getWindow().setBackgroundDrawable(insetBg);
            View decor = dialog.getWindow().getDecorView();

            View contentPanel = decor.findViewById(android.R.id.content);
            if (contentPanel == null) {
                contentPanel = decor.findViewById(androidx.appcompat.R.id.parentPanel);
            }
            if (contentPanel != null) {
                int pad12 = (int) (12 * density);
                contentPanel.setPadding(pad12, pad12, pad12, pad12);
            }

            if (decor instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) decor;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    styleDialogViewHierarchyRecursive(vg.getChildAt(i), density);
                }
            } else {
                styleDialogViewHierarchyRecursive(decor, density);
            }
        }

        if (dialog instanceof androidx.appcompat.app.AlertDialog) {
            androidx.appcompat.app.AlertDialog ad = (androidx.appcompat.app.AlertDialog) dialog;
            int[] btnIds = {
                DialogInterface.BUTTON_POSITIVE,
                DialogInterface.BUTTON_NEGATIVE,
                DialogInterface.BUTTON_NEUTRAL
            };
            for (int id : btnIds) {
                Button btn = ad.getButton(id);
                if (btn != null) {
                    styleInfuseDialogButton(btn, density);
                }
            }
        } else if (dialog instanceof android.app.AlertDialog) {
            android.app.AlertDialog ad = (android.app.AlertDialog) dialog;
            int[] btnIds = {
                DialogInterface.BUTTON_POSITIVE,
                DialogInterface.BUTTON_NEGATIVE,
                DialogInterface.BUTTON_NEUTRAL
            };
            for (int id : btnIds) {
                Button btn = ad.getButton(id);
                if (btn != null) {
                    styleInfuseDialogButton(btn, density);
                }
            }
        }
    }

    private static void styleInfuseDialogButton(Button btn, float density) {
        btn.setAllCaps(false);
        btn.setTextColor(0xFFFFFFFF);
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        btn.setPadding((int) (20 * density), (int) (8 * density), (int) (20 * density), (int) (8 * density));

        GradientDrawable btnBg = new GradientDrawable();
        btnBg.setCornerRadius(10 * density);
        btnBg.setColor(btn.hasFocus() ? 0xFFFF7F02 : 0xFF2C2C2E);
        btn.setBackground(btnBg);

        if (btn.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) btn.getLayoutParams();
            lp.leftMargin = (int) (6 * density);
            lp.rightMargin = (int) (6 * density);
            lp.topMargin = (int) (0 * density);
            lp.bottomMargin = (int) (2 * density); // Spazio extra sotto i bottoni Cancel / OK!
            btn.setLayoutParams(lp);
        }

        // Aumenta anche il padding inferiore e destro della barra contenitore dei bottoni (buttonPanel)
        if (btn.getParent() instanceof View) {
            View buttonBar = (View) btn.getParent();
            buttonBar.setPadding(
                (int) (6 * density),
                (int) (0 * density),
                (int) (6 * density),
                (int) (6 * density) // Garantisce respiro dal bordo inferiore della card
            );
        }

        btn.setOnFocusChangeListener((v, hasFocus) -> {
            btnBg.setColor(hasFocus ? 0xFFFF7F02 : 0xFF2C2C2E);
            btn.setTypeface(null, hasFocus ? Typeface.BOLD : Typeface.NORMAL);
        });
    }

    private static void styleDialogViewHierarchyRecursive(View view, float density) {
        if (view == null) return;
        if (view instanceof EditText) {
            EditText et = (EditText) view;
            et.setTextColor(0xFFFFFFFF);
            et.setHintTextColor(0xFF8E8E93);
            int padH = (int) (14 * density);
            int padV = (int) (10 * density);
            et.setPadding(padH, padV, padH, padV);

            GradientDrawable etBg = new GradientDrawable();
            etBg.setColor(0xFF1C1C1E);
            etBg.setCornerRadius(10 * density);
            etBg.setStroke((int) (1.5f * density), et.hasFocus() ? 0xFFFF7F02 : 0xFF38383C);
            et.setBackground(etBg);

            if (et.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) et.getLayoutParams();
                lp.topMargin = (int) (6 * density);
                lp.bottomMargin = (int) (6 * density);
                et.setLayoutParams(lp);
            }

            View.OnFocusChangeListener existing = et.getOnFocusChangeListener();
            et.setOnFocusChangeListener((v, hasFocus) -> {
                if (existing != null) existing.onFocusChange(v, hasFocus);
                etBg.setStroke((int) ((hasFocus ? 2.0f : 1.5f) * density), hasFocus ? 0xFFFF7F02 : 0xFF38383C);
            });
        } else if (view instanceof CheckBox) {
            CheckBox cb = (CheckBox) view;
            cb.setTextColor(0xFFD1D1D6);
            cb.setButtonTintList(ColorStateList.valueOf(0xFFFF7F02));
            int pad = (int) (8 * density);
            cb.setPadding(pad, pad, pad, pad);
            GradientDrawable cbBg = new GradientDrawable();
            cbBg.setCornerRadius(8 * density);
            cbBg.setColor(Color.TRANSPARENT);
            cb.setBackground(cbBg);
            cb.setOnFocusChangeListener((v, hasFocus) -> {
                cbBg.setColor(hasFocus ? 0x33FF7F02 : Color.TRANSPARENT);
                cb.setTextColor(hasFocus ? 0xFFFFFFFF : 0xFFD1D1D6);
            });
        } else if (view instanceof TextView && !(view instanceof Button)) {
            TextView tv = (TextView) view;
            tv.setTextColor(0xFFFFFFFF);
        } else if (view instanceof ViewGroup) {
            // Rimuove eventuali sfondi grigi o bordi squadrati intermedi dei panel di AlertDialog
            // Il background è già gestito dalla Window principale, quindi i panel devono essere interamente trasparenti
            view.setBackgroundColor(Color.TRANSPARENT);
            view.setBackground(null);
            
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                styleDialogViewHierarchyRecursive(vg.getChildAt(i), density);
            }
        }
    }

    public static void showInfuseSingleChoiceDialog(Context context, String title, CharSequence[] entries, int checkedItem, DialogInterface.OnClickListener listener) {
        if (context == null || entries == null || entries.length == 0) return;
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        float density = context.getResources().getDisplayMetrics().density;
        int pad20 = (int) (20 * density);
        int pad12 = (int) (12 * density);

        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(pad20, pad20, pad20, pad20);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF161618); // Antracite scuro Infuse
        bg.setCornerRadius(14 * density);
        bg.setStroke(Math.max(2, (int) (1.5f * density)), 0xFF38383C);
        root.setBackground(bg);

        if (title != null && !title.isEmpty()) {
            TextView titleTv = new TextView(context);
            titleTv.setText(title);
            titleTv.setTextColor(0xFF8E8E93);
            titleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            titleTv.setTypeface(null, Typeface.BOLD);
            titleTv.setPadding(pad12, 0, pad12, pad12);
            root.addView(titleTv);
        }

        View viewToFocus = null;

        for (int i = 0; i < entries.length; i++) {
            final int idx = i;
            final boolean isChecked = (i == checkedItem);

            LinearLayout row = new LinearLayout(context);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding((int) (16 * density), (int) (12 * density), (int) (16 * density), (int) (12 * density));
            row.setFocusable(true);
            row.setFocusableInTouchMode(false);

            GradientDrawable itemBg = new GradientDrawable();
            itemBg.setCornerRadius(10 * density);
            itemBg.setColor(Color.TRANSPARENT);
            row.setBackground(itemBg);

            TextView itemTv = new TextView(context);
            itemTv.setText(entries[i]);
            itemTv.setTextColor(0xFFFFFFFF);
            itemTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            itemTv.setTypeface(null, isChecked ? Typeface.BOLD : Typeface.NORMAL);
            LinearLayout.LayoutParams tvLp = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            itemTv.setLayoutParams(tvLp);

            TextView checkTv = new TextView(context);
            checkTv.setText(isChecked ? "✓" : "");
            checkTv.setTextColor(0xFFFF7F02); // Spunta arancione quando non in focus, bianca quando in focus
            checkTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            checkTv.setTypeface(null, Typeface.BOLD);
            checkTv.setPadding((int) (12 * density), 0, 0, 0);

            row.addView(itemTv);
            row.addView(checkTv);

            row.setOnFocusChangeListener((v, hasFocus) -> {
                itemBg.setColor(hasFocus ? 0xFFFF7F02 : Color.TRANSPARENT);
                itemTv.setTypeface(null, (hasFocus || isChecked) ? Typeface.BOLD : Typeface.NORMAL);
                checkTv.setTextColor(hasFocus ? 0xFFFFFFFF : 0xFFFF7F02);
            });

            row.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onClick(dialog, idx);
                }
                dialog.dismiss();
            });

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    (int) (360 * density), ViewGroup.LayoutParams.WRAP_CONTENT);
            if (i > 0) lp.topMargin = (int) (4 * density);
            row.setLayoutParams(lp);
            root.addView(row);

            if (isChecked || viewToFocus == null) {
                viewToFocus = row;
            }
        }

        dialog.setContentView(root);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();
        if (viewToFocus != null) {
            final View targetFocus = viewToFocus;
            targetFocus.post(targetFocus::requestFocus);
        }
    }
}
