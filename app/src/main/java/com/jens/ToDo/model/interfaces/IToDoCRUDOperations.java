package com.jens.ToDo.model.interfaces;

import com.jens.ToDo.model.ToDo;

import java.util.List;

public interface IToDoCRUDOperations {
    public ToDo createItem(ToDo item);
    public List<ToDo> readAllItems();
    public ToDo readItem(long id);
    public boolean updateItem(ToDo item);
    public boolean deleteItem(long id);
}
