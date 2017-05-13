package com.vsevolod.swipe.addphoto.accountAuthenticator;

import android.content.Context;

import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.config.MyApplication;

/**
 * Created by vsevolod on 12.05.17.
 */

public class AccountGeneral {
    private static final Context mContext = MyApplication.getAppContext();
    public static final String ARG_ACCOUNT_TYPE = mContext.getResources().getString(R.string.account_type);
    public static final String ARG_TOKEN_TYPE = "com.vsevolod.swipe.addphoto.EXTRA_TOKEN_TYPE";
    public static final String ARG_AUTH_TYPE = "AUTH_TYPE";
    public static final String ARG_ACCOUNT_NAME = "Hyper Fax"; // TODO: 11.05.17 add user name here
    public static final String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";
    public static final String PARAM_USER_PASS = "USER_PASS";
    public static final String KEY_ACCOUNT_PHONE_NUMBER = "com.vsevolod.swipe.addphoto.phoneNumber";
}