package com.example.user.myapplication.info;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.util.Log;

import com.example.user.myapplication.item.ItemMember;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Image {

    public static boolean isRequestPermissions(Activity activity, String[] permissions) {
        ArrayList<String> list = new ArrayList<>();
        for (String check : permissions) {
            int selfCheck = ContextCompat.checkSelfPermission(activity.getApplicationContext(), check);
            int granted = PackageManager.PERMISSION_GRANTED;
            if (selfCheck != granted) {
                list.add(check);
            }
        }
        return !list.isEmpty();
    }

    public static File createImageFile() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA);
        String timeStamp = sdf.format(new Date());

        File imageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        + "/Charger/Profile");
        if (!imageDir.exists()) {
            if (imageDir.mkdirs()) {
                Log.i("[SON]", "create '/Charger/Profile' in /Pictures");
            }
        }
        return new File(imageDir, timeStamp + ".jpg");
    }

    public static Uri uriFromFileProvider(Activity activity, File file) {
        /* authority must be the same path as authorities in AndroidManifest.xml */
        String authority = activity.getApplicationContext().getPackageName() + ".provider";
        return FileProvider.getUriForFile(activity, authority, file);
    }

    public static void invokeCamera(Activity activity, File file, int code) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFromFileProvider(activity, file));
        intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        activity.startActivityForResult(intent, code);
    }

    public static void invokeAlbum(Activity activity, int code) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        activity.startActivityForResult(intent, code);
    }

    public static void cropImage(Activity activity, Uri inputUri, Uri outputUri, int code) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(inputUri, "image/*");
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("output", outputUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        activity.startActivityForResult(intent, code);
    }

    public static void cleanUpImage(Activity activity) {
        File imageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        + "/Charger/Profile");
        File[] files = imageDir.listFiles();
        if (files == null) {
            return;
        }

        int i = 0;
        while (i < files.length) {

            ArrayList<ItemMember> list = Collection.getSharedMember(activity);
            Uri savedFileUri = Uri.fromFile(files[i]);
            boolean delete = true;

            for (int j = 0; j < list.size(); j++) {
                Uri savedListUri = list.get(j).getImageUri();
                if (savedFileUri.equals(savedListUri)) {
                    delete = false;
                }
            }

            if (delete) {
                File file = new File(savedFileUri.getPath());
                Log.i("[SON]", "File that need to be deleted : " + savedFileUri);

                if (file.delete()) {
                    Log.i("[SON]", "delete 성공");
                } else {
                    Log.i("[SON]", "delete 실패");
                }

                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, savedFileUri);
                activity.sendBroadcast(intent);
            }

            i++;
        }
    }

}