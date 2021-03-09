package com.example.mobilegroupproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;

public class MyDialogFragment extends DialogFragment {
    Cursor mCursor;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); //alertdialog example
        if(getTag().equals("add")) {
            builder.setTitle("Add Activity");
            builder.setMessage("Enter Activity Name: ");
            final EditText edit = new EditText(getActivity());
            builder.setView(edit);
            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Uri mNewUri;

                    ContentValues mNewValues = new ContentValues();

                    mNewValues.put(contentProvider.COLUMN_PROFILES, edit.getText().toString());
                    mNewValues.put(contentProvider.COLUMN_TIME, 0);
                    mNewUri = getActivity().getContentResolver().insert(
                            contentProvider.CONTENT_URI, mNewValues);
                    ((MainActivity) getActivity()).createSpinner();
                }
            });

        }
        else if(getTag().equals("remove"))
        {
            builder.setTitle("Remove Activity");
            builder.setMessage("Pick the Activity Name: ");
            Spinner selection = new Spinner(getActivity());
            mCursor = getActivity().getContentResolver().query(contentProvider.CONTENT_URI, null, null, null, null);
            int i = 0;
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
            while (i < mCursor.getCount()) {
                if (mCursor.getCount() > 0) {
                    mCursor.moveToNext();
                    adapter.add(mCursor.getString(1));
                    Log.d("here", mCursor.getString(1));
                    i++;
                }
            }
            selection.setAdapter(adapter);
            builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String mSelectionClause = contentProvider.COLUMN_PROFILES + " = ? ";
                    String[] mSelectionArgs = {selection.getSelectedItem().toString()};
                    int mRowsDeleted = 0;
                    mRowsDeleted = getActivity().getContentResolver().delete(contentProvider.CONTENT_URI, mSelectionClause,
                            mSelectionArgs);
                    ((MainActivity) getActivity()).createSpinner();
                }
            });
            builder.setView(selection);
        }
        else if(getTag().equals("data"))
        {
            Spinner spinner = (Spinner) getActivity().findViewById(R.id.dataSpinner);
            String mSelectionClause = contentProvider.COLUMN_PROFILES + " = ? ";
            String[] mSelectionArgs = { spinner.getSelectedItem().toString()};
            int savedTime = 0;
            mCursor = getActivity().getContentResolver().query(contentProvider.CONTENT_URI, null, mSelectionClause, mSelectionArgs, null);
            if (mCursor.getCount() > 0) {
                mCursor.moveToNext();
                savedTime = mCursor.getInt(2);
            }
            builder.setTitle("In the Activity " + spinner.getSelectedItem().toString() + " you have spent a total of ");
            builder.setMessage(Math.ceil(savedTime/60) + " minutes.");
        }
        return builder.create();
    }

}