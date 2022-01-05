package com.dev.tuanteo.tuanamthanh.units;

import android.content.Context;
import android.content.SharedPreferences;

import com.dev.tuanteo.tuanamthanh.object.RepeatType;

public class SharePreferenceUtils {

    private static final String SHUFFLER_PREFERENCE = "shuffler_reference";
    private static final String REPEAT_PREFERENCE = "repeat_reference";

    private static SharedPreferences mPreference;
    private static SharePreferenceUtils mInstance;

    private SharePreferenceUtils(Context context) {
        mPreference = context.getSharedPreferences("com.example.app", Context.MODE_PRIVATE);
    }

    public static SharePreferenceUtils getInstance(Context context) {
        if (mInstance == null) {
            return mInstance = new SharePreferenceUtils(context);
        }
        return mInstance;
    }

    public void putShufflerValue(boolean value) {
        SharedPreferences.Editor editor = mPreference.edit();
        editor.putBoolean(SHUFFLER_PREFERENCE, value);
        editor.apply();
    }

    public boolean getShufflerValue() {
        return mPreference.getBoolean(SHUFFLER_PREFERENCE, false);
    }

    public void setRepeatType(RepeatType type) {
        SharedPreferences.Editor editor = mPreference.edit();
        editor.putString(REPEAT_PREFERENCE, String.valueOf(type));
        editor.apply();
    }

    public String getRepeatType() {
        return mPreference.getString(REPEAT_PREFERENCE, String.valueOf(RepeatType.REPEAT_NO));
    }
}
