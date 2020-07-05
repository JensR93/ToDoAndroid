package com.jens.ToDo.model.tasks;

import android.os.AsyncTask;

import com.jens.ToDo.model.impl.SyncedToDoCrudOperations;

import java.util.function.Consumer;

public class DeleteAllRemoteItemTask extends AsyncTask<Void, Void, Boolean> {

    private SyncedToDoCrudOperations crudOperations;
    private Consumer<Boolean> callback;

    public DeleteAllRemoteItemTask(SyncedToDoCrudOperations crudOperations) {
        this.crudOperations = crudOperations;
    }


    @Override
    protected Boolean doInBackground(Void... voids) {

        return  this.crudOperations.deleteAllRemoteItems();

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
