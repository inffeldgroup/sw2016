package at.tugraz.inffeldgroup.dailypic.test;

import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;
import junit.framework.Assert;

import at.tugraz.inffeldgroup.dailypic.MainActivity;

import java.util.ArrayList;

import at.tugraz.inffeldgroup.dailypic.ImageFetcher;
import at.tugraz.inffeldgroup.dailypic.db.UriWrapper;

/**
 * Created by marco on 13/04/16.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public MainActivityTest() {
        super(MainActivity.class);
    }

    public void testPreviousFunction() {
        ImageFetcher img_fetcher = new ImageFetcher(this.getActivity());

        ArrayList<UriWrapper> image_paths_01 = img_fetcher.getNextRandomImages(6, getActivity());
        ArrayList<UriWrapper> image_paths_02 = img_fetcher.getNextRandomImages(6, getActivity());
        ArrayList<UriWrapper> image_paths_03 = img_fetcher.getPrevRandomImages(6, getActivity());


        Assert.assertTrue(image_paths_03.equals(image_paths_01) == true);
    }

    public void testNextFunction() {
        ImageFetcher img_fetcher = new ImageFetcher(this.getActivity());

        ArrayList<UriWrapper> image_paths_01 = img_fetcher.getNextRandomImages(6, getActivity());
        ArrayList<UriWrapper> image_paths_02 = img_fetcher.getNextRandomImages(6, getActivity());
        ArrayList<UriWrapper> image_paths_03 = img_fetcher.getPrevRandomImages(6, getActivity());
        ArrayList<UriWrapper> image_paths_04 = img_fetcher.getNextRandomImages(6, getActivity());


        Assert.assertTrue(image_paths_04.equals(image_paths_02) == true);
    }
}
