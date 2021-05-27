package com.vladkor.dactyltranslator.list;

import android.net.Uri;

public class Person {

    private String name;
    private String imageProfile;
    private int score;
    private String ID;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Person(String name, String imageProfile, int score) {
        this.name = name;
        this.imageProfile = imageProfile;
        this.score = score;
        this.level = score / 100;
    }

    public Person(String name, String imageProfile, int score, String id) {
        this.name = name;
        this.imageProfile = imageProfile;
        this.score = score;
        this.level = score / 100;
        this.ID = id;
    }
    public Person() {
        this.name = "";
        this.imageProfile = "";
        this.score = 0;
        this.level = 0;
        this.ID = "";
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    private int level;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(String imageProfile) {
        this.imageProfile = imageProfile;
    }


    public int getLevel() {
        return level;
    }

    public void setLevel(int score) {
        this.level = (int)(score/100);
    }
}
