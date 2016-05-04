package at.tugraz.inffeldgroup.dailypic;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public class ImageGridViewAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Uri> imgUri;

    public ImageGridViewAdapter(Context c, ArrayList<Uri> imgUri){
        mContext = c;
        this.imgUri = imgUri;
    }

    public void setNewImages(ArrayList<Uri> arrayList){
        this.imgUri = arrayList;
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

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            int h = mContext.getResources().getDisplayMetrics().widthPixels;
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new GridView.LayoutParams(h/2, h/2));
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageBitmap(ImageTools.getDownsampledBitmap(mContext, imgUri.get(position), 100, 100));
        return imageView;
    }
}