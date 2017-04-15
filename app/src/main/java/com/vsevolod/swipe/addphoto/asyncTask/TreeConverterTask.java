package com.vsevolod.swipe.addphoto.asyncTask;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.config.RealmHelper;
import com.vsevolod.swipe.addphoto.model.realm.FlowsTreeModel;
import com.vsevolod.swipe.addphoto.model.responce.ResponseFlowsTreeModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vsevolod on 09.04.17.
 */

public class TreeConverterTask extends AsyncTask<ResponseFlowsTreeModel, String, List<FlowsTreeModel>> {
    private final String TAG = "TreeConverterTask";
    private RealmHelper mRealmHelper = new RealmHelper();

    @Override
    protected List<FlowsTreeModel> doInBackground(ResponseFlowsTreeModel... params) {
        Log.d(TAG, "doInBackground");
        mRealmHelper.open();
        List<FlowsTreeModel> result = new ArrayList<>();
        ResponseFlowsTreeModel model = params[0];
        List<List<String>> list = model.getList();
        List<String> tmp;

        if (list.size() > 100) {
            mRealmHelper.dropRealmTree();
            for (int i = 0; i < list.size(); i++) {
                tmp = list.get(i);
                if (tmp.size() == 4) {
                    mRealmHelper.save(new FlowsTreeModel(tmp.get(0), tmp.get(1), tmp.get(2), tmp.get(3)));
                    publishProgress(String.valueOf(i));
                }
            }
        }
        return result;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Log.d(TAG, "onProgressUpdate: count = " + values[0].toString());
    }

    @Override
    protected void onPostExecute(List<FlowsTreeModel> flowsTreeModels) {
        super.onPostExecute(flowsTreeModels);
        Toast.makeText(MyApplication.getAppContext(), "Обновлено", Toast.LENGTH_SHORT).show();
    }
}
