package com.archos.mediacenter.video.leanback.details;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.Presenter;
import com.archos.mediacenter.video.R;

public class InfuseIconActionPresenter extends Presenter {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.infuse_action_icon_pill, parent, false);
        // Animazione ingrandimento al focus D-PAD
        v.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                view.animate().scaleX(1.08f).scaleY(1.08f).setDuration(150).start();
            } else {
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start();
            }
        });
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        Action action = (Action) item;
        ImageView iconView = viewHolder.view.findViewById(R.id.icon);
        long id = action.getId();

        if (id == VideoActionAdapter.ACTION_MARK_AS_WATCHED) {
            iconView.setImageResource(R.drawable.trakt_watched);
        } else if (id == VideoActionAdapter.ACTION_MARK_AS_NOT_WATCHED) {
            iconView.setImageResource(R.drawable.ic_eye_outline);
        } else if (id == VideoActionAdapter.ACTION_UNSCRAP) {
            iconView.setImageResource(R.drawable.ic_infuse_unscrap);
        } else if (id == VideoActionAdapter.ACTION_SCRAP) {
            iconView.setImageResource(R.drawable.ic_infuse_refresh);
        } else if (id == VideoActionAdapter.ACTION_ADD_TO_LIST) {
            iconView.setImageResource(R.drawable.ic_infuse_bookmark);
        } else if (id == VideoActionAdapter.ACTION_UNINDEX || id == VideoActionAdapter.ACTION_HIDE || id == VideoActionAdapter.ACTION_REMOVE_FROM_LIST) {
            iconView.setImageResource(R.drawable.ic_infuse_minus_circle);
        } else if (id == VideoActionAdapter.ACTION_DELETE || id == VideoActionAdapter.ACTION_CONFIRM_DELETE) {
            iconView.setImageResource(R.drawable.ic_infuse_delete);
        } else if (id == VideoActionAdapter.ACTION_INDEX) {
            iconView.setImageResource(R.drawable.ic_infuse_info);
        } else {
            iconView.setImageResource(R.drawable.ic_infuse_settings); // Fallback MORE icon
        }
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
    }
}