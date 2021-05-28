package com.vladkor.dactyltranslator;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.vladkor.dactyltranslator.Game.GameCreator;
import com.vladkor.dactyltranslator.Game.GameFragment;
import com.vladkor.dactyltranslator.Game.GameTransitionFragment;
import com.vladkor.dactyltranslator.list.Person;

public interface Movable {
    public void MoveTo(GameFragment fragment);
    public void MoveTo(GameTransitionFragment fragment);
    public void ReAuth();
    public Person GetMyPerson();
    public void SetMyPerson(Person person);
    public void SetMyPersonData(Person person);
    public void SetMyRefData(DatabaseReference ref);
}
