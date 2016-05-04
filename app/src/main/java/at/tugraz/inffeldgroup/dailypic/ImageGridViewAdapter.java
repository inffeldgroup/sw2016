package at.tugraz.inffeldgroup.dailypic;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class ImageGridViewAdapter extends BaseAdapter {
    private Context mContext;
    private int layoutResourceId = R.layout.image_item;
    private ArrayList<Uri> imgUri;
    private ViewHolder holder = null;

    public ImageGridViewAdapter(Context c, ArrayList<Uri> imgUri){
        mContext = c;
        this.imgUri = imgUri;
    }
    public ViewHolder getHolder(){
        return holder;
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
        View row = convertView;
        //ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId,parent,false);
            holder = new ViewHolder();
            holder.image=(ImageView)row.findViewById(R.id.image);
            holder.checked=(ImageView)row.findViewById(R.id.checked);
            holder.fav = (ImageView)row.findViewById(R.id.fav);
            row.setTag(holder);
            int h = mContext.getResources().getDisplayMetrics().widthPixels;
            holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.image.setLayoutParams(new RelativeLayout.LayoutParams(h/2, h/2));
            //holder.fav.setLayoutParams(new RelativeLayout.LayoutParams(h/8, h/8));
            //holder.checked.setLayoutParams(new RelativeLayout.LayoutParams(h/8,h/8));
        } else {
            holder = (ViewHolder) row.getTag();
        }
        holder.image.setImageBitmap(ImageTools.getDownsampledBitmap(mContext, imgUri.get(position), 100, 100));
        return row;
    }
    static class ViewHolder {
        ImageView image;
        ImageView checked;
        ImageView fav;
    }
}