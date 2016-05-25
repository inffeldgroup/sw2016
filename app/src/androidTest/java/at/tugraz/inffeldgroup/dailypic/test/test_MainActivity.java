package at.tugraz.inffeldgroup.dailypic.test;

import android.test.ActivityInstrumentationTestCase2;
import junit.framework.Assert;

import at.tugraz.inffeldgroup.dailypic.activities.MainActivity;

import java.util.ArrayList;

import at.tugraz.inffeldgroup.dailypic.ImageFetcher;
import at.tugraz.inffeldgroup.dailypic.db.UriWrapper;

public class test_MainActivity extends ActivityInstrumentationTestCase2<MainActivity> {

    public test_MainActivity() {
        super(MainActivity.class);
    }

    public void testPreviousFunction() {
        ImageFetcher img_fetcher = new ImageFetcher(this.getActivity());

        ArrayList<UriWrapper> image_paths_01 = img_fetcher.getNextRandomImages(6, getActivity());
        ArrayList<UriWrapper> image_paths_02 = img_fetcher.getNextRandomImages(6, getActivity());
        ArrayList<UriWrapper> image_paths_03 = img_fetcher.getPrevRandomImages(6, getActivity());


        Assert.assertTrue(image_paths_03.equals(image_paths_01));
    }

    public void testNextFunction() {
        ImageFetcher img_fetcher = new ImageFetcher(this.getActivity());

        for (int i = 0; i < 10000; i++)
        {
            ArrayList<UriWrapper> image_paths_01 = img_fetcher.getNextRandomImages(6, getActivity());
            ArrayList<UriWrapper> image_paths_02 = img_fetcher.getNextRandomImages(6, getActivity());
            ArrayList<UriWrapper> image_paths_03 = img_fetcher.getPrevRandomImages(6, getActivity());
            ArrayList<UriWrapper> image_paths_04 = img_fetcher.getNextRandomImages(6, getActivity());
            Assert.assertFalse(image_paths_04.equals(image_paths_02));
        }

    }
}
