package com.example.user.myapplication;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.user.myapplication.info.Collection;
import com.example.user.myapplication.item.ItemNotice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Eab_NoticeModify extends AppCompatActivity {

    int position;

    EditText edit1;
    EditText edit2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deab_activity_change);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("공지사항 글 수정");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

        position = getIntent().getIntExtra("포지션", 0);

        edit1 = findViewById(R.id.deab_edit_subject);
        edit2 = findViewById(R.id.deab_edit_text);

        ArrayList<ItemNotice> list = Collection.getSharedNotice(this);
        edit1.setText(list.get(position).getSubject());
        edit2.setText(list.get(position).getText());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_apply, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_apply:
                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat sdf = new SimpleDateFormat("yy.MM.dd", Locale.KOREA);

                String value1 = edit1.getText().toString();
                String value2 = edit2.getText().toString();
                String value3 = sdf.format(date);

                ItemNotice noticeItem = new ItemNotice(value1, value2, value3);
                ArrayList<ItemNotice> list = Collection.getSharedNotice(this);
                list.set(position, noticeItem);
                Collection.putSharedNotice(this, list);

                setResult(RESULT_OK);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}