package com.chcgp.hpad.util.general;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CHCUtils {

	// constructing constants
	public static final int ONLINEDOC_ORG_CONSTRUCTING = 0;
	
	
	
	private static List<Bitmap> allOpenedBitmapsForGallery =
			new ArrayList<Bitmap>();

	public static synchronized void recycleBitmapForGallery(Bitmap bitmap) {
		if (null != bitmap) {
			bitmap.recycle();
			if (allOpenedBitmapsForGallery.contains(allOpenedBitmapsForGallery)) {
				allOpenedBitmapsForGallery.remove(bitmap);
			}
			bitmap = null;
			System.gc();
		}
	}
	
	public static void makeLongToast(
			Context context, 
			CharSequence text) {
		
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
		
	}
	
	
	
	public static void makeShortToast(
			Context context, 
			CharSequence text) {
		
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
		
	}
	
	
	
	public static synchronized void recycleAllBitmapForGallery() {
		Bitmap[] bitmaps = new Bitmap[] {};
		bitmaps = allOpenedBitmapsForGallery.toArray(bitmaps);
		for (int i = 0, size = bitmaps.length; i < size; i++) {
			recycleBitmapForGallery(bitmaps[i]);
			bitmaps[i] = null;
		}
		bitmaps = null;
		System.gc();
	}

	public static synchronized Bitmap getBitmapForGallery(String path) {
		Bitmap bitmap = null;
		BitmapFactory.Options opt = new BitmapFactory.Options();
		// 这个injustdecodebounds很重要，此时VM不会真的分配内存
		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opt);
		// 获取到这个图片的原始宽度和高度
		int picWidth = opt.outWidth;
		int picHeight = opt.outHeight;
		if ((picWidth == -1) || (picHeight == -1))
			return null;
		// 屏的宽度和高度
		int screenWidth = 1024;
		int screenHeight = 768;

		// isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
		opt.inSampleSize = 1;
		// 根据屏的大小和图片大小计算出缩放比例
		if (picWidth > picHeight) {
			if (picWidth > screenWidth)
				opt.inSampleSize = picWidth / screenWidth;
		} else {
			if (picHeight > screenHeight)
				opt.inSampleSize = picHeight / screenHeight;
		}

		// 这次再真正地生成一个有像素的，经过缩放了的bitmap
		opt.inJustDecodeBounds = false;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		try {
			bitmap = BitmapFactory.decodeFile(path, opt);
			allOpenedBitmapsForGallery.add(bitmap);
		} catch (IllegalArgumentException e) {
			Log.e("CHCUtils", "Illegal argument exception.", e);
		} catch (OutOfMemoryError e) {
			Log.e("CHCUtils", "Out of memory error :(", e);
		}
		return bitmap;
	}

	private static List<Bitmap> allOpenedBitmapsForCamera = 
			new ArrayList<Bitmap>();

	public static synchronized void recycleBitmapForCamera(Bitmap bitmap) {
		if (null != bitmap) {
			bitmap.recycle();
			if (allOpenedBitmapsForCamera.contains(allOpenedBitmapsForCamera)) {
				allOpenedBitmapsForCamera.remove(bitmap);
			}
			bitmap = null;
			System.gc();
		}
	}

	public static synchronized void recycleAllBitmapForCamera() {
		Bitmap[] bitmaps = new Bitmap[] {};
		bitmaps = allOpenedBitmapsForCamera.toArray(bitmaps);
		for (int i = 0, size = bitmaps.length; i < size; i++) {
			recycleBitmapForCamera(bitmaps[i]);
			bitmaps[i] = null;
		}
		bitmaps = null;
		System.gc();
	}

	public static Bitmap getBitMapForCamera(String path) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opt);

		// 获取到这个图片的原始宽度和高度
		int picWidth = opt.outWidth;
		int picHeight = opt.outHeight;

		// 获取屏的宽度和高度
		int screenWidth = 100;
		int screenHeight = 100;

		// isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
		opt.inSampleSize = 1;
		// 根据屏的大小和图片大小计算出缩放比例
		if (picWidth > picHeight) {
			if (picWidth > screenWidth)
				opt.inSampleSize = picWidth / screenWidth;
		} else {
			if (picHeight > screenHeight)
				opt.inSampleSize = picHeight / screenHeight;
		}

		// 这次再真正地生成一个有像素的，经过缩放了的bitmap
		opt.inJustDecodeBounds = false;
		opt.inPurgeable = true;
		opt.inInputShareable = true;

		Bitmap temp = BitmapFactory.decodeFile(path, opt);
		int width = temp.getWidth();
		int height = temp.getHeight();
		int newWidth = 67;
		int newHeight = 66;
		// calculate the scale - in this case = 0.4f
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// create a matrix for the manipulation
		Matrix matrix = new Matrix();
		// resize the bit map
		matrix.postScale(scaleWidth, scaleHeight);
		// rotate the Bitmap
		// matrix.postRotate(45);
		// recreate the new Bitmap
		Bitmap resizedBitmap = 
				Bitmap.createBitmap(temp, 0, 0, width, height, matrix, true);
		temp.recycle();
		temp = null;
		System.gc();
		allOpenedBitmapsForCamera.add(resizedBitmap);
		return resizedBitmap;
	}

	/**
	 * this method should only be called at the onDestroy() method of an activity
	 * 
	 * @param view
	 *          a reference to the root View of the current activity
	 */
	public static void unbindDrawables(View view) {
		if (null != view) {
			if (view.getBackground() != null) {
				view.getBackground().setCallback(null);
			}
			if (view instanceof ViewGroup) {
				for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
					unbindDrawables(((ViewGroup) view).getChildAt(i));
				}
				
				// necessary because of AdapterView does not allow removeAllViews,
				// and possibly other ones
				try {
					
					((ViewGroup) view).removeAllViews();
					
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}

	/**
	 * default size bitmap decoding
	 * 
	 * @param f
	 * @return
	 */
	public static Bitmap decodeFile(File f) {
		return decodeFile(f, 500);
	}

	/**
	 * decode the image bitmap according to specified size
	 * 
	 * @param f
	 * @param maxSize
	 * @return
	 */
	public static Bitmap decodeFile(File f, int maxSize) {
		Bitmap b = null;
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;

			FileInputStream fis = new FileInputStream(f);
			BitmapFactory.decodeStream(fis, null, o);
			fis.close();

			int scale = 1;
			if (o.outHeight > maxSize || o.outWidth > maxSize) {
				scale = (int) Math.pow(
						2, 
						(int) Math.round(
								Math.log(maxSize / (double) Math.max(o.outHeight, o.outWidth))
								/ Math.log(0.5)));
			}

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			fis = new FileInputStream(f);
			b = BitmapFactory.decodeStream(fis, null, o2);
			fis.close();
		} catch (IOException e) {
		}
		return b;
	}
	
	/**
	 * unzip zip file from zipFilePath file to targetPath
	 * does not do any editting
	 * @param zipFilePath
	 * @param targetPath
	 */
	public static void unzipFile(String zipFilePath, String targetPath) {
		try {
			File zipFile = new File(zipFilePath);
			InputStream is = new FileInputStream(zipFile);
			ZipInputStream zis = new ZipInputStream(is);
			ZipEntry entry = null;
			System.out.println("正在解压:" + zipFile.getName() + "...");
			while ((entry = zis.getNextEntry()) != null) {
				String zipPath = entry.getName();
				String[] zp = zipPath.split("\\\\");
				String temppath = targetPath;
				for (int i = 0; i < zp.length - 1; ++i) {
					temppath = temppath + "/" + zp[i];
				}
				zipPath = zp[zp.length - 1];
				try {
					if (entry.isDirectory()) {
						File zipFolder = new File(temppath + "/" + zipPath);
						if (!zipFolder.exists()) {
							zipFolder.mkdirs();
						}
					} else {
						File file = new File(temppath + "/" + zipPath);
						if (!file.exists()) {
							File pathDir = new File(temppath);
							pathDir.mkdirs();
							file.createNewFile();
						}
						FileOutputStream fos = new FileOutputStream(file);
						int bread;
						byte[] buffer = new byte[1024];
						while ((bread = zis.read(buffer)) != -1) {
							fos.write(buffer, 0, bread);
							fos.flush();
						}
						fos.close();
					}
				} catch (Exception e) {
					Log.e(CHCUtils.class.getName(), e.getMessage(), e);
					continue;
				}
			}
			zis.close();
			is.close();
			System.out.println("解压完成");
		} catch (Exception e) {
			Log.e(CHCUtils.class.getName(), e.getMessage(), e);
		}
	}

	public static void removeAllViewsIn(Activity a, int viewId) {
		((LinearLayout) a.findViewById(viewId)).removeAllViews();
	}
	
	
	public static android.app.Dialog 
	displayDialog(
			final Context context, 
			final View vv, 
			final String title,
			final String message) {

		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);

		builder.setView(vv);
		builder.setCancelable(false);
		android.app.Dialog dialog = builder.create();
		dialog.show();
		
		return dialog;
	}

	public static ArrayList<File> getFilesContainingUnder(
			String fullPath,
			String containingStr) {
		
		ArrayList<File> list = new ArrayList<File>();
		
		File folder = new File(fullPath);
		if (folder.exists() && folder.isDirectory()) {
			File[] files = folder.listFiles();
			for (File f : files) {
				if (f.getName().contains(containingStr))
					list.add(f);
			}
		}
		
		return list;
	}

	
	
	
	/**
	 * replaces a list of files with another list of files, indicated by names
	 * @param originalList
	 * @param newList
	 * @return
	 */
	public static void replaceFiles(ArrayList<String> originalList,
			ArrayList<String> newList)
		throws UnsupportedOperationException, OperationCanceledException {
		
		if (originalList.size() != newList.size())
			throw new UnsupportedOperationException();
		else {
			String name = null;
			for (int i = 0, size = originalList.size(); i < size; i++) {
				name = originalList.get(i);
				File f = new File(name);
				File newF = new File(newList.get(i));
				if (f.exists() && newF.exists()) {
					if (f.delete()) {
						File temp = new File(name);
						newF.renameTo(temp);
					} else {
						throw new OperationCanceledException("Delete failed");
					}
				} else {
					throw new UnsupportedOperationException("Wrong lists of file names");
				}
			}
		}
	}
	
	
	
	/**
	 * fetch JSONArray from given url, must be called on worker thread
	 * @param url
	 * @return
	 * @throws JSONException
	 * @throws IOException
	 */
	public static JSONArray getRemoteJsonArray(String url)
			throws JSONException, IOException {
		HttpClient client = new DefaultHttpClient();
    HttpGet get = new HttpGet(url);
    HttpResponse response = client.execute(get);
    BufferedReader reader = new BufferedReader(
    		new InputStreamReader(
            response.getEntity().getContent()));
    
    StringBuilder builder = new StringBuilder();
    for (String s = reader.readLine(); s != null; s = reader.readLine()) {
        builder.append(s);
    }
    return new JSONArray(builder.toString());
	}
	
	
	
	/**
	 * get one JSONArray from a JSONArray according to tag and name pair
	 * @param nameTag
	 * @param name
	 * @param jsonArray
	 * @return
	 * @throws JSONException
	 */
	public static JSONArray getCorespondingJsonArray(String nameTag,
			String name, String arrayTag, JSONArray jsonArray) 
			throws JSONException, OperationCanceledException {
		JSONObject org;
		String temp;
		for(int i = 0, size = jsonArray.length(); i < size; i++){ 
      org = (JSONObject) jsonArray.opt(i); 
      temp = org.getString(nameTag);
      if (temp.equals(name))
      	return org.getJSONArray(arrayTag);
    }
		throw new OperationCanceledException("array not found");
	}

}
