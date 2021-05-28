package com.vladkor.dactyltranslator.Game;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.vladkor.dactyltranslator.R;
import com.vladkor.dactyltranslator.TopPlacesList.Person;

public class GameTransitionFragment extends Fragment {





    private Person myPerson;

    private Button nextLevelButton;
    private TextView scoreOfLevel;
    private TextView namePerson;
    private TextView scorePerson;
    private TextView levelPerson;
    private ProgressBar progressBar;
    private ImageView imagePerson;
    private GoogleSignInAccount account;

    private boolean isFirst = true;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private int offsetScore;

    public GameTransitionFragment(int offsetScore) {
        this.offsetScore = offsetScore;
    }

    public GameTransitionFragment() {
        this.offsetScore = 0;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            offsetScore = Integer.parseInt(getArguments().getString("OFFSET"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_transition, container, false);
        account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account == null){
            return v;
        }



        levelPerson = v.findViewById(R.id.level_update);
        scoreOfLevel = v.findViewById(R.id.score_of_level);
        nextLevelButton = v.findViewById(R.id.next_level_button);
        namePerson = v.findViewById(R.id.name_person);
        scorePerson = v.findViewById(R.id.score_text_view);
        progressBar = v.findViewById(R.id.score_progress_bar);
        imagePerson = v.findViewById(R.id.image_person);
        //UpdateViewPerson();

        nextLevelButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_gameTransitionFragment_to_gameFragment));
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

        myRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Person item = snapshot.getValue(Person.class);
                if(item.getID().equals(account.getId())){
                    myPerson = item;
                    if(isFirst){
                        int score = myPerson.getScore() + offsetScore;
                        if (score<0){
                            myPerson.setScore(0);
                        }else{
                            myPerson.setScore(score);
                        }
                        myRef.child(account.getId()).setValue(myPerson);
                        isFirst = false;
                    }
                    UpdateViewPerson();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Person item = snapshot.getValue(Person.class);
                if(item.getID().equals(account.getId())){
                    myPerson = item;
                    if(isFirst){
                        int score = myPerson.getScore() + offsetScore;
                        if (score<0){
                            myPerson.setScore(0);
                        }else{
                            myPerson.setScore(score);
                        }
                        myRef.child(account.getId()).setValue(myPerson);
                        isFirst = false;
                    }
                    UpdateViewPerson();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return v;
    }

    private void UpdateViewPerson(){
        namePerson.setText(myPerson.getName());
        int shortScore = myPerson.getScore() % 100;
        scorePerson.setText(Integer.toString(shortScore));
        Picasso.get().load(myPerson.getImageProfile()).into(imagePerson);
        progressBar.setProgress(shortScore);
        levelPerson.setText(Integer.toString(myPerson.getLevel()));
        scorePerson.setText(String.format("%d/100", shortScore));
        if(offsetScore < 0){
            scoreOfLevel.setTextColor(Color.RED);
        }else{
            scoreOfLevel.setTextColor(Color.GREEN);
        }
        scoreOfLevel.setText(Integer.toString(offsetScore));
    }

}