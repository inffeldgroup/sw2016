package at.tugraz.inffeldgroup.dailypic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class ImageGenerator extends AsyncTask<Void,Void,Void>
{
    public Activity activity;

    // -------------------- START IMAGE GENERATION
    private Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            Log.d("NETWORK ERROR", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                //+ "/Android/data/"
                + "/Download/");
                //+ activity.getApplicationContext().getPackageName()
                //+ "/Files/");
        //File mediaStorageDir = new File(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        Log.d("TAG", "path to save to: " + mediaStorageDir.getAbsolutePath());
        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpeg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    public void storeRandomBitmap(int nr_of_images_to_create) {
        for (int i = 0; i < nr_of_images_to_create; i++) {
            Bitmap b = getBitmapFromURL("https://www.random.org/bitmaps/?format=png&width=300&height=300&zoom=1");
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                Log.d("TAG",
                        "Error creating media file, check storage permissions: ");// e.getMessage());
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                b.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d("TAG", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("TAG", "Error accessing file: " + e.getMessage());
            }
        }
    }
    // -------------------- END IMAGE GENERATION

    @Override
    protected Void doInBackground(Void... voids) {
        this.storeRandomBitmap(2);
        return null;
    }
}

public class MainActivity extends AppCompatActivity {

    private ImageHandler img_fetcher;
    private ArrayList<View> image_view;
    private List<Uri> img_list;

    private Bitmap getDownsampledBitmap(Uri uri, int targetWidth, int targetHeight) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options outDimens = getBitmapDimensions(uri);

            int sampleSize = calculateSampleSize(outDimens.outWidth, outDimens.outHeight, targetWidth, targetHeight);

            bitmap = downsampleBitmap(uri, sampleSize);

        } catch (Exception e) {
            //handle the exception(s)
        }

        return bitmap;
    }

    private BitmapFactory.Options getBitmapDimensions(Uri uri) throws FileNotFoundException, IOException {
        BitmapFactory.Options outDimens = new BitmapFactory.Options();
        outDimens.inJustDecodeBounds = true; // the decoder will return null (no bitmap)

        InputStream is= getContentResolver().openInputStream(uri);
        // if Options requested only the size will be returned
        BitmapFactory.decodeStream(is, null, outDimens);
        is.close();

        return outDimens;
    }

    private int calculateSampleSize(int width, int height, int targetWidth, int targetHeight) {
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

    private Bitmap downsampleBitmap(Uri uri, int sampleSize) throws FileNotFoundException, IOException {
        Bitmap resizedBitmap;
        BitmapFactory.Options outBitmap = new BitmapFactory.Options();
        outBitmap.inJustDecodeBounds = false; // the decoder will return a bitmap
        outBitmap.inSampleSize = sampleSize;

        InputStream is = getContentResolver().openInputStream(uri);
        resizedBitmap = BitmapFactory.decodeStream(is, null, outBitmap);
        is.close();

        return resizedBitmap;
    }

    private void setImages(List<Uri> uris, List<View> views) {
        if (uris == null || views == null) {
            Log.d("[DALYPIC - ERROR]", "MainActivity.setImages: uris or views == null!");
            return;
        }
        for (int i = 0; i < 6; i++) {
            ((ImageView) views.get(i)).setImageBitmap(getDownsampledBitmap(uris.get(i), 200, 200));
            ((ImageView) views.get(i)).setTag(uris.get(i).toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ImageGenerator ig = new ImageGenerator();
        ig.activity = this;
        ig.execute();

        // create image fetcher for current activity
        this.img_fetcher = new ImageHandler(MainActivity.this);

        List<Uri> rand_img = this.img_fetcher.getNextRandomImagePaths();

        this.image_view = new ArrayList<View>();
        this.image_view.add(findViewById(R.id.img_1));
        this.image_view.add(findViewById(R.id.img_2));
        this.image_view.add(findViewById(R.id.img_3));
        this.image_view.add(findViewById(R.id.img_4));
        this.image_view.add(findViewById(R.id.img_5));
        this.image_view.add(findViewById(R.id.img_6));

        setImages(rand_img, this.image_view);
        img_list = rand_img;
    }




    public void sharebuttonOnClick(View v)
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void backButtonOnClick(View v) {
        List<Uri> rand_img = this.img_fetcher.getPreviousRandomImagePaths();
        if(rand_img != null) {
            img_list.clear();
            img_list = rand_img;
        }
        setImages(rand_img, this.image_view);
    }

    public void nextButtonOnClick(View v) {
        img_list.clear();
        List<Uri>rand_img = this.img_fetcher.getNextRandomImagePaths();
        img_list = rand_img;
        setImages(rand_img, this.image_view);
    }

    public void onImg1Click(View v) {
        Intent intent = new Intent(MainActivity.this, FullscreenImage.class);
        intent.setData(img_list.get(0));
        startActivity(intent);
    }
    public void onImg2Click(View v) {
        Intent intent = new Intent(MainActivity.this, FullscreenImage.class);
        intent.setData(img_list.get(1));
        startActivity(intent);
    }
    public void onImg3Click(View v) {
        Intent intent = new Intent(MainActivity.this, FullscreenImage.class);
        intent.setData(img_list.get(2));
        startActivity(intent);
    }
    public void onImg4Click(View v) {
        Intent intent = new Intent(MainActivity.this, FullscreenImage.class);
        intent.setData(img_list.get(3));
        startActivity(intent);
    }
    public void onImg5Click(View v) {
        Intent intent = new Intent(MainActivity.this, FullscreenImage.class);
        intent.setData(img_list.get(4));
        startActivity(intent);
    }
    public void onImg6Click(View v) {
        Intent intent = new Intent(MainActivity.this, FullscreenImage.class);
        intent.setData(img_list.get(5));
        startActivity(intent);


    }

}


