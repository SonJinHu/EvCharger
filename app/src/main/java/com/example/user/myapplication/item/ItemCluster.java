package com.example.user.myapplication.item;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ItemCluster implements ClusterItem {
    private LatLng position;
    private int tag;

    public ItemCluster(double lat, double lng, int tag) {
        position = new LatLng(lat, lng);
        this.tag = tag;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public int getTag() {
        return tag;
    }
}
