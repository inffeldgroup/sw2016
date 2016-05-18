package at.tugraz.inffeldgroup.dailypic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import at.tugraz.inffeldgroup.dailypic.ImageGridViewAdapter.ViewHolder;
import at.tugraz.inffeldgroup.dailypic.db.AndroidDatabaseManager;
import at.tugraz.inffeldgroup.dailypic.db.DbDatasource;
import at.tugraz.inffeldgroup.dailypic.db.UriWrapper;
import at.tugraz.inffeldgroup.dailypic.util.DoubleClickListener;
import java.util.ArrayList;
import java.util.HashMap;

import at.tugraz.inffeldgroup.dailypic.ShakeDetector;

public class MainActivity extends AppCompatActivity {
    public static final int numberOfItems = 6;
    private ImageFetcher img_fetcher;
    private GridView gridView;
    private ImageGridViewAdapter gridAdapter;

    private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private ShakeDetector mShakeDetector;


    private float x1,x2;
    static final int MIN_DISTANCE = 150;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.img_fetcher = new ImageFetcher(MainActivity.this);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mShakeDetector = new ShakeDetector();
		mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
				/*
				 * The following method, "handleShakeEvent(count):" is a stub //
				 * method you would use to setup whatever you want done once the
				 * device has been shook.
				 */
                handleShakeEvent(count);
            }
        });
        ArrayList<UriWrapper> startUpPictures = img_fetcher.getNextRandomImages(numberOfItems, this);
        ArrayList<UriWrapper> nextPictures = img_fetcher.getNextRandomImages(numberOfItems, this);
        gridAdapter = new ImageGridViewAdapter(this, startUpPictures, nextPictures);
        gridView = (GridView) findViewById(R.id.mainGridView);
        gridView.setAdapter(gridAdapter);

        setGridViewClickListener();

        gridView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                Log.d("xD", Integer.toString(position) + " " + Boolean.toString(gridView.isItemChecked(position)));
                ViewHolder item = (ViewHolder) gridView.getChildAt(position).getTag();
                if(!gridView.isItemChecked(position)){
                    gridView.setItemChecked(position, true);
                    item.checked.setVisibility(View.VISIBLE);
                    item.image.setImageAlpha(127);
                }
                else {
                    item.checked.setVisibility(View.INVISIBLE);
                    item.image.setImageAlpha(255);
                    gridView.setItemChecked(position, false);
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ViewHolder item = (ViewHolder) gridView.getChildAt(position).getTag();
                        if(!gridView.isItemChecked(position)){
                            gridView.setItemChecked(position, true);
                            item.checked.setVisibility(View.VISIBLE);
                            item.image.setImageAlpha(127);
                        }
                        else {
                            item.checked.setVisibility(View.INVISIBLE);
                            item.image.setImageAlpha(255);
                            gridView.setItemChecked(position, false);
                            if(gridView.getCheckedItemCount() == 0)
                                clearSelection();
                        }
                    }
                });

                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) { return false; }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(MainActivity.this, FullscreenImage.class);
                        intent.setData(gridAdapter.getUriList().get(position).getUri());
                        startActivity(intent);
                    }
                });
                gridView.setChoiceMode(AbsListView.CHOICE_MODE_NONE);

            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override

            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                gridView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
                Log.d("xD", Integer.toString(position) + " " + Boolean.toString(gridView.isItemChecked(position)));
                ViewHolder item = (ViewHolder) gridView.getChildAt(position).getTag();
                if(!gridView.isItemChecked(position)){
                    gridView.setItemChecked(position, true);
                    item.checked.setVisibility(View.VISIBLE);
                    item.image.setImageAlpha(127);
                }
                else {

                    item.checked.setVisibility(View.INVISIBLE);
                    item.image.setImageAlpha(255);
                    gridView.setItemChecked(position, false);
                }
                return true;
            }
        });


        gridView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        float deltaX = x2 - x1;

                        if (Math.abs(deltaX) > MIN_DISTANCE)
                        {
                            // Left to Right swipe action
                            if (x2 > x1)
                            {
                                backButtonOnClick(v);
                            }

                            // Right to left swipe action
                            else
                            {

                                nextButtonOnClick(v);
                            }

                        }
                        else
                        {
                            // consider as something else - a screen tap for example
                            if(event.getAction() == MotionEvent.ACTION_MOVE){
                                return true;
                            }
                        }
                        break;
                }
                return false;
            }

        });
        //for advertisement
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    private void handleShakeEvent(int count) {
        Log.d("NOTE:", "Shake it " + count + " times");
        if(count > 2) {
            clearSelection();
            gridAdapter.setNextImages(img_fetcher.getNextRandomImages(numberOfItems, this));
        }
    }

    public void sharebuttonOnClick(View v)
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        sendIntent.setType("image/*");
        if(gridView.getCheckedItemPositions() != null) {
            SparseBooleanArray checked = gridView.getCheckedItemPositions();
            ArrayList<Uri> shareList = new ArrayList<Uri>();
            for (int i = 0; i < gridView.getCount(); i++)
                if (checked.get(i)) {
                    Log.d("asdf", "test" + checked.toString());
                    shareList.add(gridAdapter.getUriList().get(i).getUri());
                }
            sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, shareList);
            startActivity(Intent.createChooser(sendIntent, "Share via"));
            }
        else Toast.makeText(this, "No Images for sharing selected!", Toast.LENGTH_SHORT).show();
    }

    public void deleteButtonOnClick(View v) {
        if(gridView.getCheckedItemPositions() != null) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Delete Images")
                    .setMessage("Are you sure you want to delete the selected images?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteImages();
                        }

                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            clearSelection();
                        }

                    })
                    .show();
        } else {
            Toast.makeText(this, "No images for deletion selected!", Toast.LENGTH_SHORT).show();
        }
    }



    private void deleteImages() {
        SparseBooleanArray checked = gridView.getCheckedItemPositions();
        HashMap<Integer, ImageGridViewAdapter.ViewHolder> del_map = new HashMap<Integer, ViewHolder>();
        for (int i = 0; i < gridView.getCount(); i++) {
            if (checked.get(i)) {
                del_map.put(i, (ViewHolder) gridView.getChildAt(i).getTag());
            }
        }

        // call delete image function
        this.img_fetcher.deleteImages(del_map);
        // replace deleted pictures
        this.img_fetcher.replaceDeletedImages(del_map, this);
        clearSelection();
    }

    public void favButtonOnClick(View v) {
        Intent intent = new Intent(MainActivity.this, FavouriteActivity.class);
        startActivity(intent);
    }

    public void nextButtonOnClick(View v) {
        clearSelection();
        gridAdapter.setNextImages(img_fetcher.getNextRandomImages(numberOfItems, this));
    }

    public void backButtonOnClick(View v) {
        clearSelection();
        gridAdapter.setPreviousImages(img_fetcher.getPrevRandomImages(numberOfItems, this), img_fetcher.getNextRandomImages(numberOfItems, this));
    }

    @Override
    public void onBackPressed()
    {
        if(gridView.getChoiceMode() != AbsListView.CHOICE_MODE_MULTIPLE_MODAL)
          super.onBackPressed();
        clearSelection();
    }

    public void clearSelection(){
        if(gridView.getChoiceMode() == AbsListView.CHOICE_MODE_MULTIPLE_MODAL) {
            for (int i = 0; i < gridView.getCount(); i++) {
                ((ViewHolder) gridView.getChildAt(i).getTag()).image.setImageAlpha(255);
                ((ViewHolder) gridView.getChildAt(i).getTag()).checked.setVisibility(View.INVISIBLE);
                gridView.setItemChecked(i, false);
            }
            gridView.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
            setGridViewClickListener();
        }
    }

    private void setGridViewClickListener() {
        gridView.setOnItemClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick(View v, int position) {
                UriWrapper uri = gridAdapter.getUriList().get(position);
                FavouriteHandler.toggleFavouriteState(MainActivity.this, uri);

                // Update grid view with favorite stars
                ArrayList<UriWrapper> uriListNew = new ArrayList<UriWrapper>();
                for (UriWrapper uriWrapper : gridAdapter.getUriList()) {
                    uriListNew.add(DbDatasource.getInstance(MainActivity.this).getUriWrapper(uriWrapper.getUri()));
                }
                gridAdapter.setNewImages(uriListNew);
                //gridAdapter = new ImageGridViewAdapter(MainActivity.this, uriList);
                //gridView.setAdapter(gridAdapter);
            }

            @Override
            public void onSingleClick(View v, int position) {
                Intent intent = new Intent(MainActivity.this, FullscreenImage.class);
                intent.setData(gridAdapter.getUriList().get(position).getUri());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }
}


