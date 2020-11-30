package com.hypersphere.croco;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.hypersphere.croco.model.WordsList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CrocoApplication extends Application {

	public static File filesDir;
	private static Context context;
	private static List<WordsList> wordsLists;

	@Override
	public void onCreate() {
		super.onCreate();
		filesDir = getFilesDir();

		context = CrocoApplication.this;
		initWordsLists();
	}

	private void initWordsLists(){
		ArrayList<WordsList> lists = new ArrayList<>();

		lists.add(new WordsList(R.raw.animals, R.drawable.animals));
		lists.add(new WordsList(R.raw.basic, R.drawable.basic));
		lists.add(new WordsList(R.raw.food, R.drawable.food));
		lists.add(new WordsList(R.raw.animals, R.drawable.animals));

		wordsLists = Collections.unmodifiableList(lists);
	}

	static public Context getContext(){
		return context;
	}

	static public List<WordsList> getAvailableWordsLists(){
		return wordsLists;
	}

	public static boolean isInternetAvailable() {
		ConnectivityManager cm =
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

		return activeNetwork != null &&
				activeNetwork.isConnectedOrConnecting();
	}
}
