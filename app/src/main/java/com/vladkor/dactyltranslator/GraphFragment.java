package com.vladkor.dactyltranslator;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;


public class GraphFragment extends Fragment implements View.OnClickListener {

    private CardView zoom;
    private ImageView imageZoom;
    private boolean isZoomIn = true;
    private EditText editText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_graph, container, false);
        zoom = v.findViewById(R.id.zoom_in);
        editText = v.findViewById(R.id.editText);
        imageZoom = v.findViewById(R.id.image_zoom);
        zoom.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == zoom.getId()){
            if(isZoomIn){
                editText.setTextSize(50);
                imageZoom.setImageResource(R.drawable.ic_zoom_out_24);
                isZoomIn = !isZoomIn;
            }else{
                editText.setTextSize(20);
                imageZoom.setImageResource(R.drawable.ic_zoom_in_24);
                isZoomIn = !isZoomIn;
            }

        }
    }
}