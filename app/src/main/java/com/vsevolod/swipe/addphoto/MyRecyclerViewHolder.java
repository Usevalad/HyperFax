package com.vsevolod.swipe.addphoto;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by vsevolod on 13.03.17.
 */

public class MyRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final String extra = "photo uri";
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
        mImageView.setOnClickListener(this);
        mPathTextView = (TextView) itemView.findViewById(R.id.path_text_view);
        mDateTextView = (TextView) itemView.findViewById(R.id.date_text_view);
    }

    @Override
    public void onClick(View v) {
        String photoUri = MyRecyclerAdapter.data.get(getAdapterPosition()).getPhotoURI();
        Intent intent = new Intent(context, FullscreenActivity.class);
        intent.putExtra(extra, photoUri);
        context.startActivity(intent);

    }
}
