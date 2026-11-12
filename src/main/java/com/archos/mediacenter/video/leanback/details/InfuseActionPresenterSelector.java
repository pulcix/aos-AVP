package com.archos.mediacenter.video.leanback.details;

import androidx.leanback.widget.Action;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.PresenterSelector;

public class InfuseActionPresenterSelector extends PresenterSelector {
    private final Presenter mPrimaryPresenter;
    private final Presenter mIconPresenter;

    public InfuseActionPresenterSelector(Presenter primaryPresenter, Presenter iconPresenter) {
        mPrimaryPresenter = primaryPresenter;
        mIconPresenter = iconPresenter;
    }

    @Override
    public Presenter getPresenter(Object item) {
        if (item instanceof Action) {
            Action action = (Action) item;
            long id = action.getId();
            // Primary pills: Play/Resume, Play from beginning, Next episode
            if (id == VideoActionAdapter.ACTION_PLAY || 
                id == VideoActionAdapter.ACTION_RESUME || 
                id == VideoActionAdapter.ACTION_LOCAL_RESUME || 
                id == VideoActionAdapter.ACTION_REMOTE_RESUME ||
                id == VideoActionAdapter.ACTION_PLAY_FROM_BEGIN ||
                id == VideoActionAdapter.ACTION_NEXT_EPISODE ||
                id == VideoActionAdapter.ACTION_LIST_EPISODES) {
                return mPrimaryPresenter;
            }
            return mIconPresenter;
        }
        return mPrimaryPresenter;
    }
    
    @Override
    public Presenter[] getPresenters() {
        return new Presenter[]{mPrimaryPresenter, mIconPresenter};
    }
}