package com.vladkor.dactyltranslator;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.service.controls.Control;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vladkor.dactyltranslator.GuideList.MyGuideRecyclerAdapter;
import com.vladkor.dactyltranslator.dactylLanguage.DactylWords;
import com.vladkor.dactyltranslator.GuideList.ItemWord;

import java.util.ArrayList;

public class GuideFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Movable controller;

    private ArrayList<ItemWord> words = new ArrayList<>();
    private RecyclerView recyclerView;


    private String mParam1;
    private String mParam2;

    public GuideFragment() {

    }


    public static GuideFragment newInstance(String param1, String param2) {
        GuideFragment fragment = new GuideFragment();
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
        View v = inflater.inflate(R.layout.fragment_guide, container, false);
        recyclerView = v.findViewById(R.id.guide_recycler);
        for(int i = 0; i < DactylWords.words.length; i++){
            words.add(new ItemWord(DactylWords.words[i], DactylWords.urls[i]));
        }
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(v.getContext(), 2, GridLayoutManager.VERTICAL, false);
        MyGuideRecyclerAdapter adapter = new MyGuideRecyclerAdapter(words);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);

        return v;
    }

    public void setController(Movable movable){
        controller = movable;
    }
}