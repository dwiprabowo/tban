package com.aqsara.tambalban;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

/**
 * Created by dwi on 015, 9/15/15.
 */
public class GoogleLoginActivity extends BaseGoogleLogin implements View.OnClickListener{

    @Override
    protected String title() {
        return "Login Akun Google";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.sign_in_button){
            login();
            findViewById(R.id.sign_in_loading).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        super.onConnected(bundle);
        this.finish();
    }
}
