package com.jens.ToDo.model.tasks;

import android.os.AsyncTask;

import com.jens.ToDo.model.ToDo;
import com.jens.ToDo.model.interfaces.IToDoCRUDOperations;

import java.util.function.Consumer;

public class CreateItemTask extends AsyncTask<ToDo, Void, ToDo> {
    private IToDoCRUDOperations crudOperations;
    private Consumer<ToDo> callback;

    public CreateItemTask(IToDoCRUDOperations crudOperations) {
        this.crudOperations = crudOperations;
    }

    @Override
    protected ToDo doInBackground(ToDo... toDos) {

        return crudOperations.createItem(toDos[0]);
    }


    @Override
    protected void onPostExecute(ToDo dataItem) {
        callback.accept(dataItem);
    }
    public void run(ToDo toDo, Consumer<ToDo> callback){
        this.callback=callback;
        super.execute(toDo);
    }
}
