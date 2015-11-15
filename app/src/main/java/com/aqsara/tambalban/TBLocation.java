package com.aqsara.tambalban;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by dwi on 005, 11/5/15.
 */
public class TBLocation implements Comparable{

    private String title, open_time, close_time;
    private int id;
    private LatLng latLng, pos;
    private float weight;
    private Marker marker;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOpen_time() {
        return open_time;
    }

    public void setOpen_time(String open_time) {
        this.open_time = open_time;
    }

    public String getClose_time() {
        return close_time;
    }

    public void setClose_time(String close_time) {
        this.close_time = close_time;
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

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public LatLng getPos() {
        return pos;
    }

    public void setPos(LatLng pos) {
        this.pos = pos;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    TBLocation(LatLng latLng, LatLng pos, Marker marker,
               String title, String open_time, String close_time, int id){
        this.latLng = latLng;
        this.pos = pos;
        this.marker = marker;
        this.title = title;
        this.open_time = open_time;
        this.close_time = close_time;
        this.id = id;
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
