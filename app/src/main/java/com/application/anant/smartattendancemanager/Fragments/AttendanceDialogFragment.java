package com.application.anant.smartattendancemanager.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.application.anant.smartattendancemanager.R;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class AttendanceDialogFragment extends DialogFragment {

    String key;
    private EditText attendedEditText;
    private EditText conductedEditText;

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
        attendedEditText = v.findViewById(R.id.class_attended);
        conductedEditText = v.findViewById(R.id.class_conducted);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(v)
                // Add action buttons
                .setPositiveButton(R.string.add, (dialog, id) -> {
                    //Do nothing as we override it later
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    //Close the Dialog and do nothing
                    AttendanceDialogFragment.this.getDialog().cancel();
                });
        return builder.create();

    }

    @Override
    public void onStart() {
        super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point
        AlertDialog d = (AlertDialog) getDialog();
        AtomicBoolean wantToCloseDialog = new AtomicBoolean(false);
        if (d != null) {
            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {

                String classAttended = attendedEditText.getText().toString();
                String classConducted = conductedEditText.getText().toString();
                if (classAttended.length() > 0
                        && classConducted.length() > 0) {
                    if (Integer.parseInt(classAttended) > Integer.parseInt(classConducted)) {
                        Toast.makeText(getContext(), R.string.classes_greater,
                                Toast.LENGTH_SHORT).show();
                        wantToCloseDialog.set(false);
                    } else if(Integer.parseInt(classAttended)<0 || Integer.parseInt(classConducted)<0){
                        Toast.makeText(getContext(), R.string.negative_classes,
                                Toast.LENGTH_SHORT).show();
                        wantToCloseDialog.set(false);
                    } else {

                        HashMap<String, Object> result = new HashMap<>();
                        result.put(key, (classAttended) + "/" + (classConducted));
                        // Send the positive button event back to the host activity
                        mListener.onDialogPositiveClick(result);
                        wantToCloseDialog.set(true);

                    }
                } else {
                    wantToCloseDialog.set(false);
                    Toast.makeText(getContext(), R.string.class_empty, Toast.LENGTH_SHORT).show();
                }
                //Do stuff, possibly set wantToCloseDialog to true then...
                if (wantToCloseDialog.get())
                    dismiss();
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            });
        }
    }
}
