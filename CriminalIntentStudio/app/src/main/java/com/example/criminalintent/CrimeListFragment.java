package com.example.criminalintent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class CrimeListFragment extends ListFragment {

	private static final String TAG = "CrimeListFragment";
	public static final int REQUEST_CRIME = 1;
	private boolean subtitleVisible;
	private List<Crime> crimes;
	private Callbacks callbacks;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setHasOptionsMenu(true);

		getActivity().setTitle(R.string.crimes_title);
		crimes = CrimeLab.get(getActivity()).getCrimes();

		setListAdapter(new CrimeAdapter());
		setRetainInstance(true);
		subtitleVisible = false;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Crime crime = ((CrimeAdapter) getListAdapter()).getItem(position);

		callbacks.onCrimeSelected(crime);
	}

	@Override
	public void onResume() {
		super.onResume();
		((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_crime_list, menu);

		MenuItem item = menu.findItem(R.id.menu_item_show_subtitle);
		if (item != null) {
			updateSubtitleMenuItem(item);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_new_crime:
			Crime crime = new Crime();
			CrimeLab.get(getActivity()).getCrimes().add(crime);
			callbacks.onCrimeSelected(crime);
			((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
			return true;
		case R.id.menu_item_show_subtitle:
			subtitleVisible = !subtitleVisible;
			updateSubtitleMenuItem(item);
			updateSubTitle();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, parent, savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
				&& subtitleVisible) {
			updateSubTitle();
		}

		ListView listView = (ListView) view.findViewById(android.R.id.list);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
			listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					mode.getMenuInflater().inflate(
							R.menu.crime_list_item_context, menu);
					return true;
				}

				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
					return false;
				}

				@Override
				public boolean onActionItemClicked(ActionMode mode,
						MenuItem item) {
					switch (item.getItemId()) {
					case R.id.menu_item_delete_crime:
						CrimeAdapter adapter = (CrimeAdapter) getListAdapter();
						ListView listView = getListView();
						CrimeLab crimeLab = CrimeLab.get(getActivity());
						for (int i = adapter.getCount(); i >= 0; i--) {
							if (listView.isItemChecked(i)) {
								crimeLab.remove(adapter.getItem(i));
							}
						}
						mode.finish();
						adapter.notifyDataSetChanged();
						return true;
					default:
						return false;
					}
				}

				@Override
				public void onDestroyActionMode(ActionMode mode) {
				}

				@Override
				public void onItemCheckedStateChanged(ActionMode mode,
						int position, long id, boolean checked) {
				}
			});
		} else {
			registerForContextMenu(listView);
		}
		return view;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo info) {
		getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context,
				menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_delete_crime:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			CrimeAdapter adapter = (CrimeAdapter) getListAdapter();
			CrimeLab.get(getActivity()).remove(adapter.getItem(info.position));
			adapter.notifyDataSetChanged();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		CrimeLab.get(getActivity()).saveCrimes();
	}

	public void updateSubTitle() {
		ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
		if (actionBar.getSubtitle() == null) {
			actionBar.setSubtitle(R.string.subtitle);
		} else {
			actionBar.setSubtitle(null);
		}
	}

	public void updateUI() {
		((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
	}

	public void updateSubtitleMenuItem(MenuItem item) {
		item.setTitle(subtitleVisible ? R.string.hide_subtitle
				: R.string.show_subtitle);
	}

	private class CrimeAdapter extends ArrayAdapter<Crime> {

		public CrimeAdapter() {
			super(getActivity(), 0, crimes);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.list_itme_crime, parent, false);
			}
			Crime crime = getItem(position);

			TextView titleTextView = (TextView) convertView
					.findViewById(R.id.crime_list_item_titleTextView);
			TextView dateTextView = (TextView) convertView
					.findViewById(R.id.crime_list_item_dateTextView);
			CheckBox solvedCheckBox = (CheckBox) convertView
					.findViewById(R.id.crime_list_item_solvedCheckBox);

			titleTextView.setText(crime.getmTitle());
			dateTextView.setText(crime.getmDate().toString());
			solvedCheckBox.setChecked(crime.ismSolved());
			return convertView;
		}
	}

	public interface Callbacks {
		void onCrimeSelected(Crime crime);
	}
}
