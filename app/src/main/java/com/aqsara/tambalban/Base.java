package com.aqsara.tambalban;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class Base extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener{

    public static final int RC_SIGN_IN = 9001;
    private GoogleApiClient googleApiClient;
    private GoogleSignInAccount user;
    private GoogleSignInOptions gso;

    public GoogleSignInOptions getGso(){
        return gso;
    }

    public GoogleApiClient getGoogleApiClient(){
        return googleApiClient;
    }

    public void setUser(GoogleSignInAccount user){
        this.user = user;
    }

    protected String title(){
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setLogo(R.mipmap.actionbar_logo);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(title()==null?getString(R.string.app_name):title());
        }

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr =
                Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()){
            user = opr.get().getSignInAccount();
            signedInUser();
        }else{
            if(isNetworkConnected()){
                notSignedInUser();
            }else{
                _exit("Tidak terkoneksi dengan Internet");
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    public void signedInUser(){}
    public void notSignedInUser(){}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Util.d(connectionResult.getErrorMessage());
    }

    public GoogleSignInAccount getUser(){
        return user;
    }

    public void _exit(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public boolean isNetworkConnected(){
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(LoadingActivity.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null;
    }

    public void signOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        signOutSuccess();
                        // [END_EXCLUDE]
                    }
                });
    }

    public void signOutSuccess(){}
}
