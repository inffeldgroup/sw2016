package at.tugraz.inffeldgroup.dailypic;

import android.app.*;
import android.database.Cursor;
import android.net.*;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.*;
import java.io.*;

/**
 * Created by markus on 13/04/16.
 */
public class ImageHandler {
    private Activity activity;
    private int view_size;
    private int history_size;
    private ArrayList<ArrayList<Integer>> id_sets;
    private int current_id_sets_index;

    public ImageHandler(Activity activity){
        this.activity = activity;
        this.view_size = 6;
        this.history_size = 3;

        this.current_id_sets_index = -1;
        this.id_sets = new ArrayList<ArrayList<Integer>>();
    }

    public ArrayList<Uri> getPreviousRandomImagePaths() {
        if(this.id_sets.size() <= 0) {
            return null;
        }
        if(this.current_id_sets_index <= 0) {
            return null;
        }
        if(this.current_id_sets_index <= (this.id_sets.size() - this.history_size)) {
            return null;
        }

        this.current_id_sets_index--;

        ArrayList<Integer> id_set = this.id_sets.get(this.current_id_sets_index);
        ArrayList<Uri> image_paths = getRandomImagePaths(id_set);

        return image_paths;
    }

    public ArrayList<Uri> getNextRandomImagePaths() {

        ArrayList<Integer> id_set = null;
        this.current_id_sets_index++;

        if(this.current_id_sets_index <= this.id_sets.size() - 1) {

            id_set = this.id_sets.get(this.current_id_sets_index);
        }

        ArrayList<Uri> image_paths = getRandomImagePaths(id_set);

        return image_paths;
    }

    public List<Uri> replaceDeletedImg(Map<Integer, View> imgs_del) {
        ArrayList<Uri> new_imgs = new ArrayList<Uri>();
        ArrayList<Integer> cur_set = this.id_sets.get(this.current_id_sets_index);

        for (Map.Entry<Integer, View> kvp : imgs_del.entrySet()) {
            // delete image using kvp.getValue()
            ImageView iv = (ImageView) kvp.getValue();
            String path = (String) iv.getTag();
            new File(path).delete();

            Random rgen = new Random();
            int new_idx = rgen.nextInt();

            cur_set.set(kvp.getKey(), new_idx);
        }

        new_imgs = this.getRandomImagePaths(cur_set);

        return new_imgs;
    }

    private ArrayList<Uri> getRandomImagePaths(ArrayList<Integer> id_set) {

        ArrayList<Integer> new_entry;

        if (id_set == null) {
            new_entry = new ArrayList<Integer>();

            Random rand_gen = new Random();
            int seed = rand_gen.nextInt();
            Random rand_gen2 = new Random(seed);
            int image_index;

            // The values are not reduced modulo image size!
            for(int i = 0; i < this.view_size; i++) {
                image_index = Math.abs(rand_gen2.nextInt());
                new_entry.add(image_index);
            }

            // Add new id_set
            this.id_sets.add(new_entry);
        } else {
            new_entry = id_set;
        }

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


            int image_index;
            Uri path_uri;
            for(int i = 0; i < new_entry.size(); i++) {
                image_index = new_entry.get(i) % paths_all.size();
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
