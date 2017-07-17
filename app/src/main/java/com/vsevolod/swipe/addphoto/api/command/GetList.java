package com.vsevolod.swipe.addphoto.api.command;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;
import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.config.RealmHelper;
import com.vsevolod.swipe.addphoto.constant.Constants;
import com.vsevolod.swipe.addphoto.model.query.ListQueryModel;
import com.vsevolod.swipe.addphoto.model.responce.ListResponse;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

/**
 * Created by vsevolod on 7/17/17.
 */

public class GetList implements Api {
    private final String TAG = getClass().getSimpleName();
    private Context mContext;
    private RealmHelper mRealmHelper;
    private String mToken;

    public GetList(Context context, RealmHelper realmHelper, String token) {
        this.mContext = context;
        this.mRealmHelper = realmHelper;
        this.mToken = token;
    }

    @Override
    public void execute() {
        Log.e(TAG, "execute");
        try {
            ListQueryModel listQueryModel = new ListQueryModel(mToken, mRealmHelper.getNotSyncedDataStatesIds());
            Response<ListResponse> response;
            try {
                response = MyApplication.getApi().getList(listQueryModel).execute();
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                Toast.makeText(mContext, "Ошибка соединения с сервером", Toast.LENGTH_SHORT).show();
                return;
            }

            if (response.isSuccessful() &&
                    TextUtils.equals(response.body().getStatus(), Constants.RESPONSE_STATUS_OK)) {
                List<String> ids = response.body().getIds();
                List<String> states = response.body().getStateCodes();
                List<String> comments = response.body().getComments();
                Log.e(TAG, "getStateCodesFromServer: ids.size " + ids.size());
                Log.e(TAG, "getStateCodesFromServer: states.size " + states.size());
                Log.e(TAG, "getStateCodesFromServer: comments.size " + comments.size());
                for (int i = 0; i < states.size(); i++) {
                    if (!mRealmHelper.isStateCodeEqual(ids.get(i), states.get(i))) {
                        mRealmHelper.setField(ids.get(i), mRealmHelper.STATE_CODE, states.get(i), false);
                        Log.e(TAG, "onResponse: id = " + ids.get(i));
                        Log.e(TAG, "onResponse: state = " + states.get(i));
                    }
                    if (!TextUtils.isEmpty(comments.get(i))) {
                        mRealmHelper.setField(ids.get(i), mRealmHelper.COMMENT, comments.get(i), false);
                        Log.e(TAG, "onResponse: comment = " + comments.get(i));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
