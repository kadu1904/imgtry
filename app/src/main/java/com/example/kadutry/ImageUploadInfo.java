package com.example.kadutry;

public class ImageUploadInfo {
    String title,description,image;

    public ImageUploadInfo() {
    }

    public ImageUploadInfo(String title, String description, String image) {
        this.title = title;
        this.description = description;
        this.image = image;
    }

    public ImageUploadInfo(String mPostTitle, String mPostDescr, String toString, String toLowerCase) {
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }
}
