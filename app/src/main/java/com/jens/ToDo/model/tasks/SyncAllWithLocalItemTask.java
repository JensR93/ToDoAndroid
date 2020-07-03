package com.jens.ToDo.model.tasks;

import android.os.AsyncTask;

import com.jens.ToDo.model.interfaces.IToDoCRUDOperations;

import java.util.function.Consumer;

public class SyncAllWithLocalItemTask extends AsyncTask<Long, Void, Boolean> {

    private IToDoCRUDOperations crudOperations;
    private Consumer<Boolean> callback;

    public SyncAllWithLocalItemTask(IToDoCRUDOperations crudOperations) {
        this.crudOperations = crudOperations;
    }



    @Override
    protected Boolean doInBackground(Long... longs) {
        boolean returnval = this.crudOperations.syncAllItemsWithLocal();
        return returnval;
    }



    @Override
    protected void onPostExecute(Boolean aBoolean) {
        callback.accept(aBoolean);
    }

    public void run(Consumer<Boolean> callback){
        this.callback=callback;
        super.execute();
    }

}
