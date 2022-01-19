package com.gmail.epsilon1011.busyfamily;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class CreateUser extends AppCompatActivity {

    private EditText textNickname;
    private Button Createbtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private Toolbar CreateToolbar;
    private String user_id;
    private String color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        CreateToolbar = findViewById(R.id.createToolbar);
        setSupportActionBar(CreateToolbar);
        getSupportActionBar().setTitle("Create User");
        CreateToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToMain();
            }
        });

        textNickname = findViewById(R.id.createNickname);
        Createbtn = findViewById(R.id.createbtn);


        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        user_id = mAuth.getCurrentUser().getUid();

        Createbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Createbtn.setEnabled(false);

                if(color==null){
                    Toast.makeText(CreateUser.this, "Select a color", Toast.LENGTH_LONG).show();
                    Createbtn.setEnabled(true);
                } else if(textNickname.getText().toString().isEmpty()){
                    Toast.makeText(CreateUser.this, "Choose a Nickname", Toast.LENGTH_LONG).show();
                    Createbtn.setEnabled(true);
                } else{


                    mStore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            final String id = task.getResult().getString("Server_id");

                            mStore.collection("Family_Calendar_Users").whereEqualTo("Server_id",id).whereEqualTo("user_id",textNickname.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                    if (!task.getResult().getDocuments().isEmpty()) {

                                        Toast.makeText(CreateUser.this, "There is already a user with this Nickname! Please choose a different one.", Toast.LENGTH_LONG).show();
                                        Createbtn.setEnabled(true);

                                    }else{

                                        Map<String, String> userMap = new HashMap<>();

                                        userMap.put("Server_id", id);
                                        userMap.put("user_id", textNickname.getText().toString());
                                        userMap.put("Color" , color);
                                        userMap.put("Type","Fake");
                                        userMap.put("ImageUrl", "none");

                                        mStore.collection("Family_Calendar_Users").add(userMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {

                                                Toast.makeText(CreateUser.this, "User Created", Toast.LENGTH_LONG).show();
                                                sendToMain();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                }

            }
        });

    }

    public void createColorRadio(View v){
        RadioGroup group = findViewById(R.id.createradiogroup);
        RadioButton btn;
        int radioButtonid = group.getCheckedRadioButtonId();
        btn = findViewById(radioButtonid);

        color = btn.getText().toString();
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(CreateUser.this , FamilyCalendar.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void onBackPressed() {

        sendToMain();
    }
}
