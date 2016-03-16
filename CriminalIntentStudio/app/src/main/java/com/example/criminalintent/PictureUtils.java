package com.example.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.widget.ImageView;

public class PictureUtils {

	@SuppressWarnings("deprecation")
	public static BitmapDrawable getScaledDrawable(Activity activity,
			String path) {
		Display display = activity.getWindowManager().getDefaultDisplay();
		int destWidth = display.getWidth();
		int destHeight = display.getHeight();

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		int srcWidth = options.outWidth;
		int srcHeight = options.outHeight;
		int inSampleSize = 1;
		if (srcWidth > destWidth || srcHeight > destHeight) {
			inSampleSize = Math.round(srcWidth > srcHeight ? srcWidth
					/ srcHeight : srcHeight / srcWidth);
		}

		options = new BitmapFactory.Options();
		options.inSampleSize = inSampleSize;

		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		return new BitmapDrawable(activity.getResources(), bitmap);
	}

	public static void cleanImageView(ImageView view) {
		Drawable drawable = view.getDrawable();
		if (drawable instanceof BitmapDrawable) {
			Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
			if (bitmap != null) {
				bitmap.recycle();
				view.setImageDrawable(null);
			}
		}
	}
}
