package com.jens.ToDo.model.interfaces;

import com.jens.ToDo.model.ToDo;
import com.jens.ToDo.model.User;
import com.jens.ToDo.ui.Main.MainActivity;

import java.util.List;

import retrofit2.Call;

public interface IToDoCRUDOperations {
    public ToDo createItem(ToDo item);
    public List<ToDo> readAllItems();
    public ToDo readItem(long id);
    public boolean updateItem(ToDo item);
    public boolean deleteItem(long id);
    public boolean deleteAllItems();
    public boolean syncAllItemsWithLocal();
    public Call<Boolean> authenticateUser(User user);
}
