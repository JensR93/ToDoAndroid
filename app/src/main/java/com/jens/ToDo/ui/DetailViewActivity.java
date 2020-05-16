package com.jens.ToDo.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.jens.ToDo.R;
import com.jens.ToDo.model.ToDo;
import com.jens.ToDo.model.ToDoApplication;
import com.jens.ToDo.model.interfaces.IToDoCRUDOperations;
import com.jens.ToDo.model.tasks.DeleteItemTask;
import com.jens.ToDo.model.tasks.UpdateItemTask;

public class DetailViewActivity extends AppCompatActivity {


    //region Variable

    FloatingActionButton fab;
    TextInputEditText inputName;
    TextInputEditText inputDescription;
    TextInputEditText inputID;
    IToDoCRUDOperations crudOperations;
    ToDo selectedItem;
    MenuItem saveMenuItem;

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
    }
    private void enableSaveButton() {
        saveMenuItem.getIcon().setAlpha(255);
        saveMenuItem.setEnabled(true);
    }
    private void deleteDataItem() {
        Intent returnIntent = new Intent();
        new DeleteItemTask(this.crudOperations).run(selectedItem.getId(), success -> {
            returnIntent.putExtra("dataitem", selectedItem);
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

                ToDo d = new ToDo();
                d.setName(inpName);
                d.setDescription(inpDescription);
                new Thread(() -> {
                    selectedItem = crudOperations.createItem(d);
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
