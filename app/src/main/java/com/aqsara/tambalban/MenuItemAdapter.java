package com.aqsara.tambalban;

import android.app.Activity;
import android.widget.ArrayAdapter;

/**
 * Created by dwi on 028, 10/28/15.
 */
public class MenuItemAdapter extends ArrayAdapter<String> {

    private final Activity context;

    public MenuItemAdapter(Activity context, String[] titles, boolean isMenuUser){
        super(context, R.layout.menu_item, titles);
        this.context = context;
    }
}
