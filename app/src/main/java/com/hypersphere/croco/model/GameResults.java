package com.hypersphere.croco.model;

import java.io.Serializable;
import java.util.List;

/**
 * Presents results of finished game. At the moment contains List of {@link Player}.
 */
public class GameResults implements Serializable {

	private List<Player> playersScores;

	public GameResults(List<Player> playersScores) {
		this.playersScores = playersScores;
	}

	public List<Player> getPlayersScores() {
		return playersScores;
	}
}
