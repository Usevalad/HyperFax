package com.vsevolod.swipe.addphoto.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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
import com.vsevolod.swipe.addphoto.command.MyasoApi;
import com.vsevolod.swipe.addphoto.command.method.GetList;
import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.config.PathConverter;
import com.vsevolod.swipe.addphoto.config.PreferenceHelper;
import com.vsevolod.swipe.addphoto.config.RealmHelper;
import com.vsevolod.swipe.addphoto.model.query.ListModel;
import com.vsevolod.swipe.addphoto.model.query.TokenModel;
import com.vsevolod.swipe.addphoto.model.realm.DataModel;
import com.vsevolod.swipe.addphoto.model.responce.ResponseFlowsTreeModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.RealmChangeListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// FIXME: 21.04.17 find memory leak
// FIXME: 21.04.17 fix memory leak
// FIXME: 21.04.17 make recyclerView item flexible, fix two-line prefix with comment, also different screen sizes
// FIXME: 21.04.17 check onActivityResult, looks horribly
// TODO: 21.04.17 add internet connection checker
// FIXME: 21.04.17 handle hardware back button onClick (show dialog fragment: "do you really want quit?")
// TODO: 13.05.17 add feature: когда я нажимаю обновить дерево, но у меня нет токена.аккаунта - я отправляюсь
// в логин активити, где получаю токен, но после это просто попадаю в мэин активити. Надо сделать так,
// что бы после получения токена продолжилось действие, которое проверяло наличие этого токена.
// то есть если я нажимал обновить дерево, то после получения токена оно начало обновляться без
// очередного вмешательства юзера
// TODO: 13.05.17 add some settings in account menu (shared prefs)
// TODO: 13.05.17 if creates new account - need to update flowsTree
// TODO: 13.05.17 set onRealmChangeListener to make recyclerView not static
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int CAPTURE_IMAGE_ACTIVITY_REQ = 31;
    private static final int SELECT_PICTURE = 12;
    public static RecyclerView mRecyclerView; // static recycler can be memory leak
    public static List<DataModel> data;
    private PreferenceHelper mPreferenceHelper = new PreferenceHelper();
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
    private MyasoApi api = new MyasoApi();
    private AccountManager mAccountManager;
    private String mAuthToken;
    private RealmChangeListener realmChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
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
        realmChangeListener = new RealmChangeListener() {
            @Override
            public void onChange(Object element) {
                setRecyclerViewAdapter();
                Log.e(TAG, "onChange: invalidate");
            }
        };
        mRealmHelper.getRealm().addChangeListener(realmChangeListener);
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
                setRecyclerViewAdapter();
                break;
            case R.id.main_menu_repeat_download:
                GetList list = new GetList(api);
                if (mRealmHelper.dataQueue().size() < 1) {
                    break;
                }
                String[] ids = new String[mRealmHelper.dataQueue().size()];
                for (int i = 0; i < mRealmHelper.dataQueue().size(); i++) {
                    ids[i] = mRealmHelper.dataQueue().get(i);
                }
                ListModel model = new ListModel(mPreferenceHelper.getToken(), ids);
                list.execute(model);
                Toast.makeText(this, "Идет загрузка", Toast.LENGTH_SHORT).show(); // FIXME: 11.05.17 hardcode
                break;
            case R.id.main_menu_notifications:
                isChecked = !item.isChecked();
                item.setChecked(isChecked);
                Toast.makeText(this, isChecked ? "Вкл" : "Выкл", Toast.LENGTH_SHORT).show();// FIXME: 11.05.17 hardcode
                mRealmHelper.countData();
                break;
            case R.id.main_menu_request_flow:
                getTree();
                break;
            case R.id.main_menu_log_out:
                Intent intent = new Intent(this, LoginActivity.class);
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
                // TODO: 21.04.17 wrap in a method
                startCameraActivity();
                break;
            case R.id.fab_gallery:
                mFABCamera.setClickable(false);
                mFABGallery.setClickable(false);
                // TODO: 21.04.17 wrap this in a method
                Intent intent = new Intent();
                intent.setType("image/*");// FIXME: 11.05.17 hardcode
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);// FIXME: 11.05.17 hardcode
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HH_mm_ss", Locale.US).format(new Date());

        return new File(directory.getPath() + File.separator + "IMG_"// FIXME: 11.05.17 hardcode
                + timeStamp + ".jpg");// FIXME: 11.05.17 hardcode
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
                    Toast.makeText(this, "Image saved successfully in: " + data.getData(),// FIXME: 11.05.17 hardcode
                            Toast.LENGTH_LONG).show();
                }
                startAddingActivity(photoUri.getPath());

            } else if (requestCode == SELECT_PICTURE) {
                photoUri = data.getData();
                String path = PathConverter.getFullPath(photoUri);
                startAddingActivity(path);
            } else {
                Toast.makeText(this, "Call out for image capture failed!",// FIXME: 11.05.17 hardcode
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startAddingActivity(String path) {
        Log.d(TAG, "startAddingActivity");
        Intent intent = new Intent(this, AddingActivity.class);
        intent.putExtra("path", path);// FIXME: 11.05.17 hardcode
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

    private void getTree() {
        Log.d(TAG, "getTree");
        mAccountManager = AccountManager.get(this);
        Account[] acc = mAccountManager.getAccountsByType(AccountGeneral.ARG_ACCOUNT_TYPE);

        if (acc.length == 0) {
            Log.e(TAG, "No accounts of type " + AccountGeneral.ARG_ACCOUNT_TYPE + " found");
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra(AccountGeneral.ARG_IS_ADDING_NEW_ACCOUNT, true);
            startActivity(intent);
            return;
        }
        Account account = acc[0];
        startAuthTokenFetch(account);
    }

    private void startAuthTokenFetch(Account account) {
        Log.d(TAG, "startAuthTokenFetch");
        Bundle options = new Bundle();
        android.os.Handler handler = new android.os.Handler();
        OnAccountManagerComplete callBack = new OnAccountManagerComplete();
        mAccountManager.getAuthToken(
                account,
                AccountGeneral.ARG_TOKEN_TYPE,
                options,
                this,
                callBack,
                handler
        );
    }

    private class OnAccountManagerComplete implements AccountManagerCallback<Bundle> {
        private final String TAG = this.getClass().getSimpleName();

        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            Bundle bundle;
            Log.e(TAG, "run");
            try {
                bundle = result.getResult();
            } catch (OperationCanceledException e) {
                e.printStackTrace();
                return;
            } catch (AuthenticatorException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            mAuthToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
            Log.e(TAG, "Received authentication token " + mAuthToken);
            MyApplication.getApi().getTree(new TokenModel(mAuthToken)).enqueue(new Callback<ResponseFlowsTreeModel>() {
                @Override
                public void onResponse(Call<ResponseFlowsTreeModel> call, Response<ResponseFlowsTreeModel> response) {
                    if (response.isSuccessful()) {
                        List<List<String>> list = response.body().getList();
                        if (list != null) {
                            Log.d(TAG, list.toString());
                            Log.d(TAG, list.get(0).toString());

                            TreeConverterTask task = new TreeConverterTask();
                            task.execute(response.body());
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseFlowsTreeModel> call, Throwable t) {
                    Log.e(TAG, "onFailure: ", t);
                }
            });
        }
    }
}