package com.akslibrary.utility;

import android.content.Context;
import android.content.SharedPreferences;

public class ShareData {

    private SharedPreferences mPref = null;
    private SharedPreferences.Editor mEditor = null;
    private Context mContext;


    public ShareData(Context context) {
        mContext = context;
    }


    public void addToSharedPref(String prefName, String data) {
        mPref = mContext.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        mEditor = mPref.edit();
        mEditor.putString(prefName, data);
        mEditor.commit();
    }

    public void addToSharedPref(String prefName, Integer data) {
        mPref = mContext.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        mEditor = mPref.edit();
        mEditor.putInt(prefName, data);
        mEditor.commit();

    }

    public void addToSharedPref(String prefName, Long value) {
        mPref = mContext.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        mEditor = mPref.edit();
        mEditor.putLong(prefName, value);
        mEditor.commit();
    }

    public void addToSharedPref(String prefName, boolean value) {
        mPref = mContext.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        mEditor = mPref.edit();
        mEditor.putBoolean(prefName, value);
        mEditor.commit();
    }

    public void clearSharedPref(String prefName) {
        mPref = mContext.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        mEditor = mPref.edit();
        mEditor.remove(prefName);
        mEditor.commit();
    }

    /**
     * Default value is null
     *
     * @param prefName
     * @return
     */
    public String getDataFromSharedPref(String prefName) {
        mPref = mContext.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return mPref.getString(prefName, null);
    }

    public Boolean getBooleanFromSharedPref(String prefName, Boolean defaultValue) {
        mPref = mContext.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return mPref.getBoolean(prefName, defaultValue);
    }

    public void addToSharedPref(String prefName, Boolean value) {
        mPref = mContext.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        mEditor = mPref.edit();
        mEditor.putBoolean(prefName, value);
        mEditor.commit();
    }

    public Integer getIntegerFromSharedPref(String prefName, Integer defaultValue) {
        mPref = mContext.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return mPref.getInt(prefName, defaultValue);
    }

    public Long getLongFromSharedPref(String prefName, Long defaultValue) {
        mPref = mContext.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return mPref.getLong(prefName, defaultValue);
    }


}
