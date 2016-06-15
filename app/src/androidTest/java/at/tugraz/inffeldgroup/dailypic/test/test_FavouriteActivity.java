package at.tugraz.inffeldgroup.dailypic.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import at.tugraz.inffeldgroup.dailypic.activities.FavouriteActivity;
import at.tugraz.inffeldgroup.dailypic.activities.MainActivity;

public class test_FavouriteActivity extends ActivityInstrumentationTestCase2<MainActivity> {
    public test_FavouriteActivity(){
        super(MainActivity.class);
    }

    public void testFavouriteActivityInit(){
        Activity FavouriteActivity = new FavouriteActivity();
        assertTrue(true);
    }
}
