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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FamilyActivities extends AppCompatActivity {

    private Toolbar FamilyActivitiesToolbar;
    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;
    private String user_id;
    private RecyclerView ActivitiesView;
    private List<FamilyActivitiesItem> activityList;
    private FamilyActivitiesAdapter familyActivitiesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_activities);

        FamilyActivitiesToolbar = findViewById(R.id.familyActivitiesToolbar);
        setSupportActionBar(FamilyActivitiesToolbar);
        getSupportActionBar().setTitle("Family Activities");
        FamilyActivitiesToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToFamilyCalendar();
            }
        });

        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        user_id = mAuth.getCurrentUser().getUid();

        ActivitiesView = findViewById(R.id.activitiesView);
        ActivitiesView.setLayoutManager(new LinearLayoutManager(this));
        ActivitiesView.setHasFixedSize(true);

        activityList = new ArrayList<>();
        familyActivitiesAdapter = new FamilyActivitiesAdapter(activityList);
        ActivitiesView.setAdapter(familyActivitiesAdapter);


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

                                    FamilyActivitiesItem list = doc.getDocument().toObject(FamilyActivitiesItem.class);
                                    activityList.add(list);
                                    familyActivitiesAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }

                });
            }
        });

    }


    private void sendToFamilyCalendar(){
        Intent familyCalendarIntent = new Intent(FamilyActivities.this,FamilyCalendar.class);
        startActivity(familyCalendarIntent);
        finish();
    }

    @Override
    public void onBackPressed() {

        sendToFamilyCalendar();
    }
}



