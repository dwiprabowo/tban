package com.aqsara.tambalban;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONException;
import org.json.JSONObject;

public class StaticData {

    public static String protocol = "http";
    public static String host_api = "ban.aiueoo.com";
    public final static String base_url_api = "/api/web/";
    private static GoogleSignInAccount account;

    public static String getBaseAPIURL() {
        return StaticData.protocol + "://" + StaticData.host_api + StaticData.base_url_api;
    }

    public static void setAccount(GoogleSignInAccount account){
        StaticData.account = account;
    }

    public static GoogleSignInAccount getAccount(){
        return account;
    }

    public static String toUserStr(GoogleSignInAccount account){
        JSONObject user = new JSONObject();
        if(account != null){
            try {
                user.put("displayName", account.getDisplayName());
                user.put("id", account.getId());
                user.put("photo", account.getPhotoUrl());
                user.put("email", account.getEmail());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return user.toString();
        }
        return "";
    }
}
