package at.tugraz.inffeldgroup.dailypic;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import at.tugraz.inffeldgroup.dailypic.db.UriWrapper;

public class ImageGridViewAdapter extends BaseAdapter {
    private Context mContext;
    private int layoutResourceId = R.layout.image_item;
    private ArrayList<UriWrapper> imgUri;
    private ViewHolder holder = null;

    public ImageGridViewAdapter(Context c, ArrayList<UriWrapper> imgUri){
        mContext = c;
        this.imgUri = imgUri;
    }
    public ViewHolder getHolder(){
        return holder;
    }
    public void setNewImages(ArrayList<UriWrapper> arrayList){
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
        int h = mContext.getResources().getDisplayMetrics().widthPixels;
        int v = mContext.getResources().getDisplayMetrics().heightPixels;
        if (row == null) {
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId,parent,false);
            holder = new ViewHolder();
            holder.image=(ImageView)row.findViewById(R.id.image);
            holder.checked=(ImageView)row.findViewById(R.id.checked);
            holder.fav = (ImageView)row.findViewById(R.id.fav);
            if (imgUri.get(position).isFav()) {
                holder.fav.setVisibility(View.VISIBLE);
            } else {
                holder.fav.setVisibility(View.INVISIBLE);
            }
            row.setTag(holder);
            int bar = mContext.getResources().getDimensionPixelSize(mContext.getResources().getIdentifier("status_bar_height", "dimen", "android"));
            int bot;
            double vert;
            float dppxl = Math.round(1 * (Resources.getSystem().getDisplayMetrics().densityDpi / 160f));
            if(((Activity) mContext).findViewById(R.id.but_share) != null) {
                bot = ((Activity) mContext).findViewById(R.id.but_share).getHeight();
                vert = Math.floor((v - (3* bot) - bar - (4 * dppxl))/3);
            }
            else {
                bot = ((Activity) mContext).findViewById(R.id.textView).getHeight();
                vert = Math.floor((v - bot - bar - (4 * dppxl))/3);
            }
            Log.d("muhaha","1dp= " + dppxl);
            RelativeLayout layout = (RelativeLayout)row.findViewById(R.id.image_layout);
            switch (position) {
                case 0: layout.setPadding(1*(int)dppxl, 1*(int)dppxl, 0, 1*(int)dppxl);
                        break;
                case 1: layout.setPadding(1*(int)dppxl, 1*(int)dppxl, 1*(int)dppxl, 1*(int)dppxl);
                        break;
                case 2: layout.setPadding(1*(int)dppxl, 0, 0, 1*(int)dppxl);
                        break;
                case 3: layout.setPadding(1*(int)dppxl, 0, 1*(int)dppxl, 1*(int)dppxl);
                        break;
                case 4: layout.setPadding(1*(int)dppxl, 0, 0, 1*(int)dppxl);
                        break;
                case 5: layout.setPadding(1*(int)dppxl, 0, 1*(int)dppxl, 1*(int)dppxl);
                        break;
            }
            Log.d("Dimensions","Statusbar: "+bar + " pannels "+ bot);
            if(position == 1)
                ((ViewHolder) parent.getChildAt(0).getTag()).image.setLayoutParams(new RelativeLayout.LayoutParams(h/2,(int)vert));
            holder.image.setLayoutParams(new RelativeLayout.LayoutParams(h/2,(int)vert));
        } else {
            holder = (ViewHolder) row.getTag();
        }
        //holder.image.setImageURI(imgUri.get(position));
        BitmapWorkerTask task = new BitmapWorkerTask(holder.image, mContext);
        task.execute(imgUri.get(position).getUri());
        //holder.image.setImageBitmap(ImageTools.getDownsampledBitmap(mContext, imgUri.get(position), h/2, (v-350)/3));
        holder.uri = imgUri.get(position).getUri();
        return row;
    }
    static class ViewHolder {
        ImageView image;
        ImageView checked;
        ImageView fav;
        Uri uri;
    }
}