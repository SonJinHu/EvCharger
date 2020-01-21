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
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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
import java.util.Objects;

public class EbA_MemberModify extends AppCompatActivity {

    private final int CAMERA_REQUEST_CODE = 0;
    private final int ALBUM_REQUEST_CODE = 1;
    private final int CROP_REQUEST_CODE = 2;
    private final int CAMERA_PERMISSION_CODE = 3;
    private final int ALBUM_PERMISSION_CODE = 4;

    int position;
    ArrayList<ItemMember> presentArray;

    ImageView iv;
    EditText edit1, edit2, edit3;

    boolean isSetImage = false;
    File imageFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dfa_activity_modify);
        position = getIntent().getIntExtra("포지션", 0);
        presentArray = Collection.getSharedMember(this);

        initToolbar();
        initView();

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelect();
            }
        });

        edit3.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    apply();
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
                Image.cropImage(this, data.getData(), uri, CROP_REQUEST_CODE);
                break;
            case CROP_REQUEST_CODE:
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
                sendBroadcast(intent);

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    iv.setImageBitmap(bitmap);
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

        /* if 프로필 사진이 'drawable' 폴더에 저장된 기본이미지, 새로운 이미지파일 생성 */
        Uri presentUri = presentArray.get(position).getImageUri();
        if (isDefaultUri(presentUri))
            imageFile = Image.createImageFile();
        else
            imageFile = new File(presentUri.getPath());

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

    void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("회원정보 수정");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
    }

    void initView() {
        iv = findViewById(R.id.dfa_iv_profile);
        edit1 = findViewById(R.id.dfa_edit_grade);
        edit2 = findViewById(R.id.dfa_edit_id);
        edit3 = findViewById(R.id.dfa_edit_pw);

        Uri presentUri = presentArray.get(position).getImageUri();
        isSetImage = !isDefaultUri(presentUri);

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), presentUri);
            String grade = Integer.toString(presentArray.get(position).getGrade());

            iv.setImageBitmap(bitmap);
            edit1.setText(grade);
            edit2.setText(presentArray.get(position).getId());
            edit3.setText(presentArray.get(position).getPassword());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    boolean isDefaultUri(Uri uri) {
        Uri defaultUri1 = Collection.managerImageUri;
        Uri defaultUri2 = Collection.generalImageUri;
        return uri.equals(defaultUri1) | uri.equals(defaultUri2);
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
                apply();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void dialogSelect() {
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
                        ActivityCompat.requestPermissions(EbA_MemberModify.this, cameraPermissions, CAMERA_PERMISSION_CODE);
                        break;
                    case 1:
                        ActivityCompat.requestPermissions(EbA_MemberModify.this, albumPermissions, ALBUM_PERMISSION_CODE);
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


    void apply() {
        Uri uri;
        if (imageFile == null) {
            if (isSetImage) {
                uri = presentArray.get(position).getImageUri();
            } else {
                uri = Collection.generalImageUri;
            }
        } else {
            uri = Uri.fromFile(imageFile);
        }
        int grade = Integer.parseInt(edit1.getText().toString());
        String id = edit2.getText().toString();
        String pw = edit3.getText().toString();
        ItemMember member = new ItemMember(uri, grade, id, pw);

        presentArray.set(position, member);
        Collection.putSharedMember(this, presentArray);

        setResult(RESULT_OK);
        finish();
    }
}