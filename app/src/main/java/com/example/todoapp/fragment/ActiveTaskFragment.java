package com.example.todoapp.fragment;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.EditTaskActivity;
import com.example.todoapp.R;
import com.example.todoapp.TaskAdapter;
import com.example.todoapp.data.TaskEntry;
import com.example.todoapp.viewModel.MainViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ActiveTaskFragment extends Fragment implements TaskAdapter.OnItemClickListener{

    public static final String EXTRA_ID_KEY = "task_id_key";
    private TaskAdapter adapter;
    private MainViewModel mainViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.active_tasks_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = getView().findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TaskAdapter(getContext(),this);
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        LiveData<List<TaskEntry>> taskEntryLiveData = mainViewModel.getTaskEntries();
        taskEntryLiveData.observe(getActivity(), new Observer<List<TaskEntry>>() {
            @Override
            public void onChanged(List<TaskEntry> taskEntries) {
                adapter.setTaskEntryList(taskEntries);
            }

        });
        adapter.setTaskEntryList(mainViewModel.getTaskEntries().getValue());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        FloatingActionButton floatingActionButton = getView().findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo don't forger to change EditTaskActivity to editorActivity later
                //Intent intent = new Intent(getActivity(), EditTaskActivity.class);
                //startActivity(intent);
                AddTaskBottomSheetDialog addTaskBottomSheetDialog = new AddTaskBottomSheetDialog();
                addTaskBottomSheetDialog.show(getFragmentManager(),"Add task bottom sheet");
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallBack);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    @Override
    public void onItemViewStatusChange(RecyclerView.ViewHolder viewHolder) {
        createTaskCompletedSnackBar(viewHolder);
    }

    @Override
    public void onItemViewClick(int id) {
        Intent intent = new Intent(getActivity(), EditTaskActivity.class);
        intent.putExtra(EXTRA_ID_KEY,id);
        startActivity(intent);
    }

    ItemTouchHelper.Callback itemTouchHelperCallBack = new ItemTouchHelper.Callback() {

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.DOWN | ItemTouchHelper.UP;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int from = viewHolder.getAdapterPosition();
            int to = target.getAdapterPosition();
            Collections.swap(adapter.getTaskEntryList(), from, to);
            adapter.notifyItemMoved(from, to);
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            try {
                if (direction == ItemTouchHelper.END) {
                    createTaskDeletePendingSnackBar(viewHolder);
                } else {
                    createTaskCompletedSnackBar(viewHolder);
                }

            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage());
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.recycler_view_item_swipe_left_background))
                    .addSwipeLeftActionIcon(R.drawable.ic_done)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.recycler_view_item_swipe_right_background))
                    .addSwipeRightActionIcon(R.drawable.ic_delete)
                    .addSwipeRightLabel("Delete")
                    .setSwipeRightLabelColor(Color.WHITE)
                    .addSwipeLeftLabel("Completed")
                    .setSwipeLeftLabelColor(Color.WHITE)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }
    };

    private void createTaskDeletePendingSnackBar(final RecyclerView.ViewHolder viewHolder){
        final int position = viewHolder.getAdapterPosition();
        final TaskEntry entry = adapter.removeItem(position);
        Snackbar snackbar = Snackbar.make(viewHolder.itemView, "Task " + entry.getName() + " deleted.", Snackbar.LENGTH_LONG);
        snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                    mainViewModel.deleteTaskEntry(entry);
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

    private void createTaskCompletedSnackBar(RecyclerView.ViewHolder viewHolder){
        final int position = viewHolder.getAdapterPosition();
        final TaskEntry entry = adapter.removeItem(position);
        Snackbar snackbar = Snackbar.make(viewHolder.itemView, "Task " + entry.getName() + " completed.", Snackbar.LENGTH_SHORT);
        snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                    entry.setCompleted(true);
                    mainViewModel.updateTaskEntry(entry);
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
