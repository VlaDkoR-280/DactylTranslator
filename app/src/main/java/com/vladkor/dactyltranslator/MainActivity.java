package com.vladkor.dactyltranslator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.camerakit.CameraKitView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String MODEL_PATH = "";
    private static final boolean QUANT = false;
    private static final String LABEL_PATH = "";
    private static final int INPUT_SIZE = 180;

    private Classifier classifier;

    private LessonsActivity lessonsActivity;
    private CardView btnToggleCamera;
    private CardView btnDetectObject;

    private CardView btnGraphicsEditor;
    private CardView btnLessons;
    private CardView btnAbout;

    private CardView btnClearTranslate;

    private CameraKitView cameraKitView;

    private CardView bottomLinearLayout;
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout transtaleTextLayout;
    private ImageView bottomSheetArrow;

    private TextView resultTextView;
    private TextView forceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultTextView = findViewById(R.id.t9_text_view);
        forceTextView = findViewById(R.id.force_text_view);

        btnDetectObject = findViewById(R.id.CaptureBtn);
        btnToggleCamera = findViewById(R.id.ToggleBtn);

        btnDetectObject.setOnClickListener(this);
        btnToggleCamera.setOnClickListener(this);

        btnGraphicsEditor = findViewById(R.id.graphicsEditorBtn);
        btnLessons = findViewById(R.id.lessonsBtn);
        btnAbout = findViewById(R.id.aboutBtn);

        btnGraphicsEditor.setOnClickListener(this);
        btnLessons.setOnClickListener(this);
        btnAbout.setOnClickListener(this);

        cameraKitView = findViewById(R.id.camera);

        btnClearTranslate = findViewById(R.id.clearButton);

        btnClearTranslate.setOnClickListener(this);
        bottomLinearLayout = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomLinearLayout);
        transtaleTextLayout = findViewById(R.id.translate_text);
        bottomSheetArrow = findViewById(R.id.bottom_sheet_arrow);
        ViewTreeObserver vto = transtaleTextLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                transtaleTextLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                bottomSheetBehavior.setPeekHeight(transtaleTextLayout.getMeasuredHeight());
            }
        });

        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.addBottomSheetCallback(
                new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        switch (newState){
                            case BottomSheetBehavior.STATE_HIDDEN:
                                break;
                            case BottomSheetBehavior.STATE_EXPANDED:
                                //bottomSheetArrow.setImageResource(R.drawable);
                                break;
                            case BottomSheetBehavior.STATE_COLLAPSED:
                                //bottomSheetArrow.setImageResource();
                                break;
                            case BottomSheetBehavior.STATE_DRAGGING:
                                break;
                            case BottomSheetBehavior.STATE_SETTLING:
                                //bottomSheetArrow.setImageResource();
                                break;
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                    }
                }
        );


    }

    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }

    @Override
    protected void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        cameraKitView.onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnDetectObject.getId()){
            cameraKitView.captureImage(new CameraKitView.ImageCallback() {
                @Override
                public void onImage(CameraKitView cameraKitView, byte[] bytes) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.button_capture), Toast.LENGTH_SHORT).show();
                }
            });
        }else if(v.getId() == btnToggleCamera.getId()){
            cameraKitView.toggleFacing();
        }else if (v.getId() == btnClearTranslate.getId()){
            resultTextView.setText("");
            forceTextView.setText("");
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.clear_toast_text), Toast.LENGTH_SHORT).show();
        }else if(v.getId() == btnGraphicsEditor.getId()){
            Intent i = new Intent(getApplicationContext(), GraphActivity.class);
            startActivity(i);
            //Toast.makeText(getApplicationContext(), getResources().getString(R.string.graphics_editor_name), Toast.LENGTH_SHORT).show();
        }else if (v.getId() == btnLessons.getId()){
            //Toast.makeText(getApplicationContext(), getResources().getString(R.string.lessons_name), Toast.LENGTH_SHORT).show();
            lessonsActivity = new LessonsActivity();
            Intent i = new Intent(getApplicationContext(), lessonsActivity.getClass());
            startActivity(i);
        }else if (v.getId() == btnAbout.getId()){
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.settings_name), Toast.LENGTH_SHORT).show();
        }
    }
}