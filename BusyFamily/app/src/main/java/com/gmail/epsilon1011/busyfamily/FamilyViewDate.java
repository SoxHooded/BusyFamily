package com.gmail.epsilon1011.busyfamily;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FamilyViewDate extends AppCompatActivity {

    private String Date;
    private Toolbar viewDateToolbar;
    private RecyclerView DateView;
    private List<FamilyViewActivitiesItem> activitiesList;
    private FamilyViewDateAdapter viewDateAdapter;
    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_view_date);

        Date = getIntent().getStringExtra("date");

        viewDateToolbar = findViewById(R.id.ViewDateToolbar);
        setSupportActionBar(viewDateToolbar);

        if(Date!=null) {
            getSupportActionBar().setTitle(Date);
        }

        viewDateToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToFamilyCalendar();
            }
        });

        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        user_id = mAuth.getCurrentUser().getUid();

        DateView = findViewById(R.id.fDateView);
        DateView.setLayoutManager(new LinearLayoutManager(this));
        DateView.setHasFixedSize(true);

        //model
        activitiesList = new ArrayList<>();
        //adapter
        viewDateAdapter = new FamilyViewDateAdapter(activitiesList);
        //set adapter to recycler view
        DateView.setAdapter(viewDateAdapter);



        mStore.collection("Family_Calendar_Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                String serv_id = task.getResult().getString("Server_id");

                mStore.collection("Family_Calendar_Users").whereEqualTo("Server_id",serv_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (QueryDocumentSnapshot document : task.getResult()) {

                            final String doc_id = document.getId();

                            mStore.collection("Activities").whereEqualTo("user_id",doc_id).whereEqualTo("Date",Date).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(QuerySnapshot documentSnapshots,FirebaseFirestoreException e) {
                                    if (documentSnapshots != null) {

                                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                                FamilyViewActivitiesItem list = doc.getDocument().toObject(FamilyViewActivitiesItem.class);
                                                activitiesList.add(list);
                                                viewDateAdapter.notifyDataSetChanged();

                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void sendToFamilyCalendar(){
        Intent sendToCalendar = new Intent(FamilyViewDate.this,FamilyCalendar.class);
        startActivity(sendToCalendar);
        finish();
    }

    @Override
    public void onBackPressed() {

        sendToFamilyCalendar();
    }
}

