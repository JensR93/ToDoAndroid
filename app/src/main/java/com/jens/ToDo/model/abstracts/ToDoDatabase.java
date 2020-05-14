package com.jens.ToDo.model.abstracts;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.jens.ToDo.model.ToDo;
import com.jens.ToDo.model.interfaces.ToDoDao;

@Database(entities = {ToDo.class},version=1)
public abstract class ToDoDatabase extends RoomDatabase {
    public abstract ToDoDao getDao();
}
