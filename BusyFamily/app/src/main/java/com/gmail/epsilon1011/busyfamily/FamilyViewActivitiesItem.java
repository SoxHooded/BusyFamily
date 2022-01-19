package com.gmail.epsilon1011.busyfamily;

public class FamilyViewActivitiesItem implements Comparable<FamilyViewActivitiesItem>{

    public FamilyViewActivitiesItem(){}

    public String Date , DateTime , desc ,  user_id , Time;

    public FamilyViewActivitiesItem(String date1, String dateTime1, String desc1, String user_id1 , String Time1) {
        this.Date = date1;
        this.DateTime = dateTime1;
        this.desc = desc1;
        this.user_id = user_id1;
        this.Time=Time1;
    }

    public String getTheDateActivities() {
        return Date;
    }

    public void setTheDateActivites(String date1) {
        Date = date1;
    }

    public String getTheDateTimeActivities() {
        return DateTime;
    }

    public void setTheDateTimeActivities(String dateTime1) {
        DateTime = dateTime1;
    }

    public String getTheDescActivities() {
        return desc;
    }

    public void setTheDescActivities(String desc1) {
        this.desc = desc1;
    }

    public String getTheUser_idActivities() {
        return user_id;
    }

    public void setTheUser_idActivities(String user_id1) {
        this.user_id = user_id1;
    }

    public String getTheTimeActivities(){return Time;}

    public void setTheTimeActivities(String time1){
        this.Time=time1;
    }

    @Override
    public int compareTo (FamilyViewActivitiesItem o){
        if (getTheDateTimeActivities() == null || o.getTheDateTimeActivities() == null) {
            return 0;
        }
        return getTheDateTimeActivities().compareTo(o.getTheDateTimeActivities());
    }



}
