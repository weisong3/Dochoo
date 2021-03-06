package com.chc.dochoo.userlogin;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;

import com.chc.dochoo.CHCApplication;
import com.chc.dochoo.conversations.ConversationActivity;
import com.chc.exceptions.InternalErrorException;
import com.chc.found.FoundSettings;
import com.chc.found.fragments.RegistrationFragment.OnRegisterSucceedListener;
import com.chc.found.fragments.SignInFragment;
import com.chc.found.fragments.SignInFragment.OnNewUserClickedListener;
import com.chc.found.models.AddEntityState;
import com.chc.found.models.EntityUser;
import com.chc.found.network.NetworkRequestsUtil;
import com.chc.found.presenters.EntityPresenter;
import com.chc.found.views.IEntityView;
import com.test.found.R;

import java.io.IOException;
import java.util.List;

public class UserLogInActivity extends ActionBarActivity
        implements OnNewUserClickedListener, OnRegisterSucceedListener {

    public static final String IS_DOCTOR_KEY = "isDoctor";
    public static final String IS_SINGLE_KEY = "isSingle";
    private static final String TAG = "UserLogInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register_user);

        if (findViewById(R.id.regster_fragment_container) != null) {
            Fragment fragment = new SignInFragment();
            loadFragment(fragment);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (getSupportFragmentManager().findFragmentById(R.id.regster_fragment_container) == null) {
            transaction.add(R.id.regster_fragment_container, fragment);
        } else if (getSupportFragmentManager().findFragmentById(R.id.regster_fragment_container) != fragment) {
            transaction.replace(R.id.regster_fragment_container, fragment);
            transaction.addToBackStack(fragment.getClass().getName());
        }
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static final void startLogInActivity(Activity a) {
        Intent intent = new Intent(a, UserLogInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        a.startActivity(intent);
        a.overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(0, 0);
    }

    @Override
    public void onNewUserClicked() {
        Intent intent = new Intent(this, UserRoleSelectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onLoginSucceed() {
        CHCApplication app = CHCApplication.getInstance(this);
        app.getHelper().clearAllTables();
        loadContactsFromCloud();
        new getMissedPushTask().execute(app.getUserId(),app.getRegId());
        gotoUserMainActivity();
    }

    @Override
    public void onRegisterSucceed() {
        CHCApplication app = CHCApplication.getInstance(this);
        app.getHelper().clearAllTables();
        loadContactsFromCloud();
        gotoUserMainActivity();
    }

    private void loadContactsFromCloud() {
        CHCApplication application = CHCApplication.getInstance(this);
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
                return CHCApplication.getInstance(UserLogInActivity.this);
            }
        });
        presenter.refreshAllContactList(application.getHelper(), application.getUserId(), application.getRegId(),
                FoundSettings.getInstance(application).isPatient());

    }

    public void gotoUserMainActivity() {

        ConversationActivity.start(this);

        UserLogInActivity.this.finish();

    }

    private class getMissedPushTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try{
                result = NetworkRequestsUtil.getMissingMsg(params[0], params[1]);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            } catch (InternalErrorException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return result;
        }
    }

    private class getMissedGroupMsgTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            return null;
        }
    }
}
