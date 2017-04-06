package com.vsevolod.swipe.addphoto.holder;

/**
 * Created by vsevolod on 06.04.17.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.github.johnkil.print.PrintView;
import com.vsevolod.flowstreelibrary.model.TreeNode;
import com.vsevolod.swipe.addphoto.R;

public class IconTreeItemHolder extends TreeNode.BaseNodeViewHolder<IconTreeItemHolder.IconTreeItem> {
    private TextView tvValue;
    private PrintView arrowView;

    public IconTreeItemHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(final TreeNode node, IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_icon_node, null, false);
        tvValue = (TextView) view.findViewById(R.id.node_value);
        tvValue.setText(value.text);
        arrowView = (PrintView) view.findViewById(R.id.arrow_icon);

        return view;
    }

    @Override
    public void toggle(boolean active) {
        arrowView.setIconText(context.getResources()
                .getString(active ? R.string.ic_keyboard_arrow_down : R.string.ic_keyboard_arrow_right));
    }

    public static class IconTreeItem {
        public String text;

        public IconTreeItem(String text) {
            this.text = text;
        }
    }
}