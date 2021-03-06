package com.aqsara.tambalban;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dwi on 013, 10/13/15.
 */
public class NewMainActivity extends BaseGoogleLogin implements OnMapReadyCallback{

    private ListView mDrawerList;
    private final ThreadLocal<ArrayAdapter<String>> mAdapter = new ThreadLocal<>();

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;

    GoogleMap mGoogleMap;
    String base_api_url = "http://10.42.0.20/api/web/";

    @Override
    protected String title() {
        return getString(R.string.app_name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);

        mDrawerList = (ListView) findViewById(R.id.navList);
        addDrawerItems();

        LayoutInflater inflater = getLayoutInflater();
        View listHeaderView = inflater.inflate(R.layout.header_list, null, false);
        mDrawerList.addHeaderView(listHeaderView);

        try {
            TextView headerProfileName = (TextView) findViewById(R.id.header_profile_name);
            headerProfileName.setText(StaticData.getUser(this).getString("displayName"));
            Log.d("ban", StaticData.getUser(this).getString("image").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case 0:
                        if(NewMainActivity.this.isLoggedIn()){
                            NewMainActivity.this.logOut();
                        }
                        break;
                    case 1:
                            startActivity(new Intent(NewMainActivity.this, RootHelpActivity.class));
                        break;
                }
            }
        });

        if(getSupportActionBar() != null){
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

        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                addMarker(latLng, true, true);
            }
        });
    }

    private class RetrieveTask extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... params) {
            String user_id = null;
            try{
                user_id = StaticData.getUser(NewMainActivity.this).getString("id");
            }catch (Exception e){
                e.printStackTrace();
            }
            String strUrl = base_api_url + "get_locations/" + user_id;
            URL url = null;
            StringBuffer sb = new StringBuffer();
            try{
                url = new URL(strUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream iStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
                String line = "";
                while ((line=reader.readLine()) != null){
                    sb.append(line);
                }
                reader.close();
                iStream.close();
            }catch(MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e){
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

    private class ParserTask extends AsyncTask<String, Void, List<HashMap<String, String>>>{

        @Override
        protected List<HashMap<String, String>> doInBackground(String... params) {
            MarkerJSONParser markerParser = new MarkerJSONParser();
            JSONObject json = null;
            try{
                json = new JSONObject(params[0]);
            }catch (JSONException e){
                e.printStackTrace();
            }
            List<HashMap<String, String>> markersList = markerParser.parse(json);
            return markersList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            super.onPostExecute(hashMaps);
            for(int i=0;i< hashMaps.size();i++){
                HashMap<String, String> marker = hashMaps.get(i);
                Log.d("ban", "marker value: "+marker.toString());
                LatLng latLng = new LatLng(
                    Double.parseDouble(marker.get("latitude")),
                        Double.parseDouble(marker.get("longitude"))
                );
                boolean pending = Boolean.parseBoolean(marker.get("is_pending"));
                addMarker(latLng, pending, false);
            }
        }
    }

    private void addMarker(final LatLng latLng){
        addMarker(latLng, false, true);
    }

    private void addMarker(final LatLng latLng, boolean add, boolean confirm){
        final MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        if(add){
            markerOptions.icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            );
        }
        final Marker marker = mGoogleMap.addMarker(markerOptions);
        if(add && confirm){
            addLocation(latLng, marker);
        }
    }

    private void addLocation(final LatLng latlng, final Marker marker){
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(latlng.latitude-0.002, latlng.longitude), 16)
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
                                + "\ndata tambahan: "
                )
                .setPositiveButton("Tambahkan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new SaveTask().execute(
                                getUser().toString()
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
            Log.d("ban", "user: "+user.replace("&", "#dan#"));
            Log.d("ban", "lat: "+lat);
            Log.d("ban", "lng: "+lng);
            String strUrl = base_api_url+"add_user_locations";
            URL url = null;

            StringBuffer sb = new StringBuffer();
            try{
                url = new URL(strUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());

                outputStreamWriter.write("google_user_data="+ user.replace("&", "#dan#") + "&latitude=" + lat + "&longitude="+lng);
                outputStreamWriter.flush();
                outputStreamWriter.close();

                InputStream iStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));

                String line = "";

                while((line=reader.readLine()) != null){
                    sb.append(line);
                }

                Log.d("ban", sb.toString());
                reader.close();
                iStream.close();
            }catch(MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
            return sb.toString();
        }

    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(
            this, mDrawerLayout, R.string.app_name, R.string.app_name
        ){
            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                if(getSupportActionBar() != null){
                    getSupportActionBar().setTitle("Menu");
                }
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
                if(getSupportActionBar() != null){
                    getSupportActionBar().setTitle(mActivityTitle);
                }
                invalidateOptionsMenu();
            }
        };
    }

    private void addDrawerItems() {
        String[] menus = {"INFO"};
        mAdapter.set(new ArrayAdapter<>(this, R.layout.drawer_menu_item, menus));
        mDrawerList.setAdapter(mAdapter.get());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return
                id == R.id.action_settings
                        || mDrawerToggle.onOptionsItemSelected(item)
                        || super.onOptionsItemSelected(item);
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
        LatLng initial = MainActivity.getInitialLatLng();
        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initial, 16));
    }
}
