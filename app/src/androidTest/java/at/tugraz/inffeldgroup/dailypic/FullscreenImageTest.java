package at.tugraz.inffeldgroup.dailypic;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import junit.framework.TestCase;

/**
 * Created by christian on 20.04.16.
 */
public class FullscreenImageTest extends ActivityInstrumentationTestCase2 {

    public FullscreenImageTest() {
        super(FullscreenImage.class);
    }

    private Solo mySolo;
    public void setUp() throws Exception {
        super.setUp();
        mySolo = new Solo(getInstrumentation(),getActivity());
    }

    public void tearDown() throws Exception {

    }

    public void testButton() {
        mySolo.clickOnButton("Close");
    }
}