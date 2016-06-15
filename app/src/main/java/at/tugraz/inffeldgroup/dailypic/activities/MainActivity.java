package at.tugraz.inffeldgroup.dailypic.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
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
    public static int NUMBER_OF_ITEMS = 6;
    public static int NUMBER_OF_PRELOADITEMS = 12;
    public static int NUMBER_OF_ROWS = 2;
    public static int HELPER_NUMBER_OF_ITEMS = 4;
    public static boolean FAV = false;
    private static final int ALPHA_HALF_VISIBLE = 127;
    private static final int ALPHA_FULL_VISIBLE = 255;
    private static final int MIN_DISTANCE = 150;
    private static final int SHAKE_LIMIT = 3;
    private boolean ready = false;

    private ImageFetcher imageFetcher;
    //private GridView gridView;
    private ViewAnimator gridAnimator;
    private ImageGridViewAdapter gridAdapter;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    private float x1_in = 0, x1_out = 0,x2_in = 0, x2_out = 0, y1_in = 0,y1_out = 0,y2_in = 0,y2_out = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar topBar = (Toolbar) findViewById(R.id.act_main_toolbar);
        setSupportActionBar(topBar);
        topBar.setTitle("");

        this.imageFetcher = new ImageFetcher(MainActivity.this);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new MyOnShakeListener());
        ArrayList<UriWrapper> startUpPictures = imageFetcher.getNextRandomImages(NUMBER_OF_PRELOADITEMS, this);
        ArrayList<UriWrapper> nextPictures = imageFetcher.getNextRandomImages(NUMBER_OF_PRELOADITEMS, this);
        gridAdapter = new ImageGridViewAdapter(this, startUpPictures, nextPictures);
        gridAnimator=(ViewAnimator)findViewById(R.id.viewGridAnimator);
        {
            GridView gridView = new GridView(this);
            gridView.setNumColumns(NUMBER_OF_ROWS);
            gridView.setAdapter(gridAdapter);
            gridView.setMultiChoiceModeListener(new MyMultipleChoiceListener());
            gridView.setOnItemLongClickListener(new MyOnItemLongClickListener());
            gridView.setOnTouchListener(new MyOnTouchListener());
            setGridViewClickListener(gridView);
            gridAdapter.shorten_list_items(NUMBER_OF_ITEMS);

            gridAdapter.notifyDataSetChanged();
            gridAnimator.addView(gridView);
        }
        Animation in = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        Animation out = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
        gridAnimator.setInAnimation(in);
        gridAnimator.setOutAnimation(out);

        gridAnimator.setAnimateFirstView(true);
        initAdvertise();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_topbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.topbar_action_favourite:
                MainActivity.FAV = true;
                favButtonOnClick();
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
                databaseOnClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    protected void onStop() {
        super.onStop();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run(){

            }
        }, 48 * 60 * 60 * 1000);

        Intent myIntent = new Intent(this, PushUpNotification.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent PendI = PendingIntent.getBroadcast(this, 101, myIntent, 0);

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime()
                + (48 * 60 * 60 * 1000), 7 * 24 * 60 * 60 * 1000, PendI);

    }

    private void initAdvertise() {
        AdView mAdView = new AdView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mAdView.setAdUnitId("ca-app-pub-3406938862137540/3927782716");
        mAdView.setBackgroundColor(Color.BLACK);
        AdRequest adRequest = new AdRequest.Builder().build();
        double dppxl = (1 * (Resources.getSystem().getDisplayMetrics().densityDpi / 160f));
        int v = this.getResources().getDisplayMetrics().heightPixels;
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.act_main_baseLayout);
        layout.addView(mAdView, params);
        if(v/dppxl > 720)
            mAdView.setAdSize(AdSize.BANNER);
        else
          mAdView.setAdSize(AdSize.SMART_BANNER);
        mAdView.loadAd(adRequest);
    }

    private void handleShakeEvent(int count) {
        if (count > SHAKE_LIMIT) {
            clearSelection();
            {
                GridView gridView = new GridView(this);
                gridView.setNumColumns(NUMBER_OF_ROWS);
                gridView.setAdapter(gridAdapter);
                gridView.setMultiChoiceModeListener(new MyMultipleChoiceListener());
                gridView.setOnItemLongClickListener(new MyOnItemLongClickListener());
                gridView.setOnTouchListener(new MyOnTouchListener());
                setGridViewClickListener(gridView);
                gridAdapter.setNextImages(imageFetcher.getNextRandomImages(NUMBER_OF_PRELOADITEMS, this));
                gridAdapter.shorten_list_items(NUMBER_OF_ITEMS);
                gridAdapter.notifyDataSetChanged();

                if (gridAnimator.getChildCount()==4)
                {
                    gridAnimator.removeViewAt(0);
                }
                gridAnimator.addView(gridView);
            }
            Animation in = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
            Animation out = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
            gridAnimator.setInAnimation(in);
            gridAnimator.setOutAnimation(out);
            gridAnimator.showNext();
        }
    }
    private void helpButtonOnClick() {
        Intent intent = new Intent(MainActivity.this, HelpScreenActivity.class);
        startActivity(intent);
    }

    public void sharebuttonOnClick() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        sendIntent.setType("image/*");
        if (((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).getCheckedItemPositions() != null) {
            SparseBooleanArray checked = ((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).getCheckedItemPositions();
            ArrayList<Uri> shareList = new ArrayList<Uri>();
            for (int i = 0; i < ((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).getCount(); i++)
                if (checked.get(i)) {
                    shareList.add(gridAdapter.getUriList().get(i).getUri());
                }
            sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, shareList);
            startActivity(Intent.createChooser(sendIntent, "Share via"));
        } else Toast.makeText(this, R.string.act_main_toast_share, Toast.LENGTH_SHORT).show();
    }

    private void deleteButtonOnClick() {
        if (((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).getCheckedItemPositions() != null) {
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
        SparseBooleanArray checked = ((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).getCheckedItemPositions();
        HashMap<Integer, ImageGridViewAdapter.ViewHolder> del_map = new HashMap<Integer, ViewHolder>();
        for (int i = 0; i < ((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).getCount(); i++) {
            if (checked.get(i)) {
                del_map.put(i, (ViewHolder) ((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).getChildAt(i).getTag());
            }
        }

        this.imageFetcher.deleteImages(del_map);
        this.imageFetcher.replaceDeletedImages(checked, gridAdapter, del_map, this);
        clearSelection();
        gridAdapter.shorten_list_items(NUMBER_OF_ITEMS);
        gridAdapter.notifyDataSetChanged();
    }

    private void backButtonOnClick() {

        if (this.imageFetcher.getNumberOfPichtures() == 0) {
            return;
        }
        boolean history;
        clearSelection();
        {
            GridView gridView = new GridView(this);
            gridView.setNumColumns(NUMBER_OF_ROWS);
            gridView.setAdapter(gridAdapter);
            gridView.setMultiChoiceModeListener(new MyMultipleChoiceListener());
            gridView.setOnItemLongClickListener(new MyOnItemLongClickListener());
            gridView.setOnTouchListener(new MyOnTouchListener());
            setGridViewClickListener(gridView);
            history = gridAdapter.setPreviousImages(imageFetcher.getNextRandomImages(NUMBER_OF_PRELOADITEMS, this));
            gridAnimator.addView(gridView,0);
        }
        if(history)
        {
            Animation in = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
            Animation out = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);
            gridAnimator.setInAnimation(in);
            gridAnimator.setOutAnimation(out);
            gridAnimator.showPrevious();
            if (gridAnimator.getChildCount()==5)
            {
                gridAnimator.removeViewAt(4);
            }
            else
            {
                if (gridAnimator.getChildCount()==4)
                {
                    gridAnimator.removeViewAt(3);
                }
            }
        }
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

    private void databaseOnClick(){
        Intent dbmanager = new Intent(MainActivity.this, AndroidDatabaseManager.class);
        startActivity(dbmanager);
    }

    private void nextButtonOnClick() {
        clearSelection();
        {
            GridView gridView = new GridView(this);
            gridView.setNumColumns(NUMBER_OF_ROWS);
            gridView.setAdapter(gridAdapter);
            gridView.setMultiChoiceModeListener(new MyMultipleChoiceListener());
            gridView.setOnItemLongClickListener(new MyOnItemLongClickListener());
            gridView.setOnTouchListener(new MyOnTouchListener());
            setGridViewClickListener(gridView);
            gridAdapter.setNextImages(imageFetcher.getNextRandomImages(NUMBER_OF_PRELOADITEMS, this));
            gridAdapter.shorten_list_items(NUMBER_OF_ITEMS);
            gridAdapter.notifyDataSetChanged();
            if (gridAnimator.getChildCount()==4)
            {
                gridAnimator.removeViewAt(0);
            }
            gridAnimator.addView(gridView);
        }
        Animation in = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        Animation out = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
        gridAnimator.setInAnimation(in);
        gridAnimator.setOutAnimation(out);
        gridAnimator.showNext();
    }

    @Override
    public void onBackPressed() {
        if (((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).getChoiceMode() != AbsListView.CHOICE_MODE_MULTIPLE_MODAL)
            super.onBackPressed();
        clearSelection();
    }

    private void clearSelection() {
        if (((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).getChoiceMode() == AbsListView.CHOICE_MODE_MULTIPLE_MODAL) {
            for (int i = 0; i < ((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).getCount(); i++) {
                ((ViewHolder) ((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).getChildAt(i).getTag()).image.setImageAlpha(ALPHA_FULL_VISIBLE);
                ((ViewHolder) ((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).getChildAt(i).getTag()).checked.setVisibility(View.INVISIBLE);
                ((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).setItemChecked(i, false);
            }
            ((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).setChoiceMode(AbsListView.CHOICE_MODE_NONE);
            setGridViewClickListener(((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())));
        }
    }

    private void setGridViewClickListener(GridView gridView) {
        gridView.setOnItemClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick(View v, int position) {
                UriWrapper uri = gridAdapter.getUriList().get(position);
                FavouriteHandler.toggleFavouriteState(MainActivity.this, uri);
                ViewHolder item = (ViewHolder) ((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).getChildAt(position).getTag();
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

            for (int position = 0; position < NUMBER_OF_ITEMS; position++) {
                if(((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).getChildAt(position) != null) {
                    if (((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).getChildAt(position).getTag() != null) {
                        ViewHolder item = (ViewHolder) ((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).getChildAt(position).getTag();
                        if (uriListNew.get(position).isFav()) {
                            item.fav.setVisibility(View.VISIBLE);
                        } else {
                            item.fav.setVisibility(View.INVISIBLE);
                        }
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

            ViewHolder item = (ViewHolder) ((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).getChildAt(position).getTag();
            if (!((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).isItemChecked(position)) {
                ((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).setItemChecked(position, true);
                item.checked.setVisibility(View.VISIBLE);
                item.image.setImageAlpha(ALPHA_HALF_VISIBLE);
            } else {
                item.checked.setVisibility(View.INVISIBLE);
                item.image.setImageAlpha(ALPHA_FULL_VISIBLE);
                ((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).setItemChecked(position, false);
            }
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            ((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ViewHolder item = (ViewHolder) ((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).getChildAt(position).getTag();
                    if (!((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).isItemChecked(position)) {
                        ((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).setItemChecked(position, true);
                        item.checked.setVisibility(View.VISIBLE);
                        item.image.setImageAlpha(ALPHA_HALF_VISIBLE);
                    } else {
                        item.checked.setVisibility(View.INVISIBLE);
                        item.image.setImageAlpha(ALPHA_FULL_VISIBLE);
                        ((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).setItemChecked(position, false);
                        if (((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).getCheckedItemCount() == 0)
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
            ((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, FullscreenActivity.class);
                    intent.setData(gridAdapter.getUriList().get(position).getUri());
                    startActivity(intent);
                }
            });
            ((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).setChoiceMode(AbsListView.CHOICE_MODE_NONE);

        }


    }

    private class MyOnItemLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            ((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
            ViewHolder item = (ViewHolder) ((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).getChildAt(position).getTag();
            if (!((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).isItemChecked(position)) {
                ((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).setItemChecked(position, true);
                item.checked.setVisibility(View.VISIBLE);
                item.image.setImageAlpha(ALPHA_HALF_VISIBLE);
            } else {
                item.checked.setVisibility(View.INVISIBLE);
                item.image.setImageAlpha(ALPHA_FULL_VISIBLE);
                ((GridView)gridAnimator.getChildAt(gridAnimator.getDisplayedChild())).setItemChecked(position, false);
            }
            return true;
        }
    }

    private class MyOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()&MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    x1_in = event.getX(event.getActionIndex());
                    y1_in = event.getY(event.getActionIndex());
                    //Toast.makeText(getApplicationContext(), "" + x1_in +" "+ y1_in, Toast.LENGTH_SHORT).show();
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    x2_in = event.getX(event.getActionIndex());
                    y2_in = event.getY(event.getActionIndex());
                    //Toast.makeText(getApplicationContext(), "" + x2_in +" "+ y2_in, Toast.LENGTH_SHORT).show();
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    x2_out = event.getX(event.getActionIndex());
                    y2_out = event.getY(event.getActionIndex());
                    //Toast.makeText(getApplicationContext(), "" + x2_out +" "+ y2_out, Toast.LENGTH_SHORT).show();
                    break;
                case MotionEvent.ACTION_UP:
                    x1_out = event.getX(event.getActionIndex());
                    y1_out = event.getY(event.getActionIndex());
                    //Toast.makeText(getApplicationContext(), "" + x1_out +" "+ y1_out, Toast.LENGTH_SHORT).show();
                    float deltaX = x1_in - x1_out;

                    if (x2_in==0&&x2_out==0&&y2_in==0&&y2_out==0)
                    {
                        if (Math.abs(deltaX) > MIN_DISTANCE&&(Math.abs(y1_in-y1_out)<300)) {
                            if ((x1_out > x1_in)) {
                                backButtonOnClick();
                            } else {
                                nextButtonOnClick();
                            }

                        } else {
                            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                                return true;
                            }
                        }
                    }
                    else
                    {
                        if ((Math.hypot(x1_in-x2_in,y1_in-y2_in))<(Math.hypot(x1_out-x2_out,y1_out-y2_out)))
                        {
                            //zoomin kleiner
                            if(NUMBER_OF_ROWS !=1)
                            {
                                if (NUMBER_OF_ROWS == 2)
                                {
                                    NUMBER_OF_PRELOADITEMS = 6;
                                }
                                NUMBER_OF_ITEMS = NUMBER_OF_ITEMS - HELPER_NUMBER_OF_ITEMS;
                                HELPER_NUMBER_OF_ITEMS = HELPER_NUMBER_OF_ITEMS-2;
                                NUMBER_OF_ROWS--;
                                {
                                    GridView gridView = new GridView(getApplicationContext());
                                    gridView.setNumColumns(NUMBER_OF_ROWS);
                                    gridView.setAdapter(gridAdapter);
                                    gridView.setMultiChoiceModeListener(new MyMultipleChoiceListener());
                                    gridView.setOnItemLongClickListener(new MyOnItemLongClickListener());
                                    gridView.setOnTouchListener(new MyOnTouchListener());
                                    setGridViewClickListener(gridView);
                                    gridAdapter.setNextImages(imageFetcher.getNextRandomImages(NUMBER_OF_PRELOADITEMS, getApplicationContext()));
                                    gridAdapter.shorten_list_items(NUMBER_OF_ITEMS);
                                    gridAdapter.notifyDataSetChanged();

                                    switch (gridAnimator.getDisplayedChild())
                                    {
                                        case 1:gridAnimator.removeViewAt(0);
                                            break;
                                        case 2:gridAnimator.removeViewAt(0);
                                            gridAnimator.removeViewAt(1);
                                            break;
                                    }
                                    gridAnimator.addView(gridView);
                                }
                                Animation in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_zoom_out);
                                Animation out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.nothing);
                                gridAnimator.setInAnimation(in);
                                gridAnimator.setOutAnimation(out);
                                gridAnimator.showNext();
                                gridAdapter.clearHistory();
                            }

                        }
                        else
                        {
                            //zoomout größer
                            if(NUMBER_OF_ROWS !=3)
                            {
                                NUMBER_OF_PRELOADITEMS = 12;
                                HELPER_NUMBER_OF_ITEMS = HELPER_NUMBER_OF_ITEMS+2;
                                NUMBER_OF_ITEMS = NUMBER_OF_ITEMS + HELPER_NUMBER_OF_ITEMS;
                                NUMBER_OF_ROWS++;
                                {
                                    GridView gridView = new GridView(getApplicationContext());
                                    gridView.setNumColumns(NUMBER_OF_ROWS);
                                    gridView.setAdapter(gridAdapter);
                                    gridView.setMultiChoiceModeListener(new MyMultipleChoiceListener());
                                    gridView.setOnItemLongClickListener(new MyOnItemLongClickListener());
                                    gridView.setOnTouchListener(new MyOnTouchListener());
                                    setGridViewClickListener(gridView);
                                    gridAdapter.setNextImages(imageFetcher.getNextRandomImages(NUMBER_OF_PRELOADITEMS, getApplicationContext()));
                                    gridAdapter.shorten_list_items(NUMBER_OF_ITEMS);
                                    gridAdapter.notifyDataSetChanged();
                                    switch (gridAnimator.getDisplayedChild())
                                    {
                                        case 1:gridAnimator.removeViewAt(0);
                                            break;
                                        case 2:gridAnimator.removeViewAt(0);
                                            gridAnimator.removeViewAt(1);
                                            break;
                                    }
                                    gridAnimator.addView(gridView);
                                }
                                Animation in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.nothing);
                                Animation out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_zoom_in);
                                gridAnimator.setInAnimation(in);
                                gridAnimator.setOutAnimation(out);
                                gridAnimator.showNext();
                                gridAdapter.clearHistory();


                            }
                        }
                        if (event.getAction() == MotionEvent.ACTION_MOVE) {
                            return true;
                        }
                    }
                    x1_in = 0;
                    x1_out = 0;
                    x2_in = 0;
                    x2_out = 0;
                    y1_in = 0;
                    y1_out = 0;
                    y2_in = 0;
                    y2_out = 0;
                    break;

            }
            return false;
        }
    }
}


