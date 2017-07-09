package com.aqsara.tambalban;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * Created by dwi on 7/9/2017.
 */

public class SettingsActivity extends Base{
    @Override
    protected String title() {
        return "Settings";
    }

    EditText webProtocol, host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        webProtocol = (EditText) findViewById(R.id.settings_web_protocol);
        host = (EditText) findViewById(R.id.settings_host);

        webProtocol.setText(StaticData.protocol);
        host.setText(StaticData.host_api);
    }

    public void update(View view) {
        StaticData.protocol = String.valueOf(webProtocol.getText());
        StaticData.host_api = String.valueOf(host.getText());
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
}
