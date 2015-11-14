package com.aqsara.tambalban;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by dwi on 005, 11/5/15.
 */
public class TBLocation implements Comparable{

    private LatLng latLng, pos;
    private float weight;
    private Marker marker;

    TBLocation(LatLng latLng, LatLng pos, Marker marker){
        this.latLng = latLng;
        this.pos = pos;
        this.marker = marker;
        calculate();
    }

    public void select(GoogleMap googleMap){
//        marker.showInfoWindow();
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(this.latLng, 16));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(this.latLng, 16));
    }

    private void calculate(){
        if(this.pos == null)return;
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(this.pos.latitude-this.latLng.latitude);
        double dLng = Math.toRadians(this.pos.longitude-this.latLng.longitude);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(this.latLng.latitude)) * Math.cos(Math.toRadians(this.pos.latitude)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        int meterConversion = 1609;

        this.weight = new Float(dist * meterConversion).floatValue();
    }

    public float getWeight(){
        return this.weight;
    }

    @Override
    public int compareTo(Object another) {
        TBLocation o = (TBLocation) another;
        if(weight > o.weight){
            return 1;
        }else if(weight < o.weight){
            return -1;
        }
        return 0;
    }
}
