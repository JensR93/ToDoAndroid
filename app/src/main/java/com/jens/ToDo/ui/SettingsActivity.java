package com.jens.ToDo.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.jens.ToDo.R;
import com.jens.ToDo.model.ToDoApplication;
import com.jens.ToDo.model.interfaces.IToDoCRUDOperations;
import com.jens.ToDo.model.tasks.CheckRemoteAvailableTask;
import com.jens.ToDo.model.tasks.DeleteAllItemTask;
import com.jens.ToDo.model.tasks.DeleteItemTask;

public class SettingsActivity extends AppCompatActivity {

    private IToDoCRUDOperations crudOperations;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_activity);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }



        crudOperations=  ((ToDoApplication) getApplication()).getCRUDOperations();

        Button b = findViewById(R.id.buttonCheckDBConnection);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new CheckRemoteAvailableTask().run(available -> {
                    ((ToDoApplication) getApplication()).setRemoteCRUDMode(available);
                    if(available)
                    {
                        Toast.makeText(SettingsActivity.this,"Remote available",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(SettingsActivity.this,"Remote not available",Toast.LENGTH_SHORT).show();
                    }
                    ToDoApplication ToDoApplication = (ToDoApplication) getApplication();

                });

            }
        });
        Button b2 = findViewById(R.id.buttondeleteAllToDo);

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new DeleteAllItemTask(crudOperations).run(success -> {

                    if(success){
                        Toast.makeText(SettingsActivity.this,"Delete success",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(SettingsActivity.this,"Delete failed",Toast.LENGTH_SHORT).show();
                    }
                    //setContentView(R.layout.activity_main);
                    //finish();
                });

            }
        });
    }


}