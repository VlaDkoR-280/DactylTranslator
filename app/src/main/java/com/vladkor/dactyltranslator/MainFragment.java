package com.vladkor.dactyltranslator;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.camerakit.CameraKitView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;


public class MainFragment extends Fragment implements View.OnClickListener {


    private static final String MODEL_PATH = "";
    private static final boolean QUANT = false;
    private static final String LABEL_PATH = "";
    private static final int INPUT_SIZE = 180;

    private Classifier classifier;

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


    public MainFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        resultTextView = v.findViewById(R.id.t9_text_view);
        forceTextView = v.findViewById(R.id.force_text_view);

        btnDetectObject = v.findViewById(R.id.CaptureBtn);
        btnToggleCamera = v.findViewById(R.id.ToggleBtn);

        btnDetectObject.setOnClickListener(this);
        btnToggleCamera.setOnClickListener(this);

        btnGraphicsEditor = v.findViewById(R.id.graphicsEditorBtn);
        btnLessons = v.findViewById(R.id.lessonsBtn);
        btnAbout = v.findViewById(R.id.aboutBtn);

        btnGraphicsEditor.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_mainFragment_to_graphFragment));
        btnLessons.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_mainFragment_to_signInFragment));
        btnAbout.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_mainFragment_to_aboutFragment));

        cameraKitView = v.findViewById(R.id.camera);

        btnClearTranslate = v.findViewById(R.id.clearButton);

        btnClearTranslate.setOnClickListener(this);
        bottomLinearLayout = v.findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomLinearLayout);
        transtaleTextLayout = v.findViewById(R.id.translate_text);
        bottomSheetArrow = v.findViewById(R.id.bottom_sheet_arrow);
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
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }

    @Override
    public void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
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
                    Toast.makeText(v.getContext(), getResources().getString(R.string.button_capture), Toast.LENGTH_SHORT).show();
                }
            });
        }else if(v.getId() == btnToggleCamera.getId()){
            cameraKitView.toggleFacing();
        }else if (v.getId() == btnClearTranslate.getId()){
            resultTextView.setText("");
            forceTextView.setText("");
            Toast.makeText(v.getContext(), getResources().getString(R.string.clear_toast_text), Toast.LENGTH_SHORT).show();
        }
    }
}