package com.example.criminalintent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.content.Context;

public class CriminalIntentJSONSerializer {

	private Context context;
	private String fileName;

	public CriminalIntentJSONSerializer(Context context, String fileName) {
		this.context = context;
		this.fileName = fileName;
	}

	@SuppressLint("NewApi")
	public void save(List<Crime> crimes) throws JSONException, IOException {
		JSONArray array = new JSONArray();
		for (Crime crime : crimes) {
			array.put(crime.toJSON());
		}

		try (Writer writer = new OutputStreamWriter(context.openFileOutput(
				fileName, Context.MODE_PRIVATE))) {
			writer.write(array.toString());
		}
	}

	@SuppressLint("NewApi")
	public List<Crime> loadCrimes() throws IOException, JSONException {
		List<Crime> crimes = new ArrayList<>();
		List<String> files = Arrays.asList(context.fileList());
		if(files.contains(fileName)) {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(
					context.openFileInput(fileName)))) {
				StringBuilder sb = new StringBuilder();
				String str;
				while ((str = reader.readLine()) != null) {
					sb.append(str);
				}
				
				JSONArray array = (JSONArray) new JSONTokener(sb.toString())
				.nextValue();
				for(int i = 0; i < array.length(); i++) {
					crimes.add(new Crime(array.getJSONObject(i)));
				}
			}
		}
		return crimes;
	}
}
