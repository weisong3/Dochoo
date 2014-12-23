package com.chcgp.hpad.util.download;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import com.chcgp.hpad.util.general.CHCGeneralUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageDownloader {
	final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

  final int cacheSize = maxMemory / 4; // in KB

	private static final String LAST_CLEAR_CACHE_TIME_FILE_NAME = "ImageDownloaderLastCacheTime";

	private static final String LAST_CLEAR_CACHE_TIME_TAG = "LastClearTime";

	private static final long INTERVAL_CLEAR_CACHE = 48 * 3600 * 1000; // 12 hours
	
	private static ExecutorService executor = Executors.newSingleThreadExecutor();

	private static final ImageDownloader instance = new ImageDownloader();

	private static final int BUFFER_SIZE = 1024 * 100;

	private static final int MAX_PIXEL = 256;

	LruCache<String, Bitmap> imageCache;
	
	public interface Callback {
		void onImageLoaded(Bitmap bitmap);
	}

	private ImageDownloader() {
		imageCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight() / 1024; // in KB
			}
		};
	}

	public static ImageDownloader getInstance() {
		return instance;
	}
	
	public void downloadWithCallback(final Context context, String url, final int maxMinSize, final Callback callback) {
		// Caching code right here
		String filename = String.valueOf(url.hashCode());

		// Is the bitmap in our memory cache?
		Bitmap bitmap = imageCache.get(filename);

		if (bitmap == null) {

			File f = new File(getCacheDirectory(context),
					filename);
			if (f.exists() && f.isFile()) {
				bitmap = BitmapFactory.decodeFile(f.getPath());
		
				if (bitmap != null) {
					imageCache.put(filename, bitmap);
				}
			}
		}

		// No? download it
		if (bitmap == null) {
			AsyncTask<String, Void, Bitmap> task = new AsyncTask<String, Void, Bitmap>() {

				@Override
				protected Bitmap doInBackground(String... params) {
					return downloadAndDecodeBitmap(params[0], maxMinSize, getCacheDirectory(context).getAbsolutePath());
				}

				@Override
				protected void onPostExecute(Bitmap result) {
					if (result != null) callback.onImageLoaded(result);
				}
				
			};
			try {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
				} else {
					task.execute(url);
				}
			} catch (Exception e) {
			}
		} else {
			// Yes? set the image
			callback.onImageLoaded(bitmap);
		}

		bitmap = null;
	}
	
	public void download(String url, ImageView imageView, Resources res, int resId) {
		download(url, imageView, res, drawableToBitmap(res.getDrawable(resId)));
	}
	
	public void download(String url, ImageView imageView, Resources res, Bitmap defaultBitmap) {
		if (cancelPotentialDownload(url, imageView)) {

			// Caching code right here
			String filename = String.valueOf(url.hashCode());

			// Is the bitmap in our memory cache?
			Bitmap bitmap = imageCache.get(filename);

			if (bitmap == null) {

				File f = new File(getCacheDirectory(imageView.getContext()),
						filename);
				if (f.exists() && f.isFile()) {
					bitmap = BitmapFactory.decodeFile(f.getPath());
	
					if (bitmap != null) {
						imageCache.put(filename, bitmap);
					}
				}
			}

			// No? download it
			if (bitmap == null) {
				BitmapDownloaderTask task = new BitmapDownloaderTask(imageView, MAX_PIXEL, MAX_PIXEL);
				DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task, res, defaultBitmap);
				imageView.setImageDrawable(downloadedDrawable);

				try {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
					} else {
						task.execute(url);
					}
				} catch (Exception e) {
					cancelPotentialDownload(url, imageView);
				}
			} else {
				// Yes? set the image
				imageView.setImageBitmap(bitmap);
			}

			bitmap = null;
		}
	}
	
