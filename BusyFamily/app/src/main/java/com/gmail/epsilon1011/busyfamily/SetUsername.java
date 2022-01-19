package com.gmail.epsilon1011.busyfamily;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;



public class SetUsername extends AppCompatActivity {


    private EditText textUsername;
    private Button Savebtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_username);


        textUsername = (EditText) findViewById(R.id.textUsername);
        Savebtn = (Button) findViewById(R.id.savebtn);

        mAuth = FirebaseAuth.getInstance();  //Firebase Auth
        mStore = FirebaseFirestore.getInstance();  //Firestore

        //savebtn onClick
        Savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Savebtn.setEnabled(false);
                //get user input
                final String username = textUsername.getText().toString();

                if (!(username.length() >= 6 && username.length() <= 16)) {

                    Toast.makeText(SetUsername.this, "Username must be between 6 and 16 characters!", Toast.LENGTH_LONG).show();
                    Savebtn.setEnabled(true);

                } else {
                    mStore.collection("Users").whereEqualTo("Username", username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (!task.getResult().getDocuments().isEmpty()){
                                Toast.makeText(SetUsername.this, "This username already exists!" , Toast.LENGTH_LONG).show();
                                Savebtn.setEnabled(true);
                            }
                             else {
                                final String userid = mAuth.getCurrentUser().getUid();
                                final String email = mAuth.getCurrentUser().getEmail();

                                //user's map
                                Map<String, String> userMap = new HashMap<>();
                                userMap.put("Username", username);
                                userMap.put("Email", email);
                                userMap.put("Server_id" , "none");

                                //upload map to db
                                mStore.collection("Users").document(userid).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            sendToMain();
                                        } else {
                                            String e = task.getException().getMessage();
                                            Toast.makeText(SetUsername.this, "Error : " + e, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                            }
                        }
                    });
                }
                 }
                });



            }


    //intents
    private void sendToMain() {

        Intent mainIntent = new Intent(SetUsername.this , MainMenu.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}