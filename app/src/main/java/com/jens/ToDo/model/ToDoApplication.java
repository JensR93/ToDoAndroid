package com.jens.ToDo.model;

import android.app.Application;
import android.widget.Toast;

import com.jens.ToDo.model.impl.RoomToDoCRUDOperationsImpl;
import com.jens.ToDo.model.interfaces.IToDoCRUDOperations;

public class ToDoApplication extends Application {

    private IToDoCRUDOperations crudOperations;

    public IToDoCRUDOperations getCRUDOperations(){

        if(crudOperations==null){
            Toast.makeText(this,"CRUD Operations has not been set. Use local as default",Toast.LENGTH_LONG);
            this.crudOperations=new RoomToDoCRUDOperationsImpl(this);
        }
        return crudOperations;
    }
}
