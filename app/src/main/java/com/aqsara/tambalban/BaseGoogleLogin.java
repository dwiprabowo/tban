package com.aqsara.tambalban;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class BaseGoogleLogin extends BaseApp implements
        GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;

    private boolean mIsResolving = false;
    private boolean mShouldResolve = false;

    private Person loggedInUser = null;

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

    public void logOut(){
        if(!mGoogleApiClient.isConnected()){
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Keluar Akun")
                .setMessage("Sambungan dengan akun google Anda akan diputus, Anda yakin?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                        Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
                        mGoogleApiClient.disconnect();
//                        imageView.setImageResource(R.drawable.user);
                        StaticData.deleteUser(BaseGoogleLogin.this);
                        BaseGoogleLogin.this.finish();
                    }
                })
                .setNegativeButton("Tidak", null)
                .show();
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

    public boolean isLoggedIn(){
        return mGoogleApiClient.isConnected();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null){
            loggedInUser = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        }
    }

//    public String getUserName(){
//        if(loggedInUser != null){
//            return loggedInUser.getDisplayName();
//        }
//        return null;
//    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if(!mIsResolving && mShouldResolve){
            if(connectionResult.hasResolution()){
                try{
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                }catch (IntentSender.SendIntentException e){
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            }
        }
    }

//    public void setUserImage(ImageView imageView, String url){
//        new DownloadImageTask(imageView).execute(url);
//    }

//    public void setUserImageViewMenu(ImageView imageView){
//        this.imageView = imageView;
//    }

//    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
//        ImageView bmImage;
//
//        public DownloadImageTask(ImageView bmImage) {
//            this.bmImage = bmImage;
//        }
//
//        protected Bitmap doInBackground(String... urls) {
//            String urldisplay = urls[0];
//            Bitmap mIcon11 = null;
//            try {
//                InputStream in = new java.net.URL(urldisplay).openStream();
//                mIcon11 = BitmapFactory.decodeStream(in);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return mIcon11;
//        }
//
//        protected void onPostExecute(Bitmap result) {
////            bmImage.setImageBitmap(addWhiteBorder(result, 5));
//        }
//
////        private Bitmap addWhiteBorder(Bitmap bmp, int borderSize) {
////            int color = Color.BLACK;
////            if(bmp == null){
////                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.user);
////                color = Color.TRANSPARENT;
////            }
////            Bitmap bmpWithBorder = Bitmap.createBitmap(
////                    bmp.getWidth() + borderSize * 2
////                    , bmp.getHeight() + borderSize * 2
////                    , bmp.getConfig()
////            );
////            Canvas canvas = new Canvas(bmpWithBorder);
////            canvas.drawColor(color);
////            canvas.drawBitmap(bmp, borderSize, borderSize, null);
////            return bmpWithBorder;
////        }
//    }

    public void login(){
        mShouldResolve = true;
        mGoogleApiClient.connect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            if(resultCode != RESULT_OK){
                mShouldResolve = false;
            }
            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }
}
