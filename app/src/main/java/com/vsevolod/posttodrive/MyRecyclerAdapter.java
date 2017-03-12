package com.vsevolod.posttodrive;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;

/**
 * Created by vsevolod on 07.03.17.
 */

public class MyRecyclerAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<Model> data;

    public MyRecyclerAdapter(Context context, List<Model> data) {
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
        Picasso.with(context).load(data.get(position).getPhoto()).
                placeholder(R.drawable.test).
                into(((MyRecyclerViewHolder) holder).mImageView);
        ((MyRecyclerViewHolder) holder).mDateTextView.setText(data.get(position).getDate());
        ((MyRecyclerViewHolder) holder).mPathTextView.setText(data.get(position).getPath());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


}
