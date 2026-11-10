package com.archos.mediacenter.video.leanback.settings;

import android.content.Intent;
import android.os.Bundle;

import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.leanback.LeanbackActivity;
import com.archos.mediacenter.video.utils.ThemeManager;

public class VideoSettingsActivity extends LeanbackActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Apply black theme variant for leanback preferences when in black theme
        // BEGIN PULCIX CUSTOM THEME
        ThemeManager themeManager = ThemeManager.getInstance(this);
        if (themeManager.isInfuseTheme()) {
            setTheme(R.style.MyLeanbackTheme_Preferences_Infuse);
        } else if (themeManager.isBlackTheme()) {
            setTheme(R.style.MyLeanbackTheme_Preferences_Black);
        }
        // END PULCIX CUSTOM THEME
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_settings);
        overridePendingTransition(R.anim.slide_in_from_right, 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // FIXME
        overridePendingTransition(0, R.anim.slide_out_to_right);
    }

    private int getResultCode() {
        // This is a workaround to get the current result code
        // since there's no public API for it
        return 0; // We can't easily get this, so we'll log differently
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getSupportFragmentManager().findFragmentById(R.id.settingsFragment) != null) {
            ((VideoSettingsFragment) getSupportFragmentManager().findFragmentById(R.id.settingsFragment)).onActivityResult(requestCode, resultCode, data);
        }
    }
}
