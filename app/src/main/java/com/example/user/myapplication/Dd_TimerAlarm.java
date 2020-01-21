package com.example.user.myapplication;

import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.user.myapplication.info.Collection;

public class Dd_TimerAlarm extends AppCompatActivity {

    Ringtone ringtone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN);


        Animation anim = AnimationUtils.loadAnimation(this, R.anim.animation);
        TextView tv = findViewById(R.id.alarm_tv);
        tv.startAnimation(anim);

        ringtone = RingtoneManager.getRingtone(this, Collection.getSharedRingtone(this));
        ringtone.setStreamType(AudioManager.STREAM_ALARM);
        ringtone.play();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ringtone.stop();
    }
}