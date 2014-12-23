package com.chc.custom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.chc.dialog.CHCCommand;

public class CHCCustomUtil {
	public static final String CUST_PATH
	  = Environment.getExternalStorageDirectory()
	  + File.separator + "chcconf" + File.separator;
	
	public static final String LAUNCHER_LOGO_FILE_NAME
  	= "logo_launcher.png";
	
	public static final String UNLOCK_SCREEN_BG_FILE_NAME
	  = "unlock_screen_bg.jpg";
	
	public static final String HOME_WIDGET2_FILE_NAME
	  = "home_widget2.png";

	private static final String HOME_WIDGET3_FILE_NAME
	  = "home_widget3.png";
	
	public static final String VIDEO_CHANNEL_FILE_NAME
  	= "video_channel.txt";
	
	public static final String VIDEO_BUTTON_NAME
	  = "video_button.png";
	
	public static final String HIDE_CHC_VIDEO
	  = "no_chc_video";
	
	public static final String INSTRUCTIONS_INDEX_PAGE
	  = "instructions" + File.separator + "index.html";
	
	private static final String DOCTORS_INDEX_PAGE
	  = "doctors" + File.separator + "index.html";
	
	private static final String TITLE_URL_FILE_NAME
	  = "background_title.txt";
	
	private static final String CONFIG_PROPERTIES_FILE_NAME
	  = "config_props";

	
	
	public static boolean isCustomFolderAvailable() {
		return new File(CUST_PATH).isDirectory();
	}
	
	/**
	 * set the title image of an activity such as cloud storage activity
	 * @param a
	 * @param iv
	 * @param fileNameNoPath name of the file not containing path
	 * @return : success or not
	 */
	public static boolean checkAndSetActivityTitle(ImageView iv, CHCAppEnum app) {
		if (iv == null)
			return false;
		Drawable d = Drawable.createFromPath(CUST_PATH + app.getHeaderFileNameNoPath());
		if (d != null) {
			iv.setImageDrawable(d);
			return true;
		} else
			return false;
	}
	
	/**
	 * launcher specific title
	 * @param a
	 * @param iv
	 * @return
	 */
	public static boolean checkAndSetLauncherTitle(ImageView iv) {
		if (iv == null)
			return false;
		Drawable d = Drawable.createFromPath(CUST_PATH + LAUNCHER_LOGO_FILE_NAME);
		if (d != null) {
			iv.setImageDrawable(d);
			return true;
		} else
			return false;
	}
	
	
	public static String getConfigFolderPathForVideo() {
		return CUST_PATH.replaceAll("/", "//");
	}
	
