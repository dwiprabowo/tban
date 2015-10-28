package com.aqsara.tambalban;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by dwi on 013, 10/13/15.
 */
public class NewMainActivity extends AppCompatActivity implements OnMapReadyCallback{

    private ListView mDrawerList;
    private final ThreadLocal<ArrayAdapter<String>> mAdapter = new ThreadLocal<>();

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;

    GoogleMap mGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);

        mDrawerList = (ListView) findViewById(R.id.navList);
        addDrawerItems();

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(
                        NewMainActivity.this
                        , "Menu #"+position+" selected!"
                        , Toast.LENGTH_SHORT
                ).show();
            }
        });

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        
        setupDrawer();

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
        mGoogleMap = mapFragment.getMap();
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(
            this, mDrawerLayout, R.string.app_name, R.string.app_name
        ){
            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                if(getSupportActionBar() != null){
                    getSupportActionBar().setTitle("Menu");
                }
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
                if(getSupportActionBar() != null){
                    getSupportActionBar().setTitle(mActivityTitle);
                }
                invalidateOptionsMenu();
            }
        };
    }

    private void addDrawerItems() {
        String[] menus = {"Bantuan", "Tentang"};
        mAdapter.set(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, menus));
        mDrawerList.setAdapter(mAdapter.get());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return
                id == R.id.action_settings
                        || mDrawerToggle.onOptionsItemSelected(item)
                        || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng initial = MainActivity.getInitialLatLng();
        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initial, 16));
    }
}
