package com.example.criminalintent;

import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.util.Log;

public class CrimeLab {

	private static final String TAG = "CrimeLab";
	private static final String FILENAME = "crimes.json";

	private CriminalIntentJSONSerializer serializer;

	private static CrimeLab crimeLab;
	private Context appContext;
	private List<Crime> crimes;

	private CrimeLab(Context appContext) {
		this.appContext = appContext;
		serializer = new CriminalIntentJSONSerializer(appContext, FILENAME);

		try {
			crimes = serializer.loadCrimes();
		} catch (Exception e) {
			Log.e(TAG, "Error loading crimes", e);
		}
	}

	public static CrimeLab get(Context context) {
		if (crimeLab == null) {
			crimeLab = new CrimeLab(context.getApplicationContext());
		}

		return crimeLab;
	}

	public List<Crime> getCrimes() {
		return crimes;
	}

	public Crime getCrime(UUID id) {
		Crime target = null;
		for (Crime crime : crimes) {
			target = crime.getmId().equals(id) ? crime : target;
		}
		return target;
	}

	public CrimeLab add(Crime crime) {
		crimes.add(crime);
		return this;
	}

	public boolean remove(Crime crime) {
		return crimes.remove(crime);
	}

	public boolean saveCrimes() {
		try {
			serializer.save(crimes);
			Log.i(TAG, "Crimes saved to file");
			return true;
		} catch (Exception e) {
			Log.e(TAG, "Error saving crimes", e);
			return false;
		}
	}
}
