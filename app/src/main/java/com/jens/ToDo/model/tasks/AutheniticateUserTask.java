package com.jens.ToDo.model.tasks;

import android.os.AsyncTask;

import com.jens.ToDo.model.User;
import com.jens.ToDo.model.interfaces.IToDoCRUDOperations;

import java.util.function.Consumer;

import retrofit2.Call;

public class AutheniticateUserTask extends AsyncTask<User, Void, Call<Boolean>> {

    private IToDoCRUDOperations crudOperations;
    private Consumer<Call<Boolean>> callback;

    public AutheniticateUserTask(IToDoCRUDOperations crudOperations) {
        this.crudOperations = crudOperations;
    }





    @Override
    protected Call<Boolean> doInBackground(User... users) {
        return  this.crudOperations.authenticateUser(users[0]);

    }

    @Override
    protected void onPostExecute(Call<Boolean> aBoolean) {
        callback.accept(aBoolean);
    }

    public void run(User user, Consumer<Call<Boolean>> callback){
        this.callback=callback;
        super.execute(user);
    }

}
