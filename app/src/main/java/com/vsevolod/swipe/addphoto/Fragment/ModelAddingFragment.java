package com.vsevolod.swipe.addphoto;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import it.sephiroth.android.library.picasso.Picasso;

public class ModelAddingFragment extends Fragment {
    private ImageView mImageView;
    private AutoCompleteTextView mAutoCompleteTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_model_adding, container, false);

        mImageView = (ImageView) rootView.findViewById(R.id.model_adding_image_view);
        mAutoCompleteTextView = (AutoCompleteTextView) rootView.findViewById(R.id.model_adding_edit_text);

        Picasso.with(getActivity().getApplicationContext())
                .load("add uri") //TODO add uri here
                .into(mImageView);


        return rootView;
    }

}