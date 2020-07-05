package com.jens.ToDo.model.tasks;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Consumer;

public class CheckRemoteAvailableTask extends AsyncTask<Void,Void,Boolean> {

    private Consumer<Boolean> callback;
    private ProgressBar progressBar;

    public CheckRemoteAvailableTask(ProgressBar progressBar) {
        this.progressBar=progressBar;

    }

    public CheckRemoteAvailableTask() {

    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {


            HttpURLConnection connection = (HttpURLConnection) new URL("http://10.0.2.2:8080/").openConnection();
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
        if(progressBar!=null){
            progressBar.setVisibility(View.VISIBLE);
        }
        this.callback=callback;
        super.execute();
    }
}
