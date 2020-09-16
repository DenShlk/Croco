package com.hypersphere.croco.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.hypersphere.croco.CrocoApplication;
import com.hypersphere.croco.model.WordsList;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsHelper {

	private static final String APP_PREFERENCES = "CROCO_APP_PREFS";
	private static final String SOUND_PREF_NAME = "SOUND_PREF_NAME";
	private static final String VIBRO_PREF_NAME = "VIBRO_PREF_NAME";
	private static final String LAST_CHOSEN_DICTS = "LAST_CHOSEN_DICTS";

	private static SharedPreferences preferences = CrocoApplication.getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
	private static SharedPreferences.Editor editor = preferences.edit();
	private static Gson mGson = new Gson();

	public static void setLastChosenDicts(List<WordsList> chosen){
		List<String> chosenNames = new ArrayList<>();
		for(WordsList dict : chosen){
			chosenNames.add(dict.name);
		}
		String data = mGson.toJson(chosenNames);
		editor.putString(LAST_CHOSEN_DICTS, data);
		editor.commit();
	}

	public static List<Boolean> getLastChosenDicts(){
		String data = preferences.getString(LAST_CHOSEN_DICTS, "[]");
		List<String> chosen = Arrays.asList(mGson.fromJson(data, (Type) String[].class));
		List<WordsList> dicts = CrocoApplication.getAvailableWordsLists();
		List<Boolean> isChosen = new ArrayList<>(dicts.size());
		for (int i = 0; i < dicts.size(); i++) {
			isChosen.add(chosen.contains(dicts.get(i).name));
		}
		return isChosen;
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
}
