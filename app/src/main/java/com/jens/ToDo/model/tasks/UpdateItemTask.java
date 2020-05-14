package com.jens.ToDo.model.tasks;

import android.os.AsyncTask;

import com.jens.ToDo.model.ToDo;
import com.jens.ToDo.model.interfaces.IToDoCRUDOperations;

import java.util.function.Consumer;

public class UpdateItemTask extends AsyncTask<ToDo, Void, Boolean> {

    private IToDoCRUDOperations crudOperations;
    private Consumer<Boolean> callback;

    public UpdateItemTask(IToDoCRUDOperations crudOperations) {
        this.crudOperations = crudOperations;
    }

    @Override
    protected Boolean doInBackground(ToDo... dataItems) {
        boolean returnval = this.crudOperations.updateItem(dataItems[0]);
        return returnval;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        callback.accept(aBoolean);
    }

    public void run(ToDo itemToBeUpdated, Consumer<Boolean> callback){
        this.callback=callback;
        super.execute(itemToBeUpdated);
    }

}
