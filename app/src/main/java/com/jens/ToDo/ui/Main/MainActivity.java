package com.jens.ToDo.ui.Main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.jens.ToDo.R;
import com.jens.ToDo.model.Settings;
import com.jens.ToDo.model.ToDo;
import com.jens.ToDo.model.ToDoApplication;
import com.jens.ToDo.model.ToDoContact;
import com.jens.ToDo.model.impl.SyncedToDoCrudOperations;
import com.jens.ToDo.model.interfaces.IToDoCRUDOperations;
import com.jens.ToDo.model.tasks.CheckRemoteAvailableTask;
import com.jens.ToDo.model.tasks.CreateItemTask;
import com.jens.ToDo.model.tasks.DeleteAllItemTask;
import com.jens.ToDo.model.tasks.DeleteAllLocalItemTask;
import com.jens.ToDo.model.tasks.DeleteAllRemoteItemTask;
import com.jens.ToDo.model.tasks.ReadAllItemsTask;
import com.jens.ToDo.model.tasks.ReadItemTask;
import com.jens.ToDo.model.tasks.SyncAllWithLocalItemTask;
import com.jens.ToDo.model.tasks.UpdateItemTask;
import com.jens.ToDo.ui.DetailView.ContactManager;
import com.jens.ToDo.ui.DetailView.DetailViewActivity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    boolean isConnected=false;
    private ContactManager contactmanager;
    Dialog myDialog;
    Settings mySettings;
    ListView listView;
    ImageButton reloadButton;
    List <View> viewItemList;
    LinearLayout linearLayoutMessage;
    //region Constants
    public static final int CALL_DETAILVIEW_FOR_CREATE = 0;
    public static final int CALL_DETAILVIEW_FOR_EDIT = 1;
    private static final String LOGGING_TAG = DetailViewActivity.class.getSimpleName();

    //endregion
    
    private boolean favouriteSort = true;
    private ContactManager ccc;
    
    //region Variable

    private ArrayAdapter<ToDo> listViewAdapter;
    private ArrayAdapter<ToDoContact> listViewAdapterContacts;
    private Intent newTodoIntent;

    private Intent settingsIntent;    //private ToDoDatabase db;
    private IToDoCRUDOperations crudOperations;
    private ToDo selectedItem;
    List<ToDo> dbItemList = null;
    private ProgressBar progressBar;
    private TextView textMessageMain;

    private Comparator<ToDo> alphabeticComperator = (l, r) -> String.valueOf(l.getName()).compareTo(r.getName());
    ArrayAdapter<ToDo> ArrayAdapterToDoItemContact;
    private Comparator<ToDo> expiryComperator = (l, r) -> String.valueOf(l.getExpiry()).compareTo(String.valueOf(r.getExpiry()));
    private Comparator<ToDo> undonedoneComperator = (l, r) -> Boolean.compare(l.isDone(),r.isDone());

    private Comparator<ToDo> favouriteComperator = (l, r) -> Boolean.compare(r.isFavourite(),l.isFavourite());
    //endregion 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewItemList=new ArrayList<View>();
        contactmanager=new ContactManager(MainActivity.this);
        mySettings=new Settings(true,true,true,true,true);

        setContentView(R.layout.activity_main);
        findElements();


        listViewAdapter = createListViewAdapter(this);
        linearLayoutMessage.setVisibility(View.GONE);


        myDialog=new Dialog(this);

        loadToDoElementsFromDatabase();
        updateColoumShow();
    }

    private void loadToDoElementsFromDatabase() {
        new CheckRemoteAvailableTask(progressBar).run(available -> {
            ((ToDoApplication) getApplication()).setRemoteCRUDMode(available);

            ToDoApplication ToDoApplication = (ToDoApplication) getApplication();
            crudOperations = (IToDoCRUDOperations) ToDoApplication.getCRUDOperations();

            createListener();
            readDatabase(true);
            if(available)
            {
                isConnected=true;


            }
            else{
                isConnected=false;
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overview_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.showColoumns){
            showColoumnPopup();
            return true;
        }
        if(item.getItemId()==R.id.createToDo){
            newTodoIntent = new Intent(this, DetailViewActivity.class);
            startActivityForResult(newTodoIntent, CALL_DETAILVIEW_FOR_CREATE);
            return true;
        }
        if(item.getItemId()==R.id.showSort){
            showSortPopup();
            return true;
        }
        if(item.getItemId()==R.id.DeleteAllLocal){
            deleteAllLocalDataItems();
            return true;
        }
        if(item.getItemId()==R.id.DeleteAllRemote){
            deleteAllRemoteDataItems();
            return true;
        }
        if(item.getItemId()==R.id.StartSync){
            startSync();
            return true;
        }
        if(item.getItemId()==R.id.CheckConnection){
            new CheckRemoteAvailableTask(progressBar).run(available -> {
                ((ToDoApplication) getApplication()).setRemoteCRUDMode(available);
                progressBar.setVisibility(View.GONE);
                ToDoApplication ToDoApplication = (ToDoApplication) getApplication();
                crudOperations = (IToDoCRUDOperations) ToDoApplication.getCRUDOperations();

                if(available)
                {
                    isConnected=true;
                    setMessageText("Connection Test successfull",5000,10);
                }
                else{
                    isConnected=false;
                    setMessageText("Connection Test failed",0,10);
                }

            });
            return true;
        }


        if(item.getItemId()==R.id.SyncwithLocalItems){
            syncToDoWithLocal();
            return true;
        }
        if(item.getItemId()==R.id.SyncwithRemoteItems){
            syncToDoWithRemote();
            return true;
        }
        if(item.getItemId()==R.id.DeleteAll){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.mainDeleteAllElements);
            builder.setPositiveButton(R.string.mainDeleteAll, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteAllDataItem();
                }
            });
            builder.setNegativeButton(R.string.mainAbort, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this, R.string.mainAbort, Toast.LENGTH_SHORT).show();
                }
            });
            builder.create().show();


        }
        return super.onOptionsItemSelected(item);
    }

    private void startSync() {
        if(isConnected)
        {
            if (listViewAdapter.getCount() > 0) {
                syncToDoWithLocal();
            } else {
                syncToDoWithRemote();
            }
        }
    }

    private void deleteAllLocalDataItems() {
        SyncedToDoCrudOperations syncedToDoCrudOperations = (SyncedToDoCrudOperations) crudOperations;
        new DeleteAllLocalItemTask(syncedToDoCrudOperations).run(success -> {

            if(success){
                setMessageText("Local Todo deleted",2000,6);
                listViewAdapter.clear();
                dbItemList.clear();
            }
            else{

                setMessageText("Local Todo not deleted",2000,10);
            }
            //setContentView(R.layout.activity_main);
            //finish();
        });
    }
    private void deleteAllRemoteDataItems() {
        SyncedToDoCrudOperations syncedToDoCrudOperations = (SyncedToDoCrudOperations) crudOperations;
        new DeleteAllRemoteItemTask(syncedToDoCrudOperations).run(success -> {

            if(success){
                setMessageText("Remote Todo deleted",2000,6);
            }
            else{
                setMessageText("Remote Todo not deleted",2000,10);
            }
            //setContentView(R.layout.activity_main);
            //finish();
        });
    }
    private void deleteAllDataItem() {

        new DeleteAllItemTask(crudOperations).run(success -> {

            if(success){

                setMessageText("Delete all ToDo successfull",2000,6);
                listViewAdapter.clear();
                dbItemList.clear();
            }
            else{
                setMessageText("Delete all ToDo failed",0,6);
            }
            //setContentView(R.layout.activity_main);
            //finish();
        });
    }
    private void syncToDoWithLocal() {

        new SyncAllWithLocalItemTask(crudOperations).run(success -> {

            if(success){
                setMessageText("Sync remote connection with local successfull",2000,6);
            }
            else{
                setMessageText("Sync remote connection with local not successfull",0,6);
            }
            //setContentView(R.layout.activity_main);
            //finish();
        });
    }
    private void syncToDoWithRemote() {

        SyncedToDoCrudOperations syncedToDoCrudOperations = (SyncedToDoCrudOperations) crudOperations;
        new DeleteAllLocalItemTask(syncedToDoCrudOperations).run(success ->{
            if(success){
                new ReadAllItemsTask(syncedToDoCrudOperations.getRemoteCrud(), progressBar).run(ToDos -> {
                    dbItemList = ToDos;
                    for ( ToDo todo : dbItemList)
                    {
                        if(todo.getContacts()==null){
                            todo.setContacts(new ArrayList<String >());
                        }
                        new CreateItemTask(syncedToDoCrudOperations.getLocalCrud()).run(todo, todonew -> {
                            if(dbItemList.size()>0){
                                listViewAdapter.clear();
                                listViewAdapter.addAll(ToDos);
                                updateSort();
                                String message ="";}
                        });

                    }


                });
            }
        });

/*        new SyncAllWithRemoteItemTask(crudOperations).run(success -> {

            if(success){

                setMessageText("Sync local connection with remote successfull",2000,6);
                readDatabase(false);
            }
            else{


                setMessageText("Sync local connection with remote not successfull",0,6);
            }
            //setContentView(R.layout.activity_main);
            //finish();
        });*/
    }
    private void showColoumnPopup() {

        myDialog.setContentView(R.layout.activity_main_coloumn_popup);
        Button buttonOk = myDialog.findViewById(R.id.itemButtonOk);
        CheckBox checkShowDescription = myDialog.findViewById(R.id.itemDescription);
        CheckBox checkShowExpiry = myDialog.findViewById(R.id.itemExpiry);
        CheckBox checkShowBookmark = myDialog.findViewById(R.id.itemBoomark);
        CheckBox checkShowContacts =myDialog.findViewById(R.id.itemContacts);
        CheckBox checkShowDone =myDialog.findViewById(R.id.itemDone);

        checkShowBookmark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mySettings.setShowBookmark(isChecked);

                updateColoumShow();

            }
        });
        checkShowDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mySettings.setShowDone(isChecked);

                updateColoumShow();

            }
        });
        checkShowContacts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mySettings.setShowContacts(isChecked);

                updateColoumShow();

            }
        });
        checkShowDescription.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mySettings.setShowDescription(isChecked);
                updateColoumShow();
            }
        });
        checkShowExpiry.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mySettings.setShowExpiry(isChecked);
                updateColoumShow();
            }
        });
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();

            }
        });

        checkShowBookmark.setChecked(mySettings.isShowBookmark());
        checkShowDescription.setChecked(mySettings.isShowDescription());
        checkShowExpiry.setChecked(mySettings.isShowExpiry());
        checkShowContacts.setChecked(mySettings.isShowContacts());
        checkShowDone.setChecked(mySettings.isShowDone());
        myDialog.show();


    }

    private void updateColoumShow() {
        if(viewItemList.size()>0){
        for(int i =0;i<viewItemList.size();i++){
            View v =  viewItemList.get(i);
            CheckBox itemBookmark = v.findViewById(R.id.itemFavourite);
            CheckBox itemDone = v.findViewById(R.id.itemReady);

            LinearLayout itemDescriptionViewLinearLayout= v.findViewById(R.id.itemDescriptionLayout);
            LinearLayout itemExiryLinearLayout= v.findViewById(R.id.itemExiryLayout);
            LinearLayout itemContactsLinearLayout= v.findViewById(R.id.itemContactsLayout);

            if(mySettings.isShowDescription()){
                itemDescriptionViewLinearLayout.setVisibility(View.VISIBLE);
            }
            else{
                itemDescriptionViewLinearLayout.setVisibility(View.GONE);
            }
            if(mySettings.isShowExpiry()){
                itemExiryLinearLayout.setVisibility(View.VISIBLE);

            }
            else{
                itemExiryLinearLayout.setVisibility(View.GONE);

            }
            if(mySettings.isShowBookmark()){
                itemBookmark.setVisibility(View.VISIBLE);


            }
            else{
                itemBookmark.setVisibility(View.GONE);
            }
            if(mySettings.isShowDone()){
                itemDone.setVisibility(View.VISIBLE);

            }
            else{
                itemDone.setVisibility(View.GONE);
            }
            if(mySettings.isShowContacts()){

                itemContactsLinearLayout.setVisibility(View.VISIBLE);
            }
            else{
                itemContactsLinearLayout.setVisibility(View.GONE);

            }

        }}
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
                        updateColoumShow();
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
                        updateSort();
                    }
                    updateColoumShow();
                }

            }
            updateColoumShow();
        }
    }
    
    private void findElements() {
        linearLayoutMessage=findViewById(R.id.linearLayoutMessage);
        listView = findViewById(R.id.listView1);
        listView.setAdapter(listViewAdapter);
        progressBar = findViewById(R.id.progressbar);
        textMessageMain=findViewById(R.id.textMessageMain);
        reloadButton = findViewById(R.id.imageButtonReloadConnection);


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


        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadToDoElementsFromDatabase();
            }
        });



    }
    private void setMessageText(String text, int length, int textsize){
        linearLayoutMessage.setVisibility(View.VISIBLE);
        if(isConnected){
            if(textsize>0){
                float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, textsize, getResources().getDisplayMetrics());
                textMessageMain.setTextSize(pixels);
            }
            textMessageMain.setTextColor(Color.GREEN);
            reloadButton.setVisibility(View.GONE);


            new AsyncTask<Void,Void,Void>(){

                @Override
                protected void onPostExecute(Void aVoid) {
                    linearLayoutMessage.setVisibility(View.GONE);
                    super.onPostExecute(aVoid);
                }

                @Override
                protected Void doInBackground(Void... voids) {
                    if(length>0){
                    try {
                        Thread.sleep(length);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    }
                    return null;
                }
            }.execute();
        }
        else{
            textMessageMain.setTextColor(Color.RED);
            reloadButton.setVisibility(View.VISIBLE);
        }
        textMessageMain.setText(text);
    }
    private void handleSelectedItem(ToDo clickedToDo) {
        Intent newTodoIntentForEdit = new Intent(this, DetailViewActivity.class);
        newTodoIntentForEdit.putExtra(DetailViewActivity.ARG_ITEM_ID, clickedToDo.getId());
        startActivityForResult(newTodoIntentForEdit, CALL_DETAILVIEW_FOR_EDIT);
    }

    private ArrayAdapter<ToDo> createListViewAdapter(MainActivity mainActivity) {


       ArrayAdapter<ToDo> a =   new ArrayAdapter<ToDo>(this, R.layout.activity_main_listitem, R.id.itemName) {


            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View itemView = convertView;
                ToDo currentItem = getItem(position);
                if(currentItem.getToDoContactList()!=null&&currentItem.getToDoContactList().size()==0){
                    contactmanager.readContactFromDataItem(currentItem);

                }
                itemView = getLayoutInflater().inflate(R.layout.activity_main_listitem, null);
                viewItemList.add(itemView);
                TextView itemNameView = itemView.findViewById(R.id.itemName);

                TextView itemDescriptionView = itemView.findViewById(R.id.itemDescription);
                TextView itemExiry= itemView.findViewById(R.id.itemExiry);
                TextView itemContacts= itemView.findViewById(R.id.itemContacts);

                LinearLayout itemDescriptionViewLinearLayout= itemView.findViewById(R.id.itemDescriptionLayout);
                LinearLayout  itemExiryLinearLayout= itemView.findViewById(R.id.itemExiryLayout);
                LinearLayout  itemContactsLinearLayout= itemView.findViewById(R.id.itemContactsLayout);


                CheckBox itemReadyView = itemView.findViewById(R.id.itemReady);
                CheckBox itemFavouriteView=itemView.findViewById(R.id.itemFavourite);
                if (itemNameView != null && itemReadyView != null) {



                    itemNameView.setText(currentItem.toString());

                    itemDescriptionView.setText(currentItem.getDescription());
                    if(currentItem.getExpiry()!=null) {

                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.uuuu");

                            LocalDateTime date =LocalDateTime.ofInstant(Instant.ofEpochMilli(currentItem.getExpiry()), ZoneId.systemDefault());

                            LocalTime localTime = date.toLocalTime();

                            String localDateString = formatter.format(date);

                        itemExiry.setText(localDateString+" "+localTime);
                    }
                    if(currentItem.getToDoContactList()!=null){
                        int count =0;
                        String value="";
                        for(ToDoContact contact: currentItem.getToDoContactList()){
                            value+=contact.getName()+"\n";
                        }
                        itemContacts.setText(value);
                    }

                    long now= new java.sql.Timestamp(System.currentTimeMillis()).getTime();
                    if(currentItem.getExpiry()!=null&&currentItem.getExpiry()<now)
                    {
                        itemNameView.setTextColor(Color.RED);
                        itemExiry.setTextColor(Color.RED);
                    }

                    itemReadyView.setOnCheckedChangeListener(null);
                    itemReadyView.setChecked(currentItem.isDone());
                    itemFavouriteView.setChecked(currentItem.isFavourite());


                    itemReadyView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            currentItem.setDone(isChecked);
                            new UpdateItemTask(crudOperations).run(currentItem, updated -> {
                                if (updated) {
                                    updateSort();
                                }
                            });
                        }
                    });
                    itemFavouriteView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            currentItem.setFavourite(isChecked);
                            new UpdateItemTask(crudOperations).run(currentItem, updated -> {
                                if (updated) {
                                    updateSort();
                                }
                            });
                        }
                    });




                }


                return itemView;

            }


        };

return  a;
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
    private void readDatabase(boolean sync) {

        new ReadAllItemsTask(this.crudOperations, progressBar).run(ToDos -> {
            dbItemList = ToDos;
            if(dbItemList.size()>0){
            listViewAdapter.clear();
            listViewAdapter.addAll(ToDos);
            updateSort();
            String message ="";}
            if(sync){
               // message="Remote Connection successfull";
                startSync();
            }



        });

    }

}
