package at.tugraz.inffeldgroup.dailypic.uitest;

import android.media.Image;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.robotium.solo.Solo;

import junit.framework.TestCase;

import at.tugraz.inffeldgroup.dailypic.activities.FullscreenActivity;

/**
 * Created by christian on 20.04.16.
 */
public class uitest_FullscreenActivity extends ActivityInstrumentationTestCase2 {

    public uitest_FullscreenActivity() {
        super(FullscreenActivity.class);
    }

    private Solo mySolo;
    public void setUp() throws Exception {
        super.setUp();
        mySolo = new Solo(getInstrumentation(),getActivity());
    }

    /*
    public void testCloseButton() {
        mySolo.clickOnButton("Close");
    }


    public void tearDown() throws Exception{
        mySolo.clickLongOnScreen(10,10);
        mySolo.waitForDialogToOpen();
        mySolo.waitForDialogToClose();
        mySolo.wait(1000);

    }
    */
}