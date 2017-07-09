package com.aqsara.tambalban;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Base implements OnMapReadyCallback {

    private ListView mDrawerList;
    private final ThreadLocal<ArrayAdapter<String>> mAdapter = new ThreadLocal<>();

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;

    GoogleMap mGoogleMap;
    String base_api_url =
            StaticData.protocol + "://" + StaticData.host_api + StaticData.base_url_api;

    private LatLng position;
    private Location location;

    private LocationsManager locationsManager;
    public static int locationSelected;
    public static LatLng locationSelectedLatLng;

    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private static final int INITIAL_REQUEST=1337;

    @Override
    public void signedInUser() {
        super.signedInUser();

        TextView headerProfileName = (TextView) findViewById(R.id.header_profile_name);
        headerProfileName.setText(getUser().getDisplayName());
    }

    private void signOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage("Putuskan sambungan akun google Anda?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        signOut();
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("Batal", null);
        builder.show();
    }

    private class MarkerInfoWindow implements GoogleMap.InfoWindowAdapter{

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View v = getLayoutInflater().inflate(R.layout.info_window_layout, null);
            TBLocation location = locationsManager.getLocation(marker);
            locationSelected = location.getId();
            locationSelectedLatLng = location.getLatLng();

            TextView _title = (TextView)v.findViewById(R.id.title);
            TextView _openTime = (TextView)v.findViewById(R.id.open_time);
            TextView _closeTime = (TextView)v.findViewById(R.id.close_time);
            _title.setText(location.getTitle());
            if(location.getOpen_time() != null)
                _openTime.setText(getString(R.string.prefix_buka, location.getOpen_time()));
            if(location.getClose_time() != null)
                _closeTime.setText(getString(R.string.prefix_tutup, location.getClose_time()));
            return v;
        }
    }

    private boolean canAccessLocation() {
        return(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    private boolean hasPermission(String perm) {
        return(PackageManager.PERMISSION_GRANTED==ContextCompat.checkSelfPermission(this, perm));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d("ban", "requestCode: " + requestCode);
        Toast.makeText(this, "Location Request", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ban", "MainActivity...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);

        if (!canAccessLocation()) {
            ActivityCompat.requestPermissions(this, INITIAL_PERMS, INITIAL_REQUEST);
            System.exit(0);
        }

        mDrawerList = (ListView) findViewById(R.id.navList);
        addDrawerItems();

        LayoutInflater inflater = getLayoutInflater();
        View listHeaderView;
        listHeaderView = inflater.inflate(R.layout.header_list, null, false);
        mDrawerList.addHeaderView(listHeaderView);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        signOutDialog();
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this, InfoActivity.class));
                        break;
                }
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        setupDrawer();

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
        mGoogleMap = mapFragment.getMap();

        new RetrieveTask().execute();

        locationsManager = new LocationsManager(mGoogleMap);

        initLocation();
    }

    public void reportLocation(View v){
        if(locationSelected != 0){
            startActivity(new Intent(MainActivity.this, ReportLocation.class));
            finish();
        }else{
            Toast.makeText(this, "Lokasi pelaporan belum dipilih!", Toast.LENGTH_SHORT).show();
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void initLocation(){
        LocationManager locationManager =
                (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location == null){
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
    }

    @Override
    public void signOutSuccess() {
        startActivity(new Intent(this, GoogleLoginActivity.class));
        finish();
    }

    private class RetrieveTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String user_id = null;
            try {
                user_id = StaticData.getAccount().getId();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String strUrl = base_api_url + "get_locations/" + user_id;
            URL url;
            StringBuffer sb;
            sb = new StringBuffer();
            try {
                url = new URL(strUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream iStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                iStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            if(s == null || s.equals("")){
                _exit("Terjadi kesalahan pada Server, Coba beberapa saat lagi", MainActivity.this);
            }
            super.onPostExecute(s);
            new ParserTask().execute(s);
        }
    }

    private class ParserTask extends AsyncTask<String, Void, List<HashMap<String, String>>> {

        @Override
        protected List<HashMap<String, String>> doInBackground(String... params) {
            MarkerJSONParser markerParser = new MarkerJSONParser();
            JSONObject json = null;
            try {
                json = new JSONObject(params[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return markerParser.parse(json);
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            super.onPostExecute(hashMaps);
            for (int i = 0; i < hashMaps.size(); i++) {
                HashMap<String, String> marker = hashMaps.get(i);
                LatLng latLng = new LatLng(
                        Double.parseDouble(marker.get("latitude")),
                        Double.parseDouble(marker.get("longitude"))
                );
                boolean pending = Boolean.parseBoolean(marker.get("is_pending"));
                if(pending){
                    addMarker(latLng, true);
                }else{
                    addMarkerStatic(
                            latLng,
                            marker.get("name"),
                            marker.get("type"),
                            marker.get("open_time"),
                            marker.get("close_time"),
                            Integer.parseInt(marker.get("id"))
                    );
                }
            }
        }
    }

    public void findClosest(View view){
        locationsManager.closest();
    }

    private BitmapDescriptor setMarkerIcon(String type){
        int icon_res = R.drawable.type_unknown;
        type = type.toLowerCase();
        switch (type){
            case "motor":
                icon_res = R.drawable.type_bike;
                break;
            case "mobil":
                icon_res = R.drawable.type_car;
                break;
            case "mobil/motor":
            case "semua":
                icon_res = R.drawable.type_all;
                break;
        }
        return BitmapDescriptorFactory.fromResource(icon_res);
    }

    private void addMarkerStatic(
            final LatLng latLng,
            String title,
            String type,
            String openTime,
            String closeTime,
            int locationID
    ){
        final MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(setMarkerIcon(type));
        if(!openTime.equals("-NA-")){
            openTime = openTime.substring(0, 5);
        }
        if(!closeTime.equals( "-NA-")){
            closeTime = closeTime.substring(0, 5);
        }
        final Marker marker = mGoogleMap.addMarker(markerOptions);
        mGoogleMap.setInfoWindowAdapter(new MarkerInfoWindow());
        locationsManager.add(
                new TBLocation(latLng, position, title, openTime, closeTime, locationID)
                , marker
        );
    }

    private void addMarker(final LatLng latLng, boolean add) {
        final MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        if (add) {
            markerOptions.icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            );
        }
        mGoogleMap.addMarker(markerOptions);
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, R.string.app_name, R.string.app_name
        ) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Menu");
                }
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(mActivityTitle);
                }
                invalidateOptionsMenu();
            }
        };
    }

    private void addDrawerItems() {
        String[] menus = {"Settings", "Info"};
        mAdapter.set(new ArrayAdapter<>(this, R.layout.drawer_menu_item, menus));
        mDrawerList.setAdapter(mAdapter.get());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_add_location){
            startActivity(new Intent(this, AddNewLocation.class));
            finish();
            return true;
        }
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }
        googleMap.setMyLocationEnabled(true);
        boolean positionSet = false;
        if(location != null){
            position = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16));
            positionSet = true;
        }
        if(!positionSet){
            Toast.makeText(this, "Lokasi tidak terdeteksi!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add, menu);
        return true;
    }

}
