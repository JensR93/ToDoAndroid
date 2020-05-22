package com.jens.ToDo.ui.Main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.jens.ToDo.ui.DetailView.DetailViewActivity;
import com.jens.ToDo.ui.SettingsActivity;

import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Dialog myDialog;

    
    //region Constants
    public static final int CALL_DETAILVIEW_FOR_CREATE = 0;
    public static final int CALL_DETAILVIEW_FOR_EDIT = 1;
    private static final String LOGGING_TAG = DetailViewActivity.class.getSimpleName();

    //endregion
    
    private boolean favouriteSort = true;

    
    //region Variable
    private TextView itemNameView;
    CheckBox itemReadyView;
    private ListView listView;
    private FloatingActionButton floatingActionButton;
    private ArrayAdapter<ToDo> listViewAdapter;
    private Intent newTodoIntent;

    private Intent settingsIntent;    //private ToDoDatabase db;
    private IToDoCRUDOperations crudOperations;
    private ToDo selectedItem;
    List<ToDo> dbItemList = null;
    private ProgressBar progressBar;
    private Comparator<ToDo> alphabeticComperator = (l, r) -> String.valueOf(l.getName()).compareTo(r.getName());

    private Comparator<ToDo> expiryComperator = (l, r) -> String.valueOf(l.getExpiry()).compareTo(String.valueOf(r.getExpiry()));
    private Comparator<ToDo> undonedoneComperator = (l, r) -> Boolean.compare(l.isDone(),r.isDone());

    private Comparator<ToDo> favouriteComperator = (l, r) -> Boolean.compare(r.isFavourite(),l.isFavourite());
    //endregion 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findElements();
        createListener();
        listViewAdapter = createListViewAdapter();

        myDialog=new Dialog(this);

        new CheckRemoteAvailableTask().run(available -> {
            ((ToDoApplication) getApplication()).setRemoteCRUDMode(available);
            if(available)
            {
                Toast.makeText(this,"Remote available",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this,"Remote not available",Toast.LENGTH_SHORT).show();
            }
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
        if(item.getItemId()==R.id.showSettings){
            showSettings();
            return true;
        }
        if(item.getItemId()==R.id.showSort){
            showSortPopup();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSortPopup() {

        myDialog.setContentView(R.layout.activity_main_sort_popup);

        RadioGroup radioSortGroup= myDialog.findViewById(R.id.radioGroup_character);
        RadioButton radioSortButtonFavourite= myDialog.findViewById(R.id.radioSortDone);
        RadioButton radioSortButtonExpiry= myDialog.findViewById(R.id.radioSortFavouriteAndExpiry);

        if(favouriteSort){
            radioSortButtonFavourite.setChecked(true);
        }
        else {
            radioSortButtonExpiry.setChecked(true);
        }

        radioSortGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked)
                {
                    if(checkedRadioButton.getText().equals(radioSortButtonFavourite.getText())){
                        favouriteSort=true;
                        updateSort();
                        myDialog.dismiss();
                    }
                    if(checkedRadioButton.getText().equals(radioSortButtonExpiry.getText())){
                        favouriteSort=false;
                        updateSort();
                        myDialog.dismiss();
                    }
                    // Changes the textview's text to "Checked: example radiobutton text"

                }
            }
        });

        myDialog.show();
    }


    // requestCode = Von welchem Aufruf komme ich zurpck
    // result = Ergebnis (OK / Fehler)
    // data = Daten
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (data != null) {
            if (requestCode == CALL_DETAILVIEW_FOR_CREATE && resultCode == DetailViewActivity.STATUS_CREATED) {

                try {
                    selectedItem = (ToDo) data.getSerializableExtra("ToDoItem");
                    //long itemid = Long.parseLong(data.getStringExtra(NewTodoActivity.ARG_ITEM_ID));
                    this.dbItemList.add(selectedItem);
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
                        this.dbItemList.removeIf(currentItem -> currentItem.getId().equals(selectedItem.getId()));
                        this.dbItemList.add(selectedItem);
                        this.listViewAdapter.addAll(dbItemList);
                        updateSort();
                        editToDoToList(selectedItem);
                    });

                }

                if (resultCode == DetailViewActivity.STATUS_DELETED) {
                    // Semesterprojekt
                    boolean deleted = (Boolean) data.getSerializableExtra("success");

                    ToDo itemToRemoveFromList = (ToDo) data.getSerializableExtra("ToDoItem");
                    this.listViewAdapter.clear();
                    this.dbItemList.removeIf(currentItem -> currentItem.getId().equals(itemToRemoveFromList.getId()));

                    this.listViewAdapter.addAll(dbItemList);

                    //this.dbItemList.add(itemToRemoveFromList);
                    updateSort();
                    if (deleted) {
                        Toast.makeText(this, "Deleted " + selectedItem.getName(), Toast.LENGTH_LONG).show();
                        updateSort();
                    }
                }

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

        if(favouriteSort){
            this.listViewAdapter.sort(favouriteComperator);
        }
        else{
            this.listViewAdapter.sort(expiryComperator);
        }

        this.listViewAdapter.sort(undonedoneComperator);
        listViewAdapter.notifyDataSetChanged();
        listView.setAdapter(listViewAdapter);
        listView.invalidateViews();
        listView.refreshDrawableState();
    }

    private void editToDoToList(ToDo d1) {
        selectedItem.setDescription(d1.getDescription());
        selectedItem.setName(d1.getName());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void readDatabase() {

        new ReadAllItemsTask(this.crudOperations, progressBar).run(ToDos -> {
            dbItemList = ToDos;

            listViewAdapter.addAll(ToDos);
            updateSort();

        });

    }
    private void showSettings() {

        settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

}
