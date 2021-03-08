package com.example.mobilegroupproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

public class MyDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder adding = new AlertDialog.Builder(getActivity()); //alertdialog example
        adding.setTitle("Add Activity");
        adding.setMessage("Enter Activity Name: ");
        final EditText edit = new EditText(getActivity());
        adding.setView(edit);
        adding.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri mNewUri;

                ContentValues mNewValues = new ContentValues();

                mNewValues.put(contentProvider.COLUMN_PROFILES, edit.getText().toString());

                mNewUri = getActivity().getContentResolver().insert(
                        contentProvider.CONTENT_URI, mNewValues);
            }
        });
        return adding.create();
    }

}
