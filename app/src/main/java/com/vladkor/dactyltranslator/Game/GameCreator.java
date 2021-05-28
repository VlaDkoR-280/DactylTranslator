package com.vladkor.dactyltranslator.Game;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Random;

public class GameCreator {
    Random random;
    private int ID;
    private int attemps;
    private String[] letters;
    private Uri[] imageLetters;
    private String answer;
    public GameCreator(){
        letters = new String[]{"а","б","в","г","д","е","ж","з","и","к","л","м","н","о","п","р","с","т","у","ф","х","ц","ч","ш","ъ","ы","э","ю","я"};
        imageLetters = new Uri[]{};
        attemps = 3;
    }

    private int GenerateId(){
        random = new Random();
        int id;
        do {
            id = random.nextInt();
        }while(id > 0 && id < letters.length);
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
        answer = letters[4];
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
        return answer;
    }
    public String getUriAnswer(){
        //return imageLetters[ID].toString();
        //TEST
        return "https://lh3.googleusercontent.com/a-/AOh14GgvbkD9X9XSXNaF4wYWR4H-nG8GxT6JieSjtL-Q0w=s96-c";
    }

}
