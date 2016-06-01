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

    public void testDoNotShowSamePicturesInOneGrid() {
        ImageFetcher imgf = new ImageFetcher(this.getActivity());

        ArrayList<UriWrapper> img1 = imgf.getNextRandomImages(6, getActivity());

        for (int x = 0; x < 100; x++) {
            for (int i = 0; i < img1.size(); i++) {
                for (int j = 0; j < img1.size(); j++) {
                    if (j == i) continue;
                    assertTrue(img1.get(i).equals(img1.get(j)));
                }
            }
        }

    }

    public void testPreviousFunction() {
        ImageFetcher img_fetcher = new ImageFetcher(this.getActivity());

        ArrayList<UriWrapper> image_paths_01 = img_fetcher.getNextRandomImages(6, getActivity());
        ArrayList<UriWrapper> image_paths_02 = img_fetcher.getNextRandomImages(6, getActivity());


        // Assert.assertTrue(image_paths_03.equals(image_paths_01));
    }

    public void testNextFunction() {
        ImageFetcher img_fetcher = new ImageFetcher(this.getActivity());

        for (int i = 0; i < 100; i++)
        {
            ArrayList<UriWrapper> image_paths_01 = img_fetcher.getNextRandomImages(6, getActivity());
            ArrayList<UriWrapper> image_paths_02 = img_fetcher.getNextRandomImages(6, getActivity());
            ArrayList<UriWrapper> image_paths_04 = img_fetcher.getNextRandomImages(6, getActivity());
            Assert.assertFalse(image_paths_04.equals(image_paths_02));
        }

    }
}
