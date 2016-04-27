package at.tugraz.inffeldgroup.dailypic;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.GridView;

public class FavouriteActivity extends AppCompatActivity {
    private GridView gridView;
    //private FavouriteGridViewAdapter gridAdapter;
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = (GridView) findViewById(R.id.gridView);
        //gridAdapter = new FavouriteGridViewAdapter(this, R.layout.favourite_item, getData());
        adapter = new ArrayAdapter<ImageItem>(this, R.layout.favourite_item, getData());
        gridView.setAdapter(adapter);
    }

    private ArrayList<ImageItem> getData() {
        ArrayList<ImageItem> ret = new ArrayList<ImageItem>();

        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[] {MediaStore.Images.Media.DATA};
        Cursor curs = this.getContentResolver().query(images, projection, "", null, "");

        ArrayList<String> imagePaths = new ArrayList<String>();

        if(curs.moveToFirst()){
            int data_column = curs.getColumnIndex(MediaStore.Images.Media.DATA);

            String path;
            do{
                path = curs.getString(data_column);
                imagePaths.add(path);
            }while(curs.moveToNext());
        }

        curs.close();

        for (int i = 0; i < imagePaths.size(); i++) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePaths.get(i));
            ret.add(new ImageItem(bitmap));
        }
        return ret;
    }


}
