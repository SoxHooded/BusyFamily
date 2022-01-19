package com.gmail.epsilon1011.busyfamily;

public class FamilyAlbumItem {

    private String ImageUrl;

    public FamilyAlbumItem(){
        //empty constructor needed
    }

    public FamilyAlbumItem(String imageUrl) {

        ImageUrl = imageUrl;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl1) {
        ImageUrl = imageUrl1;
    }


}