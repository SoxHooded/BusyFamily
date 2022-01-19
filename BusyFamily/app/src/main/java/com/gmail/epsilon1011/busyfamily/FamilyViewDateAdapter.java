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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.List;

public class FamilyViewDateAdapter extends RecyclerView.Adapter<FamilyViewDateAdapter.ViewHolder> {

    public List<FamilyViewActivitiesItem> activitiesItem;
    private FirebaseFirestore mStore;


    public FamilyViewDateAdapter(List<FamilyViewActivitiesItem> item1) {
        this.activitiesItem = item1;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        //inflate view (list_item)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activities_show_item_id, parent, false);

        mStore = FirebaseFirestore.getInstance();

        Collections.sort(activitiesItem);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        final String desc = activitiesItem.get(position).getTheDescActivities();
        holder.setDesc(desc);
        final String date = activitiesItem.get(position).getTheDateActivities();
        holder.setTheDate(date);
        final String time = activitiesItem.get(position).getTheTimeActivities();
        holder.setTheTime(time);
        final String dateTime = date+"/"+time;
        final String user_id = activitiesItem.get(position).getTheUser_idActivities();

        mStore.collection("Family_Calendar_Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                String type = task.getResult().getString("Type");
                final String color =task.getResult().getString("Color");
                String id = task.getResult().getString("user_id");

                if (type.equals("Guest")){
                    holder.deletebtn.setVisibility(View.GONE);
                }

                if(type.equals("Fake")){
                    holder.setTheUsername(id);
                    holder.settheTextColor(color);
                }else{
                    mStore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            String name = task.getResult().getString("Username");

                            holder.setTheUsername(name);
                            holder.settheTextColor(color);

                        }
                    });
                }

            }
        });

        holder.deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                holder.deletebtn.setEnabled(false);

                mStore.collection("Activities").whereEqualTo("user_id",user_id).whereEqualTo("desc",desc).whereEqualTo("DateTime",dateTime).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            final String doc_id = document.getId();

                            mStore.collection("Activities").document(doc_id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(v.getContext(),"Activity Deleted!!",Toast.LENGTH_LONG).show();

                                    NotificationHandler.cancelReminder(user_id+desc+dateTime);

                                    holder.deletebtn.setEnabled(true);

                                    Intent refresh = new Intent(v.getContext(), FamilyViewDate.class);
                                    refresh.putExtra("date",date);
                                    v.getContext().startActivity(refresh);

                                }
                            });
                        }

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() { //list size

        return activitiesItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private Button deletebtn;
        private TextView desc;
        private TextView date;
        private TextView time;
        private TextView name;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            deletebtn= mView.findViewById(R.id.delete);

        }

        public void setDesc(String desc1) {
            desc = mView.findViewById(R.id.desc);
            desc.setText(desc1);
        }
        public void setTheDate(String date1){
            date = mView.findViewById(R.id.date);
            date.setText("Date: "+date1);
        }

        public void setTheTime(String time1){
            time = mView.findViewById(R.id.time);
            time.setText("Time: "+time1);
        }

        public void setTheUsername(String username1){
            name = mView.findViewById(R.id.user);
            name.setText("User : "+username1);
        }

        public void settheTextColor(String getcolor) {
            String red = "#d54444";
            String blue = "#5171c2";
            String green = "#64d27e";
            String yellow = "#c9c166";
            String purple = "#ad53b9";
            if (getcolor != null) {
                if (getcolor.equals("Red")) {
                    name.setTextColor(Color.parseColor(red));
                } else if (getcolor.equals("Blue")) {
                    name.setTextColor(Color.parseColor(blue));
                } else if (getcolor.equals("Green"))
                    name.setTextColor(Color.parseColor(green));
                else if(getcolor.equals("Yellow"))
                    name.setTextColor(Color.parseColor(yellow));
                else if(getcolor.equals("Purple")) {
                    name.setTextColor(Color.parseColor(purple));
                }
            }
        }

    }
}