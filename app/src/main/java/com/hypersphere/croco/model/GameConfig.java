package com.hypersphere.croco.model;

import com.hypersphere.croco.helpers.IOHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameConfig implements Serializable {

	public static final int MIN_ROUND_DURATION = 30;
	public static final int MAX_ROUND_DURATION = 300;
	public static final int BASE_ROUND_DURATION = 60;
	public static final int MIN_PLAYERS_COUNT = 2;
	public static final int MAX_PLAYERS_COUNT = 10;
	public static final int BASE_PLAYERS_COUNT = 3;

	public final int pointsPerWord = 2;
	public final int pointFinePerSkip = 1;
	public final int roundDuration;
	public final int playersCount;
	public final List<WordsList> wordsLists;
	public final List<String> playerNames = new ArrayList<>();

	public GameConfig(int roundDuration, int playersCount, List<WordsList> wordsLists) {
		this.roundDuration = roundDuration;
		this.playersCount = playersCount;
		this.wordsLists = wordsLists;

		for (int i = 0; i < playersCount; i++) {
			String name;
			do{
				name = IOHelper.getRandomName();
			}while (playerNames.contains(name));

			playerNames.add(name);
		}
	}
}
