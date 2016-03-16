package com.example.criminalintent;

import org.json.JSONException;
import org.json.JSONObject;

public class Photo {

	private final static String JSON_FILENAME = "fileName";

	private final String fileName;

	public Photo(String fileName) {
		this.fileName = fileName;
	}

	public Photo(JSONObject json) throws JSONException {
		fileName = json.getString(JSON_FILENAME);
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_FILENAME, fileName);
		return json;
	}

	public String getFileName() {
		return fileName;
	}
}
