package com.vsevolod.swipe.addphoto.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.vsevolod.swipe.addphoto.config.MyApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static com.vsevolod.swipe.addphoto.config.Constants.THUMB_SIZE;

/**
 * Created by vsevolod on 23.06.17.
 */

public class ImageConverter {
    private static final String TAG = "ImageConverter";

    public static byte[] imageToByte(String path) {
        File imageFile = new File(path);
        byte[] byteArray = null;
        if (imageFile.exists()) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap thumbImage = ThumbnailUtils.extractThumbnail(
                    BitmapFactory.decodeFile(imageFile.getAbsolutePath()), THUMB_SIZE, THUMB_SIZE);
            int imageQuality = 40;
            thumbImage.compress(Bitmap.CompressFormat.JPEG, imageQuality, stream);
            byteArray = stream.toByteArray();

            try {
                thumbImage.recycle();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
                FirebaseCrash.log(TAG + " " + e.getMessage());
            }
        } else {
            Log.e(TAG, "addImage: file is not exist");
            Toast.makeText(MyApplication.getAppContext(), "Не правильный путь к файлу", Toast.LENGTH_SHORT).show();
        }

        return byteArray;
    }
}
