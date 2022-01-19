package com.gmail.epsilon1011.busyfamily;

public class InvitesItem {

    public String req_type , from;

    public InvitesItem(){}

    public InvitesItem(String req_type){

        this.req_type = req_type;

    }

    public void setthisUser_id(String user_id) {

        this.from = user_id;
    }

    public String getthisUser_id() {
        return from;

    }

    public String gettheReq_type() {
        return req_type;
    }

    public void settheReq_type(String type) {
        this.req_type = type;
    }

}
