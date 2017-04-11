package com.example.sangt.find_spots;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.view.View;

import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends Activity {



    private EditText txtEmail, txtPassword;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        auth = FirebaseAuth.getInstance();
        txtEmail  = (EditText) findViewById(R.id.txtEmail);
        txtPassword  = (EditText) findViewById(R.id.txtPassword);

        progressDialog = new ProgressDialog(this);

        final Button btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                login();
            }
        });

        final Button btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                register();
            }
        });

        SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);


        txtEmail.setText(myPrefs.getString("username", ""));
    }


    public void register()
    {
        Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(myIntent);
    }


    public boolean successfulLogin()
    {
        return true;
    }

    public void login()
    {


        progressDialog.setMessage("Logging In Please Wait...");
        progressDialog.show();



        //authenticate user
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        if(email.length()==0||password.length()==0)
        {
            Toast.makeText(LoginActivity.this, "Please enter an email and password", Toast.LENGTH_LONG).show();
            progressDialog.hide();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        progressDialog.hide();
                        if (!task.isSuccessful()) {
                            // there was an error

                            Toast.makeText(LoginActivity.this, "Email or password invalid", Toast.LENGTH_LONG).show();

                        } else {

                            SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);


                            SharedPreferences.Editor e = myPrefs.edit();
                            e.putString("username", txtEmail.getText().toString()); // add or overwrite someValue
                            e.commit(); // this saves to disk and notifies observers





                            Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });



    }




}
