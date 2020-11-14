package com.example.todoapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDoa {

    @Query("SELECT * FROM tasks")
    LiveData<List<TaskEntry>> getAllTasks();
    @Query("SELECT * FROM tasks WHERE id = :id")
    LiveData<TaskEntry> getTaskWithId(int id);
    @Insert
    void insertTask(TaskEntry taskEntry);
    @Update
    void updateTask(TaskEntry taskEntry);
    @Delete
    void deleteTask(TaskEntry taskEntry);
    @Query("DELETE FROM tasks")
    void deleteAllTasks();
    @Query("SELECT * FROM tasks WHERE isCompleted = 1")
    LiveData<List<TaskEntry>> getCompletedTasks();
    @Query("SELECT * FROM tasks WHERE isCompleted = 0")
    LiveData<List<TaskEntry>> getActiveTasks();

}
