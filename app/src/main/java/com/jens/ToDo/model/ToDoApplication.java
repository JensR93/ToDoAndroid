package com.jens.ToDo.model;

import android.app.Application;
import android.widget.Toast;

import com.jens.ToDo.model.impl.RetroFitToDoRUDOperationsImpl;
import com.jens.ToDo.model.impl.RoomToDoCRUDOperationsImpl;
import com.jens.ToDo.model.impl.SyncedToDoCrudOperations;
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
    @Override
    public void onCreate() {
        super.onCreate();
        CreateDatabaseImp();
    }

    public void CreateDatabaseImp() {
        //Fur das Projekt zum switchen ob offline oder online sync

        super.onCreate();
        //crudOperations = new RoomDataItemCRUDOperationsImpl(this);
        //crudOperations=new SQLiteDataItemCRUDOperationsImpl(this);
        //crudOperations=new RetroFitDataItemCRUDOperationsImpl();


        //crudOperations=new SyncedDataItemCrudOperations(new RoomDataItemCRUDOperationsImpl(this),new RetroFitDataItemCRUDOperationsImpl());
    }

    public void setRemoteCRUDMode(boolean localCRUDMode){
        if(!localCRUDMode){

            Toast.makeText(this,"Server not accessible. Use localCRUD",Toast.LENGTH_LONG);
            this.crudOperations=new RoomToDoCRUDOperationsImpl(this);

        }
        else{

            Toast.makeText(this,"Server accessible. Use remoteCRUD",Toast.LENGTH_LONG);
            crudOperations=new SyncedToDoCrudOperations(new RoomToDoCRUDOperationsImpl(this),new RetroFitToDoRUDOperationsImpl());
        }
    }
}
