package com.example.user.myapplication.item;

import android.net.Uri;

public class ItemMember {

    private String imageUri;
    private int grade;
    private String id;
    private String password;

    public ItemMember(Uri imageUri, int grade, String id, String password) {
        setImageUri(imageUri);
        setGrade(grade);
        setId(id);
        setPassword(password);
    }

    private void setImageUri(Uri imageUri) {
        this.imageUri = imageUri.toString();
    }

    private void setId(String id) {
        this.id = id;
    }

    private void setPassword(String password) {
        this.password = password;
    }

    private void setGrade(int grade) {
        this.grade = grade;
    }

    public Uri getImageUri() {
        return Uri.parse(imageUri);
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public int getGrade() {
        return grade;
    }

}