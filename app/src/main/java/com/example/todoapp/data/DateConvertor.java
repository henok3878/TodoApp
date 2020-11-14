package com.example.todoapp.data;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateConvertor {

    @TypeConverter
    public Long DateToLongConvertor(Date date){
        if(date != null){
            return date.getTime();
        }
        return null;
    }
    @TypeConverter
    public Date LongToDateConvertor(Long timeStamp){
        if(timeStamp != null) {
            return new Date(timeStamp);
        }
        return null;
    }
}
