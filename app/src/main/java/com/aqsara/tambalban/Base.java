package com.aqsara.tambalban;

import android.app.Activity;
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

    private GoogleApiClient googleApiClient;
    private GoogleSignInAccount user;
    protected GoogleSignInOptions gso;

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
            handleSignInResult(opr.get());
        }else{
//            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult result) {
//                    hideProgressDialog();
                    handleSignInResult(result);
                }
            });

        }
    }

    private void handleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()){
            user = result.getSignInAccount();
            StaticData.setAccount(user);
            signedInUser();
        }else{
            if(isNetworkConnected()){
                notSignedInUser();
            }else{
                _exit("Tidak terkoneksi dengan Internet", this);
            }
        }
    }

//    private void showProgressDialog(){
//        if(progressDialog == null){
//            progressDialog = new ProgressDialog(this);
//            progressDialog.setMessage("Menunggu Login Akun");
//            progressDialog.setIndeterminate(true);
//        }
//        progressDialog.show();
//    }
//
//    private void hideProgressDialog(){
//        if(progressDialog != null && progressDialog.isShowing()){
//            progressDialog.hide();
//        }
//    }

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

    public void _exit(String message, Activity activity){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        activity.finish();
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

    public void delay(Runnable r){
        delay(r, 2400l);
    }

    public void delay(Runnable r, Long milis){
        final Handler handler = new Handler();
        handler.postDelayed(r, milis);
    }
}
