package com.jens.ToDo.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.jens.ToDo.model.interfaces.IToDoCRUDOperations;
import com.jens.ToDo.model.tasks.UpdateItemTask;

import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //region Constants
    public static final int CALL_DETAILVIEW_FOR_CREATE = 0;
    public static final int CALL_DETAILVIEW_FOR_EDIT = 1;
    public static final int CALL_CONTACT_PICK = 2;
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
}
