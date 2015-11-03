package com.aqsara.tambalban;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

public class StaticData {

    public static void saveUser(Context ctx, Person user){
        String userStr = user!=null?user.toString():"";
        SharedPreferences sp = ctx.getSharedPreferences(
                "appdata"
                , Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("user", userStr);
        editor.apply();
    }

    public static JSONObject getUser(Context ctx){
        SharedPreferences sp = ctx.getSharedPreferences("appdata", Context.MODE_PRIVATE);
        String userStr = sp.getString("user", "");
        JSONObject user = null;
        try {
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
