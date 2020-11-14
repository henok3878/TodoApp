package com.example.todoapp.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.widget.CompoundButtonCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.todoapp.R;
import com.example.todoapp.Util.ColorUtil;
import com.example.todoapp.Util.Constants;
import com.example.todoapp.data.TaskEntry;
import com.example.todoapp.viewModel.AddTaskViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddTaskBottomSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener {


    private TextInputEditText taskNameInput;
    private MaterialButton priorityButton;
    private MaterialButton dueDateButton;
    private MaterialButton reminderButton;
    private MaterialButton repeatButton;
    private ImageButton saveTaskButton;
    private AddTaskViewModel addTaskViewModel;
    private View priorityIndicator;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_task_bottom_sheet,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        taskNameInput = getView().findViewById(R.id.bottom_sheet_task_name);
        priorityButton = getView().findViewById(R.id.add_task_set_priority_button);
        priorityButton.setOnClickListener(this);
        dueDateButton = getView().findViewById(R.id.add_task_set_due_button);
        dueDateButton.setOnClickListener(this);
        reminderButton = getView().findViewById(R.id.add_task_set_reminder_button);
        reminderButton.setOnClickListener(this);
        repeatButton = getView().findViewById(R.id.add_task_set_repetition_button);
        repeatButton.setOnClickListener(this);
        saveTaskButton = getView().findViewById(R.id.add_task_save_button);
        saveTaskButton.setOnClickListener(this);
        priorityIndicator = getView().findViewById(R.id.add_task_priority_indicator);
        addTaskViewModel = ViewModelProviders.of(this).get(AddTaskViewModel.class);
        setPriorityButtonSelectedColor(Constants.PRIORITY_4);

        showKeyboard();
    }


  private void showKeyboard(){
      taskNameInput.requestFocus();
      InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
      inputMethodManager.showSoftInput(taskNameInput,InputMethodManager.SHOW_IMPLICIT);
  }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_task_set_priority_button:
                showPriorityMenu(view);
                break;
            case R.id.add_task_set_due_button:
                Toast.makeText(getActivity(), "Due Button clicked...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.add_task_set_reminder_button:
                Toast.makeText(getActivity(), "Reminder button clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.add_task_set_repetition_button:
                Toast.makeText(getActivity(), "Repeatition button clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.add_task_save_button:
                // insert task
                insertTask();
                dismiss();
                break;
        }
    }

    @SuppressLint("RestrictedApi")
    public void showPriorityMenu(View view){
        PopupMenu popupMenu = new PopupMenu(getActivity(),view);
        popupMenu.setOnMenuItemClickListener(priorityPopMenuListener);
        popupMenu.inflate(R.menu.priority_menu_item);
        @SuppressLint("RestrictedApi")
        MenuPopupHelper menuPopupHelper = new MenuPopupHelper(getActivity(),(MenuBuilder)popupMenu.getMenu(),view);
        menuPopupHelper.setForceShowIcon(true);
        menuPopupHelper.setGravity(Gravity.START);
        menuPopupHelper.show();
    }

    private void insertTask(){
        addTaskViewModel.setName(taskNameInput.getText().toString());
        addTaskViewModel.insertTask();
    }

    PopupMenu.OnMenuItemClickListener priorityPopMenuListener = new PopupMenu.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int priority = Constants.PRIORITY_4;
            switch (item.getItemId()){
                case R.id.priority_1:
                    priority = Constants.PRIORITY_1;
                    break;
                case R.id.priority_2:
                    priority = Constants.PRIORITY_2;
                    break;
                case R.id.priority_3:
                    priority = Constants.PRIORITY_3;
                    break;
                case R.id.priority_4:
                    priority = Constants.PRIORITY_4;
                    break;
                default:
                    return false;
            }
            setPriorityButtonSelectedColor(priority);
            addTaskViewModel.setPriority(priority);
            return true;
        }
    };

    private void setPriorityButtonSelectedColor(int priority){
        priorityIndicator.setBackgroundColor(ColorUtil.getPriorityColor(getActivity(),priority));
    }
}
