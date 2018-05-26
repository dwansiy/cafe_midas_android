package com.xema.cafemidas.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xema.cafemidas.model.Profile;

import static android.content.Context.MODE_PRIVATE;

public class PreferenceHelper {
    // auto login
    public static void saveMyProfile(Context context, Profile profile) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String json = gson.toJson(profile, Profile.class);

        SharedPreferences preferences = context.getSharedPreferences(Constants.PREF_KEY_LOCAL, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        prefsEditor.putString(Constants.PREF_KEY_LOCAL_PROFILE, json);
        prefsEditor.apply();
    }

    public static Profile loadMyProfile(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.PREF_KEY_LOCAL, MODE_PRIVATE);
        String json = preferences.getString(Constants.PREF_KEY_LOCAL_PROFILE, null);
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        return gson.fromJson(json, Profile.class);
    }

    public static void saveId(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.PREF_KEY_LOCAL, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        prefsEditor.putString(Constants.PREF_KEY_LOCAL_ID, id);
        prefsEditor.apply();
    }

    public static String loadId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.PREF_KEY_LOCAL, MODE_PRIVATE);
        return preferences.getString(Constants.PREF_KEY_LOCAL_ID, null);
    }

    public static void savePw(Context context, String pw) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.PREF_KEY_LOCAL, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        prefsEditor.putString(Constants.PREF_KEY_LOCAL_PW, pw);
        prefsEditor.apply();
    }

    public static String loadPw(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.PREF_KEY_LOCAL, MODE_PRIVATE);
        return preferences.getString(Constants.PREF_KEY_LOCAL_PW, null);
    }

    public static void saveAutoSignInEnabled(Context context, boolean flag) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.PREF_KEY_LOCAL, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        prefsEditor.putBoolean(Constants.PREF_KEY_LOCAL_AUTO_SIGN_IN, flag);
        prefsEditor.apply();
    }

    public static boolean loadAutoSignInEnabled(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.PREF_KEY_LOCAL, MODE_PRIVATE);
        return preferences.getBoolean(Constants.PREF_KEY_LOCAL_AUTO_SIGN_IN, false);
    }

    public static void resetAll(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.PREF_KEY_LOCAL, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        prefsEditor.clear().apply();
    }
}
