package at.tugraz.inffeldgroup.dailypic;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

import at.tugraz.inffeldgroup.dailypic.activities.MainActivity;
import at.tugraz.inffeldgroup.dailypic.db.UriWrapper;

public class ImageGridViewAdapter extends BaseAdapter {
    private Context mContext;
    private int layoutResourceId = R.layout.image_item;
    private ViewHolder holder = null;

    private Stack<UriWrapper> uriHistory;

    private ArrayList<Bitmap> previousBitmaps;
    private ArrayList<Bitmap> currentBitmaps;
    private ArrayList<Bitmap> nextBitmaps;

    private ArrayList<UriWrapper> previousUris;
    private ArrayList<UriWrapper> currentUris;
    private ArrayList<UriWrapper> nextUris;

    public ArrayList<UriWrapper> getUriList() {
        return  currentUris;
    }

    public ImageGridViewAdapter(Context c, ArrayList<UriWrapper> startUp, ArrayList<UriWrapper> next)
    {
        mContext = c;
        previousBitmaps = new ArrayList<>();
        currentBitmaps = new ArrayList<>();
        nextBitmaps = new ArrayList<>();

        previousUris = new ArrayList<>();
        currentUris = startUp;
        nextUris = next;

        uriHistory = new Stack<>();

        preloadBitmaps(nextBitmaps, next);
    }

    public void setNextImages(ArrayList<UriWrapper> nextImages)
    {
        for (UriWrapper img : previousUris) {
            uriHistory.push(img);
        }

        previousBitmaps = currentBitmaps;
        currentBitmaps = nextBitmaps;
        nextBitmaps = new ArrayList<>();
        preloadBitmaps(nextBitmaps, nextImages);

        previousUris = currentUris;
        currentUris = nextUris;
        nextUris = nextImages;

        notifyDataSetChanged();
    }

    public void setNewImages(ArrayList<UriWrapper> newImages)
    {
        currentBitmaps = new ArrayList<>();
        currentUris = newImages;
        notifyDataSetChanged();
    }
    public void updateFavStatus(ArrayList<UriWrapper> newImages)
    {
        currentBitmaps = new ArrayList<>();
        currentUris = newImages;
    }

    public void setPreviousImages(ArrayList<UriWrapper> nextImages)
    {
        if (uriHistory.isEmpty()) {
            Toast.makeText(mContext, "No history available", Toast.LENGTH_LONG).show();
            return;
        }

        nextUris = nextImages;
        nextBitmaps = new ArrayList<>();
        preloadBitmaps(nextBitmaps, nextImages);

        currentUris = previousUris;
        currentBitmaps = previousBitmaps;

        previousUris = new ArrayList<>();
        for (int i = 0; i < MainActivity.NUMBER_OF_ITEMS && !uriHistory.isEmpty(); i++) {
            previousUris.add(uriHistory.pop());
        }
        Collections.rotate(previousUris, MainActivity.NUMBER_OF_ITEMS / 2);
        previousBitmaps = new ArrayList<>();
        preloadBitmaps(previousBitmaps, previousUris);

        notifyDataSetChanged();
    }

    private void preloadBitmaps(ArrayList<Bitmap> target, ArrayList<UriWrapper> imagesToPreload) {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        if (am.isLowRamDevice()) {
            return; // Do not use preloading on devices with low ram
        }

        for (UriWrapper img : imagesToPreload) {
            if (img.getUri() != null) {
                BitmapPreloaderTask.preLoadBitmap(img.getUri(), target, mContext);
            }
        }
    }

    public int getCount() {
        return currentUris.size();
    }

    public Object getItem(int position) {
        return currentUris.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
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
            if(((Activity) mContext).findViewById(R.id.act_main_but_share) != null) {
                bot = ((Activity) mContext).findViewById(R.id.act_main_but_share).getHeight();
                vert = ((v - (3* bot) - bar - (2 * dppxl))/3);
                RelativeLayout layout = (RelativeLayout)row.findViewById(R.id.image_layout);
                switch (position) {
                    case 0:
                        layout.setPadding(0, 0, 0, 1 * (int) dppxl);
                        break;
                    case 1:
                        layout.setPadding(1 * (int) dppxl, 0, 0, 1 * (int) dppxl);
                        break;
                    case 2:
                        layout.setPadding(0, 0, 0, 1 * (int) dppxl);
                        break;
                    case 3:
                        layout.setPadding(1 * (int) dppxl, 0, 0, 1 * (int) dppxl);
                        break;
                    case 4:
                        layout.setPadding(0, 0, 0, 0);
                        break;
                    case 5:
                        layout.setPadding(1 * (int) dppxl, 0, 0, 0);
                        break;
                }
            }
            else {
                bot = ((Activity) mContext).findViewById(R.id.act_fav_topPanel).getHeight();
                vert = ((v - bot - bar - (2 * dppxl))/3);
            }

            if(position == 1)
                ((ViewHolder) parent.getChildAt(0).getTag()).image.setLayoutParams(new RelativeLayout.LayoutParams(h/2,(int)vert));
            holder.image.setLayoutParams(new RelativeLayout.LayoutParams(h/2,(int)vert));
        } else {
            holder = (ViewHolder) row.getTag();
        }
        if (currentUris.get(position).isFav()) {
            holder.fav.setVisibility(View.VISIBLE);
        } else {
            holder.fav.setVisibility(View.INVISIBLE);
        }

        if (position < currentBitmaps.size()) {
            // Use preloaded bitmap whenever available
            holder.image.setImageBitmap(currentBitmaps.get(position));
        } else {
            // Retrieve bitmap from uri when there is no preloaded bitmap
            BitmapWorkerTask.loadBitmap(currentUris.get(position).getUri(), holder.image, mContext, h, v);
        }

        holder.uri = currentUris.get(position).getUri();
        return row;
    }

    public static class ViewHolder {
        public ImageView image;
        public ImageView checked;
        public ImageView fav;
        public Uri uri;
    }
}