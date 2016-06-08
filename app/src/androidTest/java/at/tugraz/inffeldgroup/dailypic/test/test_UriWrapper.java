package at.tugraz.inffeldgroup.dailypic.test;

import android.test.ActivityInstrumentationTestCase2;

import junit.framework.Assert;

import java.util.ArrayList;

import at.tugraz.inffeldgroup.dailypic.ImageFetcher;
import at.tugraz.inffeldgroup.dailypic.activities.MainActivity;
import at.tugraz.inffeldgroup.dailypic.db.UriWrapper;

/**
 * Created by raphi on 02/06/2016.
 */
public class test_UriWrapper extends ActivityInstrumentationTestCase2<MainActivity> {
    public test_UriWrapper() {
        super(MainActivity.class);
    }


    public void testUriWrapperTranslation(){

        ImageFetcher img_fetcher = new ImageFetcher(getActivity());
        ArrayList<UriWrapper> images = img_fetcher.getNextRandomImages(6, getActivity());
        UriWrapper test_wrapper = images.get(0);

        Assert.assertTrue(test_wrapper.equals(images.get(0)));
        Assert.assertFalse(test_wrapper.equals(null));
        Object dummy = new Object();
        Assert.assertFalse(test_wrapper.equals(dummy));

    }

    public void testUriWrapperToString(){

        ImageFetcher img_fetcher = new ImageFetcher(getActivity());
        ArrayList<UriWrapper> images = img_fetcher.getNextRandomImages(6, getActivity());
        UriWrapper test_wrapper = images.get(0);
        String obj = images.get(0).getUri().toString();
        Assert.assertEquals(obj, test_wrapper.toString());


    }



}