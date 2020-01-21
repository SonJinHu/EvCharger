package com.example.user.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.myapplication.info.Collection;
import com.example.user.myapplication.item.ItemMember;

import java.util.ArrayList;

public class B_LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private final int REQUEST_LOGIN_INFO = 0;

    boolean exit = false;
    ArrayList<ItemMember> memberList;

    TextInputLayout layoutId;
    TextInputLayout layoutPw;
    TextInputEditText editId;
    TextInputEditText editPw;
    CheckBox cbHide;
    CheckBox cbAuto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b_activity_login);
        initView();

        editPw.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    login();
                    return true;
                }
                return false;
            }
        });
    }

    // 순서가 일정하지 않음. 확실한 것은 onResume() 보다 먼저 실행됨.
    // 아이디, 비밀번호 자동입력 immediately after 회원가입 in C_JoinActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOGIN_INFO) {
            if (resultCode == RESULT_OK) {
                memberList = Collection.getSharedMember(this);
                editId.setText(memberList.get(memberList.size()-1).getId());
                editPw.setText(memberList.get(memberList.size()-1).getPassword());
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish();
        } else {
            Toast.makeText(this, "'뒤로'버튼 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3000);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_cb_hide:
                if (cbHide.isChecked()) {
                    /* 비밀번호 숨기기 */
                    editPw.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    /* 비밀번호 보이기 */
                    editPw.setTransformationMethod(null);
                }
                break;
            case R.id.b_cb_auto:
                cbAuto.setChecked(false);
                Toast.makeText(getApplicationContext(), "준비 중입니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.b_bt_login:
                login();
                break;
            case R.id.b_tv_goJoin:
                Intent intent = new Intent(getApplicationContext(), C_JoinActivity.class);
                startActivityForResult(intent, REQUEST_LOGIN_INFO);
                break;
        }
    }

    void initView() {
        layoutId = findViewById(R.id.b_textInputLayout_id);
        layoutPw = findViewById(R.id.b_textInputLayout_pw);
        editId = findViewById(R.id.b_textInputEditText_id);
        editPw = findViewById(R.id.b_textInputEditText_pw);
        cbHide = findViewById(R.id.b_cb_hide);
        cbAuto = findViewById(R.id.b_cb_auto);
    }

    void login() {
        layoutId.setErrorEnabled(false);
        layoutPw.setErrorEnabled(false);

        memberList = Collection.getSharedMember(this);
        if (!Collection.isFilled(editId, layoutId, "아이디를 입력해주세요.")) {
            layoutId.requestFocus();
            return;
        }
        if (!isExisted(editId, layoutId)) {
            layoutId.requestFocus();
            return;
        }
        if (!Collection.isFilled(editPw, layoutPw, "비밀번호를 입력해주세요.")) {
            layoutPw.requestFocus();
            return;
        }
        if (!isMatched(editId, editPw, layoutPw)) {
            layoutPw.requestFocus();
            return;
        }

        /* save membership information that will log in */
        Collection.currentID = editId.getText().toString();
        Collection.currentGRADE = getGrade(editId.getText().toString());
        Collection.currentUri = getUri(editId.getText().toString());

        Intent intent = new Intent(getApplicationContext(), D_MainActivity.class);
        startActivity(intent);
        finish();
    }

    /* ID 존재 여부 확인 */
    boolean isExisted(TextInputEditText editId, TextInputLayout layout) {
        String value = editId.getText().toString();
        for (int i = 0; i < memberList.size(); i++) {
            if (memberList.get(i).getId().equals(value)) {
                return true;
            }
        }
        layout.setError("입력한 아이디가 존재하지 않습니다.");
        return false;
    }

    /* ID's 비밀번호 확인 */
    boolean isMatched(TextInputEditText editId, TextInputEditText editPw, TextInputLayout layout) {
        String value1 = editId.getText().toString();
        String value2 = editPw.getText().toString();
        int index = 0;
        for (int i = 0; i < memberList.size(); i++) {
            if (memberList.get(i).getId().equals(value1)) {
                index = i;
            }
        }
        if (memberList.get(index).getPassword().equals(value2)) {
            return true;
        }
        layout.setError("비밀번호를 잘못 입력하셨습니다.");
        return false;
    }

    int getGrade(String id) {
        int index = 0;
        for (int i = 0; i < memberList.size(); i++) {
            if (memberList.get(i).getId().equals(id)) {
                index = i;
            }
        }
        return memberList.get(index).getGrade();
    }

    Uri getUri(String id) {
        int index = 0;
        for (int i = 0; i < memberList.size(); i++) {
            if (memberList.get(i).getId().equals(id)) {
                index = i;
            }
        }
        return memberList.get(index).getImageUri();
    }

}