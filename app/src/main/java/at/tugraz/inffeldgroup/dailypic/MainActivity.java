package at.tugraz.inffeldgroup.dailypic;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private int numberOfItems = 6;
    private ImageFetcher img_fetcher;
    private List<Uri> img_list;
    private GridView gridView;
    private ImageGridViewAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.img_fetcher = new ImageFetcher(MainActivity.this);
        final ArrayList<Uri> uriList = img_fetcher.getNextRandomImages(numberOfItems);

        gridAdapter = new ImageGridViewAdapter(this, uriList);
        gridView = (GridView) findViewById(R.id.mainGridView);
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, FullscreenImage.class);
                intent.setData(uriList.get(position));
                startActivity(intent);
            }
        });
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
        ArrayList<Uri> uriList = this.img_fetcher.getPrevRandomImages(numberOfItems);
        gridAdapter.setNewImages(uriList);
        gridAdapter.notifyDataSetChanged();
    }

    public void favButtonOnClick(View v){
        Intent intent = new Intent(MainActivity.this, FavouriteActivity.class);
        startActivity(intent);
    }

    public void nextButtonOnClick(View v) {;
        ArrayList<Uri> uriList = this.img_fetcher.getNextRandomImages(numberOfItems);
        gridAdapter.setNewImages(uriList);
        gridAdapter.notifyDataSetChanged();
    }
}


