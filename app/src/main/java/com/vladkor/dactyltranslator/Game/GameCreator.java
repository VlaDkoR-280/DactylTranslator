package com.vladkor.dactyltranslator.Game;

import android.graphics.Bitmap;
import android.net.Uri;

import com.vladkor.dactyltranslator.dactylLanguage.DactylWords;

import java.util.ArrayList;
import java.util.Random;

public class GameCreator {
    Random random;
    private int ID;
    private int attemps;
    private String[] letters;
    private String[] imageLetters;
    private String answer;

    public GameCreator(){
        letters = DactylWords.words;
        imageLetters = DactylWords.urls;
        attemps = 3;
    }

    private int GenerateId(){
        random = new Random();
        int id;
        do {
            id = random.nextInt(letters.length);
        }while(id < 0 && id >= letters.length);
        ID = id;
        return id;
    }
    private int GenerateId(int startBounds, int endBounds){
        random = new Random();
        int id;
        do {
            id = random.nextInt();
        }while(id < startBounds && id > endBounds);
        return id;
    }


    public void CreateEasyGame() {
        answer = letters[GenerateId()];
    }

    public void CreateNormalGame() {

    }

    public void CreateHardGame() {

    }

    public int getAttemps(){
        return attemps;
    }

    public boolean checkAnswer(String answer) {
        attemps--;
        return this.answer == answer && attemps >= 0;
    }

    public String getAnswer(){
        return String.format("\"%s\"", answer);
    }

    public String getUriAnswer(){
        return imageLetters[ID];
    }

}
