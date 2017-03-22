package com.vsevolod.swipe.addphoto;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private final int THUMBSIZE = 500;
    public static RecyclerView mRecyclerView;
    public static List<Model> data;
    public static String user;
    private boolean isChecked = false;
    private Realm realm;
    public static Context context;


    private static final int CAPTURE_IMAGE_ACTIVITY_REQ = 0;
    Uri fileUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
//to start login activity
//        if (user == null) {
//            Intent intent = new Intent(this, LoginActivity.class);
//            startActivity(intent);
//        }
        data = new ArrayList<>();
        realm = Realm.getDefaultInstance();

        initRealmData();


        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.fab).setOnClickListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        context = getApplicationContext();
        setRecyclerViewAdapter();
    }


    private void initRealmData() {
        Log.d(TAG, "initRealmData");
        //db
        RealmQuery query = realm.where(Model.class);
        RealmResults<Model> results = query.findAllSorted("date", Sort.DESCENDING);
        data = results;
//        int size = data.size();
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        switch (item.getItemId()) {
            case R.id.main_menu_clear_data:
                dropRealm();
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

    private void dropRealm() {
        Log.d(TAG, "dropRealm");

        RealmResults<Model> results = realm.where(Model.class).findAll();

        // All changes to data must happen in a transaction
        realm.beginTransaction();
        // Delete all matches
        results.deleteAllFromRealm();
        realm.commitTransaction();
        setRecyclerViewAdapter();
        Toast.makeText(this, "Данные удалены", Toast.LENGTH_SHORT).show();
    }


    private void setRecyclerViewAdapter() {
        Log.d(TAG, "setRecyclerViewAdapter");
        int i = data.size();
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
//                DialogFragment newFragment = new MyDialogFragment();
//                newFragment.show(getSupportFragmentManager(), "missiles");
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = Uri.fromFile(getOutputPhotoFile());
                i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(i, CAPTURE_IMAGE_ACTIVITY_REQ);


                break;
            default:
                break;
        }
    }

    private void addNewDataItem(@NonNull byte[] byteArray, @NonNull String photoUri) {
        Log.d(TAG, "addNewDataItem");
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("E HH:mm:ss  dd.MM.yyyy");
        String formattedDate = df.format(c.getTime());


        Model model = new Model(formattedDate, "@2101", photoUri, byteArray);
        saveToRealm(model);
//        data.add(model);
        setRecyclerViewAdapter();
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


    private void saveToRealm(Model model) {
        Log.d(TAG, "saveToRealm");
        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();

        // Create an object
        Model newModel = realm.createObject(Model.class);

        // Set its fields
        newModel.setDate(model.getDate());
        newModel.setPath(model.getPath());
        newModel.setPhoto(model.getPhoto());
        newModel.setPhotoURI(model.getPhotoURI());

        realm.commitTransaction();

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
                    Toast.makeText(this, "Image saved successfully",
                            Toast.LENGTH_LONG).show();
                    photoUri = fileUri;
                } else {
                    photoUri = data.getData();
                    Toast.makeText(this, "Image saved successfully in: " + data.getData(),
                            Toast.LENGTH_LONG).show();
                }
                setPic(photoUri.getPath());
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Callout for image capture failed!",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setPic(String photoUri) {
        Log.d(TAG, "setPic");

        File imageFile = new File(photoUri);
        if (imageFile.exists()) {

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            //to compress full size photo use commented strings below
//        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            //compress thumbnail
            Bitmap thumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imageFile.getAbsolutePath()),
                    THUMBSIZE, THUMBSIZE);
            thumbImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            addNewDataItem(byteArray, photoUri);
        }
    }
}
