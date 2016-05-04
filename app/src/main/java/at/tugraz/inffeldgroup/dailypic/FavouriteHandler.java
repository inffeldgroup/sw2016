package at.tugraz.inffeldgroup.dailypic;

import android.content.Context;
import android.net.*;
import android.os.Environment;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.*;

/**
 * Created by marco on 04/05/16.
 */
public class FavouriteHandler {

    public static final String folder_name = "/DailyPicFavs/";
    public static final String fav_folder_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Environment.DIRECTORY_PICTURES + FavouriteHandler.folder_name;

    public FavouriteHandler() {

    }

    public boolean moveImgsToFavFolder(Context context, Uri uri) {
        // TODO: distinguish between move to fav folder or to WHICH OTHER direcotry?
        File f = new File(FavouriteHandler.fav_folder_path);

        if (f.exists() == false) {
            if (f.mkdir() == false) {
                Log.e("[DAYLIPIC]", "Creating directory failed");
                return false;
            }
        }

        boolean ret = true;

        File src = new File(uri.getPath());
        File dest = new File(FavouriteHandler.fav_folder_path + src.getName());

        if (src.renameTo(dest) == false) {
            Log.e("[DAYLIPIC]", "Error moving file to favourites folder: " + src.getAbsolutePath());
            ret = false;
        } else {
            Log.d("[DAYLIPIC]", "Moved image to favourites folder: " + src.getName());
            Toast.makeText(context, "Moved image to DailyPicFavs foler: " + src.getName(), Toast.LENGTH_LONG);
        }

        return ret;
    }

}
