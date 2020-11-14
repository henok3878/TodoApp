package com.example.todoapp.Util;

import android.content.Context;
import android.util.TypedValue;

import androidx.core.content.ContextCompat;

import com.example.todoapp.R;

public class ColorUtil {
    private ColorUtil(){

    }

    public static int  getPriorityColor(Context context, int priority){
        switch (priority){
            case Constants.PRIORITY_1:
                return context.getResources().getColor(R.color.high_priority_color);
            case Constants.PRIORITY_2:
                return context.getResources().getColor(R.color.medium_priority_color);
            case Constants.PRIORITY_3:
                return context.getResources().getColor(R.color.low_priority_color);
            default:
                // here choose default icon color of the theme
                TypedValue typedValue = new TypedValue();
                context.getTheme().resolveAttribute(R.attr.colorControlNormal, typedValue, true);
                return ContextCompat.getColor(context, typedValue.resourceId);
        }
    }

}
