package at.tugraz.inffeldgroup.dailypic;

import java.io.File;
import java.util.ArrayList;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class FavouriteActivity extends AppCompatActivity {
    private GridView gridView;
    private ImageGridViewAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        final ArrayList<Uri> uriList = getImgUri();

        gridAdapter = new ImageGridViewAdapter(this, uriList);
        gridView = (GridView) findViewById(R.id.favGridView);
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FavouriteActivity.this, FullscreenImage.class);
                intent.setData(uriList.get(position));
                startActivity(intent);
            }
        });

    }

    private ArrayList<Uri> getImgUri() {
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[] {MediaStore.Images.Media.DATA};
        Cursor cursor = this.getContentResolver().query(images, projection, "", null, "");

        ArrayList<String> paths_all = new ArrayList<String>();
        ArrayList<Uri> paths_specific = new ArrayList<Uri>();

        if(cursor.moveToFirst()) {
            int data_column = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

            String path_string;
            do {
                path_string = cursor.getString(data_column);
                paths_all.add(path_string);
            } while (cursor.moveToNext());

            Uri path_uri;
            for(int i = 0; i < cursor.getCount(); i++){
                path_uri = Uri.fromFile(new File(paths_all.get(i)));
                paths_specific.add(path_uri);
            }

        }
        cursor.close();

        if (paths_specific.size() == 0) {
            return null;
        }
        return paths_specific;

    }




}
