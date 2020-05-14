package com.jens.ToDo.model.interfaces;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.jens.ToDo.model.ToDo;

import java.util.List;
@Dao
public interface ToDoDao {
    @Query("select * from ToDo")
    public List<ToDo> readAll();


    @Query("select * from ToDo where id==(:id)")
    public ToDo readById(long id);

    @Insert
    public long create(ToDo item);

    @Delete
    public void delete(ToDo item);

    @Update
    public void update(ToDo item);
}
