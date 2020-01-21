package com.example.user.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.myapplication.info.Collection;
import com.example.user.myapplication.info.Image;
import com.example.user.myapplication.item.ItemMember;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class C_JoinActivity extends AppCompatActivity implements View.OnClickListener {

    private final int CAMERA_REQUEST_CODE = 0;
    private final int ALBUM_REQUEST_CODE = 1;
    private final int CROP_REQUEST_CODE = 2;
    private final int CAMERA_PERMISSION_CODE = 3;
    private final int ALBUM_PERMISSION_CODE = 4;

    ImageView iv;
    TextInputLayout layoutId, layoutPw1, layoutPw2;
    TextInputEditText editId, editPw1, editPw2;

    /* 사용자 지정 이미지 존재하면, add '기본이미지로 변경' in dialog */
    boolean isSetImage = false;
    File imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_activity_join);
        initView();

        editPw2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    register();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Image.cleanUpImage(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;

        Uri uri24 = Image.uriFromFileProvider(this, imageFile);
        Uri uri = Uri.fromFile(imageFile);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                Image.cropImage(this, uri24, uri, CROP_REQUEST_CODE);
                break;
            case ALBUM_REQUEST_CODE:
                /* use Uri.fromFile() at any cost */
                Image.cropImage(this, data.getData(), uri, CROP_REQUEST_CODE);
                break;
            case CROP_REQUEST_CODE:
                /* use Uri.fromFile() at any cost */
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
                sendBroadcast(intent);

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    iv.setImageBitmap(bitmap);
                    isSetImage = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int granted = PackageManager.PERMISSION_GRANTED;
        if (grantResults[0] != granted) {
            Toast.makeText(getApplicationContext(), "This feature is not available without permission", Toast.LENGTH_LONG).show();
            return;
        }

        imageFile = Image.createImageFile();
        switch (requestCode) {
            case CAMERA_PERMISSION_CODE:
                if (grantResults[1] == granted) {
                    Image.invokeCamera(this, imageFile, CAMERA_REQUEST_CODE);
                } else {
                    Toast.makeText(getApplicationContext(), "This feature is not available without permission", Toast.LENGTH_LONG).show();
                }
                break;
            case ALBUM_PERMISSION_CODE:
                Image.invokeAlbum(this, ALBUM_REQUEST_CODE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.c_iv_profile:
                selectDialog();
                break;
            case R.id.c_bt_register:
                register();
                break;
            case R.id.c_tv_goLogin:
                finish();
                break;
        }
    }

    void initView() {
        iv = findViewById(R.id.c_iv_profile);
        layoutId = findViewById(R.id.c_textInputLayout_name);
        layoutPw1 = findViewById(R.id.c_textInputLayout_password1);
        layoutPw2 = findViewById(R.id.c_textInputLayout_password2);
        editId = findViewById(R.id.c_textInputEditText_name);
        editPw1 = findViewById(R.id.c_textInputEditText_password1);
        editPw2 = findViewById(R.id.c_textInputEditText_password2);
    }

    void selectDialog() {
        List<String> ListItems = new ArrayList<>();
        ListItems.add(getString(R.string.dialog_menu1));
        ListItems.add(getString(R.string.dialog_menu2));
        if (isSetImage) {
            ListItems.add(getString(R.string.dialog_menu3));
        }
        CharSequence[] items = ListItems.toArray(new String[ListItems.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("프로필");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] cameraPermissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                String[] albumPermissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                switch (which) {
                    case 0:
                        ActivityCompat.requestPermissions(C_JoinActivity.this, cameraPermissions, CAMERA_PERMISSION_CODE);
                        break;
                    case 1:
                        ActivityCompat.requestPermissions(C_JoinActivity.this, albumPermissions, ALBUM_PERMISSION_CODE);
                        break;
                    case 2:
                        try {
                            Uri uri = Collection.generalImageUri;
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            iv.setImageBitmap(bitmap);

                            imageFile = null;
                            isSetImage = false;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
        builder.show();
    }

    void register() {
        layoutId.setErrorEnabled(false);
        layoutPw1.setErrorEnabled(false);
        layoutPw2.setErrorEnabled(false);

        if (!Collection.isFilled(editId, layoutId, "아이디를 입력해주세요.")) {
            layoutId.requestFocus();
            return;
        }
        if (isDuplicated(editId, layoutId)) {
            layoutId.requestFocus();
            return;
        }
        if (!Collection.isFilled(editPw1, layoutPw1, "비밀번호를 입력해주세요.")) {
            layoutPw1.requestFocus();
            return;
        }
        if (!Collection.isFilled(editPw2, layoutPw2, "비밀번호를 확인해주세요.")) {
            layoutPw2.requestFocus();
            return;
        }
        if (!isPaired(editPw1, editPw2, layoutPw2)) {
            layoutPw2.requestFocus();
            return;
        }

        Uri uri;
        if (imageFile == null) {
            uri = Collection.generalImageUri;
        } else {
            uri = Uri.fromFile(imageFile);
        }
        int grade = Collection.generalGrade;
        String id = editId.getText().toString();
        String pw = editPw2.getText().toString();

        Log.i("회원가입 정보", uri.toString() + ", " + id + ", " + pw);
        Collection.addMember(this, uri, grade, id, pw);

        Toast.makeText(getApplicationContext(), "화원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();

        setResult(RESULT_OK);
        finish();
    }

    /* ID 중복 확인 */
    boolean isDuplicated(TextInputEditText editID, TextInputLayout layout) {
        ArrayList<ItemMember> list = Collection.getSharedMember(this);
        String value = editID.getText().toString();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(value)) {
                layout.setError("중복된 아이디입니다.");
                return true;
            }
        }
        return false;
    }

    /* 비밀번호 확인 */
    boolean isPaired(TextInputEditText editPw1, TextInputEditText editPw2, TextInputLayout layout) {
        String value1 = editPw1.getText().toString().trim();
        String value2 = editPw2.getText().toString().trim();
        if (!value1.contentEquals(value2)) {
            layout.setError("비밀번호가 일치하지 않습니다.");
            return false;
        }
        return true;
    }

}