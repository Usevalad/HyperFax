package com.vsevolod.swipe.addphoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;

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
        Bitmap bitmap = BitmapFactory.decodeByteArray(data.get(position).getPhoto(), 0, data.get(position)
                .getPhoto().length);

        ((MyRecyclerViewHolder) holder).mImageView.setImageBitmap(bitmap);
        ((MyRecyclerViewHolder) holder).mDateTextView.setText(data.get(position).getDate());
        ((MyRecyclerViewHolder) holder).mPathTextView.setText(data.get(position).getPath());
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount");
        return data.size();
    }


}