package com.vsevolod.swipe.addphoto.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;

import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.accountAuthenticator.AccountGeneral;
import com.vsevolod.swipe.addphoto.adapter.MyRecyclerAdapter;
import com.vsevolod.swipe.addphoto.asyncTask.TreeConverterTask;
import com.vsevolod.swipe.addphoto.config.Constants;
import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.config.PathConverter;
import com.vsevolod.swipe.addphoto.config.RealmHelper;
import com.vsevolod.swipe.addphoto.fragment.QuitFragment;
import com.vsevolod.swipe.addphoto.model.realm.DataModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.RealmChangeListener;

// FIXME: 21.04.17 find memory leak
// FIXME: 21.04.17 fix memory leak
// FIXME: 21.04.17 make recyclerView item flexible, fix two-line prefix with comment, also different screen sizes
// FIXME: 21.04.17 check onActivityResult, looks horribly
// FIXME: 21.04.17 handle hardware back button onClick (show dialog fragment: "do you really want quit?")
// TODO: 13.05.17 add some settings in account menu (shared prefs)
// TODO: 16.05.17 add some message if no internet connection
public class MainActivity extends AppCompatActivity implements View.OnClickListener, RealmChangeListener {
    private final String TAG = MainActivity.class.getSimpleName();
    private final int CAPTURE_IMAGE_ACTIVITY_REQ = 31;
    private final int SELECT_PICTURE = 12;
    private long mLastOnchangeAction = 0L;
    public RecyclerView mRecyclerView;
    public List<DataModel> data;
    private FloatingActionButton mFAB;
    private FloatingActionButton mFABCamera;
    private FloatingActionButton mFABGallery;
    private Animation fab_open;
    private Animation fab_close;
    private Animation rotate_forward;
    private Animation rotate_backward;
    private Boolean isFabOpen = false;
    private boolean isChecked = false;
    private Uri fileUri = null;
    private RealmHelper mRealmHelper = new RealmHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        if (getIntent().getExtras() != null && getIntent().getExtras()
                .getBoolean(Constants.INTENT_KEY_EXIT, false)) {
            finish();
        }
        mRealmHelper.open();
        data = mRealmHelper.getData();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.logo);
        setSupportActionBar(toolbar);
        setFABAnimation();
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        setRecyclerViewAdapter();
        setFabHidingAbility();
        mRealmHelper.getRealm().addChangeListener(this);
        setPeriodicSync();
    }

    private void setPeriodicSync() {
        int syncTime = mRealmHelper.getNotSyncedDataStatesIds().length > 0 ?
                Constants.MILLISECONDS_FIVE_MIN : Constants.MILLISECONDS_HOUR;
        ContentResolver.addPeriodicSync(
                new Account(AccountGeneral.ARG_ACCOUNT_NAME, AccountGeneral.ARG_ACCOUNT_TYPE),
                getResources().getString(R.string.content_authority),
                new Bundle(),
                syncTime);
    }

    private void setFABAnimation() {
        Log.d(TAG, "setFABAnimation");
        mFAB = (FloatingActionButton) findViewById(R.id.fab);
        mFABCamera = (FloatingActionButton) findViewById(R.id.fab_camera);
        mFABGallery = (FloatingActionButton) findViewById(R.id.fab_gallery);
        mFAB.setOnClickListener(this);
        mFABCamera.setOnClickListener(this);
        mFABGallery.setOnClickListener(this);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        mRealmHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        mRealmHelper.open();
        isFabOpen = true;
        mFABCamera.setClickable(true);
        mFABGallery.setClickable(true);
        animateFAB();
        setRecyclerViewAdapter();
        super.onResume();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu");
        MenuItem checkable = menu.findItem(R.id.main_menu_notifications);
        checkable.setChecked(isChecked);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        switch (item.getItemId()) {
            case R.id.main_menu_clear_data:
                mRealmHelper.dropRealmData();
                break;
            case R.id.main_menu_repeat_download:
                // syncing data
                ContentResolver.requestSync(
                        new Account(AccountGeneral.ARG_ACCOUNT_NAME, AccountGeneral.ARG_ACCOUNT_TYPE),
                        getResources().getString(R.string.content_authority),
                        new Bundle());
                break;
            case R.id.main_menu_notifications:
                isChecked = !item.isChecked();
                item.setChecked(isChecked);
                mRealmHelper.countData();//for debug only
                break;
            case R.id.main_menu_request_flow:
                AccountManager am = AccountManager.get(this);
                if (am.getAccountsByType(AccountGeneral.ARG_ACCOUNT_TYPE).length > 0) {
                    new TreeConverterTask().execute();
                } else {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.main_menu_log_out:
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setRecyclerViewAdapter() {
        Log.d(TAG, "setRecyclerViewAdapter");
        if (data != null) {
            Log.d(TAG, "setRecyclerViewAdapter: true");
            try {
                mRecyclerView.setAdapter(new MyRecyclerAdapter(MyApplication.getAppContext(), data));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick");
        switch (v.getId()) {
            case R.id.fab:
                animateFAB();
                break;
            case R.id.fab_camera:
                mFABCamera.setClickable(false);
                mFABGallery.setClickable(false);
                startCameraActivity();
                break;
            case R.id.fab_gallery:
                mFABCamera.setClickable(false);
                mFABGallery.setClickable(false);
                Intent intent = new Intent();
                intent.setType(Constants.MEDIA_TYPE_IMAGE);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        Constants.ACTION_SELECT_PICTURE), SELECT_PICTURE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        Log.d(TAG, "onCreateContextMenu");
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.d(TAG, "onContextItemSelected");
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete:
                // TODO: 29.03.17 handle this
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private File getOutputPhotoFile() {
        Log.d(TAG, "getOutputPhotoFile");
        File directory = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), getPackageName());
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                Log.e(TAG, "Failed to create storage directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(new Date());

        String img = "IMG";
        return new File(directory.getPath() + File.separator + img
                + timeStamp + Constants.EXTENSION_JPG);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        if (resultCode != RESULT_CANCELED) {
            Uri photoUri;
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQ) {
                if (data == null) {
                    // A known bug here! The image should have saved in fileUri
                    photoUri = fileUri;
                } else {
                    photoUri = data.getData();
                }
                startAddingActivity(photoUri.getPath());

            } else if (requestCode == SELECT_PICTURE) {
                photoUri = data.getData();
                String path = PathConverter.getFullPath(photoUri);
                startAddingActivity(path);
            } else {
                Log.e(TAG, "onActivityResult: " + "Call out for image capture failed!");
            }
        }
    }

    private void startAddingActivity(String path) {
        Log.d(TAG, "startAddingActivity");
        Intent intent = new Intent(this, AddingActivity.class);
        intent.putExtra(Constants.INTENT_KEY_PATH, path);
        startActivity(intent);
    }

    private void startCameraActivity() {
        Log.d(TAG, "startCameraActivity");
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = Uri.fromFile(getOutputPhotoFile());
        i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(i, CAPTURE_IMAGE_ACTIVITY_REQ);
    }

    private void setFabHidingAbility() {
        Log.d(TAG, "setFabHidingAbility");
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // Scroll Down
                    if (mFAB.isShown()) {
                        if (isFabOpen) {
                            animateFAB();
                        }
                        mFAB.setClickable(false);
                        mFAB.hide();
                    }
                } else if (dy < 0) {
                    // Scroll Up
                    if (!mFAB.isShown()) {
                        mFAB.setClickable(true);
                        mFAB.show();
                    }
                }
            }
        });
    }

    public void animateFAB() {
        if (isFabOpen) {
            mFAB.startAnimation(rotate_backward);
            mFABCamera.startAnimation(fab_close);
            mFABGallery.startAnimation(fab_close);
            mFABCamera.setClickable(false);
            mFABGallery.setClickable(false);
            isFabOpen = false;
            Log.d(TAG, "animateFAB: close");
        } else {
            mFAB.startAnimation(rotate_forward);
            mFABCamera.startAnimation(fab_open);
            mFABGallery.startAnimation(fab_open);
            mFABCamera.setClickable(true);
            mFABGallery.setClickable(true);
            isFabOpen = true;
            Log.d(TAG, "animateFAB: open");
        }
    }

