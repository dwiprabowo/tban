package com.aqsara.tambalban;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.widget.GridView;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;


public class MainMenuActivity extends BaseGoogleLogin{

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
        gv.setAdapter(new MenuCore(this, prgmImages));
    }

    @Override
    protected String title() {
        return "Lokasi TambalBan";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void setAppLocation(Location location){
        lastKnownLocation = location;
    }
}
