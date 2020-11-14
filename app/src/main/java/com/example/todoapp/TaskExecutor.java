package com.example.todoapp;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskExecutor {

    private Executor databaseExecutor;
    private static TaskExecutor sTaskExecutor;

    private TaskExecutor(Executor databaseExecutor){
        this.databaseExecutor = databaseExecutor;
    }

    public static synchronized TaskExecutor getInstance(){
        if(sTaskExecutor == null ){
            return new TaskExecutor(Executors.newSingleThreadExecutor());
        }
        return sTaskExecutor;
    }
    public Executor getsExecutor() {
        return databaseExecutor;
    }
}
