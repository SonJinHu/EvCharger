package com.example.user.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.myapplication.info.Collection;
import com.example.user.myapplication.info.TimerNotification;
import com.example.user.myapplication.service.TimerService;

import java.util.Objects;

public class Dd_Timer extends AppCompatActivity implements View.OnClickListener {

    private final int RINGTONE_REQUEST_CODE = 0;
    private final int SEND_THREAD_MESSAGE = 1;

    /* set or not set notification in Service by this  */
    public static boolean isTimerActivityVisible;
    /* hand it over to service */
    public static int settingSeconds;
    boolean switchPower;

    View constraint_start, constraint_ongoing;
    NumberPicker np_hour, np_min, np_sec;
    TextView tv_display;
    Button bt_start, bt_pause, bt_continue, bt_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dd_fragment_alarm);
        initToolbar();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setView();
        isTimerActivityVisible = true;
        TimerNotification.cancelNotification(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        switchPower = false;
        isTimerActivityVisible = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStop() {
        super.onStop();
        isTimerActivityVisible = false;
        if (Collection.isServiceRunning(this, TimerService.class)) {
            TimerNotification.setNotification(getApplicationContext(), TimerService.remainsSeconds);
        }
    }

    void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("타이머");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
    }

    void initView() {
        constraint_start = findViewById(R.id.dd_constraint_start);
        constraint_ongoing = findViewById(R.id.dd_constraint_ongoing);

        np_hour = findViewById(R.id.dd_np_hour);
        np_min = findViewById(R.id.dd_np_min);
        np_sec = findViewById(R.id.dd_np_sec);

        np_hour.setMaxValue(24);
        np_hour.setMinValue(0);
        np_min.setMaxValue(59);
        np_min.setMinValue(0);
        np_sec.setMaxValue(59);
        np_sec.setMinValue(0);

        tv_display = findViewById(R.id.dd_tv_display);

        bt_start = findViewById(R.id.dd_bt_start);
        bt_pause = findViewById(R.id.dd_bt_pause);
        bt_continue = findViewById(R.id.dd_bt_continue);
        bt_cancel = findViewById(R.id.dd_bt_cancel);
    }

    void setView() {
        if (Collection.isServiceRunning(this, TimerService.class)) {
            constraint_start.setVisibility(View.GONE);
            constraint_ongoing.setVisibility(View.VISIBLE);
            bt_pause.setVisibility(View.VISIBLE);
            bt_continue.setVisibility(View.GONE);

            switchPower = true;
            new TimerThread().start();
        } else {
            if (Collection.getSharedTimerIsPause(this)) {
                constraint_start.setVisibility(View.GONE);
                constraint_ongoing.setVisibility(View.VISIBLE);
                bt_pause.setVisibility(View.GONE);
                bt_continue.setVisibility(View.VISIBLE);
                tv_display.setText(Collection.formattedSecondsStr(Collection.getSharedTimer(this)));
            } else {
                /* exist only '시작' button, when this activity start first*/
                constraint_start.setVisibility(View.VISIBLE);
                constraint_ongoing.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case RINGTONE_REQUEST_CODE:
                Uri ringtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                Collection.putSharedRingtone(this, ringtoneUri);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timer, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_timer:
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "타이머 소리 설정");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Collection.getSharedRingtone(this));
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
                startActivityForResult(intent, RINGTONE_REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dd_bt_start:
                if (0 == np_hour.getValue() * 3600 + np_min.getValue() * 60 + np_sec.getValue()) {
                    Toast.makeText(getApplicationContext(), "시간을 설정해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                timerStart();
                break;
            case R.id.dd_bt_pause:
                timerPause();
                break;
            case R.id.dd_bt_continue:
                timerContinue();
                break;
            case R.id.dd_bt_cancel:
                timerCancel();
                break;
        }
    }

    void timerStart() {
        Collection.putSharedTimerIsPause(this, false);
        constraint_start.setVisibility(View.GONE);
        constraint_ongoing.setVisibility(View.VISIBLE);
        bt_pause.setVisibility(View.VISIBLE);
        bt_continue.setVisibility(View.GONE);

        settingSeconds = np_hour.getValue() * 3600 + np_min.getValue() * 60 + np_sec.getValue();
        TimerService.remainsSeconds = settingSeconds;

        switchPower = true;
        new TimerThread().start();
        startService(new Intent(this, TimerService.class));
    }

    void timerPause() {
        Collection.putSharedTimerIsPause(this, true);
        bt_pause.setVisibility(View.GONE);
        bt_continue.setVisibility(View.VISIBLE);

        switchPower = false;
        stopService(new Intent(this, TimerService.class));
    }

    void timerContinue() {
        Collection.putSharedTimerIsPause(this, false);
        bt_pause.setVisibility(View.VISIBLE);
        bt_continue.setVisibility(View.GONE);

        settingSeconds = Collection.getSharedTimer(this);
        TimerService.remainsSeconds = settingSeconds;

        switchPower = true;
        new TimerThread().start();
        startService(new Intent(this, TimerService.class));
    }

    void timerCancel() {
        Collection.putSharedTimerIsPause(this, false);
        constraint_start.setVisibility(View.VISIBLE);
        constraint_ongoing.setVisibility(View.GONE);

        switchPower = false;
        stopService(new Intent(this, TimerService.class));

        tv_display.setText(R.string.timer_initialization);
    }

    /* every 500 millis, load timerSeconds from TimerService */
    private class TimerThread extends Thread {

        TimerHandler handler = new TimerHandler();

        @Override
        public void run() {
            while (switchPower) {
                Message msg = handler.obtainMessage();
                msg.what = SEND_THREAD_MESSAGE;
                msg.arg1 = TimerService.remainsSeconds;
                Log.i("[SON]", "remainsSeconds : " + TimerService.remainsSeconds + " from Timer.class");
                handler.sendMessage(msg);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private class TimerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEND_THREAD_MESSAGE:
                    tv_display.setText(Collection.formattedSecondsStr(msg.arg1));
                    Collection.putSharedTimer(getApplicationContext(), msg.arg1);
                    if (msg.arg1 == 0 && isTimerActivityVisible) {
                        timerCancel();
                        startActivity(new Intent(Dd_Timer.this, Dd_TimerAlarm.class));
                    }
                    break;
            }
        }
    }

}