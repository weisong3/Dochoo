package com.chc.dochoo.splash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.chc.dochoo.CHCApplication;
import com.chc.dochoo.conversations.ConversationActivity;
import com.chc.dochoo.userlogin.ReauthenticateActivity;
import com.chc.dochoo.userlogin.UserLogInActivity;
import com.chc.found.FoundSettings;
import com.chc.found.models.AddEntityState;
import com.chc.found.models.EntityUser;
import com.chc.found.network.NetworkRequestsUtil;
import com.chc.found.presenters.AbstractAccountPresenter;
import com.chc.found.presenters.EntityPresenter;
import com.chc.found.presenters.PushServicePresenter;
import com.chc.found.presenters.PushServicePresenter.IPushServiceView;
import com.chc.found.views.IEntityView;
import com.chcgp.hpad.util.download.ImageDownloader;
import com.test.found.R;

import org.apache.commons.lang.StringUtils;

import java.util.List;

public class SplashActivity extends Activity implements IPushServiceView{

	private static final long SPLASH_DELAY = 2 * 1000; // milliseconds
	private static int VERSION = 1;
	private static final String VERSION_TAG = "version";
	private static final String TAG = SplashActivity.class.getSimpleName();
	private PushServicePresenter pushServicePresenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.splash_activity);

		PackageInfo pInfo;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			VERSION = pInfo.versionCode;
            checkAppVersion();
//			ActivityTracker.getInstance(this).recordActivityOpen();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				ImageDownloader.checkCacheExpiration(SplashActivity.this);
			}
		});
		t.start();

		pushServicePresenter = new PushServicePresenter(this);
		pushServicePresenter.checkGetuiServices();

	}

    private class checkVersionTask extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids) {
            try{
                return NetworkRequestsUtil.getCurrentAppVersion();
            }catch (Exception e){
                Log.e(TAG, "check app version exception : " +e.getMessage());
            }
           return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(StringUtils.isNotBlank(s)){
                try{
                    int serverVersion = Integer.parseInt(s);
                    if(serverVersion > VERSION){
                        Toast.makeText(SplashActivity.this, getString(R.string.new_version_app_available), Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    Log.e(TAG,"check app version exception : " +e.getMessage());
                }
            }
            super.onPostExecute(s);
        }
    }
    private void checkAppVersion(){
        new checkVersionTask().execute();
    }

    @Override
	public void onBackPressed() {
        finish();
	}

	private void writeVersion() {
		SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
		Editor e = sp.edit();
		e.putInt(VERSION_TAG, VERSION);
		e.commit();
	}

	private boolean isNewVersion() {
		SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
		int oldVersion = sp.getInt(VERSION_TAG, 0);

		return oldVersion != VERSION;
	}

	private void startIntroActivity() {

		writeVersion();

//		Bundle args = new Bundle();
//		args.putIntArray(IntroductionActivity.INPUT_IDS, INTRO_DRAWABLE_ARR);
//		startActivity(new Intent(this, IntroductionActivity.class)
//		  .putExtras(args).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
	}

	@Override
	public void onRegisterPushIdFailed() {

        Toast.makeText(this, getString(R.string.register_push_service_fail),
                Toast.LENGTH_SHORT).show();

        finish();

	}

	@Override
	public Activity getContextActivity() {
		return this;
	}

    @Override
    public void onRegisterPushIdSucceeded() {
        final Handler mainHandler = new Handler();
        CHCApplication application = CHCApplication.getInstance(SplashActivity.this);
        EntityPresenter presenter = new EntityPresenter(new IEntityView() {
            @Override
            public void onEntityLoaded(EntityUser user) {
            }

            @Override
            public void onEntityLoaded(List<EntityUser> user) {
            }

            @Override
            public void getEntityFailed(AddEntityState state) {
            }

            @Override
            public CHCApplication getCHCApplication() {
                return CHCApplication.getInstance(SplashActivity.this);
            }
        });
//        if (application.isSignedIn()) {
//            if (FoundSettings.getInstance(application).isPatient()) {
//                presenter.loadProfessionalList(application.getHelper(), application.getUserId(), application.getRegId());
//            } else {
//                presenter.refreshRelationList(application.getHelper(), application.getUserId(), application.getRegId());
//                presenter.refreshPatientUserList(application.getHelper(), application.getUserId(), application.getRegId());
//            }
//        }
        mainHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                CHCApplication application = CHCApplication.getInstance(SplashActivity.this);

                if (application.isSignedIn()) {
                    // load user profile updates from server
                    AbstractAccountPresenter.loadUserProfile(application, application.getUserId());

                    if (FoundSettings.isReauthenticationNeeded(SplashActivity.this)) {
                        ReauthenticateActivity.start(SplashActivity.this);
                    } else {
                        ConversationActivity.start(SplashActivity.this);
                    }

                    finish();
                } else {
                    UserLogInActivity.startLogInActivity(SplashActivity.this);
                    finish();
                }
            }

        }, SPLASH_DELAY);
    }

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }
}
