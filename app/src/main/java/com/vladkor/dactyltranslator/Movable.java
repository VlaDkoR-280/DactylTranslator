package com.vladkor.dactyltranslator;

import com.google.firebase.database.DatabaseReference;
import com.vladkor.dactyltranslator.Game.GameFragment;
import com.vladkor.dactyltranslator.Game.GameTransitionFragment;
import com.vladkor.dactyltranslator.TopPlacesList.Person;

public interface Movable {
    public void MoveTo(GameFragment fragment);
    public void MoveTo(GameTransitionFragment fragment);
    public void MoveTo(GuideFragment fragment);
    public void ReAuth();
    public Person GetMyPerson();
    public void SetMyPerson(Person person);
    public void SetMyPersonData();
    public void SetMyRefData(DatabaseReference ref);
}
