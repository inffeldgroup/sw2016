package at.tugraz.inffeldgroup.dailypic.uitest;

import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.ImageButton;

import com.robotium.solo.Solo;

import at.tugraz.inffeldgroup.dailypic.R;
import at.tugraz.inffeldgroup.dailypic.MainActivity;
import junit.framework.TestCase;

import at.tugraz.inffeldgroup.dailypic.MainActivity;

/**
 * Created by Merkus on 20.04.2016.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private Solo mySolo;

    public MainActivityTest() {
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
        assertTrue(true);
    }

    public void testShareButton(){
        mySolo.waitForActivity("MainActivity");
        mySolo.clickOnImageButton(1);//ShareButton
        assertTrue(true);
    }

    public void testFavButton(){
        mySolo.waitForActivity("MainActivity");
        mySolo.clickOnImageButton(3);//Fav Button
        mySolo.waitForActivity("Favourite");
        mySolo.goBack();
        mySolo.waitForActivity("MainActivity");
        assertTrue(true);
    }
}