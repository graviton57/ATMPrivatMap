package com.havrylyuk.privat.util;

import android.content.SharedPreferences;

import com.havrylyuk.privat.AppPrivat;



/**
 *
 * Created by Igor Havrylyuk on 26.01.2017.
 */

public class PreferencesHelper {

    private static PreferencesHelper sInstance = null;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    //singleton
    public static PreferencesHelper getInstance() {
        if(sInstance == null) {
            sInstance = new PreferencesHelper();
        }
        return sInstance;
    }

    public PreferencesHelper() {
        this.sharedPreferences = AppPrivat.getSharedPreferences();
        this.editor = this.sharedPreferences.edit();
    }

    public String getSearchMode(String name, String defaultValue){
        return sharedPreferences.getString(name, defaultValue);
    }

    public void setSearchMode(String name,String mode){
        editor.putString(name, mode);
        editor.apply();
    }

    public int getSuggestionsCount(String name) {
        return  Integer.parseInt(sharedPreferences.getString(name, "3"));
    }

    public void setSuggestionsCount(String name, int count){
        editor.putInt(name, count);
        editor.apply();
    }

    public boolean isShowMarker(String name) {
        return sharedPreferences.getBoolean(name,true);
    }

    public void setShowMarker(String name, boolean show){
        editor.putBoolean(name, show);
        editor.apply();
    }

}
