package com.gmail.epsilon1011.busyfamily;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Invites extends AppCompatActivity {

    private Toolbar InvitesToolbar;
    private RecyclerView InvitesView;
    private List<InvitesItem> InvitesList;
    private InvitesAdapter invitesAdapter;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private String current_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invites);


        InvitesToolbar = findViewById(R.id.invitesToolbar);
        setSupportActionBar(InvitesToolbar);
        getSupportActionBar().setTitle("Invites");
        InvitesToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToMain();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        current_id = mAuth.getCurrentUser().getUid();

        InvitesView=(RecyclerView) findViewById(R.id.invitesView);
        InvitesView.setHasFixedSize(true);
        InvitesView.setLayoutManager(new LinearLayoutManager(this));

        //model
        InvitesList = new ArrayList<>();
        //adapter
        invitesAdapter = new InvitesAdapter(InvitesList);
        //set adapter to recycler view
        InvitesView.setAdapter(invitesAdapter);


        //get logged-in user's friend requests
        mStore.collection("Family_Calendar_Invites").document(current_id).collection(current_id)
                .whereEqualTo("req_type" , "received").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (documentSnapshots != null) {
                    for ( DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            //add results to list and notify adapter
                            InvitesItem res = doc.getDocument().toObject(InvitesItem.class);
                            InvitesList.add(res);
                            invitesAdapter.notifyDataSetChanged();



                        }
                    }
                }
                if(InvitesList.isEmpty()){
                    Toast.makeText(Invites.this,"No invitations found!",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void sendToMain(){
        Intent mainIntent = new Intent(Invites.this,MainMenu.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void onBackPressed() {

        sendToMain();
    }
}
