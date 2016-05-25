package at.tugraz.inffeldgroup.dailypic.db;

import android.net.Uri;

/**
 * Created by marco on 11/05/16.
 */
public class UriWrapper {

    private Uri uri;
    private boolean is_favourite;

    public UriWrapper(Uri uri, boolean is_favourite) {
        this.uri = uri;
        this.is_favourite = is_favourite;
    }

    public Uri getUri() {
        return this.uri;
    }

    public void setFavourite(boolean isFav) {
        this.is_favourite = isFav;
    }

    public boolean isFav() {
        return this.is_favourite;
    }

    @Override
    public String toString() {
        return this.uri.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null )
            return false;
        if(obj.getClass() != this.getClass())
            return false;
        UriWrapper other = (UriWrapper)obj;
        return (this.uri.equals(other.uri) && (this.is_favourite == other.is_favourite));
    }

}
