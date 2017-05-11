package com.vsevolod.swipe.addphoto.activity;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.haha.perflib.Main;
import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.model.query.AuthModel;
import com.vsevolod.swipe.addphoto.model.responce.UserModel;

import java.io.IOException;

import retrofit2.Response;

/**
 * A login screen that offers login via phone number/password.
 */
// FIXME: 21.04.17 refactor!!!!
// FIXME: 21.04.17 handle hardware back button onCLick (quit from app)
// TODO: 21.04.17 add phone number finding lib
public class LoginActivity extends AccountAuthenticatorActivity {//implements LoaderCallbacks<Cursor>
    private final String TAG = LoginActivity.class.getSimpleName();
    public final static String ARG_ACCOUNT_TYPE = "com.vsevolod.swipe.addphoto";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "Hyper Fax"; // TODO: 11.05.17 add user name here
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";


    public final static String PARAM_USER_PASS = "USER_PASS";
    //    private static final int REQUEST_READ_CONTACTS = 0;
    public static final String EXTRA_TOKEN_TYPE = "com.vsevolod.swipe.addphoto.EXTRA_TOKEN_TYPE"; // FIXME: 11.05.17 hardcode
    private AsyncTask mAuthTask = null;
    private EditText mPhoneNumberView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
//    private MyasoApi api = new MyasoApi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.

