package com.gmail.epsilon1011.busyfamily;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FamilyChat extends AppCompatActivity {

    private Toolbar FamilyChatToolbar;
    private EditText ChatMessage;
    private ImageButton Sendbtn;
    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;
    private String user_id;
    private RecyclerView ChatView;
    private List<FamilyChatItem> chatList;
    private FamilyChatAdapter familyChatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_chat);

        FamilyChatToolbar = findViewById(R.id.familyChatToolbar);
        setSupportActionBar(FamilyChatToolbar);
        getSupportActionBar().setTitle("Family Chat");
        FamilyChatToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToFamilyCalendar();
            }
        });

        ChatMessage = findViewById(R.id.chatMessage);
        Sendbtn = findViewById(R.id.sendbtn);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

        user_id = mAuth.getCurrentUser().getUid();


        ChatView = findViewById(R.id.chatView);
        ChatView.setLayoutManager(new LinearLayoutManager(this));
        ChatView.setHasFixedSize(true);

        //model
        chatList = new ArrayList<>();
        //adapter
        familyChatAdapter = new FamilyChatAdapter(chatList);
        //set adapter to recycler view
        ChatView.setAdapter(familyChatAdapter);

        ChatView.scrollToPosition(chatList.size()-1);



        mStore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String server_id = task.getResult().getString("Server_id");

                mStore.collection("Family_Calendar_Chat").whereEqualTo("Server_id", server_id).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (documentSnapshots != null) {
                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                    FamilyChatItem list = doc.getDocument().toObject(FamilyChatItem.class);
                                    chatList.add(list);
                                    familyChatAdapter.notifyDataSetChanged();

                                    ChatView.scrollToPosition(chatList.size()-1);


                                }
                            }
                        }
                    }

                });

            }
        });


        Sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sendbtn.setEnabled(false);

                final String message = ChatMessage.getText().toString();

                if(!message.isEmpty()){

                    ChatMessage.setText("");

                    mStore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                           String serv_id = task.getResult().getString("Server_id");


                    SimpleDateFormat showformat = new SimpleDateFormat("dd-MM-yyyy'/'HH:mm");
                    String postime = showformat.format(new Date());

                    SimpleDateFormat dbformat = new SimpleDateFormat("yyyy-dd-MM'/'HH:mm:ss");
                    String timestamp = dbformat.format(new Date());

                    Map<String , Object> chatMap = new HashMap<>();
                    chatMap.put("Message" , message);
                    chatMap.put("Sender" , user_id);
                    chatMap.put("PostTime" , postime);
                    chatMap.put("Timestamp" , timestamp);
                    chatMap.put("Server_id",serv_id);


                    mStore.collection("Family_Calendar_Chat").add(chatMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {

                            Sendbtn.setEnabled(true);
                            ChatView.scrollToPosition(chatList.size()-1);
                        }
                    });

                        }
                    });
                }else{
                    Sendbtn.setEnabled(true);
                }
            }
        });

    }

    private void sendToFamilyCalendar(){
        Intent familyCalendarIntent = new Intent(FamilyChat.this,FamilyCalendar.class);
        startActivity(familyCalendarIntent);
        finish();
    }

    @Override
    public void onBackPressed() {

        sendToFamilyCalendar();
    }
}

