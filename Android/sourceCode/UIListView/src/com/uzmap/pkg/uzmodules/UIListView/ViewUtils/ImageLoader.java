package com.uzmap.pkg.uzmodules.UIListView.ViewUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.uzmap.pkg.uzmodules.UIListView.widget.CircleImageView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;

/**
 * 
 * @author Robot
 * 
 */

public class ImageLoader {

	public static final String TAG = "lyh";

	/**
	 * the max size of the cache
	 */
	public static int MAX_CACHE_SIZE = (int) Runtime.getRuntime().maxMemory() / 8;

	/**
	 * the path of the cache
	 */
	public static String CACHE_PATH = Environment.getExternalStorageDirectory()
			+ "/.image";

	/**
	 * the number of concurrent threads
	 */
	public static int THREAD_NUMS = 3;

	/**
	 * read buffer size
	 */
	public static final int BUF_SIZE = 1024;

	/**
	 * cached image format
	 */
	public static final String CACHED_IMAGE_FORMAT = ".jpg";

	/**
	 * sample flag (avoid OOM, just applies to local file)
	 */
	public static final boolean SAMPLED_FLAG = false;
	
	/**
	 * placeholder bitmap
	 */
	public Bitmap placeHolderBmp;
	
	public void setPlaceHolderBitmap(Bitmap bmp){
		this.placeHolderBmp = bmp;
	}
	
	
	private static LruCache<String, Bitmap> caches = new LruCache<String, Bitmap>(
			MAX_CACHE_SIZE) {

		@Override
		protected void entryRemoved(boolean evicted, String key,
				Bitmap oldValue, Bitmap newValue) {
			Log.i(TAG, "Entry Removed");
		}

		@Override
		protected int sizeOf(String key, Bitmap value) {
			int size = value.getRowBytes() * value.getHeight();
			return size;
		}

	};
	private static ExecutorService mThreadPool = Executors
			.newFixedThreadPool(THREAD_NUMS);

	public void load(View view, final String path) {

		// load from memory at first
		Bitmap cacheBitmap = caches.get(md5(path));

		if (cacheBitmap == null) {
			ImageDownTask task = new ImageDownTask(view, path);
			mThreadPool.execute(task);
		} else {
			setImage(view, cacheBitmap, 0);
		}

	}

	public void load(View view, final String path,
			OnLoadProgressListener progressListener) {

		// load from memory at first
		Bitmap cacheBitmap = caches.get(md5(path));

		if (cacheBitmap == null) {
			ImageDownTask task = new ImageDownTask(view, path, progressListener);
			mThreadPool.execute(task);
		} else {
			setImage(view, cacheBitmap, 0);
		}

	}

	@SuppressWarnings("deprecation")
	public void load(View view, final String path, int corner, String format) {
		
		if(placeHolderBmp != null){
			if (view instanceof CircleImageView) {
				((CircleImageView) view).setWillRoundedBitmap(placeHolderBmp, corner);
			} else if (view instanceof ImageView) {
				((ImageView) view).setImageBitmap(placeHolderBmp);
			} else {
				view.setBackgroundDrawable(new BitmapDrawable(placeHolderBmp));
			}
		}
		
		// load from memory
		Bitmap cacheBitmap = caches.get(md5(path));

		if (cacheBitmap == null) {
			
			String imageFormat;
			
			if(path.endsWith("png")){
				imageFormat = ".png";
			} else {
				imageFormat = "jpg";
			}
			ImageDownTask task = new ImageDownTask(view, path, corner, imageFormat);
			mThreadPool.execute(task);
		} else {
			setImage(view, cacheBitmap, corner);
		}
	}

