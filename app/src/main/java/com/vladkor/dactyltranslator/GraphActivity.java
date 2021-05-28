package com.vladkor.dactyltranslator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class GraphActivity extends AppCompatActivity implements View.OnClickListener {
    private CardView zoom;
    private ImageView imageZoom;
    private boolean isZoomIn = true;
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        zoom = findViewById(R.id.zoom_in);
        editText = findViewById(R.id.editText);
        imageZoom = findViewById(R.id.image_zoom);
        zoom.setOnClickListener(this);
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