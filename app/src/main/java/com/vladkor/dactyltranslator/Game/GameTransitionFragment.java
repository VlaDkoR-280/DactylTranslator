package com.vladkor.dactyltranslator.Game;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vladkor.dactyltranslator.Movable;
import com.vladkor.dactyltranslator.R;
import com.vladkor.dactyltranslator.list.Person;

import org.jetbrains.annotations.NotNull;

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

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private Person myPerson;

    private Button nextLevelButton;
    private TextView namePerson;
    private TextView scorePerson;
    private ProgressBar progressBar;
    private ImageView imagePerson;

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
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        myPerson = controller.GetMyPerson();
        if(currentUser == null || myPerson == null){
            controller.ReAuth();
        }
        myPerson.setScore(myPerson.getScore() + offsetScore);
        controller.SetMyPerson(myPerson);
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
        nextLevelButton = v.findViewById(R.id.next_level_button);
        namePerson = v.findViewById(R.id.name_person);
        scorePerson = v.findViewById(R.id.score_text_view);
        progressBar = v.findViewById(R.id.score_progress_bar);
        imagePerson = v.findViewById(R.id.image_person);
        int score = myPerson.getScore() % 100;
        progressBar.setProgress(score);
        scorePerson.setText(String.format("%d/100", score));
        namePerson.setText(myPerson.getName());

        nextLevelButton.setOnClickListener(this);

        return v;
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