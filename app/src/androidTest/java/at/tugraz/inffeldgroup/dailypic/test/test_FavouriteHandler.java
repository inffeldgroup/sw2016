package at.tugraz.inffeldgroup.dailypic.test;

import android.test.ActivityInstrumentationTestCase2;

import junit.framework.Assert;

import java.util.ArrayList;

import at.tugraz.inffeldgroup.dailypic.FavouriteHandler;
import at.tugraz.inffeldgroup.dailypic.ImageFetcher;
import at.tugraz.inffeldgroup.dailypic.activities.MainActivity;
import at.tugraz.inffeldgroup.dailypic.db.DbDatasource;
import at.tugraz.inffeldgroup.dailypic.db.UriWrapper;

public class test_FavouriteHandler extends ActivityInstrumentationTestCase2<MainActivity>{
    public test_FavouriteHandler() {
        super(MainActivity.class);
    }

    public void testFavouriteHandler() {

        ImageFetcher imageFetcher = new ImageFetcher(getActivity());
        final ArrayList<UriWrapper> uriList = imageFetcher.getNextRandomImages(6, getActivity());

        UriWrapper img = DbDatasource.getInstance(getActivity()).getUriWrapper(uriList.get(0).getUri());
        boolean prevFavState = img.isFav();
        FavouriteHandler.toggleFavouriteState(getActivity(), uriList.get(0));

        img = DbDatasource.getInstance(getActivity()).getUriWrapper(uriList.get(0).getUri());
        Assert.assertTrue(img.isFav() != prevFavState);
    }
}
