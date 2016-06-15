package at.tugraz.inffeldgroup.dailypic.test;

import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;
import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import at.tugraz.inffeldgroup.dailypic.ImageFetcher;
import at.tugraz.inffeldgroup.dailypic.ImageGridViewAdapter;
import at.tugraz.inffeldgroup.dailypic.activities.MainActivity;
import at.tugraz.inffeldgroup.dailypic.db.UriWrapper;

public class test_ImageFetcher  extends ActivityInstrumentationTestCase2<MainActivity> {
    public test_ImageFetcher() {
        super(MainActivity.class);
    }

    public void testdeleteerror() {
        ImageFetcher imgfetcher = new ImageFetcher(getActivity(), true);
        ArrayList<UriWrapper> lst = imgfetcher.getNextRandomImages(4, getActivity().getApplicationContext());

        Map<Integer, ImageGridViewAdapter.ViewHolder> delmap = new HashMap<>();
        for (int i = 0; i < 4; i++) {
            ImageGridViewAdapter.ViewHolder vh = new ImageGridViewAdapter.ViewHolder();
            vh.uri = lst.get(i).getUri();
            delmap.put(i, vh);
        }

        imgfetcher.deleteImages(delmap);

    }

    public void testdeletesuccess() {
        ImageFetcher imgfetcher = new ImageFetcher(getActivity(), true);
        Map<Integer, ImageGridViewAdapter.ViewHolder> delmap = new HashMap<>();
        imgfetcher.deleteImages(delmap);
    }

    public void testreloadimagesafterdeletion() {
        ImageFetcher imgfetcher = new ImageFetcher(getActivity(), true);
        ArrayList<UriWrapper> startup = imgfetcher.getNextRandomImages(4, getActivity().getApplicationContext());
        ArrayList<UriWrapper> next = imgfetcher.getNextRandomImages(4, getActivity().getApplicationContext());
        ImageGridViewAdapter gv = new ImageGridViewAdapter(getActivity().getApplicationContext(), startup, next);

        SparseBooleanArray spa = new SparseBooleanArray();
        spa.append(0, true);
        spa.append(1, true);
        spa.append(2, true);
        spa.append(3, true);

        Map<Integer, ImageGridViewAdapter.ViewHolder> vh = new HashMap<>();
        vh.put(0, null);
        vh.put(1, null);
        vh.put(2, null);
        vh.put(3, null);

        imgfetcher.replaceDeletedImages(spa, gv, vh, getActivity().getApplicationContext());
    }
}
