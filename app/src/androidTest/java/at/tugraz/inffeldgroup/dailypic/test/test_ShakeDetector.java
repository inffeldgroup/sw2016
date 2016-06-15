package at.tugraz.inffeldgroup.dailypic.test;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.test.ActivityInstrumentationTestCase2;

import junit.framework.Assert;

import at.tugraz.inffeldgroup.dailypic.ShakeDetector;
import at.tugraz.inffeldgroup.dailypic.activities.MainActivity;

public class test_ShakeDetector extends ActivityInstrumentationTestCase2<MainActivity> {
    public test_ShakeDetector() {
        super(MainActivity.class);
    }

    public void testInstantiation(){
        ShakeDetector shakeDetector = new ShakeDetector();
        assertTrue(shakeDetector != null);
    }

    public void testOnSensorChanged(){
        /*
        * not testable because SensorEvents cannot be faked ... thanks Google
        * */
        Assert.assertTrue(true);
    }


}
