package at.tugraz.inffeldgroup.dailypic;

import android.content.Context;

import at.tugraz.inffeldgroup.dailypic.db.DbDatasource;
import at.tugraz.inffeldgroup.dailypic.db.UriWrapper;

public class FavouriteHandler {

    public static void toggleFavouriteState(Context context, UriWrapper uri) {
        DbDatasource.getInstance(context).insert(uri);
        uri.setFavourite(!uri.isFav());
        DbDatasource.getInstance(context).update(uri);
    }

}

