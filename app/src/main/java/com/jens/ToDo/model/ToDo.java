package com.jens.ToDo.model;

import android.app.Activity;
import android.database.Cursor;
import android.provider.ContactsContract;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.jens.ToDo.ui.DetailView.Contactmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Entity
public class ToDo implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String name;
    private String description;
    @SerializedName("done")
    private boolean done =false;
    private boolean favourite =false;

    private Long expiry;

    private List <String> contacts = new ArrayList<String>();

    @Ignore
    private transient List<ToDoContact> toDoContactList = new ArrayList<ToDoContact>();
    public ToDo(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public Long getExpiry() {
        return expiry;
    }

    public void setExpiry(Long expiry) {
        this.expiry = expiry;
    }

    public List<String> getContacts() {
        return contacts;
    }

    public void setContacts(List<String> contacts) {
        this.contacts = contacts;
    }
    public void addContact(String contact) {

        contacts.add(contact);
    }
    private String getStringFromArrayList(){
        String retval ="";
        return retval;
    }

    public List<ToDoContact> getToDoContactList() {
        return toDoContactList;
    }

    public void setToDoContactList(List<ToDoContact> toDoContactList) {
        this.toDoContactList = toDoContactList;
    }

    @Override
    public String toString() {
        return name ;
    }

    public void addToDoContact(ToDoContact toDoContact) {
        this.toDoContactList.add(toDoContact);
        //this.contacts.add(toDoContact.getID());
    }

    public String getContactStringMultiLine(){
        String retval ="";
        for (ToDoContact toDoContact:toDoContactList) {

            retval+=toDoContact.getName()+"("+toDoContact.getID()+")\n";
        }
        return retval;
    }


    public void removeToDoContact(ToDoContact toDoContact) {

        for (Iterator<ToDoContact> iter = toDoContactList.listIterator(); iter.hasNext(); ) {
            ToDoContact toDoContactListElement = iter.next();
            if (toDoContactListElement.getID().equals(toDoContact.getID())) {
                iter.remove();
            }
        }
        for (Iterator<String> iter = contacts.listIterator(); iter.hasNext(); ) {
            String toDoContactListElement = iter.next();
            if (toDoContactListElement.equals(toDoContact.getID())) {
                iter.remove();
            }
        }
    }

}
