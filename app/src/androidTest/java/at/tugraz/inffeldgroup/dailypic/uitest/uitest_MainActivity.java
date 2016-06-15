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

    /*
    public void setUp() throws Exception {
        super.setUp();
        mySolo = new Solo(getInstrumentation(), getActivity());
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testdelete() {
        mySolo.waitForActivity("MainActivity");
        mySolo.clickOnMenuItem("Delete");
        mySolo.goBack();
    }




    public void testhelp(){
        mySolo.waitForActivity("MainActivity");
        mySolo.clickOnImage(0);
        mySolo.clickOnMenuItem("Help");
        mySolo.goBack();
    }

    public void testdatabase(){
        mySolo.waitForActivity("MainActivity");
        mySolo.clickOnImage(0);
        mySolo.clickOnMenuItem("database");
        mySolo.goBack();
    }

    public void testShare(){
        mySolo.waitForActivity("MainActivity");
       mySolo.clickOnMenuItem("Share");
        mySolo.goBack();

    }

public void testnext(){
    mySolo.waitForActivity("MainAvtivity");
    //mySolo.clickOnMenuItem("favourite");
    mySolo.scrollToSide(Solo.RIGHT);
    mySolo.sleep(600);
}

    public void testback(){
        mySolo.waitForActivity("MainActivity");
        mySolo.scrollToSide(Solo.RIGHT);
        mySolo.sleep(600);
        mySolo.scrollToSide(Solo.LEFT);
        mySolo.sleep(6000);

    }
*/
}
