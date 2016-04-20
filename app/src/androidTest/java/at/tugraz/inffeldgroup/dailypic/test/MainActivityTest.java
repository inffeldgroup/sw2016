package at.tugraz.inffeldgroup.dailypic.test;

import android.app.Activity;
import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityTestCase;
import junit.framework.Assert;
import at.tugraz.inffeldgroup.dailypic.MainActivity;

import java.util.ArrayList;

import at.tugraz.inffeldgroup.dailypic.ImageFetcher;

/**
 * Created by marco on 13/04/16.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public MainActivityTest() {
        super(MainActivity.class);
    }

    public void testArrayNotNull(){
        ImageFetcher.getInstance().init(this.getActivity());

        ArrayList<Uri> image_paths = ImageFetcher.getInstance().getNextRandomImagePaths();
        Assert.assertTrue(image_paths != null);
    }

    public void testPreviousFunction() {
        ImageFetcher.getInstance().init(this.getActivity());

        ArrayList<Uri> image_paths_01 = ImageFetcher.getInstance().getNextRandomImagePaths();
        ArrayList<Uri> image_paths_02 = ImageFetcher.getInstance().getNextRandomImagePaths();
        ArrayList<Uri> image_paths_03 = ImageFetcher.getInstance().getPreviousRandomImagePaths();


        Assert.assertTrue(image_paths_03.equals(image_paths_01) == true);
    }

    public void testNextFunction() {
        ImageFetcher.getInstance().init(this.getActivity());

        ArrayList<Uri> image_paths_01 = ImageFetcher.getInstance().getNextRandomImagePaths();
        ArrayList<Uri> image_paths_02 = ImageFetcher.getInstance().getNextRandomImagePaths();
        ArrayList<Uri> image_paths_03 = ImageFetcher.getInstance().getPreviousRandomImagePaths();
        ArrayList<Uri> image_paths_04 = ImageFetcher.getInstance().getNextRandomImagePaths();


        Assert.assertTrue(image_paths_04.equals(image_paths_02) == true);
    }

}
