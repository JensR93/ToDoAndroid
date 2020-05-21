package com.jens.ToDo.ui.Activity.DetailView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.jens.ToDo.model.ToDo;
import com.jens.ToDo.model.ToDoContact;

public class Contactmanager {

    DetailViewActivity detailViewActivity;

    public Contactmanager(DetailViewActivity detailViewActivity) {
        this.detailViewActivity = detailViewActivity;
    }

    public ToDo readContactFromDataItem(ToDo dataItem){
        String retval ="";
        String name = null;
        String phoneNumber =null;
        String email = null;
        for (String stringContactId: dataItem.getContacts()) {

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

            Log.i(detailViewActivity.LOGGING_TAG, String.format("contactID: %s", contactId));

            if (verifyReadContactPermission()) {
                Cursor phoneCursor = detailViewActivity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{contactId}, null);

                Cursor pCur = detailViewActivity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = ?", new String[]{contactId}, null);

                while (pCur.moveToNext()) {
                    // Do something with phones
                    phoneNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Log.i(detailViewActivity.LOGGING_TAG, String.format("phone: %s", phoneNumber));
                    Cursor emailCur = detailViewActivity.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",new String[]{contactId}, null);
                    if (emailCur.moveToNext()) {
                        emailAdress = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        Log.i(detailViewActivity.LOGGING_TAG, String.format("email: %s", emailAdress));
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

//        Cursor contactsCursor = detailViewActivity.getContentResolver().query(contactUri, null, null, null);
//        if (contactsCursor.moveToFirst()) {
//            String contactName = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//            String contactId = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts._ID));
//
//            String phoneNumber = null;
//            String emailAdress=null;;
//
//            //selectedItem.addContact(contactId);
//            retval=retval+"\n"+contactName+"("+contactId+")";
//
//            Log.i(detailViewActivity.LOGGING_TAG, String.format("contactID: %s", contactId));
//
//            if (verifyReadContactPermission()) {
//                Cursor phoneCursor = detailViewActivity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
//                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{contactId}, null);
//
//
//                Cursor pCur = detailViewActivity.getContentResolver().query(
//                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                        null,
//                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID
//                                + " = ?", new String[]{contactId}, null);
//                while (pCur.moveToNext()) {
//                    // Do something with phones
//                    String phoneNo = pCur
//                            .getString(pCur
//                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//
//                    //nameList.add(name); // Here you can list of contact.
//                    //phoneList.add(phoneNo); // Here you will get list of phone number.
//
//
//                    Cursor emailCur = detailViewActivity.getContentResolver().query(
//                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
//                            null,
//                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
//                            new String[]{contactId}, null);
//                    while (emailCur.moveToNext()) {
//                        emailAdress = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
//
//                        SmsManager smsManager = SmsManager.getDefault();
//                        String sms = "smsText.getText().toString()";
//                        // smsManager.sendTextMessage("012345", null, sms, null, null);
////Send the SMS//
//
//                        String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(detailViewActivity); // Need to change the build to API 19
//
////                        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
////                        sendIntent.setType("text/plain");
////                        sendIntent.putExtra(Intent.EXTRA_TEXT, "text");
////                        sendIntent.setData(Uri.parse("sms:" + phoneNo));
////                        sendIntent.putExtra(Intent.EXTRA_UID,  ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
//
////                        if (defaultSmsPackageName != null)// Can be null in case that there is no default, then the user would be able to choose
////                        // any app that support this intent.
////                        {
////                            sendIntent.setPackage(defaultSmsPackageName);
////                        }
////                        startActivity(sendIntent);
//
//                        Intent email = new Intent(Intent.ACTION_SEND);
//                        email.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAdress});
//                        email.putExtra(Intent.EXTRA_SUBJECT, "subject");
//                        email.putExtra(Intent.EXTRA_TEXT, "mess");
//
//                        //need this to prompts email client only
//
//                        //sendIntent.setType("vnd.android-dir/mms-sms/");
//
//                        //startActivity(Intent.createChooser(sendIntent, "Choose an Email client :"));
//
//
//
//
//                        email.setType("message/rfc822");
//
//                        //startActivity(Intent.createChooser(email, "Choose an Email client :"));
//                        //emailList.add(email); // Here you will get list of email
//
//                    }
//                    emailCur.close();
//                }
//
//
//                if (contactsCursor.moveToFirst()) {
//                    do {
//                        phoneNumber = String.valueOf(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                        int phoneNumberType = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA2);
//                        Log.i(detailViewActivity.LOGGING_TAG, String.format("phoneNumber: %s", phoneNumber));
//                        Log.i(detailViewActivity.LOGGING_TAG, String.format("phoneNumberType: %s", phoneNumberType));
//                        if (phoneNumberType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
//
//                            Log.i(detailViewActivity.LOGGING_TAG, String.format("Found mobileNumber: %s", phoneNumber));
//                        }
//                    }
//                    while (phoneCursor.moveToNext());
//                }
//                //ToDoContact c = new ToDoContact(contactId,contactName,phoneNumber,emailAdress);
//                //selectedItem.addToDoContact(c);
//            }
//        }

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
