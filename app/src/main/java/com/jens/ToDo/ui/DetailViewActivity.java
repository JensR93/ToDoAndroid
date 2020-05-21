package com.jens.ToDo.ui;
/**
 * @author Jens
 */

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import com.jens.ToDo.R;
import com.jens.ToDo.model.ToDo;
import com.jens.ToDo.model.ToDoApplication;
import com.jens.ToDo.model.interfaces.IToDoCRUDOperations;
import com.jens.ToDo.model.tasks.DeleteItemTask;
import com.jens.ToDo.model.tasks.UpdateItemTask;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class DetailViewActivity extends AppCompatActivity implements View.OnClickListener {


    //region Variable

    FloatingActionButton fab;
    TextInputEditText inputName;
    TextInputEditText inputDescription;
    TextInputEditText inputID;
    EditText inputDueDate;
    EditText inputDueTime;
    TextView textImportContacts;
    AppCompatCheckBox checkDone;
    AppCompatCheckBox checkFavourite;

    IToDoCRUDOperations crudOperations;

    ToDo selectedItem;
    ToDo toDoElementToCreate;
    MenuItem saveMenuItem, deleteMenuItem;


    int mYear, mMonth, mDay, mHour, mMinute;

    //endregion

    //region Constants
    public static final int STATUS_CREATED = 0;
    public static final int STATUS_EDITED = 1;
    public static final int STATUS_DELETED = 2;

    public static final int CALL_CONTACT_PICK = 10;
    public static final String ARG_ITEM_ID = "itemID";
    private static final String LOGGING_TAG = DetailViewActivity.class.getSimpleName();
    //endregion
    long itemId;

    //wird beim ersten Start aufgerufen
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);

        findElements();
        crudOperations = ((ToDoApplication) getApplication()).getCRUDOperations();

        // Intent von MainActivity speichern
        itemId = getIntent().getLongExtra(ARG_ITEM_ID, -1);

        CreateListener();

        inputDueTime.setOnClickListener(this);
        inputDueDate.setOnClickListener(this);

        if (itemId != -1) {
            loadToDoObject(itemId);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    // Beim schließen, wegswipen / über Taskmanager schließen
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // Aufruf wenn nicht mehr im Hintergrund
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View view) {

        if (view == inputDueDate) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            inputDueDate.setText(String.format("%02d.%02d.%04d", dayOfMonth, monthOfYear + 1, year));

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (view == inputDueTime) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            inputDueTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                        }
                    }, mHour, mMinute, true);
            timePickerDialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.detailview_menu, menu);
        saveMenuItem = menu.findItem(R.id.saveItem);
        deleteMenuItem = menu.findItem(R.id.deleteItem);

        if (itemId != -1) {
            //loadToDoObject(itemId);
            deleteMenuItem.setEnabled(true);
            deleteMenuItem.getIcon().setAlpha(255);
        } else {
            deleteMenuItem.setEnabled(false);
            deleteMenuItem.getIcon().setAlpha(0);
        }
        saveMenuItem.setEnabled(true);
        saveMenuItem.getIcon().setAlpha(255);


        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CALL_CONTACT_PICK && resultCode == Activity.RESULT_OK) {
            Log.i(getClass().getSimpleName(), "got intent from contact picker" + data);
            showContactDetails(data.getData());
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.saveItem) {
            saveDataItem();
            return true;
        }
        if (item.getItemId() == R.id.deleteItem) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Wirklich löschen?");
            builder.setPositiveButton("Löschen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteDataItem();
                }
            });
            builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(DetailViewActivity.this, "Abbuch", Toast.LENGTH_SHORT).show();
                }
            });
            builder.create().show();

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void findElements() {
        inputName = findViewById(R.id.inputName);
        inputDescription = findViewById(R.id.inputDescription);
        inputID = findViewById(R.id.inputID);
        inputDueDate = findViewById(R.id.inputDueDate);
        inputDueTime = findViewById(R.id.inputDueTime);
        checkDone = findViewById(R.id.inputDone);
        checkFavourite = findViewById(R.id.inputFavourite);
        textImportContacts = findViewById(R.id.textImportContacts);
    }

    private void loadToDoObject(long itemId) {
        new AsyncTask<Long, Void, ToDo>() {
            @Override
            protected ToDo doInBackground(Long... longs) {

                return selectedItem = crudOperations.readItem(longs[0]);
            }

            @Override
            protected void onPostExecute(ToDo dataItem) {
                //super.onPostExecute(dataItem);
                if (dataItem != null) {
                    inputDescription.setText(dataItem.getDescription());
                    inputID.setText(dataItem.getId().toString());
                    checkDone.setChecked(dataItem.isDone());
                    checkFavourite.setChecked(dataItem.isFavourite());

                    inputName.setText(dataItem.getName());
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.uuuu");

                    for (String stringContactId: dataItem.getContacts()) {

                        final Cursor phoneCursor = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                new String[] {
                                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                                        ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
                                },
                                ContactsContract.Data.CONTACT_ID + "=?",
                                new String[] {String.valueOf(stringContactId)}, null);

                        try {
                            final int idxAvatarUri = phoneCursor.getColumnIndexOrThrow(
                                    ContactsContract.CommonDataKinds.Phone.PHOTO_URI);
                            final int idxName = phoneCursor.getColumnIndexOrThrow(
                                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                            final int idxPhone = phoneCursor.getColumnIndexOrThrow(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER);

                            while (phoneCursor.moveToNext()) {
                                String phoneNumber = phoneCursor.getString(idxPhone);
                                String name = phoneCursor.getString(idxName);
                                String avatarUri = phoneCursor.getString(idxAvatarUri);
                                textImportContacts.setText(textImportContacts.getText()+"\n"+name+"("+stringContactId+")");
                                Log.d("Details", "Phone number: " + phoneNumber);
                                Log.d("Details", "Name: " + name);
                                Log.d("Details", "Avatar URI: " + avatarUri);
                            }
                        } finally {
                            phoneCursor.close();
                        }
                    }

                    if (dataItem.getExpiry() != null) {


                        LocalDateTime date =
                                LocalDateTime.ofInstant(Instant.ofEpochMilli(dataItem.getExpiry()), ZoneId.systemDefault());

                        //LocalDate localDate = date.toLocalDate();
                        LocalTime localTime = date.toLocalTime();

                        String localDateString = formatter.format(date);
                        inputDueDate.setText(localDateString);
                        inputDueTime.setText(localTime.toString());
                    }
                }
            }
        }.execute(itemId);
    }

    private void CreateListener() {

        inputName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                    if (inputName.getText().length() > 0) {
                        enableSaveButton();
                    }
                }
                return false;
            }
        });

        inputDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                    if (inputName.getText().length() > 0) {
                        enableSaveButton();
                    }
                }
                return false;
            }
        });
        checkFavourite.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                    if (inputName.getText().length() > 0) {
                        enableSaveButton();
                    }
                }
                return false;
            }
        });
        checkDone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                    if (inputName.getText().length() > 0) {
                        enableSaveButton();
                    }
                }
                return false;
            }
        });
        textImportContacts.setOnClickListener(new TextView.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent contactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(contactIntent, CALL_CONTACT_PICK);

            }
        });


    }

    private void enableSaveButton() {
        saveMenuItem.getIcon().setAlpha(255);
        saveMenuItem.setEnabled(true);
    }

    private void deleteDataItem() {
        Intent returnIntent = new Intent();
        new DeleteItemTask(this.crudOperations).run(selectedItem.getId(), success -> {
            returnIntent.putExtra("ToDoItem", selectedItem);
            returnIntent.putExtra("success", success);
            setResult(STATUS_DELETED, returnIntent);
            setContentView(R.layout.activity_main);
            finish();
        });

    }

    private void saveDataItem() {
        long itemId = getIntent().getLongExtra(ARG_ITEM_ID, -1);
        Intent returnIntent = new Intent();
        String inpName = inputName.getText().toString();
        String inpDescription = inputDescription.getText().toString();

        //EDIT
        if (itemId != -1 && selectedItem != null) {
            selectedItem.setName(inputName.getText().toString());
            selectedItem.setDescription(inputDescription.getText().toString());
            selectedItem.setFavourite(checkFavourite.isChecked());
            selectedItem.setDone(checkDone.isChecked());


            if (!inputDueDate.getText().toString().equals("") || !inputDueTime.getText().toString().equals("")) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.uuuu");
                LocalDate datePart = LocalDate.parse(inputDueDate.getText(), formatter);
                LocalTime timePart = LocalTime.parse(inputDueTime.getText());
                LocalDateTime dt = LocalDateTime.of(datePart, timePart);

                long longDateTimeValue = dt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                selectedItem.setExpiry(longDateTimeValue);
            }
            new UpdateItemTask(crudOperations).run(selectedItem, updated -> {

                if (updated) {
                    returnIntent.putExtra(ARG_ITEM_ID, selectedItem.getId());

                    returnIntent.putExtra("ToDoItem", selectedItem);
                    setResult(STATUS_EDITED, returnIntent);
                    setContentView(R.layout.activity_main);

                    finish();
                }

            });
        }
        //Create
        else {
            if (itemId == -1) {

                //TODO: CreateItemTask bauen: Parameter mehrere Strings und nicht das Objekt --> ID != 0

                if(toDoElementToCreate==null)
                {
                    toDoElementToCreate = new ToDo();
                }
                toDoElementToCreate.setName(inpName);
                toDoElementToCreate.setDescription(inpDescription);
                toDoElementToCreate.setDone(checkDone.isChecked());
                toDoElementToCreate.setFavourite(checkFavourite.isChecked());


                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.uuuu");
                if (!inputDueDate.getText().toString().equals("") || !inputDueTime.getText().toString().equals("")) {
                    LocalDate datePart = LocalDate.parse(inputDueDate.getText(), formatter);
                    LocalTime timePart = LocalTime.parse(inputDueTime.getText());
                    LocalDateTime dt = LocalDateTime.of(datePart, timePart);
                    long longDateTimeValue = dt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    toDoElementToCreate.setExpiry(longDateTimeValue);
                }


                new Thread(() -> {
                    selectedItem = crudOperations.createItem(toDoElementToCreate);
                    runOnUiThread(() -> {


                        returnIntent.putExtra("ToDoItem", selectedItem);

                        returnIntent.putExtra(ARG_ITEM_ID, selectedItem.getId());
                        setResult(STATUS_CREATED, returnIntent);
                        setContentView(R.layout.activity_main);
                        finish();
                    });

                }).start();
            } else {
//                setResult(-1, returnIntent);
//                returnIntent.putExtra("dataitem", selectedItem);
//                setContentView(R.layout.activity_main);
//                finish();
            }
        }
    }

    private void showContactDetails(Uri contactUri) {
        Log.i(LOGGING_TAG, String.format("got contactURI: %s", contactUri));
        Cursor contactsCursor = getContentResolver().query(contactUri, null, null, null);
        if (contactsCursor.moveToFirst()) {
            String contactName = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String contactId = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts._ID));

            Log.i(LOGGING_TAG, String.format("contactName: %s", contactName));

            //UI
            if(toDoElementToCreate==null)
            {
                toDoElementToCreate = new ToDo();
            }
            if(selectedItem!=null)
            {
                selectedItem.addContact(contactId);
            }
            toDoElementToCreate.addContact(contactId);
            textImportContacts.setText(textImportContacts.getText() + "\n" + contactName + "("+contactId+")");


            Log.i(LOGGING_TAG, String.format("contactID: %s", contactId));

            if (verifyReadContactPermission()) {
                Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{contactId}, null);


                Cursor pCur = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                + " = ?", new String[]{contactId}, null);
                while (pCur.moveToNext()) {
                    // Do something with phones
                    String phoneNo = pCur
                            .getString(pCur
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    //nameList.add(name); // Here you can list of contact.
                    //phoneList.add(phoneNo); // Here you will get list of phone number.


                    Cursor emailCur = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{contactId}, null);
                    while (emailCur.moveToNext()) {
                        String email2 = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));

                        SmsManager smsManager = SmsManager.getDefault();
                        String sms = "smsText.getText().toString()";
                        // smsManager.sendTextMessage("012345", null, sms, null, null);
