package com.hypersphere.croco.model;

import java.io.Serializable;

/**
 * Presents data about one player or command. Contains it's name, points and state.
 * States: player is moving, player moved, player did not move.
 */
public class Player implements Serializable {

	private String name;
	private Integer points;
	private State state;

	public Player(String name) {
		this(name, 0);
	}

	public Player(String name, Integer points) {
		this(name, points, State.DidNotMove);
	}

	public Player(String name, Integer points, State state) {
		this.name = name;
		this.points = points;
		this.state = state;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPoints() {
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}

	public void addPoints(Integer delta){
		points += delta;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public enum State {
		Moved,
		Moving,
		DidNotMove,
	}
}
