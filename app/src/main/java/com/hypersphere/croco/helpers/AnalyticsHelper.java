package com.hypersphere.croco.helpers;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.hypersphere.croco.CrocoApplication;

import org.jetbrains.annotations.NotNull;

/**
 * Provides connection with Firebase Analytics service.
 */
public class AnalyticsHelper {

	private static FirebaseAnalytics mFirebaseAnalytics;

	/**
	 * Gets instance of {@link FirebaseAnalytics} object. Uses {@link CrocoApplication} to get
	 * context of app.
	 */
	private static void init(){
		Context context = CrocoApplication.getContext();
		mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
	}

	public static void sendEvent(ActionId actionId){
		sendEvent(actionId, null);
	}

	/**
	 * Sends event with given word in bundle.
	 * @param actionId
	 * @param word
	 */
	public static void sendEventWithWord(ActionId actionId, String word){
		sendEvent(actionId, createWordBundle(word));
	}

	public static void sendEvent(ActionId actionId, @Nullable Bundle bundle){
		if(mFirebaseAnalytics == null) init();

		mFirebaseAnalytics.logEvent(actionId.toString(), bundle);
	}

	/**
	 * Creates bundle with given world on key "word".
	 * @param word word to put in bundle.
	 * @return Bundle with word
	 */
	@NotNull
	public static Bundle createWordBundle(String word){
		Bundle bundle = new Bundle();
		bundle.putString("word", word);
		return bundle;
	}

	public enum ActionId {
		LaunchApp,
		ClickContinue,
		ClickNewGame,
		ClickRules,
		ClickSettings,
		ClickHelpInGame,
		CreateGame,
		GuessWord,
		SkipWord,
		RanOutOfWords,
		ResetWordsAfterRanOut,
		LeaveGameAfterRanOutOfWords,
	}

}
