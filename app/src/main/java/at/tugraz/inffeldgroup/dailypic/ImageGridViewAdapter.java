package at.tugraz.inffeldgroup.dailypic;

import at.tugraz.inffeldgroup.dailypic.ImageTools;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

import java.lang.ref.WeakReference;
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
            row.setTag(holder);
            int bar = mContext.getResources().getDimensionPixelSize(mContext.getResources().getIdentifier("status_bar_height", "dimen", "android"));
            int bot;
            double vert;
            double dppxl = (1 * (Resources.getSystem().getDisplayMetrics().densityDpi / 160f));
            if(((Activity) mContext).findViewById(R.id.but_share) != null) {
                bot = ((Activity) mContext).findViewById(R.id.but_share).getHeight();
                vert = ((v - (3* bot) - bar - (2 * dppxl))/3)+1;
            }
            else {
                bot = ((Activity) mContext).findViewById(R.id.textView).getHeight();
                vert = ((v - bot - bar - (2 * dppxl))/3);
            }
            Log.d("muhaha","1dp= "+ dppxl +" "+ (bar+2*dppxl+3*bot+3*vert) + " "+ v);
            RelativeLayout layout = (RelativeLayout)row.findViewById(R.id.image_layout);
            switch (position) {
                case 0: layout.setPadding(0, 0, 0, 1*(int)dppxl);
                        break;
                case 1: layout.setPadding(1*(int)dppxl, 0, 0, 1*(int)dppxl);
                        break;
                case 2: layout.setPadding(0, 0, 0, 1*(int)dppxl);
                        break;
                case 3: layout.setPadding(1*(int)dppxl, 0, 0, 1*(int)dppxl);
                        break;
                case 4: layout.setPadding(0, 0, 0, 0);
                        break;
                case 5: layout.setPadding(1*(int)dppxl, 0, 0, 0);
                        break;
            }
            Log.d("Dimensions","Statusbar: "+bar + " pannels "+ bot);
            if(position == 1)
                ((ViewHolder) parent.getChildAt(0).getTag()).image.setLayoutParams(new RelativeLayout.LayoutParams(h/2,(int)vert));
            holder.image.setLayoutParams(new RelativeLayout.LayoutParams(h/2,(int)vert));
        } else {
            holder = (ViewHolder) row.getTag();
        }
        if (imgUri.get(position).isFav()) {
            holder.fav.setVisibility(View.VISIBLE);
        } else {
            holder.fav.setVisibility(View.INVISIBLE);
        }


        if (cancelPotentialWork(imgUri.get(position).getUri(), holder.image)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(holder.image, imgUri.get(position).getUri(), mContext);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(mContext.getResources(), Bitmap.createBitmap(h, v, Bitmap.Config.ARGB_8888), task);
            holder.image.setImageDrawable(asyncDrawable);
            task.execute(imgUri.get(position).getUri());
        }


        //holder.image.setImageURI(imgUri.get(position));
        /** BitmapWorkerTask task = new BitmapWorkerTask(holder.image, mContext);
        task.execute(imgUri.get(position).getUri()); **/
        //holder.image.setImageBitmap(ImageTools.getDownsampledBitmap(mContext, imgUri.get(position), h/2, (v-350)/3));

        holder.uri = imgUri.get(position).getUri();
        return row;
    }

    public static boolean cancelPotentialWork(Uri data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final Uri bitmapData = bitmapWorkerTask.data;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData == null || !bitmapData.equals(data)) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    public static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final ImageGridViewAdapter.AsyncDrawable asyncDrawable = (ImageGridViewAdapter.AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    public static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    static class ViewHolder {
        ImageView image;
        ImageView checked;
        ImageView fav;
        Uri uri;
    }
}