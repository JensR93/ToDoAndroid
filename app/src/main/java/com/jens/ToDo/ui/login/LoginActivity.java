package com.jens.ToDo.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jens.ToDo.R;
import com.jens.ToDo.model.RemoteUserOperationsImpl;
import com.jens.ToDo.model.ToDoApplication;
import com.jens.ToDo.model.User;
import com.jens.ToDo.model.interfaces.IToDoCRUDOperations;
import com.jens.ToDo.model.tasks.CheckRemoteAvailableTask;
import com.jens.ToDo.ui.DetailView.DetailViewActivity;
import com.jens.ToDo.ui.Main.MainActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends Activity {
    private IToDoCRUDOperations crudOperations;
    private EditText email;
    private EditText password;
    private Button buttonSignIn;
    private Button buttonStandardLogin;

    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar=findViewById(R.id.loading);
        checkRemoteAvailable();



    }

    private void  checkRemoteAvailable() {
        progressBar.setVisibility(View.VISIBLE);
        new CheckRemoteAvailableTask().run(available -> {
            ((ToDoApplication) getApplication()).setRemoteCRUDMode(available);
            if(available)
            {
                Toast.makeText(this, R.string.taskRemoteAvailable,Toast.LENGTH_LONG).show();
                findElements();
            }
            else{
                Intent remoteNotAvailableIntent = new Intent(this, MainActivity.class);
                startActivity(remoteNotAvailableIntent);

            }
            ToDoApplication ToDoApplication = (ToDoApplication) getApplication();
            crudOperations = (IToDoCRUDOperations) ToDoApplication.getCRUDOperations();
            //Thread.sleep(2000);

            progressBar.setVisibility(View.GONE);

        });
    }

    private void findElements() {
        password=findViewById(R.id.password);
        email=findViewById(R.id.username);
        buttonSignIn=findViewById(R.id.login);
        buttonStandardLogin=findViewById(R.id.Standardlogin);
        buttonStandardLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email.setText("s@bht.de");
                password.setText("000000");
            }
        });
        buttonSignIn.setOnClickListener(v -> {
            User user = new User(email.getText().toString(),password.getText().toString());
            RemoteUserOperationsImpl r = new RemoteUserOperationsImpl();
            crudOperations.authenticateUser(user).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if(response.body()!=null&&response.body()){
                    Intent loginSuccessIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(loginSuccessIntent);
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {

                }
            });
//            boolean success = r.authenticateUser(user);
//            Toast.makeText(this,"login = "+success,Toast.LENGTH_LONG).show();
//            if(success){
//                Intent loginSuccessIntent = new Intent(this, MainActivity.class);
//                startActivity(loginSuccessIntent);
//            }
        });


    }

}
