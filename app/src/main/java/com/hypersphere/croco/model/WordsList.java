package com.hypersphere.croco.model;

import android.util.Log;

import com.hypersphere.croco.helpers.IOHelper;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class WordsList implements Serializable {

	private int resourceId;
	private int drawableResourceId;

	private boolean unpacked;
	private String name;
	private String description;
	private List<String> words;

	public WordsList(int resourceId, int drawableResourceId){
		this.resourceId = resourceId;
		this.drawableResourceId = drawableResourceId;
	}

	// TODO: 30.11.2020 should delete data to optimize memory usage
	private void unpackFromResources(){
		try {
			List<String> rawData = IOHelper.getListFromRes(resourceId);

			name = rawData.get(0);
			description = rawData.get(1);

			// remove name and description
			rawData.remove(0);
			rawData.remove(0);

			words = Collections.unmodifiableList(rawData);

			unpacked = true;
		}catch (IndexOutOfBoundsException e){
			Log.e(getClass().getName(), String.format("Error while resource (id=%s) unpacking: %s", resourceId, e.getMessage()));
		}
	}

	public int getResourceId() {
		return resourceId;
	}

	public int getDrawableResourceId() {
		return drawableResourceId;
	}

	public List<String> getWords(){
		if(unpacked) return words;

		unpackFromResources();
		return words;
	}

	public String getName(){
		if(unpacked) return name;

		unpackFromResources();
		return name;
	}

	public String getDescription(){
		if(unpacked) return description;

		unpackFromResources();
		return description;
	}
}
