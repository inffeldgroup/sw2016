package at.tugraz.inffeldgroup.dailypic;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

public class FullscreenImage extends Activity {


    @SuppressLint("NewApi")



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen_image);
        SubsamplingScaleImageView imgDisplay;
        imgDisplay = (SubsamplingScaleImageView) findViewById(R.id.imgDisplay);
        imgDisplay.setOrientation(SubsamplingScaleImageView.ORIENTATION_USE_EXIF);
        imgDisplay.setImage(ImageSource.uri(getIntent().getData()).tilingDisabled());
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