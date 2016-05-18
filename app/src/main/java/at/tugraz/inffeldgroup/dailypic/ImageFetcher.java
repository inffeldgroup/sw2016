package at.tugraz.inffeldgroup.dailypic;

import android.app.*;
import android.database.Cursor;
import android.net.*;
import android.provider.MediaStore;
import android.widget.Toast;
import android.content.Context;

import java.util.*;
import java.io.*;

import at.tugraz.inffeldgroup.dailypic.ImageGridViewAdapter.ViewHolder;
import at.tugraz.inffeldgroup.dailypic.db.DbDatasource;
import at.tugraz.inffeldgroup.dailypic.db.UriWrapper;

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

    public ArrayList<UriWrapper> getNextRandomImages(int size, Context context){

        ArrayList<UriWrapper> ret = new ArrayList<UriWrapper>();

        if (imgPaths.isEmpty()) {
            return ret;
        }

        int seed = seed_gen.nextInt();
        int image_index;
        Random rand_gen = new Random(seed);

        this.seedHistory.push(seed);
        for(int i = 0; i < size && i < imgPaths.size(); i++){
            image_index = Math.abs(rand_gen.nextInt()) % imgPaths.size();
            Uri uri = Uri.fromFile(new File(imgPaths.get(image_index)));

            UriWrapper image = DbDatasource.getInstance(context).getUriWrapper(uri);
            if (!ret.contains(image)) {
                ret.add(image);
            }
        }
        return ret;
    }

    public ArrayList<UriWrapper> getPrevRandomImages(int size, Context context){
        ArrayList<UriWrapper> ret = new ArrayList<UriWrapper>();

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
            Uri uri = Uri.fromFile(new File(imgPaths.get(image_index)));
            ret.add(DbDatasource.getInstance(context).getUriWrapper(uri));
        }

        return ret;
    }

    public void deleteImages(Map<Integer, ViewHolder> vh) {

        StringBuilder error_deleted = new StringBuilder();

        for (Map.Entry<Integer, ViewHolder> kvp : vh.entrySet()) {
            ViewHolder v = kvp.getValue();
            File f = new File(v.uri.getPath());
            String f_name = f.getName();
            boolean result = f.delete();
            if (result == false) {
                error_deleted.append(f_name + " ");
            }
        }

        if (error_deleted.length() > 0) {
            String files = error_deleted.toString().trim().replace(" ", ", ");
            Toast.makeText(activity, "Error deleting image(s): " + files, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(activity, "Successfully deleted images.", Toast.LENGTH_LONG).show();
        }
    }

    public void replaceDeletedImages(Map<Integer, ViewHolder> vh, Context context) {
        int size = vh.size();
        ArrayList<UriWrapper> new_imgs = getNextRandomImages(size, context);
        int i = 0;
        for (Map.Entry<Integer, ViewHolder> kvp : vh.entrySet()) {
            ViewHolder v = kvp.getValue();
            v.image.setImageURI(new_imgs.get(i++).getUri());
        }
    }
}