package com.aqsara.tambalban;

import android.os.Bundle;

public class InfoItem2Activity extends BaseApp {
    @Override
    protected String title() {
        return "Lokasi Terdekat";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_item2);
    }
}
