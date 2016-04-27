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
public class ImageFetcher {
    private Activity activity;
    private int view_size;
    private int history_size;
    private ArrayList<Integer> seeds;
    private int current_seed_index;

    public ImageFetcher(Activity activity){
        this.activity = activity;
        this.view_size = 6;
        this.history_size = 3;

        this.current_seed_index = -1;
        this.seeds = new ArrayList<Integer>();
    }

    public ArrayList<Uri> getPreviousRandomImagePaths() {
        if(this.seeds.size() <= 0) {
            return null;
        }
        if(this.current_seed_index <= 0) {
            return null;
        }
        if(this.current_seed_index <= (this.seeds.size() - this.history_size)) {
            return null;
        }

        this.current_seed_index--;
        int seed = this.seeds.get(this.current_seed_index);
        ArrayList<Uri> image_paths = getRandomImagePaths(seed);
        return image_paths;
    }

    public ArrayList<Uri> getNextRandomImagePaths() {

        int seed;
        this.current_seed_index++;
        if(this.current_seed_index > this.seeds.size() - 1) {
            Random rand_gen = new Random();
            seed = rand_gen.nextInt();
            this.seeds.add(seed);
        }
        else {
            seed = this.seeds.get(this.current_seed_index);
        }

        ArrayList<Uri> image_paths = getRandomImagePaths(seed);
        return image_paths;
    }

    private ArrayList<Uri> getRandomImagePaths(int seed) {

        // Get picture directory
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[] { MediaStore.Images.Media.DATA };
        Cursor cursor = this.activity.getContentResolver().query(images, projection, "", null, "");

        ArrayList<String> paths_all = new ArrayList<String>();
        ArrayList<Uri> paths_specific = new ArrayList<Uri>();

        if(cursor.moveToFirst()) {
            int data_column = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

            String path_string;
            do {
                path_string = cursor.getString(data_column);
                paths_all.add(path_string);
            } while(cursor.moveToNext());

            Random rand_gen = new Random(seed);
            int image_index;
            Uri path_uri;
            for(int i = 0; i < this.view_size; i++) {
                image_index = Math.abs(rand_gen.nextInt()) % paths_all.size();
                path_uri = Uri.fromFile(new File(paths_all.get(image_index)));
                paths_specific.add(path_uri);
            }

        }

        cursor.close();

        if (paths_specific.size() == 0) {
            return null;
        }

        return paths_specific;
    }


}
