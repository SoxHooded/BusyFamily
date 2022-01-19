package com.gmail.epsilon1011.busyfamily;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class FamilyInvite extends AppCompatActivity {

    private Toolbar FamilyInviteToolbar;
    private Button Invitebtn;
    private EditText InviteUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private String current_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_invite);

        Invitebtn = findViewById(R.id.invitebtn);
        InviteUser = findViewById(R.id.inviteUser);

        FamilyInviteToolbar = findViewById(R.id.familyInviteToolbar);
        setSupportActionBar(FamilyInviteToolbar);
        getSupportActionBar().setTitle("Invite To Your Calendar");
        FamilyInviteToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToFamilyCalendar();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        current_id = mAuth.getCurrentUser().getUid();



        Invitebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Invitebtn.setEnabled(false);

                final String username = InviteUser.getText().toString();

                if ((username.length() < 6 || username.length() > 16)) {
                    Toast.makeText(FamilyInvite.this,"Username must be between 6 and 16 characters!",Toast.LENGTH_LONG).show();
                    Invitebtn.setEnabled(true);
                }
                else {
                    mStore.collection("Users").whereEqualTo("Username",username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            boolean result = task.getResult().isEmpty();
                            if (result){
                                Toast.makeText(FamilyInvite.this,"User does not exist",Toast.LENGTH_LONG).show();
                                Invitebtn.setEnabled(true);
                            }
                            else{

                                mStore.collection("Users").whereEqualTo("Username",username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {

                                            final String doc_id = document.getId();
                                            final String res = document.getString("Server_id");

                                            mStore.collection("Family_Calendar_Users").document(current_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    String type = task.getResult().getString("Type");

                                                    if (!type.equals("Owner")){
                                                        Toast.makeText(FamilyInvite.this,"Only the owner of the Calendar can invite",Toast.LENGTH_LONG).show();
                                                        Invitebtn.setEnabled(true);
                                                    }
                                                  else if(doc_id.equals(current_id)) {
                                                        Toast.makeText(FamilyInvite.this,"That's you silly!!",Toast.LENGTH_LONG).show();
                                                        Invitebtn.setEnabled(true);
                                                    }
                                                    else if (!res.equals("none")){
                                                        Toast.makeText(FamilyInvite.this,"User is already a member of a Family calendar",Toast.LENGTH_LONG).show();
                                                        Invitebtn.setEnabled(true);
                                                    }
                                                    else {


                                                        Map<String, String> SendReqMap = new HashMap<>();
                                                        SendReqMap.put("req_type", "sent");
                                                        SendReqMap.put("to", doc_id);


                                                        mStore.collection("Family_Calendar_Invites").document(current_id).collection(current_id).document(doc_id).set(SendReqMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {


                                                                if (task.isSuccessful()) {
                                                                    Map<String, String> ReceiveReqMap = new HashMap<>();
                                                                    ReceiveReqMap.put("req_type", "received");
                                                                    ReceiveReqMap.put("from", mAuth.getCurrentUser().getUid());


                                                                    mStore.collection("Family_Calendar_Invites").document(doc_id).collection(doc_id).document(current_id).set(ReceiveReqMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            Toast.makeText(FamilyInvite.this, "Invitation sent!", Toast.LENGTH_LONG).show();
                                                                            Invitebtn.setEnabled(true);
                                                                        }
                                                                    });
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
                        }
                    });
                }
            }
        });

    }

    private void sendToFamilyCalendar(){
        Intent familyCalendarIntent = new Intent(FamilyInvite.this,FamilyCalendar.class);
        startActivity(familyCalendarIntent);
        finish();
    }

    @Override
    public void onBackPressed() {

        sendToFamilyCalendar();
    }
}
