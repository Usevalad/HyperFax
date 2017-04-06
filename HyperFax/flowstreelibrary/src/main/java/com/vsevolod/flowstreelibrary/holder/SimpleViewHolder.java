package com.vsevolod.flowstreelibrary.holder;

/**
 * Created by vsevolod on 06.04.17.
 */

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.vsevolod.flowstreelibrary.model.TreeNode;


/**
 * Created by Bogdan Melnychuk on 2/11/15.
 */
public class SimpleViewHolder extends TreeNode.BaseNodeViewHolder<Object> {

    public SimpleViewHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, Object value) {
        final TextView tv = new TextView(context);
        tv.setText(String.valueOf(value));
        return tv;
    }

    @Override
    public void toggle(boolean active) {

    }
}