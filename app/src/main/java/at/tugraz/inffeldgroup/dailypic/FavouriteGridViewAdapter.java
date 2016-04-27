package at.tugraz.inffeldgroup.dailypic;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public class FavouriteGridViewAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Uri> imgUri;

    public FavouriteGridViewAdapter(Context c, ArrayList<Uri> imgUri){
        mContext = c;
        this.imgUri = imgUri;
    }

    public int getCount() {
        return imgUri.size();
    }

    public Object getItem(int position) {
        return imgUri.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            int h = mContext.getResources().getDisplayMetrics().widthPixels;
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            //imageView.setLayoutParams(new GridView.LayoutParams(400, 400));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new GridView.LayoutParams(h/2, h/2));
            //imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageURI(imgUri.get(position));
        return imageView;
    }
}