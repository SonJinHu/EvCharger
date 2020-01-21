package com.example.user.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.user.myapplication.adapter.AdapterMember;
import com.example.user.myapplication.info.Collection;
import com.example.user.myapplication.item.ItemMember;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Eb_MemberActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    private final int MODIFY_MEMBER = 0;

    AdapterMember adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.df_activity_member);
        initToolbar();

        ArrayList<ItemMember> list = Collection.getSharedMember(this);
        adapter = new AdapterMember(list);
        listView = findViewById(R.id.df_listview);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MODIFY_MEMBER:
                if (resultCode == RESULT_OK) {
                    adapter.updateList(Collection.getSharedMember(Eb_MemberActivity.this));
                }
                break;
        }
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
                        modifyMember(position);
                        break;
                    case 1:
                        deleteMember(position);
                        break;
                }
            }
        });
        builder.show();
        return true;
    }

    void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("회원관리");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
    }

    void modifyMember(int position) {
        Intent intent = new Intent(getApplicationContext(), EbA_MemberModify.class);
        intent.putExtra("포지션", position);
        startActivityForResult(intent, MODIFY_MEMBER);
    }

    void deleteMember(int position) {
        ArrayList<ItemMember> list = Collection.getSharedMember(this);
        list.remove(position);
        Collection.putSharedMember(this, list);
        adapter.updateList(Collection.getSharedMember(Eb_MemberActivity.this));
    }
}
