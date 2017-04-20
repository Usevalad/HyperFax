package com.vsevolod.swipe.addphoto.recyclerView;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.activity.FullscreenActivity;

/**
 * Created by vsevolod on 13.03.17.
 */

public class MyRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final String PHOTO_URI = "photo uri";
    private final String PHOTO_URL = "photo url";
    TextView mDateTextView;
    TextView mPathTextView;
    TextView mComment;
    TextView mLocation;
    ImageView mPhotoImageView;
    ImageView mStateIconImageView;
    CardView mCardView;
    Context context;

    public MyRecyclerViewHolder(final Context context, View itemView) {
        super(itemView);
        this.context = context;
        mCardView = (CardView) itemView.findViewById(R.id.my_card_view);
        mPhotoImageView = (ImageView) itemView.findViewById(R.id.photo_image_view);
        mPhotoImageView.setOnClickListener(this);
        mStateIconImageView = (ImageView) itemView.findViewById(R.id.icon_state_image_view);
        mPathTextView = (TextView) itemView.findViewById(R.id.path_text_view);
        mDateTextView = (TextView) itemView.findViewById(R.id.date_text_view);
        mComment = (TextView) itemView.findViewById(R.id.comment_text_view);
        mLocation = (TextView) itemView.findViewById(R.id.location_text_view);
    }

    @Override
    public void onClick(View v) {
        String photoUri = MyRecyclerAdapter.data.get(getAdapterPosition()).getPhotoURI();
        String photoUrl = MyRecyclerAdapter.data.get(getAdapterPosition()).getServerPhotoURL();
        Intent intent = new Intent(context, FullscreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PHOTO_URI, photoUri);
        intent.putExtra(PHOTO_URL, photoUrl);
        context.startActivity(intent);
    }
}