//	public void download(String url, ImageView imageView, Resources res, int defaultRes) {
//		if (url == null) throw new NullPointerException("url is null");
//
//		if (cancelPotentialDownload(url, imageView)) {
//
//			// Caching code right here
//			String filename = String.valueOf(url.hashCode());
//
//			// Is the bitmap in our memory cache?
//			Bitmap bitmap = imageCache.get(filename);
//
//			if (bitmap == null) {
//
//				File f = new File(getCacheDirectory(imageView.getContext()),
//						filename);
//				if (f.exists() && f.isFile()) {
//					bitmap = BitmapFactory.decodeFile(f.getPath());
//
//					if (bitmap != null) {
//						imageCache.put(filename, bitmap);
//					}
//				}
//
//			}
//
//			// No? download it
//			if (bitmap == null) {
//				BitmapDownloaderTask task = new BitmapDownloaderTask(imageView, MAX_PIXEL, MAX_PIXEL);
//				Drawable defaultDrawable = imageView.getDrawable();
//				DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task, res);
//				imageView.setImageDrawable(downloadedDrawable);
//
//				try {
//					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//						task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
//					} else {
//						task.execute(url);
//					}
//				} catch (Exception e) {
//					cancelPotentialDownload(url, imageView);
//				}
//			} else {
//				// Yes? set the image
//				imageView.setImageBitmap(bitmap);
//			}
//
//			bitmap = null;
//		}
//	}
	
	/**
	 * @param url
	 * @param imageView
	 * @param width
	 * @param height
	 * @param res
	 * @param resId
	 */
	public void download(String url, ImageView imageView, int width,
			int height, Resources res, int resId) {
		if (url == null)
			return;
		download(url, imageView, width, height, res,
				BitmapFactory.decodeResource(res, resId));
	}

	public void download(String url, ImageView imageView, int width,
			int height, Resources res, Bitmap defaultRes) {
		if (cancelPotentialDownload(url, imageView)) {

			// Caching code right here
			String filename = String.valueOf(url.hashCode());

			// Is the bitmap in our memory cache?
			Bitmap bitmap = imageCache.get(filename);

			// No? is it in disk cache?
			if (bitmap == null) {

				File f = new File(getCacheDirectory(imageView.getContext()),
						filename);
				
				if (f.exists() && f.isFile()) {
					bitmap = CHCGeneralUtil.decodeFileMinimumSize(f, Math.min(width, height));
	
					if (bitmap != null) {
						imageCache.put(filename, bitmap);
					}
				}

			}

			if (bitmap == null) {
			    // No? download it
				BitmapDownloaderTask task = new BitmapDownloaderTask(imageView,
						width, height);
				DownloadedDrawable downloadedDrawable = new DownloadedDrawable(
						task, res, defaultRes);
				imageView.setImageDrawable(downloadedDrawable);

				try {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
					} else {
						task.execute(url);
					}
				} catch (Exception e) {
					cancelPotentialDownload(url, imageView);
				}
			} else {
				// Yes? set the image
				imageView.setImageBitmap(bitmap);
			}

			bitmap = null;
		}
	}

	// cancel a download (internal only)
	private static boolean cancelPotentialDownload(String url,
			ImageView imageView) {
		BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

		if (bitmapDownloaderTask != null) {
			String bitmapUrl = bitmapDownloaderTask.url;
			if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
				bitmapDownloaderTask.cancel(true);
			} else {
				// The same URL is already being downloaded.
				return false;
			}
		}
		return true;
	}

	// gets an existing download if one exists for the imageview
	private static BitmapDownloaderTask getBitmapDownloaderTask(
			ImageView imageView) {
		if (imageView != null) {
			Drawable drawable = imageView.getDrawable();
			if (drawable instanceof DownloadedDrawable) {
				DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
				return downloadedDrawable.getBitmapDownloaderTask();
			}
		}
		return null;
	}

	public static void checkCacheExpiration(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				LAST_CLEAR_CACHE_TIME_FILE_NAME, Context.MODE_PRIVATE);
		long last = sp.getLong(LAST_CLEAR_CACHE_TIME_TAG, 0);
		long current = System.currentTimeMillis();
		if (INTERVAL_CLEAR_CACHE < current - last) {
			// do clear and set time
			File[] files = getCacheDirectory(context).listFiles();
            if (files != null) {
                for (File f : files) {
                    f.delete();
                }
            }
			SharedPreferences.Editor editor = sp.edit();
			editor.putLong(LAST_CLEAR_CACHE_TIME_TAG, current);
			editor.commit();
            getInstance().imageCache.evictAll();
		}
		// if within interval quit
	}

	// our caching functions
	// Find the dir to save cached images
	private static File getCacheDirectory(Context context) {
		String sdState = android.os.Environment.getExternalStorageState();
		File cacheDir;

		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File sdDir = android.os.Environment.getExternalStorageDirectory();

			// TODO : Change your diretcory here
			cacheDir = new File(sdDir, "/data/chc/cache");
		} else
			cacheDir = context.getCacheDir();

		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
		return cacheDir;
	}

	private void writeFile(final Bitmap bmp, final File f) {

		try {
			executor.submit(new Runnable() {
				
				@Override
				public void run() {
	
					FileOutputStream out = null;
	
					try {
						out = new FileOutputStream(f);
						bmp.compress(Bitmap.CompressFormat.PNG, 80, out);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							if (out != null)
								out.close();
						} catch (Exception ex) {
						}
					}
					
				}
			});
		} catch (Exception e) {}
		
	}

	// /////////////////////

	// download asynctask
	public class BitmapDownloaderTask extends AsyncTaskRelaxed<String, Void, Bitmap> {
		private String url;
		private final WeakReference<ImageView> imageViewReference;
		private int width;
		private int height;

		public BitmapDownloaderTask(ImageView imageView, int width, int height) {
			this.width = width;
			this.height = height;
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		@Override
		// Actual download method, run in the task thread
		protected Bitmap doInBackground(String... params) {
			// params comes from the execute() call: params[0] is the url.
			url = params[0];
            ImageView imageView = imageViewReference.get();
            if (imageView == null) return null;
			return downloadAndDecodeBitmap(params[0], Math.min(width, height), getCacheDirectory(imageView.getContext()).getAbsolutePath());
		}

		@Override
		// Once the image is downloaded, associates it to the imageView
		protected void onPostExecute(Bitmap bitmap) {
            ImageView imageView = imageViewReference.get();
            BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
            // Change bitmap only if this process is still associated with
            // it
            if (imageView != null && this == bitmapDownloaderTask && !isCancelled()
                    && bitmap != null) {
                imageView.setImageBitmap(bitmap);

                String filename = String.valueOf(url.hashCode());
                // cache the image
                imageCache.put(filename, bitmap);
            }
		}
	}

	// no longer of use since the removal of width and height parameters
//	private Bitmap decode(Bitmap image, int width, int height) {
//		return Bitmap.createScaledBitmap(image, width, height, false);
//	}

	static class DownloadedDrawable extends BitmapDrawable {
		private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

		public DownloadedDrawable(BitmapDownloaderTask task,
				Resources res, Bitmap defaultRes) {
			super(res, defaultRes);
			bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>(
					task);
		}
		
		@Deprecated
		public DownloadedDrawable(BitmapDownloaderTask task, Resources res) {
			super(res);
			bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>(
					task);
		}

		public BitmapDownloaderTask getBitmapDownloaderTask() {
			return bitmapDownloaderTaskReference.get();
		}
	}

	// the actual download code
	static Bitmap downloadAndDecodeBitmap(String url, int maxMinSize, String downloadPath) {
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
				HttpVersion.HTTP_1_1);
		HttpClient client = new DefaultHttpClient(params);
		final HttpGet getRequest = new HttpGet(url);

		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w("ImageDownloader", "Error " + statusCode
						+ " while retrieving bitmap from " + url);
				return null;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = new BufferedInputStream(entity.getContent());
					String filename = String.valueOf(url.hashCode());
					File f = new File(downloadPath, filename);
					if (f.exists()) f.delete();
					BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
					byte[] buffer = new byte[BUFFER_SIZE];
					int read = 0;
					while ((read = inputStream.read(buffer)) != -1) {
						bos.write(buffer, 0, read);
					}
					bos.flush();
					CHCGeneralUtil.closeQuietly(bos);
					
					return CHCGeneralUtil.decodeFileMinimumSize(f, maxMinSize);
				} finally {
					CHCGeneralUtil.closeQuietly(inputStream);
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			// Could provide a more explicit error message for IOException or
			// IllegalStateException
			getRequest.abort();
			Log.w("ImageDownloader", "Error while retrieving bitmap from "
					+ url + e.toString());
		} finally {
			if (client != null) {
				// client.close();
			}
		}
		return null;
	}
	
	public static void shutdown() {
		executor.shutdown();
	}


    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap;
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        if (width > 0 && height > 0) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        } else {
            bitmap = null;
        }

        return bitmap;
    }

    public boolean checkExistence(String url){
        String filename = String.valueOf(url.hashCode());
        // Is the bitmap in our memory cache?
        Bitmap bitmap = imageCache.get(filename);

        // No? is it in disk cache?
        if (bitmap == null) {
            return false;
        }
        return true;
    }
}
