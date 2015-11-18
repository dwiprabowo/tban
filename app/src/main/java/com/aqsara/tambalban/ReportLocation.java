package com.aqsara.tambalban;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReportLocation extends Base
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    GoogleApiClient googleApiClient;
    GoogleMap googleMap;
    LatLng position;

    @Override
    protected String title() {
        return "Laporkan Lokasi";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_location);

        buildGoogleApiClient();
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        googleMap = mapFragment.getMap();
    }

    protected synchronized void buildGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        position = MainActivity.locationSelectedLatLng;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16));
        addMarker();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void addMarker(){
        final MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        googleMap.addMarker(markerOptions);
    }

    private void saveData(
            String user, String location, String desc
    ){
        class SaveData extends AsyncTask<String, Void, String>{

            @Override
            protected String doInBackground(String... params) {
                String user = params[0];
                String location = params[1];
                String desc = params[2];

                String strUrl = StaticData.base_url_api + "add_location_report";
                URL url;

                StringBuilder sb = new StringBuilder();
                try{
                    url = new URL(strUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    OutputStreamWriter outputStreamWriter =
                            new OutputStreamWriter(connection.getOutputStream());

                    outputStreamWriter.write(
                            "google_user_data=" + user.replace("&", "#dan#") +
                                    "&location_id=" + location +
                                    "&desc=" + desc
                    );
                    outputStreamWriter.flush();
                    outputStreamWriter.close();

                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;

                    while((line = reader.readLine()) != null){
                        sb.append(line);
                    }
                    reader.close();
                    inputStream.close();
                }catch(IOException e){
                    e.printStackTrace();
                }

                return sb.toString();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                startActivity(new Intent(ReportLocation.this, MainActivity.class));
                Toast.makeText(
                        ReportLocation.this, "Terima Kasih atas Laporan Anda", Toast.LENGTH_SHORT
                ).show();
                MainActivity.locationSelected = 0;
                MainActivity.locationSelectedLatLng = null;
                ReportLocation.this.finish();
            }
        }

        SaveData worker = new SaveData();
        worker.execute(user, location, desc);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void submitData(View view){
        saveData(
                StaticData.toUserStr(getUser()),
                String.valueOf(MainActivity.locationSelected),
                String.valueOf(((EditText) findViewById(R.id.desc_text)).getText())
        );
        view.setVisibility(View.GONE);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
