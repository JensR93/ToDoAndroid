package com.jens.ToDo.model.impl;

import com.jens.ToDo.model.ToDo;
import com.jens.ToDo.model.User;
import com.jens.ToDo.model.interfaces.IToDoCRUDOperations;
import com.jens.ToDo.model.tasks.CreateItemTask;
import com.jens.ToDo.model.tasks.DeleteAllItemTask;
import com.jens.ToDo.ui.Main.MainActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class SyncedToDoCrudOperations implements IToDoCRUDOperations {
    private IToDoCRUDOperations localCrud;
    private  IToDoCRUDOperations remoteCrud;

    public IToDoCRUDOperations getLocalCrud() {
        return localCrud;
    }

    public IToDoCRUDOperations getRemoteCrud() {
        return remoteCrud;
    }

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

    public boolean deleteAllLocalItems() {
        if(localCrud.deleteAllItems()){
            return  true;
        }
        return false;
    }
    public boolean deleteAllRemoteItems() {
        if(remoteCrud.deleteAllItems()){
            return  true;
        }
        return false;
    }
    @Override
    public boolean syncAllItemsWithLocal() {
        if( remoteCrud.deleteAllItems()){
            List<ToDo> toDoList = localCrud.readAllItems();
            for ( ToDo todo : toDoList)
            {
                remoteCrud.createItem(todo);
            }
            return true;
        }
        return false;
    }

//    @Override
//    public boolean syncAllItemsWithRemote() {
//        List<ToDo> toDoList = remoteCrud.readAllItems();
//        final boolean[] retVal = {false};
//        new DeleteAllItemTask(localCrud).run(success -> {
//
//            if(success){
//                retVal[0] = success;
//                for ( ToDo todo : toDoList)
//                {
//                    if(todo.getContacts()==null){
//                        todo.setContacts(new ArrayList<String >());
//                    }
//                    new CreateItemTask(localCrud).run(todo, todonew -> {
//
//                    });
//
//                }
//
//            }
//            else{
//
//            }
//        });
//
//
//        return retVal[0];
//    }

    @Override
    public Call<Boolean> authenticateUser(User user) {
        Call<Boolean>  b = remoteCrud.authenticateUser(user);
        return b;
    }
}
