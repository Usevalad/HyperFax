package com.vsevolod.swipe.addphoto.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.activity.FullscreenActivity;
import com.vsevolod.swipe.addphoto.model.realm.DataModel;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by vsevolod on 13.03.17.
 */

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyRecyclerViewHolder> {
    private final String TAG = this.getClass().getSimpleName();
    private Context context;
    public static List<DataModel> data;

    public MyRecyclerAdapter(Context context, @NonNull List<DataModel> data) {
        Log.d(TAG, "MyRecyclerAdapter: constructor");
        this.context = context;
        this.data = data;
    }

    @Override
    public MyRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.recycler_view_item, parent, false);
        return new MyRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyRecyclerViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");
        DataModel model = data.get(position);
        byte[] photoByteArray = model.getPhoto();
        Bitmap bitmap = BitmapFactory.decodeByteArray(photoByteArray, 0, photoByteArray.length);

        SimpleDateFormat viewDateFormat = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy E");
        String viewDate = viewDateFormat.format(model.getDate()); //date format for textView
        holder.mPhotoImageView.setImageBitmap(bitmap);
        holder.mStateIconImageView.setImageResource(model.getStateIconImage());
        holder.mDateTextView.setText(viewDate);
        holder.mPathTextView.setText(model.getName() + " @" + model.getPrefix());
        holder.mComment.setText(model.getComment());
        holder.mLocation.setText("lat: " +
                String.valueOf(model.getLatitude()) + " long: " + String.valueOf(model.getLongitude()));
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount");
        return data.size();
    }

    class MyRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final String PHOTO_URL = "photo url";// FIXME: 11.05.17 hardcode
        TextView mDateTextView;
        TextView mPathTextView;
        TextView mComment;
        TextView mLocation;
        ImageView mPhotoImageView;
        ImageView mStateIconImageView;
        CardView mCardView;
        Context context;

        private MyRecyclerViewHolder(View itemView) {
            super(itemView);
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
            String photoUri = data.get(getAdapterPosition()).getPhotoURL();
            Intent intent = new Intent(context, FullscreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(PHOTO_URL, photoUri);
            context.startActivity(intent);
        }
    }
}