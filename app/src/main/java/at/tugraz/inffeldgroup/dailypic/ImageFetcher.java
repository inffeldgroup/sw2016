package at.tugraz.inffeldgroup.dailypic;

import android.app.*;
import android.database.Cursor;
import android.net.*;
import android.provider.MediaStore;

import java.util.*;
import java.io.*;

/**
 * Created by markus on 13/04/16.
 */

public class ImageFetcher{
    private Activity activity;
    private Stack<Integer> seedHistory;
    private Random seed_gen = new Random();

    // TODO: handle files from DailyPicFavs folder in a different way, since they should be marked with a star in the usual view!

    private Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private ArrayList<String> imgPaths;
    private String[] projection;

    public ImageFetcher(Activity activity){
        this.activity = activity;
        this.seedHistory = new Stack<Integer>();
        this.projection = new String[]{MediaStore.Images.Media.DATA};
        this.imgPaths = new ArrayList<String>();

        Cursor cursor = this.activity.getContentResolver().query(images, projection, "", null, "");
        if(cursor.moveToFirst()){
            int data_column = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

            do{
                imgPaths.add(cursor.getString(data_column));
            }while(cursor.moveToNext());
        }
    }

    public ArrayList<Uri> getNextRandomImages(int size){
        ArrayList<Uri> ret = new ArrayList<Uri>();
        int seed = seed_gen.nextInt();
        int image_index;
        Random rand_gen = new Random(seed);

        this.seedHistory.push(seed);
        for(int i = 0; i < size; i++){
            image_index = Math.abs(rand_gen.nextInt()) % imgPaths.size();
            ret.add(Uri.fromFile(new File(imgPaths.get(image_index))));
        }

        return ret;
    }

    public ArrayList<Uri> getPrevRandomImages(int size){
        ArrayList<Uri> ret = new ArrayList<Uri>();
        int seed;
        int image_index;

        if(seedHistory.size() > 1){
            this.seedHistory.pop();
            seed = this.seedHistory.lastElement();
        }else{
            seed = this.seedHistory.get(0);
        }
        Random rand_gen = new Random(seed);

        for(int i = 0; i < size; i++){
            image_index = Math.abs(rand_gen.nextInt()) % imgPaths.size();
            ret.add(Uri.fromFile(new File(imgPaths.get(image_index))));
        }

        return ret;
    }
}