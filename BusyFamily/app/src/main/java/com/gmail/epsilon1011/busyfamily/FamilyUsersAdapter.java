package com.gmail.epsilon1011.busyfamily;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FamilyUsersAdapter extends RecyclerView.Adapter<FamilyUsersAdapter.ViewHolder> {

    public List<FamilyUsersItem> userItem;
    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private String current_user;


    public FamilyUsersAdapter(List<FamilyUsersItem> userItem1) {
        this.userItem = userItem1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        //inflate view (list_item)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_item, parent, false);

        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mStorage= FirebaseStorage.getInstance();

        current_user = mAuth.getCurrentUser().getUid();

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);

        final String user_id = userItem.get(position).getUser_id();

        mStore.collection("Family_Calendar_Users").document(current_user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                final String checkType = task.getResult().getString("Type");
                final String type = userItem.get(position).getTheType();

                if(type.equals("Fake")) {

                    final String color = userItem.get(position).getTheColor();
                    final String user = userItem.get(position).getUser_id();
                    final String url = userItem.get(position).getTheImageUrl();

                    mStore.collection("Family_Calendar_Users").whereEqualTo("user_id", user).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                final String doc_id = document.getId();

                                holder.setUsernameText(user);
                                holder.setColorText(color);
                                holder.settheTextColor(color);
                                holder.setTypeText(type);
                                holder.setTheImageColor(color);
                                holder.LoadImage(url);

                                    holder.ChangeColorbtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            holder.Deletebtn.setEnabled(false);
                                            holder.ChangeColorbtn.setEnabled(false);
                                            holder.ChangeImagebtn.setEnabled(false);
                                            Intent sendToUsers = new Intent(v.getContext(), FamilyUsers.class);
                                            sendToUsers.putExtra("check", doc_id);
                                            v.getContext().startActivity(sendToUsers);
                                        }
                                    });
                                    holder.Deletebtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            holder.Deletebtn.setEnabled(false);
                                            holder.ChangeColorbtn.setEnabled(false);
                                            holder.ChangeImagebtn.setEnabled(false);
                                            if(deletingAlert(v, doc_id,type)){
                                                holder.Deletebtn.setEnabled(true);
                                                holder.ChangeColorbtn.setEnabled(true);
                                                holder.ChangeImagebtn.setEnabled(true);
                                            }
                                        }
                                    });

                                    holder.ChangeImagebtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            holder.Deletebtn.setEnabled(false);
                                            holder.ChangeColorbtn.setEnabled(false);
                                            holder.ChangeImagebtn.setEnabled(false);

                                            Intent sendToChangeImage = new Intent(v.getContext(), ChangeImage.class);
                                            sendToChangeImage.putExtra("id", user_id);
                                            v.getContext().startActivity(sendToChangeImage);

                                        }
                                    });
                            }
                        }

                    });

                }
                    else{

                    if(user_id!=null) {

            mStore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    String username = task.getResult().getString("Username");

                    holder.setUsernameText(username);

                    if (user_id.equals(current_user)) {
                        holder.Deletebtn.setText("Leave Calendar");
                    } else{
                        holder.ChangeImagebtn.setVisibility(View.GONE);
                    }

                    String color = userItem.get(position).getTheColor();
                    String url = userItem.get(position).getTheImageUrl();

                    holder.setColorText(color);
                    holder.settheTextColor(color);
                    holder.setTypeText(type);
                    holder.setTheImageColor(color);
                    holder.LoadImage(url);


                    if (checkType != null) {
                        if (checkType.equals("Owner")) {
                            holder.ChangeColorbtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    holder.Deletebtn.setEnabled(false);
                                    holder.ChangeColorbtn.setEnabled(false);
                                    holder.ChangeImagebtn.setEnabled(false);

                                    Intent sendToUsers = new Intent(v.getContext(), FamilyUsers.class);
                                    sendToUsers.putExtra("check", user_id);
                                    v.getContext().startActivity(sendToUsers);
                                }
                            });

                            holder.ChangeImagebtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    holder.Deletebtn.setEnabled(false);
                                    holder.ChangeColorbtn.setEnabled(false);
                                    holder.ChangeImagebtn.setEnabled(false);

                                    Intent sendToChangeImage = new Intent(v.getContext(), ChangeImage.class);
                                    sendToChangeImage.putExtra("id", user_id);
                                    v.getContext().startActivity(sendToChangeImage);

                                }
                            });
                            holder.Deletebtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    holder.Deletebtn.setEnabled(false);
                                    holder.ChangeColorbtn.setEnabled(false);
                                    holder.ChangeImagebtn.setEnabled(false);

                                    if (user_id.equals(current_user)) {
                                        if(leavingAlert(v)){
                                            holder.Deletebtn.setEnabled(true);
                                            holder.ChangeColorbtn.setEnabled(true);
                                            holder.ChangeImagebtn.setEnabled(true);
                                        }
                                    } else {
                                        if(deletingAlert(v, user_id,type)){
                                            holder.Deletebtn.setEnabled(true);
                                            holder.ChangeColorbtn.setEnabled(true);
                                            holder.ChangeImagebtn.setEnabled(true);
                                        }
                                    }
                                }
                            });

                        } else {
                                if(user_id.equals(current_user)) {

                                    holder.ChangeColorbtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            holder.Deletebtn.setEnabled(false);
                                            holder.ChangeColorbtn.setEnabled(false);
                                            holder.ChangeImagebtn.setEnabled(false);

                                            Intent sendToUsers = new Intent(v.getContext(), FamilyUsers.class);
                                            sendToUsers.putExtra("check", user_id);
                                            v.getContext().startActivity(sendToUsers);
                                        }
                                    });

                                    holder.ChangeImagebtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            holder.Deletebtn.setEnabled(false);
                                            holder.ChangeColorbtn.setEnabled(false);
                                            holder.ChangeImagebtn.setEnabled(false);

                                            Intent sendToChangeImage = new Intent(v.getContext(), ChangeImage.class);
                                            sendToChangeImage.putExtra("id", user_id);
                                            v.getContext().startActivity(sendToChangeImage);

                                        }
                                    });

                                    holder.Deletebtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            holder.Deletebtn.setEnabled(false);
                                            holder.ChangeColorbtn.setEnabled(false);
                                            holder.ChangeImagebtn.setEnabled(false);
                                            if(leavingAlert(v)){
                                                holder.Deletebtn.setEnabled(true);
                                                holder.ChangeColorbtn.setEnabled(true);
                                                holder.ChangeImagebtn.setEnabled(true);
                                            }
                                        }
                                    });
                                }
                         else {
                                    holder.Deletebtn.setVisibility(View.GONE);
                                    holder.ChangeColorbtn.setVisibility(View.GONE);
                                    holder.ChangeImagebtn.setVisibility(View.GONE);
                                }

                        }
                    }
                }

            });
        }

        }
            }
        });
    }


    @Override
    public int getItemCount() { //list size

        return userItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView username;
        private TextView color;
        private TextView type;
        private Button ChangeColorbtn;
        private Button Deletebtn;
        private Button ChangeImagebtn;
        private CircleImageView image;


        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            Deletebtn = mView.findViewById(R.id.deleteUserbtn);
            ChangeColorbtn = mView.findViewById(R.id.changeColorbtn);
            ChangeImagebtn = mView.findViewById(R.id.changeImagebtn);
            image = mView.findViewById(R.id.usersImage);
        }

        public void setUsernameText(String usernameText) {
            username = mView.findViewById(R.id.userUsername);
            username.setText("Name: "+usernameText);
        }

        public void setColorText(String colorText) {
            color = mView.findViewById(R.id.userColor);
            color.setText("Color: "+colorText);
        }

        public void setTypeText(String typeText) {
            type = mView.findViewById(R.id.userType);
            type.setText("Type: "+typeText);
        }


        public void settheTextColor(String getcolor) {
            String red = "#d54444";
            String blue = "#5171c2";
            String green = "#64d27e";
            String yellow = "#c9c166";
            String purple = "#ad53b9";
            if (getcolor != null) {
                if (getcolor.equals("Red")) {
                    color.setTextColor(Color.parseColor(red));
                } else if (getcolor.equals("Blue")) {
                    color.setTextColor(Color.parseColor(blue));
                } else if (getcolor.equals("Green"))
                    color.setTextColor(Color.parseColor(green));
                else if(getcolor.equals("Yellow"))
                    color.setTextColor(Color.parseColor(yellow));
                else if(getcolor.equals("Purple")) {
                    color.setTextColor(Color.parseColor(purple));
                }
            }
        }

        private void setTheImageColor(String getcolor){
            String red = "#d54444";
            String blue = "#5171c2";
            String green = "#64d27e";
            String yellow = "#c9c166";
            String purple = "#ad53b9";
            if (getcolor != null) {
                if (getcolor.equals("Red")) {
                    image.setBorderColor(Color.parseColor(red));
                } else if (getcolor.equals("Blue")) {
                    image.setBorderColor(Color.parseColor(blue));
                } else if (getcolor.equals("Green"))
                    image.setBorderColor(Color.parseColor(green));
                else if(getcolor.equals("Yellow"))
                    image.setBorderColor(Color.parseColor(yellow));
                else if(getcolor.equals("Purple")) {
                    image.setBorderColor(Color.parseColor(purple));
                }
            }

        }

        private void LoadImage(String url){

            if (url!=null) {
                if(url!="none") {
                    Picasso.get()
                            .load(url)
                            .placeholder(R.mipmap.imgdefault)
                            .fit()
                            .centerCrop()
                            .into(image);
                }
            }
        }
    }

    public boolean leavingAlert(final View v){

        AlertDialog.Builder dial = new AlertDialog.Builder(v.getContext());
        dial.setMessage("Are you sure you want to leave this Calendar?").setCancelable(false).
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent leaving = new Intent(v.getContext(),FamilyCalendar.class);
                        leaving.putExtra("Leaving", "true");
                        v.getContext().startActivity(leaving);
                    }
                }).
                setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = dial.create();
        alert.setTitle("Leaving Calendar");
        alert.show();

        return true;
    }

    public boolean deletingAlert(final View v ,final String id,final String type){

        AlertDialog.Builder dial = new AlertDialog.Builder(v.getContext());
        dial.setMessage("Are you sure you want to delete this user?").setCancelable(false).
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mStore.collection("Family_Calendar_Users").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                String url = task.getResult().getString("ImageUrl");

                                if(url!=null){
                                    if(!url.equals("none")){
                                        mStorage.getReferenceFromUrl(url).delete();
                                    }
                                }

                                mStore.collection("Family_Calendar_Users").document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(type.equals("Fake")) {
                                            mStore.collection("Activities").whereEqualTo("user_id",id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        final String doc_id = document.getId();

                                                        mStore.collection("Activities").document(doc_id).delete();

                                                    }
                                                }
                                            });
                                        }
                                    }
                                });


                            }
                        });

                        new CountDownTimer(2000, 1000) {

                            public void onTick(long millisUntilFinished) {

                            }

                            public void onFinish() {

                                Toast.makeText(v.getContext(), "User Deleted", Toast.LENGTH_LONG).show();
                                Intent ref = new Intent(v.getContext(),FamilyUsers.class);
                                v.getContext().startActivity(ref);

                            }
                        }.start();
                    }
                }).
                setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = dial.create();
        alert.setTitle("Deleting User");
        alert.show();

        return true;
    }

}
