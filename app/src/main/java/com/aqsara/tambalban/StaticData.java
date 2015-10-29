package com.aqsara.tambalban;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dwi on 028, 10/28/15.
 */
public class StaticData {

    public static void saveUser(Context ctx, Person user){
        String userStr = user!=null?user.toString():"";
        Context context = ctx;
        SharedPreferences sp = context.getSharedPreferences(
                "appdata"
                , Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("user", userStr);
        editor.commit();
    }

    public static JSONObject getUser(Context ctx){
        Context context = ctx;
        SharedPreferences sp = context.getSharedPreferences("appdata", Context.MODE_PRIVATE);
        String userStr = sp.getString("user", "");
        Log.d("ban", userStr);
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
