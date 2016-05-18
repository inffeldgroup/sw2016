package at.tugraz.inffeldgroup.dailypic.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import at.tugraz.inffeldgroup.dailypic.R;

public class FullscreenActivity extends Activity {
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
        btnClose.setOnClickListener(new MyOnClickListener());
    }

    public void onFullScreenClick(View v) {
        FullscreenActivity.this.finish();
    }

    private class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            FullscreenActivity.this.finish();
        }
    }
}