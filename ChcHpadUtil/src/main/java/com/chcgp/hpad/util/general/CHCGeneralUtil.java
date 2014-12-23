package com.chcgp.hpad.util.general;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;

import org.springframework.util.StringUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CHCGeneralUtil {
	private static final String LOGIN_PACKAGE_NAME = "com.chcgp.hpad.login";
	private static final String CAMERA_ACTIVITY_PACKAGENAME = "com.chcgp.hpad.camera";
	private static final String TAG = "CHCGeneralUtil";
	public static String QUICKOFFICE_PACKAGENAME = "com.qo.android.am3";
	public static String QUICKOFFICE_CLASSNAME = "com.qo.android.quickoffice.QuickofficeDispatcher";
	public static String ADOBEREADER_PACKAGENAME = "com.adobe.reader";
	public static String ADOBEREADER_CLASSNAME = "com.adobe.reader.AdobeReader";

	/**
	 * use this method for general unzipping!
	 * 
	 * @param targetPath
	 * @param zipFilePath
	 * @param extension
	 *          if null, general zip
	 */
	public static void unzipFile(String targetPath, String zipFilePath, String extension) {
		try {
			File zipFile = new File(zipFilePath);
			InputStream is = new FileInputStream(zipFile);
			ZipInputStream zis = new ZipInputStream(is);
			ZipEntry entry = null;
			System.out.println("unzipping" + zipFile.getName() + "...");
			while ((entry = zis.getNextEntry()) != null) {
				String zipPath = entry.getName();
				if (extension != null) {
					zipPath = zipPath.replace(".jpg", "." + extension);
				}
				Log.i("Path", zipPath);
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
					Log.e(CHCGeneralUtil.class.getName(), e.getMessage(), e);
					continue;
				}
			}
			zis.close();
			is.close();
			System.out.println("��ѹ���");
		} catch (Exception e) {
			Log.e(CHCGeneralUtil.class.getName(), e.getMessage(), e);
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
						(int) Math.round(Math.log(maxSize / (double) Math.max(o.outHeight, o.outWidth))
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

	public static Bitmap decodeFileFromDrawable(int id, int maxSize, Context context) {
		Bitmap b = null;
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;

			InputStream inputStream = context.getResources().openRawResource(id);
			BitmapFactory.decodeStream(inputStream, null, o);
			inputStream.close();

			int scale = 1;
			if (o.outHeight > maxSize || o.outWidth > maxSize) {
				scale = (int) Math.pow(
						2,
						(int) Math.round(Math.log(maxSize / (double) Math.max(o.outHeight, o.outWidth))
								/ Math.log(0.5)));
			}

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			inputStream = context.getResources().openRawResource(id);
			b = BitmapFactory.decodeStream(inputStream, null, o2);
			inputStream.close();
		} catch (IOException e) {
		}
		return b;
	}

	public static boolean startActivity(Context a, String packageName) {
		PackageManager pm = a.getPackageManager();
		boolean result = true;
		try {
			Intent intent = pm.getLaunchIntentForPackage(packageName);
//			PackageInfo pi = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
//			String activityName = null;
//			for (ActivityInfo ai : pi.activities) {
//				if (!ai.name.isEmpty())
//					;
//			}
//			Intent intent = new Intent(a, Class.forName(pi.applicationInfo.className));
//			Intent intent = new Intent("android.intent.action.MAIN");
//			String packageAndClassStr = packageName + "/";
//	    intent.setComponent(ComponentName.unflattenFromString(packageAndClassStr));
//	    intent.addCategory("android.intent.category.LAUNCHER");
			if (intent != null) {
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			intent.addCategory(Intent.CATEGORY_LAUNCHER);
				a.startActivity(intent);
			} else {
				result = startActivityUsingScheme(a, packageName);
			}
		} catch (Exception e) {
			Log.e(a.getClass().getName(), e.getMessage(), e);
			result = startActivityUsingScheme(a, packageName);
		}
		return result;
	}

	public static boolean startActivity(Activity a, String packageName, Bundle args) {
		PackageManager pm = a.getPackageManager();
		boolean result = true;
		try {
			Intent intent = pm.getLaunchIntentForPackage(packageName);
			if (null != intent) {
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				if (args != null)
					intent.putExtras(args);
				a.startActivity(intent);
			} else {
				result = startActivityUsingScheme(a, packageName, args);
			}
		} catch (Exception e) {
			Log.e(a.getClass().getName(), e.getMessage(), e);
			result = startActivityUsingScheme(a, packageName, args);
		}
		return result;
	}

	public static boolean startActivityUsingScheme(Activity a, String scheme, Bundle args) {
		Uri uri = Uri.parse(scheme + "://");
		Intent intent = new Intent(Intent.ACTION_RUN, uri);
		boolean result = true;
		try {
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			if (args != null)
				intent.putExtras(args);
			a.startActivity(intent);
		} catch (Exception e) {
			Log.e(a.getClass().getName(), e.getMessage(), e);
			result = false;
		}
		return result;
	}
	

	public static boolean startActivityUsingScheme(Context a, String scheme) {
		Uri uri = Uri.parse(scheme + "://");
		Intent intent = new Intent(Intent.ACTION_RUN, uri);
		boolean result = true;
		try {
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			a.startActivity(intent);
		} catch (Exception e) {
			Log.e(a.getClass().getName(), e.getMessage(), e);
			result = false;
		}
		return result;
	}
	
	public static boolean startActivity(Activity a, String packageName, Bundle args, int flags) {
		PackageManager pm = a.getPackageManager();
		boolean result = true;
		try {
			Intent intent = pm.getLaunchIntentForPackage(packageName);
			if (null != intent) {
				intent.addFlags(flags);
				if (args != null)
					intent.putExtras(args);
				a.startActivity(intent);
			}
		} catch (Exception e) {
			Log.e(a.getClass().getName(), e.getMessage(), e);
			result = false;
		}
		return result;
	}

	public static boolean askUserLogin(Context a) {
		return startActivity(a, LOGIN_PACKAGE_NAME);
	}

	public static ArrayList<View> getViewsByTag(ViewGroup root, String tag) {
		ArrayList<View> views = new ArrayList<View>();
		final int childCount = root.getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View child = root.getChildAt(i);
			if (child instanceof ViewGroup) {
				views.addAll(getViewsByTag((ViewGroup) child, tag));
			}

			final Object tagObj = child.getTag();
			if (tagObj != null && tagObj.equals(tag)) {
				views.add(child);
			}

		}
		return views;
	}

	/**
	 * start an activity of className in packageName app
	 * 
	 * @param activity
	 * @param packageName
	 * @param className
	 * @param fileUri
	 *          : the data to open
	 * @param fileType
	 * @return
	 */
	public static boolean checkAndStartPackage(android.app.Activity activity, String packageName,
			String className, String fileUri, String fileType) {
		try {
			activity.getPackageManager().getApplicationInfo(packageName, 0);

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(android.net.Uri.parse(fileUri), fileType);
			intent.setClassName(packageName, className);

			activity.startActivity(intent);
			return true;
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
	}

	public static boolean startFrontCameraActivity(Activity a) {
		Bundle bundle = new Bundle();
		bundle.putBoolean("profile_photo", true);
		return startActivity(a, CAMERA_ACTIVITY_PACKAGENAME, bundle);
	}
	
	public static boolean startFrontCameraActivityUsingScheme(Activity a) {
		Bundle bundle = new Bundle();
		bundle.putBoolean("profile_photo", true);
		return startActivityUsingScheme(a, CAMERA_ACTIVITY_PACKAGENAME, bundle);
	}

	public static void startWifiSettingActivity(Activity a) {
		a.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
	}

	public static void closeQuietly(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException ioe) {
			// ignore
		}
	}

	static public boolean deleteDirectory(File path) {
        if (path == null) return false;
		if (path.exists()) {
			File[] files = path.listFiles();
			if (files == null) {
				return true;
			}
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

    public static boolean delete(String path) {
        if (path == null || path.isEmpty()) return false;
        File f = new File(path);
        if (f.exists()) {
            if (f.isDirectory()) {
                return deleteDirectory(f);
            } else {
                return f.delete();
            }
        }
        return false;
    }

	public static String executeCommand(String[] args) {
		String result = new String();
		ProcessBuilder processBuilder = new ProcessBuilder(args);
		Process process = null;
		InputStream errIs = null;
		InputStream inIs = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int read = -1;
			process = processBuilder.start();
			errIs = process.getErrorStream();
			while ((read = errIs.read()) != -1) {
				baos.write(read);
			}
			baos.write('\n');
			inIs = process.getInputStream();
			while ((read = inIs.read()) != -1) {
				baos.write(read);
			}
			byte[] data = baos.toByteArray();
			result = new String(data);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			try {
				if (errIs != null) {
					errIs.close();
				}
				if (inIs != null) {
					inIs.close();
				}
			} catch (IOException e) {
				Log.e(TAG, e.getMessage(), e);
			}
			if (process != null) {
				process.destroy();
			}
		}
		return result;
	}

	public static boolean installThroughCommand(String apkAbsolutePath) {

		String[] args = { "pm", "install", "-r", apkAbsolutePath };
		
		String result = executeCommand(args);

		return result.contains("Success");
	}
	
	public static boolean uninstallThroughCommand(String packageName) {

		String[] args = { "pm", "uninstall", packageName };
		
		String result = executeCommand(args);

		return result.contains("Success");
	}
	
	public static Bitmap loadBitmapFromView(View v) {
    Bitmap b = Bitmap.createBitmap( v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);                
    Canvas c = new Canvas(b);
    v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
    v.draw(c);
    return b;
	}
	
	/**
	 * not finished
	 * @param v
	 * @param totalWidth
	 * @param totalHeight
	 * @param fileName
	 * @return
	 */
	public static Bitmap takeScreenShot(View v, int totalWidth, int totalHeight, String fileName) {
		// TODO edit to change for scrollview
		v.setDrawingCacheEnabled(true);
		v.layout(0, 0, totalWidth, totalHeight);
		v.buildDrawingCache(true);
		Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
		v.setDrawingCacheEnabled(false);
//		Bitmap b = Bitmap.createBitmap( totalWidth, totalHeight, Bitmap.Config.ARGB_8888);                
//    Canvas c = new Canvas(b);
//    v.layout(0, 0, totalWidth, totalHeight);
//    v.draw(c);
		return saveBitmap(b, fileName) ? b : null;
	}
	
	public static boolean saveBitmap(Bitmap b, String fileName) {
		// Save bitmap
		SDCardUtil.createFolder(fileName.substring(0, fileName.lastIndexOf(File.separator)));
		File myPath = new File(fileName);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(myPath);
			b.compress(Bitmap.CompressFormat.JPEG, 85, fos);
			fos.flush();
			fos.close();
			return true;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			return false;
		}
	}

	public static Bitmap takeScreenShot(View v, String fileName) {
		Bitmap b = Bitmap.createBitmap( v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);                
	  Canvas c = new Canvas(b);
	  v.layout(0, 0, v.getWidth(), v.getHeight());
	  v.draw(c);

		return saveBitmap(b, fileName) ? b : null;
	}
	
	public static Bitmap takeScreenShot(View v) {
		Bitmap b = Bitmap.createBitmap( v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);                
	  Canvas c = new Canvas(b);
	  v.layout(0, 0, v.getWidth(), v.getHeight());
	  v.draw(c);

		return b;
	}
	
	public static Bitmap overlayToDownRightCorner(Bitmap bmp1, Bitmap bmp2) {
    Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
    Canvas canvas = new Canvas(bmOverlay);
    canvas.drawBitmap(bmp1, new Matrix(), null);
    Matrix matrix = new Matrix();
    matrix.setTranslate(bmp1.getWidth() - bmp2.getWidth(), bmp1.getHeight() - bmp2.getHeight());
    canvas.drawBitmap(bmp2, matrix, null);
    return bmOverlay;
	}
	
	public static Bitmap overlayToDownCenter(Bitmap bmp1, Bitmap bmp2) {
    Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
    Canvas canvas = new Canvas(bmOverlay);
    canvas.drawBitmap(bmp1, new Matrix(), null);
    Matrix matrix = new Matrix();
    matrix.setTranslate(((float) bmp1.getWidth() - bmp2.getWidth()) / 2, bmp1.getHeight() - bmp2.getHeight());
    canvas.drawBitmap(bmp2, matrix, null);
    return bmOverlay;
	}

	public static Bitmap overlayToDownLeftCorner(Bitmap bmp1, Bitmap bmp2) {
    Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
    Canvas canvas = new Canvas(bmOverlay);
    canvas.drawBitmap(bmp1, new Matrix(), null);
    Matrix matrix = new Matrix();
    matrix.setTranslate(0, bmp1.getHeight() - bmp2.getHeight());
    canvas.drawBitmap(bmp2, matrix, null);
    return bmOverlay;
	}
	

	public static Bitmap decodeQRCode(Resources res, int resId, int inSampleSize) {
	// First decode with inJustDecodeBounds=true to check dimensions
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeResource(res, resId, options);

    // Calculate inSampleSize
    options.inSampleSize = inSampleSize;

    // Decode bitmap with inSampleSize set
    options.inJustDecodeBounds = false;
    return BitmapFactory.decodeResource(res, resId, options);
	}
	
	public static Point getScreenSize(final Activity a) {
		Display display = a.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size;
	}
	
	public static void copyFile(File sourceFile, File destFile) throws IOException {
		
		new File(destFile.getParent()).mkdirs();
		
	    if(!destFile.exists()) {
	        destFile.createNewFile();
	    }

	    FileChannel source = null;
	    FileChannel destination = null;

	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destFile).getChannel();
	        destination.transferFrom(source, 0, source.size());
	    }catch (Exception e) {
	    	e.printStackTrace();
	    }finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}
	
	public static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
    if (parser.getEventType() != XmlPullParser.START_TAG) {
        throw new IllegalStateException();
    }
    int depth = 1;
    while (depth != 0) {
        switch (parser.next()) {
        case XmlPullParser.END_TAG:
            depth--;
            break;
        case XmlPullParser.START_TAG:
            depth++;
            break;
        }
    }
	}
	
	public static int getAge(String year, String month, String day) throws NumberFormatException {
		int yearInt = Integer.parseInt(year);
		int monthInt = Integer.parseInt(month);
		int dayInt = Integer.parseInt(day);
		monthInt--;
		Calendar birthCal = Calendar.getInstance();
		birthCal.set(yearInt, monthInt, dayInt);
		Calendar todayCal = Calendar.getInstance();
		
		int diff = todayCal.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);
	    if (birthCal.get(Calendar.MONTH) > todayCal.get(Calendar.MONTH)
	    		|| (birthCal.get(Calendar.MONTH) == todayCal.get(Calendar.MONTH)
	    			&& birthCal.get(Calendar.DATE) > todayCal.get(Calendar.DATE))) {
	        diff--;
	    }
	    return diff;
	}
	
	public static boolean checkChineseString(String str){
		boolean isChinese = false;
		String regEx = "[\\u4e00-\\u9fa5]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		while(m.find()) {
			isChinese = true;
			break;
		} 
		return isChinese;
	} 
	
	public static String readFile(String path) throws IOException {
		  FileInputStream stream = new FileInputStream(new File(path));
		  String str = null;
		  try {
		    FileChannel fc = stream.getChannel();
		    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
		    /* Instead of using default, pass in a decoder. */
		    str = Charset.defaultCharset().decode(bb).toString();
		  } finally {
		    stream.close();
		  }
		  return str;
	}
	
	public static int calculateInSampleSize(
    BitmapFactory.Options options, int reqWidth, int reqHeight) {
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

    public static int calculateInSampleSizeByMaxSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width smaller than the requested height and width.
            while ((height / inSampleSize) > reqHeight
                    || (width / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;

    }

    public static int calculateInSampleSizeByMaxSize(
            int width, int height, int reqWidth, int reqHeight) {

        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width smaller than the requested height and width.
            while ((height / inSampleSize) > reqHeight
                    || (width / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;

    }

    public synchronized static Bitmap decodeFileMinimumSize(File file, int minSize) {
		Bitmap bitmap = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeStream(fis, null, options);
	    
			options.inSampleSize = CHCGeneralUtil.calculateInSampleSize(options, minSize, minSize);
			options.inJustDecodeBounds = false;
			
			fis.close();
			
			fis = new FileInputStream(file);
			
			bitmap = BitmapFactory.decodeStream(fis, null, options);
			
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			bitmap = null;
		} finally {
			CHCGeneralUtil.closeQuietly(fis);
		}
		
		return bitmap;
	}
	
	public synchronized static Bitmap decodeFileMaximumSize(File file, int maxSize) {
		Bitmap bitmap = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeStream(fis, null, options);
	    
			options.inSampleSize = CHCGeneralUtil.calculateInSampleSizeByMaxSize(options, maxSize, maxSize);
			options.inJustDecodeBounds = false;
			
			fis.close();
			
			fis = new FileInputStream(file);
			
			bitmap = BitmapFactory.decodeStream(fis, null, options);
			
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			bitmap = null;
		} finally {
			CHCGeneralUtil.closeQuietly(fis);
		}
		
		return bitmap;
	}
	
	public synchronized static Options decodeStreamOptionsOnly(InputStream inputstream, int maxSize) {
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputstream, null, options);
	    
			options.inSampleSize = CHCGeneralUtil.calculateInSampleSizeByMaxSize(options, maxSize, maxSize);
			options.inJustDecodeBounds = false;
			return options;
			
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		
		return null;
	}

    /**
     * Generates a UUID without dash character ("-")
     * @return
     */
    public static String randomUuidNoDash() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Generates a Base64 version of UUID
     * @return a string in Base64 URL-safe format without padding at the end and no wrapping
     */
    public static String randomUuidBase64() {
        UUID uuid = UUID.randomUUID();
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return encodeUrlSafe64(bb.array());
    }

    public static String encodeUrlSafe64(byte[] array) {
        int flags = Base64.NO_PADDING | Base64.NO_WRAP | Base64.URL_SAFE;
        return Base64.encodeToString(array, flags);
    }

    public static byte[] decodeBase64FromUrlSafe(String content) {
        int flags = Base64.URL_SAFE;
        return Base64.decode(content, flags);
    }

    public static Bitmap scaleBitmapPerMaxSize(Bitmap in, int maxSize) {
        if (in.getWidth() <= maxSize && in.getHeight() <= maxSize) return in;

        int sampleSize = CHCGeneralUtil.calculateInSampleSizeByMaxSize(
                in.getWidth(), in.getHeight(), maxSize, maxSize);
        return Bitmap.createScaledBitmap(in, in.getWidth() / sampleSize, in.getHeight() / sampleSize, false);
    }

    public static Bitmap cropSquareBitmap(Bitmap srcBmp) {
        Bitmap dstBmp;
        if (srcBmp.getWidth() >= srcBmp.getHeight()){

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
                    0,
                    srcBmp.getHeight(),
                    srcBmp.getHeight()
            );

        }else{

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    0,
                    srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
                    srcBmp.getWidth(),
                    srcBmp.getWidth()
            );
        }

        return dstBmp;
    }

}
