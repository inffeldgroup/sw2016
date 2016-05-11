package at.tugraz.inffeldgroup.dailypic;

import android.content.Context;
import android.net.*;
import android.os.Environment;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.*;

import at.tugraz.inffeldgroup.dailypic.db.DbDatasource;
import at.tugraz.inffeldgroup.dailypic.db.UriWrapper;

/**
 * Created by marco on 04/05/16.
 */
public class FavouriteHandler {

    public static final String folder_name = "/DailyPicFavs/";
    public static final String fav_folder_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Environment.DIRECTORY_PICTURES + FavouriteHandler.folder_name;

    public FavouriteHandler() {

    }

    public boolean moveImgsToFavorites(Context context, Uri uri) {
        DbDatasource.getInstance(context).insert(new UriWrapper(uri, true));
        return true;
    }

}
