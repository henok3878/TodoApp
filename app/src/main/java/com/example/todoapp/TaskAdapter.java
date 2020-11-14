package com.example.todoapp;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.widget.CompoundButtonCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.Util.ColorUtil;
import com.example.todoapp.data.TaskEntry;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskEntryViewHolder> {

    private List<TaskEntry> taskEntryList = new ArrayList<>();
    private OnItemClickListener itemClickListener;
    private Context context;
    public TaskAdapter(Context context, OnItemClickListener listener){
        this.context = context;
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public TaskEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.task_item_list,parent,false);

        return new TaskEntryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskEntryViewHolder holder, int position) {
            TaskEntry currentTask = taskEntryList.get(position);
            holder.setUpViews(currentTask);
    }

    @Override
    public int getItemCount() {
        if(taskEntryList != null){
            return taskEntryList.size();
        }
        return 0;
    }

    public List<TaskEntry> getTaskEntryList() {
        return taskEntryList;
    }

    public TaskEntry getTaskEntryAt(int position){
        return taskEntryList.get(position);
    }

    public void setTaskEntryList(List<TaskEntry> taskEntryList) {
        this.taskEntryList = taskEntryList;
        notifyDataSetChanged();
    }

    class TaskEntryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textViewTaskName;
        private TextView textViewTaskDate;
        //private CheckBox markCheckBox;
        private AppCompatCheckBox markCheckBox;
        private RelativeLayout relativeLayout;
        public TaskEntryViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textViewTaskName = itemView.findViewById(R.id.task_name_view);
            textViewTaskDate = itemView.findViewById(R.id.task_date_view);
            relativeLayout = itemView.findViewById(R.id.item_list_background);
            markCheckBox = itemView.findViewById(R.id.mark_check_box);
            markCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   itemClickListener.onItemViewStatusChange(TaskEntryViewHolder.this);
                    setupTextViewStrike(markCheckBox.isChecked());
                }
            });
        }

        public void setupTextViewStrike(boolean isCompleted){
            if(isCompleted){
                // here underline on the name of the task
                textViewTaskName.setPaintFlags(textViewTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            else {
                textViewTaskName.setPaintFlags(textViewTaskName.getPaintFlags() & ~ Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }

        public void setUpViews(TaskEntry taskEntry){
            textViewTaskName.setText(taskEntry.getName());
            textViewTaskDate.setText(DateFormat.getDateInstance().format(taskEntry.getDate()));
            relativeLayout.setBackgroundColor(ColorUtil.getPriorityColor(context,taskEntry.getPriority()));
            markCheckBox.setChecked(taskEntry.isCompleted());
            setupTextViewStrike(taskEntry.isCompleted());
            ColorStateList colorStateList = ColorStateList.valueOf(ColorUtil.getPriorityColor(context,taskEntry.getPriority()));
            CompoundButtonCompat.setButtonTintList(markCheckBox,colorStateList);

        }

        @Override
        public void onClick(View view) {
            int id = taskEntryList.get(getAdapterPosition()).getId();
            itemClickListener.onItemViewClick(id);
        }
    }
    public interface OnItemClickListener {
       void onItemViewClick(int id);
       void onItemViewStatusChange(RecyclerView.ViewHolder viewHolder);

    }

    public TaskEntry removeItem(int position){
        TaskEntry entry = taskEntryList.get(position);
        taskEntryList.remove(position);
        notifyItemRemoved(position);
        return  entry;
    }

    public  void addItem(TaskEntry entry, int position){
        taskEntryList.add(position,entry);
        notifyItemInserted(position);
    }
}
