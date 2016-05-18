package at.tugraz.inffeldgroup.dailypic.activities;

import java.util.ArrayList;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import at.tugraz.inffeldgroup.dailypic.FavouriteHandler;
import at.tugraz.inffeldgroup.dailypic.ImageGridViewAdapter;
import at.tugraz.inffeldgroup.dailypic.R;
import at.tugraz.inffeldgroup.dailypic.db.AndroidDatabaseManager;
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
        gridAdapter = new ImageGridViewAdapter(this, uriList, new ArrayList<UriWrapper>());
        gridView = (GridView) findViewById(R.id.favGridView);
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick(View v, int position) {
                UriWrapper uri = uriList.get(position);
                FavouriteHandler.toggleFavouriteState(FavouriteActivity.this, uri);
                // Refresh grid view with removed favorite
                ArrayList<UriWrapper> uriListNew = DbDatasource.getInstance(FavouriteActivity.this).getAllFavorites();
                gridAdapter.setNewImages(uriListNew);
            }

            @Override
            public void onSingleClick(View v, int position) {
                Intent intent = new Intent(FavouriteActivity.this, FullscreenActivity.class);
                intent.setData(uriList.get(position).getUri());
                startActivity(intent);
            }
        });


        // TODO: ONLY FOR DEBUGGING
        TextView title = (TextView) findViewById(R.id.textView);
        if (title != null) {title.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent dbmanager = new Intent(FavouriteActivity.this, AndroidDatabaseManager.class);
                startActivity(dbmanager);
            }
        });}

    }
}
