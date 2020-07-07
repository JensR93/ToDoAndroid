package com.jens.ToDo.ui.DetailView;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
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

public class ContactManager {

    public Activity activity;


    public ContactManager(Activity activity) {
        this.activity = activity;
    }


    /**
     * Calls readFromDevice,readFromToDo
     * @param contactUri
     * @param selectedItem
     * @return
     */
    public ToDo showAddContactDetails(Uri contactUri, ToDo selectedItem) {
        String contactString =  readToDOContactFromDevice(contactUri);
        selectedItem.addContact(contactString);
        selectedItem.setToDoContactList(new ArrayList<ToDoContact>());
        readContactFromDataItem(selectedItem);
        return selectedItem;
    }

    //region readToDo

    /**
     * Reads a contact from a Device and returns the contactID as String
     * @param contactUri
     * @return
     */
    private String readToDOContactFromDevice(Uri contactUri){
        String contactId="";
        Cursor contactsCursor = activity.getContentResolver().query(contactUri, null, null, null);
        if (contactsCursor.moveToFirst()) {
            return contactId = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts._ID));
        }
        return contactId;
    }

    /**
     * Checks if the App has the rights to read Contacts
     * @return
     */
    private boolean verifyReadContactPermission() {
        int hasReadContactsPermission = activity.checkSelfPermission(Manifest.permission.READ_CONTACTS);
        if (hasReadContactsPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            activity.requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 4);
            return false;
        }
    }

    /**
     * All contactID strings stored for each ToDoElement are read and added to the ToDoElement
     * @param toDo
     */
    public void readContactFromDataItem(ToDo toDo){
        if (verifyReadContactPermission()) {

            String[] phoneNumber;
            String[] emailAdress;
            for (String stringContactId : toDo.getContacts()){



                Bitmap photo;
                String retval = "";
                String name = null;

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
                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                ContactsContract.CommonDataKinds.Email.ADDRESS,
                        },
                        ContactsContract.Data.CONTACT_ID + "=?",
                        new String[]{String.valueOf(stringContactId)}, null);


                name=getNameUsingContactId(stringContactId);


                try {
                    final int idxName2 = phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    final int idxName3 = EmailCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);


                    final int idxPhone = phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    final int idxEmail = EmailCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.ADDRESS);

                    int count = 0;
                    phoneNumber = new String[phoneCursor.getCount()];
                    emailAdress = new String[EmailCursor.getCount()];

                    while (phoneCursor.moveToNext()) {
                        phoneNumber[count] = phoneCursor.getString(idxPhone);
                        //name = phoneCursor.getString(idxName2);
                        retval = retval + "\n" + name + "(" + stringContactId + ")";
                        count++;
                    }
                    count = 0;
                    while (EmailCursor.moveToNext()) {
                        emailAdress[count] = EmailCursor.getString(idxEmail);
                        //name = EmailCursor.getString(idxName3);
                        count++;
                    }
                    photo = readPhoto(Long.parseLong(stringContactId));
                } finally {
                    phoneCursor.close();
                    EmailCursor.close();
                }

                toDo.addToDoContact(new ToDoContact(stringContactId, name, phoneNumber, emailAdress, photo));


            }
        }
    }

    private String getNameUsingContactId(String contactId){
        String name="";
        String cContactIdString = ContactsContract.Contacts._ID;
        Uri cCONTACT_CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String cDisplayNameColumn = ContactsContract.Contacts.DISPLAY_NAME;

        String selection = cContactIdString + " = ? ";
        String[] selectionArgs = new String[]{String.valueOf(contactId)};

        Cursor cursor = activity.getContentResolver().query(cCONTACT_CONTENT_URI, null, selection, selectionArgs, null);
        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToFirst();
            while ((cursor != null) && (cursor.isAfterLast() == false)) {
                if (cursor.getColumnIndex(cContactIdString) >= 0) {
                    if (contactId.equals(cursor.getString(cursor.getColumnIndex(cContactIdString)))) {
                         name = cursor.getString(cursor.getColumnIndex(cDisplayNameColumn));
                        break;
                    }
                }
                cursor.moveToNext();
            }
        }
        if (cursor != null)
            cursor.close();
        return name;
    }

    /**
     * Reads the contactPhoto from the Contact
     * @param contactId
     * @return
     */
    private Bitmap readPhoto(long contactId) {
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

    /**Starts an Activity to send an SMS
     *
     * @param number
     * @param textToWrite
     */
    public void sendSMS(String number, String textToWrite){
        if(activity!=null) {
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
            smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            smsIntent.setData(Uri.parse("smsto:" + number)); // This ensures only SMS apps respond
            smsIntent.putExtra("sms_body", textToWrite);
            activity.startActivity(smsIntent);
        }
    }



    /**
     * Starts an Activity to send an Email
     * @param adress
     * @param textToWrite
     * @param subject
     */
    public void sendEmail(String adress, String textToWrite, String subject){
        if(activity!=null) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + adress));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, textToWrite);
            activity.startActivity(Intent.createChooser(emailIntent, "Choose an Email client :"));
        }
    }

    /**
     * Starts an Activity to start a Call
     * @param number
     */
    public void startCall(String number){
        if(activity!=null) {
            Intent telIntent = new Intent(Intent.ACTION_DIAL);
            telIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            telIntent.setData(Uri.parse("tel:" +number));
            activity.startActivity(telIntent);
        }
    }








}
