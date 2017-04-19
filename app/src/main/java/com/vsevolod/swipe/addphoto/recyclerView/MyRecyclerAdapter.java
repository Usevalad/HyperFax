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

import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.model.realm.DataModel;

import java.util.List;

/**
 * Created by vsevolod on 13.03.17.
 */

public class MyRecyclerAdapter extends RecyclerView.Adapter {
    private final String TAG = "MyRecyclerAdapter";
    private Context context;
    static List<DataModel> data;

    public MyRecyclerAdapter(Context context, @NonNull List<DataModel> data) {
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
        DataModel model = data.get(position);
        byte[] photoByteArray = model.getPhoto();
        Bitmap bitmap = BitmapFactory.decodeByteArray(photoByteArray, 0, photoByteArray.length);

        ((MyRecyclerViewHolder) holder).mPhotoImageView.setImageBitmap(bitmap);
        ((MyRecyclerViewHolder) holder).mStateIconImageView.setImageResource(model.getStateIconImage());
        ((MyRecyclerViewHolder) holder).mDateTextView.setText(model.getViewDate());
        ((MyRecyclerViewHolder) holder).mPathTextView.setText(model.getName() + " @" + model.getPrefix());
        ((MyRecyclerViewHolder) holder).mComment.setText(model.getComment());
        ((MyRecyclerViewHolder) holder).mLocation.setText("lat: " +
                String.valueOf(model.getLatitude()) + " long: " + String.valueOf(model.getLongitude()));
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount");
        return data.size();
    }
}