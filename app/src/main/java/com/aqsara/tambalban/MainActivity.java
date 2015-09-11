package com.aqsara.tambalban;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements
    GoogleApiClient.ConnectionCallbacks
    , GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    boolean appReady = false;

    public static Context mainActivity;
    public static final String INITIAL_LOCATION = "initial_location";

    private static Location appLocation;

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    public void setAppLocation(Location lastKnownLocation){
        appLocation = lastKnownLocation;
        Context context = mainActivity;
        SharedPreferences sp = context.getSharedPreferences(
                "appdata"
                , Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("last_known_location", getAppLocationJSONString());
        editor.commit();
    }

    public static LatLng getInitialLatLng(){
        Context context = mainActivity;
        SharedPreferences sp = context.getSharedPreferences("appdata", Context.MODE_PRIVATE);
        String lastKnownLocation = sp.getString("last_known_location", "{}");
        JSONObject latLng = null;
        try {
            latLng = new JSONObject(lastKnownLocation);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LatLng return_value = null;
        if(latLng != null){
            try {
                return_value = new LatLng(latLng.getDouble("lat"), latLng.getDouble("lng"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return return_value;
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
        mainActivity = this;
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        buildGoogleApiClient();
        animateWheel();
    }

    private void animateWheel(){
        RotateAnimation anim = new RotateAnimation(
                0f, 359f
                ,RotateAnimation.RELATIVE_TO_SELF, .5f, RotateAnimation.RELATIVE_TO_SELF, .5f
        );
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(2400);

        final ImageView wheel = (ImageView) findViewById(R.id.imageView);
        wheel.startAnimation(anim);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onConnected(Bundle bundle) {
        setAppLocation(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));
        if(!isNetworkConnected()){
            _exit("Koneksi Internet tidak terdeteksi");
            return;
        }
        Toast.makeText(this, "Mohon tunggu, mencari lokasi Anda ...", Toast.LENGTH_SHORT)
                .show();
        runDelay(3000, new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
                MainActivity.this.finish();
                startActivity(intent);
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        _exit("Cannot Retrieve Your Location");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        _exit("Cannot Retrieve Your Location");
    }

    private void _exit(String message){
        Toast.makeText(this, "Cannot Retrieve Your Location", Toast.LENGTH_LONG).show();
        runDelay(3000, new Runnable() {
            @Override
            public void run() {
                MainActivity.this.finish();
            }
        });
    }

    private void runDelay(long milis, Runnable r){
        Handler h = new Handler();
        h.postDelayed(r, milis);
    }

    private boolean isNetworkConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if(ni == null){
            return false;
        }
        return true;
    }
}