	public Bitmap getImageFromNet(String path) {

		URL url;
		try {
			url = new URL(path);
			URLConnection urlConnection = url.openConnection();
			Bitmap bitmap = BitmapFactory.decodeStream(urlConnection
					.getInputStream());

			return bitmap;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	public Bitmap getImageFromNetWithProgress(String path,
			OnLoadProgressListener progressListener) {

		URL url;
		try {
			url = new URL(path);
			URLConnection urlConnection = url.openConnection();
			int totalCount = urlConnection.getInputStream().available();

			InputStream inputStream = urlConnection.getInputStream();

			byte[] array = readStream(inputStream, totalCount, progressListener);
			Bitmap bitmap = BitmapFactory.decodeByteArray(array, 0,
					array.length);

			return bitmap;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public Bitmap getBitmapFromLocal(String path) {

		if (SAMPLED_FLAG) {
			return getSampledBitmap(path);
		}

		File file = new File(path);
		try {
			FileInputStream input = new FileInputStream(file);
			Bitmap bitmap = BitmapFactory.decodeStream(input);
			return bitmap;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String md5(String string) {

		if (TextUtils.isEmpty(string)) {
			return null;
		}

		byte[] hash;

		try {
			hash = MessageDigest.getInstance("MD5").digest(
					string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString();
	}

	public void saveBitmap(Bitmap bitmap, String path, String fileName, String format) {

		if (bitmap == null || TextUtils.isEmpty(path)
				|| TextUtils.isEmpty(fileName)) {
			return;
		}

		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}

		try {
			FileOutputStream output = new FileOutputStream(new File(file,
					fileName));
			if (format.endsWith(".png")) {
				bitmap.compress(CompressFormat.PNG, 100, output);
			} else {
				bitmap.compress(CompressFormat.JPEG, 100, output);
			}
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	class ImageDownTask implements Runnable {

		private Bitmap mBitmap;
		private String url;
		private View view;
		private int corner;
		
		private String format;

		private OnLoadProgressListener mProgressListener;

		public ImageDownTask(View view, String url) {
			this.url = url;
			this.view = view;
		}

		public ImageDownTask(View view, String url,
				OnLoadProgressListener mProgressListener) {
			this(view, url);
			this.mProgressListener = mProgressListener;
		}

		public ImageDownTask(View view, String url, int corner) {
			this(view, url);
			this.corner = corner;

		}
		
		public ImageDownTask(View view, String url, int corner, String format){
			this(view, url, corner);
			this.format = format;
		}

		public ImageDownTask(View view, String url,
				OnLoadProgressListener mProgressListener, int corner) {
			this(view, url, mProgressListener);
			this.corner = corner;
		}

		@Override
		public void run() {

			// load from disk
			File file = new File(url);
			if (file.exists()) {
				mBitmap = getBitmapFromLocal(url);
				caches.put(md5(url), mBitmap);
				Bitmap bitmap = caches.get(md5(url));
				setImage(view, bitmap, corner);

				return;
			}

			mBitmap = getBitmapFromLocal(CACHE_PATH + "/" + md5(url)
					+ format);

			Log.i(TAG, " -- cache path -- : " + CACHE_PATH);

			if (mBitmap != null) {

				caches.put(md5(url), mBitmap);
				setImage(view, caches.get(md5(url)), corner);

			} else {
				if (mProgressListener != null) {
					mBitmap = getImageFromNetWithProgress(this.url,
							mProgressListener);
				} else {
					mBitmap = getImageFromNet(this.url);
				}

				if (mBitmap == null) {
					return;
				}
				caches.put(md5(url), mBitmap);
				saveBitmap(mBitmap, CACHE_PATH, md5(url) + this.format, this.format);
				setImage(view, caches.get(md5(url)), corner);

				if (mOnLoadCompleteListener != null)
					mOnLoadCompleteListener.onLoadComplete();
			}

		}
	}

	@SuppressWarnings("deprecation")
	public void setImage(final View view, final Bitmap bitmap, final int corner) {
		
		if(bitmap == null){
			return;
		}

		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {

				if (view instanceof CircleImageView) {
					((CircleImageView) view).setWillRoundedBitmap(bitmap, corner);
				} else if (view instanceof ImageView) {
					((ImageView) view).setImageBitmap(bitmap);
				} else {
					view.setBackgroundDrawable(new BitmapDrawable(bitmap));
				}

			}

		});
	}

	/**
	 * <P>
	 * Generally only to the local image sampling read, local image is compared
	 * commonly large, some even more than 5M, in order to prevent the OOM need
	 * sampled
	 * </P>
	 * 
	 * @param localFile
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public Bitmap getSampledBitmap(String localFile) {

		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(localFile, newOpts);

		if (bitmap == null) {
			return null;
		}

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 800f;//
		float ww = 480f;//
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;

		newOpts.inPreferredConfig = Config.ARGB_8888;
		newOpts.inPurgeable = true;
		newOpts.inInputShareable = true;

		bitmap = BitmapFactory.decodeFile(localFile, newOpts);

		return bitmap;
	}

	public interface OnLoadProgressListener {
		public void onLoadProgress(int progress);
	}

	public byte[] readStream(InputStream in, int totalCount,
			OnLoadProgressListener mProgressListener) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		byte[] buffer = new byte[BUF_SIZE];
		int len = -1;
		int curByteCount = 0;
		while ((len = in.read(buffer)) != -1) {
			outputStream.write(buffer, 0, len);
			curByteCount += len;

			float progressRange = (float) curByteCount / (float) totalCount
					* 100f;
			mProgressListener.onLoadProgress((int) progressRange);
		}
		outputStream.close();
		in.close();
		return outputStream.toByteArray();
	}

	public interface OnLoadCompleteListener {
		public void onLoadComplete();
	}

	public OnLoadCompleteListener mOnLoadCompleteListener;

	public void setOnLoadCompleteListener(OnLoadCompleteListener listener) {
		this.mOnLoadCompleteListener = listener;
	}

	/**
	 * cancel all downloading task
	 */
	public void cancelTasks() {
		mThreadPool.shutdown();
	}
}
