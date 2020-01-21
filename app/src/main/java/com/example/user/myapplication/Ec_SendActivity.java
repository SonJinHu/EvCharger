package com.example.user.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.Objects;

public class Ec_SendActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dg_activity_send);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setTitle("의견 보내기");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dg_bt_mail:
                Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
                mailIntent.setType("text/plain");
                mailIntent.setData(Uri.parse("mailto:sonjh08@naver.com"));
                mailIntent.putExtra(Intent.EXTRA_SUBJECT, "전기charger? : 불편하신 점이나 개선할 사항을 보내주세요.");
                startActivity(mailIntent);
                break;
            case R.id.dg_bt_call:
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:010-2277-2661"));
                startActivity(phoneIntent);
                break;
        }
    }
}
