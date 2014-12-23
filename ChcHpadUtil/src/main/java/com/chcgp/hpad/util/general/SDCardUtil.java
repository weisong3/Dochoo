package com.chcgp.hpad.util.general;

import java.io.File;

import android.os.Environment;

/**
 * class name:SDCardUtil<BR>
 * class description:sd鍗″伐鍏风被<BR>
 * PS锛�<BR>
 * 
 * @version 1.00 2012-5-3
 * @author ZHENSHI)peijiangping
 */
public class SDCardUtil {

	/**
	 * Role:鍦╯d鍗′腑鍒涘缓鏂囦欢澶�BR>
	 * Date:2012-5-3<BR>
	 * 
	 * @author ZHENSHI)peijiangping
	 */
	public static void createFolder(String path) {
		File tmpFile = new File(path);
//		System.out.println(path + "path");
		if (!tmpFile.exists()) {
			tmpFile.mkdirs();
//			System.out.println(tmpFile.mkdirs() + "tmpFile.mkdirs()");
		}
	}

	/**
	 * Role:鑾峰緱sd鍗℃牴鐩綍<BR>
	 * Date:2012-5-3<BR>
	 * 
	 * @author ZHENSHI)peijiangping
	 */
	public static String getSDCardPath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	/**
	 * Role:鍒ゆ柇sd鍗℃槸鍚﹀凡鎻掑叆<BR>
	 * Date:2012-5-3<BR>
	 * 
	 * @author ZHENSHI)peijiangping
	 */
	public static boolean hasStorage() {
		String state = android.os.Environment.getExternalStorageState();
		if (android.os.Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}
}
