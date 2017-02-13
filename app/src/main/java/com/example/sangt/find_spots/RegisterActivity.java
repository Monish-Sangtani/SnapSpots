package com.example.sangt.find_spots;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegisterActivity extends Activity {



    private FirebaseAuth firebaseAuth;



    private EditText txtEmail=null;
    private EditText txtPassword1=null;
    private EditText txtPassword2=null;


    AlertDialog.Builder alert=null;


    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        progressDialog = new ProgressDialog(this);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword1 = (EditText) findViewById(R.id.txtPassword1);
        txtPassword2 = (EditText) findViewById(R.id.txtPassword2);


        alert= new AlertDialog.Builder(this);



        firebaseAuth = FirebaseAuth.getInstance();

        final Button btn_register = (Button) findViewById(R.id.btn_register);


        btn_register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                register();
            }
        });
    }


    public void register()
    {

        String email=txtEmail.getText().toString().trim();
        String pass1=txtPassword1.getText().toString().trim();
        String pass2=txtPassword2.getText().toString().trim();



        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(pass1)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }

        if(!pass1.equals(pass2))
        {
            Toast.makeText(this,"Passwords don't match",Toast.LENGTH_LONG).show();
            return;
        }

        if(pass1.length()<6)
        {
            Toast.makeText(this,"Password too short",Toast.LENGTH_LONG).show();
            return;
        }


        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();


        firebaseAuth.createUserWithEmailAndPassword(email, pass1)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {



                        //checking if success
                        progressDialog.hide();
                        if(task.isSuccessful()){


                            Intent myIntent = new Intent(RegisterActivity.this, MapsActivity.class);
                            startActivity(myIntent);
                        }else{


                            if(task.getException()!=null)
                            {

                                Toast.makeText(getApplication(),"Error registering: "+task.getException().toString(),Toast.LENGTH_LONG).show();
                               return;
//                                Log.d("D", task.getException().getMessage());
//                                alert.setTitle("Error");
//                                alert.setMessage("Registration not successful: "+task.getResult().toString());
//                                alert.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
//
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                });
//                                AlertDialog dialog = alert.create();
//                                //alert.show();
//                                dialog.show();
                            }

                        }


                    }
                });



    }
}
