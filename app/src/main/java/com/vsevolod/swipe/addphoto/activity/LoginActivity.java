package com.vsevolod.swipe.addphoto.activity;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.accountAuthenticator.AccountGeneral;
import com.vsevolod.swipe.addphoto.config.Constants;
import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.config.PreferenceHelper;
import com.vsevolod.swipe.addphoto.fragment.QuitFragment;
import com.vsevolod.swipe.addphoto.model.query.AuthModel;
import com.vsevolod.swipe.addphoto.model.responce.AuthResponseModel;

import java.io.IOException;

import retrofit2.Response;

/**
 * A login screen that offers login via phone number/password.
 */
// TODO: 21.04.17 add phone number finding library
// TODO: 13.05.17 refactor
public class LoginActivity extends AccountAuthenticatorActivity implements TextView.OnEditorActionListener,
        OnClickListener, View.OnTouchListener {
    private static final int PERMISSION_REQUEST_CODE = 142;
    private final String TAG = this.getClass().getSimpleName();
    private AsyncTask mAuthTask = null;
    private EditText mPhoneNumberView;
    private EditText mPasswordView;
    private ImageButton mViewPasswordButton;
    private View mProgressView;
    private View mLoginFormView;
    private String password;
    private String phoneNumber;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        setContentView(R.layout.activity_login);
        mContext = getApplicationContext();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        final String phoneNumber = getPhoneNumber();
        mPhoneNumberView = (EditText) findViewById(R.id.phone_number);
        mPhoneNumberView.setText(phoneNumber);
        mPhoneNumberView.setSelection(mPhoneNumberView
                .getText().length());

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(this);
        mViewPasswordButton = (ImageButton) findViewById(R.id.view_password_button);
        mViewPasswordButton.setOnTouchListener(this);
        findViewById(R.id.login_button).setOnClickListener(this);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);


        // TODO: 15.06.17 remove
        mPhoneNumberView.setText("+380630674650");
        mPasswordView.setText("sevatest");

    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "onBackPressed");
        QuitFragment fragment = new QuitFragment();
        String MY_DIALOG = "MyDialog";
        fragment.show(getFragmentManager(), MY_DIALOG);
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        mPasswordView.setOnEditorActionListener(null);
        findViewById(R.id.login_button).setOnClickListener(null);
        mViewPasswordButton.setOnTouchListener(null);
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume");
        mPasswordView.setOnEditorActionListener(this);
        findViewById(R.id.login_button).setOnClickListener(this);
        mViewPasswordButton.setOnTouchListener(this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        mPasswordView.setOnEditorActionListener(null);
        findViewById(R.id.login_button).setOnClickListener(null);
        mViewPasswordButton.setOnTouchListener(null);
        super.onDestroy();
    }

    @NonNull
    private String getPhoneNumber() {
        //костыль, который по аккаунтам находит номер телефона
        Log.e(TAG, "getPhoneNumber");
        AccountManager am = AccountManager.get(this);
        Account[] accounts = am.getAccounts();
        String phoneNumber = "+380";
        for (Account ac : accounts) {
            if (ac.name.startsWith(phoneNumber)) { //viber acc name is phone number
                phoneNumber = ac.name;
                break;
            }
        }
        return phoneNumber;
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        Log.e(TAG, "attemptLogin");
        if (mAuthTask != null) {
            return;
        }
        // Reset errors.
        mPhoneNumberView.setError(null);
        mPasswordView.setError(null);
        // Store values at the time of the login attempt.
        phoneNumber = mPhoneNumberView.getText().toString();
        password = mPasswordView.getText().toString();

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
            mPhoneNumberView.setError(getResources().getString(R.string.phone_number_invalid));
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
            if (isOnline()) {
                showProgress(true);
                new LoginTask().execute();
            } else {
                Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        //TODO: improve validation
        Log.e(TAG, "isPhoneNumberValid");
        return phoneNumber.length() == Constants.PHONE_NUMBER_LENGTH;
    }

    private boolean isPasswordValid(String password) {
        //TODO: improve validation
        Log.e(TAG, "isPasswordValid");
        return password.length() > Constants.MIN_PASSWORD_LENGTH;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        Log.e(TAG, "showProgress");
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
    }

    private void finishLogin(Intent intent) {
        // base class can do what Android requires with the
        // KEY_ACCOUNT_AUTHENTICATOR_RESPONSE extra that onCreate has
        // already grabbed
        AccountGeneral.finishLogin(this, intent, password);
        setAccountAuthenticatorResult(intent.getExtras());
        // Tell the account manager settings page that all went well
        setResult(RESULT_OK, intent);
        //finish, we are done
        finish();
        startMainActivity();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == R.id.login || actionId == EditorInfo.IME_NULL) {
            attemptLogin();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCOUNT_MANAGER)
                        == PackageManager.PERMISSION_GRANTED) {

            attemptLogin();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.INTERNET,
                            Manifest.permission.ACCOUNT_MANAGER},
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    attemptLogin();
                break;
            default:
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public boolean isOnline() {
        Log.e(TAG, "isOnline");
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPasswordView.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case MotionEvent.ACTION_UP:
                mPasswordView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
        }
        return true;
    }

    private class LoginTask extends AsyncTask<Void, Void, Intent> {
        private final String TAG = this.getClass().getSimpleName();
        private String notify;
        private String resultCode;
        private String manufacturer;
        private String name;

        @Override
        protected Intent doInBackground(Void... params) {
            Log.e(TAG, "doInBackground");
            AuthModel user = new AuthModel(phoneNumber, password);
            Response<AuthResponseModel> response;
            String authToken;
            final Intent res = new Intent();
            try {
                response = MyApplication.getApi().authenticate(user).execute();

                if (response.code() == 201 || response.code() == 200) {
                    switch (response.body().getStatus()) {
                        case Constants.RESPONSE_STATUS_FAIL:
                            Log.e(TAG, "Status FAIL. error message: " + response.body().getError());
                            break;
                        case Constants.RESPONSE_STATUS_OK:
                            notify = response.body().getNotify();
                            resultCode = response.body().getResult();
                            Log.e(TAG, "Status OK. notify: " + notify);
                            Log.e(TAG, "Status OK. result: " + resultCode);


                            if (TextUtils.equals(resultCode, Constants.RESPONSE_AUTH_SUB_STATUS_VALID)
                                    && response.isSuccessful()) {
                                //all right
                                authToken = response.body().getToken();
                                String name = response.body().getName();
                                Log.e(TAG, "token : " + authToken);
                                new PreferenceHelper().saveString(PreferenceHelper.APP_PREFERENCES_ACCOUNT_NAME, name);
                                res.putExtra(AccountManager.KEY_ACCOUNT_NAME, name);
                                res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, AccountGeneral.ARG_ACCOUNT_TYPE);
                                res.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);
                                res.putExtra(AccountGeneral.PARAM_USER_PASS, password);
                                res.putExtra(AccountGeneral.KEY_ACCOUNT_PHONE_NUMBER, phoneNumber);

                                return res;
                            } else return null;

                        case Constants.RESPONSE_STATUS_PARAM:
                            Log.e(TAG, "Status PARAM");
                            showProgress(false);
                            break;
                        default:
                            break;
                    }
                } else {
                    Log.e(TAG, "response code = " + response.code());
                    Log.e(TAG, "response message = " + response.message());
                }
            } catch (IOException e) {
                e.printStackTrace();
                res.putExtra(AccountGeneral.KEY_ERROR_MESSAGE, e.getMessage());
            }
            return res;
        }

        @Override
        protected void onPostExecute(Intent intent) {
            mAuthTask = null;
            if (intent != null) {
                if (intent.hasExtra(AccountGeneral.KEY_ERROR_MESSAGE)) {
                    mPasswordView.setError(intent.getStringExtra(AccountGeneral.KEY_ERROR_MESSAGE));
                    mPasswordView.requestFocus();
                } else {
                    finishLogin(intent);
                }
            } else {
                onCancelled();
            }
        }

        @Override
        protected void onCancelled() {
            Log.e(TAG, "onCancelled");
            mAuthTask = null;
            showProgress(false);
            if (TextUtils.equals(resultCode, Constants.RESPONSE_AUTH_SUB_STATUS_TEL)) {
                mPhoneNumberView.setError(notify);
            } else if (TextUtils.equals(resultCode, Constants.RESPONSE_AUTH_SUB_STATUS_PASS)) {
                mPasswordView.setError(notify);
            }
            if (!TextUtils.isEmpty(notify)) {
                Intent intent = new Intent(mContext, NotificationActivity.class);
                intent.putExtra("notify", notify);
                intent.setAction("main");
                PendingIntent pIntent = PendingIntent.getActivity(mContext, (int) System.currentTimeMillis(), intent, 0);
                Notification n = new Notification.Builder(mContext)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(notify)
                        .setSmallIcon(R.drawable.ic_toolbar_logo)
                        .setContentIntent(pIntent)
                        .setAutoCancel(true)
//                    .addAction(R.drawable.round_logo96x96, "Call", pIntent)
//                    .addAction(R.drawable.round_logo96x96, "More", pIntent)
//                    .addAction(R.drawable.round_logo96x96, "And more", pIntent)
                        .build();
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                notificationManager.notify(0, n);
            }
        }
    }
}