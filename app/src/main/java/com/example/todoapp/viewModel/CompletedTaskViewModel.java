package com.example.todoapp.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.todoapp.data.Repository;
import com.example.todoapp.data.TaskEntry;

import java.util.List;


public class CompletedTaskViewModel extends AndroidViewModel {

    private Repository dataRepo;
    private LiveData<List<TaskEntry>> completedTasks;

    public CompletedTaskViewModel(@NonNull Application application) {
        super(application);
        dataRepo = Repository.getInstance(application);
        completedTasks = dataRepo.getCompletedTasks();
    }

    public LiveData<List<TaskEntry>> getCompletedTasks() {
        return completedTasks;
    }
    public void updateTask(TaskEntry entry){
        dataRepo.updateTask(entry);
    }
}
