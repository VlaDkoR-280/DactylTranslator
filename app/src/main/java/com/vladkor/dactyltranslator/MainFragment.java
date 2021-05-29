package com.vladkor.dactyltranslator;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.HttpAuthHandler;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.camerakit.CameraKitView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class MainFragment extends Fragment implements View.OnClickListener {

    //Queue<ByteBuffer> queue;
    private static final String MODEL_PATH = "model_converted.tflite";
    private static final boolean QUANT = false;
    private static final String LABEL_PATH = "labels.txt";
    private static final int INPUT_SIZE = 180;
    private Executor executor = Executors.newSingleThreadExecutor();

    private Classifier classifier;

    private CardView btnToggleCamera;
    private CardView btnDetectObject;

    private CardView btnGraphicsEditor;
    private CardView btnLessons;
    private CardView btnAbout;

    private CardView btnClearTranslate;

    Handler handler = new Handler();

    private TextureView mImageView;

    private CardView bottomLinearLayout;
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout transtaleTextLayout;
    private ImageView bottomSheetArrow;

    private TextView resultTextView;
    private TextView forceTextView;


    CameraService[] myCameras = null;
    private ImageView im;

    private CameraManager mCameraManager    = null;
    private final int CAMERA1   = 0;
    private final int CAMERA2   = 1;
    private boolean cameraFace = false;


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

        mImageView = v.findViewById(R.id.camera);
        btnGraphicsEditor = v.findViewById(R.id.graphicsEditorBtn);
        btnLessons = v.findViewById(R.id.lessonsBtn);
        btnAbout = v.findViewById(R.id.aboutBtn);

        btnGraphicsEditor.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_mainFragment_to_graphFragment));
        btnLessons.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_mainFragment_to_signInFragment));
        btnAbout.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_mainFragment_to_aboutFragment));




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

        if (getContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                ||
                (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        )
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

        mCameraManager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
        try{
            myCameras = new CameraService[mCameraManager.getCameraIdList().length];

            for (String cameraID : mCameraManager.getCameraIdList()) {
                int id = Integer.parseInt(cameraID);

                myCameras[id] = new CameraService(mCameraManager,cameraID);

            }
        }
        catch(CameraAccessException e){
            e.printStackTrace();
        }

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorFlowImageClassifier.create(
                            getContext().getAssets(),
                            MODEL_PATH,
                            LABEL_PATH,
                            INPUT_SIZE,
                            QUANT);
//                    makeButtonVisible();
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });

        return v;
    }


    @Override
    public void onStart() {
        super.onStart();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (myCameras[CAMERA2].isOpen()) {
                    myCameras[CAMERA2].closeCamera();
                }
                if (myCameras[CAMERA1] != null) {
                    if (!myCameras[CAMERA1].isOpen()) myCameras[CAMERA1].openCamera();
                }
            }
        }, 500);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnDetectObject.getId()){
            startClassifer();
        }else if(v.getId() == btnToggleCamera.getId()){
            if(cameraFace){
                if (myCameras[CAMERA2].isOpen()) {myCameras[CAMERA2].closeCamera();}
                if (myCameras[CAMERA1] != null) {
                    if (!myCameras[CAMERA1].isOpen()) myCameras[CAMERA1].openCamera();
                }
                cameraFace = !cameraFace;
            }else{
                if (myCameras[CAMERA1].isOpen()) {myCameras[CAMERA1].closeCamera();}
                if (myCameras[CAMERA2] != null) {
                    if (!myCameras[CAMERA2].isOpen()) myCameras[CAMERA2].openCamera();
                }
                cameraFace = !cameraFace;
            }
        }else if (v.getId() == btnClearTranslate.getId()){
            resultTextView.setText("");
            forceTextView.setText("");
            Toast.makeText(v.getContext(), getResources().getString(R.string.clear_toast_text), Toast.LENGTH_SHORT).show();
        }
    }



    public class CameraService {


        private String mCameraID;
        private CameraDevice mCameraDevice = null;
        private CameraCaptureSession mCaptureSession;

        public CameraService(CameraManager cameraManager, String cameraID) {

            mCameraManager = cameraManager;
            mCameraID = cameraID;

        }

        private CameraDevice.StateCallback mCameraCallback = new CameraDevice.StateCallback() {

            @Override
            public void onOpened(CameraDevice camera) {
                mCameraDevice = camera;
                createCameraPreviewSession();
            }

            @Override
            public void onDisconnected(CameraDevice camera) {
                mCameraDevice.close();
                mCameraDevice = null;
            }

            @Override
            public void onError(CameraDevice camera, int error) {
            }
        };


        private void createCameraPreviewSession() {

            SurfaceTexture texture = mImageView.getSurfaceTexture();

            texture.setDefaultBufferSize(180,180);
            Surface surface = new Surface(texture);

            try {
                final CaptureRequest.Builder builder =
                        mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

                builder.addTarget(surface);




                mCameraDevice.createCaptureSession(Arrays.asList(surface),
                        new CameraCaptureSession.StateCallback() {

                            @Override
                            public void onConfigured(CameraCaptureSession session) {
                                mCaptureSession = session;
                                try {
                                    mCaptureSession.setRepeatingRequest(builder.build(),null,null);
                                } catch (CameraAccessException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onConfigureFailed(CameraCaptureSession session) { }}, null );
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

        }




        public boolean isOpen() {
            if (mCameraDevice == null) {
                return false;
            } else {
                return true;
            }
        }

        public void openCamera() {
            try {
                if (getContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    mCameraManager.openCamera(mCameraID,mCameraCallback,null);

                }



            } catch (CameraAccessException e) {

            }
        }

        public void closeCamera() {

            if (mCameraDevice != null) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        }



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                classifier.close();
            }
        });
    }


    @Override
    public void onPause() {
        if(myCameras[CAMERA1].isOpen()){myCameras[CAMERA1].closeCamera();}
        if(myCameras[CAMERA2].isOpen()){myCameras[CAMERA2].closeCamera();}
        super.onPause();
    }


    private void startClassifer(){
        Bitmap bitmap = mImageView.getBitmap();
        bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);
        final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);
        resultTextView.setText(resultTextView.getText().toString() + results.toString());
    }
}