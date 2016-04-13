package at.tugraz.inffeldgroup.dailypic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActionBar().hide();
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
    }
}
