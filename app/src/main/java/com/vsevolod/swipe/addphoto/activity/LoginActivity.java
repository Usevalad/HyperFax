package com.vsevolod.swipe.addphoto.activity;

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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.NotificationCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.accountAuthenticator.AccountGeneral;
import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.config.PreferenceHelper;
import com.vsevolod.swipe.addphoto.constant.AuthSubStatus;
import com.vsevolod.swipe.addphoto.constant.Constants;
import com.vsevolod.swipe.addphoto.constant.ResponseStatus;
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
// TODO: 23.06.17 add valid permissions
public class LoginActivity extends AccountAuthenticatorActivity implements TextView.OnEditorActionListener,
        OnClickListener {

    private final String TAG = this.getClass().getSimpleName();
    private AsyncTask mAuthTask = null;
    private EditText mPhoneEditText;
    private EditText mPasswordEditText;
    private ImageButton mViewPasswordButton;
    private Button mSubmitButton;
    private View mProgressView;
    private View mLoginFormView;
    private String password;
    private String phoneNumber;
    private boolean showPass = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        setContentView(R.layout.activity_login);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        mPhoneEditText = (EditText) findViewById(R.id.phone_number);
        mPhoneEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        mPhoneEditText.setOnEditorActionListener(this);
        mPhoneEditText.setText(getPhoneNumber());
        mPhoneEditText.setSelection(getPhoneNumber().length());

        mPasswordEditText = (EditText) findViewById(R.id.password);
        mPasswordEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mPasswordEditText.setOnEditorActionListener(this);

        mViewPasswordButton = (ImageButton) findViewById(R.id.view_password_button);
        mViewPasswordButton.setOnClickListener(this);

        mSubmitButton = (Button) findViewById(R.id.login_button);
        mSubmitButton.setOnClickListener(this);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        // TODO: 15.06.17 remove
//        mPhoneEditText.setText("+380630674650");
//        mPasswordEditText.setText("sevatest");
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
        mPasswordEditText.setOnEditorActionListener(null);
        findViewById(R.id.login_button).setOnClickListener(null);
        mViewPasswordButton.setOnTouchListener(null);
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume");
        mPasswordEditText.setOnEditorActionListener(this);
        findViewById(R.id.login_button).setOnClickListener(this);
        mViewPasswordButton.setOnClickListener(this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        mPasswordEditText.setOnEditorActionListener(null);
        findViewById(R.id.login_button).setOnClickListener(null);
        mViewPasswordButton.setOnClickListener(null);
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
        phoneNumber = mPhoneEditText.getText().toString();
        password = mPasswordEditText.getText().toString();
        mPasswordEditText.setError(passwordError(password));
        mPhoneEditText.setError(phoneError(phoneNumber));
        // FIXME: 8/11/17 mPhoneEditText.getError == null
        // FIXME: 8/11/17 add validation to utils
        if (passwordError(password) == null && phoneError(phoneNumber) == null) {
            if (isOnline()) {
                showProgress(true);
                new LoginTask().execute();
            } else {
                Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            }
        } else mSubmitButton.setVisibility(View.GONE);
    }

    private String phoneError(String phoneNumber) {
        Log.e(TAG, "isPhoneNumberValid");
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneEditText.requestFocus();
            return "Заполните поле";
        } else if (!phoneNumber.startsWith("+380")) {
            mPhoneEditText.requestFocus();
            return "+380XXXXXXXXX";
        } else if (phoneNumber.length() > Constants.PHONE_NUMBER_LENGTH) {
            mPhoneEditText.requestFocus();
            return "Номер слишком длинный";
        } else if (phoneNumber.length() < Constants.PHONE_NUMBER_LENGTH) {
            mPhoneEditText.requestFocus();
            return "Номер слишком короткий";
        } else
            return null;
    }

    private String passwordError(String password) {
        Log.e(TAG, "isPasswordValid");
        if (TextUtils.isEmpty(password)) {
            return "Заполните поле";
        } else
            return null;
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
        switch (actionId) {
            case EditorInfo.IME_ACTION_NEXT:
                mPhoneEditText.setError(phoneError(mPhoneEditText.getText().toString()));
                if (phoneError(mPhoneEditText.getText().toString()) == null &&
                        passwordError(mPasswordEditText.getText().toString()) == null) {
                    hideKeyboard();
                    mSubmitButton.setVisibility(View.VISIBLE);
                } else if (phoneError(mPhoneEditText.getText().toString()) == null &&
                        passwordError(mPasswordEditText.getText().toString()) != null)
                    mPasswordEditText.requestFocus();
                else {
                    mSubmitButton.setVisibility(View.GONE);
                }
                return true;
            case EditorInfo.IME_ACTION_DONE:
                mPasswordEditText.setError(passwordError(mPasswordEditText.getText().toString()));
                if (passwordError(mPasswordEditText.getText().toString()) == null &&
                        phoneError(mPhoneEditText.getText().toString()) == null) {
                    hideKeyboard();
                    mSubmitButton.setVisibility(View.VISIBLE);
                } else {
                    mSubmitButton.setVisibility(View.GONE);
                }
                return true;
            default:
                return false;
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                attemptLogin();
                break;
            case R.id.view_password_button:
                showPassword();
                break;
            default:
                break;
        }
    }

    private void showPassword() {
        showPass = !showPass;
        if (showPass) {
            mPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            mViewPasswordButton.setImageResource(R.drawable.ic_eye_off);
        } else {
            mViewPasswordButton.setImageResource(R.drawable.ic_eye);
            mPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    public boolean isOnline() {
        Log.e(TAG, "isOnline");
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private class LoginTask extends AsyncTask<Void, Void, Intent> {
        private final String TAG = this.getClass().getSimpleName();
        private String notify;
        private String resultCode;
        private String manufacturer;
        private String name;
        private Context mContext = MyApplication.getContext();

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
                        case ResponseStatus.FAIL:
                            Log.e(TAG, "Status FAIL. error message: " + response.body().getError());
                            break;
                        case ResponseStatus.OK:
                            notify = response.body().getNotify();
                            resultCode = response.body().getResult();
                            Log.e(TAG, "Status OK. notify: " + notify);
                            Log.e(TAG, "Status OK. result: " + resultCode);

                            if (TextUtils.equals(resultCode, AuthSubStatus.VALID)
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

                        case ResponseStatus.PARAM:
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
                    mPasswordEditText.setError(intent.getStringExtra(AccountGeneral.KEY_ERROR_MESSAGE));
                    mPasswordEditText.requestFocus();
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
            if (TextUtils.equals(resultCode, AuthSubStatus.TEL)) {
                mPhoneEditText.setError(notify);
            } else if (TextUtils.equals(resultCode, AuthSubStatus.PASS)) {
                mPasswordEditText.setError(notify);
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
                        .build();
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                notificationManager.notify(0, n);
            }
        }
    }
}