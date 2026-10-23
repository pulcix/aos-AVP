// Copyright 2017 Archos SA
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.archos.mediacenter.video.leanback.presenter;

import androidx.leanback.widget.BaseCardView;
import androidx.leanback.widget.Presenter;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.leanback.adapter.object.Box;
import com.archos.mediacenter.video.utils.ThemeManager;

/**
 * Created by vapillon on 10/04/15.
 */
public class BoxItemPresenter extends Presenter {

    protected class BoxViewHolder extends ViewHolder {
        ViewGroup mRoot;
        BaseCardView mCard;
        ViewGroup mImageViewContainer;
        ImageView mImageView;
        TextView mTextView;

        public BoxViewHolder(ViewGroup parent) {
            super(new BaseCardView(parent.getContext()));
            mCard = (BaseCardView)view;
            mCard.setFocusable(true);
            mCard.setFocusableInTouchMode(true);

            mRoot = (ViewGroup)LayoutInflater.from(parent.getContext()).inflate(R.layout.leanback_box, mCard, false);
            mImageViewContainer = (ViewGroup)mRoot.findViewById(R.id.image_container);
            mImageView = (ImageView)mImageViewContainer.findViewById(R.id.image);
            mTextView = (TextView)mRoot.findViewById(R.id.name);

            BaseCardView.LayoutParams lp = new BaseCardView.LayoutParams(mRoot.getLayoutParams());
            lp.viewType = BaseCardView.LayoutParams.VIEW_TYPE_MAIN;
            mCard.addView(mRoot);
        }
        ImageView getImageView() {
            return mImageView;
        }
        TextView getTextView() {
            return mTextView;
        }
    }

    @Override
    public BoxViewHolder onCreateViewHolder(ViewGroup parent) {
        return new BoxViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        final BoxViewHolder vh = (BoxViewHolder)viewHolder;
        Box box = (Box)item;
        
        // BEGIN PULCIX CUSTOM THEME
        boolean isInfuseTheme = ThemeManager.getInstance(vh.mCard.getContext()).isInfuseTheme();

        vh.getTextView().setText(box.getName());

        int customDrawable = 0;
        if (isInfuseTheme && box.getBoxId() != null) {
            switch (box.getBoxId()) {
                case ALL_MOVIES:
                    customDrawable = R.drawable.infuse_cover_all_movies;
                    break;
                case ALL_ANIMES:
                    customDrawable = R.drawable.infuse_cover_all_animes;
                    break;
                case ALL_TVSHOWS:
                case ALL_ANIMESHOWS:
                    customDrawable = R.drawable.infuse_cover_tv_shows;
                    break;
                case MOVIES_BY_GENRE:
                case TVSHOWS_BY_GENRE:
                case ANIMES_BY_GENRE:
                    customDrawable = R.drawable.infuse_cover_genre;
                    break;
                case MOVIES_BY_YEAR:
                case ANIMES_BY_YEAR:
                case EPISODES_BY_DATE:
                    customDrawable = R.drawable.infuse_cover_year;
                    break;
                case COLLECTIONS:
                case ANIME_COLLECTIONS:
                    customDrawable = R.drawable.infuse_cover_collections;
                    break;
                // Network & Files row: restore original Nova icons (do not override)
                case NETWORK:
                case FTP:
                case USB:
                case SDCARD:
                case FOLDERS:
                case NON_SCRAPED_VIDEOS:
                    customDrawable = 0;
                    break;
            }
        }

        if (customDrawable != 0) {
            vh.getImageView().setImageResource(customDrawable);
            vh.getImageView().setScaleType(ImageView.ScaleType.FIT_XY); // Expand to fill 16:9 box correctly!
            vh.mImageViewContainer.setBackgroundColor(android.graphics.Color.TRANSPARENT);
            vh.mImageViewContainer.setBackground(null);
            
            // Applica l'aspect ratio 200x130dp programmato
            ViewGroup.LayoutParams lp = vh.mRoot.getLayoutParams();
            if (lp != null) {
                float density = vh.mRoot.getResources().getDisplayMetrics().density;
                lp.width = (int) (200 * density);
                lp.height = (int) (130 * density);
                vh.mRoot.setLayoutParams(lp);
            }
        } else {
            vh.mImageViewContainer.setBackgroundColor(ThemeManager.getInstance(vh.mCard.getContext()).getLeanbackHeaderColor());
            if (box.getBitmap() != null) {
                vh.getImageView().setImageBitmap(box.getBitmap());
            } else {
                vh.getImageView().setImageResource(box.getIconResId());
                vh.getImageView().setScaleType(ImageView.ScaleType.CENTER);
            }
            // Ripristina dimensioni originali per tema classico (preso da R.dimen.box_width e box_height)
            ViewGroup.LayoutParams lp = vh.mRoot.getLayoutParams();
            if (lp != null) {
                lp.width = vh.mRoot.getResources().getDimensionPixelSize(R.dimen.box_width);
                lp.height = vh.mRoot.getResources().getDimensionPixelSize(R.dimen.box_height);
                vh.mRoot.setLayoutParams(lp);
            }
        }

        if (isInfuseTheme) {
            InfuseCardStyler.applyInfuseCardStyle(vh.mCard);
        }
        // END PULCIX CUSTOM THEME
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
    }
}
