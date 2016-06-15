package at.tugraz.inffeldgroup.dailypic.test;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import junit.framework.Assert;

import java.util.ArrayList;

import javax.sql.DataSource;

import at.tugraz.inffeldgroup.dailypic.ImageFetcher;
import at.tugraz.inffeldgroup.dailypic.activities.MainActivity;
import at.tugraz.inffeldgroup.dailypic.db.DbDatasource;
import at.tugraz.inffeldgroup.dailypic.db.SqlLiteHelper;
import at.tugraz.inffeldgroup.dailypic.db.UriWrapper;

/**
 * Created by raphi on 02/06/2016.
 */

public class test_DB extends ActivityInstrumentationTestCase2<MainActivity> {
    public test_DB() {
        super(MainActivity.class);
    }


    public void testDatabaseInsertDelete() {

        DbDatasource test_db = DbDatasource.getInstance(getActivity());
        ImageFetcher img_fetcher = new ImageFetcher(getActivity());
        ArrayList<UriWrapper> images = img_fetcher.getNextRandomImages(6, getActivity());
        test_db.insert(images.get(0));
        Assert.assertTrue(test_db.checkIfExists(images.get(0)));
        test_db.delete(images.get(0));
        Assert.assertFalse(test_db.checkIfExists(images.get(0)));
    }

    public void testDatabaseGetAllFavourites() {

        DbDatasource test_db = DbDatasource.getInstance(getActivity());
        int databaseSize = test_db.getAllFavorites().size();
        //Assert.assertTrue(test_db.getAllFavorites().isEmpty());
        ImageFetcher img_fetcher = new ImageFetcher(getActivity());
        ArrayList<UriWrapper> images = img_fetcher.getNextRandomImages(6, getActivity());

        test_db.insert(images.get(0));
        Assert.assertTrue(test_db.checkIfExists(images.get(0)));
        images.get(0).setFavourite(true);
        test_db.update(images.get(0));
        ArrayList<UriWrapper> favs = test_db.getAllFavorites();
        Assert.assertTrue(favs.contains(images.get(0)));
        test_db.delete(images.get(0));
        Assert.assertTrue(test_db.getAllFavorites().size() == databaseSize);
    }

    public void testGetData() {
        DbDatasource dbs = DbDatasource.getInstance(getActivity());
        ArrayList<Cursor> ret = SqlLiteHelper.getInstance(getActivity().getApplicationContext()).getData("SELECT * FROM Images;");
        assertTrue(ret.size() != 0);
        ret = SqlLiteHelper.getInstance(getActivity().getApplicationContext()).getData("");
        assertTrue(ret.size() != 0);
    }




}
