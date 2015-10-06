package com.aqsara.tambalban;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.plus.model.people.Person;

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
 * Created by dwi on 11/09/15.
 */
public class MapActivity extends BaseGoogleLogin implements OnMapReadyCallback, LocationListener {

    GoogleMap mGoogleMap;
    String base_api_url = "http://10.42.0.20/api/web/";

    int mode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mode = getIntent().getIntExtra("mode", 0);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.find_map_fragment);
        mapFragment.getMapAsync(this);
        mGoogleMap = mapFragment.getMap();

        switch (mode){
            case AppConstants.MAP_MODE_SEARCH:
                new RetrieveTask().execute();
                getSupportActionBar().setTitle("Cari Lokasi");
                break;
            case AppConstants.MAP_MODE_ADD:
                getSupportActionBar().setTitle("Tambah Lokasi");
                mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        addMarker(latLng);
                    }
                });
                break;
        }
    }

    @Override
    protected String title() {
        return null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng initial = MainActivity.getInitialLatLng();
        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initial, 16));
    }

    private void addMarker(final LatLng latlng){
        final MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latlng);
        markerOptions.title(latlng.latitude + "," + latlng.longitude);
        final Marker marker = mGoogleMap.addMarker(markerOptions);
        if(mode == AppConstants.MAP_MODE_ADD){
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(latlng.latitude-0.004, latlng.longitude), 16)
            );
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    new ContextThemeWrapper(this, R.style.DialogSlideAnim)
            );
            AlertDialog dialog = builder
            .setMessage(
                    "Anda akan menambahkan lokasi pada latitude: "
                    +latlng.latitude
                    +" dan longitude: "
                    +latlng.longitude
                    +"\ndata tambahan: "
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

    private class RetrieveTask extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... params) {
            String strUrl = base_api_url+"get_locations";
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
