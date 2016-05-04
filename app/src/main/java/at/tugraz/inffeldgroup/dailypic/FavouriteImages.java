package at.tugraz.inffeldgroup.dailypic;

import android.content.ContentValues;
import android.content.Context;
import android.net.*;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.util.*;

/**
 * Created by marco on 04/05/16.
 */
public class FavouriteImages {

    private static final String folder_name = "/DailyPicFavs/";
    private static final String base_path = Environment.DIRECTORY_PICTURES + FavouriteImages.folder_name;

    public FavouriteImages() {

    }

    public void moveImgsToFavFolder(Context context, List<Uri> favs) {
        for (Uri uri : favs) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "DailyPicFavourites");
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.BUCKET_ID, uri.getPath().hashCode());
            values.put(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, FavouriteImages.folder_name );

            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg" );
            values.put(MediaStore.Images.Media.DESCRIPTION, "DailyPicFavourites");//context.getResources().getString( R.string.product_image_description ) );
            values.put(MediaStore.MediaColumns.DATA, uri.getPath());
            Uri u = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }

    }

}
