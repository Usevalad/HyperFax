package com.vsevolod.swipe.addphoto.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vsevolod.flowstreelibrary.model.TreeNode;
import com.vsevolod.flowstreelibrary.view.AndroidTreeView;
import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.holder.IconTreeItemHolder;
import com.vsevolod.swipe.addphoto.model.Folder;
import com.vsevolod.swipe.addphoto.model.realm.FlowsTreeModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFolderPickedListener} interface
 * to handle interaction events.
 * Use the {@link FlowsTreeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FlowsTreeFragment extends Fragment {
    private static final String TAG = "FlowsTreeFragment";
    private AndroidTreeView mAndroidTreeView;
    private static List<FlowsTreeModel> mTreeModelList = new ArrayList<>();
    private List<Folder> mFolders;
    private Map<String, Folder> mData = new HashMap<>();
    private List<Folder> mParent = new ArrayList<>();
    private OnFolderPickedListener mListener;

    public FlowsTreeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FlowsTreeFragment.
     */
    public static FlowsTreeFragment newInstance(List<FlowsTreeModel> flowsTreeModelList) {
        FlowsTreeFragment fragment = new FlowsTreeFragment();
        if (flowsTreeModelList != null)
            mTreeModelList = flowsTreeModelList;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mTreeModelList != null) {
            mFolders = getFolders();
            mParent = getParents();
            mData = getMap();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_flows_tree, container, false);
        ViewGroup containerView = (ViewGroup) rootView.findViewById(R.id.container);
        TreeNode root = TreeNode.root();
        mapTree();
        addChildrenRec(mParent);
        buildTree(root, mParent);

        mAndroidTreeView = new AndroidTreeView(getActivity(), root);
        mAndroidTreeView.setDefaultAnimation(true);
        mAndroidTreeView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        mAndroidTreeView.setDefaultViewHolder(IconTreeItemHolder.class);
        mAndroidTreeView.setDefaultNodeClickListener(nodeClickListener);

        containerView.addView(mAndroidTreeView.getView());

        mAndroidTreeView.expandLevel(1); // expand first level

        if (savedInstanceState != null) {
            String state = savedInstanceState.getString("tState");
            if (!TextUtils.isEmpty(state)) {
                mAndroidTreeView.restoreState(state);
            }
        }

        return rootView;
    }

    private TreeNode.TreeNodeClickListener nodeClickListener = new TreeNode.TreeNodeClickListener() {
        @Override
        public void onClick(TreeNode node, Object value) {
            IconTreeItemHolder.IconTreeItem item = (IconTreeItemHolder.IconTreeItem) value;
            if (!node.haveChildren()) {
                mListener.onFolderPicked(item.text);
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFolderPickedListener) {
            mListener = (OnFolderPickedListener) context;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tState", mAndroidTreeView.getSaveState());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void mapTree() {
        for (Folder folder : mData.values()) {
            String parentId = folder.getParentId();
            Folder parentFolder = mData.get(parentId);
            if (parentFolder != null)
                parentFolder.addChild(folder);
        }

        int mapSize = mData.size();
        Log.e(TAG, "mapTree: map size: " + mapSize);
    }

    private Map<String, Folder> getMap() {
        Map<String, Folder> data = new HashMap<>();
        for (int i = 0; i < mTreeModelList.size(); i++) {
            data.put(mTreeModelList.get(i).getId(), mFolders.get(i));
        }
        return data;
    }

    private List<Folder> getParents() {
        List<Folder> parent = new ArrayList<>();

        for (int i = 0; i < mTreeModelList.size(); i++) {
            if (mTreeModelList.get(i).getParentId() == null) {
                FlowsTreeModel model = mTreeModelList.get(i);
                parent.add(new Folder(model.getViewName(), model.getId(), model.getParentId(), model.getPrefix()));
            }
        }
        return parent;
    }

    public void addChildrenRec(List<Folder> parents) {
        for (int i = 0; i < parents.size(); i++) {
            addChildren(parents.get(i)); //если айди родителей совпадет с parentId дочерних узлов,
            // то они будут добавлены в массив children
            if (parents.get(i).getChildren().size() > 0) {//если есть дочерние элементы, то
                addChildrenRec(parents.get(i).getChildren());
            }
        }
    }

    private List<Folder> getFolders() {
        List<Folder> folders = new ArrayList<>();
        if (mTreeModelList != null) {
            for (int i = 0; i < mTreeModelList.size(); i++) {
                FlowsTreeModel model = mTreeModelList.get(i);
                folders.add(new Folder(model.getViewName(), model.getId(), model.getParentId(), model.getPrefix()));
            }
        }
        return folders;
    }

    public void addChildren(Folder parent) {
        List<Folder> children = new ArrayList<>();
        for (Folder folder : mData.values()) {
            if (TextUtils.equals(folder.getParentId(), parent.getId())) {
                children.add(folder);
                Log.e(TAG, "addChild: parent " + parent.getName() +
                        " get new child " + folder.getName());
            }
        }

        Collections.sort(children, new Comparator<Folder>() {
            @Override
            public int compare(Folder lhs, Folder rhs) {
                return Integer.parseInt(lhs.getPrefix()) - Integer.parseInt(rhs.getPrefix());
            }
        });

        for (Folder child : children) {
            boolean contains = false;
            for (Folder folder : parent.getChildren()) {
                if (TextUtils.equals(folder.getPrefix(), child.getPrefix())) {
                    contains = true;
                }
            }
            if (!contains) {
                parent.addChild(child);
            }
        }
    }

    public void buildTree(TreeNode root, List<Folder> parent) {
        List<TreeNode> tmp;
        for (int i = 0; i < parent.size(); i++) {
            root.addChild(new TreeNode(new IconTreeItemHolder.IconTreeItem(
                    parent.get(i).getName() + " " + parent.get(i).getPrefix())));
            if (parent.get(i).haveChild()) {

                tmp = root.getChildren();
                for (int j = 0; j < tmp.size(); j++) {
                    TreeNode node = tmp.get(j);
                    IconTreeItemHolder.IconTreeItem item = (IconTreeItemHolder.IconTreeItem) node.getValue();
                    CharSequence text = item.text;
                    if (TextUtils.equals(text, parent.get(i).getName() + " " + parent.get(i).getPrefix())) {
                        buildTree(node, parent.get(i).getChildren());
                    }
                }
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFolderPickedListener {
        void onFolderPicked(String folder);
    }
}