package at.tugraz.inffeldgroup.dailypic;

import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int numberOfItems = 6;
    private ImageFetcher img_fetcher;
    private ArrayList<Uri> uriList;
    private GridView gridView;
    private ImageGridViewAdapter gridAdapter;
    private FavouriteHandler favhandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.img_fetcher = new ImageFetcher(MainActivity.this);
        this.uriList = img_fetcher.getNextRandomImages(numberOfItems);

        this.favhandler = new FavouriteHandler();

        gridAdapter = new ImageGridViewAdapter(this, uriList);
        gridView = (GridView) findViewById(R.id.mainGridView);
        gridView.setAdapter(gridAdapter);

        abstract class DoubleClickListener implements AdapterView.OnItemClickListener {
            private static final long DOUBLE_CLICK_TIME_DELTA = 500; //milliseconds

            private CountDownTimer timer;
            long lastClickTime = 0;

            private void startSingleClickTimer(final View view, final int position) {
                lastClickTime = System.currentTimeMillis();
                timer = new CountDownTimer(DOUBLE_CLICK_TIME_DELTA, DOUBLE_CLICK_TIME_DELTA) {
                    @Override
                    public void onTick(long l) {}

                    @Override
                    public void onFinish() {
                        onSingleClick(view, position);
                    }
                };
                timer.start();
            }

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {

                if (lastClickTime == 0) {
                    startSingleClickTimer(view, position);
                } else {
                    if (System.currentTimeMillis() - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                        timer.cancel();
                        lastClickTime = 0;
                        onDoubleClick(view, position);
                    } else {
                        if (timer != null) {
                            timer.cancel();
                        }
                        startSingleClickTimer(view, position);
                    }
                }
            }

            public abstract void onSingleClick(View v, int position);
            public abstract void onDoubleClick(View v, int position);
        }

        gridView.setOnItemClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick(View v, int position) {
                Uri uri = uriList.get(position);
                MainActivity.this.favhandler.moveImgsToFavFolder(getApplicationContext(), uri);
                Toast.makeText(getApplicationContext(), "Added to favourites: " + uri.getLastPathSegment().toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSingleClick(View v, int position) {
                Intent intent = new Intent(MainActivity.this, FullscreenImage.class);
                intent.setData(uriList.get(position));
                startActivity(intent);
            }
        });

        gridView.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    return true;
                }
                return false;
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
        uriList = this.img_fetcher.getPrevRandomImages(numberOfItems);
        gridAdapter.setNewImages(uriList);
        gridAdapter.notifyDataSetChanged();
    }

    public void favButtonOnClick(View v) {
        Intent intent = new Intent(MainActivity.this, FavouriteActivity.class);
        startActivity(intent);
    }

    public void nextButtonOnClick(View v) {
        uriList = this.img_fetcher.getNextRandomImages(numberOfItems);

        gridAdapter.setNewImages(uriList);
        gridAdapter.notifyDataSetChanged();
    }
}


