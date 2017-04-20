package com.vsevolod.swipe.addphoto.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.view.TouchImageView;

import java.io.File;

import it.sephiroth.android.library.picasso.Picasso;

public class FullscreenActivity extends AppCompatActivity implements View.OnClickListener {
    private final String PHOTO_URI = "photo uri";
    private final String PHOTO_URL = "photo url";
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private TouchImageView mTouchImageView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mTouchImageView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mTransparentPanel;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            mTransparentPanel.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible = true;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        mTransparentPanel = findViewById(R.id.fullscreen_content_controls);
        mTouchImageView = (TouchImageView) findViewById(R.id.fullscreen_content);
        mTouchImageView.setImageResource(R.drawable.test);
        mTouchImageView.setOnClickListener(this);
        findViewById(R.id.full_screen_back_button).setOnClickListener(this);
        String photoURI = getIntent().getStringExtra(PHOTO_URI);
        String photoURL = getIntent().getStringExtra(PHOTO_URL);
        if (photoURL != null) {
            Picasso.with(this)
                    .load(photoURL)
                    .into(mTouchImageView);
        } else {
            setImageFromStorage(photoURI);
        }
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, 0);
    }

    private void setImageFromStorage(String path) {
        File imageFile = new File(path);
        if (imageFile.exists()) {
            Picasso.with(this)
                    .load(imageFile)
                    .into(mTouchImageView);
        } else {
            mTouchImageView.setImageResource(R.drawable.test);
        }
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        mTransparentPanel.setVisibility(View.GONE);
        mVisible = false;
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system status bar
        mTouchImageView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.full_screen_back_button:
                finish();
                break;
            case R.id.fullscreen_content:
                toggle();
            default:
                break;
        }
    }
}
