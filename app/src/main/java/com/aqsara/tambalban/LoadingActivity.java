package com.aqsara.tambalban;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;


public class LoadingActivity extends Activity implements
    GoogleApiClient.ConnectionCallbacks
    , GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    public static Context mainActivity;
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
        editor.apply();
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
                Double lat = -7.47895799208485400;
                Double lng = 110.22654052823782000;
                return_value = new LatLng(lat, lng);
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
            _exit("Tidak ada koneksi Internet");
            return;
        }
        runDelay(3000, new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (StaticData.getUser(LoadingActivity.this) != null) {
                    intent = new Intent(LoadingActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(LoadingActivity.this, LoginActivity.class);
                }
                LoadingActivity.this.finish();
                startActivity(intent);
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        _exit("Lokasi Anda tidak terdeteksi");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        _exit("Lokasi Anda tidak terdeteksi");
    }

    private void _exit(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        runDelay(3000, new Runnable() {
            @Override
            public void run() {
                LoadingActivity.this.finish();
            }
        });
    }

    private void runDelay(long milis, Runnable r){
        Handler h = new Handler();
        h.postDelayed(r, milis);
    }

    private boolean isNetworkConnected(){
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(LoadingActivity.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null;
    }
}
