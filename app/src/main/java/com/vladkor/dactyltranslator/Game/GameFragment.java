package com.vladkor.dactyltranslator.Game;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.camerakit.CameraKitView;
import com.squareup.picasso.Picasso;
import com.vladkor.dactyltranslator.Movable;
import com.vladkor.dactyltranslator.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private GameCreator gameCreator;
    private Movable controller;



    private CameraKitView cameraKitView;
    private CardView toggleCamera;
    private CardView captureImage;
    private CardView helpButton;
    private CardView trueAnswer;
    ImageView trueImageAnswer;

    private String mParam1;
    private String mParam2;
    private TextView answerText;
    private TextView attemps;

    public GameFragment() {
        // Required empty public constructor
    }

    public static GameFragment newInstance(String param1, String param2) {
        GameFragment fragment = new GameFragment();
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
        gameCreator = new GameCreator();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game, container, false);
        attemps = v.findViewById(R.id.attemps);
        cameraKitView = v.findViewById(R.id.camera_game);
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
        cameraKitView.onStart();
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

    public void setController(Movable movable){
        controller = movable;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==captureImage.getId()){
            cameraKitView.captureImage(new CameraKitView.ImageCallback() {
                @Override
                public void onImage(CameraKitView cameraKitView, byte[] bytes) {

                    String myAnswer = "";
                    if(gameCreator.checkAnswer(myAnswer)){
                        GameTransitionFragment gtf = new GameTransitionFragment();

                        toGameTransition(10);
                    }
                    int a = gameCreator.getAttemps();
                    attemps.setText(Integer.toString(a));
                    if (a <= 0){
                        GameTransitionFragment gtf = new GameTransitionFragment();
                        toGameTransition(-5);
                    }
                }
            });
        }else if (v.getId()==toggleCamera.getId()){
            cameraKitView.toggleFacing();
        }else if(v.getId() == helpButton.getId()){
            //TODO показать правильный ответ.
            trueAnswer.setVisibility(View.VISIBLE);
            Picasso.get().load(gameCreator.getUriAnswer()).into(trueImageAnswer);

        }else if(v.getId() == trueAnswer.getId()){
            trueAnswer.setVisibility(View.GONE);
            toGameTransition(-5);
        }
    }

    private void toGameTransition(int offsetScore){
        GameTransitionFragment gameTransitionFragment = new GameTransitionFragment(offsetScore);
        controller.MoveTo(gameTransitionFragment);

    }
}