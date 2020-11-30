package com.hypersphere.croco.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.hypersphere.croco.CrocoApplication;
import com.hypersphere.croco.model.WordsList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SettingsHelper {

	private static final String APP_PREFERENCES = "CROCO_APP_PREFS";
	private static final String SOUND_PREF_NAME = "SOUND_PREF_NAME";
	private static final String VIBRO_PREF_NAME = "VIBRO_PREF_NAME";
	private static final String LAST_CHOSEN_DICTS = "LAST_CHOSEN_DICTS";

	private static SharedPreferences preferences = CrocoApplication.getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
	private static SharedPreferences.Editor editor = preferences.edit();
	private static Gson mGson = new Gson();

	public static void setLastChosenLists(List<WordsList> chosen){
		List<String> chosenNames = new ArrayList<>();
		for(WordsList list : chosen){
			chosenNames.add(list.getName());
		}
		String data = mGson.toJson(chosenNames);
		editor.putString(LAST_CHOSEN_DICTS, data);
		editor.commit();
	}

	public static List<Boolean> getLastChosenLists(){
		String data = preferences.getString(LAST_CHOSEN_DICTS, "[]");
		HashSet<String> chosen = mGson.fromJson(data, HashSet.class);

		List<WordsList> lists = CrocoApplication.getAvailableWordsLists();

		List<Boolean> isChosen = new ArrayList<>(lists.size());

		for (WordsList list : lists) {
			isChosen.add(chosen.contains(list.getName()));
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
