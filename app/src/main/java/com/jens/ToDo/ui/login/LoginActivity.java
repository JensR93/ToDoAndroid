package com.jens.ToDo.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
    private TextView textViewRemoteAvailable, textViewMessage;
    private boolean validEmail,validPassword;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar=findViewById(R.id.loading);
        progressBar.setVisibility(View.VISIBLE);
        checkRemoteAvailable();
        findElements();
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

                setMessage("",0,false);
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
                setMessage("",0,false);
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
        if(text!=null&&text.length()>0&&text.contains("@")&&text.length()>4)
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
                //Toast.makeText(this, R.string.taskRemoteAvailable,ToasRemoteAvailablet.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                textViewRemoteAvailable.setVisibility(View.VISIBLE);

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

        textViewRemoteAvailable=findViewById(R.id.RemoteAvailable);
        textViewMessage =findViewById(R.id.Message);
        buttonStandardLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email.setText("s@bht.de");
                password.setText("000000");
            }
        });
        email.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(password.getText()!=null){
                        if(password.getText().length()!=6&&password.getText().length()>0){
                        setMessage("Password is not 6 digits",2000,true);
                    }
                }
                return false;
            }
        });
        email.setOnClickListener(v -> {
            if(password.getText()!=null){
                if(password.getText().length()!=6&&password.getText().length()>0){
                    setMessage("Password is not 6 digits",2000,true);
                }
            }
        });
        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(email.getText()!=null&&email.getText().length()>0){
                    if(email.getText().length()<5){
                        setMessage("email adress is too short",2000,true);
                    }
                    else if(!email.getText().toString().contains("@")){
                        setMessage("Please enter a valid email",2000,true);
                    }
                }
                return false;
            }
        });
        password.setOnClickListener(v -> {
            if(email.getText()!=null){
                if(email.getText().length()<5){
                    setMessage("email adress is too short",2000,true);
                }
                else if(!email.getText().toString().contains("@")){
                    setMessage("Please enter a valid email",2000,true);
                }
            }
        });

        buttonSignIn.setOnClickListener(v -> {
            User user = new User(email.getText().toString(),password.getText().toString());
            progressBar.setVisibility(View.VISIBLE);


            crudOperations.authenticateUser(user).enqueue( new Callback<Boolean>() {

                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    progressBar.setVisibility(View.GONE);
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
                        Toast.makeText(LoginActivity.this,"Login failed",Toast.LENGTH_LONG).show();

                        setMessage("Login failed",2000,true);
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this,"Login failed",Toast.LENGTH_LONG).show();
                    setMessage("Login failed",2000,true);
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

    private void setMessage(String message, int duration, boolean fail){
        if(fail){
            textViewMessage.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
            textViewMessage.setTextColor(Color.RED);
        }
        else{

            textViewMessage.setTextColor(Color.GREEN);
            textViewMessage.setTextSize(TypedValue.COMPLEX_UNIT_DIP,12);
        }
        textViewMessage.setVisibility(View.VISIBLE);
        textViewMessage.setText(message);
    }
}
