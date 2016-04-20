package at.tugraz.inffeldgroup.dailypic;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageFetcher img_fetcher;
    private ArrayList<View> image_view;

    private void setImages(List<Uri> uris, List<View> views) {
        if (uris == null || views == null) {
            Log.d("[DALYPIC - ERROR]", "MainActivity.setImages: uris or views == null!");
            return;
        }
        for (int i = 0; i < 6; i++) {
            ((ImageView) views.get(i)).setImageURI(uris.get(i));
        }
    }

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
        setImages(rand_img, this.image_view);
    }

    public void nextButtonOnClick(View v) {

        List<Uri> rand_img = this.img_fetcher.getNextRandomImagePaths();
        setImages(rand_img, this.image_view);
    }
}

