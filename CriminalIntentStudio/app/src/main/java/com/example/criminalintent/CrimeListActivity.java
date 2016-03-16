package com.example.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class CrimeListActivity extends SingleFragmentActivity implements
		CrimeListFragment.Callbacks, CrimeFragment.Callbacks {

	@Override
	protected int getLayoutResId() {
		return R.layout.activity_masterdetail;
	}

	@Override
	protected Fragment createFragment() {
		return new CrimeListFragment();
	}

	@Override
	public void onCrimeSelected(Crime crime) {
		if (findViewById(R.id.detailFragmentContainer) == null) {
			Intent intent = new Intent(this, CrimePagerActivity.class);
			intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getmId());
			startActivity(intent);
		} else {
			FragmentManager fm = this.getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();

			Fragment oldDetail = fm
					.findFragmentById(R.id.detailFragmentContainer);
			if (oldDetail != null) {
				ft.remove(oldDetail);
			}
			ft.add(R.id.detailFragmentContainer,
					CrimeFragment.newInstant(crime.getmId())).commit();
		}
	}

	@Override
	public void onCrimeUpdated(Crime crime) {
		CrimeListFragment fragment = (CrimeListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragmentContainer);
		fragment.updateUI();
	}
}
