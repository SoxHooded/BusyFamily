package com.gmail.epsilon1011.busyfamily;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FamilyActivitiesAdapter extends RecyclerView.Adapter<FamilyActivitiesAdapter.ViewHolder> {

    public List<FamilyActivitiesItem> activityItem;
    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;

    public FamilyActivitiesAdapter(List<FamilyActivitiesItem> activityItem1){this.activityItem = activityItem1;}


    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //inflate view (list_item)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activities_item, parent, false);

        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        return new ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);

        final String url = activityItem.get(position).getImageUrlActivites();
        final String type = activityItem.get(position).getTypeActivites();
        final String color = activityItem.get(position).getColorActivites();
        final String user_id = activityItem.get(position).getUser_idActivities();
        final String serv_id = activityItem.get(position).getServer_idActivities();

        holder.LoadImage(url);
        holder.setTheImageColor(color);


        if(type!=null){
            if(!type.equals("Fake")){
                mStore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        String username = task.getResult().getString("Username");
                        holder.setTheUsernameText(username);

                    }
                });
            }
            else{
                holder.setTheUsernameText(user_id);
            }
        }

        holder.viewActivitiesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(type!=null){
                    if(!type.equals("Fake")) {

                        Intent viewActivities = new Intent(v.getContext(),FamilyViewActivities.class);
                        viewActivities.putExtra("id", user_id);
                        viewActivities.putExtra("type", type);
                        viewActivities.putExtra("serv_id",serv_id);
                        v.getContext().startActivity(viewActivities);

                    } else{

                        Intent viewActivities = new Intent(v.getContext(),FamilyViewActivities.class);
                        viewActivities.putExtra("id", user_id);
                        viewActivities.putExtra("type", type);
                        viewActivities.putExtra("serv_id",serv_id);
                        v.getContext().startActivity(viewActivities);

                    }
                }

            }
        });



    }

    @Override
    public int getItemCount() { //list size

        return activityItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView username;
        private CircleImageView image;
        private Button viewActivitiesbtn;


        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            image = mView.findViewById(R.id.activitiesImage);
            viewActivitiesbtn = mView.findViewById(R.id.viewActivitiesbtn);

        }

        public void setTheUsernameText(String usernameText) {
            username = mView.findViewById(R.id.activitiesUsername);
            username.setText(usernameText);
        }

        private void setTheImageColor(String getcolor) {
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
                else if (getcolor.equals("Yellow"))
                    image.setBorderColor(Color.parseColor(yellow));
                else if (getcolor.equals("Purple")) {
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


}
