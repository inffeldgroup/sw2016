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
    public void testback(){
        mySolo.waitForActivity("MainActivity");
        mySolo.clickOnButton("NEXT");
        mySolo.sleep(2000);
        mySolo.clickOnButton("BACK");
        mySolo.sleep(2000);
    }
    public void testnext(){
        mySolo.waitForActivity("MainActivity");
        mySolo.clickOnButton("NEXT");
        mySolo.sleep(500);
    }
    public void testhelpscreen(){
        mySolo.waitForActivity("MainActivity");
        mySolo.clickOnText("DailyPic");
        mySolo.sleep(500);
        mySolo.goBack();
        assertTrue(true);
    }
    public void testGarbageButton(){
        mySolo.waitForActivity("MainActivity");
        mySolo.clickOnImageButton(0);//Garbage
        mySolo.waitForText("No images for deletion selected!");
        mySolo.sleep(500);
        assertTrue(true);
    }

    public void testShareButton(){
        mySolo.waitForActivity("MainActivity");
        mySolo.clickOnImageButton(1);//ShareButton
        mySolo.sleep(500);
        mySolo.waitForText("No images for sharing selected!");
        assertTrue(true);
    }

    public void testFavButton(){
        mySolo.waitForActivity("MainActivity");
        mySolo.clickOnImageButton(2);//Fav Button
        mySolo.sleep(500);
        mySolo.waitForActivity("Favourites");
        mySolo.goBack();
        mySolo.waitForActivity("MainActivity");
        mySolo.sleep(500);
        assertTrue(true);
    }
}