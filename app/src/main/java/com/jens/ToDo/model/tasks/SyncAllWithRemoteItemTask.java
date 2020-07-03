package com.jens.ToDo.model.tasks;

import android.os.AsyncTask;

import com.jens.ToDo.model.interfaces.IToDoCRUDOperations;
import com.jens.ToDo.ui.Main.MainActivity;

import java.util.function.Consumer;

public class SyncAllWithRemoteItemTask extends AsyncTask<MainActivity, Void, Boolean> {

    private IToDoCRUDOperations crudOperations;
    private Consumer<Boolean> callback;

    public SyncAllWithRemoteItemTask(IToDoCRUDOperations crudOperations) {
        this.crudOperations = crudOperations;
    }




    @Override
    protected Boolean doInBackground(MainActivity... mainActivities) {
        boolean returnval = this.crudOperations.syncAllItemsWithRemote(mainActivities[0]);
        return returnval;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        callback.accept(aBoolean);
    }

    public void run(MainActivity activity,Consumer<Boolean> callback){
        this.callback=callback;
        super.execute(activity);
    }

}
