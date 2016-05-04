package at.tugraz.inffeldgroup.dailypic;

import android.content.Context;
import android.net.*;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.*;

/**
 * Created by marco on 04/05/16.
 */
public class FavouriteHandler {

    private static final String folder_name = "/DailyPicFavs/";
    private static final String base_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Environment.DIRECTORY_PICTURES + FavouriteHandler.folder_name;

    public FavouriteHandler() {

    }

    public void moveImgsToFavFolder(Context context, List<Uri> favs) {

        File f = new File(FavouriteHandler.base_path);

        if (f.exists() == false) {
            if (f.mkdir() == false) {
                Log.e("[DAYLIPIC]", "Creating directory failed");
                return;
            }
        }

        for (Uri uri : favs) {

            //Log.d("[DAYLIPIC]", "Environment path: " + FavouriteHandler.base_path);
            File src = new File(uri.getPath());
            File dest = new File(FavouriteHandler.base_path + src.getName());
            //Log.d("[DAYLIPIC]", "src: " + src.getPath());
            //Log.d("[DAYLIPIC]", "destination: " + dest.getPath());

            if (src.renameTo(dest) == false) {
                Log.e("[DAYLIPIC]", "Error moving file to favourites folder: " + src.getAbsolutePath());
            } else {
                Log.d("[DAYLIPIC]", "Moved image to favourites folder: " + src.getName());
            }
        }

    }

}
