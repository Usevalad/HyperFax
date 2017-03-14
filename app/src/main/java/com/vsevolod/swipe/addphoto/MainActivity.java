package com.vsevolod.swipe.addphoto;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    public static RecyclerView mRecyclerView;
    public static List<Model> data;
    public static String user;
    private boolean isChecked = false;
    private Realm realm;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static byte[] byteArray;
    public static Bitmap imageBitmap;
    public static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
//to start login activity
//        if (user == null) {
//            Intent intent = new Intent(this, LoginActivity.class);
//            startActivity(intent);
//        }
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
        //db
        RealmQuery query = realm.where(Model.class);
        RealmResults<Model> results = query.findAllSorted("date", Sort.DESCENDING);
        data = results;
        int size = data.size();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem checkable = menu.findItem(R.id.main_menu_notifications);
        checkable.setChecked(isChecked);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        int i = data.size();
        if (data != null) {
            mRecyclerView.setAdapter(new MyRecyclerAdapter(this, data));
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
//                DialogFragment newFragment = new MyDialogFragment();
//                newFragment.show(getSupportFragmentManager(), "missiles");

                dispatchTakePictureIntent();
                break;
            default:
                break;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byteArray = stream.toByteArray();
            int sasda = byteArray.length;

        }
        addNewDataItem();
    }

    private void addNewDataItem() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("E HH:mm  dd.MM.yyyy");
        String formattedDate = df.format(c.getTime());


        Model model = new Model(formattedDate, "some path", byteArray);
        saveToRealm(model);
//        data.add(model);
        setRecyclerViewAdapter();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
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
        Toast.makeText(this, "delete", Toast.LENGTH_SHORT).show();
//        long i =  info.id; // nullPointerException
    }


    private void saveToRealm(Model model) {
        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();

        // Create an object
        Model newModel = realm.createObject(Model.class);

        // Set its fields
        newModel.setDate(model.getDate());
        newModel.setPath(model.getPath());
        newModel.setPhoto(model.getPhoto());

        realm.commitTransaction();

    }

}
