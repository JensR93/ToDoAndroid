package com.jens.ToDo.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.jens.ToDo.R;
import com.jens.ToDo.model.ToDo;
import com.jens.ToDo.model.ToDoApplication;
import com.jens.ToDo.model.interfaces.IToDoCRUDOperations;
import com.jens.ToDo.model.tasks.DeleteItemTask;
import com.jens.ToDo.model.tasks.UpdateItemTask;

import java.security.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DetailViewActivity extends AppCompatActivity  implements View.OnClickListener{


    //region Variable

    FloatingActionButton fab;
    TextInputEditText inputName;
    TextInputEditText inputDescription;
    TextInputEditText inputID;
    EditText inputDueDate;
    EditText inputDueTime;
    AppCompatCheckBox checkDone;
    AppCompatCheckBox checkFavourite;

    IToDoCRUDOperations crudOperations;

    ToDo selectedItem;
    MenuItem saveMenuItem;


    int mYear, mMonth, mDay, mHour, mMinute;

    //endregion

    //region Constants
    public static final int STATUS_CREATED = 0;
    public static final int STATUS_EDITED = 1;
    public static final int STATUS_DELETED = 2;
    public static final String ARG_ITEM_ID = "itemID";
    private static final String LOGGING_TAG = DetailViewActivity.class.getSimpleName();
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);

        findElements();
        crudOperations = ((ToDoApplication) getApplication()).getCRUDOperations();

        // Intent von MainActivity speichern
        long itemId = getIntent().getLongExtra(ARG_ITEM_ID, -1);
        if (itemId != -1) {
            loadToDoObject(itemId);
        }
        CreateListener();

        inputDueTime.setOnClickListener(this);
        inputDueDate.setOnClickListener(this);
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

                            inputDueDate.setText(String.format("%02d.%02d.%04d", dayOfMonth, monthOfYear+1,year));

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

        getMenuInflater().inflate(R.menu.detailview_menu,menu);

        saveMenuItem = menu.findItem(R.id.saveItem);
        saveMenuItem.setEnabled(false);
        saveMenuItem.getIcon().setAlpha(100);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.saveItem){
            saveDataItem();
            return  true;
        }
        if(item.getItemId()==R.id.deleteItem){
            deleteDataItem();
            return  true;
        }
        else{
            return super.onOptionsItemSelected(item);
        }
    }
    private void findElements() {
        fab = findViewById(R.id.fab);
        inputName = findViewById(R.id.inputID);
        inputDescription = findViewById(R.id.inputName);
        inputID = findViewById(R.id.inputDescription);
        inputDueDate = findViewById(R.id.inputDueDate);
        inputDueTime = findViewById(R.id.inputDueTime);
        checkDone = findViewById(R.id.inputDone);
        checkFavourite = findViewById(R.id.inputFavourite);
    }

    private void loadToDoObject(long itemId) {
        new AsyncTask<Long, Void, ToDo>() {
            @Override
            protected ToDo doInBackground(Long... longs) {

                return selectedItem = crudOperations.readItem(longs[0]);
            }

            @Override
            protected void onPostExecute(ToDo dataItem) {
                super.onPostExecute(dataItem);
                if(dataItem!=null){
                    inputDescription.setText(dataItem.getName());
                    inputID.setText(dataItem.getDescription());
                }
            }
        }.execute(itemId);
    }

    private void CreateListener(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveDataItem();

            }
        });
        inputName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_NEXT || actionId==EditorInfo.IME_ACTION_DONE){
                    if(inputName.getText().length()>0){
                        enableSaveButton();
                    }
                }
                return false;
            }
        });
        inputID.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_NEXT || actionId==EditorInfo.IME_ACTION_DONE){
                    if(inputName.getText().length()>0){
                        enableSaveButton();
                    }
                }
                return false;
            }
        });
        inputDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_NEXT || actionId==EditorInfo.IME_ACTION_DONE){
                    if(inputName.getText().length()>0){
                        enableSaveButton();
                    }
                }
                return false;
            }
        });
        checkFavourite.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_NEXT || actionId==EditorInfo.IME_ACTION_DONE){
                    if(inputName.getText().length()>0){
                        enableSaveButton();
                    }
                }
                return false;
            }
        });
        checkDone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_NEXT || actionId==EditorInfo.IME_ACTION_DONE){
                    if(inputName.getText().length()>0){
                        enableSaveButton();
                    }
                }
                return false;
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
        String inpName = inputDescription.getText().toString();
        String inpDescription = inputID.getText().toString();

        //EDIT
        if (itemId != -1 && selectedItem != null) {
            selectedItem.setName(inputDescription.getText().toString());
            selectedItem.setDescription(inputID.getText().toString());
            new UpdateItemTask(crudOperations).run(selectedItem, updated -> {

                if(updated){
                    returnIntent.putExtra(ARG_ITEM_ID, selectedItem.getId());

                    returnIntent.putExtra("ToDoItem", selectedItem);
                    setResult(STATUS_EDITED,returnIntent);
                    setContentView(R.layout.activity_main);

                    finish();
                }

            });
        }
        //Create
        else {
            if (itemId == -1) {

                //TODO: CreateItemTask bauen: Parameter mehrere Strings und nicht das Objekt --> ID != 0

                ToDo toDoElementToCreate = new ToDo();
                toDoElementToCreate.setName(inpName);
                toDoElementToCreate.setDescription(inpDescription);
                toDoElementToCreate.setDone(checkDone.isChecked());
                toDoElementToCreate.setFavourite(checkFavourite.isChecked());
                Date date = new Date();
                Long tsLong = System.currentTimeMillis()/1000;
                //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.uuuu");
                //LocalDate datePart = LocalDate.parse(inputDueDate.getText(),formatter);
                //LocalTime timePart = LocalTime.parse(inputDueTime.getText());
                //LocalDateTime dt = LocalDateTime.of(datePart, timePart);
                toDoElementToCreate.setExpiry(tsLong);

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
}
