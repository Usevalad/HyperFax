package com.vsevolod.posttodrive;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by vsevolod on 07.03.17.
 */

public class MyRecyclerViewHolder extends RecyclerView.ViewHolder {
    TextView mDateTextView;
    TextView mPathTextView;
    ImageView mImageView;
    CardView mCardView;
    Context context;

    public MyRecyclerViewHolder(final Context context, View itemView) {
        super(itemView);
        this.context = context;
        mCardView = (CardView) itemView.findViewById(R.id.my_card_view);
        mCardView.setOnCreateContextMenuListener((MainActivity) context);
        mImageView = (ImageView) itemView.findViewById(R.id.photo);
        mPathTextView = (TextView) itemView.findViewById(R.id.path_text_view);
        mDateTextView = (TextView) itemView.findViewById(R.id.date_text_view);
    }
}
