package com.jens.ToDo.model.tasks;

import android.os.AsyncTask;

import com.jens.ToDo.model.ToDo;
import com.jens.ToDo.model.interfaces.IToDoCRUDOperations;

import java.util.function.Consumer;

public class ReadItemTask extends AsyncTask<Long, Void, ToDo> {
    private IToDoCRUDOperations crudOperations;
    private Consumer<ToDo> callback;

    public ReadItemTask(IToDoCRUDOperations crudOperations) {
        this.crudOperations = crudOperations;
    }

    @Override
    protected ToDo doInBackground(Long... longs) {
        return crudOperations.readItem(longs[0]);
    }

    @Override
    protected void onPostExecute(ToDo dataItem) {
        callback.accept(dataItem);
    }
    public void run(long itemID, Consumer<ToDo> callback){
        this.callback=callback;
        super.execute(itemID);
    }
}
