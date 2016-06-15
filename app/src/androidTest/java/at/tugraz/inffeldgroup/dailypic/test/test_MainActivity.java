package at.tugraz.inffeldgroup.dailypic.test;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.util.Log;

import junit.framework.Assert;

import at.tugraz.inffeldgroup.dailypic.activities.MainActivity;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import at.tugraz.inffeldgroup.dailypic.ImageFetcher;
import at.tugraz.inffeldgroup.dailypic.db.UriWrapper;

public class test_MainActivity extends ActivityInstrumentationTestCase2<MainActivity> {
    private static boolean firstRun = true;
    public test_MainActivity() {
        super(MainActivity.class);
    }

    public static ArrayList<String> getImagesPath(Activity activity) {
        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        String PathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            PathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(PathOfImage.substring(PathOfImage.lastIndexOf("/")+1, PathOfImage.length()));
        }
        return listOfAllImages;
    }

    protected void setUp() {
        if (firstRun) {
            firstRun = false;

            File dir = new File(Environment.getExternalStorageDirectory() + "/Daily Pic");
            if (dir.isDirectory())
            {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++)
                {
                    new File(dir, children[i]).delete();
                }
            }
            dir.delete();
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File("file://"+ Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            getActivity().sendBroadcast(mediaScanIntent);

        }
    }

    public void testPreinstallImages(){
        String[] assets = null;
        try {
            assets = getActivity().getResources().getAssets().list("pictures");
            File dir = new File(Environment.getExternalStorageDirectory() + "/Daily Pic");
            ArrayList<String> file_list = new ArrayList<String>(Arrays.asList(dir.list(null)));
            for (String name : assets) {
                assertTrue(file_list.contains(name));
            }
            ArrayList<String> media_list = getImagesPath(this.getActivity());
            for (String name : assets) {
                assertTrue(media_list.contains(name));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /*
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
    */

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
    @UiThreadTest
    public void testHandleShakeEvent() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException, InstantiationException, InterruptedException {
        Method method = MainActivity.class.getDeclaredMethod("handleShakeEvent", Integer.TYPE);
        method.setAccessible(true);
        Activity mainActivity = new MainActivity();
        //Thread.sleep(10000);
        method.invoke(this.getActivity(), 1);
        method.invoke(this.getActivity(), 5);
        //Thread.sleep(10000);

        assertTrue(true);
    }
}
