package com.gmail.epsilon1011.busyfamily;

public class MyActivitiesItem implements Comparable<MyActivitiesItem> {

    public MyActivitiesItem() {}

    public String Date , DateTime , desc , notification , user_id , Time;

    public MyActivitiesItem(String date1, String dateTime1, String desc1, String user_id1 , String Time1) {
        this.Date = date1;
        this.DateTime = dateTime1;
        this.desc = desc1;
        this.user_id = user_id1;
        this.Time=Time1;
    }

    public String getDateActivities() {
        return Date;
    }

    public void setDateActivites(String date1) {
        Date = date1;
    }

    public String getDateTimeActivities() {
        return DateTime;
    }

    public void setDateTimeActivities(String dateTime1) {
        DateTime = dateTime1;
    }

    public String getDescActivities() {
        return desc;
    }

    public void setDescActivities(String desc1) {
        this.desc = desc1;
    }

    public String getUser_idActivities() {
        return user_id;
    }

    public void setUser_idActivities(String user_id1) {
        this.user_id = user_id1;
    }

    public String getTimeActivities(){return Time;}

    public void setTimeActivities(String time1){
        this.Time=time1;
    }

    @Override
        public int compareTo (MyActivitiesItem o){
        if (getDateTimeActivities() == null || o.getDateTimeActivities() == null) {
            return 0;
        }
        return getDateTimeActivities().compareTo(o.getDateTimeActivities());
    }

}
