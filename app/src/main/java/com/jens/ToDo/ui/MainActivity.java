package com.jens.ToDo.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.jens.ToDo.R;
import com.jens.ToDo.model.ToDo;
import com.jens.ToDo.model.ToDoApplication;
import com.jens.ToDo.model.interfaces.IToDoCRUDOperations;
import com.jens.ToDo.model.tasks.CheckRemoteAvailableTask;
import com.jens.ToDo.model.tasks.ReadAllItemsTask;
import com.jens.ToDo.model.tasks.ReadItemTask;
import com.jens.ToDo.model.tasks.UpdateItemTask;

import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    
    //region Constants
    public static final int CALL_DETAILVIEW_FOR_CREATE = 0;
    public static final int CALL_DETAILVIEW_FOR_EDIT = 1;
    public static final int CALL_CONTACT_PICK = 2;
    private static final String LOGGING_TAG = DetailViewActivity.class.getSimpleName();
    //endregion
    
    
    
    //region Variable
    private TextView itemNameView;
    CheckBox itemReadyView;
    private ListView listView;
    private FloatingActionButton floatingActionButton;
    private ArrayAdapter<ToDo> listViewAdapter;
    private Intent newTodoIntent;
    //private ToDoDatabase db;
    private IToDoCRUDOperations crudOperations;
    private ToDo selectedItem;
    List<ToDo> dbItemList = null;
    private ProgressBar progressBar;
    private Comparator<ToDo> alphabeticComperator = (l, r) -> String.valueOf(l.getName()).compareTo(r.getName());
    //endregion 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findElements();
        createListener();
        listViewAdapter = createListViewAdapter();
        new CheckRemoteAvailableTask().run(available -> {
            ((ToDoApplication) getApplication()).setRemoteCRUDMode(available);
            ToDoApplication ToDoApplication = (ToDoApplication) getApplication();
            crudOperations = (IToDoCRUDOperations) ToDoApplication.getCRUDOperations();
            readDatabase();
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overview_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.showContacts) {
            showContacts();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // requestCode = Von welchem Aufruf komme ich zurpck
    // result = Ergebnis (OK / Fehler)
    // data = Daten
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data != null) {
            if (requestCode == CALL_DETAILVIEW_FOR_CREATE && resultCode == DetailViewActivity.STATUS_CREATED) {

                try {
                    selectedItem = (ToDo) data.getSerializableExtra("ToDo");
                    //long itemid = Long.parseLong(data.getStringExtra(NewTodoActivity.ARG_ITEM_ID));

                    updateSort();
                } catch (Exception e) {
                }

                if (selectedItem != null) {
                    listViewAdapter.add(selectedItem);
                    Toast.makeText(this, selectedItem.toString() + " was created", Toast.LENGTH_LONG).show();
                }
            }

            if (requestCode == CALL_DETAILVIEW_FOR_EDIT) {
                if (resultCode == DetailViewActivity.STATUS_EDITED) {
                    selectedItem = (ToDo) data.getSerializableExtra("ToDoItem");
                    long longExtra = selectedItem.getId();
                    new ReadItemTask(this.crudOperations).run(longExtra, selectedItem -> {
                        this.listViewAdapter.clear();
                        this.dbItemList.removeIf(currentItem -> currentItem.getId() == selectedItem.getId());
                        this.dbItemList.add(selectedItem);
                        this.listViewAdapter.addAll(dbItemList);
                        updateSort();
                        editToDoToList(selectedItem);
                    });

                }

                //TODO Button hinzufügen in detailview
                if (resultCode == DetailViewActivity.STATUS_DELETED) {
                    // Semesterprojekt
                    boolean deleted = (Boolean) data.getSerializableExtra("success");

                    ToDo itemToRemoveFromList = (ToDo) data.getSerializableExtra("ToDo");
                    this.listViewAdapter.clear();
                    this.dbItemList.removeIf(currentItem -> currentItem.getId() == itemToRemoveFromList.getId());

                    this.listViewAdapter.addAll(dbItemList);
                    //this.dbItemList.add(itemToRemoveFromList);
                    updateSort();
                    if (deleted) {
                        Toast.makeText(this, "Delted " + selectedItem.getName(), Toast.LENGTH_LONG).show();
                        updateSort();
                    }
                }

            }
            if (requestCode == CALL_CONTACT_PICK && resultCode == Activity.RESULT_OK) {
                Log.i(getClass().getSimpleName(), "got intent from contact picker" + data);
                showContactDetails(data.getData());
            }
        }
    }
    
    private void findElements() {

        listView = findViewById(R.id.listView1);
        floatingActionButton = findViewById(R.id.fabDashboard);
        listView.setAdapter(listViewAdapter);
        progressBar = findViewById(R.id.progressbar);
    }
    private void createListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedItem = listViewAdapter.getItem(position).toString();
                ToDo clickedToDo = listViewAdapter.getItem(position);
                Snackbar.make(view, clickedToDo.toString(), Snackbar.LENGTH_LONG).show();
                selectedItem = clickedToDo;
                handleSelectedItem(clickedToDo);
            }
        });

        newTodoIntent = new Intent(this, DetailViewActivity.class);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivityForResult(newTodoIntent, CALL_DETAILVIEW_FOR_CREATE);
                //  setContentView(R.layout.activity_detailview);
            }
        });

    }
    private void handleSelectedItem(ToDo clickedToDo) {
        Intent newTodoIntentForEdit = new Intent(this, DetailViewActivity.class);
        newTodoIntentForEdit.putExtra(DetailViewActivity.ARG_ITEM_ID, clickedToDo.getId());
        startActivityForResult(newTodoIntentForEdit, CALL_DETAILVIEW_FOR_EDIT);
    }
    private ArrayAdapter<ToDo> createListViewAdapter() {
        return new ArrayAdapter<ToDo>(this, R.layout.activity_main_listitem, R.id.itemName) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View itemView = convertView;

                itemView = getLayoutInflater().inflate(R.layout.activity_main_listitem, null);

                itemNameView = itemView.findViewById(R.id.itemName);
                ToDo currentItem = getItem(position);
                itemReadyView = itemView.findViewById(R.id.itemReady);

                if (itemNameView != null && itemReadyView != null) {
                    itemNameView.setText(currentItem.toString());
                    itemReadyView.setOnCheckedChangeListener(null);
                    itemReadyView.setChecked(currentItem.isDone());
                    itemReadyView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            currentItem.setDone(isChecked);
                            new UpdateItemTask(crudOperations).run(currentItem, updated -> {
                                // TODO: Wird aus irgendeinem Grund nicht ausgeführt
                                if (updated) {
                                    Toast.makeText(MainActivity.this, "Updated " + currentItem.getName(), Toast.LENGTH_LONG).show();
                                    updateSort();
                                }
                            });
                        }
                    });
                }
                return itemView;
            }
        };

    }
    private void updateSort() {
        this.listViewAdapter.sort(alphabeticComperator);
        //this.listViewAdapter.sort(undonedoneComperator);
        listViewAdapter.notifyDataSetChanged();
        listView.setAdapter(listViewAdapter);
        listView.invalidateViews();
        listView.refreshDrawableState();
    }
    private void editToDoToList(ToDo d1) {
        //TODO Kein Refresh, eventuell Object selectedItem wird nur aktualisiert???

        //listViewAdapter.add(d1);
        selectedItem.setDescription(d1.getDescription());
        selectedItem.setName(d1.getName());
        //TODO HIER WEITER MACHEN!!!
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void readDatabase() {

        new ReadAllItemsTask(this.crudOperations, progressBar).run(ToDos -> {
            dbItemList = ToDos;

            listViewAdapter.addAll(ToDos);
            updateSort();

            //dbItemList.sort((l,r)->String.valueOf(l.getName()).compareTo(r.getName()));
//            ToDos.sort(new Comparator<ToDo>() {
//                @Override
//                public int compare(ToDo o1, ToDo o2) {
//
//                    return String.valueOf(o1.getName()).compareTo(o2.getName());
//                }
//            });

        });

    }

    //region Contacts

    private void showContactDetails(Uri contactUri) {
        Log.i(LOGGING_TAG, String.format("got contactURI: %s", contactUri));
        Cursor contactsCursor = getContentResolver().query(contactUri, null, null, null);
        if (contactsCursor.moveToFirst()) {
            String contactName = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String contactId = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts._ID));

            Log.i(LOGGING_TAG, String.format("contactName: %s", contactName));
            Log.i(LOGGING_TAG, String.format("contactID: %s", contactId));

            if (verifyReadContactPermission()) {
                Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{contactId}, null);


                Cursor pCur = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                + " = ?", new String[] { contactId }, null);
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
                        String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));

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
    private void showContacts() {
        Intent contactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactIntent, CALL_CONTACT_PICK);
    }

    //endregion
}
