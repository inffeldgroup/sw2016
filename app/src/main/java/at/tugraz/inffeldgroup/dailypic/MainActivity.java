package at.tugraz.inffeldgroup.dailypic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.CountDownTimer;
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
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import at.tugraz.inffeldgroup.dailypic.ImageGridViewAdapter.ViewHolder;
import at.tugraz.inffeldgroup.dailypic.db.AndroidDatabaseManager;
import java.util.ArrayList;
import java.util.HashMap;

import at.tugraz.inffeldgroup.dailypic.ShakeDetector;

public class MainActivity extends AppCompatActivity {
    public static final int numberOfItems = 6;
    private ImageFetcher img_fetcher;
    public ArrayList<Uri> uriList;
    private GridView gridView;
    private ImageGridViewAdapter gridAdapter;
    private FavouriteHandler favhandler;

    private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private ShakeDetector mShakeDetector;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.img_fetcher = new ImageFetcher(MainActivity.this);
        this.uriList = img_fetcher.getNextRandomImages(numberOfItems);

        this.favhandler = new FavouriteHandler();

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
        gridAdapter = new ImageGridViewAdapter(this, uriList);
        gridView = (GridView) findViewById(R.id.mainGridView);
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick(View v, int position) {
                Uri uri = uriList.get(position);
                MainActivity.this.favhandler.moveImgsToFavorites(MainActivity.this, uri);
                Toast.makeText(getApplicationContext(), "Added to favourites: " + uri.getLastPathSegment().toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSingleClick(View v, int position) {
                Intent intent = new Intent(MainActivity.this, FullscreenImage.class);
                intent.setData(uriList.get(position));
                startActivity(intent);
            }
        });
        gridView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                Log.d("xD", Integer.toString(position) + " " + Boolean.toString(gridView.isItemChecked(position)));
                ViewHolder item = (ViewHolder) gridView.getChildAt(position).getTag();
                if(!gridView.isItemChecked(position)){
                    gridView.setItemChecked(position, true);
                    item.checked.setVisibility(View.VISIBLE);
                    item.image.setAlpha(0.5f);
                }
                else {
                    item.checked.setVisibility(View.INVISIBLE);
                    item.image.setAlpha(1f);
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
                            item.image.setAlpha(0.5f);
                        }
                        else {
                            item.checked.setVisibility(View.INVISIBLE);
                            item.image.setAlpha(1f);
                            gridView.setItemChecked(position, false);
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
                        intent.setData(uriList.get(position));
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
                    item.image.setAlpha(0.5f);
                }
                else {

                    item.checked.setVisibility(View.INVISIBLE);
                    item.image.setAlpha(1f);
                    gridView.setItemChecked(position, false);
                }
                return true;
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
        //for advertisement
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        ImageButton deleteButton =(ImageButton)findViewById(R.id.but_delete);
        if (deleteButton != null) {deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent dbmanager = new Intent(MainActivity.this, AndroidDatabaseManager.class);
                startActivity(dbmanager);
            }
        });}
    }

    private void handleShakeEvent(int count) {
        Log.d("NOTE:", "Shake it " + count + " times");
        if(count > 2) {
            uriList = img_fetcher.getNextRandomImages(numberOfItems);
            clearSelection();

            gridAdapter.setNewImages(uriList);
            gridAdapter.notifyDataSetChanged();
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
                    shareList.add(uriList.get(i));
                }
            sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, shareList);
            startActivity(sendIntent);
        }
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
        this.img_fetcher.replaceDeletedImages(del_map);
        clearSelection();
    }

    public void backButtonOnClick(View v) {
        uriList = img_fetcher.getPrevRandomImages(numberOfItems);
        clearSelection();
        gridAdapter.setNewImages(uriList);
        gridAdapter.notifyDataSetChanged();
    }

    public void favButtonOnClick(View v) {
        Intent intent = new Intent(MainActivity.this, FavouriteActivity.class);
        startActivity(intent);
    }

    public void nextButtonOnClick(View v) {
        uriList = img_fetcher.getNextRandomImages(numberOfItems);
        clearSelection();

        gridAdapter.setNewImages(uriList);
        gridAdapter.notifyDataSetChanged();
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
                ((ViewHolder) gridView.getChildAt(i).getTag()).image.setAlpha(1f);
                ((ViewHolder) gridView.getChildAt(i).getTag()).checked.setVisibility(View.INVISIBLE);
                gridView.setItemChecked(i, false);
            }
            gridView.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
            gridView.setOnItemClickListener(new DoubleClickListener() {
                @Override
                public void onDoubleClick(View v, int position) {
                    Uri uri = uriList.get(position);
                    MainActivity.this.favhandler.moveImgsToFavorites(MainActivity.this, uri);
                    Toast.makeText(getApplicationContext(), "Added to favourites: " + uri.getLastPathSegment().toString(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSingleClick(View v, int position) {
                    Intent intent = new Intent(MainActivity.this, FullscreenImage.class);
                    intent.setData(uriList.get(position));
                    startActivity(intent);
                }
            });
        }
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


