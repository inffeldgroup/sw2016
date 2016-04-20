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
        ImageFetcher img_fetcher = new ImageFetcher(MainActivity.this);

        List<Uri> rand_img = img_fetcher.getNextRandomImagePaths();

        List<View> imgv = new ArrayList<View>();
        imgv.add(findViewById(R.id.img_1));
        imgv.add(findViewById(R.id.img_2));
        imgv.add(findViewById(R.id.img_3));
        imgv.add(findViewById(R.id.img_4));
        imgv.add(findViewById(R.id.img_5));
        imgv.add(findViewById(R.id.img_6));

        setImages(rand_img, imgv);

    }
    public void sharebuttonOnClick(View v)
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
}
