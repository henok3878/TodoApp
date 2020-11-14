package com.example.todoapp.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.todoapp.data.Repository;
import com.example.todoapp.data.TaskEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private Repository dataRepo;
    private LiveData<List<TaskEntry>> activeTaskEntries;

    public MainViewModel(@NonNull Application application) {
        super(application);
        dataRepo = Repository.getInstance(application);
        activeTaskEntries = dataRepo.getActiveTasks();
    }


    public LiveData<List<TaskEntry>> getTaskEntries() {
        return activeTaskEntries;
    }

    public void updateTaskEntry(TaskEntry entry){
        dataRepo.updateTask(entry);
    }
    public void deleteTaskEntry(TaskEntry entry){dataRepo.deleteTask(entry);}
}
