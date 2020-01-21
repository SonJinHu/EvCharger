package com.example.user.myapplication.info;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;

import com.example.user.myapplication.Dd_Timer;
import com.example.user.myapplication.R;

import static android.content.Context.NOTIFICATION_SERVICE;

@RequiresApi(api = Build.VERSION_CODES.O)
public class TimerNotification {

    /*private static final int notifyID= 180526;
    private static final String CHANNEL_ID = "my_channel_01";*/

    private static final int notifyID = 180526;
    public static final String CHANNEL_ID = "my_channel_01";// The id of the channel.

    public static void setNotification(Context context, int remainsSeconds) {

        PendingIntent pIntent = PendingIntent.getActivity(context, 0, new Intent(context, Dd_Timer.class), 0);
        Notification mBuilder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_timer_black_24dp)
                .setContentTitle("타이머")
                .setContentText(Collection.formattedSecondsStr(remainsSeconds))

                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setPriority(Notification.PRIORITY_MIN)
                .setContentIntent(pIntent)
                .setChannelId(CHANNEL_ID)
                .build();

        NotificationManager mMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        assert mMgr != null;
        mMgr.notify(notifyID, mBuilder);
    }

    public static void cancelNotification(Context context) {
        NotificationManager mMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        assert mMgr != null;
        mMgr.cancel(notifyID);
    }

        /*PendingIntent pIntent = PendingIntent.getActivity(context, 0, new Intent(context, Dd_Timer.class), 0);
        Notification mBuilder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_timer_black_24dp)
                .setContentTitle("타이머")
                .setContentText(Collection.formattedSecondsStr(remainsSeconds))

                .setOngoing(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pIntent)
                .build();

        NotificationManager mMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        assert mMgr != null;
        mMgr.notify(notifyID, mBuilder);
    }

    public static void cancelNotification(Context context) {
        NotificationManager mMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        assert mMgr != null;
        mMgr.cancel(notifyID);
    }*/
}
