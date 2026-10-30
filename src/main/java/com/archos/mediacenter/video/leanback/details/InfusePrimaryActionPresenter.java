package com.archos.mediacenter.video.leanback.details;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.Presenter;
import com.archos.mediacenter.video.R;

public class InfusePrimaryActionPresenter extends Presenter {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.infuse_action_primary_pill, parent, false);
        // Animazione ingrandimento al focus D-PAD
        v.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                view.animate().scaleX(1.05f).scaleY(1.05f).setDuration(150).start();
            } else {
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start();
            }
        });
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        Action action = (Action) item;
        TextView textView = viewHolder.view.findViewById(R.id.text);
        
        textView.setText(action.getLabel1());
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
    }
}