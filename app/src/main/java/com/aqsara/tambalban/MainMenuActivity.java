package com.aqsara.tambalban;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.widget.GridView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;


public class MainMenuActivity extends BaseApp implements
        GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient googleApiClient;
    Location lastKnownLocation;

    GridView gv;
    Context context;
    ArrayList prgmName;
    public static int [] prgmImages = {
            R.drawable.search
            , R.drawable.add
            , R.drawable.user
            , R.drawable.info,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        gv=(GridView) findViewById(R.id.gridView1);
        gv.setAdapter(new MenuAction(this, prgmImages));
    }

    @Override
    protected String title() {
        return "Menu Utama";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onConnected(Bundle bundle) {
        setAppLocation(LocationServices.FusedLocationApi.getLastLocation(googleApiClient));
    }

    private void setAppLocation(Location location){
        lastKnownLocation = location;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
