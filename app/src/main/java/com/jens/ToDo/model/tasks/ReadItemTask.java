package com.jens.ToDo.model.tasks;

import android.os.AsyncTask;

import com.jens.todo.model.DataItem.DataItem;
import com.jens.todo.model.Interface.IDataItemCRUDOperations;

import java.util.function.Consumer;

public class ReadItemTask extends AsyncTask<Long, Void, DataItem> {
    private IDataItemCRUDOperations crudOperations;
    private Consumer<DataItem> callback;

    public ReadItemTask(IDataItemCRUDOperations crudOperations) {
        this.crudOperations = crudOperations;
    }

    @Override
    protected DataItem doInBackground(Long... longs) {
        return crudOperations.readItem(longs[0]);
    }

    @Override
    protected void onPostExecute(DataItem dataItem) {
        callback.accept(dataItem);
    }
    public void run(long itemID, Consumer<DataItem> callback){
        this.callback=callback;
        super.execute(itemID);
    }
}
