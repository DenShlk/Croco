package com.hypersphere.croco;

import android.app.Application;
import android.content.Context;

import com.hypersphere.croco.model.WordsList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class CrocoApplication extends Application {

	public static File filesDir;
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		filesDir = getFilesDir();

		context = CrocoApplication.this;
	}

	static public Context getContext(){
		return context;
	}

	static public List<WordsList> getAvailableWordsLists(){
		List<WordsList> list = new ArrayList<>();

		list.add(new WordsList("Базовый", "Обычный набор слов. Например: фантазёр, кетчуп, удача.", R.raw.basic));
		list.add(new WordsList("Еда", "Кушать подано! Приготовьтесь показывать котлеты по киевски и паннакоту!", R.raw.food));

		return list;
	}

	static public List<Boolean> getCheckedAtInit(){
		List<Boolean> list = new ArrayList<>(getAvailableWordsLists().size());

		for(WordsList wordsList : getAvailableWordsLists()){
			list.add(false);
		}
		// TODO: 08.06.2020 smarter
		list.set(0, true);

		return list;
	}
}
