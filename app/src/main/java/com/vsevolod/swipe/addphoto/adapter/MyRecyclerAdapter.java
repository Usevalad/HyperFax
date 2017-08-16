package com.vsevolod.swipe.addphoto.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.activity.FullscreenActivity;
import com.vsevolod.swipe.addphoto.constant.Constants;
import com.vsevolod.swipe.addphoto.constant.IntentKey;
import com.vsevolod.swipe.addphoto.model.realm.DataModel;

import java.util.List;

import static com.vsevolod.swipe.addphoto.util.MyTextUtil.highLightMatches;
import static com.vsevolod.swipe.addphoto.util.MyTextUtil.toBold;

/**
 * Created by vsevolod on 13.03.17.
 */

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyRecyclerViewHolder> {
    private final String TAG = this.getClass().getSimpleName();
    private Context context;
    public List<DataModel> data;
    private String mSearchString;
    private DeletePostCallback mDeletePostCallback;

    public MyRecyclerAdapter(Context context, @NonNull List<DataModel> data,
                             DeletePostCallback callback) {
        Log.d(TAG, "MyRecyclerAdapter: constructor");
        this.context = context;
        this.data = data;
        this.mDeletePostCallback = callback;
    }

    public MyRecyclerAdapter(Context context, @NonNull List<DataModel> data, String searchString,
                             DeletePostCallback callback) {
        Log.d(TAG, "MyRecyclerAdapter: constructor");
        this.context = context;
        this.data = data;
        this.mSearchString = searchString;
        this.mDeletePostCallback = callback;
    }

    @Override
    public MyRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.recycler_view_item, parent, false);
        return new MyRecyclerViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(MyRecyclerViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");
        DataModel model = data.get(position);
        byte[] photoByteArray = model.getPhoto();
        Bitmap bitmap = BitmapFactory.decodeByteArray(photoByteArray, 0, photoByteArray.length);
        holder.mPhotoImageView.setImageBitmap(bitmap);
        holder.mStateIconImageView.setImageResource(model.getStateIconImage());
        holder.mDateTextView.setText(highLightMatches(model.getViewDate(), mSearchString));
        holder.mDateTextView.setContentDescription(model.getViewDate());
        holder.mPathTextView.setText(TextUtils.concat(
                toBold("статья: "),
                highLightMatches(model.getViewArticle() + " " + model.getPrefix(), mSearchString)));
        holder.mPathTextView.setContentDescription("статья: " + model.getViewArticle() + " " + model.getPrefix());

        if (TextUtils.isEmpty(model.getViewDescription())) {
            holder.mDescriptionTextView.setVisibility(View.GONE);
        } else {
            holder.mDescriptionTextView.setVisibility(View.VISIBLE);
            holder.mDescriptionTextView.setText(TextUtils.concat(
                    toBold("описание: "),
                    highLightMatches(model.getViewDescription(), mSearchString)));
            holder.mDescriptionTextView.setContentDescription("описание: " + model.getViewDescription());
        }
        if (TextUtils.isEmpty(model.getViewComment())) {
            holder.mCommentTextView.setVisibility(View.GONE);
        } else {
            holder.mCommentTextView.setVisibility(View.VISIBLE);
            holder.mCommentTextView.setText(TextUtils.concat(
                    toBold("комментарий: "),
                    highLightMatches(model.getViewComment(), mSearchString)));
            holder.mCommentTextView.setContentDescription("комментарий: " + model.getViewComment());
        }
    }

    @Override
    public void onViewRecycled(MyRecyclerViewHolder holder) {
        holder.itemView.setOnCreateContextMenuListener(null);
        holder.itemView.setOnClickListener(null);
        super.onViewRecycled(holder);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        TextView mDateTextView;
        TextView mPathTextView;
        TextView mDescriptionTextView;
        TextView mCommentTextView;
        ImageView mPhotoImageView;
        ImageView mStateIconImageView;
        Context context;

        private MyRecyclerViewHolder(final Context context, View itemView) {
            super(itemView);
            this.context = context;
            mPhotoImageView = (ImageView) itemView.findViewById(R.id.photo_image_view);
            mStateIconImageView = (ImageView) itemView.findViewById(R.id.icon_state_image_view);
            mPathTextView = (TextView) itemView.findViewById(R.id.path_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.date_text_view);
            mDescriptionTextView = (TextView) itemView.findViewById(R.id.description_text_view);
            mCommentTextView = (TextView) itemView.findViewById(R.id.comment_text_view);
            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            //get titles
            String delete = v.getContext().getString(R.string.delete);
            //set menu items
            MenuItem Delete = menu.add(Menu.NONE, Constants.DELETE_ID, 1, delete);
            //set onClickListener
            Delete.setOnMenuItemClickListener(this);
        }


        @Override
        public void onClick(View v) {
            String serverPhotoURL = data.get(getAdapterPosition()).getServerPhotoURL();
            String storagePhotoURL = data.get(getAdapterPosition()).getStoragePhotoURL();
            Intent intent = new Intent(context, FullscreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(IntentKey.SERVER_PHOTO_URL, serverPhotoURL);
            intent.putExtra(IntentKey.STORAGE_PHOTO_URL, storagePhotoURL);
            context.startActivity(intent);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case Constants.DELETE_ID:
                    int position = getAdapterPosition();
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, data.size());
                    mDeletePostCallback.deletePost(data.get(position));
                    return true;
                default:
                    return false;
            }
        }
    }

    public interface DeletePostCallback {
        void deletePost(DataModel model);
    }
}