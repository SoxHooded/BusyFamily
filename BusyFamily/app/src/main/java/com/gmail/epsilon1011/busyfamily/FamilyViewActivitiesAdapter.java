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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.List;

public class FamilyViewActivitiesAdapter extends RecyclerView.Adapter<FamilyViewActivitiesAdapter.ViewHolder> {

    public List<FamilyViewActivitiesItem> activitiesItem;
    private FirebaseFirestore mStore;


    public  FamilyViewActivitiesAdapter(List<FamilyViewActivitiesItem> item1){
        this.activitiesItem = item1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        //inflate view (list_item)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activities_show_item, parent, false);

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

                if (type.equals("Guest")){
                    holder.deletebtn.setVisibility(View.GONE);
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

                                    mStore.collection("Family_Calendar_Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                            String id = task.getResult().getString("user_id");
                                            String serv_id = task.getResult().getString("Server_id");
                                            String type = task.getResult().getString("Type");

                                            Toast.makeText(v.getContext(),"Activity Deleted!!",Toast.LENGTH_LONG).show();

                                            NotificationHandler.cancelReminder(user_id+desc+dateTime);

                                            holder.deletebtn.setEnabled(true);

                                            final Intent refresh = new Intent(v.getContext(), FamilyViewActivities.class);
                                            refresh.putExtra("id",id);
                                            refresh.putExtra("type", type);
                                            refresh.putExtra("serv_id",serv_id);
                                            v.getContext().startActivity(refresh);


                                        }
                                    });
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

    }

}
