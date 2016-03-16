package com.example.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends ActionBarActivity implements
		CrimeFragment.Callbacks {

	private ViewPager viewPager;
	private List<Crime> crimes;

	@Override
	public void onCreate(Bundle savedInstantState) {
		super.onCreate(savedInstantState);
		viewPager = new ViewPager(this);
		viewPager.setId(R.id.viewPager);
		setContentView(viewPager);

		crimes = CrimeLab.get(this).getCrimes();

		FragmentManager fm = getSupportFragmentManager();
		viewPager.setAdapter(new FragmentStatePagerAdapter(fm) {

			@Override
			public Fragment getItem(int arg0) {
				return CrimeFragment.newInstant(crimes.get(arg0).getmId());
			}

			@Override
			public int getCount() {
				return crimes.size();
			}

		});

		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				updateTitle(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		Crime crime = CrimeLab.get(this).getCrime(
				(UUID) getIntent().getSerializableExtra(
						CrimeFragment.EXTRA_CRIME_ID));
		int index = crimes.indexOf(crime);
		viewPager.setCurrentItem(index);
		updateTitle(index);
	}

	public void updateTitle(int index) {
		String title = crimes.get(index).getmTitle();
		if (title != null) {
			setTitle(title);
		}
	}

	@Override
	public void onCrimeUpdated(Crime crime) {
	}
}
