package com.example.imagedemo.image;

import java.util.List;

public class ImageFolder {
    private String name;
    private List<ImageItem> imageItems;

    public ImageFolder(String name, List<ImageItem> imageItems) {
        this.name = name;
        this.imageItems = imageItems;
    }

    public String getName() {
        return name;
    }

    public List<ImageItem> getImageItems() {
        return imageItems;
    }

    public void setImageItems(List<ImageItem> imageItems) {
        this.imageItems = imageItems;
    }
}
