package at.tugraz.inffeldgroup.dailypic.uitest;

import android.graphics.PointF;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import junit.framework.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import at.tugraz.inffeldgroup.dailypic.ShakeDetector;
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
        mySolo.clickOnMenuItem("Delete");
        mySolo.goBack();
    }

    public void testShare(){
        mySolo.waitForActivity("MainActivity");
        mySolo.clickOnMenuItem("Share");
        mySolo.goBack();
    }

    public void testShare2(){
        mySolo.waitForActivity("MainActivity");
        mySolo.clickLongOnScreen(50, 600, 3000);
        mySolo.clickOnMenuItem("Share");
        mySolo.goBack();
    }

    public void testDelete() {
        mySolo.waitForActivity("MainActivity");
        mySolo.clickLongOnScreen(50, 600, 3000);
        mySolo.clickOnMenuItem("Delete");
        mySolo.waitForDialogToOpen();
        mySolo.clickOnButton("Yes");
    }

    public void testnext(){
        mySolo.waitForActivity("MainActivity");
        mySolo.scrollToSide(Solo.RIGHT);
        mySolo.sleep(600);
    }

    public void testback(){
        mySolo.waitForActivity("MainActivity");
        mySolo.scrollToSide(Solo.RIGHT);
        mySolo.sleep(600);
        mySolo.scrollToSide(Solo.LEFT);
        mySolo.sleep(600);
    }

    public void testhelp(){
        mySolo.waitForActivity("MainActivity");
        mySolo.clickOnImage(0);
        mySolo.clickOnMenuItem("Help");
        mySolo.waitForActivity("HelpScreenActivity");
        mySolo.goBack();
    }

    public void testlongclick() {
        mySolo.waitForActivity("MainActivity");
        mySolo.clickOnImage(2);
        mySolo.sleep(1000);
        mySolo.clickLongOnScreen(500, 750, 3000);
        mySolo.goBack();
    }
}
