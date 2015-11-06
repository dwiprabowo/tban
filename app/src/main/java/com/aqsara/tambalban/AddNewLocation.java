package com.aqsara.tambalban;

import android.app.TimePickerDialog;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
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

/**
 * Created by dwi on 006, 11/6/15.
 */
public class AddNewLocation extends BaseApp
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    GoogleApiClient googleApiClient;
    Location location;
    GoogleMap googleMap;
    LatLng position;

    @Override
    protected String title() {
        return "Tambah Lokasi";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_location);

        buildGoogleApiClient();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        googleMap = mapFragment.getMap();

        Spinner spinner = (Spinner) findViewById(R.id.spinner_vehicle_type);
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        this, R.array.vehicle_types, android.R.layout.simple_spinner_item
                );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
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
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        position = new LatLng(location.getLatitude(), location.getLongitude());
//        googleMap.setMyLocationEnabled(true);
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

    public void openTime(View view){
        timePicker(R.id.open_time, "Jam Buka");
    }

    private void timePicker(int button_id, String title){
        final Button button = (Button) findViewById(button_id);
        TimePickerDialog timePickerDialog;
        timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        button.setText(toHumanTime(hourOfDay, minute));
                    }
                },
                0,
                0,
                true
        );
        timePickerDialog.setTitle(title);
        timePickerDialog.show();
    }

    private String toHumanTime(int hour, int minute){
        String hourStr = String.valueOf(hour);
        String minuteStr = String.valueOf(minute);
        if(hourStr.length() < 2){
            hourStr = "0"+hourStr;
        }
        if(minuteStr.length() < 2){
            minuteStr = "0"+minuteStr;
        }
        return hourStr+":"+minuteStr;
    }

    public void closeTime(View view){
        timePicker(R.id.close_time, "Jam Tutup");
    }

    private void saveData(
            String user, String lat, String lng,
            String title, String type, String openTime, String closeTime
    ){
        class SaveData extends AsyncTask<String, Void, String>{

            @Override
            protected String doInBackground(String... params) {
                String user = params[0];
                String lat = params[1];
                String lng = params[2];
                String title = params[3];
                String type = params[4];
                String openTime = params[5];
                String closeTime = params[6];

                String strUrl = StaticData.base_url_api + "add_user_locations";
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
                                    "&latitude=" + lat +
                                    "&longitude=" + lng +
                                    "&title=" + title +
                                    "&type=" + type +
                                    "&open_time=" + openTime +
                                    "&close_time=" + closeTime
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
                AddNewLocation.this.finish();
            }
        }

        SaveData worker = new SaveData();
        worker.execute(user, lat, lng, title, type, openTime, closeTime);
    }

    public void submitData(View view){
        saveData(
                StaticData.getUser(this).toString(),
                String.valueOf(position.latitude),
                String.valueOf(position.longitude),
                ((EditText) findViewById(R.id.editText)).getText().toString(),
                ((Spinner) findViewById(R.id.spinner_vehicle_type)).getSelectedItem().toString(),
                ((Button) findViewById(R.id.open_time)).getText().toString(),
                ((Button)findViewById(R.id.close_time)).getText().toString()
        );
        view.setVisibility(View.GONE);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
    }
}
