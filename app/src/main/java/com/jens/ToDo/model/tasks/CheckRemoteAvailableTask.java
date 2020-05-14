package com.jens.ToDo.model.tasks;

import android.os.AsyncTask;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Consumer;

public class CheckRemoteAvailableTask extends AsyncTask<Void,Void,Boolean> {

    private Consumer<Boolean> callback;

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {

            HttpURLConnection connection = (HttpURLConnection) new URL("http://10.0.2.2:8080/api/todos").openConnection();
            connection.setReadTimeout(1000);
            connection.setConnectTimeout(1500);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            connection.getInputStream();
            return true;
        }
        catch (Throwable e){
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        this.callback.accept(aBoolean);
    }

    public void run(Consumer<Boolean> callback){
        this.callback=callback;
        super.execute();
    }
}
