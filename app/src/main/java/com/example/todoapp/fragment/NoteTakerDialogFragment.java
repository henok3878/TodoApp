package com.example.todoapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.todoapp.EditTaskActivity;
import com.example.todoapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;


public class NoteTakerDialogFragment extends DialogFragment {
    private static final String TAG = "NoteTakerDialogFragment";
    private OnNoteSavedListener noteSavedListener;

    private TextInputEditText noteInput;
    private MaterialButton saveButton;
    private MaterialButton cancelButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.custom_dialog,container,false);
    }

    public interface OnNoteSavedListener{
        void onNoteSaved(String note);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            noteSavedListener = (EditTaskActivity)getActivity();
        }
        catch (ClassCastException e){
            Log.d(TAG, "onAttach: ClassCastException "+e.getMessage());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noteInput = getView().findViewById(R.id.dialog_notes_input);
        saveButton = getView().findViewById(R.id.note_dialog_save);
        cancelButton = getView().findViewById(R.id.note_dialog_cancel);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(noteInput.getText() != null) {
                    String note = noteInput.getText().toString();
                    noteSavedListener.onNoteSaved(note);
                }
               getDialog().dismiss();
            }
        });
        Bundle bundle = getArguments();
        if(bundle != null){
            String previousNote = getArguments().getString(EditTaskActivity.PREVIOUS_NOTE_KEY);
            noteInput.setText(previousNote);
        }
        noteInput.requestFocus();
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

    }
}
