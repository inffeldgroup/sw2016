package at.tugraz.inffeldgroup.dailypic;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
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
        Bundle extras = getIntent().getExtras();
        ImageView imgDisplay;
        imgDisplay = (ImageView) findViewById(R.id.imgDisplay);
        if(extras != null) {
            Bitmap bmp = (Bitmap) extras.getParcelable("imagebitmap");
            imgDisplay.setImageBitmap(bmp);
        }

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