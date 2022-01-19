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

public class ViewDate extends AppCompatActivity {

    private String Date;
    private Toolbar viewDateToolbar;
    private RecyclerView DateView;
    private List<MyActivitiesItem> activitiesList;
    private ViewDateAdapter viewDateAdapter;
    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_date);

        Date = getIntent().getStringExtra("date");

        viewDateToolbar = findViewById(R.id.viewDateToolbar);
        setSupportActionBar(viewDateToolbar);
        if(Date!=null) {
            getSupportActionBar().setTitle(Date);
        }
        viewDateToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToMyCalendar();
            }
        });

        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        user_id = mAuth.getCurrentUser().getUid();

        DateView = findViewById(R.id.viewDateView);
        DateView.setLayoutManager(new LinearLayoutManager(this));
        DateView.setHasFixedSize(true);

        //model
        activitiesList = new ArrayList<>();
        //adapter
        viewDateAdapter = new ViewDateAdapter(activitiesList);
        //set adapter to recycler view
        DateView.setAdapter(viewDateAdapter);

        mStore.collection("Activities").whereEqualTo("user_id",user_id).whereEqualTo("Date",Date).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent( QuerySnapshot documentSnapshots,  FirebaseFirestoreException e) {

                if (documentSnapshots != null) {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            MyActivitiesItem list = doc.getDocument().toObject(MyActivitiesItem.class);
                            activitiesList.add(list);
                            viewDateAdapter.notifyDataSetChanged();

                        }
                    }
                }

                if(activitiesList.isEmpty()){
                        Toast.makeText(ViewDate.this,"No activities found!",Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void sendToMyCalendar(){
        Intent sendToCalendar = new Intent(ViewDate.this,MyCalendar.class);
        startActivity(sendToCalendar);
        finish();
    }

    @Override
    public void onBackPressed() {

        sendToMyCalendar();
    }
}
