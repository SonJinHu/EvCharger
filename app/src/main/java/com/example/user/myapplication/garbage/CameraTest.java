package com.example.user.myapplication.garbage;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.user.myapplication.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CameraTest extends AppCompatActivity {

    private final int CAMERA_REQUEST_CODE = 0;
    private final int ALBUM_REQUEST_CODE = 1;
    private final int CROP_REQUEST_CODE = 2;
    private final int PERMISSION_REQUEST_CODE = 10;

    ImageView iv;

    Uri imageUri;
    File imageFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_activity_join);

        iv = findViewById(R.id.c_iv_profile);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogImage();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                cropImage(imageUri, imageUri);
                break;
            case ALBUM_REQUEST_CODE:
                /* use Uri.fromFile() at any cost */
                cropImage(data.getData(), Uri.fromFile(imageFile));
                break;
            case CROP_REQUEST_CODE:
                /* use Uri.fromFile() at any cost */
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile));
                sendBroadcast(intent);

                Log.i("[SON]imageUri", imageUri + "");
                Log.i("[SON]Uri.fromFile()", Uri.fromFile(imageFile) + "");

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(imageFile));
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
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults[0] == granted && grantResults[1] == granted) {
                    invokeCamera();
                } else {
                    Toast.makeText(getApplicationContext(), "Cannot use the camera without permission", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    void dialogImage() {
        List<String> ListItems = new ArrayList<>();
        ListItems.add("사진 촬영");
        ListItems.add("앨범에서 사진 선택");
        CharSequence[] items = ListItems.toArray(new String[ListItems.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("프로필");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        /* invokeCamera() after check permissions  */
                        checkPermissionsForCamera();
                        break;
                    case 1:
                        invokeAlbum();
                        break;
                }
            }
        });
        builder.show();
    }

    void checkPermissionsForCamera() {
        String[] permissionRequest = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        int selfPermissionCamera = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        int selfPermissionWrite = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int granted = PackageManager.PERMISSION_GRANTED;

        if (selfPermissionCamera == granted && selfPermissionWrite == granted) {
            invokeCamera();
        } else {
            ActivityCompat.requestPermissions(this, permissionRequest, PERMISSION_REQUEST_CODE);
        }
    }

    void invokeCamera() {
        imageUri = createImageUri();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    void invokeAlbum() {
        imageUri = createImageUri();
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, ALBUM_REQUEST_CODE);
    }

    Uri createImageUri() {
        /* authority must be the same path as authorities in AndroidManifest.xml */
        String authority = getApplicationContext().getPackageName() + ".provider";
        imageFile = createImageFile();

        return FileProvider.getUriForFile(this, authority, imageFile);
    }

    File createImageFile() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA);
        String timeStamp = sdf.format(new Date());

        File imageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Charger/");
        if (!imageDir.exists()) {
            if (imageDir.mkdirs())
                Log.i("[SON]", "create '/Charger' folder in /Pictures");
        }
        return new File(imageDir, timeStamp + ".jpg");
    }

    void cropImage(Uri inUri, Uri outUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(inUri, "image/*");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("output", outUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, CROP_REQUEST_CODE);
        // intent.putExtra("outputX", 256);
        // intent.putExtra("outputY", 256);
        // intent.putExtra("crop", true);
        // intent.putExtra("scale", true);
        // intent.putExtra("return-data", true);
    }

//    void deleteImageFile(Uri imageUri) {
//        File imageFile = new File(imageUri.getPath());
//        if (imageFile.exists()) {
//            if (imageFile.delete())
//                Log.i("SON", "delete image file");
//        }
//    }

}
