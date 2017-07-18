package com.vsevolod.swipe.addphoto.activity;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.google.firebase.crash.FirebaseCrash;
import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.accountAuthenticator.AccountGeneral;
import com.vsevolod.swipe.addphoto.adapter.MyRecyclerAdapter;
import com.vsevolod.swipe.addphoto.asyncTask.ServerSyncTask;
import com.vsevolod.swipe.addphoto.asyncTask.TreeConverterTask;
import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.config.RealmHelper;
import com.vsevolod.swipe.addphoto.constant.Constants;
import com.vsevolod.swipe.addphoto.fragment.QuitFragment;
import com.vsevolod.swipe.addphoto.fragment.RemovePhotoFragment;
import com.vsevolod.swipe.addphoto.model.realm.DataModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.RealmChangeListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RealmChangeListener,
        SwipeRefreshLayout.OnRefreshListener {
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 23;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 24;
    private final String TAG = MainActivity.class.getSimpleName();
    private long mLastOnchangeAction = 0L;
    public RecyclerView mRecyclerView;
    public List<DataModel> data;
    private FloatingActionButton mFAB;
    private FloatingActionButton mFABCamera;
    private FloatingActionButton mFABGallery;
    public static SwipeRefreshLayout swipeRefreshLayout;
    private Animation mFabOpen;
    private Animation mFabClose;
    private Animation mRotateForward;
    private Animation mRotateBackward;
    private Boolean isFabOpen = false;
    private boolean isChecked = false;
    private Uri mFileUri = null;
    private RealmHelper mRealmHelper;
    private Context mContext = MyApplication.getAppContext();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        mRealmHelper = new RealmHelper();
        mRealmHelper.open();
        data = mRealmHelper.getData();
        mRealmHelper.getRealm().addChangeListener(this);
        checkAccountAvailability();
        setContentView(R.layout.activity_main);
        setViews();

    }

    private void setViews() {
        Log.e(TAG, "setViews");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.ic_toolbar_logo);
        toolbar.setTitle("HyperFax v" + MyApplication.getAppVersionName());
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        setSupportActionBar(toolbar);
        setFABAnimation();
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setHasFixedSize(true);
        setRecyclerViewAdapter();
        setFabHidingAbility();
    }

    /*
    Check account exists. If no HyperFax account in AccManager - app needs to create one
     */
    private void checkAccountAvailability() {
        AccountManager mAccountManager = AccountManager.get(this);
        Account[] ac = mAccountManager.getAccountsByType(AccountGeneral.ARG_ACCOUNT_TYPE);
        if (ac.length < 1) {
            Log.e(TAG, "onCreate: no such accs");
            startLoginActivity(false);
        } else {
            AccountGeneral.setPeriodicSync(this);
        }
    }

    private void setFABAnimation() {
        Log.e(TAG, "setFABAnimation");
        mFAB = (FloatingActionButton) findViewById(R.id.fab);
        mFABCamera = (FloatingActionButton) findViewById(R.id.fab_camera);
        mFABGallery = (FloatingActionButton) findViewById(R.id.fab_gallery);
        mFAB.setOnClickListener(this);
        mFABCamera.setOnClickListener(this);
        mFABGallery.setOnClickListener(this);
        mFabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        mFabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        mRotateForward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        mRotateBackward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        mRealmHelper.getRealm().removeAllChangeListeners();
        mRealmHelper.close();
        mFAB.setOnClickListener(null);
        mFABCamera.setOnClickListener(null);
        mFABGallery.setOnClickListener(null);
        swipeRefreshLayout.setOnRefreshListener(null);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        mFAB.setOnClickListener(null);
        mFABCamera.setOnClickListener(null);
        mFABGallery.setOnClickListener(null);
        swipeRefreshLayout.setOnRefreshListener(null);
        mRealmHelper.getRealm().removeAllChangeListeners();
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
        mRealmHelper.getRealm().addChangeListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        animateFAB();
        setRecyclerViewAdapter();
        AccountGeneral.sync();
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
    }

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
                RemovePhotoFragment fragment = new RemovePhotoFragment();
                fragment.show(getFragmentManager(), "MyDialog");
                break;
            case R.id.main_menu_instruction:
                String url = "http://telegra.ph/Instrukciya-HyperFax-06-06";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
            case R.id.main_menu_request_flow:
                new TreeConverterTask().execute();
