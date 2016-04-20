package at.tugraz.inffeldgroup.dailypic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("[DEBUG]", "App starting.");

        ImageFetcher.getInstance().init(MainActivity.this);

        Log.d("[DEBUG]", "PREVIOUS:");
        ImageFetcher.getInstance().getPreviousRandomImagePaths();
        Log.d("[DEBUG]", "NEXT:");
        ImageFetcher.getInstance().getNextRandomImagePaths();
        Log.d("[DEBUG]", "NEXT:");
        ImageFetcher.getInstance().getNextRandomImagePaths();
        Log.d("[DEBUG]", "NEXT:");
        ImageFetcher.getInstance().getNextRandomImagePaths();
        Log.d("[DEBUG]", "PREVIOUS:");
        ImageFetcher.getInstance().getPreviousRandomImagePaths();
        Log.d("[DEBUG]", "NEXT:");
        ImageFetcher.getInstance().getNextRandomImagePaths();

    }
}
