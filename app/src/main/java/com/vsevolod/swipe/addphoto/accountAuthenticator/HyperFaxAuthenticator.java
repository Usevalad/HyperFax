package com.vsevolod.swipe.addphoto.accountAuthenticator;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.vsevolod.swipe.addphoto.activity.LoginActivity;
import com.vsevolod.swipe.addphoto.config.Constants;
import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.config.PreferenceHelper;
import com.vsevolod.swipe.addphoto.model.query.AuthModel;
import com.vsevolod.swipe.addphoto.model.responce.UserModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vsevolod on 08.05.17.
 */

public class HyperFaxAuthenticator extends AbstractAccountAuthenticator {
    private final String TAG = HyperFaxAuthenticator.class.getSimpleName();
    private Context mContext; // TODO: 10.05.17 remove context
    private String mAuthToken;
    private PreferenceHelper mPreferenceHelper = new PreferenceHelper();

    public HyperFaxAuthenticator(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
                             String authTokenType, String[] requiredFeatures, Bundle options)
            throws NetworkErrorException {
        Log.d(TAG, "addAccount");
        final Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra(Constants.EXTRA_TOKEN_TYPE, accountType);
        intent.putExtra(Constants.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(Constants.ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        if (options != null) {
            bundle.putAll(options);
        }
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options)
            throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
                               String authTokenType, Bundle options) throws NetworkErrorException {
        Log.d(TAG, "getAuthToken");

//        if (!authTokenType.equals(AccountGeneral.AUTHTOKEN_TYPE_READ_ONLY)
//                && !authTokenType.equals(AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS)) {
//            final Bundle result = new Bundle();
//            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
//            return result;
//        }

        final Bundle result = new Bundle();
        final AccountManager am = AccountManager.get(MyApplication.getAppContext());
        mAuthToken = am.peekAuthToken(account, authTokenType);
        if (TextUtils.isEmpty(mAuthToken)) {
            final String password = am.getPassword(account);
            final String phoneNumber = am.getUserData(account, "phoneNumber");// FIXME: 10.05.17 hardcode! add to constants
            if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(phoneNumber)) {
                final AuthModel user = new AuthModel(phoneNumber, password);
                MyApplication.getApi().authenticate(user).enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        Log.d(TAG, "onResponse");
                        if (response.body() != null) {
                            Log.d(TAG, "onResponse: body != null");
                            switch (response.body().getStatus()) {
                                case Constants.RESPONSE_STATUS_AUTH:
                                    Log.e(TAG, "onResponse: need auth");
//                                    startLoginActivity();
                                    break;
                                case Constants.RESPONSE_STATUS_PARAM:
                                    Log.e(TAG, "onResponse: неверный набор параметров");
                                    //empty token
//                                    startLoginActivity();
                                    break;
                                case Constants.RESPONSE_STATUS_FAIL:
                                    Log.e(TAG, "onResponse: сбой обработки задачи по внешней причине");
                                    break;
                                case Constants.RESPONSE_STATUS_OK: // here i need to understand what was the
                                    // call model to know how to react
                                    Log.e(TAG, "onResponse: выполнено успешно, ождиается корректный " +
                                            "протокол выдачи конкретной задачи");
                                    mAuthToken = response.body().getToken().toString();
                                    mPreferenceHelper.saveString(PreferenceHelper.APP_PREFERENCES_TOKEN, mAuthToken);
                                    // TODO: 10.05.17 delete prefHelper?
                                    // FIXME: 15.04.17  token = not found
//                                    startMainActivity();
                                    break;
                                case Constants.RESPONSE_STATUS_BAD:
                                    Log.e(TAG, "onResponse: задача не поддерживается сервером");
                                    break;
                                case Constants.RESPONSE_STATUS_INIT:
                                    Log.e(TAG, "onResponse: проблема на сервере или неверные JSON-данные в POST");
                                    break;
                                case Constants.RESPONSE_STATUS_DIE:
                                    Log.e(TAG, "onResponse: задача внезапно умерла при обработке");
                                    break;
                                default:
                                    Log.e(TAG, "onResponse: default");
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
                Log.d(TAG, "authenticate");
            }
        }

        if (!TextUtils.isEmpty(mAuthToken)) {
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, mAuthToken);
        } else {
            final Intent intent = new Intent(mContext, LoginActivity.class);
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
            intent.putExtra(Constants.EXTRA_TOKEN_TYPE, authTokenType);
            final Bundle bundle = new Bundle();
            bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        }
        return result;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        throw new UnsupportedOperationException();
    }
}