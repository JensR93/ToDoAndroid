package com.jens.ToDo.model.impl;

import com.jens.ToDo.model.ToDo;
import com.jens.ToDo.model.User;
import com.jens.ToDo.model.interfaces.IToDoCRUDOperations;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroFitToDoRUDOperationsImpl implements IToDoCRUDOperations {
    @Override
    public Call<Boolean> authenticateUser(User user) {
        return webAPIClient.authenticateUser(user);
    }

    private iToDoWebAPI webAPIClient;

    public RetroFitToDoRUDOperationsImpl() {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(loggingInterceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(("http://10.0.2.2:8080/"))
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        webAPIClient=retrofit.create(iToDoWebAPI.class);
    }

    @Override
    public ToDo createItem(ToDo item) {
        try {
            ToDo ToDo = webAPIClient.createItem(item).execute().body();
            return ToDo;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<ToDo> readAllItems() {
        try {
            return webAPIClient.readAllItems().execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ToDo readItem(long id) {
        try {
            return webAPIClient.readItem(id).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean updateItem(ToDo item) {
        try {
            Call<ToDo> call =   webAPIClient.updateItem(item.getId(),item);
            call.execute().body();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteItem(long id) {
        try {
            if( webAPIClient.deleteItem(id).execute().body()!=null){
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    @Override
    public boolean deleteAllItems() {
        try {
            if( webAPIClient.deleteAllItems().execute().body()!=null){
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }



}
