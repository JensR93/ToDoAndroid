package com.jens.ToDo.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jens.ToDo.R;
import com.jens.ToDo.model.ToDoApplication;
import com.jens.ToDo.model.User;
import com.jens.ToDo.model.interfaces.IToDoCRUDOperations;
import com.jens.ToDo.model.tasks.CheckRemoteAvailableTask;
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
    private boolean validEmail,validPassword;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar=findViewById(R.id.loading);
        checkRemoteAvailable();
        findElements();
        progressBar.setVisibility(View.GONE);
        buttonSignIn.setEnabled(false);

        createListener();

    }

    private void createListener() {
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(checkValidEmail(s.toString()))
                {
                    validEmail=true;
                }
                else {
                    validEmail=false;
                }
                checkValidLogin();
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(checkValidPassword(s.toString())){
                    validPassword=true;
                }
                else {
                    validPassword=false;
                }
                checkValidLogin();
            }
        });
    }

    private void checkValidLogin() {
        if(validEmail&&validPassword){
            buttonSignIn.setEnabled(true);
        }
        else{
            buttonSignIn.setEnabled(false);
        }
    }

    private boolean checkValidEmail(String text) {
        if(text!=null&&text.length()>0&&text.contains("@"))
        {
            return true;
        }
        return false;
    }

    private boolean checkValidPassword(String text) {
        if(text.length()==6) return true;
        return false;
    }

    private void  checkRemoteAvailable() {

        new CheckRemoteAvailableTask().run(available -> {

            ((ToDoApplication) getApplication()).setRemoteCRUDMode(available);
            if(available)
            {
                //Toast.makeText(this, R.string.taskRemoteAvailable,Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);

            }
            else{
                Intent remoteNotAvailableIntent = new Intent(this, MainActivity.class);
                startActivity(remoteNotAvailableIntent);

            }
            ToDoApplication ToDoApplication = (ToDoApplication) getApplication();
            crudOperations = (IToDoCRUDOperations) ToDoApplication.getCRUDOperations();
            //Thread.sleep(2000);



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
            progressBar.setVisibility(View.VISIBLE);


            crudOperations.authenticateUser(user).enqueue( new Callback<Boolean>() {

                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(response.body()!=null&&response.body()){



                    Intent loginSuccessIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(loginSuccessIntent);
                    }
                    if(response.body()!=null&&!response.body()){
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this,"Login failed",Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this,"Login failed",Toast.LENGTH_LONG).show();
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
