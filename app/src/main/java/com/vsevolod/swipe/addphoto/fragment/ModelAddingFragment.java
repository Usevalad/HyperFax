//package com.vsevolod.swipe.addphoto.fragment;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AutoCompleteTextView;
//import android.widget.ImageView;
//
//import com.vsevolod.swipe.addphoto.R;
//import com.vsevolod.swipe.addphoto.activity.MainActivity;
//import com.vsevolod.swipe.addphoto.config.RealmHelper;
//
//import it.sephiroth.android.library.picasso.Picasso;
//
//public class ModelAddingFragment extends Fragment {
//    private Context mContext;
//    private ImageView mImageView;
//    private AutoCompleteTextView mAutoCompleteTextView;
//    private RealmHelper mRealmHelper;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mContext = getActivity();
//        mRealmHelper = new RealmHelper(mContext);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View rootView = inflater.inflate(R.layout.fragment_model_adding, container, false);
//
//        mImageView = (ImageView) rootView.findViewById(R.id.model_adding_image_view);
//        mAutoCompleteTextView = (AutoCompleteTextView) rootView.findViewById(R.id.model_adding_auto_complete_text_view);
//
//
//        //TODO: add uri here
//        Picasso.with(getActivity().getApplicationContext())
//                .load("add uri")
//                .into(mImageView);
//
//
//        return rootView;
//    }
//}