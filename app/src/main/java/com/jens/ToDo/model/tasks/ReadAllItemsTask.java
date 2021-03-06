package com.jens.ToDo.model.tasks;

import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.core.util.Consumer;

import com.jens.ToDo.model.ToDo;
import com.jens.ToDo.model.interfaces.IToDoCRUDOperations;

import java.util.List;


public class ReadAllItemsTask extends AsyncTask<Void, Void, List<ToDo>> {

    private IToDoCRUDOperations crudOperations;
    private Consumer<List<ToDo>> callback;
    private ProgressBar progressBar;

    public ReadAllItemsTask(IToDoCRUDOperations crudOperations, ProgressBar progressBar) {
        this.crudOperations = crudOperations;
        this.progressBar = progressBar;
    }


//    Actions wie sms und email haben keine Infos des TODOS
//            Erster Appstart --> Keine Berechtigung wegen Kontakte lesen


    @Override
    protected List<ToDo> doInBackground(Void... voids) {
        return crudOperations.readAllItems();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onPostExecute(List<ToDo> dataItems) {

        this.progressBar.setVisibility(View.GONE);
        callback.accept(dataItems);
    }

    public void run(Consumer<List<ToDo>> consumer) {
        this.callback = consumer;
        super.execute();
    }

    @Override
    protected void onPreExecute() {
        this.progressBar.setVisibility(View.VISIBLE);
    }
}
