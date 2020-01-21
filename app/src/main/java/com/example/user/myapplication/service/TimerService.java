package com.example.user.myapplication.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import androidx.annotation.RequiresApi;
import android.util.Log;

import com.example.user.myapplication.Dd_Timer;
import com.example.user.myapplication.Dd_TimerAlarm;
import com.example.user.myapplication.R;
import com.example.user.myapplication.info.Collection;

import static com.example.user.myapplication.info.TimerNotification.CHANNEL_ID;

public class TimerService extends Service {

    /* hand it over to TimerActivity */
    public static int remainsSeconds;
    boolean switchService;
    PowerManager.WakeLock mWakeLock;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i("[SON]", "onCreate() from TimerService.class");
        super.onCreate();
        /*PowerManager mPowerMgr = (PowerManager) getSystemService(POWER_SERVICE);
        assert mPowerMgr != null;
        mWakeLock = mPowerMgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "cpuWakeLock");
        mWakeLock.acquire(10 * 60 * 1000L *//*10 minutes*//*);*/

        switchService = true;
        new ServiceThread().start();
    }

    @Override
    public void onDestroy() {
        Log.i("[SON]", "onDestroy() from TimerService.class");
        super.onDestroy();
        /*mWakeLock.release();*/

        switchService = false;
    }


    class ServiceThread extends Thread {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            int settingSeconds = Dd_Timer.settingSeconds;
            int count = 0;
            while (count <= settingSeconds && switchService) {
                remainsSeconds = settingSeconds - count;
                Log.i("[SON]", "remainsSeconds : " + remainsSeconds + " from TimerService.class");
                count++;

                if (remainsSeconds == 0) {
                    alarm();
                }

                if (!Dd_Timer.isTimerActivityVisible) {
                    startForeground(180526, setNotification(remainsSeconds));
                } else {
                    stopForeground(true);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void alarm() {
        if (Dd_Timer.isTimerActivityVisible) {
            return;
        }

        switchService = false;
        stopSelf();

        Intent intent = new Intent(this, Dd_TimerAlarm.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    Notification setNotification(int remainsSeconds) {
        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), Dd_Timer.class), 0);
        return new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_timer_black_24dp)
                .setContentTitle("타이머")
                .setContentText(Collection.formattedSecondsStr(remainsSeconds))

                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setPriority(Notification.PRIORITY_MIN)
                .setContentIntent(pIntent)
                .setChannelId(CHANNEL_ID)
                .build();
    }

}