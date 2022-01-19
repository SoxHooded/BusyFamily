package com.gmail.epsilon1011.busyfamily;

public class FamilyActivitiesItem {

    public String user_id , Server_id , Color , Type , ImageUrl;

    public FamilyActivitiesItem (){}

    public FamilyActivitiesItem(String user_id1, String server_id1, String color1, String type1 ,String url){
        this.user_id = user_id1;
        this.Server_id = server_id1;
        this.Color = color1;
        this.Type = type1;
        this.ImageUrl = url;
    }


    public String getUser_idActivities() {
        return user_id;
    }

    public void setUser_idActivities(String user_id) {
        this.user_id = user_id;
    }

    public String getServer_idActivities() {
        return Server_id;
    }

    public void setServer_idActivities(String server_id) {
        Server_id = server_id;
    }

    public String getColorActivites() {
        return Color;
    }

    public void setColorActivites(String color) {
        Color = color;
    }

    public String getTypeActivites() {
        return Type;
    }

    public void setTypeActivites(String type) {
        Type = type;
    }

    public String getImageUrlActivites() {
        return ImageUrl;
    }

    public void setImageUrlActivities(String imageUrl) {
        ImageUrl = imageUrl;
    }
}
