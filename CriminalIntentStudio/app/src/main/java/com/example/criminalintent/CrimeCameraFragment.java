package com.example.criminalintent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class CrimeCameraFragment extends Fragment {

	public final static String EXTRA_PHOTO_FILENAME = "com.example.criminalintent.photo_filename";

	private final static String TAG = "CrimeCameraFragment";
	private Camera camera;
	private SurfaceView surfaceView;
	private View progressContainer;

	private Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {

		@Override
		public void onShutter() {
			progressContainer.setVisibility(View.VISIBLE);
		}
	};

	private Camera.PictureCallback picktureCallback = new Camera.PictureCallback() {

		@SuppressLint("NewApi")
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			String file = UUID.randomUUID().toString() + ".jpg";
			try (FileOutputStream os = getActivity().openFileOutput(file,
					Context.MODE_PRIVATE)) {
				os.write(data);

				Log.i(TAG, "JPEG saved at " + file);

				Intent intent = new Intent();
				intent.putExtra(EXTRA_PHOTO_FILENAME, file);
				getActivity().setResult(Activity.RESULT_OK, intent);
			} catch (Exception e) {
				getActivity().setResult(Activity.RESULT_CANCELED);
				Log.e(TAG, "Error writting to " + file, e);
			}
			getActivity().finish();
		}
	};

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_crime_camera, parent,
				false);

		progressContainer = view
				.findViewById(R.id.crime_camera_progressContainer);
		progressContainer.setVisibility(View.INVISIBLE);

		Button btn = (Button) view
				.findViewById(R.id.crime_camera_takePictureButton);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (camera != null) {
					camera.takePicture(shutterCallback, null, picktureCallback);
				}
			}
		});

		PackageManager pm = getActivity().getPackageManager();
		boolean hasACamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
				|| pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)
				|| Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD
				|| Camera.getNumberOfCameras() > 0;
		btn.setEnabled(hasACamera);

		surfaceView = (SurfaceView) view
				.findViewById(R.id.crime_camera_surfaceView);
		SurfaceHolder holder = surfaceView.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.addCallback(new SurfaceHolder.Callback() {
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				if (camera != null) {
					camera.stopPreview();
				}
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				if (camera != null) {
					try {
						camera.setPreviewDisplay(holder);
					} catch (IOException e) {
						Log.e(TAG, "Error setting up preview display", e);
					}
				}
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				if (camera != null) {
					Camera.Parameters parameters = camera.getParameters();
					Size size = getBestSupportdSize(
							parameters.getSupportedPreviewSizes(), width,
							height);
					parameters.setPreviewSize(size.width, size.height);
					size = getBestSupportdSize(
							parameters.getSupportedPictureSizes(), width,
							height);
					parameters.setPictureSize(size.width, size.height);
					camera.setParameters(parameters);
					try {
						camera.startPreview();
					} catch (Exception e) {
						camera.release();
						camera = null;
						Log.e(TAG, "Error starting preview");
					}
				}
			}
		});
		return view;
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	public void onResume() {
		super.onResume();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			camera = Camera.open(0);
		} else {
			camera = Camera.open();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (camera != null) {
			camera.release();
			camera = null;
		}
	}

	private void sendResult() {

	}

	private Size getBestSupportdSize(List<Size> sizes, int w, int h) {
		Size bestSize = sizes.get(0);
		int largestArea = bestSize.width * bestSize.height;
		for (Size size : sizes) {
			int area = size.height * size.width;
			if (area > largestArea) {
				bestSize = size;
				largestArea = area;
			}
		}
		return bestSize;
	}
}
