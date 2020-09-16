package com.hypersphere.croco.helpers;

import android.util.Pair;

import com.google.gson.Gson;
import com.hypersphere.croco.CrocoApplication;
import com.hypersphere.croco.R;
import com.hypersphere.croco.model.GameConfig;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class IOHelper {

	public static final String USED_WORDS_LIST_FILE_NAME = "used_words.list";
	public static final String LAST_GAME_CONFIG_FILE_NAME = "last_game.config";

	public static List<String> namesList;

	public static Gson gson = new Gson();

	static public List<String> getListFromRes(int resourceId){
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(CrocoApplication.getContext().getResources().openRawResource(resourceId)));

		List<String> words = new ArrayList<>();
		try {
			String line = bufferedReader.readLine();
			while(line != null){
				words.add(line);
				line = bufferedReader.readLine();
			}
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return words;
	}

	static class GameData {
		GameConfig config;
		List<Pair<String, Integer>> scores;
		Integer currentPlayerIndex;

		public GameData(GameConfig config, List<Pair<String, Integer>> scores, Integer currentPlayerIndex) {
			this.config = config;
			this.scores = scores;
			this.currentPlayerIndex = currentPlayerIndex;
		}
	}

	static public void saveGame(GameConfig config, List<Pair<String, Integer>> scores, Integer currentPlayerIndex){
		GameData data = new GameData(config, scores, currentPlayerIndex);
		String stringData = gson.toJson(data);

		try {
			File dataFile = new File(CrocoApplication.filesDir, LAST_GAME_CONFIG_FILE_NAME);
			if (!dataFile.exists()) {
				dataFile.createNewFile();
			}

			BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile));
			writer.write(stringData);
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static public boolean isGameSaved(){
		File dataFile = new File(CrocoApplication.filesDir, LAST_GAME_CONFIG_FILE_NAME);
		return dataFile.exists();
	}

	static public Pair<Pair<GameConfig, List<Pair<String, Integer>>>, Integer> restoreGame(){
		try {
			File dataFile = new File(CrocoApplication.filesDir, LAST_GAME_CONFIG_FILE_NAME);
			if (!dataFile.exists()) {
				dataFile.createNewFile();
			}

			BufferedReader reader = new BufferedReader(new FileReader(dataFile));
			String stringData = reader.readLine();
			reader.close();

			GameData data = gson.fromJson(stringData, GameData.class);

			return new Pair<>(new Pair<>(data.config, data.scores), data.currentPlayerIndex);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	static public void clearUsedWords(){
		File usedWordsListFile = new File(CrocoApplication.filesDir, USED_WORDS_LIST_FILE_NAME);
		if (usedWordsListFile.exists()) {
			usedWordsListFile.delete();
		}
	}

	static public void addUsedWord(String word){
		try {
			File usedWordsListFile = new File(CrocoApplication.filesDir, USED_WORDS_LIST_FILE_NAME);
			if (!usedWordsListFile.exists()) {
				usedWordsListFile.createNewFile();
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(usedWordsListFile, true));
			writer.write(word);
			writer.newLine();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static public void addUsedWords(List<String> usedWords){
		try {
			File usedWordsListFile = new File(CrocoApplication.filesDir, USED_WORDS_LIST_FILE_NAME);
			if (!usedWordsListFile.exists()) {
				usedWordsListFile.createNewFile();
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(usedWordsListFile, true));
			for (String word : usedWords) {
				writer.write(word);
				writer.newLine();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static public String getRandomName(){
		if(namesList == null)
			namesList = getListFromRes(R.raw.names);

		Random random = new Random();
		return namesList.get(Math.abs(random.nextInt()) % namesList.size());
	}

	static public Set<String> getUsedWords() {
		HashSet<String> set = new HashSet<>();

		try {
			File usedWordsListFile = new File(CrocoApplication.filesDir, USED_WORDS_LIST_FILE_NAME);
			if (!usedWordsListFile.exists()) {
				usedWordsListFile.createNewFile();
			}else{
				BufferedReader reader = new BufferedReader(new FileReader(usedWordsListFile));
				String line;
				while ((line = reader.readLine()) != null){
					set.add(line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return set;
	}
}
