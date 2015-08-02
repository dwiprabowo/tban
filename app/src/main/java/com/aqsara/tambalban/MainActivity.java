package com.aqsara.tambalban;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity
        implements
            GoogleApiClient.ConnectionCallbacks
            , GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    boolean appReady = false;

    public static final String INITIAL_LOCATION = "initial_location";

    private static Location appLocation;

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        Log.d("googleapi", "google api executed!");
    }

    public void startApp(View view){
        Log.d("button", "clicked start!");
        Log.d("location", getAppLocationJSONString());
        if(getAppLocation() == null){
            return;
        }
        Intent intent = new Intent(this, AnotherActivity.class);

        intent.putExtra(INITIAL_LOCATION, getAppLocationJSONString());
        startActivity(intent);
    }

    public void setAppLocation(Location lastKnownLocation){
        appLocation = lastKnownLocation;
    }

    public Location getAppLocation(){
        return appLocation;
    }

    public String getAppLocationJSONString(){
        Location location = getAppLocation();

        if(location == null){
            return null;
        }
        JSONObject jsonValue = new JSONObject();

        try {
            jsonValue.put("lat", location.getLatitude());
            jsonValue.put("lng", location.getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonValue.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildGoogleApiClient();
        Log.d("activity", "oncreate finish");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("googleapi", "onConnected");
        setAppLocation(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("googleapi", "onConnectionSupended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("googleapi", "onConnectionFailed");
    }
}
