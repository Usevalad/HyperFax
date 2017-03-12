package com.vsevolod.posttodrive;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        if (user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        realm = Realm.getDefaultInstance();

        initRealmData();


        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.fab).setOnClickListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        setRecyclerViewAdapter();
    }

    private void initRealmData() {
        //db
        RealmQuery query = realm.where(Model.class);
        RealmResults<Model> results = query.findAllSorted("date", Sort.DESCENDING);
        data = results;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem checkable = menu.findItem(R.id.main_menu_notifications);
        checkable.setChecked(isChecked);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
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

    private void clearCachedData() {
        Gson gson = new Gson();
        String json_string = gson.toJson(data);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json_string);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonObject.remove("modelsList");
        data.removeAll(data);

    }

    private void setRecyclerViewAdapter() {
//        data = getCachedData(MyDialogFragment.fileName, ArrayList.class);
        int i = data.size();
        if (!(data == null)) {
            mRecyclerView.setAdapter(new MyRecyclerAdapter(this, data));
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                DialogFragment newFragment = new MyDialogFragment();
                newFragment.show(getSupportFragmentManager(), "missiles");
                break;
            default:
                break;
        }
    }


    private List<Model> getCachedData(String fileName, Class classRef) {
        Log.d(TAG, "getCachedData");
        List<Model> list = new ArrayList<>();
        FileInputStream fis;

        try {
            fis = getApplicationContext().openFileInput(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String content = br.readLine();

            Gson gson = new Gson();
            JSONArray jsonArray = new JSONArray(content);
            List<String> strings = new ArrayList<String>();
            Model model;
            for (int i = 0; i < jsonArray.length(); i++) {
                strings.add(jsonArray.getString(i));
                model = gson.fromJson(jsonArray.getString(i), Model.class);
                list.add(model);
            }

            br.close();
            return list;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //if something went wrong, return null
        return null;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete:
                deleteItem(info);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    // TODO: 08.03.17 handle this method
    private void deleteItem(AdapterContextMenuInfo info) {
        Toast.makeText(this, "delete", Toast.LENGTH_SHORT).show();
//        long i =  info.id; // nullPointerExeption
    }
}
