package com.vsevolod.swipe.addphoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Created by vsevolod on 13.03.17.
 */


public class MyRecyclerAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<Model> data;

    public MyRecyclerAdapter(Context context, @NonNull List<Model> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.recycler_view_item, parent, false);
        return new MyRecyclerViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data.get(position).getPhoto(), 0, data.get(position)
                .getPhoto().length);
        ((MyRecyclerViewHolder) holder).mImageView.setImageBitmap(bitmap);
        ((MyRecyclerViewHolder) holder).mDateTextView.setText(data.get(position).getDate());
        ((MyRecyclerViewHolder) holder).mPathTextView.setText(data.get(position).getPath());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


}