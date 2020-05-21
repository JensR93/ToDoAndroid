package com.jens.ToDo.model;

public class ToDoContact {
    private String ID;
    private String name;
    private String phoneNo;
    private String emailAdress;

    public ToDoContact( String ID,String name, String phoneNo, String emailAdress) {
        this.name = name;
        this.ID = ID;
        this.phoneNo = phoneNo;
        this.emailAdress = emailAdress;
    }

    public String getName() {
        return name;
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

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmailAdress() {
        return emailAdress;
    }

    public void setEmailAdress(String emailAdress) {
        this.emailAdress = emailAdress;
    }
}
