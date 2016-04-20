package at.tugraz.inffeldgroup.dailypic.uitest;

import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;

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
    public void testButtons(){
        mySolo.waitForActivity("MainActivity");
        mySolo.clickOnImageButton(0);//Garbage
        assertTrue(true);
        mySolo.clickOnImageButton(1);//Share
        mySolo.sleep(300);
        assertTrue(true);
        mySolo.goBack();
        mySolo.sleep(200);
        KeyEvent back = new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_BACK);
        getActivity().dispatchKeyEvent(back);
        /*mySolo.clickOnImageButton(2);//fav
        assertTrue(true);
        mySolo.clickOnButton("back");
        assertTrue(true);
        mySolo.clickOnButton("next");
        assertTrue(true);*/





        mySolo.sleep(50000);

        //while (true);
    }
}