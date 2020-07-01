package com.jens.ToDo.model;

import android.graphics.Bitmap;

public class ToDoContact {
    private String ID;
    private String name;
    private String[] phoneNo;
    private String[] emailAdress;

    private Bitmap photo;


    public ToDoContact(String ID, String name, String[] phoneNo, String[] emailAdress, Bitmap photo) {
        this.ID = ID;
        this.name = name;
        this.phoneNo = phoneNo;
        this.emailAdress = emailAdress;
        this.photo = photo;
    }


    public String getName() {
        return name;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String[] getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String[] phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String[] getEmailAdress() {
        return emailAdress;
    }

    public void setEmailAdress(String[] emailAdress) {
        this.emailAdress = emailAdress;
    }


}
