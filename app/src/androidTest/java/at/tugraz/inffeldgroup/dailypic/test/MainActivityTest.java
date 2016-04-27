package at.tugraz.inffeldgroup.dailypic.test;

import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;
import junit.framework.Assert;
import at.tugraz.inffeldgroup.dailypic.MainActivity;

import java.io.File;
import java.util.ArrayList;

import at.tugraz.inffeldgroup.dailypic.ImageHandler;

/**
 * Created by marco on 13/04/16.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public MainActivityTest() {
        super(MainActivity.class);
    }

    public void testPreviousFunction() {
        ImageHandler img_fetcher = new ImageHandler(this.getActivity());

        ArrayList<Uri> image_paths_01 = img_fetcher.getNextRandomImagePaths();
        ArrayList<Uri> image_paths_02 = img_fetcher.getNextRandomImagePaths();
        ArrayList<Uri> image_paths_03 = img_fetcher.getPreviousRandomImagePaths();


        Assert.assertTrue(image_paths_03.equals(image_paths_01) == true);
    }

    public void testNextFunction() {
        ImageHandler img_fetcher = new ImageHandler(this.getActivity());

        ArrayList<Uri> image_paths_01 = img_fetcher.getNextRandomImagePaths();
        ArrayList<Uri> image_paths_02 = img_fetcher.getNextRandomImagePaths();
        ArrayList<Uri> image_paths_03 = img_fetcher.getPreviousRandomImagePaths();
        ArrayList<Uri> image_paths_04 = img_fetcher.getNextRandomImagePaths();


        Assert.assertTrue(image_paths_04.equals(image_paths_02) == true);
    }

    public void testDeleteFunction() {

        //Assert.assertTrue(blabla == true);
    }

    public void testReplaceFunction() {

        //Assert.assertTrue(blabla == true);
    }

    public void testReturnedPaths() {
        ImageHandler ih = new ImageHandler(this.getActivity());
        ArrayList<Uri> uris = ih.getNextRandomImagePaths();
        Assert.assertTrue(uris == null);
        Assert.assertTrue(uris.size() == 6);
        for (Uri u : uris) {
            String path = u.toString();
            Assert.assertTrue(new File(path).exists() == true);
        }
    }

}
