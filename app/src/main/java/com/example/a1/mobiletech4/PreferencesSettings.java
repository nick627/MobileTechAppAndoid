package com.example.a1.mobiletech4;

import android.content.Context;
import android.content.SharedPreferences;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PreferencesSettings {

    private static final String CURRENT_PREF_FILE = "settings_pref";
    private static final String PREVIOUS_PREF_FILE = "settings_pref_previous";

    /*
    static void saveToPref(Context context, String str) {
        SharedPreferences sharedPref = context.getSharedPreferences(CURRENT_PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("code", str);
        editor.commit();
    }
    */

    static void saveToPref(Context context, String str) {
        SharedPreferences.Editor editor;
        SharedPreferences sharedPref = context.getSharedPreferences(CURRENT_PREF_FILE, Context.MODE_PRIVATE);
        boolean hasVisited = sharedPref.getBoolean("hasVisited", false);

        // if the first programm starting
        if (!hasVisited) {
            editor = sharedPref.edit();
            editor.putBoolean("hasVisited", true);
            editor.commit();
        } else {
            sharedPref = context.getSharedPreferences(PREVIOUS_PREF_FILE, Context.MODE_PRIVATE);
            editor = sharedPref.edit();
            editor.putString("code", getCurrentCode(context));
            editor.putBoolean("hasVisited", true);
            editor.commit();
        }

        sharedPref = context.getSharedPreferences(CURRENT_PREF_FILE, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putString("code", str);
        editor.commit();
    }

    static String getCurrentCode(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(CURRENT_PREF_FILE, Context.MODE_PRIVATE);
        String defaultValue = "";
        return sharedPref.getString("code", defaultValue);
    }

    static String getPreviousCode(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREVIOUS_PREF_FILE, Context.MODE_PRIVATE);
        String defaultValue = "";

        boolean hasVisitedCurrent = sharedPref.getBoolean("hasVisited", false);
        if(!hasVisitedCurrent) {
            return new String("");
        }

        return sharedPref.getString("code", defaultValue);
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    static int deleteCode(Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(CURRENT_PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
        return 1;
    }

    static void saveOldCode(Context context, String oldCode)
    {
        SharedPreferences.Editor editor;
        SharedPreferences sharedPref;

        sharedPref = context.getSharedPreferences(CURRENT_PREF_FILE, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putString("code_old", oldCode);
        editor.commit();
    }

    static void deleteOldCode(Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(CURRENT_PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("code_old");
        editor.commit();
    }

    static String getOldCode(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(CURRENT_PREF_FILE, Context.MODE_PRIVATE);
        String defaultValue = "";
        return sharedPref.getString("code_old", defaultValue);
    }
}
