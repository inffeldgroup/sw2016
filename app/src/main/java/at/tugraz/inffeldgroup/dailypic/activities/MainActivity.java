package at.tugraz.inffeldgroup.dailypic.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import at.tugraz.inffeldgroup.dailypic.FavouriteHandler;
import at.tugraz.inffeldgroup.dailypic.ImageFetcher;
import at.tugraz.inffeldgroup.dailypic.ImageGridViewAdapter;
import at.tugraz.inffeldgroup.dailypic.ImageGridViewAdapter.ViewHolder;
import at.tugraz.inffeldgroup.dailypic.PushUpNotification;
import at.tugraz.inffeldgroup.dailypic.R;
import at.tugraz.inffeldgroup.dailypic.ShakeDetector;
import at.tugraz.inffeldgroup.dailypic.db.AndroidDatabaseManager;
import at.tugraz.inffeldgroup.dailypic.db.DbDatasource;
import at.tugraz.inffeldgroup.dailypic.db.UriWrapper;
import at.tugraz.inffeldgroup.dailypic.util.DoubleClickListener;

public class MainActivity extends AppCompatActivity {
    public static final int NUMBER_OF_ITEMS = 6;
    private static final int ALPHA_HALF_VISIBLE = 127;
    private static final int ALPHA_FULL_VISIBLE = 255;
    private static final int MIN_DISTANCE = 150;
    private static final int SHAKE_LIMIT = 3;
    private boolean ready = false;

    private ImageFetcher imageFetcher;
    private GridView gridView;
    private ImageGridViewAdapter gridAdapter;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    private Toolbar topBar;

    private float x1, x2;
    private int time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        topBar = (Toolbar) findViewById(R.id.act_main_toolbar);
        setSupportActionBar(topBar);

        this.imageFetcher = new ImageFetcher(MainActivity.this);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new MyOnShakeListener());
        ArrayList<UriWrapper> startUpPictures = imageFetcher.getNextRandomImages(NUMBER_OF_ITEMS, this);
        ArrayList<UriWrapper> nextPictures = imageFetcher.getNextRandomImages(NUMBER_OF_ITEMS, this);
        gridAdapter = new ImageGridViewAdapter(this, startUpPictures, nextPictures);
        gridView = (GridView) findViewById(R.id.act_main_gridView);
        gridView.setAdapter(gridAdapter);
        gridView.setMultiChoiceModeListener(new MyMultipleChoiceListener());
        gridView.setOnItemLongClickListener(new MyOnItemLongClickListener());
        gridView.setOnTouchListener(new MyOnTouchListener());
        setGridViewClickListener();
        initAdvertise();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.topbar_action_favorite:
                favButtonOnClick();
                return true;
            case R.id.topbar_action_settings:

                return true;
            case R.id.topbar_action_share:
                sharebuttonOnClick();
                return true;
            case R.id.topbar_action_delete:
                deleteButtonOnClick();
                return true;
            case R.id.topbar_action_help:
                helpButtonOnClick();
                return true;
            case R.id.topbar_action_database:
