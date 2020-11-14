package com.example.todoapp.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;


@Entity(tableName = "tasks")
public class TaskEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String description;
    private int priority;
    private Date date;
    private boolean isCompleted;

    @Ignore
    private int colorId;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    public Date getDate() {
        return date;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    @Ignore
    public TaskEntry(String name, String description, int priority, Date date, boolean isCompleted) {
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.date = date;
        this.isCompleted = isCompleted;
    }

    public TaskEntry(int id, String name, String description, int priority, Date date, boolean isCompleted) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.date = date;
        this.isCompleted = isCompleted;
    }
    public void setCompleted(boolean isCompleted){
        this.isCompleted = isCompleted;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }




}
