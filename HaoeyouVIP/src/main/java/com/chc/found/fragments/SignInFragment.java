package com.chc.found.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.chc.dochoo.CHCApplication;
import com.chc.dochoo.userlogin.GeneralAgreementActivity;
import com.chc.dochoo.userlogin.RawStringUtil;
import com.chc.dochoo.userlogin.Role;
import com.chc.dochoo.userlogin.UserLogInActivity;
import com.chc.found.FoundSettings;
import com.chc.found.presenters.LoginPresenter;
import com.chc.found.views.ILogInView;
import com.test.found.R;

import org.apache.commons.lang.StringUtils;

public class SignInFragment extends Fragment implements ILogInView {
    private static final int REQUESTCODE = 100;

    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewNewUser;
    private TextView textViewForgotPswd;
    private Button buttonLogin;
    private ProgressDialog mProgress;

    private LoginPresenter mLoginPresenter;

    public OnNewUserClickedListener mCallback;
    private boolean agreementSigned;

    // Container Activity must implement this interface
    public interface OnNewUserClickedListener {
        public void onNewUserClicked();

        public void onLoginSucceed();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        editTextEmail = (EditText) root.findViewById(R.id.edittext_login_email);
        editTextPassword = (EditText) root.findViewById(R.id.edittext_login_password);

        // clickables
        textViewNewUser = (TextView) root.findViewById(R.id.textView_login_newuser);
        textViewForgotPswd = (TextView) root.findViewById(R.id.textView_login_forgot_password);
        buttonLogin = (Button) root.findViewById(R.id.buttonSubmit);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLoginPresenter = new LoginPresenter(this);

        SignInFragmentOnClickListener clickListener = new SignInFragmentOnClickListener();
        buttonLogin.setOnClickListener(clickListener);
        textViewNewUser.setOnClickListener(clickListener);
        textViewForgotPswd.setOnClickListener(clickListener);

        editTextPassword.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    onLoginRequest();
                    return true;
                }
                return false;
            }
        });
    }

    public void onLoginRequest() {
        String username = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        IBinder windowToken = editTextPassword.getWindowToken();
        if (windowToken != null)
            imm.hideSoftInputFromWindow(windowToken, 0);

        if (isValidInputs(username, password)) {
            /*if (!agreementSigned)
                startAgreementActivity();
            else {*/
                onAgreed();
            //}
        } else {
            onInvalidInput();
        }
    }

    private void startAgreementActivity() {
        String content = RawStringUtil.getFromRawFileAsString(getResources(), R.raw.term_of_service);
        String acknowledgement = getString(R.string.checkbox_agree_disclaimer);

        Intent startingIntent = GeneralAgreementActivity.getStartingIntent(getActivity(), content, acknowledgement);

        startActivityForResult(startingIntent, REQUESTCODE);

        getActivity().overridePendingTransition(R.anim.push_right_in_fast, R.anim.push_left_out_fast);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUESTCODE && resultCode == Activity.RESULT_OK) {
            onAgreed();
        } else {
            onDisagreed();
        }
    }

    private void onDisagreed() {
        Toast.makeText(getActivity(), R.string.must_agree, Toast.LENGTH_LONG).show();
    }

    private void onAgreed() {
        agreementSigned = true;
        String username = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            displayProgress();

            mLoginPresenter.signInUser(username, password, getCHCApplication().getRegId());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnNewUserClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnNewUserClickedListener");
        }
    }

    @Override
    public CHCApplication getCHCApplication() {
        FragmentActivity activity = getActivity();
        if (activity == null) return null;
        return CHCApplication.getInstance(activity);
    }

    private class SignInFragmentOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.textView_login_newuser) {

                mCallback.onNewUserClicked();
            } else if (id == R.id.textView_login_forgot_password) {
                handleForgotPassword();
            } else if (id == R.id.buttonSubmit) {
                onLoginRequest();
            }
        }
    }

    private void displayProgress() {
        if (mProgress == null) mProgress = ProgressDialog.show(
                getActivity(),
                null,
                getString(R.string.network_wait),
                true,
                false);
        else mProgress.show();
    }

    private void dismissProgress() {
        if (mProgress != null && mProgress.isShowing()) mProgress.dismiss();
    }

    private void onInvalidInput() {
        // TODO display error info
        Toast.makeText(getCHCApplication(), getString(R.string.login_invalid_input), Toast.LENGTH_LONG).show();
    }

    private boolean isValidInputs(String username, String password) {
        return mLoginPresenter.isValidInput(username, password);
    }

    public void handleForgotPassword() {
        Toast.makeText(getActivity(), getString(R.string.forgot_password), Toast.LENGTH_SHORT).show();
    }

    public void startRegisterActivity() {
        UserLogInActivity.startLogInActivity(getActivity());
    }

    @Override
    public void onUserSignedIn(Role role) {
        dismissProgress();
        Toast.makeText(getCHCApplication(), getString(R.string.on_user_signed_in), Toast.LENGTH_LONG).show();
        alreadySignedIn(role);
    }

    @Override
    public void onUserSignInFailed() {
        dismissProgress();
        Toast.makeText(getCHCApplication(), getString(R.string.on_user_signin_failed), Toast.LENGTH_LONG).show();
    }

    private void alreadySignedIn(Role role) {
        FoundSettings.getInstance(getActivity()).setUserRole(getActivity(), role == Role.PATIENT);
        mCallback.onLoginSucceed();
    }
}
