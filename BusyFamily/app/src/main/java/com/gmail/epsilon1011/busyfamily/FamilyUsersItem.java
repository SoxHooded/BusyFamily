package com.gmail.epsilon1011.busyfamily;

public class FamilyUsersItem {

    public FamilyUsersItem(){}

    public String user_id , Server_id , Color , Type , ImageUrl;

    public FamilyUsersItem(String user_id1, String server_id1, String color1, String type1 ,String url) {
        this.user_id = user_id1;
        this.Server_id = server_id1;
        this.Color = color1;
        this.Type = type1;
        this.ImageUrl = url;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id1) {
        this.user_id = user_id1;
    }

    public String getTheServer_id() {
        return Server_id;
    }

    public void setTheServer_id(String server_id1) {
        Server_id = server_id1;
    }

    public String getTheColor() {
        return Color;
    }

    public void setTheColor(String color1) {
        Color = color1;
    }

    public String getTheType() {
        return Type;
    }

    public void setTheType(String type1) {
        Type = type1;
    }

    public void setTheImageUrl(String url1){
        ImageUrl = url1;
    }
    public String getTheImageUrl(){return ImageUrl;}
}
