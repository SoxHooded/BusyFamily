package com.gmail.epsilon1011.busyfamily;

public class FamilyChatItem implements Comparable<FamilyChatItem>{

    public FamilyChatItem(){}

    public String Message , PostTime , Timestamp , Sender , Server_id;

    public FamilyChatItem(String Message1 , String PostTime1 , String Timestamp1 , String Sender1 , String Server_id1){
        this.Message = Message1;
        this.PostTime = PostTime1;
        this.Timestamp = Timestamp1;
        this.Sender = Sender1;
        this.Server_id = Server_id1;
    }


    public String getTheTimestamp(){return Timestamp;}

    public void setTheTimestamp(String Timestamp1){
        this.Timestamp = Timestamp1;
    }

    public String getTheMessage(){return Message;}

    public void setTheMessage(String Message1){
        this.Message = Message1;
    }

    public String getThePostTime(){return PostTime;}

    public void seThePostTime(String PostTime1){this.PostTime= PostTime1;}

    public String getTheSender(){return Sender;}

    public void setTheSender(String Sender1){this.Sender = Sender1;}

    public String getChatServer(){return Server_id;}

    public void setChatServer(String Server_id1){
        this.Server_id = Server_id1;
    }



    @Override
    public int compareTo(FamilyChatItem o) {
        if (getTheTimestamp() == null || o.getTheTimestamp() == null) {
            return 0;
        }
        return getTheTimestamp().compareTo(o.getTheTimestamp());
    }

}
