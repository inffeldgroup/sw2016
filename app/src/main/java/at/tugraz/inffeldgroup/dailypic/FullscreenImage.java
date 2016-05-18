package at.tugraz.inffeldgroup.dailypic;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

public class FullscreenImage extends Activity implements View.OnTouchListener{
    private RelativeLayout baseLayout;

    private int previousFingerPosition = 0;
    private int baseLayoutPosition = 0;
    private int defaultViewHeight;

    private boolean isClosing = false;
    private boolean isScrollingUp = false;
    private boolean isScrollingDown = false;

    @SuppressLint("NewApi")

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

        baseLayout = (RelativeLayout) findViewById(R.id.activity_fullscreen_base);
        baseLayout.setOnTouchListener(this);
    }

    public void onFullScreenClick(View v) {
        FullscreenImage.this.finish();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d("","FullscreenImage.onTouch() was called");

        // Get finger position on screen
        final int Y = (int) event.getRawY();

        // Switch on motion event type
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                defaultViewHeight = baseLayout.getHeight();
                previousFingerPosition = Y;
                baseLayoutPosition = (int) baseLayout.getY();
                break;

            case MotionEvent.ACTION_UP:
                if(isScrollingUp){
                    baseLayout.setY(0);
                    isScrollingUp = false;
                }

                if(isScrollingDown){
                    baseLayout.setY(0);
                    baseLayout.getLayoutParams().height = defaultViewHeight;
                    baseLayout.requestLayout();
                    isScrollingDown = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(!isClosing){
                    int currentYPosition = (int) baseLayout.getY();

                    if(previousFingerPosition >Y){
                        if(!isScrollingUp){
                            isScrollingUp = true;
                        }

                    if(baseLayout.getHeight()<defaultViewHeight){
                        baseLayout.getLayoutParams().height = baseLayout.getHeight() - (Y - previousFingerPosition);
                        baseLayout.requestLayout();
                    }
                    else {
                        if ((baseLayoutPosition - currentYPosition) > defaultViewHeight / 4) {
                            return true;
                        }
                    }
                    baseLayout.setY(baseLayout.getY() + (Y - previousFingerPosition));

                }
                else{
                    if(!isScrollingDown){
                        isScrollingDown = true;
                    }
                    if (Math.abs(baseLayoutPosition - currentYPosition) > defaultViewHeight / 2)
                    {
                        closeDownAndDismissDialog(currentYPosition);
                        return true;
                    }

                    baseLayout.setY(baseLayout.getY() + (Y - previousFingerPosition));
                    baseLayout.getLayoutParams().height = baseLayout.getHeight() - (Y - previousFingerPosition);
                    baseLayout.requestLayout();
                }

                previousFingerPosition = Y;
            }
            break;
        }
        return true;
    }

    public void closeDownAndDismissDialog(int currentPosition){
        isClosing = true;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenHeight = size.y;
        ObjectAnimator positionAnimator = ObjectAnimator.ofFloat(baseLayout, "y", currentPosition, screenHeight+baseLayout.getHeight());
        positionAnimator.setDuration(300);
        positionAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation) {
                animation.start();
            }

            @Override
            public void onAnimationEnd(Animator animator)
            {
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                finish();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                animation.start();
            }
        });
        positionAnimator.start();
    }

}