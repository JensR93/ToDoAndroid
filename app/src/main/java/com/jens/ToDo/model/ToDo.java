package com.jens.ToDo.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;
import com.jens.ToDo.model.Converter.ContactConverter;

import java.io.Serializable;
import java.util.ArrayList;
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
    @TypeConverters(ContactConverter.class)
    private List <String> contacts = new ArrayList<String>();

    //private List<String> contactList = new ArrayList<>();
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

    /*    public List<String> getContactList() {
            return contactList;
        }

        public void setContactList(List<String> contactList) {
            this.contactList = contactList;
        }*/
    @Override
    public String toString() {
        return name ;
    }
}
