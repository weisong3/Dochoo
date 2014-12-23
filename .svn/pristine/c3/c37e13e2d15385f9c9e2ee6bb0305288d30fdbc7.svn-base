package com.chcgp.hpad.util.general;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class CHCShareUtil {
	public static void share(Context context, String imageFileLocation, String textToSend) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("image/*");
		
		intent.putExtra(Intent.EXTRA_TEXT, textToSend);
		intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(imageFileLocation)));
		
		context.startActivity(Intent.createChooser(intent, "Share with..."));
	}
	
	public static void share(Context context, String textToSend) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		
		intent.putExtra(Intent.EXTRA_TEXT, textToSend);
		
		context.startActivity(Intent.createChooser(intent, "Share with..."));
	}
}
