package com.example.gidevents;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DeleteEventFragment extends DialogFragment {

    interface AddCityDialogListener {
        void deleteEvent();
    }

    private AddCityDialogListener listener;

    // attach fragment on activity
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof AddCityDialogListener){
            listener = (AddCityDialogListener) context;
        }else{
            throw new RuntimeException(context + " must implement AddCityDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_delete_event, null);


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView (view)
                .setTitle("Delete an event")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("DELETE", (dialog, which) -> {
                        listener.deleteEvent();
                })

                .create();
    }
}
