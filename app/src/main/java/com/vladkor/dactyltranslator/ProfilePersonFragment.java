package com.vladkor.dactyltranslator;

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
import android.widget.Toast;

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
import com.vladkor.dactyltranslator.TopPlacesList.Person;
import com.vladkor.dactyltranslator.TopPlacesList.MyRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

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

    public static final String KEY = "Users";

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
    private Button guideButton;
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
        guideButton = v.findViewById(R.id.guide_button);
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

                        if(item.getID().equals(account.getId())) {
                            myPerson = item;
                            setPersonView();
                        }
                        persons.add(item);
                    }
                    if (myPerson == null){
                        addMeToDB();
                    }
                    //TODO Сортировка по количеству очков
                    //...
                    MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(getContext(), SortTopPlaces(persons));
                    recyclerView.setAdapter(adapter);
                }catch (Exception e){
                    Toast.makeText(getContext(),e.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        logOutButton.setOnClickListener(this);
        gameButton.setOnClickListener(this);
        guideButton.setOnClickListener(this);


        return v;
    }

    public void setMovable(Movable movable){
        this.controller = movable;
    }

    private void addMeToDB(){
        Person myPerson = new Person(account.getDisplayName(),account.getPhotoUrl().toString(),0, account.getId());
        myRef.child(myPerson.getID()).setValue(myPerson);
        controller.SetMyPerson(myPerson);
    }

    private void setPersonView(){
        int score = myPerson.getScore() % 100;
        nameTextView.setText(myPerson.getName());
        scoreTextView.setText(String.format("%d/100", score));
        levelTextView.setText(Integer.toString(myPerson.getLevel()));
        progressBar.setProgress(score);
        controller.SetMyRefData(myRef);
        controller.SetMyPerson(myPerson);
    }

    private ArrayList<Person> SortTopPlaces(ArrayList<Person> users){
        ArrayList<Person> topPlaces = new ArrayList<>();
        Comparator<Person> comparator = new Comparator<Person>() {
            @Override
            public int compare(Person o1, Person o2) {
                return o2.getScore() - o1.getScore();
            }
        };
        Collections.sort(users,comparator);
        Iterator iterator = users.iterator();

        int i = 0;
        while (iterator.hasNext() && i++ < 2){
            topPlaces.add((Person) iterator.next());
        }
        return topPlaces;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == logOutButton.getId()){
            FirebaseAuth.getInstance().signOut();
            controller.ReAuth();
        }else if(v.getId() == gameButton.getId()){
            GameFragment gameFragment = new GameFragment();
            controller.MoveTo(gameFragment);
        }else if(v.getId() == guideButton.getId()){
            GuideFragment guideFragment = new GuideFragment();
            controller.MoveTo(guideFragment);
        }
    }
}