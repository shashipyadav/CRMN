package com.example.myapplication.user_interface.home.model;

import java.io.Serializable;

public class ImageModel implements Serializable {

    private String imagePath;

    public ImageModel(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String productImage) {
        this.imagePath = productImage;
    }
}
