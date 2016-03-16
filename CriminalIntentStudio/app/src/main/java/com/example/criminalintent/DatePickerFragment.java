package com.example.criminalintent;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

public class DatePickerFragment extends DialogFragment {

	public static final String EXTRA_DATE = "com.example.criminalintent.date";

	private Date date;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		date = (Date) getArguments().getSerializable(EXTRA_DATE);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		View view = getActivity().getLayoutInflater().inflate(
				R.layout.dialog_date, null);
		DatePicker datePicker = (DatePicker) view
				.findViewById(R.id.dialog_date_datePicker);
		datePicker.init(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				new OnDateChangedListener() {

					@Override
					public void onDateChanged(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						date = new GregorianCalendar(year, monthOfYear,
								dayOfMonth).getTime();
						getArguments().putSerializable(EXTRA_DATE, date);
					}

				});

		return new AlertDialog.Builder(getActivity())
				.setView(view)
				.setTitle(R.string.date_picker_title)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								sendResult(Activity.RESULT_OK);
							}

						}).create();
	}

	public static DatePickerFragment newInstance(Date date) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_DATE, date);

		DatePickerFragment fragment = new DatePickerFragment();
		fragment.setArguments(args);
		return fragment;
	}

	public void sendResult(int resultCode) {
		Fragment fragment = getTargetFragment();
		if (fragment != null) {
			Intent intent = new Intent();
			intent.putExtra(EXTRA_DATE, date);

			fragment.onActivityResult(getTargetRequestCode(), resultCode,
					intent);
		}
	}
}
