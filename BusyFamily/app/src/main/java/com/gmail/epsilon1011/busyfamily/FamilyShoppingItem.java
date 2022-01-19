package com.gmail.epsilon1011.busyfamily;

public class FamilyShoppingItem {

    public FamilyShoppingItem(){}

    public String Item;
    public String Quantity;
    public String Server_id;

    public FamilyShoppingItem(String Item1 , String Quantity1 , String server_id1)  {
        this.Item = Item1;
        this.Quantity =  Quantity1;
        this.Server_id = server_id1;

    }

    public String getTheItem() {
        return Item;
    }

    public void setTheItem(String Item1) {
        Item = Item1;
    }

    public String getTheQuantity() {
        return Quantity;
    }

    public void setTheQuantity(String Quantity1) {
        Quantity = Quantity1;
    }

    public String getTheServer() {
        return Server_id;
    }

    public void setTheServer(String server_id1) {
        Server_id = server_id1;
    }

}
