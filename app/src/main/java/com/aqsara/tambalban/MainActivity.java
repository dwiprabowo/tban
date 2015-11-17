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
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
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
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Base implements OnMapReadyCallback {

    private ListView mDrawerList;
    private final ThreadLocal<ArrayAdapter<String>> mAdapter = new ThreadLocal<>();

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;

    GoogleMap mGoogleMap;
    String base_api_url = StaticData.base_url_api;

    private LatLng position;
    private Location location;

    private LocationsManager locationsManager;
    public static int locationSelected;
    public static LatLng locationSelectedLatLng;

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
            Util.d("marker location set: "+location);
            locationSelected = location.getId();
            locationSelectedLatLng = location.getLatLng();

            if(location == null){
                Toast.makeText(MainActivity.this, "location null!", Toast.LENGTH_SHORT).show();
                System.exit(0);
            }
            TextView _title = (TextView)v.findViewById(R.id.title);
            TextView _openTime = (TextView)v.findViewById(R.id.open_time);
            TextView _closeTime = (TextView)v.findViewById(R.id.close_time);
//            Button _reportButton = (Button)v.findViewById(R.id.report_button);
            _title.setText(location.getTitle());
            if(location.getOpen_time() != null){
                _openTime.setText("Buka  ~ "+location.getOpen_time());
            }
            if(location.getClose_time() != null){
                _closeTime.setText("Tutup ~ "+location.getClose_time());
            }
//            _reportButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    startActivity(new Intent(MainActivity.this, ReportLocation.class));
//                }
//            });
            return v;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);

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
        Util.d("reportLocation !!!");
        if(locationSelected != 0){
            startActivity(new Intent(MainActivity.this, ReportLocation.class));
        }else{
            Toast.makeText(this, "Lokasi pelaporan belum dipilih!", Toast.LENGTH_SHORT).show();
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void initLocation(){
        LocationManager locationManager =
                (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (
//                canGetLocation()
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            Util.d("permission cek success!!!");
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        Util.d("location test!!: "+location);
    }

    private boolean canGetLocation(){
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        );
    }

    @Override
    public void signOutSuccess() {
        startActivity(new Intent(this, LActivity.class));
        finish();
    }

    //    private void signOut(){
//        Auth.GoogleSignInApi.signOut(BaseGoogleLogin.googleClient()).setResultCallback(
//                new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(Status status) {
//                        Log.d(StaticData.app_tag, status.toString());
//                    }
//                }
//        );
//    }

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
                    addMarker(latLng, pending, false);
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
        Util.d("findClosest!!!");
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
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(icon_res);
        return icon;
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
        if(openTime != "-NA-"){
            openTime = openTime.substring(0, 5);
        }
        if(closeTime != "-NA-"){
            closeTime = closeTime.substring(0, 5);
        }
        final Marker marker = mGoogleMap.addMarker(markerOptions);
        mGoogleMap.setInfoWindowAdapter(new MarkerInfoWindow());
        locationsManager.add(
                new TBLocation(latLng, position, marker, title, openTime, closeTime, locationID)
                , marker
        );
    }

    private void addMarker(final LatLng latLng, boolean add, boolean confirm) {
        final MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        if (add) {
            markerOptions.icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            );
        }

        final Marker marker = mGoogleMap.addMarker(markerOptions);
        if (add && confirm) {
            addLocation(latLng, marker);
        }
//
//        if(!add){
//            locationsManager.add(latLng, position, marker);
//        }
    }

    private void addLocation(final LatLng latlng, final Marker marker) {
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(latlng.latitude - 0.002, latlng.longitude), 16)
        );
        AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(this, R.style.DialogSlideAnim)
        );
        AlertDialog dialog = builder
                .setMessage(
                        "Anda akan menambahkan lokasi pada latitude: "
                                + latlng.latitude
                                + " dan longitude: "
                                + latlng.longitude
                                + "\n"
                )
                .setPositiveButton("Tambahkan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new SaveTask().execute(
                                /*getUser().toString() */ ""
                                , String.valueOf(latlng.latitude)
                                , String.valueOf(latlng.longitude)
                        );
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        marker.remove();
                    }
                })
                .create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.BOTTOM;
        wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.show();
    }

    private class SaveTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String user = params[0];
            String lat = params[1];
            String lng = params[2];
            String strUrl = base_api_url + "add_user_locations";
            URL url;

            StringBuilder sb = new StringBuilder();
            try {
                url = new URL(strUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());

                outputStreamWriter.write("google_user_data=" + user.replace("&", "#dan#") + "&latitude=" + lat + "&longitude=" + lng);
                outputStreamWriter.flush();
                outputStreamWriter.close();

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
        String[] menus = {"Info"};
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
//        position = LoadingActivity.getInitialLatLng(this);
        googleMap.setMyLocationEnabled(true);
        boolean positionSet = false;
        if(location != null){
            position = new LatLng(location.getLatitude(), location.getLongitude());
            if(position != null){
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16));
                positionSet = true;
            }
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
