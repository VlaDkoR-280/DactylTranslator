package com.vladkor.dactyltranslator;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
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
import com.vladkor.dactyltranslator.TopPlacesList.MyRecyclerViewAdapter;
import com.vladkor.dactyltranslator.TopPlacesList.Person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;


public class UserFragment extends Fragment implements View.OnClickListener {


    private DataSnapshot myDataSnapshot;

    public static final String KEY = "Users";


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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_lesson, container, false);
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
        gameButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_lessonFragment_to_gameFragment));
        guideButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_lessonFragment_to_guideFragment));
        return v;
    }


    private void addMeToDB(){
        Person myPerson = new Person(account.getDisplayName(),account.getPhotoUrl().toString(),0, account.getId());
        myRef.child(myPerson.getID()).setValue(myPerson);
    }

    private void setPersonView(){
        int score = myPerson.getScore() % 100;
        nameTextView.setText(myPerson.getName());
        scoreTextView.setText(String.format("%d/100", score));
        levelTextView.setText(Integer.toString(myPerson.getLevel()));
        progressBar.setProgress(score);
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
        while (iterator.hasNext() && i++ < 3){
            topPlaces.add((Person) iterator.next());
        }
        return topPlaces;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == logOutButton.getId()){
            FirebaseAuth.getInstance().signOut();
        }
    }
}