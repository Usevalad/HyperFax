package com.vsevolod.swipe.addphoto.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.config.RealmHelper;

/**
 * Created by vsevolod on 7/18/17.
 */

public class RemovePhotoFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(getString(R.string.app_name));
        alertDialogBuilder.setMessage(getString(R.string.realy_want_to_delete_all_data));
        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialogBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RealmHelper realmHelper = new RealmHelper();
                realmHelper.open();
                realmHelper.dropRealmData();
                realmHelper.close();
            }
        });

        return alertDialogBuilder.create();
    }
}
