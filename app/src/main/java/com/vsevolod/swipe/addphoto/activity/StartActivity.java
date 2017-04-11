package com.vsevolod.swipe.addphoto.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.command.method.Authentication;
import com.vsevolod.swipe.addphoto.command.method.GetTree;
import com.vsevolod.swipe.addphoto.command.MyasoApi;
import com.vsevolod.swipe.addphoto.config.PreferenceHelper;
import com.vsevolod.swipe.addphoto.model.realm.FlowsTreeModel;

import java.util.ArrayList;
import java.util.List;

public class StartActivity extends AppCompatActivity {
    private final String TAG = "StartActivity";
    private Context mContext;
    private PreferenceHelper mPreferenceHelper;
    public static List<FlowsTreeModel> list = new ArrayList<>();
    private MyasoApi api = new MyasoApi();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mContext = this;
        mPreferenceHelper = new PreferenceHelper();
        getServerAccess();
        getTree();
    }

    private void getServerAccess() {
        Authentication auth = new Authentication(api);
        auth.execute();
    }


    private void getTree() {
        GetTree tree = new GetTree(api);
        tree.execute();
    }
}