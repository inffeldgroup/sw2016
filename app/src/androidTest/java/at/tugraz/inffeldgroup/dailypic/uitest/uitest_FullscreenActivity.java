package at.tugraz.inffeldgroup.dailypic.uitest;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.robotium.solo.Solo;

import junit.framework.TestCase;

import java.util.ArrayList;

import at.tugraz.inffeldgroup.dailypic.ImageFetcher;
import at.tugraz.inffeldgroup.dailypic.activities.FullscreenActivity;
import at.tugraz.inffeldgroup.dailypic.db.UriWrapper;

/**
 * Created by christian on 20.04.16.
 */
public class uitest_FullscreenActivity extends ActivityInstrumentationTestCase2<FullscreenActivity> {

    public uitest_FullscreenActivity() {
        super(FullscreenActivity.class);
    }
    /*
    private Solo mySolo;
    public void setUp() throws Exception {

        ImageFetcher imageFetcher = new ImageFetcher(getActivity());
        final ArrayList<UriWrapper> uriList = imageFetcher.getNextRandomImages(6, getActivity());

        Intent intent = new Intent();
        intent.setData(uriList.get(0).getUri());
        setActivityIntent(intent);

        super.setUp();
        mySolo = new Solo(getInstrumentation(),getActivity());
    }

    public void testTearDown() throws Exception{
        mySolo.clickLongOnScreen(10,10);
        mySolo.waitForDialogToOpen();
        mySolo.waitForDialogToClose();
    }
    */
}