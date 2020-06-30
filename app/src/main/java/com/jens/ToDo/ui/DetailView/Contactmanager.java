package com.jens.ToDo.ui.DetailView;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.jens.ToDo.model.ToDo;
import com.jens.ToDo.model.ToDoContact;
import com.jens.ToDo.ui.Main.MainActivity;

public class Contactmanager {

    Activity detailViewActivity;

    public Contactmanager(DetailViewActivity detailViewActivity) {
        this.detailViewActivity = detailViewActivity;
    }
    public Contactmanager(MainActivity detailViewActivity) {
        this.detailViewActivity = detailViewActivity;
    }
    public ToDo readContactFromDataItem(ToDo dataItem){

        for (String stringContactId: dataItem.getContacts()) {
            String retval ="";
            String name = null;
            String phoneNumber =null;
            String email = null;
            final Cursor phoneCursor = detailViewActivity.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[] {
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                    },
                    ContactsContract.Data.CONTACT_ID + "=?",
                    new String[] {String.valueOf(stringContactId)}, null);
            final Cursor EmailCursor = detailViewActivity.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    new String[] {
                            ContactsContract.CommonDataKinds.Email.ADDRESS,
                    },
                    ContactsContract.Data.CONTACT_ID + "=?",
                    new String[] {String.valueOf(stringContactId)}, null);

            try {
                final int idxName = phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                final int idxPhone = phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);
                final int idxEmail = EmailCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.ADDRESS);
                if (phoneCursor.moveToNext()) {
                     phoneNumber = phoneCursor.getString(idxPhone);
                     name = phoneCursor.getString(idxName);
                    retval=retval+"\n"+name+"("+stringContactId+")";
                }
                if (EmailCursor.moveToNext()) {
                    email = EmailCursor.getString(idxEmail);
                }
            } finally {
                phoneCursor.close();
            }
            dataItem.addToDoContact(new ToDoContact(stringContactId,name,phoneNumber,email));

        }

        return dataItem;
    }

    private ToDoContact readToDOContactFromDevice(Uri contactUri){
        Cursor contactsCursor = detailViewActivity.getContentResolver().query(contactUri, null, null, null);
        if (contactsCursor.moveToFirst()) {
            String contactName = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String contactId = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts._ID));

            String phoneNumber = null;
            String emailAdress=null;;

            //Log.i(detailViewActivity.LOGGING_TAG, String.format("contactID: %s", contactId));

            if (verifyReadContactPermission()) {
                Cursor phoneCursor = detailViewActivity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{contactId}, null);

                Cursor pCur = detailViewActivity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = ?", new String[]{contactId}, null);

                while (pCur.moveToNext()) {
                    // Do something with phones
                    phoneNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    //Log.i(detailViewActivity.LOGGING_TAG, String.format("phone: %s", phoneNumber));
                    Cursor emailCur = detailViewActivity.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",new String[]{contactId}, null);
                    if (emailCur.moveToNext()) {
                        emailAdress = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        //    Log.i(detailViewActivity.LOGGING_TAG, String.format("email: %s", emailAdress));
                    }
                    emailCur.close();
                }

                return new ToDoContact(contactId,contactName,phoneNumber,emailAdress);

            }
        }
        return null;
    }

    public ToDo showAddContactDetails(Uri contactUri, ToDo selectedItem) {
        String retval ="";
        ToDoContact c =  readToDOContactFromDevice(contactUri);
        selectedItem.addToDoContact(c);
        selectedItem.addContact(c.getID());

        for (ToDoContact toDoContact: selectedItem.getToDoContactList()) {

            retval+="\n"+toDoContact.getName();
        }


        return selectedItem;
    }

    private boolean verifyReadContactPermission() {
        int hasReadContactsPermission = detailViewActivity.checkSelfPermission(Manifest.permission.READ_CONTACTS);
        if (hasReadContactsPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            detailViewActivity.requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 4);
            return false;
        }
    }
}