	public static String getVideoChannelName() {
		File f = new File(CUST_PATH + VIDEO_CHANNEL_FILE_NAME);
		if (f.isFile()) {
			String result = null;
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(f));
				result = br.readLine();
			} catch (FileNotFoundException e) {
//				Log.e(CHCCustomUtil.class.getName(), "Organization channel file not found!", e);
			} catch (Exception e) {
//				Log.e(CHCCustomUtil.class.getName(), "Unknown error", e);
			} finally {
				if (br != null)
					try {	br.close(); } catch (Exception e) {}
			}
			if (result != null)
				return result;
			else
				return new String();
		} else
			return new String();
	}
	
	public static boolean checkAndSetOrgVideoButton(ImageView iv) {
		Drawable d = Drawable.createFromPath(CUST_PATH + VIDEO_BUTTON_NAME);
		if (d != null) {
			iv.setImageDrawable(d);
			return true;
		} else
			return false;
	}
	
	public static boolean isCHCVideoHidden() {
		return new File(CUST_PATH + HIDE_CHC_VIDEO).exists(); // TODO change to config properties
	}
	
	public static boolean checkAndSetHomeWidget2(ViewGroup vg) {
		boolean result = false;
		Drawable d = Drawable.createFromPath(CUST_PATH + HOME_WIDGET2_FILE_NAME);
		if (d != null) {
			vg.removeAllViews();
			ImageView iv = new ImageView(vg.getContext());
			iv.setImageDrawable(d);
			vg.addView(iv);
			
			result = true;
		}
		
		return result;
	}
	
	public static boolean checkAndSetHomeWidget3(ImageButton ib) {
		boolean result = false;
		Drawable d = Drawable.createFromPath(CUST_PATH + HOME_WIDGET3_FILE_NAME);
		if (d != null) {
			ib.setImageDrawable(d);
			
			result = true;
		}
		
		return result;
	}
	
	public static String checkAndGetInstructionsIndexPage() {
		if (new File(CUST_PATH + INSTRUCTIONS_INDEX_PAGE).isFile())
			return "file://" + CUST_PATH + INSTRUCTIONS_INDEX_PAGE;
		else
			return new String();
	}
	
	public static String checkAndGetDoctorsIndexPage() {
		if (new File(CUST_PATH + DOCTORS_INDEX_PAGE).isFile())
			return "file://" + CUST_PATH + DOCTORS_INDEX_PAGE;
		else
			return new String();
	}
	
	public static String getBackgroundTitleUrl() {
		BufferedReader br = null;
		String result = new String();
		try {
			br = new BufferedReader(new FileReader(CUST_PATH + TITLE_URL_FILE_NAME));
			result = br.readLine();
		} catch (Exception e) {
//			Log.e(CHCCustomUtil.class.getName(), e.getMessage(), e);
		}
		
		return result;
	}
	
	public static boolean hasUnlockBg() {
		return new File(CUST_PATH + UNLOCK_SCREEN_BG_FILE_NAME).isFile();
	}
	
	public static File getConfigPropertiesFile() {
		return new File(CUST_PATH + CONFIG_PROPERTIES_FILE_NAME);
	}
	
	public static boolean checkAndSetImageButton(final ImageButton ib, final CHCCommand command
			, String entryNamePostfix) {
		boolean result = false;
		
		if (ib == null || !CustomProperties.isCustomized())
			return false;
		// get pressed and unpressed image names
		int resId = ib.getId();
		String resEntryName = ib.getContext().getResources().getResourceEntryName(resId);
		String upEntryName = resEntryName + "_up";
		String downEntryName = resEntryName + "_down";
		if (entryNamePostfix != null) {
			upEntryName += entryNamePostfix;
			downEntryName += entryNamePostfix;
		}
		
		String upImageName = CustomProperties.getCustomProperties()
			  .get(upEntryName);
		String downImageName = CustomProperties.getCustomProperties()
			  .get(downEntryName);
			
		try {
			final Drawable up = Drawable.createFromPath(CUST_PATH + upImageName);
			final Drawable down = Drawable.createFromPath(CUST_PATH + downImageName);
			if (up == null || down == null)
				return false;
			ib.setImageDrawable(up);
			// set pressed and unpressed images and actions
			ib.setOnTouchListener(new View.OnTouchListener() {
				private boolean pressed = false;
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						ib.setImageDrawable(down);
						pressed = true;
						break;
					case MotionEvent.ACTION_UP:
						ib.setImageDrawable(up);
						if (pressed) {
							v.playSoundEffect(android.view.SoundEffectConstants.CLICK);
							command.execute();
							pressed = false;
						}
						break;
					}
					return true;
				}
			});
			result = true;
		} catch (Exception e) {
//			Log.e(ib.getContext().getClass().getName(), "set image button failed",e);
		}
		
		if (result)
			ib.setVisibility(View.VISIBLE);
		
		return result;
	}
	
	public static boolean checkAndSetImageView(final ImageView iv) {
		boolean result = false;
		
		if (iv == null || !CustomProperties.isCustomized())
			return false;
		// get pressed and unpressed image names
		int resId = iv.getId();
		String resEntryName = iv.getContext().getResources().getResourceEntryName(resId);
		String upImageName = CustomProperties.getCustomProperties()
			  .get(resEntryName);
		try {
			final Drawable up = Drawable.createFromPath(CUST_PATH + upImageName);
			if (up != null) {
				iv.setImageDrawable(up);
				result = true;
			} else {
				return false;
			}
		} catch (Exception e) {
//			Log.e(iv.getContext().getClass().getName(), "set image button failed",e);
		}
		
		if (result)
			iv.setVisibility(View.VISIBLE);
		
		return result;
	}
}
