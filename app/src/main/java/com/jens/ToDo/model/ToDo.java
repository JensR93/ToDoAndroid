package com.jens.ToDo.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
//        for(ToDoContact t : toDoContactList){
//            if(t.getName().equals(toDoContact.getName())||t.getName()==toDoContact.getName()){
//                int a=0;
//                a++;
//            }
//        }
//        if(this.toDoContactList.contains(toDoContact)){
//            int a=0;
//            a++;
//        }
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
    public LocalDateTime getExpiryDate(){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.uuuu");
        LocalDateTime date =LocalDateTime.ofInstant(Instant.ofEpochMilli(getExpiry()), ZoneId.systemDefault());

        return  date;

    }
    public String getExpiryDateString(){


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.uuuu");
        String localDateString = formatter.format(getExpiryDate());
        return localDateString;
    }
    public LocalTime getExpiryTime(){
        LocalTime localTime = getExpiryDate().toLocalTime();
        return localTime;
    }
    public int getExpiryDayInt(){
        String y = getExpiryDateString();
        String[] x = y.split("[.]");
        return Integer.parseInt(x[0]);
    }
    public int getExpiryMonthInt(){
        String y = getExpiryDateString();
        String[] x = y.split("[.]");
        return Integer.parseInt(x[1]);
    }
    public int getExpiryYearInt(){
        String y = getExpiryDateString();
        String[] x = y.split("[.]");
        return Integer.parseInt(x[2]);
    }


    public int getExpiryHourInt() {
        String y = getExpiryTime().toString();
        String[] x = y.toString().split(":");
        return Integer.parseInt(x[0]);
    }

    public int getExpiryMinuteInt() {
        String y = getExpiryTime().toString();
        String[] x = y.toString().split(":");
        return Integer.parseInt(x[1]);
    }
}
