package com.jens.ToDo.model.tasks;

import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;

import com.jens.todo.model.DataItem.DataItem;
import com.jens.todo.model.Interface.IDataItemCRUDOperations;

import java.util.List;
import java.util.function.Consumer;

public class ReadAllItemsTask extends AsyncTask<Void, Void, List<DataItem>> {

    private IDataItemCRUDOperations crudOperations;
    private Consumer<List<DataItem>> callback;
    private ProgressBar progressBar;

    public ReadAllItemsTask(IDataItemCRUDOperations crudOperations, ProgressBar progressBar) {
        this.crudOperations = crudOperations;
        this.progressBar = progressBar;
    }

    @Override
    protected List<DataItem> doInBackground(Void... voids) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return crudOperations.readAllItems();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onPostExecute(List<DataItem> dataItems) {

        this.progressBar.setVisibility(View.GONE);
        callback.accept(dataItems);
    }

    public void run(Consumer<List<DataItem>> consumer) {
        this.callback = consumer;
        super.execute();
    }

    @Override
    protected void onPreExecute() {
        this.progressBar.setVisibility(View.VISIBLE);
    }
}
