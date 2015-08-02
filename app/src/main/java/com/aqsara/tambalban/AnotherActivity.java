package com.aqsara.tambalban;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dwi on 24/07/15.
 */
public class AnotherActivity extends AppCompatActivity implements OnMapReadyCallback {

    Location location;

    JSONObject parseLocation(String locationValue){
        JSONObject object = null;
        try {
            object = new JSONObject(locationValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    void setLocation(String locationValue){
        JSONObject locationObject = parseLocation(locationValue);
        Log.d("location", "locationObject:"+locationObject.toString());
        try {
            if(location == null){
                location = new Location("");
            }
            location.setLatitude(locationObject.getDouble("lat"));
            location.setLongitude(locationObject.getDouble("lng"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    Location getLocation(){
        return location;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.another_layout);

        Intent intent = getIntent();
        setLocation(intent.getStringExtra(MainActivity.INITIAL_LOCATION));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng location = new LatLng(this.location.getLatitude(), this.location.getLongitude());
        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));

        googleMap.addMarker(
                new MarkerOptions().title("You")
                        .snippet("Last know location").position(location)
        );
    }
}
