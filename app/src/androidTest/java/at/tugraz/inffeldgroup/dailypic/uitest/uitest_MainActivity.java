package at.tugraz.inffeldgroup.dailypic.uitest;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import at.tugraz.inffeldgroup.dailypic.activities.MainActivity;

/**
 * Created by Merkus on 20.04.2016.
 */
public class uitest_MainActivity extends ActivityInstrumentationTestCase2<MainActivity> {

    private Solo mySolo;

    public uitest_MainActivity() {
        super(MainActivity.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        mySolo = new Solo(getInstrumentation(),getActivity());
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }
    public void testGarbageButton(){
        mySolo.waitForActivity("MainActivity");
        mySolo.clickOnImageButton(0);//Garbage
        mySolo.sleep(1000);
        assertTrue(true);
    }

    public void testShareButton(){
        mySolo.waitForActivity("MainActivity");
        mySolo.clickOnImageButton(1);//ShareButton
        mySolo.sleep(1000);
        assertTrue(true);
    }

    public void testFavButton(){
        mySolo.waitForActivity("MainActivity");
        mySolo.clickOnImageButton(3);//Fav Button
        mySolo.sleep(1000);
        mySolo.waitForActivity("Favourites");
        mySolo.goBack();
        mySolo.waitForActivity("MainActivity");
        mySolo.sleep(1000);
        assertTrue(true);
    }
}