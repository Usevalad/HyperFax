package com.vsevolod.swipe.addphoto.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Filter;
import android.widget.ImageView;

import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.model.realm.DataModel;

import java.io.File;

import it.sephiroth.android.library.picasso.Picasso;

/**
 * Created by Student Vsevolod on 16.08.17.
 * usevalad.uladzimiravich@gmail.com
 */

public class PicassoClient {
    public static void showResizedImage(ImageView imageView, DataModel model) {
        //approximate size
        final int HEIGHT = 600;
        final int WIDTH = 600;
        //context
        Context context = imageView.getContext();
        File image = new File(model.getStoragePhotoURL());

        if (image.exists()) {
            Picasso.with(context)
                    .load(model.getStoragePhotoURL())
                    .placeholder(R.drawable.ic_toolbar_logo)
                    .resize(WIDTH, HEIGHT)
                    .centerCrop()
                    .into(imageView);
        } else {
            Picasso.with(context)
                    .load(model.getServerPhotoURL())
                    .resize(WIDTH, HEIGHT)
                    .placeholder(R.drawable.login_background)
                    .centerCrop()
                    .into(imageView);
        }
    }
}