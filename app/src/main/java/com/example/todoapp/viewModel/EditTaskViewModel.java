package com.example.todoapp.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.todoapp.data.Repository;
import com.example.todoapp.data.TaskEntry;

public class EditTaskViewModel extends AndroidViewModel {

    private int id;
    private LiveData<TaskEntry> taskEntry;
    private Repository repository;
    public EditTaskViewModel(@NonNull Application application, int id) {
        super(application);
        Log.i("AddTaskViewModelTest","AddTaskViewModel is called with id : " + id);
        repository = Repository.getInstance(application);
        taskEntry = repository.getTaskEntryWithId(id);
        this.id = id;
        Log.i("AddTaskViewModelTest","AddTaskViewModel is called...is LiveData null : " +(taskEntry == null));
    }

    public LiveData<TaskEntry> getTaskEntry() {
        return taskEntry;
    }

    public void updateTaskEntry(TaskEntry entry){
        repository.updateTask(entry);
    }

    public void insertTaskEntry(TaskEntry newEntry){
        repository.insertTask(newEntry);
    }
    public void deleteTaskEntry(TaskEntry entry){repository.deleteTask(entry);}
}