        String phoneNumber = getPhoneNUmber();
        mPhoneNumberView = (EditText) findViewById(R.id.phone_number);
        mPhoneNumberView.setText("+380936622642");//+380506361408 номер телефона максима
//        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setText("user");//admin пароль максима
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() { // TODO: 10.05.17 rename btn
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    @NonNull
    private String getPhoneNUmber() {
        // FIXME: 10.05.17 rename method
        // FIXME: 10.05.17 improve method
        //костыль, который по аккаунтам находит номер телефона
        Log.d(TAG, "getPhoneNUmber");
        AccountManager am = AccountManager.get(this);
        Account[] accounts = am.getAccounts();
        String phoneNumber = "oops";
        for (Account ac : accounts) {
            String acname = ac.name;
            String actype = ac.type;
            String acdescribe = String.valueOf(ac.describeContents());
            // Take your time to look at all available accounts
            if (acname.startsWith("+380")) { //viber acc name is phone number
                phoneNumber = acname;
            }
            System.out.println("Accounts : " + acname + ", " + actype + " describe:" + acdescribe);
        }
        return phoneNumber;
    }

//    private void populateAutoComplete() {
//        Log.d(TAG, "populateAutoComplete");
//        if (!mayRequestContacts()) {
//            return;
//        }
//        getLoaderManager().initLoader(0, null, this);
//    }

//    private boolean mayRequestContacts() {
//        Log.d(TAG, "mayRequestContacts");
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            return true;
//        }
//        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        }
//        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
//            Snackbar.make(mPhoneNumberView, R.string.permission_rationale,
//                    Snackbar.LENGTH_INDEFINITE)
//                    .setAction(android.R.string.ok, new View.OnClickListener() {
//                        @Override
//                        @TargetApi(Build.VERSION_CODES.M)
//                        public void onClick(View v) {
//                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
//                        }
//                    });
//        } else {
//            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
//        }
//        return false;
//    }

    /**
     * Callback received when a permissions request has been completed.
     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        Log.d(TAG, "onRequestPermissionsResult");
//        if (requestCode == REQUEST_READ_CONTACTS) {
//            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                populateAutoComplete();
//            }
//        }
//    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        Log.d(TAG, "attemptLogin");
        if (mAuthTask != null) {
            return;
        }
        // Reset errors.
        mPhoneNumberView.setError(null);
        mPasswordView.setError(null);
        // Store values at the time of the login attempt.
        String phoneNumber = mPhoneNumberView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberView.setError(getString(R.string.error_field_required));
            focusView = mPhoneNumberView;
            cancel = true;
        } else if (!isPhoneNumberValid(phoneNumber)) {
            mPhoneNumberView.setError("Номер не валидный"); // FIXME: 10.05.17 hardcode
            focusView = mPhoneNumberView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new AuthTask();
            String[] s = {phoneNumber, password};
            mAuthTask.execute(s);
        }
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        //TODO: improve validation
        Log.d(TAG, "isPhoneNumberValid");
        return phoneNumber.length() == 13;
    }

    private boolean isPasswordValid(String password) {
        //TODO: improve validation
        Log.d(TAG, "isPasswordValid");
        return password.length() > 3;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        Log.d(TAG, "showProgress");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

//    @Override
//    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
//        Log.d(TAG, "onCreateLoader");
//        return new CursorLoader(this,
//                // Retrieve data rows for the device user's 'profile' contact.
//                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
//                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,
//
//                // Select only email addresses.
//                ContactsContract.Contacts.Data.MIMETYPE +
//                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
//                .CONTENT_ITEM_TYPE},
//
//                // Show primary email addresses first. Note that there won't be
//                // a primary email address if the user hasn't specified one.
//                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
//    }

//    @Override
//    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
//        Log.d(TAG, "onLoadFinished");
//        List<String> emails = new ArrayList<>();
//        cursor.moveToFirst();
//        while (!cursor.isAfterLast()) {
//            emails.add(cursor.getString(ProfileQuery.ADDRESS));
//            cursor.moveToNext();
//        }
//
//        addEmailsToAutoComplete(emails);
//    }

//    @Override
//    public void onLoaderReset(Loader<Cursor> cursorLoader) {
//        Log.d(TAG, "onLoaderReset");
//    }
//
//    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
//        Log.d(TAG, "addEmailsToAutoComplete");
//        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
//        ArrayAdapter<String> adapter =
//                new ArrayAdapter<>(LoginActivity.this,
//                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);
//
////        mPhoneNumberView.setAdapter(adapter);
//    }

//
//    private interface ProfileQuery {
//        String[] PROJECTION = {
//                ContactsContract.CommonDataKinds.Email.ADDRESS,
//                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
//        };
//
//        int ADDRESS = 0;
//        int IS_PRIMARY = 1;
//    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
//    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
//
//        UserLoginTask(String phoneNumber, String password) {
//            Log.d(TAG, "UserLoginTask");
//            PreferenceHelper helper = new PreferenceHelper(); // TODO: 10.05.17 delete prefhelper and save values to accManager
//            helper.saveString(PreferenceHelper.APP_PREFERENCES_PHONE, phoneNumber);
//            helper.saveString(PreferenceHelper.APP_PREFERENCES_PASSWORD, password);
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
//            Log.d(TAG, "doInBackground"); // TODO: 10.05.17 get token via request
////            Authentication auth = new Authentication(api);
////            auth.execute();
//
//            return true;
//        }
//
//        @Override
//        protected void onPostExecute(final Boolean success) {
//            Log.d(TAG, "onPostExecute");
//            mAuthTask = null;
//            showProgress(false);
//
//            if (success) {
////                Intent intent = new Intent(CONTEXT, MainActivity.class);
////                startActivity(intent);
//            } else {
//                mPasswordView.setError(getString(R.string.error_incorrect_password));
//                mPasswordView.requestFocus();
//            }
//        }
//
//        @Override
//        protected void onCancelled() {
//            Log.d(TAG, "onCancelled");
//            mAuthTask = null;
//            showProgress(false);
//        }
//    }
//    //method for account manager auth
//    // FIXME: 10.05.17 наверное этот метод нужно засунуть в колбэк
    public void onTokenReceived(Account account, String token) {
        Log.d(TAG, "onTokenReceived");
        final AccountManager am = AccountManager.get(this);
        final Bundle result = new Bundle();
        final String password  = mPasswordView.getText().toString();
        final String number = mPhoneNumberView.getText().toString();
        if (am.addAccountExplicitly(account, password, new Bundle())) {
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, token);
            am.setAuthToken(account, account.type, token);
            am.setPassword(account,password);
            am.setUserData(account, "phoneNumber", number); // FIXME: 10.05.17 hardcode
        } else {
            result.putString(AccountManager.KEY_ERROR_MESSAGE, getString(R.string.account_already_exists));
        }
        setAccountAuthenticatorResult(result);
        setResult(RESULT_OK);
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private class AuthTask extends AsyncTask<String, Void, String> {
        private final String TAG = com.vsevolod.swipe.addphoto.asyncTask.AuthTask.class.getSimpleName();
        private String token = null;


        @Override
        protected String doInBackground(String... params) {
            String phoneNumber = params[0];
            String password = params[1];
            AuthModel user = new AuthModel(phoneNumber, password);
            Response<UserModel> response = null;
            try {
                response = MyApplication.getApi().authenticate(user).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            MyApplication.getApi().authenticate(user).enqueue(new Callback<UserModel>() {
//                @Override
//                public void onResponse(Call<UserModel> call, Response<UserModel> response) {
//                    Log.d(TAG, "onResponse");
//                    if (response.body() != null) {
//                        Log.d(TAG, "onResponse: body != null");
//                        switch (response.body().getStatus()) {
//                            case Constants.RESPONSE_STATUS_AUTH:
//                                Log.e(TAG, "onResponse: need auth");
////                            startLoginActivity();
//                                break;
//                            case Constants.RESPONSE_STATUS_PARAM:
//                                Log.e(TAG, "onResponse: неверный набор параметров");
//                                //empty token
////                            startLoginActivity();
//                                break;
//                            case Constants.RESPONSE_STATUS_FAIL:
//                                Log.e(TAG, "onResponse: сбой обработки задачи по внешней причине");
//                                break;
//                            case Constants.RESPONSE_STATUS_OK: // here i need to understand what was the
//                                // call model to know how to react
//                                Log.e(TAG, "onResponse: выполнено успешно, ождиается корректный " +
//                                        "протокол выдачи конкретной задачи");
//                                token = response.body().getToken().toString();
//                                Log.d(TAG, token);
////                            mPreferenceHelper.saveString(PreferenceHelper.APP_PREFERENCES_TOKEN, token);
//                                // FIXME: 15.04.17  token = not found
////                            startMainActivity();
//                                break;
//                            case Constants.RESPONSE_STATUS_BAD:
//                                Log.e(TAG, "onResponse: задача не поддерживается сервером");
//                                break;
//                            case Constants.RESPONSE_STATUS_INIT:
//                                Log.e(TAG, "onResponse: проблема на сервере или неверные JSON-данные в POST");
//                                break;
//                            case Constants.RESPONSE_STATUS_DIE:
//                                Log.e(TAG, "onResponse: задача внезапно умерла при обработке");
//                                break;
//                            default:
//                                Log.e(TAG, "onResponse: default");
//                                break;
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<UserModel> call, Throwable t) {
//                    t.printStackTrace();
//                }
//            });
            token = response.body().toString();
            Log.d(TAG, token);
            return token;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d(TAG, "onPostExecute");
            mAuthTask = null;
            showProgress(false);

            if (!TextUtils.isEmpty(token)) {
                Account myAccount = new Account(ARG_ACCOUNT_NAME, ARG_ACCOUNT_TYPE);
                onTokenReceived(myAccount , token);
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            Log.d(TAG, "onCancelled");
            mAuthTask = null;
            showProgress(false);
        }
    }
}