package at.tugraz.inffeldgroup.dailypic;

import android.content.Context;
import android.widget.Toast;

import at.tugraz.inffeldgroup.dailypic.db.DbDatasource;
import at.tugraz.inffeldgroup.dailypic.db.UriWrapper;

public class FavouriteHandler {

    public static void toggleFavouriteState(Context context, UriWrapper uri) {
        
        if (!DbDatasource.getInstance(context).checkIfExists(uri)) {
            DbDatasource.getInstance(context).insert(uri);
        }

        if (uri.isFav()) {
            Toast.makeText(context, "Removed from favourites: " + uri.getUri().getLastPathSegment().toString(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Added to favourites: " + uri.getUri().getLastPathSegment().toString(), Toast.LENGTH_LONG).show();
        }
        uri.setFavourite(!uri.isFav());
        DbDatasource.getInstance(context).update(uri);
    }

}

