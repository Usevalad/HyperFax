package com.vsevolod.swipe.addphoto.activity;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.vsevolod.swipe.addphoto.asyncTask.TreeConverterTask;
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
    private final String TAG = this.getClass().getSimpleName();
    private AsyncTask mAuthTask = null;
    private EditText mPhoneNumberView;
    private EditText mPasswordView;
    private ImageButton mViewPasswordButton;
    private View mProgressView;
    private View mLoginFormView;
    private String password;
    private String phoneNumber;
    private AccountManager mAccountManager;
    private Context mContext;
    private NotificationCompat.Builder mBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        setContentView(R.layout.activity_login);
        mContext = getApplicationContext();
        mBuilder = new NotificationCompat.Builder(this);

        mAccountManager = AccountManager.get(this);

        logs();

        final String phoneNumber = getPhoneNumber();
        mPhoneNumberView = (EditText) findViewById(R.id.phone_number);
        mPhoneNumberView.setText("+380936622642");//+380506361408 номер телефона Mаксима

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setText("user");//admin пароль Mаксима
        mPasswordView.setOnEditorActionListener(this);
        mViewPasswordButton = (ImageButton) findViewById(R.id.view_password_button);
        mViewPasswordButton.setOnTouchListener(this);
        findViewById(R.id.login_button).setOnClickListener(this);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

    }

    private void logs() {
        String mAccountName = getIntent().getStringExtra(AccountGeneral.ARG_ACCOUNT_NAME);
        Log.e(TAG, "onCreate: mAccountName " + mAccountName);
        String mAccountType = getIntent().getStringExtra(AccountGeneral.ARG_ACCOUNT_TYPE);
        Log.e(TAG, "onCreate: mAccountType " + mAccountType);
        String mAuthType = getIntent().getStringExtra(AccountGeneral.ARG_AUTH_TYPE);
        Log.e(TAG, "onCreate: mAuthType " + mAuthType);
        boolean isAddingNewAccount = getIntent().getBooleanExtra(AccountGeneral.ARG_IS_ADDING_NEW_ACCOUNT, false);
        Log.e(TAG, "onCreate: isAddingNewAccount " + isAddingNewAccount);
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
        // FIXME: 10.05.17 improve method
        //костыль, который по аккаунтам находит номер телефона
        Log.e(TAG, "getPhoneNumber");
        AccountManager am = AccountManager.get(this);
        Account[] accounts = am.getAccounts();
        String phoneNumber = "oops";
        for (Account ac : accounts) {
            String accountName = ac.name;
            String accountType = ac.type;
            String accountDescribe = String.valueOf(ac.describeContents());
            // Take your time to look at all available accounts
            if (accountName.startsWith("+380")) { //viber acc name is phone number
                phoneNumber = accountName;
            }
            System.out.println("Accounts : " + accountName + ", " + accountType + " describe:" + accountDescribe);
            if (TextUtils.equals(ac.name, new PreferenceHelper().getAccountName())) {
                Log.e(TAG, "getPhoneNumber: " + ac.name);
                Log.e(TAG, "getPhoneNumber: " + am.peekAuthToken(ac, AccountManager.KEY_AUTHTOKEN));
                Log.e(TAG, "getPhoneNumber: " + am.peekAuthToken(ac, AccountGeneral.ARG_TOKEN_TYPE));
                Log.e(TAG, "getPhoneNumber: " + am.peekAuthToken(ac, AccountGeneral.ARG_AUTH_TYPE));
                Log.e(TAG, "getPhoneNumber: " + am.peekAuthToken(ac, AccountGeneral.ARG_ACCOUNT_TYPE));
                Log.e(TAG, "getPhoneNumber: " + am.peekAuthToken(ac, AccountGeneral.ARG_ACCOUNT_TYPE));
                Log.e(TAG, "getPhoneNumber: " + am.peekAuthToken(ac, "com.vsevolod.swipe.addphoto"));
                Account account = new Account("Hyper Fax", "com.vsevolod.swipe.addphoto");
                Log.e(TAG, "getPhoneNumber: " + am.peekAuthToken(account, "com.vsevolod.swipe.addphoto"));
            }
        }
        return phoneNumber;
    }

    private void checkAccountAvailability() {
        Account[] ac = mAccountManager.getAccountsByType(AccountGeneral.ARG_ACCOUNT_TYPE);
        if (ac.length < 1) {
            Log.e(TAG, "onCreate: no such accs");

        } else {
            startMainActivity();
        }
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
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
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
        final Account account = new Account(
                new PreferenceHelper().getAccountName(),
                AccountGeneral.ARG_ACCOUNT_TYPE);
        Account[] acc = mAccountManager.getAccountsByType(AccountGeneral.ARG_ACCOUNT_TYPE);
        String authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);

        if (acc.length == 0) {
            Log.e(TAG, "finishLogin: adding new account");

            mAccountManager.addAccountExplicitly(account, password, null);
        } else {
            Log.e(TAG, "finishLogin: changing password in existed account");
            // Password change only
            mAccountManager.setPassword(account, password);
        }
        mAccountManager.setAuthToken(account, AccountGeneral.ARG_TOKEN_TYPE, authToken);
        // base class can do what Android requires with the
        // KEY_ACCOUNT_AUTHENTICATOR_RESPONSE extra that onCreate has
        // already grabbed
        setAccountAuthenticatorResult(intent.getExtras());
        // Tell the account manager settings page that all went well
        setResult(RESULT_OK, intent);
        ContentResolver.setSyncAutomatically(account, getString(R.string.content_authority), true);
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
        attemptLogin();
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

                            if (TextUtils.equals(resultCode, Constants.RESPONSE_AUTH_SUB_STATUS_VALID)) {
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
                            // TODO: 28.05.17 add getLog
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
                    new TreeConverterTask().execute();
                    Log.e(TAG, "onPostExecute: isFirst = true");
                    // Close the activity, we're done
                    finish();
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