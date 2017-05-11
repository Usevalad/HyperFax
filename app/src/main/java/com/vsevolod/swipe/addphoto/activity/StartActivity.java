package com.vsevolod.swipe.addphoto.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.command.MyasoApi;
import com.vsevolod.swipe.addphoto.command.method.Authentication;
import com.vsevolod.swipe.addphoto.command.method.GetTree;
import com.vsevolod.swipe.addphoto.model.realm.FlowsTreeModel;

import java.util.ArrayList;
import java.util.List;

public class StartActivity extends AppCompatActivity {
    private final String TAG = "StartActivity";
    public static List<FlowsTreeModel> list = new ArrayList<>();
    private MyasoApi api = new MyasoApi();
    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

//        ConnectivityManager cm =
//                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//        boolean isConnected = false;
//        NetworkInfo mWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//        NetworkInfo mMobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//
//        if (mWifi.isAvailable() == false && mMobile.isAvailable() == false) {
//            Toast.makeText(mContext, ",asmd", Toast.LENGTH_SHORT).show();
//            Log.d(TAG, "onCreate: no internet connection");
//        }
//        if(cm.getActiveNetworkInfo() != null){
//            isConnected = cm.getActiveNetworkInfo().isConnected();
//            Log.e(TAG, "onCreate: isConnected " + isConnected);
//            NetworkInfo info = cm.getActiveNetworkInfo();
//            info.toString();
//
//        } else {
//            Log.e(TAG, "onCreate: isConnected = null");
//        }


        getServerAccess();
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