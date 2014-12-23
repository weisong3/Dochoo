package com.chc.dochoo.userlogin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chc.dochoo.CHCApplication;
import com.chc.dochoo.conversations.ConversationActivity;
import com.chc.dochoo.splash.SplashActivity;
import com.chc.found.models.EntityUser;
import com.chc.found.presenters.LoginPresenter;
import com.chc.found.presenters.SignOutPresenter;
import com.chc.found.views.ILogInView;
import com.test.found.R;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class ReauthenticateActivity extends ActionBarActivity implements ILogInView {
    /**
     * The default email to populate the email field with.
     */
    public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

    // Values for email and password at the time of the login attempt.
    private String mEmail;
    private String mPassword;

    // UI references.
    private EditText mPasswordView;
    private ProgressDialog mProgress;
    private LoginPresenter mPresenter;
    private SignOutPresenter mSignOutPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reauthenticate);

        mPresenter = new LoginPresenter(this);
        mSignOutPresenter = new SignOutPresenter(this);

        // Set up the login form.
        mEmail = CHCApplication.getInstance(this).getUserName();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.button_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        findViewById(R.id.button_sign_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askAndSignOut();
            }
        });
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

    protected void onSignedOut() {
        Toast.makeText(this, getString(R.string.sign_out_complete), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        // Reset errors.
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mEmail = CHCApplication.getInstance(this).getUserName();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.register_all_fields_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (mPassword.length() < 6) {
            mPasswordView.setError(getString(R.string.register_password_too_short));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mEmail) || !mEmail.contains("@")) {
            // this is a legacy bug causing the username to be incorrect
            // we need to try to use from the user profile
            EntityUser user = CHCApplication.getInstance(this).getUser();
            if (user != null)
                mEmail = user.getUsername();
            else {
                finish();
                return;
            }
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (focusView != null)
                focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            startLogin();
        }
    }

    private void startLogin() {
        mPresenter.signInUser(mEmail, mPassword, getCHCApplication().getRegId());
    }

    private void showProgress(final boolean show) {
        if (mProgress != null) {
            try {
                mProgress.dismiss();
            } catch (Exception e) {}
        }
        if (show) {
            mProgress = ProgressDialog.show(this, null, getString(R.string.network_wait), true, false);
        }
    }

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, ReauthenticateActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

    @Override
    public CHCApplication getCHCApplication() {
        return CHCApplication.getInstance(this);
    }

    @Override
    public void onUserSignInFailed() {
        showProgress(false);
        Toast.makeText(getCHCApplication(), getString(R.string.on_user_signin_failed), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUserSignedIn(Role role) {
        showProgress(false);

        ConversationActivity.start(this);
    }
}
