package com.chc.found.presenters;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ExternalIntentPresenter {

	public void startCallIntent(Context context, String number) {
		try {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + number));
            context.startActivity(callIntent);
        } catch (ActivityNotFoundException activityException) {
            Log.e("Calling a Phone Number", "Call failed", activityException);
        }
	}
	
	public void startOpenURLIntent(Context context, String url) {
		if (!url.substring(0, 4).contains("http")) {
			url = "https://" + url;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)); 
		context.startActivity(intent);
	}
	
	public void startGoogleMapIntent(Context context, String label, String address) {
		Geocoder geoCoder = new Geocoder(context.getApplicationContext(),
				Locale.getDefault());
		
		try {
			List<Address> addresses = geoCoder.getFromLocationName(address, 1);
			double lat = 0, lng = 0;
			if (addresses.size() > 0) {
				lat = addresses.get(0).getLatitude();
				lng = addresses.get(0).getLongitude();
			} 
			
			String uriBegin = "geo:" + lat + "," + lng;
			String query = lat + "," + lng + "(" + label + ")";
			String encodedQuery = Uri.encode(query);
			String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
			Uri uri = Uri.parse(uriString);
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
			context.startActivity(intent);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


    public void startSmsIntent(Context context, String content) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            sendAsSmsPostKitKat(context, content);
        } else {
            sendAsSmsPreKitKat(context, content);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void sendAsSmsPostKitKat(Context context, String content) {
        String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context); //Need API 19

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, content);

        if (defaultSmsPackageName != null) {
            // Can be null in case that there is no default, then the user would be able to choose any app that support this intent.
            sendIntent.setPackage(defaultSmsPackageName);
        }
        context.startActivity(sendIntent);
    }

    private void sendAsSmsPreKitKat(Context context, String content) {
        Uri smsUri = Uri.parse("sms:");
        Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
        intent.putExtra("sms_body", content);
        context.startActivity(intent);
    }

    public void startEmailIntent(Context context, String title, String content) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        try {
            context.startActivity(Intent.createChooser(intent, ""));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void startSendTextIntent(Context context, String content) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, content);
        try {
            context.startActivity(Intent.createChooser(intent, ""));
        } catch (android.content.ActivityNotFoundException ex) {
        }
    }
}
