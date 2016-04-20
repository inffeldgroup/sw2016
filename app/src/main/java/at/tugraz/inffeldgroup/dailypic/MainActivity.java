package at.tugraz.inffeldgroup.dailypic;


import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView im = (ImageView)findViewById(R.id.imageView);
        im.setImageResource(R.mipmap.ic_launcher);

        im.setOnClickListener(new View.OnClickListener() {


            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FullscreenImage.class);

                im.buildDrawingCache();
                Bitmap image= im.getDrawingCache();

                Bundle extras = new Bundle();
                extras.putParcelable("imagebitmap", image);
                intent.putExtras(extras);
                startActivity(intent);

            }



    });
}}
