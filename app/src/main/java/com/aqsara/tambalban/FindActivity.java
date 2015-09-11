package com.aqsara.tambalban;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import android.app.Fragment;

/**
 * Created by dwi on 11/09/15.
 */
public class FindActivity extends BaseApp implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.find_map_fragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected String title() {
        return "Cari";
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng initial = MainActivity.getInitialLatLng();
        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initial, 16));
    }
}
