package at.tugraz.inffeldgroup.dailypic;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.view.View;

import static at.tugraz.inffeldgroup.dailypic.R.drawable.notify;

/**
 * Created by rebe on 4/27/16.
 */

public class pushup extends Activity {


    public void createNotification(View view) {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent("hallo");
        PendingIntent Intent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        // Build notification

        Notification notification;
        notification = new Notification.Builder(this)
                .setContentTitle("Hello ")
                .setContentText("Subject")
                .setSmallIcon(R.drawable.notify)
                .build();


        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, notification);

    }
} 
