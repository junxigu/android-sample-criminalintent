package com.example.criminalintent;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.Window;
import android.view.WindowManager;

public class CrimeCameraActivity extends SingleFragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		super.onStart();

		getSupportActionBar().hide();
	}

	@Override
	protected Fragment createFragment() {
		return new CrimeCameraFragment();
	}

}
