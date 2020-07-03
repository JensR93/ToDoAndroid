package com.jens.ToDo.model.impl;

import android.content.Context;

import androidx.room.Room;

import com.jens.ToDo.model.ToDo;
import com.jens.ToDo.model.User;
import com.jens.ToDo.model.abstracts.ToDoDatabase;
import com.jens.ToDo.model.interfaces.IToDoCRUDOperations;
import com.jens.ToDo.model.interfaces.ToDoDao;
import com.jens.ToDo.ui.Main.MainActivity;

import java.util.List;

import retrofit2.Call;

public class RoomToDoCRUDOperationsImpl implements IToDoCRUDOperations {

    private ToDoDao toToDao;

    public RoomToDoCRUDOperationsImpl(Context context) {
        ToDoDatabase db = Room.databaseBuilder(context.getApplicationContext(),ToDoDatabase.class,"ToDo-db").build();
        toToDao=db.getDao();
    }

    @Override
    public ToDo createItem(ToDo item) {
        if(item!=null)
        {
            long returnID= toToDao.create(item);
            item.setId(returnID);
        }
        return item;
    }

    @Override
    public List<ToDo> readAllItems() {
        List<ToDo> toDoList = toToDao.readAll();
        return toDoList;
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

    @Override
    public boolean syncAllItemsWithLocal() {
        return false;
    }
    @Override
    public boolean syncAllItemsWithRemote(MainActivity activity) {
        return false;
    }

    @Override
    public Call<Boolean> authenticateUser(User user) {
        return null;
    }
}
