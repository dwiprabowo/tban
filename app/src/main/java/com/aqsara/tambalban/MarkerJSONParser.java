package com.aqsara.tambalban;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MarkerJSONParser {

    public List<HashMap<String, String>> parse(JSONObject jObject){
        JSONArray jMarkers = new JSONArray();

        try{
            jMarkers = new JSONArray();

            jMarkers = jObject.getJSONArray("data");
        }catch (JSONException e){
            e.printStackTrace();
        }

        return getMarkers(jMarkers);
    }

    private List<HashMap<String, String>> getMarkers(JSONArray jMarkers){
        int markersCount = jMarkers.length();
        List<HashMap<String, String>> markersList = new ArrayList<>();
        HashMap<String, String> marker;

        for(int i = 0; i < markersCount;i++){
            try{
                marker = getMarker((JSONObject)jMarkers.get(i));
                markersList.add(marker);
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        return markersList;
    }

    private HashMap<String, String> getMarker(JSONObject jMarker){
        HashMap<String, String> marker = new HashMap<>();
        String lat = "-NA-";
        String lng = "-NA-";
        String name = "-NA-";
        String type = "-NA-";
        String open_time = "-NA-";
        String close_time = "-NA-";
        String is_pending = "false";
        String id = "";

        try{
            if(!jMarker.isNull("latitude")){
                lat = jMarker.getString("latitude");
            }

            if(!jMarker.isNull("longitude")){
                lng = jMarker.getString("longitude");
            }

            if(!jMarker.isNull("name")){
                name = jMarker.getString("name");
            }

            if(!jMarker.isNull("type")){
                type = jMarker.getString("type");
            }

            if(!jMarker.isNull("open_time")){
                open_time = jMarker.getString("open_time");
            }

            if(!jMarker.isNull("close_time")){
                close_time = jMarker.getString("close_time");
            }

            if(!jMarker.isNull("longitude")){
                lng = jMarker.getString("longitude");
            }

            if(!jMarker.isNull("is_pending")){
                is_pending = jMarker.getString("is_pending");
            }

            if(!jMarker.isNull("id")){
                id = jMarker.getString("id");
            }

            marker.put("latitude", lat);
            marker.put("longitude", lng);
            marker.put("is_pending", is_pending);
            marker.put("name", name);
            marker.put("type", type);
            marker.put("open_time", open_time);
            marker.put("close_time", close_time);
            marker.put("id", id);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return marker;
    }
}
