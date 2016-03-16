package com.example.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {

	public static final String EXTRA_CRIME_ID = "com.example.criminalintent.crime_id";

	private static final String TAG = "CrimeFragment";
	private static final String DIALOG_DATE = "date";
	private static final String DIALOG_IMAGE = "image";
	private static final int REQUEST_DATE = 0;
	private static final int REQUEST_PHOTO = 1;
	private static final int REQUEST_CONTACT = 2;

	private Crime mCrime;

	private EditText mTitleField;
	private Button mDateButton;
	private CheckBox mSolvedCheckBox;
	private ImageButton cameraButton;
	private ImageView photoView;
	private Button reportButton;
	private Button suspectButton;
	private Callbacks callbacks;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mCrime = CrimeLab.get(getActivity()).getCrime(
				(UUID) getArguments().getSerializable(EXTRA_CRIME_ID));

		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceSate) {
		View view = inflater.inflate(R.layout.fragment_crime, parent, false);

		mTitleField = (EditText) view.findViewById(R.id.crime_title);
		mTitleField.setText(mCrime.getmTitle());
		mTitleField.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mCrime.setmTitle(s.toString());
				callbacks.onCrimeUpdated(mCrime);
			}

		});

		mDateButton = (Button) view.findViewById(R.id.crime_date);
		updateDate();
		mDateButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				DatePickerFragment fragment = DatePickerFragment
						.newInstance(mCrime.getmDate());
				fragment.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
				fragment.show(fm, DIALOG_DATE);
			}
		});

		mSolvedCheckBox = (CheckBox) view.findViewById(R.id.crime_solved);
		mSolvedCheckBox.setChecked(mCrime.ismSolved());
		mSolvedCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						mCrime.setmSolved(isChecked);
						callbacks.onCrimeUpdated(mCrime);
					}
				});

		cameraButton = (ImageButton) view.findViewById(R.id.crime_imageButton);
		cameraButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						CrimeCameraActivity.class);
				startActivityForResult(intent, REQUEST_PHOTO);
			}
		});

		photoView = (ImageView) view.findViewById(R.id.crime_imageView);
		photoView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Photo photo = mCrime.getPhoto();
				if (photo != null) {
					String path = getActivity().getFileStreamPath(
							photo.getFileName()).getAbsolutePath();
					FragmentManager fm = getActivity()
							.getSupportFragmentManager();
					ImageFragment.newInstance(path).show(fm, DIALOG_IMAGE);
				}
			}
		});

		reportButton = (Button) view.findViewById(R.id.crime_reportButton);
		reportButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
				intent.putExtra(Intent.EXTRA_SUBJECT,
						getString(R.string.crime_report_subject));
				intent = Intent.createChooser(intent,
						getString(R.string.send_report));
				startActivity(intent);
			}
		});

		suspectButton = (Button) view.findViewById(R.id.crime_suspectButton);
		suspectButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_PICK,
						ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(intent, REQUEST_CONTACT);
			}
		});
		if (mCrime.getSuspect() != null) {
			suspectButton.setText(mCrime.getSuspect());
		}
		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUEST_DATE:
				Date date = (Date) intent
						.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
				mCrime.setmDate(date);
				updateDate();
				break;
			case REQUEST_PHOTO:
				String file = (String) intent
						.getSerializableExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
				if (file != null) {
					mCrime.setPhoto(new Photo(file));
					showPhoto();
				}
				break;
			case REQUEST_CONTACT:
				Uri contactUri = intent.getData();
				String[] fields = new String[] { ContactsContract.Contacts.DISPLAY_NAME, };
				Cursor cursor = getActivity().getContentResolver().query(
						contactUri, fields, null, null, null);
				if (cursor.getCount() > 0) {
					cursor.moveToFirst();
					String suspect = cursor.getString(0);
					mCrime.setSuspect(suspect);
					suspectButton.setText(suspect);
				}
				cursor.close();
				break;
			}
			callbacks.onCrimeUpdated(mCrime);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		CrimeLab.get(getActivity()).saveCrimes();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (NavUtils.getParentActivityName(getActivity()) != null) {
				NavUtils.navigateUpFromSameTask(getActivity());
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		showPhoto();
	}

	@Override
	public void onStop() {
		super.onStop();
		PictureUtils.cleanImageView(photoView);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		callbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		callbacks = null;
	}

	private void showPhoto() {
		Photo photo = mCrime.getPhoto();
		BitmapDrawable drawable = null;

		if (photo != null) {
			String path = getActivity().getFileStreamPath(photo.getFileName())
					.getAbsolutePath();
			drawable = PictureUtils.getScaledDrawable(getActivity(), path);
		}
		photoView.setImageDrawable(drawable);
	}

	private void updateDate() {
		mDateButton.setText(mCrime.getmDate().toString());
	}

	private String getCrimeReport() {
		String solvedStr = getString(mCrime.ismSolved() ? R.string.crime_report_solved
				: R.string.crime_report_unsolved);
		String dateStr = DateFormat.format("EEE, MMM dd", mCrime.getmDate())
				.toString();
		String suspect = mCrime.getSuspect();
		suspect = suspect == null ? getString(R.string.crime_report_no_suspect)
				: getString(R.string.crime_report_suspect, suspect);
		return getString(R.string.crime_report, mCrime.getmTitle(), dateStr,
				solvedStr, suspect);
	}

	public static CrimeFragment newInstant(UUID id) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_CRIME_ID, id);

		CrimeFragment fragment = new CrimeFragment();
		fragment.setArguments(args);
		return fragment;
	}

	public void returnResult() {
		getActivity().setResult(Activity.RESULT_OK, null);
	}

	public interface Callbacks {
		void onCrimeUpdated(Crime crime);
	}
}
