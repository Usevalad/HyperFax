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
import android.widget.EditText;
import android.widget.TextView;

import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.config.Constants;
import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.model.query.AuthModel;
import com.vsevolod.swipe.addphoto.model.responce.UserModel;

import java.io.IOException;

import retrofit2.Response;

/**
 * A login screen that offers login via phone number/password.
 */
// FIXME: 21.04.17 handle hardware back button onCLick (quit from app)
// TODO: 21.04.17 add phone number finding library
public class LoginActivity extends AccountAuthenticatorActivity {
    private final String TAG = LoginActivity.class.getSimpleName();
    private AsyncTask mAuthTask = null;
    private EditText mPhoneNumberView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        String phoneNumber = getPhoneNumber();
        mPhoneNumberView = (EditText) findViewById(R.id.phone_number);
        mPhoneNumberView.setText("+380936622642");//+380506361408 номер телефона Mаксима

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setText("user");//admin пароль Mаксима
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

        findViewById(R.id.login_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    @NonNull
    private String getPhoneNumber() {
        // FIXME: 10.05.17 improve method
        //костыль, который по аккаунтам находит номер телефона
        Log.d(TAG, "getPhoneNumber");
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
            mPhoneNumberView.setError(getResources().getString(R.string.phone_nuber_invalid));
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
        return phoneNumber.length() == Constants.PHONE_NUMBER_LENGTH;
    }

    private boolean isPasswordValid(String password) {
        //TODO: improve validation
        Log.d(TAG, "isPasswordValid");
        return password.length() > Constants.MIN_PASSWORD_LENGTH;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        Log.d(TAG, "showProgress");
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

    public void onTokenReceived(Account account, String token) {
        Log.d(TAG, "onTokenReceived");
        final AccountManager am = AccountManager.get(this);
        final Bundle result = new Bundle();
        final String password = mPasswordView.getText().toString();
        final String number = mPhoneNumberView.getText().toString();
        if (am.addAccountExplicitly(account, password, new Bundle())) {
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, token);
            am.setAuthToken(account, account.type, token);
            am.setPassword(account, password);
            am.setUserData(account, Constants.KEY_ACCOUNT_PHONE_NUMBER, number);
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
            Log.d(TAG, "doInBackground");
            String phoneNumber = params[0];
            String password = params[1];
            AuthModel user = new AuthModel(phoneNumber, password);
            Response<UserModel> response = null;
            try {
                response = MyApplication.getApi().authenticate(user).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                Account myAccount = new Account(Constants.ARG_ACCOUNT_NAME, Constants.ARG_ACCOUNT_TYPE);
                onTokenReceived(myAccount, token);
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