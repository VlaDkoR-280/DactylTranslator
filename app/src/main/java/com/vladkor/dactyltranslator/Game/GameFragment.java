package com.vladkor.dactyltranslator.Game;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.camerakit.CameraKitView;
import com.squareup.picasso.Picasso;
import com.vladkor.dactyltranslator.R;

import java.util.Arrays;


public class GameFragment extends Fragment implements View.OnClickListener {


    private GameCreator gameCreator;


    private CameraService[] myCameras = null;
    private CameraManager mCameraManager = null;

    private TextureView mImageView;
    private CardView toggleCamera;
    private CardView captureImage;
    private CardView helpButton;
    private CardView trueAnswer;
    private ImageView trueImageAnswer;

    private Handler handler = new Handler();

    private TextView answerText;
    private TextView attemps;
    private boolean cameraFace = true;

    private final int CAMERA1   = 0;
    private final int CAMERA2   = 1;

    public GameFragment() {

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        gameCreator = new GameCreator();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game, container, false);
        mImageView = v.findViewById(R.id.camera_game);
        attemps = v.findViewById(R.id.attemps);
        answerText = v.findViewById(R.id.answer_text);
        captureImage = v.findViewById(R.id.CaptureBtn_game);
        toggleCamera = v.findViewById(R.id.ToggleBtn_game);
        helpButton = v.findViewById(R.id.help_button);
        trueAnswer = v.findViewById(R.id.true_answer);
        trueImageAnswer = v.findViewById(R.id.true_image_answer);
        trueAnswer.setVisibility(View.GONE);
        trueAnswer.setOnClickListener(this);
        helpButton.setOnClickListener(this);
        captureImage.setOnClickListener(this);
        toggleCamera.setOnClickListener(this);
        gameCreator.CreateEasyGame();
        answerText.setText(String.format("%s", gameCreator.getAnswer()));
        int a = gameCreator.getAttemps();
        attemps.setText(Integer.toString(a));

        if (getContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                ||
                (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        )
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

        mCameraManager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
        try{
            // Получение списка камер с устройства
            myCameras = new CameraService[mCameraManager.getCameraIdList().length];

            for (String cameraID : mCameraManager.getCameraIdList()) {
                int id = Integer.parseInt(cameraID);
                myCameras[id] = new CameraService(mCameraManager,cameraID);

            }
        }
        catch(CameraAccessException e){
            e.printStackTrace();
        }

        return v;
    }



    @Override
    public void onClick(View v) {
        if(v.getId()==captureImage.getId()){

        }else if (v.getId()==toggleCamera.getId()){
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
        }else if(v.getId() == helpButton.getId()){

            trueAnswer.setVisibility(View.VISIBLE);
            Picasso.get().load(gameCreator.getUriAnswer()).into(trueImageAnswer);

        }else if(v.getId() == trueAnswer.getId()){
            trueAnswer.setVisibility(View.GONE);
            toGameTransition(-5);
        }
    }

    private void toGameTransition(int offsetScore){
        Bundle bundle = new Bundle();
        bundle.putString("OFFSET", Integer.toString(offsetScore));
        Navigation.findNavController(getView()).navigate(R.id.action_gameFragment_to_gameTransitionFragment, bundle);

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
}