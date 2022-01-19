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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    private Button Loginbtn;
    private Button lRegisterbtn;
    private EditText LoginEmail;
    private EditText LoginPassword;
    private ProgressBar LoginProg;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();
        mStore=FirebaseFirestore.getInstance();


        Loginbtn = (Button) findViewById(R.id.loginbtn);
        lRegisterbtn = (Button) findViewById(R.id.lregisterbtn);
        LoginEmail = (TextInputEditText) findViewById(R.id.loginEmail);
        LoginPassword = (TextInputEditText) findViewById(R.id.loginPassword);
        LoginProg = (ProgressBar) findViewById(R.id.loginProg);

        lRegisterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lRegisterbtn.setEnabled(false);
                Loginbtn.setEnabled(false);
                sendToRegister();
            }
        });


            Loginbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lRegisterbtn.setEnabled(false);
                    Loginbtn.setEnabled(false);

                    String email = LoginEmail.getText().toString();
                    String password = LoginPassword.getText().toString();

                    if (!TextUtils.isEmpty(email) && (!TextUtils.isEmpty(password))) {

                        LoginProg.setVisibility(View.VISIBLE);

                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    final String userid = mAuth.getCurrentUser().getUid();
                                    mStore.collection("Users").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            DocumentSnapshot document = task.getResult();

                                            if (document.exists()) {
                                                sendToMain();
                                            } else sendToSetUsername();
                                        }

                                    });

                                } else {

                                    lRegisterbtn.setEnabled(true);
                                    Loginbtn.setEnabled(true);

                                    String e = task.getException().getMessage();
                                    Toast.makeText(Login.this, "Error : " + e, Toast.LENGTH_LONG).show();


                                }

                                LoginProg.setVisibility(View.INVISIBLE);


                            }
                        });
                    } else {
                        lRegisterbtn.setEnabled(true);
                        Loginbtn.setEnabled(true);
                    }


                }
            });
    }

    protected void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser!=null){

            sendToMain();

        }


    }


    private void sendToRegister() {
        Intent registerIntent = new Intent(Login.this,Register.class);
        startActivity(registerIntent);
        finish();
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(Login.this,MainMenu.class);
        startActivity(mainIntent);
        finish();
    }

    private void sendToSetUsername(){
        Intent setUsernameIntent = new Intent(Login.this , SetUsername.class);
        startActivity(setUsernameIntent);
        finish();
    }

    @Override
    public void onBackPressed() {

        finish();
    }
}