//                databaseOnClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    protected void onStart() {
        super.onStart();
        time = 0;
    }

    protected void onStop() {
        super.onStop();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                time = 5;
            }
        }, 48 * 60 * 60 * 1000);

        Intent myIntent = new Intent(this, PushUpNotification.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent PendI = PendingIntent.getBroadcast(this, 101, myIntent, 0);

        if (time == 5) {
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + (5 * 24 * 60 * 60 * 1000), 5 * 24 * 60 * 60 * 1000, PendI);
            time = 7;
        } else if (time == 7) {
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + (7 * 24 * 60 * 60 * 1000), 7 * 24 * 60 * 60 * 1000, PendI);
        } else {
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + (48 * 60 * 60 * 1000), 48 * 60 * 60 * 1000, PendI);
        }

    }

    private void initAdvertise() {
        AdView mAdView = (AdView) findViewById(R.id.act_main_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void handleShakeEvent(int count) {
        if (count > SHAKE_LIMIT) {
            clearSelection();
            gridAdapter.setNextImages(imageFetcher.getNextRandomImages(NUMBER_OF_ITEMS, this));
            gridAdapter.notifyDataSetChanged();
        }
    }
    private void helpButtonOnClick() {
        Intent intent = new Intent(MainActivity.this, HelpScreenActivity.class);
        startActivity(intent);
    }


    private void sharebuttonOnClick() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        sendIntent.setType("image/*");
        if (gridView.getCheckedItemPositions() != null) {
            SparseBooleanArray checked = gridView.getCheckedItemPositions();
            ArrayList<Uri> shareList = new ArrayList<Uri>();
            for (int i = 0; i < gridView.getCount(); i++)
                if (checked.get(i)) {
                    shareList.add(gridAdapter.getUriList().get(i).getUri());
                }
            sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, shareList);
            startActivity(Intent.createChooser(sendIntent, "Share via"));
        } else Toast.makeText(this, R.string.act_main_toast_share, Toast.LENGTH_SHORT).show();
    }

    private void deleteButtonOnClick() {
        if (gridView.getCheckedItemPositions() != null) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.act_main_alert_title_delete_images)
                    .setMessage(R.string.act_main_alert_msg_delete_images)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteImages();
                        }

                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            clearSelection();
                        }

                    })
                    .show();
        } else {
            Toast.makeText(this, R.string.act_main_toast_delete, Toast.LENGTH_SHORT).show();
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

        this.imageFetcher.deleteImages(del_map);
        this.imageFetcher.replaceDeletedImages(checked, gridAdapter, del_map, this);
        clearSelection();
        gridAdapter.notifyDataSetChanged();
    }

    private void backButtonOnClick() {
        if (this.imageFetcher.getNumberOfPichtures() == 0) {
            return;
        }

        clearSelection();
        gridAdapter.setPreviousImages(imageFetcher.getNextRandomImages(NUMBER_OF_ITEMS, this));
    }

    private void favButtonOnClick() {
        if (DbDatasource.getInstance(MainActivity.this).getAllFavorites().isEmpty())
        {
            Toast.makeText(this, R.string.act_main_toast_fav, Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent intent = new Intent(MainActivity.this, FavouriteActivity.class);
            startActivity(intent);
        }

    }

//    private void databaseOnClick(){
//        Intent dbmanager = new Intent(MainActivity.this, AndroidDatabaseManager.class);
//        startActivity(dbmanager);
//    }

    private void nextButtonOnClick() {
        clearSelection();
        gridAdapter.setNextImages(imageFetcher.getNextRandomImages(NUMBER_OF_ITEMS, this));
    }

    @Override
    public void onBackPressed() {
        if (gridView.getChoiceMode() != AbsListView.CHOICE_MODE_MULTIPLE_MODAL)
            super.onBackPressed();
        clearSelection();
    }

    private void clearSelection() {
        if (gridView.getChoiceMode() == AbsListView.CHOICE_MODE_MULTIPLE_MODAL) {
            for (int i = 0; i < gridView.getCount(); i++) {
                ((ViewHolder) gridView.getChildAt(i).getTag()).image.setImageAlpha(ALPHA_FULL_VISIBLE);
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
                ViewHolder item = (ViewHolder) gridView.getChildAt(position).getTag();
                ArrayList<UriWrapper> uriListNew = new ArrayList<UriWrapper>();
                for (UriWrapper uriWrapper : gridAdapter.getUriList()) {
                    uriListNew.add(DbDatasource.getInstance(MainActivity.this).getUriWrapper(uriWrapper.getUri()));
                }
                gridAdapter.updateFavStatus(uriListNew);
                if ((item.fav.getVisibility()) == View.VISIBLE) {
                    item.fav.setVisibility(View.INVISIBLE);
                } else {
                    item.fav.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSingleClick(View v, int position) {
                Intent intent = new Intent(MainActivity.this, FullscreenActivity.class);
                intent.setData(gridAdapter.getUriList().get(position).getUri());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ready) {
            ArrayList<UriWrapper> uriListNew = new ArrayList<UriWrapper>();
            for (UriWrapper uriWrapper : gridAdapter.getUriList()) {
                uriListNew.add(DbDatasource.getInstance(MainActivity.this).getUriWrapper(uriWrapper.getUri()));
            }

            for (int position = 0; position < 6; position++) {
                if (gridView.getChildAt(position).getTag() != null) {
                    ViewHolder item = (ViewHolder) gridView.getChildAt(position).getTag();
                    if (uriListNew.get(position).isFav()) {
                        item.fav.setVisibility(View.VISIBLE);
                    } else {
                        item.fav.setVisibility(View.INVISIBLE);
                    }
                }
            }
            gridAdapter.updateFavStatus(uriListNew);
        } else {
            ready = true;
        }
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);

    }

    @Override
    public void onPause() {
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    public Toolbar getTopBar(){
        return this.topBar;
    }

    private class HelpScreenListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, HelpScreenActivity.class);
            startActivity(intent);
        }
    }

    private class MyOnShakeListener implements ShakeDetector.OnShakeListener {
        @Override
        public void onShake(int count) {
            handleShakeEvent(count);
        }
    }

    private class MyMultipleChoiceListener implements AbsListView.MultiChoiceModeListener {
        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

            ViewHolder item = (ViewHolder) gridView.getChildAt(position).getTag();
            if (!gridView.isItemChecked(position)) {
                gridView.setItemChecked(position, true);
                item.checked.setVisibility(View.VISIBLE);
                item.image.setImageAlpha(ALPHA_HALF_VISIBLE);
            } else {
                item.checked.setVisibility(View.INVISIBLE);
                item.image.setImageAlpha(ALPHA_FULL_VISIBLE);
                gridView.setItemChecked(position, false);
            }
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ViewHolder item = (ViewHolder) gridView.getChildAt(position).getTag();
                    if (!gridView.isItemChecked(position)) {
                        gridView.setItemChecked(position, true);
                        item.checked.setVisibility(View.VISIBLE);
                        item.image.setImageAlpha(ALPHA_HALF_VISIBLE);
                    } else {
                        item.checked.setVisibility(View.INVISIBLE);
                        item.image.setImageAlpha(ALPHA_FULL_VISIBLE);
                        gridView.setItemChecked(position, false);
                        if (gridView.getCheckedItemCount() == 0)
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
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, FullscreenActivity.class);
                    intent.setData(gridAdapter.getUriList().get(position).getUri());
                    startActivity(intent);
                }
            });
            gridView.setChoiceMode(AbsListView.CHOICE_MODE_NONE);

        }


    }

    private class MyOnItemLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            gridView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
            ViewHolder item = (ViewHolder) gridView.getChildAt(position).getTag();
            if (!gridView.isItemChecked(position)) {
                gridView.setItemChecked(position, true);
                item.checked.setVisibility(View.VISIBLE);
                item.image.setImageAlpha(ALPHA_HALF_VISIBLE);
            } else {
                item.checked.setVisibility(View.INVISIBLE);
                item.image.setImageAlpha(ALPHA_FULL_VISIBLE);
                gridView.setItemChecked(position, false);
            }
            return true;
        }
    }

    private class MyOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x1 = event.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    x2 = event.getX();
                    float deltaX = x2 - x1;

                    if (Math.abs(deltaX) > MIN_DISTANCE) {
                        if (x2 > x1) {
                            backButtonOnClick();
                        } else {
                            nextButtonOnClick();
                        }

                    } else {
                        if (event.getAction() == MotionEvent.ACTION_MOVE) {
                            return true;
                        }
                    }
                    break;
            }
            return false;
        }
    }
}


