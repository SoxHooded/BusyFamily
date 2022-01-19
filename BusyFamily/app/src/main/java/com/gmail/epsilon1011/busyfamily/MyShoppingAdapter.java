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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MyShoppingAdapter extends RecyclerView.Adapter<MyShoppingAdapter.ViewHolder>{

    public List<MyShoppingItem> myShoppingItem;
    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;



    public MyShoppingAdapter(List<MyShoppingItem> myShoppingItem){
        this.myShoppingItem = myShoppingItem;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)  {


        //inflate view (list_item)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_item, parent, false);

        mStore= FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();



        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);




        final String item_name = myShoppingItem.get(position).getTheItem();
        holder.setItemText(item_name);
        String item_quantity = myShoppingItem.get(position).getTheQuantity();
        holder.setItemQuantity(item_quantity);

        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mStore.collection("Private_Calendar").document(mAuth.getCurrentUser().getUid()).collection("Shopping_cart").document(item_name).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        holder.btn.setEnabled(false);

                        Intent refresh = new Intent(v.getContext(),MyShopping.class);
                        v.getContext().startActivity(refresh);

                    }
                });

            }
        });

    }



    @Override
    public int getItemCount() { //list size

        return myShoppingItem.size();
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
