package com.vsevolod.swipe.addphoto.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.config.Constants;

import java.io.File;

import it.sephiroth.android.library.picasso.Picasso;

public class FullscreenActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = this.getClass().getSimpleName();
    private ImageView mTouchImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        Log.e(TAG, "onCreate");
        View mTransparentPanel = findViewById(R.id.transparent_panel);
        mTouchImageView = (ImageView) findViewById(R.id.full_screen_image_view);
        mTouchImageView.setOnTouchListener(new ImageMatrixTouchHandler(this));
        findViewById(R.id.full_screen_back_button).setOnClickListener(this);
        setImage();
    }

    private void setImage() {
        Log.e(TAG, "setImage");
        File image = new File(getIntent().getStringExtra(Constants.INTENT_KEY_STORAGE_PHOTO_URL));
        String serverPhotoURL = getIntent().getStringExtra(Constants.INTENT_KEY_SERVER_PHOTO_URL);
        if (image.exists()) {
            Log.e(TAG, "STORAGE_PHOTO_URL");
            mTouchImageView.setImageURI(Uri.parse(getIntent()
                    .getStringExtra(Constants.INTENT_KEY_STORAGE_PHOTO_URL)));
        } else {
            Log.e(TAG, "SERVER_PHOTO_URL");
            Picasso.with(this)
                    .load(serverPhotoURL)
                    .into(mTouchImageView);
        }
    }

    @Override
    protected void onDestroy() {
        mTouchImageView.setOnTouchListener(null);
        findViewById(R.id.full_screen_back_button).setOnClickListener(null);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.full_screen_back_button:
                finish();
                break;
            default:
                break;
        }
    }
}