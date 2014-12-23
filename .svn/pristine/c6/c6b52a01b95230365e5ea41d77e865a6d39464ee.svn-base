package com.chc.dochoo.userlogin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.chc.dochoo.CHCApplication;
import com.chc.dochoo.conversations.ConversationActivity;
import com.chc.found.FoundSettings;
import com.chc.found.presenters.RegisterPresenter;
import com.chc.found.views.IRegisterView;
import com.test.found.R;

import java.io.Serializable;

public class RegisterInfoActivity extends ActionBarActivity
        implements OnRegisterFragmentInteractionListener, IRegisterView {

    private static final String BUNDLE_KEY_ROLE = "key_role";
    private static final int REQUEST_CODE = 321;
    private Role currentRole;
    private RegisterUserInfo lastRegisterInfo;
    private RegisterPresenter mRegisterPresenter;
    private ProgressDialog mProgress;
    private boolean agreed;


    public static Intent getIntent(Context context, Role desiredRole) {
        Intent intent = new Intent(context, RegisterInfoActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(BUNDLE_KEY_ROLE, desiredRole);
        intent.putExtras(args);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_info);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        mRegisterPresenter = new RegisterPresenter(this);

        if (savedInstanceState == null) {
            initFragment(getIntent());
        }
    }

    private void initFragment(Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            Bundle extras = intent.getExtras();
            Serializable serializable = extras.getSerializable(BUNDLE_KEY_ROLE);
            if (serializable instanceof Role) {
                currentRole = (Role) serializable;
                Fragment f;
                switch (currentRole) {
                case DOCTOR:
                    f = PhysicianInfoFragment.newInstance();
                    break;
                case CENTER:
                    f = CenterInfoFragment.newInstance();
                    break;
                case PATIENT:
                    f = PatientInfoFragment.newInstance();
                    break;
                default:
                    finish();
                    return;
                }
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, f)
                    .commit();
            } else {
                onBackPressed();
            }

        }
    }

    private void displayProgress() {
        if (mProgress == null) mProgress = ProgressDialog.show(
                this,
                null,
                getString(R.string.network_wait),
                true,
                false);
        else mProgress.show();
    }

    private void dismissProgress() {
        if (mProgress != null && mProgress.isShowing()) mProgress.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRegisterRequest(RegisterUserInfo info) {
        lastRegisterInfo = info;

        if (!agreed) {
            Intent startingIntent = GeneralAgreementActivity.getStartingIntent(
                    this,
                    RawStringUtil.getFromRawFileAsString(getResources(), R.raw.term_of_service),
                    getString(R.string.checkbox_agree_disclaimer)
            );

            startActivityForResult(startingIntent, REQUEST_CODE);
        } else {
            doRegister(info);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                onAgreed();
            } else {
                Toast.makeText(this, R.string.must_agree, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void onAgreed() {
        this.agreed = true;

        doRegister(lastRegisterInfo);
    }

    private void doRegister(RegisterUserInfo info) {
        if (info == null) return;

        displayProgress();

        String pushId = getCHCApplication().getRegId();
        switch (currentRole) {
            case DOCTOR:
                if (info instanceof RegisterPhysicianInfo) {
                    mRegisterPresenter.registerPhysician((RegisterPhysicianInfo) info, pushId);
                }
                break;
            case CENTER:
                if (info instanceof RegisterCenterInfo) {
                    mRegisterPresenter.registerMedicalGroup((RegisterCenterInfo) info, pushId);
                }
                break;
            case PATIENT:
                if (info instanceof RegisterPatientInfo) {
                    mRegisterPresenter.registerPatient((RegisterPatientInfo) info, pushId);
                }
                break;
        }
    }

    @Override
    public void onRegisterFailed(String errorMsg) {
        dismissProgress();
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRegisterSucceeded() {
        dismissProgress();
        Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();
        FoundSettings.getInstance(this).setUserRole(this, currentRole == Role.PATIENT);

        startConversationActivity();
    }

    private void startConversationActivity() {
        ConversationActivity.start(this);
        finish();
    }

    @Override
    public CHCApplication getCHCApplication() {
        return CHCApplication.getInstance(this);
    }
}
