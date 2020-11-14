package com.example.todoapp.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class EditTaskViewModelFactory extends ViewModelProvider.NewInstanceFactory{

    private Application application;
    private int id;
    public EditTaskViewModelFactory(Application application, int id){
        this.id = id;
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new EditTaskViewModel(application,id);
    }
}
