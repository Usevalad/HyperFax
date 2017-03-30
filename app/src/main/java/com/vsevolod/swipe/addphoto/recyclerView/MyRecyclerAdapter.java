package com.vsevolod.swipe.addphoto.recyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vsevolod.swipe.addphoto.Model;
import com.vsevolod.swipe.addphoto.R;

import java.util.List;

/**
 * Created by vsevolod on 13.03.17.
 */


public class MyRecyclerAdapter extends RecyclerView.Adapter {
    private final String TAG = "MyRecyclerAdapter";
    private Context context;
    static List<Model> data;

    public MyRecyclerAdapter(Context context, @NonNull List<Model> data) {
        Log.d(TAG, "MyRecyclerAdapter: constructor");
        this.context = context;
        this.data = data;
        Log.e(TAG, "MyRecyclerAdapter: wtf");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.recycler_view_item, parent, false);
        return new MyRecyclerViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");
        Model model = data.get(position);
        byte[] photoByteArray = model.getPhoto();
        Bitmap bitmap = BitmapFactory.decodeByteArray(photoByteArray, 0, photoByteArray.length);

        ((MyRecyclerViewHolder) holder).mPhotoImageView.setImageBitmap(bitmap);
        ((MyRecyclerViewHolder) holder).mStateIconImageView.setImageResource(model.getStateIconImage());
        ((MyRecyclerViewHolder) holder).mDateTextView.setText(model.getDate());
        ((MyRecyclerViewHolder) holder).mPathTextView.setText(model.getPath());
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount");
        return data.size();
    }

}