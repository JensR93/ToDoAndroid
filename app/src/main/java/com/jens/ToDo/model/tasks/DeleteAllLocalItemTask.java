package com.jens.ToDo.model.tasks;

import android.os.AsyncTask;

import com.jens.ToDo.model.impl.SyncedToDoCrudOperations;
import com.jens.ToDo.model.interfaces.IToDoCRUDOperations;

import java.util.function.Consumer;

public class DeleteAllLocalItemTask extends AsyncTask<Void, Void, Boolean> {

    private SyncedToDoCrudOperations crudOperations;
    private Consumer<Boolean> callback;

    public DeleteAllLocalItemTask(SyncedToDoCrudOperations crudOperations) {
        this.crudOperations = crudOperations;
    }


    @Override
    protected Boolean doInBackground(Void... voids) {

        return  this.crudOperations.deleteAllLocalItems();

    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        callback.accept(aBoolean);
    }

    public void run( Consumer<Boolean> callback){
        this.callback=callback;
        super.execute();
    }

}
