package com.jens.ToDo.model.impl;

import android.content.Context;

import androidx.room.Room;

import com.jens.ToDo.model.ToDo;
import com.jens.ToDo.model.abstracts.ToDoDatabase;
import com.jens.ToDo.model.interfaces.IToDoCRUDOperations;
import com.jens.ToDo.model.interfaces.ToDoDao;

import java.util.List;

public class RoomToDoCRUDOperationsImpl implements IToDoCRUDOperations {

    private ToDoDao toToDao;

    public RoomToDoCRUDOperationsImpl(Context context) {
        ToDoDatabase db = Room.databaseBuilder(context.getApplicationContext(),ToDoDatabase.class,"ToDoNEW-db").build();
        toToDao=db.getDao();
    }

    @Override
    public ToDo createItem(ToDo item) {
        long returnID= toToDao.create(item);
        item.setId(returnID);
        return item;
    }

    @Override
    public List<ToDo> readAllItems() {
        return toToDao.readAll();
    }

    @Override
    public ToDo readItem(long id) {
        return toToDao.readById(id);
    }

    @Override
    public boolean updateItem(ToDo item) {
        toToDao.update(item);
        return true;
    }

    @Override
    public boolean deleteItem(long id) {
        ToDo item = toToDao.readById(id);
        if(item==null)
        {
            return false;
        }
        else{

            toToDao.delete(item);
        }
        return true;
    }

    @Override
    public boolean deleteAllItems() {
        if(toToDao.deleteAll()>=0)
            return true;

        return false;

    }
}
