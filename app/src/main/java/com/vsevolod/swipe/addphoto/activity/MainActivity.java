package com.vsevolod.swipe.addphoto.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.accountAuthenticator.AccountGeneral;
import com.vsevolod.swipe.addphoto.adapter.MyRecyclerAdapter;
import com.vsevolod.swipe.addphoto.asyncTask.TreeConverterTask;
import com.vsevolod.swipe.addphoto.config.Constants;
import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.config.PathConverter;
import com.vsevolod.swipe.addphoto.config.PreferenceHelper;
import com.vsevolod.swipe.addphoto.config.RealmHelper;
import com.vsevolod.swipe.addphoto.fragment.QuitFragment;
import com.vsevolod.swipe.addphoto.model.realm.DataModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.RealmChangeListener;

// FIXME: 21.04.17 make recyclerView item flexible, fix two-line prefix with comment, also different screen sizes
// TODO: 13.05.17 add some settings in account menu (shared prefs)
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
    private RealmHelper mRealmHelper;
    private Context mContext = MyApplication.getAppContext();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        checkQuitIntent();
        checkAccountAvailability();
        mRealmHelper = new RealmHelper();
        mRealmHelper.open();
        data = mRealmHelper.getData();
        setContentView(R.layout.activity_main);
        setViews();
        mRealmHelper.getRealm().addChangeListener(this);
    }

    private void setViews() {
        Log.e(TAG, "setViews");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.ic_launcher);
        toolbar.setTitle(" HyperFax");
        setSupportActionBar(toolbar);
        setFABAnimation();
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setHasFixedSize(true);
        setRecyclerViewAdapter();
        setFabHidingAbility();
    }

    /*
    when activity getting intent from QuitFragment to
    close up
     */
    private void checkQuitIntent() {
        if (getIntent().getExtras() != null && getIntent().getExtras()
                .getBoolean(Constants.INTENT_KEY_EXIT, false)) {
            Log.e(TAG, "checkQuitIntent quit");
            finish();
        }
    }

    /*
    Check account exists. If no HyperFax account in AccManager - app needs to create one
     */
    private void checkAccountAvailability() {
        AccountManager manager = AccountManager.get(this);
        Account[] ac = manager.getAccountsByType(AccountGeneral.ARG_ACCOUNT_TYPE);
        if (ac.length < 1) {
            Log.e(TAG, "onCreate: no such accs");
            startLoginActivity();
        } else {
            ContentResolver.setMasterSyncAutomatically(true);
            setPeriodicSync();
        }
    }

    private void setPeriodicSync() {
        Log.e(TAG, "setPeriodicSync");
//            long syncTime = mRealmHelper.getNotSyncedDataStatesIds().length > 0 ?
//                    10000 : Constants.MILLISECONDS_HOUR;

        String accountName = new PreferenceHelper().getAccountName();
        ContentResolver.addPeriodicSync(
                new Account(accountName, AccountGeneral.ARG_ACCOUNT_TYPE),
                getResources().getString(R.string.content_authority),
                new Bundle(),
                10000);
        Log.e(TAG, "setPeriodicSync: time = " + 10000);
    }

    private void setFABAnimation() {
        Log.e(TAG, "setFABAnimation");
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
        Log.e(TAG, "onDestroy");
        mRealmHelper.close();
        mFAB.setOnClickListener(null);
        mFABCamera.setOnClickListener(null);
        mFABGallery.setOnClickListener(null);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        mFAB.setOnClickListener(null);
        mFABCamera.setOnClickListener(null);
        mFABGallery.setOnClickListener(null);
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume");
        isFabOpen = true;
        mFABCamera.setClickable(true);
        mFABGallery.setClickable(true);
        mFAB.setOnClickListener(this);
        mFABCamera.setOnClickListener(this);
        mFABGallery.setOnClickListener(this);
        animateFAB();
        setRecyclerViewAdapter();
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        Log.e(TAG, "onPrepareOptionsMenu");
//        MenuItem checkable = menu.findItem(R.id.main_menu_notifications);
//        checkable.setChecked(isChecked);
//        return true;
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e(TAG, "onCreateOptionsMenu");
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
        Log.e(TAG, "onOptionsItemSelected");
        switch (item.getItemId()) {
            case R.id.main_menu_clear_data:
                mRealmHelper.dropRealmData();
                break;
            case R.id.main_menu_repeat_download:
                // syncing data
                ContentResolver.requestSync(
                        new Account(new PreferenceHelper().getAccountName(), AccountGeneral.ARG_ACCOUNT_TYPE),
                        getResources().getString(R.string.content_authority),
                        new Bundle());
                if (!isOnline()) {
                    // TODO: 24.05.17 change to a dialog fragment
                    Toast.makeText(mContext, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
                break;
//            case R.id.main_menu_notifications:
//                isChecked = !item.isChecked();
//                item.setChecked(isChecked);
//                mRealmHelper.countData();//for debug only
//                break;
            case R.id.main_menu_request_flow:
                new TreeConverterTask().execute();
                break;
            case R.id.main_menu_log_out:
                startLoginActivity();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setRecyclerViewAdapter() {
        Log.e(TAG, "setRecyclerViewAdapter");
        if (data != null) {
            Log.e(TAG, "setRecyclerViewAdapter: true");
            try {
                mRecyclerView.setAdapter(new MyRecyclerAdapter(mContext, data));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        Log.e(TAG, "onClick");
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
        Log.e(TAG, "onCreateContextMenu");
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.e(TAG, "onContextItemSelected");
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
        Log.e(TAG, "getOutputPhotoFile");
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
        Log.e(TAG, "onActivityResult");
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
                PathConverter pathConverter = new PathConverter(mContext);
                String path = pathConverter.getFullPath(photoUri);
                startAddingActivity(path);
            } else {
                Log.e(TAG, "onActivityResult: Call out for image capture failed!");
            }
        }
    }

    private void startAddingActivity(String path) {
        Log.e(TAG, "startAddingActivity");
        Intent intent = new Intent(this, AddingActivity.class);
        intent.putExtra(Constants.INTENT_KEY_PATH, path);
        startActivity(intent);
    }

    private void startCameraActivity() {
        Log.e(TAG, "startCameraActivity");
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = Uri.fromFile(getOutputPhotoFile());
        i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(i, CAPTURE_IMAGE_ACTIVITY_REQ);
    }

    private void startLoginActivity() {
        Log.e(TAG, "startLoginActivity");
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void setFabHidingAbility() {
        Log.e(TAG, "setFabHidingAbility");
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
            Log.e(TAG, "animateFAB: close");
        } else {
            mFAB.startAnimation(rotate_forward);
            mFABCamera.startAnimation(fab_open);
            mFABGallery.startAnimation(fab_open);
            mFABCamera.setClickable(true);
            mFABGallery.setClickable(true);
            isFabOpen = true;
            Log.e(TAG, "animateFAB: open");
        }
    }

//    private void startAuthTokenFetch(Account account) {
//        Log.e(TAG, "startAuthTokenFetch");
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
        setRecyclerViewAdapter();
//        if (SystemClock.elapsedRealtime() - mLastOnchangeAction > Constants.MIN_TIME_BEFORE_NEXT_SYNC) {
//            Log.e(TAG, "onChange: invalidate");
//            Log.e(TAG, "onChange: last onchange " + mLastOnchangeAction);
//
//            mLastOnchangeAction = SystemClock.elapsedRealtime();
//            ContentResolver.requestSync(
//                    new Account(AccountGeneral.ARG_ACCOUNT_NAME, AccountGeneral.ARG_ACCOUNT_TYPE),
//                    getResources().getString(R.string.content_authority),
//                    new Bundle());
//
//            setRecyclerViewAdapter();
//        }
    }

    @Override
    public void onBackPressed() {
        QuitFragment fragment = new QuitFragment();
        fragment.show(getFragmentManager(), "MyDialog");
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
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