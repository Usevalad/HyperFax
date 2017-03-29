package com.vsevolod.swipe.addphoto.activity;

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
import android.widget.AdapterView;
import android.widget.Toast;

import com.vsevolod.swipe.addphoto.Model;
import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.RecyclerView.MyRecyclerAdapter;
import com.vsevolod.swipe.addphoto.config.RealmHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQ = 0;
    public static RecyclerView mRecyclerView;
    public static List<Model> data;
    public static String user;
    public static Context context;
    private boolean isChecked = false;
    private Uri fileUri = null;
    private RealmHelper realmHelper;

    private static final String[] COUNTRIES = new String[]{"Belgium",
            "France", "France_", "Italy", "Germany", "Spain"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        //to start login activity
//        if (user == null) {
//            Intent intent = new Intent(this, LoginActivity.class);
//            startActivity(intent);
//        }

        realmHelper = new RealmHelper(this);
        data = realmHelper.getData();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.fab).setOnClickListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        context = getApplicationContext();
        setRecyclerViewAdapter();

//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction =
//                fragmentManager.beginTransaction();
//        fragmentTransaction.replace(android.R.id.content, new ModelAddingFragment());
//        fragmentTransaction.commit();
//
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRecyclerViewAdapter();
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
                realmHelper.dropRealm();
                setRecyclerViewAdapter();
                break;
            case R.id.main_menu_repeat_download:
                Toast.makeText(this, "Идет загрузка", Toast.LENGTH_SHORT).show();
                break;
            case R.id.main_menu_notifications:
                isChecked = !item.isChecked();
                item.setChecked(isChecked);
                String turnOn;
                if (isChecked) {
                    turnOn = "Вкл";
                } else {
                    turnOn = "Выкл";
                }
                Toast.makeText(this, turnOn, Toast.LENGTH_SHORT).show();
                break;
            case R.id.main_menu_request_flow:
                Toast.makeText(this, "Обновлено", Toast.LENGTH_SHORT).show();
                break;
            case R.id.main_menu_log_out:
                Toast.makeText(this, "Выход", Toast.LENGTH_SHORT).show();
                user = null;
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
            mRecyclerView.setAdapter(new MyRecyclerAdapter(this, data));
        }
    }


    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick");
        switch (v.getId()) {
            case R.id.fab:
                startCameraActivity();
                break;
            default:
                break;
        }
    }

    private void startCameraActivity() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = Uri.fromFile(getOutputPhotoFile());
        i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(i, CAPTURE_IMAGE_ACTIVITY_REQ);
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
                deleteItem(info);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    // TODO: 08.03.17 handle this method
    private void deleteItem(AdapterView.AdapterContextMenuInfo info) {
        Log.d(TAG, "deleteItem");
        Toast.makeText(this, "delete", Toast.LENGTH_SHORT).show();
//        long i =  info.id; // nullPointerException
    }

    private File getOutputPhotoFile() {

        File directory = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), getPackageName());

        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                Log.e(TAG, "Failed to create storage directory.");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss", Locale.US).format(new Date());

        return new File(directory.getPath() + File.separator + "IMG_"
                + timeStamp + ".jpg");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQ) {
            if (resultCode == RESULT_OK) {
                Uri photoUri = null;
                if (data == null) {
                    // A known bug here! The image should have saved in fileUri
                    photoUri = fileUri;
                } else {
                    photoUri = data.getData();
                    Toast.makeText(this, "Image saved successfully in: " + data.getData(),
                            Toast.LENGTH_LONG).show();
                }
                startAddingActivity(photoUri.getPath());

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Call out for image capture failed!",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startAddingActivity(String path) {
        Intent intent = new Intent(this, AddingActivity.class);
        intent.putExtra("path", path);
        startActivity(intent);
    }

}
