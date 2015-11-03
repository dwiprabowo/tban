package com.aqsara.tambalban;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends BaseGoogleLogin implements View.OnClickListener {

    @Override
    protected String title() {
        return "Login Akun Google";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.sign_in_button){
            login();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        super.onConnected(bundle);
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        if(getUser() != null){
            StaticData.saveUser(this, getUser());
        }
        this.finish();
    }
}