//    private void startAuthTokenFetch(Account account) {
//        Log.d(TAG, "startAuthTokenFetch");
//        Bundle options = new Bundle();
//        android.os.Handler handler = new android.os.Handler();
//        OnAccountManagerComplete callBack = new OnAccountManagerComplete();
//        mAccountManager.getAuthToken(
//                account,
//                AccountGeneral.ARG_TOKEN_TYPE,
//                options,
//                this,
//                callBack,
//                handler
//        );
//    }

    @Override
    public void onChange(Object element) {
        Log.e(TAG, "onChange");
        if (SystemClock.elapsedRealtime() - mLastOnchangeAction > Constants.MIN_TIME_BEFORE_NEXT_SYNC) {
            Log.e(TAG, "onChange: invalidate");
            Log.e(TAG, "onChange: last onchange " + mLastOnchangeAction);

            mLastOnchangeAction = SystemClock.elapsedRealtime();
            ContentResolver.requestSync(
                    new Account(AccountGeneral.ARG_ACCOUNT_NAME, AccountGeneral.ARG_ACCOUNT_TYPE),
                    getResources().getString(R.string.content_authority),
                    new Bundle());

            setRecyclerViewAdapter();
        }
    }

    @Override
    public void onBackPressed() {
        QuitFragment fragment = new QuitFragment();
        fragment.show(getFragmentManager(), "MyDialog");
    }

    //    private class OnAccountManagerComplete implements AccountManagerCallback<Bundle> {
//        private final String TAG = this.getClass().getSimpleName();
//
//        @Override
//        public void run(AccountManagerFuture<Bundle> result) {
//            Bundle bundle;
//            Log.e(TAG, "run");
//            try {
//                bundle = result.getResult();
//            } catch (OperationCanceledException e) {
//                e.printStackTrace();
//                return;
//            } catch (AuthenticatorException e) {
//                e.printStackTrace();
//                return;
//            } catch (IOException e) {
//                e.printStackTrace();
//                return;
//            }
//        }
//    }
}