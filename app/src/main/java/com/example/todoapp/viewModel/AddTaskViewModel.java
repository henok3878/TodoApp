package com.example.todoapp.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.todoapp.Util.Constants;
import com.example.todoapp.data.Repository;
import com.example.todoapp.data.TaskEntry;

import java.util.Date;

public class AddTaskViewModel extends AndroidViewModel {
    // by default every task has priority p4
    private int priority = Constants.PRIORITY_4;
    private String name;

    // todo add other fields too
    private Repository dataRepo;
    public AddTaskViewModel(@NonNull Application application) {
        super(application);
        dataRepo = Repository.getInstance(application);
    }

    public void insertTask(){
        TaskEntry entry = new TaskEntry(name,"",priority,new Date(),false);
        dataRepo.insertTask(entry);
    }

    public int getPriority() {
        return priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
