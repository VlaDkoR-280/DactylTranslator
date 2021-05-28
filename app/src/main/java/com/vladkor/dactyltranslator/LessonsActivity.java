package com.vladkor.dactyltranslator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vladkor.dactyltranslator.Game.GameFragment;
import com.vladkor.dactyltranslator.Game.GameTransitionFragment;
import com.vladkor.dactyltranslator.list.Person;


public class LessonsActivity extends AppCompatActivity implements Movable {
    private static final int RC_SIGN_IN = 123;
    private ProfilePersonFragment f1;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private Person myPerson;
    private DatabaseReference myRef;

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
            signIn();
        }else{
            loadPersonFragment();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                signIn();

            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null){
                                loadPersonFragment();
                            }

                        } else {
                            signIn();
                        }
                    }
                });
    }

    private void loadPersonFragment(){

        f1 = new ProfilePersonFragment();
        f1.setMovable(this);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction()
                .setReorderingAllowed(true);
        fragmentTransaction.replace(R.id.fragment, f1);
        fragmentTransaction.commit();
    }

    @Override
    public void MoveTo(GameFragment fragment) {
        fragment.setController(this);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction()
                .setReorderingAllowed(true);
        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void MoveTo(GameTransitionFragment fragment) {
        fragment.setController(this);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction()
                .setReorderingAllowed(true);
        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void ReAuth() {
        signIn();
    }

    @Override
    public Person GetMyPerson() {
        return myPerson;
    }

    @Override
    public void SetMyPerson(Person person) {
        myPerson = person;
    }

    @Override
    public void SetMyPersonData() {
        if(myRef!=null && myPerson != null){
            myRef.child(myPerson.getID()).setValue(myPerson);
        }
    }

    @Override
    public void SetMyRefData(DatabaseReference ref) {
        myRef = ref;
    }

}