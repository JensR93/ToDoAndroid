package com.jens.ToDo.ui.DetailView;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

import com.jens.ToDo.model.ToDo;
import com.jens.ToDo.model.ToDoContact;
import com.jens.ToDo.ui.Main.MainActivity;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

public class Contactmanager {

    public DetailViewActivity activity;

    public Contactmanager(DetailViewActivity detailViewActivity) {
        this.activity = detailViewActivity;
    }



    private String readToDOContactFromDevice(Uri contactUri,ToDo selectedItem){
        String[] phonenNmber = new String[10];
        String contactId="";
        String[] emailAdress2 = new String[10];
        Cursor contactsCursor = activity.getContentResolver().query(contactUri, null, null, null);
        if (contactsCursor.moveToFirst()) {
            String contactName = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            return contactId = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts._ID));


//            String phoneNumber = null;
//            String emailAdress=null;;
//
//            //Log.i(detailViewActivity.LOGGING_TAG, String.format("contactID: %s", contactId));
//
//            if (verifyReadContactPermission()) {
//
//
//                Cursor pCur = activity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                        null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = ?", new String[]{contactId}, null);
//
//                while (pCur.moveToNext()) {
//                    // Do something with phones
//                    phonenNmber[0] = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                    phoneNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//
//                    //Log.i(detailViewActivity.LOGGING_TAG, String.format("phone: %s", phoneNumber));
//                    Cursor emailCur = activity.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
//                            null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",new String[]{contactId}, null);
//                    if (emailCur.moveToNext()) {
//                        emailAdress = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
//                        emailAdress2[0]=emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
//                        //    Log.i(detailViewActivity.LOGGING_TAG, String.format("email: %s", emailAdress));
//                    }
//                    emailCur.close();
//                }
//
//                //return new ToDoContact(contactId,contactName,phonenNmber,emailAdress2,null);
//                //return new ToDoContact();
//            }
        }
        return contactId;
    }

    public ToDo showAddContactDetails(Uri contactUri, ToDo selectedItem) {
        String retval ="";

        String c =  readToDOContactFromDevice(contactUri,selectedItem);

        selectedItem.addContact(c);
        selectedItem.setToDoContactList(new ArrayList<ToDoContact>());
        readContactFromDataItem(selectedItem);

        for (ToDoContact toDoContact: selectedItem.getToDoContactList()) {

            retval+="\n"+toDoContact.getName();
        }

        activity.createContactList();
        return selectedItem;
    }

    private boolean verifyReadContactPermission() {
        int hasReadContactsPermission = activity.checkSelfPermission(Manifest.permission.READ_CONTACTS);
        if (hasReadContactsPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            activity.requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 4);
            return false;
        }
    }


    public void readContactFromDataItem(ToDo toDo){
        if (verifyReadContactPermission()) {
            String[] phonenNmber = new String[10];
            String[] emailAdress = new String[10];

            for (String stringContactId : toDo.getContacts()){


                Bitmap photo;
                String retval = "";
                String name = null;
                String phoneNumber = null;
                String email = null;

                final Cursor phoneCursor = activity.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{
                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                ContactsContract.CommonDataKinds.Phone.NUMBER,
                        },
                        ContactsContract.Data.CONTACT_ID + "=?",
                        new String[]{String.valueOf(stringContactId)}, null);
                final Cursor EmailCursor = activity.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        new String[]{
                                ContactsContract.CommonDataKinds.Email.ADDRESS,
                        },
                        ContactsContract.Data.CONTACT_ID + "=?",
                        new String[]{String.valueOf(stringContactId)}, null);

                try {

                    final int idxName = phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    final int idxPhone = phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    final int idxEmail = EmailCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.ADDRESS);


                    //phonenNmber=new String[phoneCursor]
                    int count = 0;
                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(idxPhone);
                        phonenNmber[count] = phoneCursor.getString(idxPhone);
                        name = phoneCursor.getString(idxName);
                        retval = retval + "\n" + name + "(" + stringContactId + ")";
                        count++;
                    }
                    count = 0;
                    while (EmailCursor.moveToNext()) {
                        email = EmailCursor.getString(idxEmail);
                        emailAdress[count] = EmailCursor.getString(idxEmail);
                        count++;
                    }


                    photo = openPhoto(Long.parseLong(stringContactId));
                } finally {
                    phoneCursor.close();
                }

                toDo.addToDoContact(new ToDoContact(stringContactId, name, phonenNmber, emailAdress, photo));


            }
        }
    }

    private Bitmap openPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = activity.getContentResolver().query(photoUri,
                new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return BitmapFactory.decodeStream(new ByteArrayInputStream(data));
                }
            }
        } finally {
            cursor.close();
        }
        return null;

    }

}
