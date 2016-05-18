package at.tugraz.inffeldgroup.dailypic;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

public class FullscreenImage extends Activity {
    private float y1, y2;
    static final int MIN_DISTANCE = 50;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        SubsamplingScaleImageView imgDisplay;
        imgDisplay = (SubsamplingScaleImageView) findViewById(R.id.imgDisplay);
        imgDisplay.setImage(ImageSource.uri(getIntent().getData()));

        Button btnClose;
        btnClose = (Button) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FullscreenImage.this.finish();
            }
        });

        imgDisplay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        y1 = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        y2 = event.getX();
                        float deltaX = y2 - y1;

                        if (Math.abs(deltaX) > MIN_DISTANCE) {
                            if (y2 > y1) {
                                finish();
                            } else {
                            }

                        } else {
                            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                                return true;
                            }
                        }
                        break;
                }

                return false;
            }

        });
    }

    public void onFullScreenClick(View v) {
        FullscreenImage.this.finish();
    }
}
