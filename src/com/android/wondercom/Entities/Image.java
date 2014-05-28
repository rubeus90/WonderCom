package com.android.wondercom.Entities;

import java.io.IOException;
import java.io.InputStream;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

/**
 *  Image entity
 */
public class Image {
	public static final String TAG = "Image";
	private Context mContext;
	private Uri mUri;
	
	public Image(Context context, Uri uri){
		mContext = context;
		mUri = uri;
	}
	
	//Retrieve the bitmap from its Uri (in sample size calculated)
	public Bitmap getBitmapFromUri() {
		Log.v(TAG, "Decode image to bitmap");
		Bitmap myBitmap = decodeSampledBitmapFromUrl(450, 450);
		return myBitmap;
	}
	
	//Calculate the size of the image to be loaded (prevent memory leak due to large bitmap loading)
	public int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	//Load the bitmap in the size calculated
	public Bitmap decodeSampledBitmapFromUrl(int reqWidth, int reqHeight) {
		InputStream input;
	    try {
	        input = mContext.getContentResolver().openInputStream(mUri);
	        
	        // First decode with inJustDecodeBounds=true to check dimensions
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
	        BitmapFactory.decodeStream(input, null, options);

			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, reqWidth,
					reqHeight);
			input.close();

			// Decode bitmap with inSampleSize set
			input = mContext.getContentResolver().openInputStream(mUri);			
			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeStream(input, null, options);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "Failed to decode the bitmap with the size selected");
		}
		return null;
	}
}
