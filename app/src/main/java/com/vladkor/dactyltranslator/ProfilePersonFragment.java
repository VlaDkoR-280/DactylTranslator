package com.vladkor.dactyltranslator;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.vladkor.dactyltranslator.list.ItemTopPlace;
import com.vladkor.dactyltranslator.list.MyRecyclerViewAdapter;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfilePersonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfilePersonFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String nameProfile;
    private String idProfile;
    private Uri imageProfile;

    private TextView nameTextView;
    private ImageView avatarImageView;

    private ArrayList<ItemTopPlace> topPlaces = new ArrayList<>();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_base, container, false);
        nameTextView = v.findViewById(R.id.namePerson);
        avatarImageView = v.findViewById(R.id.imagePerson);
        recyclerView = v.findViewById(R.id.topPlaces);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
        if(account!= null){
            nameTextView.setText(account.getDisplayName());
            Uri uriPhoto = account.getPhotoUrl();
            Picasso.get().load(uriPhoto).into(avatarImageView);
        }

        topPlaces.add(new ItemTopPlace("Test", account.getPhotoUrl(), 10));
        topPlaces.add(new ItemTopPlace("Test", account.getPhotoUrl(), 10));
        topPlaces.add(new ItemTopPlace("Test", account.getPhotoUrl(), 10));
        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(getContext(), topPlaces);
        recyclerView.setAdapter(adapter);
        return v;
    }
}