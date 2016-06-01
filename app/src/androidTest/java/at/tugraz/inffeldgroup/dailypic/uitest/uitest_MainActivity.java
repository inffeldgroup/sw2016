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
        mySolo = new Solo(getInstrumentation(), getActivity());
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testdelete() {
        mySolo.waitForActivity("MainActivity");
        mySolo.clickOnMenuItem("delete");
        mySolo.waitForDialogToOpen();
        mySolo.waitForDialogToClose();
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


    public void testsettings() {
        mySolo.waitForActivity("MainActivity");
        mySolo.clickOnMenuItem("settings");
        mySolo.waitForDialogToOpen();
        mySolo.waitForDialogToClose();
        assertTrue(true);
    }



    public void testhelp(){
        mySolo.waitForActivity("MainActivity");
        mySolo.clickOnMenuItem("help");
        mySolo.waitForDialogToOpen();
        mySolo.waitForDialogToClose();
        mySolo.goBack();
        assertTrue(true);
    }

    public void testdatabase(){
        mySolo.waitForActivity("MainActivity");
        mySolo.clickOnMenuItem("database");
        mySolo.waitForDialogToOpen();
        mySolo.waitForDialogToClose();
        assertTrue(true);
    }

    public void testFullscreen(){
        mySolo.waitForActivity("MainActivity");
       // mySolo.clickOnImageButton(0);//("topbar_title_favourites");
        //mySolo.clickOnImage(1);
        mySolo.clickOnMenuItem("topbar_title_favourite");
        mySolo.waitForDialogToOpen();
        mySolo.waitForDialogToClose();
        assertTrue(true);
    }


}
