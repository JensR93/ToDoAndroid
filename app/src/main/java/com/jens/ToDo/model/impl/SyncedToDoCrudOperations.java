package com.jens.ToDo.model.impl;

import com.jens.ToDo.model.ToDo;
import com.jens.ToDo.model.User;
import com.jens.ToDo.model.interfaces.IToDoCRUDOperations;

import java.util.List;

import retrofit2.Call;

public class SyncedToDoCrudOperations implements IToDoCRUDOperations {
    private IToDoCRUDOperations localCrud;
    private  IToDoCRUDOperations remoteCrud;
    public SyncedToDoCrudOperations(IToDoCRUDOperations localCrud, IToDoCRUDOperations remoteCrud) {
        this.localCrud = localCrud;
        this.remoteCrud = remoteCrud;
    }



    @Override
    public ToDo createItem(ToDo item) {
        ToDo created = this.localCrud.createItem(item);
        if(created!=null){
            this.remoteCrud.createItem(item);
        }
        return created;
    }

    @Override
    public List<ToDo> readAllItems() {
        return this.localCrud.readAllItems();
    }

    @Override
    public ToDo readItem(long id) {
        return this.localCrud.readItem(id);
    }

    @Override
    public boolean updateItem(ToDo item) {
        if(localCrud.updateItem(item)){
            remoteCrud.updateItem(item);
            return  true;
        }
        return false;
    }

    @Override
    public boolean deleteItem(long id) {
        if(localCrud.deleteItem(id)){
            remoteCrud.deleteItem(id);
            return  true;
        }
        return false;
    }

    @Override
    public boolean deleteAllItems() {
        if(localCrud.deleteAllItems()){
            remoteCrud.deleteAllItems();
            return  true;
        }
        return false;
    }

    @Override
    public Call<Boolean> authenticateUser(User user) {
        return remoteCrud.authenticateUser(user);
    }
}
