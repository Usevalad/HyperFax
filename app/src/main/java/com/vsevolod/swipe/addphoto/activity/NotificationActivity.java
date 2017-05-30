package com.vsevolod.swipe.addphoto.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.vsevolod.swipe.addphoto.R;

public class NotificationActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private TextView mNotificationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = (Toolbar) findViewById(R.id.adding_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.ic_launcher);
        mNotificationTextView = (TextView) findViewById(R.id.notify_text_view);
        String text = getIntent().getStringExtra("notify");
        mNotificationTextView.setText(text);
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "onBackPressed");
        finish();
    }
}
