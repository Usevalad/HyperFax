package com.vsevolod.swipe.addphoto.recyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.config.RealmHelper;
import com.vsevolod.swipe.addphoto.model.realm.FlowsTreeModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vsevolod on 11.04.17.
 */

public class AutoCompleteAdapter extends BaseAdapter implements Filterable {

    private List<FlowsTreeModel> mResult = new ArrayList<>();

    public AutoCompleteAdapter() {
    }

    @Override
    public int getCount() {
        return mResult.size();
    }

    @Override
    public FlowsTreeModel getItem(int index) {
        return mResult.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) MyApplication.getAppContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.drop_down_item, parent, false);
        }

        String name = getItem(position).getName();
        String prefix = getItem(position).getPrefix();

        ((TextView) convertView.findViewById(R.id.drop_down_name)).setText(name + " | " + prefix);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                return null;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                // filter data in UI thread instead of background one because of Realm limitation:
                // the data cannot be passed across threads
                if (constraint != null) {
                    String query = constraint.toString().toLowerCase();
                    mResult = filterTreeNode(query);
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    @NonNull
    private List<FlowsTreeModel> filterTreeNode(String query) {
        RealmHelper realmHelper = new RealmHelper();
//        realmHelper.open();
        List<FlowsTreeModel> list = realmHelper.searchTree(query);
//        realmHelper.close();
        return list;
    }
}

