package com.example.todoapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.widget.CompoundButtonCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.todoapp.Util.ColorUtil;
import com.example.todoapp.Util.Constants;
import com.example.todoapp.data.TaskEntry;
import com.example.todoapp.fragment.ActiveTaskFragment;
import com.example.todoapp.fragment.DatePickerFragment;
import com.example.todoapp.fragment.NoteTakerDialogFragment;
import com.example.todoapp.fragment.TimePickerFragment;
import com.example.todoapp.viewModel.EditTaskViewModel;
import com.example.todoapp.viewModel.EditTaskViewModelFactory;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditTaskActivity extends AppCompatActivity implements NoteTakerDialogFragment.OnNoteSavedListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public static final String PREVIOUS_NOTE_KEY = "previous_note";
    private static final String TAG = "EditTaskActivity";
    private static final int DEFAULT_KEY_VALUE = -1;
    private TextInputEditText noteView;
    private TextInputEditText nameView;
    private ImageButton priorityButton;
    private AppCompatCheckBox isCompletedCheckBox;
    private MaterialButton setDueButton;
    private MaterialButton setReminderButton;
    private boolean isSettingReminder;
    private String selectedDate;
    private int currentTaskId;
    private EditTaskViewModel currentTaskViewModel;
    private LiveData<TaskEntry> currentTaskEntry;
    private TextView createdDateView;
    private int priority;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_task);
        priorityButton = findViewById(R.id.edit_task_priority_button);
        noteView = findViewById(R.id.editor_notes_view);
        nameView = findViewById(R.id.edit_task_name_field);
        setDueButton = findViewById(R.id.set_due_button);
        createdDateView = findViewById(R.id.created_date_view);
        setReminderButton = findViewById(R.id.set_reminder_button);
        isCompletedCheckBox = findViewById(R.id.editor_checkbox);

        isCompletedCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpCompleteness(isCompletedCheckBox.isChecked());
            }
        });


        nameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    nameView.clearFocus();
                }
                return false;
            }
        });
        noteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoteDialog();
            }
        });


        setDueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSettingReminder = false;
                openDatePicker();
            }
        });

        setReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSettingReminder = true;
                openDatePicker();

            }
        });
        Intent intent = getIntent();
        currentTaskId = intent.getIntExtra(ActiveTaskFragment.EXTRA_ID_KEY,DEFAULT_KEY_VALUE);
        EditTaskViewModelFactory factory = new EditTaskViewModelFactory(this.getApplication(),currentTaskId);
        currentTaskViewModel = ViewModelProviders.of(this,factory).get(EditTaskViewModel.class);
        currentTaskEntry = currentTaskViewModel.getTaskEntry();
        currentTaskEntry.observe(this, new Observer<TaskEntry>() {
            @Override
            public void onChanged(TaskEntry taskEntry) {
                currentTaskEntry.removeObserver(this);
                populateViewFromTaskEntry(taskEntry);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_activity_menu,menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_task_menu_item:
               showDeleteAlertDialog();
                return true;
            case R.id.save_task_menu_item:
                updateTask(createTaskEntryFromInputFields());
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void populateViewFromTaskEntry(TaskEntry entry){
        if(entry != null) {
            nameView.setText(entry.getName());
            String notes = entry.getDescription();
            if(!TextUtils.isEmpty(notes)) {
                noteView.setText(entry.getDescription());
            }
            createdDateView.setText(DateFormat.getDateInstance().format(entry.getDate()));
            priority = entry.getPriority();
            setUpCompleteness(entry.isCompleted());
            setUpPriority(entry.getPriority());
        }


    }

    private void setUpCompleteness(boolean isCompleted){
        setupTextViewStrike(isCompleted);
    }
    private void setUpPriority(int priority){
        setCompletedCheckBoxColor(priority);
        setPriorityButtonColor(priority);
    }
    private void setupTextViewStrike(boolean isCompleted){
        if(isCompleted){
            // here underline on the name of the task
            nameView.setPaintFlags(nameView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            nameView.setPaintFlags(nameView.getPaintFlags() & ~ Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }
    @SuppressLint("RestrictedApi")
    public void showPriorityMenu(View view){
        PopupMenu popupMenu = new PopupMenu(this,view);
        popupMenu.setOnMenuItemClickListener(priorityPopMenuListener);
        popupMenu.inflate(R.menu.priority_menu_item);

        @SuppressLint("RestrictedApi")
        MenuPopupHelper menuPopupHelper = new MenuPopupHelper(this,(MenuBuilder)popupMenu.getMenu(),view);
        menuPopupHelper.setForceShowIcon(true);
        menuPopupHelper.setGravity(Gravity.END);
        menuPopupHelper.show();
    }

    PopupMenu.OnMenuItemClickListener priorityPopMenuListener = new PopupMenu.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                case R.id.priority_1:
                    priority = Constants.PRIORITY_1;
                    setPriorityButtonColor(Constants.PRIORITY_1);
                    setCompletedCheckBoxColor(Constants.PRIORITY_1);
                    return true;
                case R.id.priority_2:
                    priority = Constants.PRIORITY_2;
                    setPriorityButtonColor(Constants.PRIORITY_2);
                    setCompletedCheckBoxColor(Constants.PRIORITY_2);
                    return true;
                case R.id.priority_3:
                    priority = Constants.PRIORITY_3;
                    setPriorityButtonColor(Constants.PRIORITY_3);
                    setCompletedCheckBoxColor(Constants.PRIORITY_3);
                    return true;
                case R.id.priority_4:
                    priority = Constants.PRIORITY_4;
                    setPriorityButtonColor(Constants.PRIORITY_4);
                    setCompletedCheckBoxColor(Constants.PRIORITY_4);
                    return true;
                default:
                    return false;
            }
        }
    };

    @Override
    public void onNoteSaved(String note) {
        noteView.setText(note);
    }
    public void showNoteDialog(){
        Log.d(TAG, "showNoteDialog: called");
        DialogFragment noteDialogFragment = new NoteTakerDialogFragment();
        String previousNote = noteView.getText().toString();
        if (!TextUtils.isEmpty(previousNote) && !previousNote.equals(getString(R.string.tab_here_to_add_notes_label))) {
            Bundle bundle =  new Bundle();
            bundle.putString(PREVIOUS_NOTE_KEY,previousNote);
            noteDialogFragment.setArguments(bundle);
        }

        noteDialogFragment.show(getSupportFragmentManager(),"NoteInputDialog");
    }

    private void setPriorityButtonColor(int priority){
        priorityButton.setColorFilter(ColorUtil.getPriorityColor(this,priority));
    }
    private void setCompletedCheckBoxColor(int priority){
        ColorStateList colorStateList = ColorStateList.valueOf(ColorUtil.getPriorityColor(this,priority));
        CompoundButtonCompat.setButtonTintList(isCompletedCheckBox,colorStateList);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        selectedDate = DateFormat.getDateInstance().format(calendar.getTime());
        if(isSettingReminder){
            openTimePicker();
            return;
        }
        setDueButton.setText(selectedDate);

    }
    private void openDatePicker(){
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(),"date picker");
    }
    private void openTimePicker(){
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.show(getSupportFragmentManager(),"time picker");
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        String time = selectedDate + "," + hourOfDay+":"+minute;
        setReminderButton.setText(time);
    }

    private void updateTask(TaskEntry entry){
        currentTaskViewModel.updateTaskEntry(entry);
    }

    private void deleteTask(){
        currentTaskViewModel.deleteTaskEntry(currentTaskEntry.getValue());
    }

    private TaskEntry createTaskEntryFromInputFields(){
        String name = nameView.getText().toString();
        String notes = noteView.getText().toString();
        Date date = new Date();
        TaskEntry entry = currentTaskEntry.getValue();
        entry.setName(name);
        entry.setCompleted(isCompletedCheckBox.isChecked());
        entry.setPriority(priority);
        entry.setDescription(notes);
        return  entry;
    }

    private void showDeleteAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_message);
        builder.setPositiveButton(getString(R.string.delete_dialog_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // delete is clicked so delete the pet
               deleteTask();
                finish();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel_dialog_button_label), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(dialogInterface != null){
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
