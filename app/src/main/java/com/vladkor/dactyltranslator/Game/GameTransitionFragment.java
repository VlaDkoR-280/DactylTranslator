package com.vladkor.dactyltranslator.Game;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.vladkor.dactyltranslator.Movable;
import com.vladkor.dactyltranslator.R;
import com.vladkor.dactyltranslator.TopPlacesList.Person;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameTransitionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameTransitionFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;
    private Movable controller;

    private Person myPerson;

    private Button nextLevelButton;
    private TextView scoreOfLevel;
    private TextView namePerson;
    private TextView scorePerson;
    private TextView levelPerson;
    private ProgressBar progressBar;
    private ImageView imagePerson;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private int offsetScore;

    public GameTransitionFragment(int offsetScore) {
        this.offsetScore = offsetScore;
    }

    public GameTransitionFragment() {
        this.offsetScore = 0;
    }

    public static GameTransitionFragment newInstance(String param1, String param2) {
        GameTransitionFragment fragment = new GameTransitionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_transition, container, false);

        myPerson = controller.GetMyPerson();

        if (myPerson == null){
            return v;
        }
        int score = myPerson.getScore() + offsetScore;
        if (score<0){
            myPerson.setScore(0);
        }else{
            myPerson.setScore(score);
        }
        levelPerson = v.findViewById(R.id.level_update);
        scoreOfLevel = v.findViewById(R.id.score_of_level);
        nextLevelButton = v.findViewById(R.id.next_level_button);
        namePerson = v.findViewById(R.id.name_person);
        scorePerson = v.findViewById(R.id.score_text_view);
        progressBar = v.findViewById(R.id.score_progress_bar);
        imagePerson = v.findViewById(R.id.image_person);
        UpdateViewPerson();

        nextLevelButton.setOnClickListener(this);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");
        myRef.child(myPerson.getID()).setValue(myPerson);
        myRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Person item = snapshot.getValue(Person.class);
                if(item.getID().equals(myPerson.getID())){
                    myPerson = item;
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

    public void setController(Movable movable){
        controller = movable;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == nextLevelButton.getId()){
            GameFragment gameFragment = new GameFragment();
            controller.MoveTo(gameFragment);
        }
    }
}