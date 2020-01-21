package com.example.user.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Da_MapFragment extends Fragment implements OnMapReadyCallback {

    private final int LOCATION_REQUEST_CODE = 0;

    GoogleMap map;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.da_fragment_maps, container, false);
        MapFragment mf = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mf.getMapAsync(this);
        
        askPermission();
        return view;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int granted = PackageManager.PERMISSION_GRANTED;
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults[0] == granted) {
                    map.setMyLocationEnabled(true);
                }
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        int checkPermission = ActivityCompat.checkSelfPermission(getActivity(), permission);
        int granted = PackageManager.PERMISSION_GRANTED;
        if (checkPermission == granted) {
            map.setMyLocationEnabled(true);
        }

        LatLng seoul = new LatLng(37.2965160, 127.0318570);
        googleMap.addMarker(new MarkerOptions().position(seoul).title("Marker in Seoul"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
    }

    void askPermission() {
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        int checkPermission = ActivityCompat.checkSelfPermission(getActivity(), permission);
        int granted = PackageManager.PERMISSION_GRANTED;
        if (checkPermission != granted) {
            buildDialog().show();
        }
    }

    AlertDialog.Builder buildDialog() {
        return new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setMessage("전기charger?를 사용하는 동안 해당 앱이 사용자의 위치에 접근하도록 허용하겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION};
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(permission, LOCATION_REQUEST_CODE);
                        }
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
    }

}