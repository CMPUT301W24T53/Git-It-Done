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

/** This is the fragment/pop up for confirming deletion of event
 * It displays the message "are you sure ?", where the user can choose to delete or go back
 * Once clicked on DELETE it deletes event from database and displays the new list of events
 */
public class DeleteEventFragment extends DialogFragment {

    private EventDialogListener listener;
    // mandatory methods to implement
    interface EventDialogListener {
        void deleteEvent();
    }

    /**
     * Attaches fragment on the activity
     * @param context The context to which the fragment is attached, typically the hosting activity.
     * This context is used for accessing resources or initializing components that require a context.
     * @throws NullPointerException if any required initialization fails or expected context features are missing.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof EventDialogListener){
            listener = (EventDialogListener) context;
        }else{
            throw new RuntimeException(context + " must implement AddCityDialogListener");
        }
    }

    /** OnCreateDialog
     * Runs on creation of the activity and holds most functionality
     * @param savedInstanceState is a bundle containing the recent data
     * which it supplied to {@link #onSaveInstanceState}.
     * This initializes the dialog box.
     */

    //pop up containing message "Are you sure ?"
    // on clicking DELETE, it deletes that specific event
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
