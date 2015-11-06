package com.aqsara.tambalban;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by dwi on 005, 11/5/15.
 */
public class LocationsManager {

    private ArrayList<TBLocation> locations = new ArrayList<>();
    GoogleMap googleMap;

    public LocationsManager(GoogleMap googleMap){
        this.googleMap = googleMap;
    }

    public void add(LatLng location, LatLng position, Marker marker){
        locations.add(new TBLocation(location, position, marker));
    }

    public void closest(){
        if(locations.size() > 0){
            Collections.sort(locations);
            locations.get(0).select(googleMap);
        }
    }
}
