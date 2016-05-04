package at.tugraz.inffeldgroup.dailypic;

import java.io.File;
import java.util.ArrayList;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class FavouriteActivity extends AppCompatActivity {
    private GridView gridView;
    private ImageGridViewAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        final ArrayList<Uri> uriList = getImgUri();
        if (uriList == null) {
            Toast.makeText(this, "No favourite pictures selected.", Toast.LENGTH_LONG).show();
            return;
        }
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
        String folder = FavouriteHandler.fav_folder_path;

        ArrayList<Uri> paths_specific = new ArrayList<Uri>();

        File f = new File(folder);
        if (!f.exists()) {
            return null;
        }

        for (File img : f.listFiles()) {
            if (img.isFile()) {
                paths_specific.add(Uri.fromFile(img));
            }
        }
        if (paths_specific.size() == 0) {
            return null;
        }

        if (paths_specific.size() == 0) {
            return null;
        }
        return paths_specific;
    }

}
