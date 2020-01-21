package com.example.user.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.myapplication.info.Collection;
import com.example.user.myapplication.item.ItemAPI;
import com.example.user.myapplication.item.ItemCluster;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.ClusterManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class D_MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private final int LOCATION_PERMISSION_CODE = 0;
    private final int REQUEST_CHECK_SETTINGS_GPS = 1;

    boolean exit = false;
    GoogleMap map;
    GoogleApiClient googleApiClient;
    Location mLocation;
    boolean switchToolbarLocationButton = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initDrawer();
        initHeader();
        initMenu();
    }

    void initDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    void initHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);

        ImageView iv = header.findViewById(R.id.nav_iv_img);
        TextView tvId = header.findViewById(R.id.nav_tv_id);
        TextView tvGrade = header.findViewById(R.id.nav_tv_grade);
        try {
            Uri uri = Collection.currentUri;
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            String grade = Integer.toString(Collection.currentGRADE);
            switch (grade){
                case "1":
                    grade = "관리자";
                    break;
                case "5":
                    grade = "일반회원";
                    break;
            }

            iv.setImageBitmap(bitmap);
            tvId.setText(Collection.currentID);
            tvGrade.setText(grade);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void initMenu() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        /* 회원등급이 1이 아니면 회원관리 메뉴 GONE */
        if (Collection.currentGRADE == 1) {
            navigationView.inflateMenu(R.menu.menu_drawer_admin);
        } else {
            navigationView.inflateMenu(R.menu.menu_drawer);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /* always 'map' menu clicked.
        start this activity first and come back this activity from other activity. */
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);

        if (Collection.isOnline(this)) {
            ConnectAPI async = new ConnectAPI();
            async.execute();
        } else {
            initSnackBar();
        }
    }

    void initSnackBar() {
        Snackbar snack = Snackbar.make(findViewById(R.id.map), "인터넷에 연결해주세요.", Snackbar.LENGTH_INDEFINITE);
        snack.setAction("새로고침", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResume();
            }
        });
        snack.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.stopAutoManage(this);
            googleApiClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    map.setMyLocationEnabled(true);
                    switchToolbarLocationButton = false;
                    supportInvalidateOptionsMenu();
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

        switch (requestCode) {
            case LOCATION_PERMISSION_CODE:
                getMyLocation();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (switchToolbarLocationButton) {
            getMenuInflater().inflate(R.menu.menu_map, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_location:
                setUPGClient();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* NavigationView.OnNavigationItemSelectedListener */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_map:
                break;
            case R.id.nav_timer:
                startActivity(new Intent(this, Dd_Timer.class));
                break;
            case R.id.nav_notice:
                startActivity(new Intent(this, Ea_NoticeActivity.class));
                break;
            case R.id.nav_member:
                startActivity(new Intent(this, Eb_MemberActivity.class));
                break;
            case R.id.nav_send:
                startActivity(new Intent(this, Ec_SendActivity.class));
                break;
            case R.id.nav_logout:
                startActivity(new Intent(this, B_LoginActivity.class));
                finish();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /* OnMapReadyCallback */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        //LatLng home = new LatLng(37.2965160, 127.0318570);
        LatLng palace = new LatLng(37.579848, 126.977028);
        LatLng bound1 = new LatLng(33.19027, 126.1236); /* 왼쪽 하단 spot */
        LatLng bound2 = new LatLng(38.62253, 131.8728); /* 오른쪽 상단 spot (울릉도, 독도 포함되지 않음 : 129.5858) */
        LatLngBounds KOREA = new LatLngBounds(bound1, bound2);
        googleMap.setLatLngBoundsForCameraTarget(KOREA);
        googleMap.setMinZoomPreference(6);

        googleMap.getUiSettings().setRotateGesturesEnabled(false);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            if (isTurnedOnGPS()) {
                map.setMyLocationEnabled(true);
                switchToolbarLocationButton = false;
                supportInvalidateOptionsMenu();
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(palace));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
            } else {
                map.setMyLocationEnabled(false);
                switchToolbarLocationButton = true;
                supportInvalidateOptionsMenu();
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(palace));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
            }
        } else {
            map.setMyLocationEnabled(false);
            switchToolbarLocationButton = true;
            supportInvalidateOptionsMenu();
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(palace));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
        }
    }

    /* GoogleApiClient.ConnectionCallbacks */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        String[] locationPermissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this, locationPermissions, LOCATION_PERMISSION_CODE);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /* GoogleApiClient.OnConnectionFailedListener */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /* LocationListener */
    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
    }

    boolean isTurnedOnGPS() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert manager != null;
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    synchronized void setUPGClient() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.stopAutoManage(this);
            googleApiClient.disconnect();
        }

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    void getMyLocation() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(1);
            locationRequest.setFastestInterval(1);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);

            PendingResult result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback() {
                @Override
                public void onResult(@NonNull Result result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                status.startResolutionForResult(D_MainActivity.this, REQUEST_CHECK_SETTINGS_GPS);
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                }
            });
        }
    }

    @SuppressLint("StaticFieldLeak")
    class ConnectAPI extends AsyncTask<Void, Void, Void> {

        ProgressBar pb = findViewById(R.id.d_progressBar);
        ArrayList<ItemAPI> items = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(Collection.urlAPI + Collection.serviceKey);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/json");

                BufferedReader rd;
                if (200 <= conn.getResponseCode() && conn.getResponseCode() <= 300) {
                    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }

                rd.close();
                conn.disconnect();
                items = parse(sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            initCluster(items);
            pb.setVisibility(View.GONE);
        }
    }

    ArrayList<ItemAPI> parse(String result) {

        ArrayList<ItemAPI> items = new ArrayList<>();

        try {
            InputStream is = new ByteArrayInputStream(result.getBytes());
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document doc = documentBuilder.parse(is);
            Element element = doc.getDocumentElement();

            NodeList items1 = element.getElementsByTagName("statId");
            NodeList items2 = element.getElementsByTagName("statNm");
            NodeList items3 = element.getElementsByTagName("chgerId");
            NodeList items4 = element.getElementsByTagName("chgerType");
            NodeList items5 = element.getElementsByTagName("addrDoro");
            NodeList items6 = element.getElementsByTagName("lat");
            NodeList items7 = element.getElementsByTagName("lng");
            NodeList items8 = element.getElementsByTagName("useTime");
            NodeList items9 = element.getElementsByTagName("stat");

            for (int i = 0; i < items1.getLength(); i++) {
                String item1 = items1.item(i).getFirstChild().getNodeValue();
                String item2 = items2.item(i).getFirstChild().getNodeValue();
                String item3 = items3.item(i).getFirstChild().getNodeValue();
                String item4 = items4.item(i).getFirstChild().getNodeValue();
                String item5 = items5.item(i).getFirstChild().getNodeValue();
                String item6 = items6.item(i).getFirstChild().getNodeValue();
                String item7 = items7.item(i).getFirstChild().getNodeValue();
                String item8 = items8.item(i).getFirstChild().getNodeValue();
                String item9 = items9.item(i).getFirstChild().getNodeValue();

                ItemAPI item = new ItemAPI();
                item.setTag(i);
                item.setStatId(item1);
                item.setStatNm(item2);
                item.setChgerId(item3);
                item.setChgerType(parsingChgerType(item4));
                item.setAddrDoro(item5);
                item.setLat(item6);
                item.setLng(item7);
                item.setUseTime(item8);
                item.setStat(parsingStat(item9));
                items.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    String parsingChgerType(String itemChgerType) {
        switch (itemChgerType) {
            case "01":
                return "DC차데모";
            case "03":
                return "DC차데모 + AC3상";
            case "06":
                return "DC차데모 + AC3상 + DC콤보";
            default:
                return "정보없음";
        }
    }

    String parsingStat(String itemStat) {
        switch (itemStat) {
            case "1":
                return "통신이상";
            case "2":
                return "충전가능";
            case "3":
                return "충전중";
            case "4":
                return "운영중지";
            case "5":
                return "점검중";
            default:
                return "정보없음";
        }
    }

    void initCluster(final ArrayList<ItemAPI> items) {
        ClusterManager<ItemCluster> clusterManager = new ClusterManager<>(D_MainActivity.this, map);
        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<ItemCluster>() {
            @Override
            public boolean onClusterItemClick(final ItemCluster myItem) {
                map.animateCamera(CameraUpdateFactory.newLatLng(myItem.getPosition()), 300, new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        initBottomSheet(items, myItem);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                return true;
            }
        });

        for (int i = 0; i < items.size(); i++) {
            double lat = Double.parseDouble(items.get(i).getLat());
            double lng = Double.parseDouble(items.get(i).getLng());
            int tag = items.get(i).getTag();
            clusterManager.addItem(new ItemCluster(lat, lng, tag));
        }

        map.setOnCameraIdleListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
        map.clear();
        clusterManager.cluster();
    }

    void initBottomSheet(ArrayList<ItemAPI> items, ItemCluster myItem) {
        View view = findViewById(R.id.bottom_linear);
        view.setVisibility(View.VISIBLE);

        TextView tv2 = findViewById(R.id.bottom_tv_statNm);
        TextView tv4 = findViewById(R.id.bottom_tv_chgerType);
        TextView tv5 = findViewById(R.id.bottom_tv_addrDoro);
        TextView tv6 = findViewById(R.id.bottom_tv_useTime);
        TextView tv7 = findViewById(R.id.bottom_tv_stat);

        tv2.setText(items.get(myItem.getTag()).getStatNm());
        tv4.setText(items.get(myItem.getTag()).getChgerType());
        tv5.setText(items.get(myItem.getTag()).getAddrDoro());
        tv6.setText(items.get(myItem.getTag()).getUseTime());
        tv7.setText(items.get(myItem.getTag()).getStat());
    }

}