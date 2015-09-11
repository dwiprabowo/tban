package com.aqsara.tambalban;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by dwi on 11/09/15.
 */
public class BaseApp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBar();
    }

    private void setActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.actionbar_logo);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Lokasi");
        actionBar.setSubtitle("Tambal Ban");
    }
}
