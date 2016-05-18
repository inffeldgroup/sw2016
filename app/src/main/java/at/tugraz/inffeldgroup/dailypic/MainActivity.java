package at.tugraz.inffeldgroup.dailypic;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

public class MainActivity extends AppCompatActivity {

    private ImageFetcher img_fetcher;
    private ArrayList<View> image_view;
    private int time;

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
        }
    }
    private List<Uri> img_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // create image fetcher for current activity
        this.img_fetcher = new ImageFetcher(MainActivity.this);

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

    protected void onStart()
    {
        super.onStart();
        time = 0;
    }

    protected void onStop()
    {
        super.onStop();

        new Timer().schedule(new TimerTask(){
            @Override
            public void run(){
                time = 5;
            }
        }, 48 * 60 * 60 * 1000);

        Intent myIntent = new Intent(this, PushUpNotification.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent PendI = PendingIntent.getBroadcast(this, 101, myIntent, 0);

        if(time == 5)
        {
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + (5 * 24 * 60 * 60 * 1000), 5 * 24 * 60 * 60 * 1000, PendI);
            time = 7;
        }
        else if(time == 7)
        {
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + (7 * 24 * 60 * 60 * 1000), 7 * 24 * 60 * 60 * 1000, PendI);
        }
        else
        {
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + (48 * 60 * 60 * 1000), 48 * 60 * 60 * 1000, PendI);
        }

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


