package com.example.anant.smartattendancemanager.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.anant.smartattendancemanager.R;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class AttendanceDialogFragment extends DialogFragment {

    String key;

    public interface NoticeDialogListener {
        void onDialogPositiveClick(HashMap<String, Object> result);
    }

    public void setArguments(String key) {
        this.key = key;
    }

    NoticeDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString()
                    + " must implement NoticeDialogListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_attendance, null);
        final EditText attendedEditText = v.findViewById(R.id.class_attended);
        final EditText conductedEditText = v.findViewById(R.id.class_conducted);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(v)
                // Add action buttons
                .setPositiveButton(R.string.add, (dialog, id) -> {
                    String classAttended = attendedEditText.getText().toString();
                    String classConducted = conductedEditText.getText().toString();
                    if (classAttended.length() > 0
                            && classConducted.length() > 0) {
                        HashMap<String, Object> result = new HashMap<>();
                        result.put(key, (classAttended) + "/" + (classConducted));
                        // Send the positive button event back to the host activity
                        mListener.onDialogPositiveClick(result);
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    //Close the Dialog and do nothing
                    AttendanceDialogFragment.this.getDialog().cancel();
                });
        return builder.create();

    }
}
