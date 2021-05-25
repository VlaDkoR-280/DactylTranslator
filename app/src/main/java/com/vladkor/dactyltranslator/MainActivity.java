package com.vladkor.dactyltranslator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.camerakit.CameraKit;
import com.camerakit.CameraKitView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String MODEL_PATH = "";
    private static final boolean QUANT = false;
    private static final String LABEL_PATH = "";
    private static final int INPUT_SIZE = 180;

    private Classifier classifier;

    private Button btnToggleCamera;
    private Button btnDetectObject;

    private CameraKitView cameraKitView;

    private LinearLayout bottomLinearLayout;
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

        cameraKitView = findViewById(R.id.camera);

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
        }
    }
}