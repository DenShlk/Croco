package com.hypersphere.croco.model;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class WordsList implements Serializable {

	public String name;
	public String description;

	public int resourceId;

	public WordsList(String name, String description, int resourceId) {
		this.name = name;
		this.description = description;
		this.resourceId = resourceId;
	}
}
