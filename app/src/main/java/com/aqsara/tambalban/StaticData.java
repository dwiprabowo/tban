package com.aqsara.tambalban;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

public class StaticData {

    public final static String base_url_api = "http://192.168.57.1/api/web/";
    public final static String app_tag = "tambalBan";

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

    public static void saveUser(Context ctx, GoogleSignInAccount account){
        String userStr = toUserStr(account);
        SharedPreferences sp = ctx.getSharedPreferences(
                "appdata"
                , Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("user", userStr);
        editor.apply();
    }

//    public static void saveUser(Context ctx, Person user){
//        String userStr = user!=null?user.toString():"";
//        SharedPreferences sp = ctx.getSharedPreferences(
//                "appdata"
//                , Context.MODE_PRIVATE
//        );
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putString("user", userStr);
//        editor.apply();
//    }

    public static JSONObject getUser(Context ctx){
        SharedPreferences sp = ctx.getSharedPreferences("appdata", Context.MODE_PRIVATE);
        String userStr = sp.getString("user", "");
        JSONObject user = null;
        try {
            Log.d("ban", "userStr value: "+userStr);
            try{
                throw new Exception();
            }catch (Exception e){
                e.printStackTrace();
//                System.exit(0);
            }
            user = new JSONObject(userStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static void deleteUser(Context ctx){
        saveUser(ctx, null);
    }
}
