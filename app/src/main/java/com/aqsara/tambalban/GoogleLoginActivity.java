package com.aqsara.tambalban;

import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

/**
 * Created by dwi on 015, 9/15/15.
 */
public class GoogleLoginActivity extends BaseApp implements
    GoogleApiClient.ConnectionCallbacks
    , GoogleApiClient.OnConnectionFailedListener
    , View.OnClickListener{

    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;

    private boolean mIsResolving = false;
    private boolean mShouldResolve = false;

    @Override
    protected String title() {
        return "Google Sign In";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null){
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            String personName = currentPerson.getDisplayName();
            String personPhoto = currentPerson.getImage().getUrl();
            String personGooglePlusProfile = currentPerson.getUrl();
            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
            Log.d("ban", currentPerson.toString());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.sign_in_button){
            onSignInClicked();
        }
    }

    void onSignInClicked(){
        mShouldResolve = true;
        mGoogleApiClient.connect();
        Toast.makeText(
                this, "SignIn Clicked!", Toast.LENGTH_LONG
        ).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("ban", "onConnectionFailed: " + connectionResult);
        if(!mIsResolving && mShouldResolve){
            if(connectionResult.hasResolution()){
                try{
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                }catch (IntentSender.SendIntentException e){
                    Log.e("ban", "Could not resolve ConnectionResult", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            }else{
                Toast.makeText(
                        this, "connectionResult.hasResoultion() false", Toast.LENGTH_LONG
                ).show();
            }
        }else{
            Toast.makeText(
                    this, "signed out", Toast.LENGTH_LONG
            ).show();
        }
    }
}
