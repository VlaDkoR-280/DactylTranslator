package com.vladkor.dactyltranslator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vladkor.dactyltranslator.Game.GameFragment;
import com.vladkor.dactyltranslator.list.Person;
import com.vladkor.dactyltranslator.list.MyRecyclerViewAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfilePersonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfilePersonFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private DataSnapshot myDataSnapshot;

    public static final String KEY = "Persons";

    private Movable controller;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private GoogleSignInAccount account;

    private Person myPerson;

    private TextView nameTextView;
    private TextView scoreTextView;
    private TextView levelTextView;
    private ImageView avatarImageView;
    private Button logOutButton;
    private ProgressBar progressBar;
    private CardView gameButton;

    private ArrayList<Person> topPlaces = new ArrayList<>();
    private ArrayList<Person> persons = new ArrayList<>();
    private RecyclerView recyclerView;

    public ProfilePersonFragment() {
        // Required empty public constructor
    }


    public static ProfilePersonFragment newInstance(String param1, String param2) {
        ProfilePersonFragment fragment = new ProfilePersonFragment();
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

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_base, container, false);
        gameButton = v.findViewById(R.id.game_button);
        scoreTextView = v.findViewById(R.id.score_text_view);
        progressBar = v.findViewById(R.id.score_progress_bar);
        logOutButton = v.findViewById(R.id.log_out_button);
        levelTextView = v.findViewById(R.id.level_person);
        nameTextView = v.findViewById(R.id.name_person);
        avatarImageView = v.findViewById(R.id.image_person);
        recyclerView = v.findViewById(R.id.top_places);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        account = GoogleSignIn.getLastSignedInAccount(getActivity());
        if(account!= null){
            nameTextView.setText(account.getDisplayName());
            Uri uriPhoto = account.getPhotoUrl();
            Picasso.get().load(uriPhoto).into(avatarImageView);
        }

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(topPlaces.size() > 0) topPlaces.clear();
                if(persons.size() > 0) persons.clear();
                try{
                    for(DataSnapshot ds : snapshot.getChildren()){
                        Person item = ds.getValue(Person.class);
                        persons.add(item);
                    }
                    if (persons.size() == 0) {
                        addMeToDB();
                    }else{
                        boolean iExist = false;
                        for(Person item : persons){
                            if(item.getID().equals(account.getId())){
                                iExist = true;
                                getMeFromDB(item);
                                break;
                            }
                        }
                        if(!iExist){
                            addMeToDB();
                        }
                    }
                    //TODO Сортировка по количеству очков
                    //...
                    topPlaces.add(new Person("Test", account.getPhotoUrl().toString(), 10));
                    topPlaces.add(new Person("Test", account.getPhotoUrl().toString(), 10));
                    topPlaces.add(new Person("Test", account.getPhotoUrl().toString(), 10));
                    MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(getContext(), topPlaces);
                    recyclerView.setAdapter(adapter);
                }catch (Exception e){
                    //Toast.makeText(getContext(),e.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        logOutButton.setOnClickListener(this);

        return v;
    }

    public void setMovable(Movable movable){
        this.controller = movable;
    }

    private void addMeToDB(){
        Person myPerson = new Person(account.getDisplayName(),account.getPhotoUrl().toString(),0, account.getId());
        myRef.push().setValue(myPerson);
    }

    private void getMeFromDB(Person item){
        myPerson = item;
        int score = myPerson.getScore() % 100;
        scoreTextView.setText(String.format("%d/100", score));
        try{
            levelTextView.setText(Integer.toString(myPerson.getLevel()));
        }catch (Exception e){}
        progressBar.setProgress(score);
        controller.SetMyPerson(item);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == logOutButton.getId()){
            FirebaseAuth.getInstance().signOut();
            controller.ReAuth();
        }else if(v.getId() == gameButton.getId()){
            GameFragment gameFragment = new GameFragment();
            controller.MoveTo(gameFragment);
        }
    }
}