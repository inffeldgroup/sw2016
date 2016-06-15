package at.tugraz.inffeldgroup.dailypic.uitest;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import at.tugraz.inffeldgroup.dailypic.activities.FavouriteActivity;
import at.tugraz.inffeldgroup.dailypic.activities.MainActivity;

public class uitest_FavouriteActivity extends ActivityInstrumentationTestCase2<FavouriteActivity> {
    private Solo mySolo;

    public uitest_FavouriteActivity() {
        super(FavouriteActivity.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        mySolo = new Solo(getInstrumentation(),getActivity());
    }

    public void testFavouriteActivityInit(){
        mySolo.waitForActivity("FavouriteActivity");
    }
}
