package com.chc.dochoo.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chc.dochoo.CHCApplication;
import com.chc.dochoo.splash.SplashActivity;
import com.chc.found.FoundSettings;
import com.chc.found.presenters.SignOutPresenter;
import com.chc.found.views.IBaseView;
import com.test.found.R;

import org.apache.commons.lang.StringUtils;

public class SettingsActivity extends ActionBarActivity implements IBaseView {
	
//	private ToggleButton mNotificationToggle;
	private TextView textViewUsername;
    private SignOutPresenter mSignOutPresenter;
    private SyncSettingPresenter syncSettingPresenter;

    private View[] messageLifeOptions, reauthOptions;
    private String[] messageLifeStrings, reauthStrings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_settings);

		mSignOutPresenter = new SignOutPresenter(this);
        syncSettingPresenter = new SyncSettingPresenter();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

//		mNotificationToggle = (ToggleButton) findViewById(R.id.notification_toggle);
		textViewUsername = (TextView) findViewById(R.id.textView_username);
        Button buttonSignOut = (Button) findViewById(R.id.button_sign_out);

		buttonSignOut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                askAndSignOut();
            }
        });

        messageLifeStrings = getResources().getStringArray(R.array.message_life);
        reauthStrings = getResources().getStringArray(R.array.reauthenticate_time);

        messageLifeOptions = new View[messageLifeStrings.length];
        reauthOptions = new View[reauthStrings.length];

        messageLifeOptions[0] = findViewById(R.id.option_1_1);
        messageLifeOptions[1] = findViewById(R.id.option_1_2);
        messageLifeOptions[2] = findViewById(R.id.option_1_3);

        reauthOptions[0] = findViewById(R.id.option_2_1);
        reauthOptions[1] = findViewById(R.id.option_2_2);
        reauthOptions[2] = findViewById(R.id.option_2_3);
        reauthOptions[3] = findViewById(R.id.option_2_4);

        setDefaultOptions();
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        setMessageLifeOnClickListeners();
        setReauthOnClickListeners();
    }

    private void setReauthOnClickListeners() {
        for (int i = 0; i < reauthOptions.length; i++) {
            View v = reauthOptions[i];
            final int index = i;
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectOption(view, reauthOptions);
                    writeReauthOption(reauthStrings[index]);
                }
            });
        }
    }

    private void writeReauthOption(String reauthString) {
        if (StringUtils.isNotBlank(reauthString))
            FoundSettings.getInstance(this).setReauthenticateInterval(this, Long.parseLong(reauthString));
    }

    private void setMessageLifeOnClickListeners() {
        for (int i = 0; i < messageLifeOptions.length; i++) {
            View v = messageLifeOptions[i];
            final int index = i;
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectOption(view, messageLifeOptions);
                    writeMessageOption(messageLifeStrings[index]);
                }
            });
        }
    }

    private void writeMessageOption(String messageLifeString) {
        FoundSettings.getInstance(this).setMsgDeleteInterval(this, Long.parseLong(messageLifeString));
        syncSettingPresenter.syncMessageLifeSetting(getCHCApplication().getUserId(), messageLifeString);
    }

    private void setDefaultOptions() {
        String currentOption = String.valueOf(FoundSettings.getInstance(this).getMsgDeleteInterval(this));
        selectOption(messageLifeOptions[getOptionIndex(messageLifeStrings, currentOption)], messageLifeOptions);
        currentOption = String.valueOf(FoundSettings.getInstance(this).getReauthenticateInterval(this));
        selectOption(reauthOptions[getOptionIndex(reauthStrings, currentOption)], reauthOptions);
    }

    private void selectOption(View select, View[] clearAll) {
        for (View v : clearAll) {
            clearSelection(v);
        }
        doSelect(select);
    }

    private void doSelect(View select) {
        select.setBackgroundResource(R.drawable.setting_option_selected);
    }

    private void clearSelection(View v) {
        v.setBackgroundResource(R.drawable.bg_settings_item);
    }

    private int getOptionIndex(String[] array, String target) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(target)) {
                return i;
            }
        }
        return array.length-1;
    }

    @Override
	protected void onResume() {
		super.onResume();

        CHCApplication.getInstance(this).showNetworkStatusToast();
//		mNotificationToggle.setChecked(FoundSettings.getInstance(this).isShowNotification());
//		mNotificationToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				FoundSettings.getInstance(SettingsActivity.this).setShowNotification(SettingsActivity.this, isChecked);
//			}
//		});
		
		if (getCHCApplication().isSignedIn())
			textViewUsername.setText(CHCApplication.getInstance(this).getUser().getUsername());
		else
			textViewUsername.setText(R.string.textview_settings_signin_first);
	}

	protected void onSignedOut() {
		Toast.makeText(this, getString(R.string.sign_out_complete), Toast.LENGTH_LONG).show();
//		textViewUsername.setText(R.string.textview_settings_signin_first);
        Intent intent = new Intent(this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public static void start(Context context) {
		Intent intent = new Intent(context, SettingsActivity.class);
		context.startActivity(intent);
	}

	@Override
	public CHCApplication getCHCApplication() {
		return CHCApplication.getInstance(this);
	}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    private void askAndSignOut() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.logout_title)
                .setMessage(R.string.logout_message)
                .setPositiveButton(R.string.logout_confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getCHCApplication().isSignedIn()) {
                            mSignOutPresenter.signOut();
                            onSignedOut();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }


}
