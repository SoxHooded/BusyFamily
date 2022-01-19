package com.gmail.epsilon1011.busyfamily;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class FamilyUsers extends AppCompatActivity {

    private Toolbar FamilyUsersToolbar;
    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private String user_id ;
    private RecyclerView UsersView;
    private List<FamilyUsersItem> userList;
    private FamilyUsersAdapter usersAdapter;
    private Dialog colorDialog;
    private String color;
    private Button invitebtn;
    private Button createbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_users);

        FamilyUsersToolbar = findViewById(R.id.familyUsersToolbar);
        setSupportActionBar(FamilyUsersToolbar);
        getSupportActionBar().setTitle("Family Users");
        FamilyUsersToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToFamilyCalendar();
            }
        });

        invitebtn = findViewById(R.id.inviteuserbtn);
        createbtn = findViewById(R.id.createuserbtn);

        invitebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invitebtn.setEnabled(false);
                createbtn.setEnabled(false);
                sendToInvite();
            }
        });
        createbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invitebtn.setEnabled(false);
                createbtn.setEnabled(false);
                sendToCreate();
            }
        });


        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();

        user_id = mAuth.getCurrentUser().getUid();

        colorDialog = new Dialog(this);

        UsersView = findViewById(R.id.usersView);
        UsersView.setLayoutManager(new LinearLayoutManager(this));
        UsersView.setHasFixedSize(true);

        userList = new ArrayList<>();
        usersAdapter = new FamilyUsersAdapter(userList);
        UsersView.setAdapter(usersAdapter);

        if(user_id!=null) {

            mStore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    String serv_id = task.getResult().getString("Server_id");


                    mStore.collection("Family_Calendar_Users").whereEqualTo("Server_id",serv_id).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                            if (documentSnapshots != null) {
                                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                    if (doc.getType() == DocumentChange.Type.ADDED) {

                                        FamilyUsersItem list = doc.getDocument().toObject(FamilyUsersItem.class);
                                        userList.add(list);
                                        usersAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }

                    });
                }});

                }

    }

    public void onStart(){
        super.onStart();

        String check = getIntent().getStringExtra("check");

        if(check!=null){
            if(!check.isEmpty()) {
                changeColorPopup(check);
            }
        }
    }


    public void changeColorPopup(final String id){
        final Button cancelbtn;
        final Button nextbtn ;
        colorDialog.setContentView(R.layout.change_color_popup);
        colorDialog.setCancelable(false);

        cancelbtn = colorDialog.findViewById(R.id.changeCancelbtn);
        nextbtn = colorDialog.findViewById(R.id.changeDonebtn);

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextbtn.setEnabled(false);
                cancelbtn.setEnabled(false);
                colorDialog.dismiss();
            }
        });

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                nextbtn.setEnabled(false);
                cancelbtn.setEnabled(false);

                if (color!=null) {

                    mStore.collection("Family_Calendar_Users").document(id).update("Color",color).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(FamilyUsers.this, "Color Changed", Toast.LENGTH_LONG).show();

                            Intent ref = new Intent(FamilyUsers.this,FamilyUsers.class);
                            ref.putExtra("check","");
                            startActivity(ref);
                        }
                    });
                }

                else{
                    nextbtn.setEnabled(true);
                    cancelbtn.setEnabled(true);
                    Toast.makeText(FamilyUsers.this, "Select a color", Toast.LENGTH_LONG).show();
                }
            }
        });

        colorDialog.show();
    }

    public void changeColorRadio(View v){
        RadioGroup group = colorDialog.findViewById(R.id.changeradiogroup);
        RadioButton btn;
        int radioButtonid = group.getCheckedRadioButtonId();
        btn = colorDialog.findViewById(radioButtonid);
        color = btn.getText().toString();
    }

    private void sendToFamilyCalendar(){
        Intent familyCalendarIntent = new Intent(FamilyUsers.this,FamilyCalendar.class);
        startActivity(familyCalendarIntent);
        finish();
    }

    private void sendToInvite(){
        Intent inviteIntent = new Intent(FamilyUsers.this,FamilyInvite.class);
        startActivity(inviteIntent);
        finish();
    }

    private void sendToCreate(){
        Intent createIntent = new Intent(FamilyUsers.this,CreateUser.class);
        startActivity(createIntent);
        finish();
    }

    @Override
    public void onBackPressed() {

        sendToFamilyCalendar();
    }

}
