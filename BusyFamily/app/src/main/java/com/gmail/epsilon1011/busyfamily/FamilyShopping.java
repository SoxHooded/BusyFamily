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
import android.widget.EditText;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FamilyShopping extends AppCompatActivity {

    private Toolbar FamilyShoppingToolbar;
    private Dialog addDialog;
    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;
    private String user_id;
    private RecyclerView ShoppingView;
    private List<FamilyShoppingItem> itemList;
    private FamilyShoppingAdapter familyShoppingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_shopping);

        FamilyShoppingToolbar = findViewById(R.id.familyShoppingToolbar);
        setSupportActionBar(FamilyShoppingToolbar);
        getSupportActionBar().setTitle("Family Shopping Cart");
        FamilyShoppingToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToFamilyCalendar();
            }
        });

        addDialog = new Dialog(this);

        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        user_id = mAuth.getCurrentUser().getUid();

        ShoppingView = findViewById(R.id.familyShoppingView);
        ShoppingView.setLayoutManager(new LinearLayoutManager(this));
        ShoppingView.setHasFixedSize(true);

        //model
        itemList = new ArrayList<>();
        //adapter
        familyShoppingAdapter = new FamilyShoppingAdapter(itemList);
        //set adapter to recycler view
        ShoppingView.setAdapter(familyShoppingAdapter);


        mStore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String server_id = task.getResult().getString("Server_id");

                mStore.collection("Family_Calendar_Shopping").whereEqualTo("Server_id", server_id).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (documentSnapshots != null) {
                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                    FamilyShoppingItem list = doc.getDocument().toObject(FamilyShoppingItem.class);
                                    itemList.add(list);
                                    familyShoppingAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }

                });

            }
        });

    }


    public void addFamilyItem(View v){
        final Button cancelbtn;
        final Button addbtn ;
        final EditText itemText;
        final EditText quantityText;

        addDialog.setContentView(R.layout.shopping_add_item);
        addDialog.setCancelable(false);

        cancelbtn = addDialog.findViewById(R.id.cancelbtn);
        addbtn = addDialog.findViewById(R.id.donebtn);
        itemText = addDialog.findViewById(R.id.itemText);
        quantityText= addDialog.findViewById(R.id.quantityText);

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelbtn.setEnabled(false);
                addbtn.setEnabled(false);

                if (!itemText.getText().toString().isEmpty() && !quantityText.getText().toString().isEmpty()) {

                    mStore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                String server_id = task.getResult().getString("Server_id");
                                insert(itemText.getText().toString(),quantityText.getText().toString(),server_id);

                        }

                        });
                    } else{
                    cancelbtn.setEnabled(true);
                    addbtn.setEnabled(true);
                }
                }
            });

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelbtn.setEnabled(true);
                addbtn.setEnabled(true);
                addDialog.dismiss();
            }
        });

        addDialog.show();
    }



    private void insert(String item , String quantity, String serv_id){

        Map<String, String> addMap2 = new HashMap<>();
        addMap2.put("Item", item);
        addMap2.put("Quantity", quantity);
        addMap2.put("Server_id", serv_id);

        String item_id = item + serv_id;

        mStore.collection("Family_Calendar_Shopping").document(item_id).set(addMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                addDialog.dismiss();
            }
        });
    }


    private void sendToFamilyCalendar(){
        Intent familyCalendarIntent = new Intent(FamilyShopping.this,FamilyCalendar.class);
        startActivity(familyCalendarIntent);
        finish();
    }

    @Override
    public void onBackPressed() {

        sendToFamilyCalendar();
    }


}