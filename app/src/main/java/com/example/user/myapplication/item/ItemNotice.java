package com.example.user.myapplication.item;

public class ItemNotice {

    private String subject;
    private String text;
    private String date;

    public ItemNotice(String subject, String text, String date) {
        this.subject = subject;
        this.text = text;
        this.date = date;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

}