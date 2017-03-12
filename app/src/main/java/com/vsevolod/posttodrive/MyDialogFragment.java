package com.vsevolod.posttodrive;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;


public class MyDialogFragment extends DialogFragment {
    public static final String fileName = "modelsList";
    private Context context;
    private EditText editTextPath;
    private EditText editTextImageUrl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_add_new_card, null))
                // Add action buttons
                .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        editTextPath = (EditText) MyDialogFragment.this.getDialog().findViewById(R.id.path_edit_text);
                        editTextImageUrl = (EditText) MyDialogFragment.this.getDialog().findViewById(R.id.image_url_edit_text);
                        String path = editTextPath.getText().toString();
                        String url = editTextImageUrl.getText().toString();
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("E HH:mm  dd.MM.yyyy");
                        String formattedDate = df.format(c.getTime());

                        saveToRealm(formattedDate, path, url);
                        MainActivity.mRecyclerView.setAdapter(new MyRecyclerAdapter(getContext(), MainActivity.data));
//                        saveToInternalFile(context, fileName, MainActivity.data);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MyDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();

    }

    private void saveToRealm(String date, String path, String photoLink) {
        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();

        // Create an object
        Model newModel = realm.createObject(Model.class);

        // Set its fields
        newModel.setDate(date);
        newModel.setPath(path);
        newModel.setPhoto(photoLink);

        realm.commitTransaction();

    }
        //saving to Json file in device storage
    private void saveToInternalFile(Context context, String fileName, List<Model> data) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(data);
        FileOutputStream fos = null;

        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(jsonString.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
