package com.gmail.epsilon1011.busyfamily;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainMenu extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private Button MyCalendarbtn;
    private Button FamilyCalendarbtn;
    private Button Invitesbtn;
    private Button Logoutbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyCalendarbtn = findViewById(R.id.myCalendarbtn);
        FamilyCalendarbtn = findViewById(R.id.familyCalendarbtn);
        Invitesbtn = findViewById(R.id.invitesbtn);
        Logoutbtn = findViewById(R.id.logoutbtn);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

        MyCalendarbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyCalendarbtn.setEnabled(false);
                FamilyCalendarbtn.setEnabled(false);
                Invitesbtn.setEnabled(false);
                Logoutbtn.setEnabled(false);

                sendToMyCalendar();
            }
        });


        FamilyCalendarbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyCalendarbtn.setEnabled(false);
                FamilyCalendarbtn.setEnabled(false);
                Invitesbtn.setEnabled(false);
                Logoutbtn.setEnabled(false);

                sendToFamilyCalendar();
            }
        });


        Invitesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyCalendarbtn.setEnabled(false);
                FamilyCalendarbtn.setEnabled(false);
                Invitesbtn.setEnabled(false);
                Logoutbtn.setEnabled(false);

                sendToInvites();
            }
        });


        Logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyCalendarbtn.setEnabled(false);
                FamilyCalendarbtn.setEnabled(false);
                Invitesbtn.setEnabled(false);
                Logoutbtn.setEnabled(false);

                Logout();
            }
        });

    }


    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {  //if user is not logged in send to login

            sendToLogin();

        }
        else {  //if user is logged in but doesnt have a username send to account
            final String userid = mAuth.getCurrentUser().getUid();

            mStore.collection("Users").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    if (!document.exists()) {
                        sendToSetUsername();
                    }else {
                        checkActivities();
                    }
                }
            });
        }
    }

    private void Logout(){
        mAuth.signOut();
        sendToLogin();
    }


    private void sendToLogin() {
        Intent loginIntent = new Intent(MainMenu.this , Login.class);
        startActivity(loginIntent);
        finish();
    }

    private void sendToSetUsername() {
        Intent usernameIntent = new Intent(MainMenu.this , SetUsername.class);
        startActivity(usernameIntent);
        finish();
    }

    private void sendToMyCalendar(){
        Intent myCalendarIntent = new Intent(MainMenu.this , MyCalendar.class);
        startActivity(myCalendarIntent);
        finish();
    }

    private void sendToFamilyCalendar(){
        Intent familyCalendarIntent = new Intent(MainMenu.this , FamilyCalendar.class);
        startActivity(familyCalendarIntent);
        finish();
    }

    private void sendToInvites(){
        Intent invitesIntent = new Intent(MainMenu.this ,Invites.class);
        startActivity(invitesIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void checkActivities(){

        mStore.collection("Activities").whereEqualTo("user_id",mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {

                    final String doc_id = document.getId();


                    mStore.collection("Activities").document(doc_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            String date = task.getResult().getString("DateTime");

                            Date thisDate = null;
                            Date currentDate = new Date();

                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy/HH:mm");

                            try {
                                thisDate = dateFormat.parse(date);//catch exception
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            if(thisDate.before(currentDate)){
                                mStore.collection("Activities").document(doc_id).delete();
                            }
                        }
                    });
                }
            }
        });
    }


}
