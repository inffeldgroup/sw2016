package at.tugraz.inffeldgroup.dailypic;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import at.tugraz.inffeldgroup.dailypic.db.DbDatasource;
import at.tugraz.inffeldgroup.dailypic.db.UriWrapper;

public class FavouriteHandler {

    public static void toggleFavouriteState(Context context, UriWrapper uri) {
        
        if (!DbDatasource.getInstance(context).checkIfExists(uri)) {
            DbDatasource.getInstance(context).insert(uri);
        }
        else{
            DbDatasource.getInstance(context).delete(uri);
        }

        if (uri.isFav()) {
            Toast.makeText(context, "Removed from favourites: " + uri.getUri().getLastPathSegment(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Added to favourites: " + uri.getUri().getLastPathSegment(), Toast.LENGTH_LONG).show();
        }
        uri.setFavourite(!uri.isFav());
        DbDatasource.getInstance(context).update(uri);
    }

    public static boolean getFavouriteState(Context context, UriWrapper uw) {
        if (!DbDatasource.getInstance(context).checkIfExists(uw)) {
            return true;
        } else {
            return false;
        }
    }
}

