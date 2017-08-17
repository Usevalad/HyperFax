package com.vsevolod.swipe.addphoto.activity;

import android.accounts.AccountManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.accountAuthenticator.AccountGeneral;
import com.vsevolod.swipe.addphoto.adapter.MyRecyclerAdapter;
import com.vsevolod.swipe.addphoto.asyncTask.TreeConverterTask;
import com.vsevolod.swipe.addphoto.config.RealmHelper;
import com.vsevolod.swipe.addphoto.model.realm.DataModel;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity implements MyRecyclerAdapter.DeletePostCallback {
    private final String TAG = this.getClass().getSimpleName();
    private RealmHelper mRealmHelper;
    private List<DataModel> data = new ArrayList<>();
    private String searchString;
    public Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        mRealmHelper = new RealmHelper();
        mRealmHelper.open();
        handleIntent(getIntent());
        setContentView(R.layout.activity_search_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        toolbar.setLogo(R.drawable.ic_toolbar_logo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (data.isEmpty()) {
            RelativeLayout mNoResultsLayout = (RelativeLayout) findViewById(R.id.no_results_layout);
            mNoResultsLayout.setVisibility(View.VISIBLE);
            TextView textView = (TextView) findViewById(R.id.text_view_no_results);
            // FIXME: 7/18/17 hardcode
            textView.setText("По запросу \'" + searchString + "\' ничего не найдено");
        } else {
            RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.search_result_recycler);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setAdapter(new MyRecyclerAdapter(this, data, searchString, this));
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        }
        mContext = getApplicationContext();
    }

    @Override
    protected void onDestroy() {
        mRealmHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        mRealmHelper.close();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mRealmHelper.open();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
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
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            case R.id.main_menu_clear_data:
                mRealmHelper.dropRealmData();
                break;
            case R.id.main_menu_instruction:
                String url = "http://telegra.ph/Instrukciya-HyperFax-06-06";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
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

    private void startLoginActivity() {
        Log.e(TAG, "startLoginActivity");
        // TODO: 29.06.17 испровить логику. при нажатии на кнопку выйти можно просто удалять акк,
        // потом активити пересоздается и заходит в логин активити
        AccountGeneral.removeAccount(this, (AccountManager) this.getSystemService(ACCOUNT_SERVICE));
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        finish();
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent");
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        Log.d(TAG, "handleIntent");
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchString = intent.getStringExtra(SearchManager.QUERY).toLowerCase();
            data = mRealmHelper.search(searchString);
        }
    }

    @Override
    public void deletePost(DataModel model) {
        mRealmHelper.deleteData(model);
    }
}