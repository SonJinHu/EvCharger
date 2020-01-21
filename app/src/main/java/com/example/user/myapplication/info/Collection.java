package com.example.user.myapplication.info;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.app.ActivityCompat;

import com.example.user.myapplication.item.ItemMember;
import com.example.user.myapplication.item.ItemNotice;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;

public class Collection {

    public static final Uri managerImageUri = Uri.parse("android.resource://com.example.user.myapplication/drawable/square");
    public static final Uri generalImageUri = Uri.parse("android.resource://com.example.user.myapplication/drawable/member");
    public static final int managerGrade = 1;
    public static final int generalGrade = 5;
    public static final String managerId = "son";
    public static final String managerPw = "son";

    public static final String urlAPI = "http://open.ev.or.kr:8080/openapi/services/rest/EvChargerService?serviceKey=";
    public static final String serviceKey = "3s0hPg6CF10KNEt0Ya4FNkSqH9ulFqK3VHqXw%2F9hLjBXBz4Ws4p%2BcW9%2FyjppAgVgS0hHFDq2EZVwbZEGoDF%2FZA%3D%3D";

    public static String currentID;
    public static int currentGRADE;
    public static Uri currentUri;

    public static void addMember(Activity activity, Uri imageUri, int grade, String id, String pw) {
        ArrayList<ItemMember> list = getSharedMember(activity);
        ItemMember member = new ItemMember(imageUri, grade, id, pw);
        list.add(member);
        putSharedMember(activity, list);
    }

    public static void addNotice(Activity activity, String subject, String text, String date) {
        ArrayList<ItemNotice> list = getSharedNotice(activity);
        if (list == null) {
            list = new ArrayList<>();
        }
        ItemNotice item = new ItemNotice(subject, text, date);
        list.add(0, item);
        putSharedNotice(activity, list);
    }

    public static String formattedSecondsStr(int seconds) {
        int hour = seconds / 3600;
        int minute = (seconds % 3600) / 60;
        int second = seconds % 60;
        return String.format(Locale.KOREA, "%02d:%02d:%02d", hour, minute, second);
    }

    public static boolean isFilled(TextInputEditText edit, TextInputLayout layout, String message) {
        String value = edit.getText().toString().trim();
        if (value.isEmpty()) {
            layout.setError(message);
            return false;
        }
        return true;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (cm != null) {
            netInfo = cm.getActiveNetworkInfo();
        }
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

    public static boolean isLocationPermission(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        return false;
    }

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void putSharedMember(Activity activity, ArrayList<ItemMember> list) {
        SharedPreferences pref = activity.getSharedPreferences("MEMBER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("member", new Gson().toJson(list));
        editor.apply();
    }

    public static ArrayList<ItemMember> getSharedMember(Activity activity) {
        SharedPreferences pref = activity.getSharedPreferences("MEMBER", Context.MODE_PRIVATE);
        String json = pref.getString("member", "");
        Type type = new TypeToken<ArrayList<ItemMember>>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }

    public static void putSharedNotice(Activity activity, ArrayList<ItemNotice> list) {
        SharedPreferences pref = activity.getSharedPreferences("NOTICE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("notice", new Gson().toJson(list));
        editor.apply();
    }

    public static ArrayList<ItemNotice> getSharedNotice(Activity activity) {
        SharedPreferences pref = activity.getSharedPreferences("NOTICE", Context.MODE_PRIVATE);
        String json = pref.getString("notice", "");
        Type type = new TypeToken<ArrayList<ItemNotice>>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }

    public static void putSharedTimer(Context context, int seconds) {
        SharedPreferences pref = context.getSharedPreferences("Timer", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("timer", seconds);
        editor.apply();
    }

    public static int getSharedTimer(Context context) {
        SharedPreferences pref = context.getSharedPreferences("Timer", Context.MODE_PRIVATE);
        if (pref == null) {
            return 0;
        }
        return pref.getInt("timer", 0);
    }

    public static void putSharedTimerIsPause(Context context, boolean isPause) {
        SharedPreferences pref = context.getSharedPreferences("Timer", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("pause", isPause);
        editor.apply();
    }

    public static boolean getSharedTimerIsPause(Context context) {
        SharedPreferences pref = context.getSharedPreferences("Timer", Context.MODE_PRIVATE);
        return pref != null && pref.getBoolean("pause", false);
    }

    public static void putSharedRingtone(Context context, Uri ringtone) {
        SharedPreferences pref = context.getSharedPreferences("Ringtone", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("ringtone", ringtone.toString());
        editor.apply();
    }

    public static Uri getSharedRingtone(Context context) {
        Uri defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM);
        SharedPreferences pref = context.getSharedPreferences("Ringtone", Context.MODE_PRIVATE);
        if (pref == null) {
            return defaultRingtoneUri;
        }
        return Uri.parse(pref.getString("ringtone", defaultRingtoneUri.toString()));
    }

}