package com.example.user.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import com.example.user.myapplication.adapter.AdapterNotice;
import com.example.user.myapplication.info.Collection;
import com.example.user.myapplication.item.ItemNotice;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ea_NoticeActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    private final int ADD_NOTICE = 0;
    private final int MODIFY_NOTICE = 1;

    AdapterNotice adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.de_activity_notice);
        initToolbar();

        /* 공지사항이 없을 경우 빈 값을 넣음 */
        if (Collection.getSharedNotice(this) == null) {
            ArrayList<ItemNotice> aa = new ArrayList<>();
            adapter = new AdapterNotice(aa);
        } else {
            adapter = new AdapterNotice(Collection.getSharedNotice(this));
        }
        ExpandableListView listView = findViewById(R.id.de_expandableListView);
        listView.setAdapter(adapter);

        /* 회원등급1 회원은 공지사항 수정, 삭제 가능 */
        if (Collection.currentGRADE == 1) {
            listView.setOnItemLongClickListener(this);
        }

        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });
    }

    void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("공지사항");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ADD_NOTICE:
                if (resultCode == RESULT_OK) {
                    adapter.updateList(Collection.getSharedNotice(Ea_NoticeActivity.this));
                }
                break;
            case MODIFY_NOTICE:
                if (resultCode == RESULT_OK) {
                    adapter.updateList(Collection.getSharedNotice(Ea_NoticeActivity.this));
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Collection.currentGRADE == 1) {
            getMenuInflater().inflate(R.menu.menu_add, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_add:
                add();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        List<String> ListItems = new ArrayList<>();
        ListItems.add("수정");
        ListItems.add("삭제");
        CharSequence[] items = ListItems.toArray(new String[ListItems.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        modify(position);
                        break;
                    case 1:
                        delete(position);
                        break;
                }
            }
        });
        builder.show();
        return true;
    }

    void add() {
        Intent intent = new Intent(getApplicationContext(), EaA_NoticeAdd.class);
        startActivityForResult(intent, ADD_NOTICE);
    }

    void modify(int position) {
        Intent intent = new Intent(getApplicationContext(), Eab_NoticeModify.class);
        intent.putExtra("포지션", position);
        startActivityForResult(intent, MODIFY_NOTICE);
    }

    void delete(int position) {
        ArrayList<ItemNotice> list = Collection.getSharedNotice(this);
        list.remove(position);
        Collection.putSharedNotice(this, list);
        adapter.updateList(Collection.getSharedNotice(Ea_NoticeActivity.this));
    }

}