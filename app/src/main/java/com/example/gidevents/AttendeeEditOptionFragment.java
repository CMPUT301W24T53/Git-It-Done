package com.example.gidevents;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AttendeeEditOptionFragment extends DialogFragment {
    private AttendeeProfileEditOption option;

    interface EditOptionDialogListener{
        void editOption(AttendeeProfileEditOption option);
    }
    private EditOptionDialogListener listener;

    public AttendeeEditOptionFragment (AttendeeProfileEditOption option){
        this.option = option;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof EditOptionDialogListener){
            listener = (EditOptionDialogListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement Listener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.attendee_edit_option_fragment, null);
        TextView editOption  = view.findViewById(R.id.attendeeEditOption);
        editOption.setText(option.getCurrentvalue());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        return builder
                .setView(view)
                .setTitle(option.getOptionType())
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", (dialog, which) ->{
                    listener.editOption(new AttendeeProfileEditOption(option.getOptionType(), editOption.getText().toString()));
                })
                .create();
    }
}