//                mRealmHelper.countTree();
//                mRealmHelper.countData();
                break;
            case R.id.main_menu_log_out:
                startLoginActivity(true);
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
                if (isCameraPermissionsAllowed())
                    startCameraActivity();
                break;
            case R.id.fab_gallery:
                if (isGalleryPermissionsAllowed())
                    startGalleryActivity();
                break;
            default:
                break;
        }
    }

    private boolean isCameraPermissionsAllowed() {
        boolean isAllowed = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                isAllowed = true;
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        CAMERA_PERMISSION_REQUEST_CODE);
            }
        } else {
            isAllowed = true;
        }

        return isAllowed;
    }

    private boolean isGalleryPermissionsAllowed() {
        boolean isAllowed = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                isAllowed = true;
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        GALLERY_PERMISSION_REQUEST_CODE);
            }
        } else {
            isAllowed = true;
        }

        return isAllowed;
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

    private Uri getOutputPhotoFile() {
        Log.e(TAG, "getOutputPhotoFile");
        File directory = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), getString(R.string.app_name));
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                Log.e(TAG, "Failed to create storage directory");
                FirebaseCrash.log("Failed to create storage directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(new Date());
        String img = "IMG";
        File file = new File(directory.getPath() + File.separator + img
                + timeStamp + Constants.EXTENSION_JPG);
        return Uri.fromFile(file);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult");
        if (resultCode == RESULT_OK) {
            Uri photoUri = data == null ? mFileUri : data.getData();
            startAddingActivity(photoUri, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case GALLERY_PERMISSION_REQUEST_CODE:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    startGalleryActivity();
                break;
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    startCameraActivity();
                break;
            default:
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void startAddingActivity(Uri path, int photoResource) {
        Log.e(TAG, "startAddingActivity");
        Intent intent = new Intent(this, AddingActivity.class);
        intent.putExtra(Constants.INTENT_KEY_PHOTO_RES, photoResource);
        intent.putExtra(Constants.INTENT_KEY_PATH, path.toString());
        startActivity(intent);
    }

    private void startCameraActivity() {
        Log.e(TAG, "startCameraActivity");
        mFABCamera.setClickable(false);
        mFABGallery.setClickable(false);
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mFileUri = getOutputPhotoFile();
        i.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
        startActivityForResult(i, Constants.CAPTURE_PICTURE_REQUEST);
    }

    private void startGalleryActivity() {
        mFABCamera.setClickable(false);
        mFABGallery.setClickable(false);
        Intent intent = new Intent();
        intent.setType(Constants.MEDIA_TYPE_IMAGE);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                Constants.ACTION_SELECT_PICTURE), Constants.SELECT_PICTURE_REQUEST);
    }

    private void startLoginActivity(boolean accountExists) {
        Log.e(TAG, "startLoginActivity");
        // TODO: 29.06.17 испровить логику. при нажатии на кнопку выйти можно просто удалять акк,
        // потом активити пересоздается и заходит в логин активити
        if (accountExists)
            AccountGeneral.removeAccount(this, (AccountManager) this.getSystemService(ACCOUNT_SERVICE));
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        finish();
        startActivity(intent);
    }

    private void setFabHidingAbility() {
        Log.e(TAG, "setFabHidingAbility");
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {   // Scroll Down
                    if (mFAB.isShown()) {
                        if (isFabOpen) {
                            animateFAB();
                        }
                        mFAB.setClickable(false);
                        mFAB.hide();
                    }
                } else if (dy < 0) {  // Scroll Up
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
            mFAB.startAnimation(mRotateBackward);
            mFABCamera.startAnimation(mFabClose);
            mFABGallery.startAnimation(mFabClose);
            mFABCamera.setClickable(false);
            mFABGallery.setClickable(false);
            isFabOpen = false;
            Log.e(TAG, "animateFAB: close");
        } else {
            mFAB.startAnimation(mRotateForward);
            mFABCamera.startAnimation(mFabOpen);
            mFABGallery.startAnimation(mFabOpen);
            mFABCamera.setClickable(true);
            mFABGallery.setClickable(true);
            isFabOpen = true;
            Log.e(TAG, "animateFAB: open");
        }
    }

    @Override
    public void onChange(Object element) {
        Log.e(TAG, "onChange");
        setRecyclerViewAdapter();
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

    @Override
    public void onRefresh() {
        Log.e(TAG, "onRefresh");
        if (isOnline()) {
            new ServerSyncTask().execute();
        } else             // TODO: 24.05.17 change to a dialog fragment
            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
    }
}