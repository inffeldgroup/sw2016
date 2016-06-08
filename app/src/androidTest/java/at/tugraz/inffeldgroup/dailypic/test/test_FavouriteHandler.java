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

//    public void testFavouriteHandler() {
//
//        ImageFetcher imageFetcher = new ImageFetcher(getActivity());
//        final ArrayList<UriWrapper> uriList = imageFetcher.getNextRandomImages(6, getActivity());
//
//        FavouriteHandler fh = new FavouriteHandler();
//        fh.toggleFavouriteState(getActivity(), uriList.get(0));
//        fh.getFavouriteState(getActivity(), uriList.get(0));
//        Assert.assertTrue(fh.getFavouriteState(getActivity(), uriList.get(0)));
//        fh.toggleFavouriteState(getActivity(), uriList.get(0));
//        Assert.assertFalse(fh.getFavouriteState(getActivity(), uriList.get(0)));
//    }

    public void testFavouriteHandler(){

        ImageFetcher img_fetcher = new ImageFetcher(getActivity());
        DbDatasource db = DbDatasource.getInstance(getActivity());
        ArrayList<UriWrapper> uri_list = img_fetcher.getNextRandomImages(6, getActivity());
        db.insert(uri_list.get(0));

        uri_list.get(0).setFavourite(false);
        FavouriteHandler fav_handler = new FavouriteHandler();
        fav_handler.toggleFavouriteState(getActivity(), uri_list.get(0));
        Assert.assertTrue(uri_list.get(0).isFav());
        Assert.assertTrue(fav_handler.getFavouriteState(getActivity(), uri_list.get(0)));

        uri_list.get(0).setFavourite(true);
        fav_handler.toggleFavouriteState(getActivity(), uri_list.get(0));
        Assert.assertFalse(uri_list.get(0).isFav());
        Assert.assertFalse(fav_handler.getFavouriteState(getActivity(), uri_list.get(0)));



    }
}
