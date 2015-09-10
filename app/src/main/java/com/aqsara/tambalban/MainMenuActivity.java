package com.aqsara.tambalban;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import java.util.ArrayList;


public class MainMenuActivity extends Activity {

    GridView gv;
    Context context;
    ArrayList prgmName;
    public static String[] prgmNameList = {
      "Let Us C", "c++", "JAVA", "Jsp"
    };
    public static int [] prgmImages = {
            R.drawable.search, R.drawable.add_place, R.drawable.user
            , R.drawable.info
            /*
            , R.drawable.images4, R.drawable.images5
            , R.drawable.images6, R.drawable.images7, R.drawable.images8
            */
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Log.d("ban", "tes");
        gv=(GridView) findViewById(R.id.gridView1);
        gv.setAdapter(new CustomAdapter(this, prgmNameList, prgmImages));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
