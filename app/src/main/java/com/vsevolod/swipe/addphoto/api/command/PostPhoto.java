package com.vsevolod.swipe.addphoto.api.command;

import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.vsevolod.swipe.addphoto.accountAuthenticator.AccountGeneral;
import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.config.RealmHelper;
import com.vsevolod.swipe.addphoto.constant.Constants;
import com.vsevolod.swipe.addphoto.model.query.CommitModel;
import com.vsevolod.swipe.addphoto.model.realm.DataModel;
import com.vsevolod.swipe.addphoto.model.responce.CommitResponseModel;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by vsevolod on 7/17/17.
 */

public class PostPhoto implements Api {
    private final String TAG = getClass().getSimpleName();
    private Context mContext;
    private RealmHelper mRealmHelper;
    private String mToken;
    private List<DataModel> mDataModel;

    public PostPhoto(Context context, RealmHelper realmHelper, String token, List<DataModel> dataModel) {
        this.mContext = context;
        this.mRealmHelper = realmHelper;
        this.mToken = token;
        this.mDataModel = dataModel;
    }

    @Override
    public void execute() {
        try {
            for (int i = 0; i < mDataModel.size(); i++) {
                DataModel dataModel = mDataModel.get(i);
                File imageFile = new File(dataModel.getStoragePhotoURL());
                RequestBody requestBody = RequestBody.create(MediaType.parse(Constants.MEDIA_TYPE_IMAGE), imageFile);
                Response<ResponseBody> postImageResponse = MyApplication.getApi().postImage(requestBody).execute();
                Log.e(TAG, "uploadData: response code " + String.valueOf(postImageResponse.code()));
                Log.e(TAG, "uploadData: response body " + String.valueOf(postImageResponse.body()));
                Log.e(TAG, "uploadData: response errorBody " + String.valueOf(postImageResponse.errorBody()));
                Log.e(TAG, "uploadData: response headers " + String.valueOf(postImageResponse.headers()));
                Log.e(TAG, "uploadData: response message " + String.valueOf(postImageResponse.message()));
                Log.e(TAG, "uploadData: response raw " + String.valueOf(postImageResponse.raw()));
                String link = postImageResponse.body().string();
                String id = dataModel.getUid();
                Log.e(TAG, "uploadData: link" + link);
                Log.e(TAG, "uploadData: id" + id);
                CommitModel commitModel = new CommitModel(
                        mToken,
                        link,
                        dataModel.getUid(),
                        dataModel.getPrefixID(),
                        dataModel.getDescription(),
                        dataModel.getSearchDate(),
                        String.valueOf(dataModel.getLatitude() + "," + dataModel.getLongitude())
                );

                Response<CommitResponseModel> commitResponse = MyApplication.getApi().commit(commitModel).execute();
                Log.e(TAG, "commit : commitResponse.status :" + commitResponse.body().getStatus());
                Log.e(TAG, "commit : commitResponse.code() " + commitResponse.code());
                Log.e(TAG, "commit : commitResponse.log() " + commitResponse.body().getLog());
                switch (commitResponse.body().getStatus()) {
                    case Constants.RESPONSE_STATUS_PARAM:
                        mRealmHelper.setField(id, mRealmHelper.STATE_CODE, Constants.DATA_MODEL_STATE_PARAM, false);
                        mRealmHelper.setField(id, mRealmHelper.IS_SYNCED, null, true);
                        break;
                    case Constants.RESPONSE_STATUS_OK:
                        mRealmHelper.setField(id, mRealmHelper.SERVER_PHOTO_URL, link, false);
                        mRealmHelper.setField(id, mRealmHelper.IS_SYNCED, null, true);
                        mRealmHelper.setField(id, mRealmHelper.STATE_CODE, Constants.DATA_MODEL_STATE_CREATED, false);
                        break;
                    case Constants.RESPONSE_STATUS_AUTH:
                        AccountGeneral.cancelPeriodicSync(mContext);
                        AccountGeneral.removeAccount(mContext, AccountManager.get(mContext));
                        break;
                    default:
                        AccountGeneral.sync();
                        FirebaseCrash.log("Запуск синхронизации после попытки загрузить фото");
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
