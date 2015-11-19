package com.aqsara.tambalban;

import android.os.Bundle;

public class InfoItem3Activity extends BaseApp {
    @Override
    protected String title() {
        return "Lokasi TambalBan";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_item3);
    }
}
