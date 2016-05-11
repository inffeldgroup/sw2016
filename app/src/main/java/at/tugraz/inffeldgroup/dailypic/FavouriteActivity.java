package at.tugraz.inffeldgroup.dailypic;

import java.util.ArrayList;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import at.tugraz.inffeldgroup.dailypic.db.DbDatasource;
import at.tugraz.inffeldgroup.dailypic.db.UriWrapper;
import at.tugraz.inffeldgroup.dailypic.util.DoubleClickListener;

public class FavouriteActivity extends AppCompatActivity {
    private GridView gridView;
    private ImageGridViewAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        final ArrayList<UriWrapper> uriList = DbDatasource.getInstance(this).getAllFavorites();
        if (uriList == null) {
            Toast.makeText(this, "No favourite pictures selected.", Toast.LENGTH_LONG).show();
            return;
        }
        gridAdapter = new ImageGridViewAdapter(this, uriList);
        gridView = (GridView) findViewById(R.id.favGridView);
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick(View v, int position) {
                UriWrapper uri = uriList.get(position);
                FavouriteHandler.toggleFavouriteState(FavouriteActivity.this, uri);
                // Refresh grid view with removed favorite
                ArrayList<UriWrapper> uriListNew = DbDatasource.getInstance(FavouriteActivity.this).getAllFavorites();
                gridAdapter = new ImageGridViewAdapter(FavouriteActivity.this, uriListNew);
                gridView.setAdapter(gridAdapter);
                gridView.invalidate();
            }

            @Override
            public void onSingleClick(View v, int position) {
                Intent intent = new Intent(FavouriteActivity.this, FullscreenImage.class);
                intent.setData(uriList.get(position).getUri());
                startActivity(intent);
            }
        });

    }
}
