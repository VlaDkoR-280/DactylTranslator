package com.vladkor.dactyltranslator.list;

import android.net.Uri;

public class ItemTopPlace {

    private String name;
    private Uri imageProfile;
    private int score;

    public ItemTopPlace(String name, Uri imageProfile, int score) {
        this.name = name;
        this.imageProfile = imageProfile;
        this.score = score;
        this.level = score / 100;
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

    public Uri getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(Uri imageProfile) {
        this.imageProfile = imageProfile;
    }


    public int getLevel() {
        return level;
    }

    public void setLevel(int score) {
        this.level = (int)(score/100);
    }
}
