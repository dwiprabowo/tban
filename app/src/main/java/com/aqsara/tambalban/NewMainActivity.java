package com.aqsara.tambalban;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dwi on 013, 10/13/15.
 */
public class NewMainActivity extends AppCompatActivity implements OnMapReadyCallback{

    private ListView mDrawerList;
    private final ThreadLocal<ArrayAdapter<String>> mAdapter = new ThreadLocal<>();

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;

    GoogleMap mGoogleMap;
    String base_api_url = "http://10.42.0.20/api/web/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);

        mDrawerList = (ListView) findViewById(R.id.navList);
        addDrawerItems();

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(
                        NewMainActivity.this
                        , "Menu #"+position+" selected!"
                        , Toast.LENGTH_SHORT
                ).show();
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
    }

    private class RetrieveTask extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... params) {
            String strUrl = base_api_url + "get_locations";
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
                LatLng latLng = new LatLng(
                    Double.parseDouble(marker.get("latitude")),
                        Double.parseDouble(marker.get("longitude"))
                );
                addMarker(latLng);
            }
        }
    }

    private void addMarker(final LatLng latLng){
        final MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        final Marker marker = mGoogleMap.addMarker(markerOptions);
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
        String[] menus = {"Bantuan", "Tentang"};
        mAdapter.set(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, menus));
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
