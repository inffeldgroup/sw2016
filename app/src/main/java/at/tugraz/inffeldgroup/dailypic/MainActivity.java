package at.tugraz.inffeldgroup.dailypic;

import android.content.Intent;
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
        img_list = uriList;

        gridAdapter = new ImageGridViewAdapter(this, uriList);
        gridView = (GridView) findViewById(R.id.mainGridView);
        gridView.setAdapter(gridAdapter);


        gridView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                Log.d("xD", Integer.toString(position) + " " + Boolean.toString(gridView.isItemChecked(position)));
                if(!gridView.isItemChecked(position)){
                    gridView.setItemChecked(position, true);

                    gridView.setAlpha(0.5f);
                }
                else {
                    gridView.setAlpha(1);
                    gridView.setItemChecked(position, false);
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(!gridView.isItemChecked(position)){
                            gridView.setItemChecked(position, true);

                            view.setAlpha(0.5f);
                        }
                        else {
                            view.setAlpha(1);
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
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if(!item.isChecked()){
                    item.setChecked(true);
                    item.getActionView().setAlpha(0.5f);
                }
                else {
                    item.getActionView().setAlpha(1);
                    item.setChecked(false);
                }
                return false;
            }

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
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, FullscreenImage.class);
                intent.setData(uriList.get(position));
                startActivity(intent);
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override

            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                gridView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
                Log.d("xD", Integer.toString(position) + " " + Boolean.toString(gridView.isItemChecked(position)));
                if(!gridView.isItemChecked(position)){
                    gridView.setItemChecked(position, true);

                    view.setAlpha(0.5f);
                }
                else {
                    view.setAlpha(1);
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
                    shareList.add(img_list.get(i));
                }
            sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, shareList);
            startActivity(sendIntent);
        }
    }

    public void backButtonOnClick(View v) {
        final ArrayList<Uri> uriList = this.img_fetcher.getPrevRandomImages(numberOfItems);
        img_list = uriList;
        clearSelection();
        gridAdapter.setNewImages(uriList);
        gridAdapter.notifyDataSetChanged();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, FullscreenImage.class);
                intent.setData(uriList.get(position));
                startActivity(intent);
            }
        });
    }

    public void favButtonOnClick(View v){
        Intent intent = new Intent(MainActivity.this, FavouriteActivity.class);
        startActivity(intent);
    }

    public void nextButtonOnClick(View v) {
        final ArrayList<Uri> uriList = this.img_fetcher.getNextRandomImages(numberOfItems);
        img_list = uriList;
        clearSelection();
        gridAdapter.setNewImages(uriList);
        gridAdapter.notifyDataSetChanged();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, FullscreenImage.class);
                intent.setData(uriList.get(position));
                startActivity(intent);
            }
        });
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
                gridView.getChildAt(i).setAlpha(1);
                gridView.setItemChecked(i, false);
            }
            gridView.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, FullscreenImage.class);
                    intent.setData(img_list.get(position));
                    startActivity(intent);
                }
            });
        }    }
}



