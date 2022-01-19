package com.gmail.epsilon1011.busyfamily;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class InvitesAdapter extends RecyclerView.Adapter<InvitesAdapter.ViewHolder>{

    public List<InvitesItem> invitesList;
    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;

    public InvitesAdapter(List<InvitesItem> invitesList){
        this.invitesList = invitesList;
    }

    @Override
    public InvitesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //inflate view (request_list)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invites_item, parent, false);


        mStore = FirebaseFirestore.getInstance();   //Firestore
        mAuth= FirebaseAuth.getInstance();  //Firebase Auth

        return new ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        final String currentid = mAuth.getCurrentUser().getUid();
        final String id = invitesList.get(position).getthisUser_id();

        if (id != null) {

            //get user from db
            mStore.collection("Users").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    String username = task.getResult().getString("Username");

                    holder.setInvUsername(username);

                    holder.declinebtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            holder.acceptbtn.setEnabled(false);
                            holder.declinebtn.setEnabled(false);

                            mStore.collection("Family_Calendar_Invites").document(currentid).collection(currentid).document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mStore.collection("Family_Calendar_Invites").document(id).collection(id).document(currentid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Intent refresh = new Intent(v.getContext(),Invites.class);
                                                v.getContext().startActivity(refresh);
                                            }
                                        });
                                    }
                                }

                            });
                        }
                    });

                    holder.acceptbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            holder.acceptbtn.setEnabled(false);
                            holder.declinebtn.setEnabled(false);

                            mStore.collection("Users").document(currentid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    String serv_id = task.getResult().getString("Server_id");

                                    if(!serv_id.equals("none")){
                                        holder.acceptbtn.setEnabled(true);
                                        holder.declinebtn.setEnabled(true);

                                        Toast.makeText(v.getContext(),"You are already a member of a Family Calendar",Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        mStore.collection("Users").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                String server_id = task.getResult().getString("Server_id");

                                                if (server_id.equals("none")){
                                                    mStore.collection("Family_Calendar_Invites").document(currentid).collection(currentid).document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                mStore.collection("Family_Calendar_Invites").document(id).collection(id).document(currentid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        Toast.makeText(v.getContext(),"This Calendar no longer exists!!",Toast.LENGTH_LONG).show();
                                                                        holder.acceptbtn.setEnabled(true);
                                                                        holder.declinebtn.setEnabled(true);
                                                                    }
                                                                });
                                                            }
                                                        }

                                                    });


                                            }
                                            else{
                                                    mStore.collection("Users").document(currentid).update("Server_id", server_id).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            mStore.collection("Family_Calendar_Invites").document(currentid).collection(currentid).document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        mStore.collection("Family_Calendar_Invites").document(id).collection(id).document(currentid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                Intent calendarIntent = new Intent(v.getContext(), FamilyCalendar.class);
                                                                                v.getContext().startActivity(calendarIntent);
                                                                            }
                                                                        });
                                                                    }
                                                                }

                                                            });


                                                        }
                                                    });
                                                }
                                            }
                                        });

                                    }
                                }
                            });
                        }
                    });


                }
            });

        }
    }






    @Override
    public int getItemCount() {  //list size

        return invitesList.size();
    }

    //ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;

        private TextView mUsername;
        private Button acceptbtn;
        private Button declinebtn;


        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            acceptbtn = itemView.findViewById(R.id.acceptbtn);
            declinebtn = itemView.findViewById(R.id.declinebtn);
        }

        //Change username EditText
        public void setInvUsername(String name) {
            mUsername = mView.findViewById(R.id.inviteUsername);
            mUsername.setText(name);

        }

    }

}
