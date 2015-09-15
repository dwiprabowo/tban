package com.aqsara.tambalban;

import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.InputStream;

/**
 * Created by dwi on 015, 9/15/15.
 */
public class BaseGoogleLogin extends BaseApp implements
        GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;

    private boolean mIsResolving = false;
    private boolean mShouldResolve = false;

    private Person loggedInUser = null;

    private ImageView imageView = null;

    public Person getUser(){
        return loggedInUser;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();
    }

    @Override
    protected String title() {
        return null;
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
            Log.d("ban", "user baru terinisialisasi");
            loggedInUser = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
            if(imageView != null){
                Log.d("ban", "harusnya ganti gambar user");
                setUserImage(imageView, loggedInUser.getImage().getUrl());
            }else{
                Log.d("ban", "imageview null");
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

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

    public void setUserImage(ImageView imageView, String url){
        new DownloadImageTask(imageView).execute(url);
    }

    public void setUserImageViewMenu(ImageView imageView){
        this.imageView = imageView;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(addWhiteBorder(result, 5));
        }

        private Bitmap addWhiteBorder(Bitmap bmp, int borderSize) {
            Bitmap bmpWithBorder = Bitmap.createBitmap(
                    bmp.getWidth() + borderSize * 2
                    , bmp.getHeight() + borderSize * 2
                    , bmp.getConfig()
            );
            Canvas canvas = new Canvas(bmpWithBorder);
            canvas.drawColor(Color.BLACK);
            canvas.drawBitmap(bmp, borderSize, borderSize, null);
            return bmpWithBorder;
        }
    }
}
