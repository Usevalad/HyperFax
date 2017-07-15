package com.vsevolod.swipe.addphoto.activity;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.accountAuthenticator.AccountGeneral;
import com.vsevolod.swipe.addphoto.adapter.AutoCompleteAdapter;
import com.vsevolod.swipe.addphoto.constant.Constants;
import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.config.PreferenceHelper;
import com.vsevolod.swipe.addphoto.config.RealmHelper;
import com.vsevolod.swipe.addphoto.model.realm.DataModel;
import com.vsevolod.swipe.addphoto.util.GeoDegree;
import com.vsevolod.swipe.addphoto.util.ImageConverter;
import com.vsevolod.swipe.addphoto.util.MyDateUtil;
import com.vsevolod.swipe.addphoto.util.PathConverter;

import java.util.Date;

public class AddingActivity extends AppCompatActivity implements TextView.OnEditorActionListener {
    private final String TAG = this.getClass().getSimpleName();
    private RealmHelper mRealmHelper;
    public AutoCompleteTextView mAutoCompleteTextView;
    private EditText mEditText;
    private Uri mPhotoUri = null;
    private String mText;
    private long mLastClickTime = 0;
    private Location mLocation = null;
    private Context mContext = MyApplication.getAppContext();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        mRealmHelper = new RealmHelper();
        mRealmHelper.open();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.adding_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setLogo(R.drawable.ic_toolbar_logo);

        final Intent intent = getIntent();

        if (TextUtils.equals(Intent.ACTION_SEND, intent.getAction()) && intent.getType() != null) {
            if (TextUtils.equals(intent.getType(), Constants.MEDIA_TYPE_IMAGE)) {
                mPhotoUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            }
        } else {
            mPhotoUri = Uri.parse(intent.getStringExtra(Constants.INTENT_KEY_PATH));
        }

        if (mPhotoUri != null) {
            ImageView mImageView = (ImageView) findViewById(R.id.adding_image_view);
            mImageView.setImageURI(mPhotoUri);
        } else {
            Toast.makeText(this, "Не удалось загрузить фото", Toast.LENGTH_SHORT).show();
        }
        mAutoCompleteTextView =
                (AutoCompleteTextView) findViewById(R.id.adding_auto_complete);
        mAutoCompleteTextView.setAdapter(new AutoCompleteAdapter(mContext));
        mAutoCompleteTextView.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        mAutoCompleteTextView.setRawInputType(InputType.TYPE_CLASS_TEXT);
        mEditText = (EditText) findViewById(R.id.adding_edit_text);
        mEditText.setImeOptions(EditorInfo.IME_ACTION_SEND);
        mEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        mEditText.setOnEditorActionListener(this);
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        mEditText.setOnEditorActionListener(null);
        mRealmHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume");
        mEditText.setOnEditorActionListener(this);
        super.onResume();
    }

    private void decodeImage() {
        Log.e(TAG, "decodeImage");
        if (mPhotoUri != null) {
            int photoResource = getIntent().getIntExtra(Constants.INTENT_KEY_PHOTO_RES, 0);
            String path = new PathConverter(this).getFullPath(mPhotoUri, photoResource);
            if (isPrefixValid()) {
                byte[] image = ImageConverter.imageToByte(path);
                saveDataToRealm(image, path);
            }
        } else {
            Toast.makeText(mContext, "Не правильный путь к фото", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isPrefixValid() {
        Log.e(TAG, "isPrefixValid");
        mText = mAutoCompleteTextView.getText().toString();
        if (!mRealmHelper.isValid(mText)) {
            String error = getResources().getString(R.string.choose_tag);
            mAutoCompleteTextView.setError(error);
            return false;
        }
        return true;
    }

    private void saveDataToRealm(@NonNull byte[] byteArray, @NonNull String photoUri) {
        Log.e(TAG, "saveDataToRealm");

        String prefix = mText.substring(mText.length() - 4); //4 is a prefix length
        double latitude = 0.0, longitude = 0.0;

        GeoDegree geoDegree = new GeoDegree(photoUri);
        if (geoDegree.isValid()) {
            latitude = geoDegree.getLatitude();
            longitude = geoDegree.getLongitude();
        }

        DataModel model = new DataModel(
                MyDateUtil.getSearchDate(),
                MyDateUtil.getViewDate(),
                prefix,
                mText.substring(0, mText.length() - 5).toLowerCase(),//5 is a prefix length + space
                mEditText.getText().toString(),
                photoUri,
                byteArray,
                latitude,
                longitude,
                mRealmHelper.getPrefixID(prefix),
                new Date()
        );

        mRealmHelper.save(model);
        Account account = new Account(new PreferenceHelper().getAccountName(), AccountGeneral.ARG_ACCOUNT_TYPE);
        Log.e(TAG, "saveDataToRealm: ContentResolver.isSyncActive");
        if (!ContentResolver.isSyncPending(account, getString(R.string.content_authority))) {
            Log.e(TAG, "saveDataToRealm: !ContentResolver.isSyncPending ");
            AccountGeneral.sync();
        }
        if (!isOnline()) {
            // TODO: 24.05.17 change to a dialog fragment
            Toast.makeText(mContext, "Нет соединения. Данные будут отправлены позже", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e(TAG, "onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_adding, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e(TAG, "onOptionsItemSelected");
        switch (item.getItemId()) {
            case R.id.send:
                checkLastClick();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void checkLastClick() {
        Log.e(TAG, "checkLastClick");
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        decodeImage();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        Log.e(TAG, "onEditorAction");
        boolean handled = false;
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            checkLastClick();
            handled = true;
        }
        return handled;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}