package at.tugraz.inffeldgroup.dailypic.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import at.tugraz.inffeldgroup.dailypic.PushUpNotification;
import at.tugraz.inffeldgroup.dailypic.activities.MainActivity;

public class test_PushUpNotification extends ActivityInstrumentationTestCase2<MainActivity> {
    public test_PushUpNotification(){
        super(MainActivity.class);
    }


    public void testOnReceive(){
        PushUpNotification pushUpNotification = new PushUpNotification();
        Intent intent = new Intent();
        Activity activity = getActivity();
        Context context = activity.getApplicationContext();
        pushUpNotification.onReceive(context, intent);
        assertTrue(true);
    }
}
