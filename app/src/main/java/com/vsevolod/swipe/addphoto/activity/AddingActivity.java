package com.vsevolod.swipe.addphoto.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import com.vsevolod.swipe.addphoto.Model;
import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.config.RealmHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import it.sephiroth.android.library.picasso.Picasso;

public class AddingActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "AddingActivity";
    private final int THUMBSIZE = 500;
    private RealmHelper mRealmHelper;
    private AutoCompleteTextView mAutoCompleteTextView;
    private ImageView mImageView;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        mRealmHelper = new RealmHelper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding);
        path = getIntent().getStringExtra("path");

        mAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.model_adding_auto_complete_text_view);
        mAutoCompleteTextView.requestFocus();
        mImageView = (ImageView) findViewById(R.id.model_adding_image_view);
        findViewById(R.id.send_button).setOnClickListener(this); // Button

        Picasso.with(this)
                .load(path)
                .into(mImageView);


    }

    private void addImage(String path) {
        File imageFile = new File(path);
        if (imageFile.exists()) {

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            //to compress full size photo use commented strings below
//        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            //compress thumbnail
            Bitmap thumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imageFile.getAbsolutePath()),
                    THUMBSIZE, THUMBSIZE);
            thumbImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            addNewDataItem(byteArray, path);
        }
    }

    private void addNewDataItem(@NonNull byte[] byteArray, @NonNull String photoUri) {
        Log.d(TAG, "addNewDataItem");
        Calendar c = Calendar.getInstance();
        SimpleDateFormat simpleDateFormatTV = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy E");
        SimpleDateFormat simpleDateFormatDB = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss E");
        String formattedDateTV = simpleDateFormatTV.format(c.getTime()); //date format for textView
        String formattedDateDB = simpleDateFormatDB.format(c.getTime());
        String photoTag = new StringBuilder()
                .append("@")
                .append(mAutoCompleteTextView.getText().toString())
                .toString();

        Model model = new Model(formattedDateDB, photoTag, photoUri, byteArray);
        mRealmHelper.saveToRealm(model);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_button:
                addImage(path);
                break;
            default:
                break;
        }
    }
}
