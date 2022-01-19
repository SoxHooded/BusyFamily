package com.gmail.epsilon1011.busyfamily;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FamilyChatAdapter extends RecyclerView.Adapter<FamilyChatAdapter.ViewHolder> {

    public List<FamilyChatItem> chatMessage;
    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;
    private String current_user;

    public FamilyChatAdapter(List<FamilyChatItem> chatMessage1) {
        this.chatMessage = chatMessage1;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)  {


        //inflate view (list_item)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message, parent, false);

        mStore= FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();
        current_user = mAuth.getCurrentUser().getUid();

        Collections.sort(chatMessage);


        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);

        final String sender = chatMessage.get(position).getTheSender();
        String Message = chatMessage.get(position).getTheMessage();
        holder.setMessageText(Message);
        final String time = chatMessage.get(position).getThePostTime();
        holder.setTimeText(time);

        if(sender.equals(current_user)){
            holder.layout2.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }




        mStore.collection("Users").document(sender).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                String username = task.getResult().getString("Username");
                holder.setSenderText(username);


                mStore.collection("Family_Calendar_Users").document(sender).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {

                            String color = task.getResult().getString("Color");
                            String url = task.getResult().getString("ImageUrl");

                                holder.setTheImageColorChat(color);
                                holder.LoadChatImage(url);

                            if (color != null) {
                                if (color.equals("Red")) {
                                    holder.layout.setBackgroundResource(R.drawable.chat_red);
                                } else if (color.equals("Blue")) {
                                    holder.layout.setBackgroundResource(R.drawable.chat_blue);
                                } else if (color.equals("Green"))
                                    holder.layout.setBackgroundResource(R.drawable.chat_green);
                                else if (color.equals("Yellow"))
                                    holder.layout.setBackgroundResource(R.drawable.chat_yellow);
                                else if (color.equals("Purple")) {
                                    holder.layout.setBackgroundResource(R.drawable.chat_purple);
                                }

                            }
                        }
                    }
                });

            }
        });


    }



    @Override
    public int getItemCount() { //list size

        return chatMessage.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private TextView time;
        private TextView sender;
        private TextView message;
        private RelativeLayout layout2;
        private LinearLayout layout;
        private CircleImageView image;



        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            sender = mView.findViewById(R.id.chatSender);
            time = mView.findViewById(R.id.chatTime);
            message = mView.findViewById(R.id.chatMessage);
            layout = mView.findViewById(R.id.chat_layout);
            layout2 = mView.findViewById(R.id.rlay);
            image = mView.findViewById(R.id.chatImage);


        }

        public void setTimeText(String chatText){
            time.setText(chatText);
        }

        public void setSenderText(String senderText){
            sender.setText(senderText);
        }

        public void setMessageText(String messageText){
            message.setText(messageText);
        }

        public void LoadChatImage(String url){

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

        public void setTheImageColorChat(String getcolor){
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

    }


}
