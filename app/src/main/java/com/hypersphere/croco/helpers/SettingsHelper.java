package com.hypersphere.croco.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.hypersphere.croco.CrocoApplication;
import com.hypersphere.croco.model.GameConfig;

public class SettingsHelper {

	private static final String APP_PREFERENCES = "CROCO_APP_PREFS";
	private static final String SOUND_PREF_NAME = "SOUND_PREF_NAME";
	private static final String VIBRO_PREF_NAME = "VIBRO_PREF_NAME";
	private static final String LAST_CHOSEN_SETTINGS = "LAST_CHOSEN_SETTINGS";
	private static final String TIPS_HAVE_SHOWN = "TIPS_HAVE_SHOWN";

	private static SharedPreferences preferences = CrocoApplication.getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
	private static SharedPreferences.Editor editor = preferences.edit();
	private static Gson mGson = new Gson();

	public static void setLastGameSettings(GameConfig config){
		String data = mGson.toJson(config);
		editor.putString(LAST_CHOSEN_SETTINGS, data);
		editor.commit();
	}

	public static GameConfig getLastGameSettings(){
		String data = preferences.getString(LAST_CHOSEN_SETTINGS, null);
		if(data == null){
			return GameConfig.getBaseConfig();
		}

		GameConfig config = mGson.fromJson(data, GameConfig.class);
		return config;
	}

	public static void setSoundPref(boolean value){
		editor.putBoolean(SOUND_PREF_NAME, value);
		editor.commit();
	}

	public static boolean getSoundPref(){
		return preferences.getBoolean(SOUND_PREF_NAME, true);
	}

	public static void setVibroPref(boolean value){
		editor.putBoolean(VIBRO_PREF_NAME, value);
		editor.commit();
	}

	public static boolean getVibroPref(){
		return preferences.getBoolean(VIBRO_PREF_NAME, true);
	}

	public static boolean getHaveTipsShown(){
		return preferences.getBoolean(TIPS_HAVE_SHOWN, false);
	}

	public static void setTipsHaveShown(boolean value){
		editor.putBoolean(TIPS_HAVE_SHOWN, value);
		editor.commit();
	}
}
