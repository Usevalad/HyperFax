package com.vsevolod.swipe.addphoto.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.command.MyasoApi;
import com.vsevolod.swipe.addphoto.command.method.Authentication;
import com.vsevolod.swipe.addphoto.command.method.Check;
import com.vsevolod.swipe.addphoto.command.method.GetTree;
import com.vsevolod.swipe.addphoto.model.realm.FlowsTreeModel;

import java.util.ArrayList;
import java.util.List;

public class StartActivity extends AppCompatActivity {
    private final String TAG = "StartActivity";
    public static List<FlowsTreeModel> list = new ArrayList<>();
    private MyasoApi api = new MyasoApi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Check check = new Check(api);
        check.execute();

//        getServerAccess();
//        getTree();
        // 11.04.17 start next activity from here
    }

    private void getServerAccess() {
        Log.d(TAG, "getServerAccess");
        Authentication auth = new Authentication(api);
        auth.execute();
    }


    private void getTree() {
        Log.d(TAG, "getTree");
        GetTree tree = new GetTree(api);
        tree.execute();
    }
}