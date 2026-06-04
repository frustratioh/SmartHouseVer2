package com.example.smarthouse.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsManager {
    private static final String PREF_NAME = "smart_house_prefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_PIN_CODE = "pin_code";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ADDRESS = "address";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void saveUserId(Context context, long userId) {
        getPrefs(context).edit().putLong(KEY_USER_ID, userId).apply();
    }

    public static long getUserId(Context context) {
        return getPrefs(context).getLong(KEY_USER_ID, -1);
    }

    public static void savePinCode(Context context, String pin) {
        getPrefs(context).edit().putString(KEY_PIN_CODE, pin).apply();
    }

    public static String getPinCode(Context context) {
        return getPrefs(context).getString(KEY_PIN_CODE, "");
    }

    public static boolean hasPinCode(Context context) {
        return !getPinCode(context).isEmpty();
    }

    public static void saveUserData(Context context, String username, String email, String address) {
        getPrefs(context).edit()
                .putString(KEY_USERNAME, username)
                .putString(KEY_EMAIL, email)
                .putString(KEY_ADDRESS, address)
                .apply();
    }

    public static String getUserName(Context context) {
        return getPrefs(context).getString(KEY_USERNAME, "");
    }

    public static String getUserEmail(Context context) {
        return getPrefs(context).getString(KEY_EMAIL, "");
    }

    public static String getUserAddress(Context context) {
        return getPrefs(context).getString(KEY_ADDRESS, "");
    }

    public static void clearAll(Context context) {
        getPrefs(context).edit().clear().apply();
    }

    public static void clearPinCode(Context context) {
        getPrefs(context).edit().remove(KEY_PIN_CODE).apply();
    }

    private static final String KEY_AVATAR = "avatar";

    public static void saveAvatar(Context context, String avatarBase64) {
        getPrefs(context).edit().putString(KEY_AVATAR, avatarBase64).apply();
    }

    public static String getAvatar(Context context) {
        return getPrefs(context).getString(KEY_AVATAR, "");
    }
}