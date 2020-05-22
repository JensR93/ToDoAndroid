package com.jens.ToDo.model.impl;

import com.jens.ToDo.model.ToDo;
import com.jens.ToDo.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

interface iToDoWebAPI {
    @POST("/api/todos")
    public Call<ToDo> createItem(@Body ToDo item);
    @GET("/api/todos")
    public Call<List<ToDo>> readAllItems();
    @GET("/api/todos/{itemId}")
    public Call<ToDo> readItem( @Path("itemId")long id);
    @PUT("/api/todos/{itemId}")
    public Call<ToDo> updateItem(@Path("itemId")long id, @Body ToDo item);
    @DELETE("/api/todos/{itemId}")
    public Call<Boolean> deleteItem(@Path("itemId")long id);
    @DELETE("/api/todos")
    public Call<Boolean> deleteAllItems();
    @PUT("/api/users/auth")
    public Call<Boolean> authenticateUser(@Body User var1);
}