//Send the SMS//

                        String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(this); // Need to change the build to API 19

//                        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
//                        sendIntent.setType("text/plain");
//                        sendIntent.putExtra(Intent.EXTRA_TEXT, "text");
//                        sendIntent.setData(Uri.parse("sms:" + phoneNo));
//                        sendIntent.putExtra(Intent.EXTRA_UID,  ContactsContract.CommonDataKinds.Phone.CONTENT_URI);

//                        if (defaultSmsPackageName != null)// Can be null in case that there is no default, then the user would be able to choose
//                        // any app that support this intent.
//                        {
//                            sendIntent.setPackage(defaultSmsPackageName);
//                        }
//                        startActivity(sendIntent);

                        Intent email = new Intent(Intent.ACTION_SEND);
                        email.putExtra(Intent.EXTRA_EMAIL, new String[]{email2});
                        email.putExtra(Intent.EXTRA_SUBJECT, "subject");
                        email.putExtra(Intent.EXTRA_TEXT, "mess");

//need this to prompts email client only

                        //sendIntent.setType("vnd.android-dir/mms-sms/");

                        //startActivity(Intent.createChooser(sendIntent, "Choose an Email client :"));

                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.parse("smsto:" + phoneNo)); // This ensures only SMS apps respond
                        intent.putExtra("sms_body", "Hey it works :) New todo ");
                       // startActivity(intent);


                        email.setType("message/rfc822");

                        //startActivity(Intent.createChooser(email, "Choose an Email client :"));
                        //emailList.add(email); // Here you will get list of email

                    }
                    emailCur.close();
                }


                if (contactsCursor.moveToFirst()) {
                    do {
                        String phoneNumber = String.valueOf(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        int phoneNumberType = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA2);
                        Log.i(LOGGING_TAG, String.format("phoneNumber: %s", phoneNumber));
                        Log.i(LOGGING_TAG, String.format("phoneNumberType: %s", phoneNumberType));
                        if (phoneNumberType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {

                            Log.i(LOGGING_TAG, String.format("Found mobileNumber: %s", phoneNumber));
                        }
                    }
                    while (phoneCursor.moveToNext());
                }
            }
        }
    }

    private boolean verifyReadContactPermission() {
        int hasReadContactsPermission = checkSelfPermission(Manifest.permission.READ_CONTACTS);
        if (hasReadContactsPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 4);
            return false;
        }
    }
}
