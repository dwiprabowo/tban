package com.aqsara.tambalban;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class LocationsManager {

    private HashMap<Marker, TBLocation> locationsMapping = new HashMap<>();
    private ArrayList<TBLocation> locations = new ArrayList<>();
    GoogleMap googleMap;

    public LocationsManager(GoogleMap googleMap){
        this.googleMap = googleMap;
    }

    public void add(TBLocation location, Marker marker){
        locations.add(location);
        locationsMapping.put(marker, location);
    }

    public TBLocation getLocation(Marker marker){
        TBLocation location = locationsMapping.get(marker);
        return  location;
    }

    public void closest(){
        if(locations.size() > 0){
            Collections.sort(locations);
            locations.get(0).select(googleMap);
        }
    }
}
