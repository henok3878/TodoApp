package com.example.todoapp.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.todoapp.TaskExecutor;
import java.util.List;
import java.util.concurrent.Executor;

public class Repository {

    private static TaskDoa taskDoa;
    private static Repository sInstance;
    private Executor databaseExecutor;

    private Repository(Context context) {
        taskDoa = AppDataBase.getInstance(context).getTaskDoa();
        databaseExecutor = TaskExecutor.getInstance().getsExecutor();
    }

    public static Repository getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new Repository(context);
        }
        return sInstance;
    }

    public LiveData<List<TaskEntry>> getAllTasks() {
        return taskDoa.getAllTasks();
    }

    public void insertTask(final TaskEntry taskEntry) {
        databaseExecutor.execute(new Runnable() {
            @Override
            public void run() {
                taskDoa.insertTask(taskEntry);
            }
        });
    }

    public void updateTask(final TaskEntry taskEntry) {
        databaseExecutor.execute(new Runnable() {
            @Override
            public void run() {
                taskDoa.updateTask(taskEntry);
            }
        });
    }

    public void deleteTask(final TaskEntry taskEntry) {
        databaseExecutor.execute(new Runnable() {
            @Override
            public void run() {
                taskDoa.deleteTask(taskEntry);
            }
        });
    }

    public void deleteAllTasks() {
        databaseExecutor.execute(new Runnable() {
            @Override
            public void run() {
                taskDoa.deleteAllTasks();
            }
        });
    }

    public LiveData<TaskEntry> getTaskEntryWithId(int id) {
        return taskDoa.getTaskWithId(id);
    }

    public LiveData<List<TaskEntry>> getCompletedTasks(){
        return taskDoa.getCompletedTasks();
    }
    public LiveData<List<TaskEntry>> getActiveTasks(){
        return taskDoa.getActiveTasks();
    }
}
