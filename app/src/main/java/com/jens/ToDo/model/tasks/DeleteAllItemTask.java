package com.jens.ToDo.model.tasks;

import android.os.AsyncTask;

import com.jens.ToDo.model.interfaces.IToDoCRUDOperations;

import java.util.function.Consumer;

public class DeleteAllItemTask extends AsyncTask<Void, Void, Boolean> {

    private IToDoCRUDOperations crudOperations;
    private Consumer<Boolean> callback;

    public DeleteAllItemTask(IToDoCRUDOperations crudOperations) {
        this.crudOperations = crudOperations;
    }


    @Override
    protected Boolean doInBackground(Void... voids) {

        return  this.crudOperations.deleteAllItems();

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
