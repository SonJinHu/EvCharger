package com.example.user.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import android.view.WindowManager;

import com.example.user.myapplication.info.Collection;
import com.example.user.myapplication.item.ItemMember;

import java.util.ArrayList;

public class A_SplashActivity extends AppCompatActivity {

    int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_splash);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        /* 관리자 아이디 추가 */
        if (Collection.getSharedMember(this) == null) {
            ArrayList<ItemMember> list = new ArrayList<>();
            list.add(new ItemMember(Collection.managerImageUri, Collection.managerGrade, Collection.managerId, Collection.managerPw));
            Collection.putSharedMember(this, list);
        }

        /* SPLASH_DISPLAY_LENGTH 동안 Splash 액티비티를 실행시키고 종료한다.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), B_LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

}