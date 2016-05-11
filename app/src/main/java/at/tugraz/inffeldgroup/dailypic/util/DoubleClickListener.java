package at.tugraz.inffeldgroup.dailypic.util;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;

/**
 * Custom implementation for single and double click listener
 * that avoids using onTouch-Listener,
 * so that it can be easily used for grid views and
 * other more complicated containers.
 */
public abstract class DoubleClickListener implements AdapterView.OnItemClickListener {
    private static final long DOUBLE_CLICK_TIME_DELTA = 500; //milliseconds

    private CountDownTimer timer;
    long lastClickTime = 0;

    private void startSingleClickTimer(final View view, final int position) {
        lastClickTime = System.currentTimeMillis();
        timer = new CountDownTimer(DOUBLE_CLICK_TIME_DELTA, DOUBLE_CLICK_TIME_DELTA) {
            @Override
            public void onTick(long l) {}

            @Override
            public void onFinish() {
                onSingleClick(view, position);
            }
        };
        timer.start();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {

        if (lastClickTime == 0) {
            startSingleClickTimer(view, position);
        } else {
            if (System.currentTimeMillis() - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                timer.cancel();
                lastClickTime = 0;
                onDoubleClick(view, position);
            } else {
                if (timer != null) {
                    timer.cancel();
                }
                startSingleClickTimer(view, position);
            }
        }
    }

    public abstract void onSingleClick(View v, int position);
    public abstract void onDoubleClick(View v, int position);
}