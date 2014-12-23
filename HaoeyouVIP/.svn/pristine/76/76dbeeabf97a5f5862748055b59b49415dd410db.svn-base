package com.chc.found;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chc.found.config.Apis;
import com.chc.found.models.EntityUser;
import com.chc.found.models.MedicalCenterUser;
import com.chc.found.presenters.QrcodePresenter;
import com.chc.found.presenters.QrcodePresenter.IQrcodeCallback;
import com.chc.found.utils.ImageUtil;
import com.chcgp.hpad.util.download.ImageDownloader;
import com.makeramen.RoundedDrawable;
import com.test.found.R;

import org.apache.commons.lang.StringUtils;

public class DochooCardActivity extends ActionBarActivity {

    private static final String TAG = DochooCardActivity.class.getSimpleName();
	
	private static final String KEY_NAME = "name";
	private static final String KEY_WEBSITE = "website";
	private static final String KEY_INSURANCE = "insurance";
	private static final String KEY_PIN = "pin";
	private static final String KEY_ICON_URL = "iconurl";

	private ViewGroup websiteWrapper;
	private ViewGroup insuranceWrapper;
	private TextView name;
	private TextView website;
	private TextView insurance;
	private TextView pin;
	private ImageView qr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dochoo_card);
		websiteWrapper = (ViewGroup) findViewById(R.id.dochoo_card_website_wrapper);
		insuranceWrapper = (ViewGroup) findViewById(R.id.dochoo_card_insurance_wrapper);
		name = (TextView) findViewById(R.id.dochoo_card_name);
		website = (TextView) findViewById(R.id.dochoo_card_website);
		insurance = (TextView) findViewById(R.id.dochoo_card_insurance);
		pin = (TextView) findViewById(R.id.dochoo_card_pin);
		qr = (ImageView) findViewById(R.id.dochoo_card_qr);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	public static void startActivity(Activity a, EntityUser entityUser) {
		Intent intent = new Intent(a, DochooCardActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(KEY_NAME, entityUser.getFullname());
		if (entityUser instanceof MedicalCenterUser)
			bundle.putString(KEY_WEBSITE, ((MedicalCenterUser) entityUser).getWebsite());
		bundle.putString(KEY_INSURANCE, entityUser.getAcceptedInsurances());
		bundle.putString(KEY_PIN, entityUser.getPin());
		bundle.putString(KEY_ICON_URL, entityUser.getProfileIconUrl());
		intent.putExtras(bundle);
		a.startActivity(intent);
		
		a.overridePendingTransition(0, 0);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		super.onNewIntent(intent);
	}

	@Override
	public void onBackPressed() {
		finish();
		this.overridePendingTransition(0, 0);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Bundle extras = getIntent().getExtras();
        if (extras == null) {
            if (Apis.DEBUG) Log.e(TAG, "Empty intent extras onResume");
            return;
        }
		String web = extras.getString(KEY_WEBSITE);
		String fullname = extras.getString(KEY_NAME);
		String insurance = extras.getString(KEY_INSURANCE);
		String pin = extras.getString(KEY_PIN);
		String iconUrl = extras.getString(KEY_ICON_URL);

        //noinspection ConstantConditions
        if (StringUtils.isNotBlank(iconUrl) && !iconUrl.equals("null")) {
			int dimen = getResources().getDimensionPixelSize(R.dimen.logo_size);
			ImageDownloader.getInstance().downloadWithCallback(this, iconUrl, dimen,
	    		new ImageDownloader.Callback() {
						
						@Override
						public void onImageLoaded(Bitmap bitmap) {
							try {
                                ActionBar actionBar = getSupportActionBar();
                                if (actionBar != null) {
                                    Drawable logo = ImageUtil.createIconImageView(getResources(), bitmap);
                                    if (logo instanceof RoundedDrawable) {
                                        ((RoundedDrawable) logo).setScaleType(ImageView.ScaleType.CENTER_CROP)
                                                .setCornerRadius(getResources().getDimensionPixelSize(R.dimen.icon_action_bar_radius))
                                                .setBorderWidth(getResources().getDimensionPixelSize(R.dimen.icon_border_size))
                                                .setBorderColors(getResources().getColorStateList(R.color.default_dochoo_orange));
                                    }
                                    actionBar.setLogo(logo);
                                }
							} catch (Exception ignored) {
                                // ignored
                            }
						}
					});
			
		}
		
		if (fullname != null && fullname.length() > 0 && !fullname.equals("null")) {
			this.name.setText(fullname);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null)
                actionBar.setTitle(fullname);
		} else {
			this.name.setVisibility(View.GONE);
		}
		
		if (web != null && web.length() > 0 && !web.equals("null")) {
			this.website.setText(web);
		} else {
			this.websiteWrapper.setVisibility(View.GONE);
		}
		
		if (insurance != null && insurance.length() > 0 && !insurance.equals("null")) {
			this.insurance.setText(insurance);
		} else {
			this.insuranceWrapper.setVisibility(View.GONE);
		}
		
		if (pin != null && pin.length() > 0 && !pin.equals("null")) {
			this.pin.setText(pin);
			int dimension = 500;
			QrcodePresenter presenter = new QrcodePresenter(new IQrcodeCallback() {
	
				@Override
				public void onEncodeSuccess(Bitmap bitmap) {
					DochooCardActivity.this.qr.setImageBitmap(bitmap);
				}
		
				@Override
				public void onEncodeFailed() {
		
				}
			});
			 	
			presenter.encode(pin, dimension);
		} else {
			this.pin.setVisibility(View.GONE);
		}
	}

}
