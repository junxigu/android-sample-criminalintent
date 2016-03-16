package com.example.criminalintent;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageFragment extends DialogFragment {

	private static final String EXTRA_IMAGE_PATH = "com.example.criminalintent.image_path";

	private String file;
	private ImageView photoView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		photoView = new ImageView(getActivity());
		file = (String) getArguments().getSerializable(EXTRA_IMAGE_PATH);
		photoView.setImageDrawable(PictureUtils.getScaledDrawable(
				getActivity(), file));
		return photoView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		PictureUtils.cleanImageView(photoView);
	}

	public static ImageFragment newInstance(String file) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(EXTRA_IMAGE_PATH, file);

		ImageFragment fragment = new ImageFragment();
		fragment.setArguments(bundle);
		fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
		return fragment;
	}
}
