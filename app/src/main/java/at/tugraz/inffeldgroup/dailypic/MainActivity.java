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

    public void favButtonOnClick(View v){
        Intent intent = new Intent(MainActivity.this, FavouriteActivity.class);
        startActivity(intent);
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


