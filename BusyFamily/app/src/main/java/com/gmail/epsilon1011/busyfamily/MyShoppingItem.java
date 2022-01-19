package com.gmail.epsilon1011.busyfamily;

public class MyShoppingItem {

    public MyShoppingItem(){}

        public String Item , Quantity;

        public MyShoppingItem(String Item1 , String Quantity1)  {
            this.Item = Item1;
            this.Quantity =  Quantity1;

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

    }
