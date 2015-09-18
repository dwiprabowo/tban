package com.aqsara.tambalban;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dwi on 11/09/15.
 */
public class FindActivity extends BaseApp implements OnMapReadyCallback, LocationListener {

    GoogleMap mGoogleMap;
    String base_api_url = "http://10.42.0.20/api/web/locations";

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mMenuTitles;

    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;

    private boolean addMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.find_map_fragment);
        mapFragment.getMapAsync(this);

        mGoogleMap = mapFragment.getMap();
//        mGoogleMap.setMyLocationEnabled(true);
//        LatLng latestLocation = latestLocation();
//        if(latestLocation != null){
//            Log.d("location", "Get latest Location!");
//            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latestLocation, 15));
//        }else{
//            Log.d("location", "set default Location!");
//            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-7.5534545, 110.6686321), 9));
//        }
//        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latlng) {
//                if (addMode) {
//                    addMarker(latlng);
//                    sendToServer(latlng);
//                }
//            }
//        });

        new RetrieveTask().execute();

//        initDrawer(savedInstanceState);
    }

    LatLng latestLocation(){
        LatLng result = null;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null && location.getTime() > Calendar.getInstance().getTimeInMillis() - 2 * 60 * 1000) {
            result = new LatLng(location.getLatitude(), location.getLongitude());
        }
        return result;
    }

//    void initDrawer(Bundle savedInstanceState){
//        mTitle = mDrawerTitle = getTitle();
//        mMenuTitles = getResources().getStringArray(R.array.menus_array);
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        mDrawerList = (ListView) findViewById(R.id.left_drawer);
//
//        // set a custom shadow that overlays the main content when the drawer opens
//        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
//        // set up the drawer's list view with items and click listener
//        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
//                R.layout.drawer_list_item, mMenuTitles));
//        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
//
//        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close){
//            @Override
//            public void onDrawerClosed(View drawerView) {
//                getSupportActionBar().setTitle(mTitle);
//                invalidateOptionsMenu();
//            }
//
//            @Override
//            public void onDrawerOpened(View drawerView) {
//                getSupportActionBar().setTitle(mTitle);
//                invalidateOptionsMenu();
//            }
//        };
//
//        mDrawerLayout.setDrawerListener(mDrawerToggle);
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        if (savedInstanceState == null) {
//            selectItem(0);
//        }
//    }

    @Override
    protected String title() {
        return "Cari Lokasi";
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng initial = MainActivity.getInitialLatLng();
        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initial, 16));
//        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initial, 15));
    }

    private void addMarker(LatLng latlng){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latlng);
        markerOptions.title(latlng.latitude + "," + latlng.longitude);
        mGoogleMap.addMarker(markerOptions);
    }

    private void sendToServer(LatLng latlng){
        new SaveTask().execute(latlng);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private class SaveTask extends AsyncTask<LatLng, Void, Void> {
        @Override
        protected Void doInBackground(LatLng... params) {
            String lat = Double.toString(params[0].latitude);
            String lng = Double.toString(params[0].longitude);
            String strUrl = base_api_url;
            URL url = null;
            try{
                url = new URL(strUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());

                outputStreamWriter.write("lat=" + lat + "&lng="+lng);
                outputStreamWriter.flush();
                outputStreamWriter.close();

                InputStream iStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";

                while((line=reader.readLine()) != null){
                    sb.append(line);
                }

                reader.close();
                iStream.close();
            }catch(MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }
    }

    private class RetrieveTask extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... params) {
            String strUrl = base_api_url;
            URL url = null;
            StringBuffer sb = new StringBuffer();
            try{
                url = new URL(strUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream iStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
                String line = "";
                while((line=reader.readLine()) != null){
                    sb.append(line);
                }

                reader.close();
                iStream.close();
            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Void, List<HashMap<String, String>>>{
        @Override
        protected List<HashMap<String, String>> doInBackground(String... params) {
            MarkerJSONParser markerParser = new MarkerJSONParser();
            JSONObject json = null;
            try{
                json = new JSONObject(params[0]);
            }catch(JSONException e){
                e.printStackTrace();
            }
            List<HashMap<String, String>> markersList = markerParser.parse(json);
            return markersList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {
            for(int i=0;i < result.size();i++){
                HashMap<String, String> marker = result.get(i);
                LatLng latLng = new LatLng(
                        Double.parseDouble(marker.get("latitude"))
                        , Double.parseDouble(marker.get("longitude"))
                );
                addMarker(latLng);
            }
        }
    }

}
