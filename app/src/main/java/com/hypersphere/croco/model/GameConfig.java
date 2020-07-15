package com.hypersphere.croco.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.hypersphere.croco.helpers.IOHelper;

public class GameConfig implements Serializable {

	private int roundDuration = 20;//20 - debug
	private int pointsPerWord = 2;
	private int pointFinePerSkip = 1;

	public final int playersCount;
	public List<WordsList> wordsLists;
	public final List<String> playerNames = new ArrayList<>();

	public GameConfig(int playersCount, List<WordsList> wordsLists) {
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

	public int getRoundDuration() {
		return roundDuration;
	}

	public void setRoundDuration(int roundDuration) {
		this.roundDuration = roundDuration;
	}

	public int getPointsPerWord() {
		return pointsPerWord;
	}

	public void setPointsPerWord(int pointsPerWord) {
		this.pointsPerWord = pointsPerWord;
	}

	public int getPointFinePerSkip() {
		return pointFinePerSkip;
	}

	public void setPointFinePerSkip(int pointFinePerSkip) {
		this.pointFinePerSkip = pointFinePerSkip;
	}
}
