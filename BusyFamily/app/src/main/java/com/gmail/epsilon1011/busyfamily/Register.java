package com.gmail.epsilon1011.busyfamily;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    private Button Registerbtn;
    private Button rLoginbtn;
    private EditText RegisterEmail;
    private EditText RegisterPassword;
    private EditText RegisterPassword2;
    private ProgressBar RegisterProg;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        Registerbtn =  findViewById(R.id.registerbtn);
        rLoginbtn =  findViewById(R.id.rloginbtn);
        RegisterEmail = (TextInputEditText) findViewById(R.id.registerEmail);
        RegisterPassword = (TextInputEditText) findViewById(R.id.registerPassword);
        RegisterPassword2 = (TextInputEditText) findViewById(R.id.registerPassword2);
        RegisterProg = (ProgressBar) findViewById(R.id.registerProg);

        mAuth=FirebaseAuth.getInstance();

        rLoginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rLoginbtn.setEnabled(false);
                Registerbtn.setEnabled(false);
                sendToLogin();
            }
        });


        Registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rLoginbtn.setEnabled(false);
                Registerbtn.setEnabled(false);

                String email = RegisterEmail.getText().toString();
                String password = RegisterPassword.getText().toString();
                String password2 = RegisterPassword2.getText().toString();


                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)
                        && !TextUtils.isEmpty(password2)){

                    if(password.equals(password2)){

                        RegisterProg.setVisibility(View.VISIBLE);

                        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()){

                                    sendToSetUsername();

                                } else{
                                    rLoginbtn.setEnabled(true);
                                    Registerbtn.setEnabled(true);

                                    String e =  task.getException().getMessage();
                                    Toast.makeText(Register.this , "Error : " +e,Toast.LENGTH_LONG).show();
                                }


                                RegisterProg.setVisibility(View.INVISIBLE);
                            }
                        });

                    } else {
                        rLoginbtn.setEnabled(true);
                        Registerbtn.setEnabled(true);

                        Toast.makeText(Register.this , "Passwords Do Not Match!!",Toast.LENGTH_LONG).show();

                    }

                } else{
                    rLoginbtn.setEnabled(true);
                    Registerbtn.setEnabled(true);
                }

            }
        });




    }
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){

            sendToMain();
        }


    }
    private void sendToMain() {

        Intent mainIntent = new Intent(Register.this , MainMenu.class);
        startActivity(mainIntent);
        finish();
    }

    private void sendToLogin(){
        Intent loginIntent = new Intent(Register.this,Login.class);
        startActivity(loginIntent);
        finish();
    }

    private void sendToSetUsername(){

        Intent setUsernameIntent = new Intent(Register.this,SetUsername.class);
        startActivity(setUsernameIntent);
        finish();

    }
    @Override
    public void onBackPressed() {

        sendToLogin();
    }


}
