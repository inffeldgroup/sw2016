package at.tugraz.inffeldgroup.dailypic;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class FullscreenImage extends Activity {


    @SuppressLint("NewApi")



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen_image);
        ZoomableImageView imgDisplay;
        imgDisplay = (ZoomableImageView) findViewById(R.id.imgDisplay);
            imgDisplay.setImageURI(getIntent().getData());

        Button btnClose;



        btnClose = (Button) findViewById(R.id.btnClose);


        btnClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FullscreenImage.this.finish();
            }
        });

    }

    public void onFullScreenClick(View v) {
        FullscreenImage.this.finish();
    }
}