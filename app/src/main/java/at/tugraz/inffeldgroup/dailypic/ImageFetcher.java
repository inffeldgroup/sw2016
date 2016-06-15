package at.tugraz.inffeldgroup.dailypic;

import android.app.*;
import android.database.Cursor;
import android.net.*;
import android.provider.MediaStore;
import android.util.SparseBooleanArray;
import android.widget.GridView;
import android.widget.Toast;
import android.content.Context;

import java.util.*;
import java.io.*;

import at.tugraz.inffeldgroup.dailypic.ImageGridViewAdapter.ViewHolder;
import at.tugraz.inffeldgroup.dailypic.activities.MainActivity;
import at.tugraz.inffeldgroup.dailypic.db.DbDatasource;
import at.tugraz.inffeldgroup.dailypic.db.UriWrapper;

public class ImageFetcher extends Activity {
    private Activity activity;
    private Random seed_gen = new Random();

    private Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private ArrayList<String> imgPaths;
    private String[] projection;

    private boolean is_test_run;

    public ImageFetcher(Activity activity){
        this.activity = activity;
        this.projection = new String[]{MediaStore.Images.Media.DATA};
        this.imgPaths = new ArrayList<String>();

        Cursor cursor = this.activity.getContentResolver().query(images, projection, "", null, "");
        if(cursor.moveToFirst()){
            int data_column = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

            do{
                imgPaths.add(cursor.getString(data_column));
            }while(cursor.moveToNext());
        }
        cursor.close();
    }

    public ImageFetcher(Activity activity, boolean is_test){
        this.activity = activity;
        this.projection = new String[]{MediaStore.Images.Media.DATA};
        this.imgPaths = new ArrayList<String>();

        Cursor cursor = this.activity.getContentResolver().query(images, projection, "", null, "");
        if(cursor.moveToFirst()){
            int data_column = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

            do{
                imgPaths.add(cursor.getString(data_column));
            }while(cursor.moveToNext());
        }
        cursor.close();

        this.is_test_run = is_test;
    }

    public int getNumberOfPichtures() {
        return this.imgPaths.size();
    }

    public ArrayList<UriWrapper> getNextRandomImages(int size, Context context){

        ArrayList<UriWrapper> ret = new ArrayList<UriWrapper>();
        if (imgPaths.isEmpty()  | imgPaths.size() < MainActivity.NUMBER_OF_ITEMS) {
            Toast.makeText(activity, R.string.image_fetcher_toast, Toast.LENGTH_LONG).show();
            return ret;
        }

        int seed = seed_gen.nextInt();
        int image_index;
        Random rand_gen = new Random(seed);


        for(int i = 0; i < size && i < imgPaths.size(); i++){
            image_index = Math.abs(rand_gen.nextInt()) % imgPaths.size();
            Uri uri = Uri.fromFile(new File(imgPaths.get(image_index)));

            UriWrapper image = DbDatasource.getInstance(context).getUriWrapper(uri);
            if (!ret.contains(image)) {
                ret.add(image);
            }
            else
            {
                i--;
            }
        }
        return ret;
    }

    public void deleteImages(Map<Integer, ViewHolder> vh) {

        StringBuilder error_deleted = new StringBuilder();
        boolean toggle_test = this.is_test_run;

        for (Map.Entry<Integer, ViewHolder> kvp : vh.entrySet()) {
            ViewHolder v = kvp.getValue();
            File f = new File(v.uri.getPath());
            String f_name = f.getName() + " ";
            boolean result = false;
            if (toggle_test == true) {
                result = true;
                toggle_test = false;
            } else {
                result = this.is_test_run ? false : f.delete();
                if (this.is_test_run == true) {
                    toggle_test = true;
                }
            }

            if (!result) {
                error_deleted.append(f_name);
            }
        }

        if (error_deleted.length() > 0) {
            String files = error_deleted.toString().trim().replace(" ", ", ");
            Toast.makeText(activity, R.string.image_fetcher_toast_delete_error+ files, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(activity, R.string.image_fetcher_toast_delete_success, Toast.LENGTH_LONG).show();
        }
    }

    public void replaceDeletedImages(SparseBooleanArray checked, ImageGridViewAdapter gv, Map<Integer, ViewHolder> vh, Context context) {
        ArrayList<UriWrapper> current_uris = gv.getUriList();
        int size = vh.size();
        ArrayList<UriWrapper> new_imgs = getNextRandomImages(size, context);
        int x = 0;
        for (int i = 0; i < current_uris.size(); i++) {
            if (checked.get(i) == true) {
                current_uris.set(i, new_imgs.get(x++));
            }
        }

        gv.setNewImages(current_uris);
    }
}