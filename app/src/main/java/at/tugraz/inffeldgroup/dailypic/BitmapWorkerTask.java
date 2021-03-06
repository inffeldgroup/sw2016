package at.tugraz.inffeldgroup.dailypic;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import at.tugraz.inffeldgroup.dailypic.util.ExifUtil;
import at.tugraz.inffeldgroup.dailypic.util.ImageTools;

class BitmapWorkerTask extends AsyncTask<Uri, Void, Bitmap> {

    public static Bitmap decodeInBackground(Context cx, Uri uri) {
        final int SCALE_DIVISOR = 4;
        int width = cx.getResources().getDisplayMetrics().widthPixels / SCALE_DIVISOR;
        int height = cx.getResources().getDisplayMetrics().heightPixels / (SCALE_DIVISOR * 2);
        return ExifUtil.rotateBitmap(uri.getPath(), ImageTools.getDownsampledBitmap(cx, uri, width, height));
    }
    
    public static void loadBitmap(Uri uri, ImageView imageView, Context context, int width, int height) {
        if (cancelPotentialWork(uri, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView, uri, context);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(context.getResources(), Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888), task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(uri);
        }
    }

    public static boolean cancelPotentialWork(Uri uri, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final Uri bitmapData = bitmapWorkerTask.uri;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData == null || !bitmapData.equals(uri)) {
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
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
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

    private final WeakReference<ImageView> imageViewReference;
    public Uri uri = null;
    Context cx;

    public BitmapWorkerTask(ImageView imageView, Uri uri, Context cx) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
        this.cx = cx;
        this.uri = uri;
    }

    @Override
    protected Bitmap doInBackground(Uri... params) {
        return decodeInBackground(cx, uri);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
            if (this == bitmapWorkerTask && imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}



class BitmapPreloaderTask extends AsyncTask<Uri, Void, Bitmap> {

    public static void preLoadBitmap(Uri uri, ArrayList<Bitmap> dest, Context context) {
        final BitmapPreloaderTask task = new BitmapPreloaderTask(dest, uri, context);
        task.execute(uri);
    }

    private final WeakReference<ArrayList<Bitmap>> bitMapContainer;
    public Uri uri = null;
    Context cx;

    public BitmapPreloaderTask(ArrayList<Bitmap> dest, Uri uri, Context cx) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        bitMapContainer = new WeakReference<ArrayList<Bitmap>>(dest);
        this.cx = cx;
        this.uri = uri;
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Uri... params) {
        return BitmapWorkerTask.decodeInBackground(cx, uri);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        final ArrayList<Bitmap> bitMaps = bitMapContainer.get();
        if (bitMaps != null && bitmap != null) {
            bitMaps.add(bitmap);
        }
    }
}

