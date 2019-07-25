package com.example.imagedemo.image;

import java.io.Serializable;

public class ImageItem implements Serializable {
    private int id;
    private String path;
    private String date;

    public ImageItem(int id, String path, String date) {
        this.id = id;
        this.path = path;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public String getDate() {
        return date;
    }

    @Override
    public boolean equals(Object obj) {
        ImageItem imageItem = (ImageItem) obj;
        return id == imageItem.getId() && path.equals(imageItem.getPath()) && date.equals(imageItem.getDate());
    }
}
