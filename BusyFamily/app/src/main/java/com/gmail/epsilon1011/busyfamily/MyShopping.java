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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyShopping extends AppCompatActivity {

    private Toolbar MyShoppingToolbar;
    private Dialog addDialog;
    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;
    private RecyclerView ShoppingView;
    private List<MyShoppingItem> itemList;
    private MyShoppingAdapter myShoppingAdapter;
    private String user_id ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_shopping);

        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        user_id = mAuth.getCurrentUser().getUid();

        addDialog = new Dialog(this);

        MyShoppingToolbar = findViewById(R.id.myShoppingToolbar);
        setSupportActionBar(MyShoppingToolbar);
        getSupportActionBar().setTitle("My Shopping Cart");
        MyShoppingToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToMyCalendar();
            }
        });

        ShoppingView = findViewById(R.id.shoppingView);
        ShoppingView.setLayoutManager(new LinearLayoutManager(this));
        ShoppingView.setHasFixedSize(true);

        //model
        itemList = new ArrayList<>();
        //adapter
        myShoppingAdapter = new MyShoppingAdapter(itemList);
        //set adapter to recycler view
        ShoppingView.setAdapter(myShoppingAdapter);

        if(user_id!=null) {
            mStore.collection("Private_Calendar").document(user_id).collection("Shopping_cart").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if (documentSnapshots != null) {
                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                MyShoppingItem list = doc.getDocument().toObject(MyShoppingItem.class);
                                itemList.add(list);
                                myShoppingAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

            });
        }
    }

        public void addItem(View v){
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
                                                  Map<String, String> addMap = new HashMap<>();
                                                  addMap.put("Item", itemText.getText().toString());
                                                  addMap.put("Quantity", quantityText.getText().toString());


                                                  if (user_id != null) {
                                                      mStore.collection("Private_Calendar").document(user_id).collection("Shopping_cart").document(itemText.getText().toString()).set(addMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                          @Override
                                                          public void onComplete(@NonNull Task<Void> task) {
                                                              if (task.isSuccessful()) {

                                                                  addDialog.dismiss();
                                                              } else{
                                                                  cancelbtn.setEnabled(true);
                                                                  addbtn.setEnabled(true);
                                                              }

                                                          }
                                                      });


                                                  } else{
                                                      cancelbtn.setEnabled(true);
                                                      addbtn.setEnabled(true);
                                                  }
                                              } else{
                                                  cancelbtn.setEnabled(true);
                                                  addbtn.setEnabled(true);
                                              }
                                          }
                                      });

            cancelbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelbtn.setEnabled(false);
                    addbtn.setEnabled(false);
                    addDialog.dismiss();
                }
            });

            addDialog.show();
        }




    private void sendToMyCalendar(){
        Intent myCalendarIntent = new Intent(MyShopping.this,MyCalendar.class);
        startActivity(myCalendarIntent);
        finish();
    }

    @Override
    public void onBackPressed() {

        sendToMyCalendar();
    }

}
