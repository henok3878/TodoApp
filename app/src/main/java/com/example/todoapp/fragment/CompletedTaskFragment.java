package com.example.todoapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.TaskAdapter;
import com.example.todoapp.data.TaskEntry;
import com.example.todoapp.viewModel.CompletedTaskViewModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class CompletedTaskFragment extends Fragment implements TaskAdapter.OnItemClickListener{

    private static final String TAG = "CompletedTaskFragment";
    private CompletedTaskViewModel completedTaskViewModel;
    private TaskAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.completed_tasks_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: called");
        super.onViewCreated(view, savedInstanceState);
        adapter = new TaskAdapter(getContext(),this);
        RecyclerView recyclerView = getView().findViewById(R.id.completed_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        completedTaskViewModel  = ViewModelProviders.of(this).get(CompletedTaskViewModel.class);
        LiveData<List<TaskEntry>> completedTasks = completedTaskViewModel.getCompletedTasks();
        completedTasks.observe(getActivity(), new Observer<List<TaskEntry>>() {
            @Override
            public void onChanged(List<TaskEntry> taskEntries) {
                adapter.setTaskEntryList(taskEntries);
            }
        });
        adapter.setTaskEntryList(completedTaskViewModel.getCompletedTasks().getValue());
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onItemViewStatusChange(RecyclerView.ViewHolder viewHolder) {
        createTaskActivateSnackBar(viewHolder);
    }

    @Override
    public void onItemViewClick(int id) {
        // do nothing here
    }



    private void createTaskActivateSnackBar(RecyclerView.ViewHolder viewHolder){
        final int position = viewHolder.getAdapterPosition();
        final TaskEntry entry = adapter.removeItem(position);
        Snackbar snackbar = Snackbar.make(viewHolder.itemView, "Task " + entry.getName() + " activated.", Snackbar.LENGTH_SHORT);
        snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                    entry.setCompleted(false);
                    completedTaskViewModel.updateTask(entry);
                }
            }
        });
        snackbar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    adapter.addItem(entry,position);
                } catch (Exception e) {
                    Log.e("MainActivity", e.getMessage());
                }
            }
        });
        snackbar.show();

    }
}
