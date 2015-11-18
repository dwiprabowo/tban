package com.aqsara.tambalban;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class TBLocation implements Comparable{

    private String title, open_time, close_time;
    private int id;
    private LatLng latLng, pos;
    private float weight;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOpen_time() {
        return open_time;
    }

    public String getClose_time() {
        return close_time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    TBLocation(LatLng latLng, LatLng pos,
               String title, String open_time, String close_time, int id){
        this.latLng = latLng;
        this.pos = pos;
        this.title = title;
        this.open_time = open_time;
        this.close_time = close_time;
        this.id = id;
        calculate();
    }

    public void select(GoogleMap googleMap){
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

        this.weight = (float) (dist * meterConversion);
    }

    @Override
    public int compareTo(@NonNull Object another) {
        TBLocation o = (TBLocation) another;
        if(weight > o.weight){
            return 1;
        }else if(weight < o.weight){
            return -1;
        }
        return 0;
    }
}
