package at.tugraz.inffeldgroup.dailypic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ImageTools {
    public static Bitmap getDownsampledBitmap(Context context, Uri uri, int targetWidth, int targetHeight) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options outDimens = getBitmapDimensions(context, uri);

            int sampleSize = calculateSampleSize(outDimens.outWidth, outDimens.outHeight, targetWidth, targetHeight);

            bitmap = downsampleBitmap(context, uri, sampleSize);

        } catch (FileNotFoundException e) {
            Log.e("E - getDownsampledBitma", e.getMessage());
            //handle the exception(s)
        }

        return bitmap;
    }

    private static BitmapFactory.Options getBitmapDimensions(Context context, Uri uri) throws FileNotFoundException {
        BitmapFactory.Options outDimens = new BitmapFactory.Options();
        outDimens.inJustDecodeBounds = true; // the decoder will return null (no bitmap)
        try {
            InputStream is= context.getContentResolver().openInputStream(uri);
            // if Options requested only the size will be returned
            BitmapFactory.decodeStream(is, null, outDimens);
            is.close();
        } catch (IOException e) {
            Log.e("E - getBitmapDimensions", e.getMessage());
        }


        return outDimens;
    }

    private static int calculateSampleSize(int width, int height, int targetWidth, int targetHeight) {
        int inSampleSize = 1;

        if (height > targetHeight || width > targetWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) targetHeight);
            final int widthRatio = Math.round((float) width / (float) targetWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    private static Bitmap downsampleBitmap(Context context, Uri uri, int sampleSize) throws FileNotFoundException {
        Bitmap resizedBitmap = null;
        BitmapFactory.Options outBitmap = new BitmapFactory.Options();
        try {
            outBitmap.inJustDecodeBounds = false; // the decoder will return a bitmap
            outBitmap.inSampleSize = sampleSize;

            InputStream is = context.getContentResolver().openInputStream(uri);
            resizedBitmap = BitmapFactory.decodeStream(is, null, outBitmap);
            is.close();
        } catch (IOException e) {
            Log.e("E - downsampleBitmap", e.getMessage());
        }

        return resizedBitmap;
    }
}
