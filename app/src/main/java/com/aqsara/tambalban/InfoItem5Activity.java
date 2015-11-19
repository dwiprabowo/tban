package com.aqsara.tambalban;

import android.os.Bundle;

public class InfoItem5Activity extends BaseApp{
    @Override
    protected String title() {
        return "Laporkan Lokasi";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_item5);
    }
}
