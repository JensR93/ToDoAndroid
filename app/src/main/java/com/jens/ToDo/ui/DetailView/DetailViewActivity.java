package com.jens.ToDo.ui.DetailView;
/**
 * @author Jens
 */

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
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

    private FloatingActionButton fab;
    private TextInputEditText inputName;
    private TextInputEditText inputDescription;
    private TextInputEditText inputID;
    private EditText inputDueDate;
    private EditText inputDueTime;
    private TextView textImportContacts;
    private AppCompatCheckBox checkDone;
    private AppCompatCheckBox checkFavourite;

    private IToDoCRUDOperations crudOperations;

    private ToDo selectedItem;
    private MenuItem saveMenuItem, deleteMenuItem;


    int mYear, mMonth, mDay, mHour, mMinute;

    //endregion

    //region Constants
    public static final int STATUS_CREATED = 0;
    public static final int STATUS_EDITED = 1;
    public static final int STATUS_DELETED = 2;

    public static final int CALL_CONTACT_PICK = 10;
    public static final String ARG_ITEM_ID = "itemID";
    public static final String LOGGING_TAG = DetailViewActivity.class.getSimpleName();
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
        } else {
            selectedItem = new ToDo();
        }


    }


    // Opens UI Elements in Android to pick date and time
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
            deleteMenuItem.setEnabled(true);
            deleteMenuItem.getIcon().setAlpha(255);
            deleteMenuItem.setVisible(true);
        } else {
            deleteMenuItem.setEnabled(false);
            deleteMenuItem.setVisible(false);
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
            Contactmanager ccc = new Contactmanager(this);
            selectedItem = ccc.showAddContactDetails(data.getData(), selectedItem);
            textImportContacts.setText(selectedItem.getContactStringMultiLine());
            //textImportContacts.setText(ccc.showAddContactDetails(data.getData(),selectedItem));
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
        Contactmanager cccc = new Contactmanager(this);
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


                    // textImportContacts.setText(cccc.readContactFromDataItem(dataItem));

                    dataItem = cccc.readContactFromDataItem(dataItem);

                    textImportContacts.setText(dataItem.getContactStringMultiLine());
                    if (dataItem.getExpiry() != null) {


                        LocalDateTime date =
                                LocalDateTime.ofInstant(Instant.ofEpochMilli(dataItem.getExpiry()), ZoneId.systemDefault());

                        //LocalDate localDate = date.toLocalDate();
                        LocalTime localTime = date.toLocalTime();

                        String localDateString = formatter.format(date);
                        inputDueDate.setText(localDateString);
                        inputDueTime.setText(localTime.toString());
                    }

                    //Contactlistitem
                    ContactListItem contactListItem = new ContactListItem(DetailViewActivity.this, selectedItem);


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

        if (inpName.length()<4) {
            Toast.makeText(this, R.string.detailSaveMinimumNameLength, Toast.LENGTH_SHORT).show();
        } else {

            //EDIT
            if (itemId != -1 && selectedItem != null) {
                selectedItem.setName(inputName.getText().toString());
                selectedItem.setDescription(inputDescription.getText().toString());
                selectedItem.setFavourite(checkFavourite.isChecked());
                selectedItem.setDone(checkDone.isChecked());


                if (!inputDueDate.getText().toString().equals("")) {
                    if (inputDueTime.getText().toString().equals("")) {
                        inputDueTime.setText("00:00");
                    }
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.uuuu");
                    LocalDate datePart = LocalDate.parse(inputDueDate.getText(), formatter);
                    LocalTime timePart = LocalTime.parse(inputDueTime.getText());
                    LocalDateTime dt = LocalDateTime.of(datePart, timePart);

                    long longDateTimeValue = dt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    selectedItem.setExpiry(longDateTimeValue);
                }
                new UpdateItemTask(crudOperations).run(selectedItem, updated -> {

                    if (updated) {
                        try {
                            returnIntent.putExtra(ARG_ITEM_ID, selectedItem.getId());

                            returnIntent.putExtra("ToDoItem", selectedItem);
                            setResult(STATUS_EDITED, returnIntent);
                            setContentView(R.layout.activity_main);

                            finish();
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }

                });
            }
            //Create
            else {
                if (itemId == -1) {

                    //TODO: CreateItemTask bauen: Parameter mehrere Strings und nicht das Objekt --> ID != 0

                    if (selectedItem == null) {
                        selectedItem = new ToDo();
                    }
                    selectedItem.setName(inpName);
                    selectedItem.setDescription(inpDescription);
                    selectedItem.setDone(checkDone.isChecked());
                    selectedItem.setFavourite(checkFavourite.isChecked());


                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.uuuu");
                    if (!inputDueDate.getText().toString().equals("") && !inputDueTime.getText().toString().equals("")) {
                        LocalDate datePart = LocalDate.parse(inputDueDate.getText(), formatter);
                        LocalTime timePart = LocalTime.parse(inputDueTime.getText());
                        LocalDateTime dt = LocalDateTime.of(datePart, timePart);
                        long longDateTimeValue = dt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                        selectedItem.setExpiry(longDateTimeValue);
                    }


                    new Thread(() -> {
                        selectedItem = crudOperations.createItem(selectedItem);
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
    }


}
