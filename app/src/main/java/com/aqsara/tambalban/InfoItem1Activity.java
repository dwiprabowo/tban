package com.aqsara.tambalban;

import android.os.Bundle;

/**
 * Created by dwi on 18/11/15.
 */
public class InfoItem1Activity extends BaseApp {
    @Override
    protected String title() {
        return "Google Login";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_item1);
    }
}
