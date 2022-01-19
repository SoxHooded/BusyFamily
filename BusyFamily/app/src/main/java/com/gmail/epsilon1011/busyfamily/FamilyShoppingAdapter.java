package com.gmail.epsilon1011.busyfamily;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FamilyShoppingAdapter extends RecyclerView.Adapter<FamilyShoppingAdapter.ViewHolder>{

    public List<FamilyShoppingItem> shoppingItem;
    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;
    private String user_id;


    public FamilyShoppingAdapter(List<FamilyShoppingItem> shoppingItem){
        this.shoppingItem = shoppingItem;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)  {


        //inflate view (list_item)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_item, parent, false);

        mStore= FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();


        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);

        final String item_name = shoppingItem.get(position).getTheItem();
        holder.setItemText(item_name);
        String item_quantity = shoppingItem.get(position).getTheQuantity();
        holder.setItemQuantity(item_quantity);



        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                holder.btn.setEnabled(false);

                mStore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        String server_id = task.getResult().getString("Server_id");

                        String item_id = item_name+server_id;

                        mStore.collection("Family_Calendar_Shopping").document(item_id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Intent refresh = new Intent(v.getContext(), FamilyShopping.class);
                                v.getContext().startActivity(refresh);

                            }
                        });

                    }
                });
            }
        });

    }



    @Override
    public int getItemCount() { //list size

        return shoppingItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private View mView;

        private TextView item;
        private TextView itemQuantity;
        private ImageButton btn;


        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            btn = itemView.findViewById(R.id.deletebtn);

        }

        public void setItemText(String itemText){
            item = mView.findViewById(R.id.item);
            item.setText(itemText);
        }

        public void setItemQuantity(String quantity){
            itemQuantity= mView.findViewById(R.id.itemQuantity);
            itemQuantity.setText(quantity);
        }



    }

}
