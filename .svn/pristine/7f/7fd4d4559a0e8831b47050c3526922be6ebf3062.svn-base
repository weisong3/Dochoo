package com.chc.found.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chc.dochoo.CHCApplication;
import com.chc.dochoo.userlogin.UserLogInActivity;
import com.chc.found.FoundSettings;
import com.chc.found.presenters.RegisterPresenter;
import com.chc.found.views.IRegisterView;
import com.test.found.R;

public class RegistrationFragment extends Fragment implements IRegisterView {

	private ViewGroup firstNameWrapper;
	private ViewGroup lastNameWrapper;
	private ViewGroup centerNameWrapper;
	private EditText editTextFirstName;
	private EditText editTextLastName;
	private EditText editTextCenterName;
	private EditText editTextEmail;
	private EditText editTextPassword;
	private EditText editTextConfirmPassword;
	private Button buttonRegister;
	private ProgressDialog mProgress;
	
	private RegisterPresenter mRegisterPresenter;
	
	public OnRegisterSucceedListener mCallback;
	private boolean isDoctor;
	private boolean isSingle;

    // Container Activity must implement this interface
    public interface OnRegisterSucceedListener {
        public void onRegisterSucceed();
    }
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnRegisterSucceedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnRegisterSucceedListener");
        }
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		isDoctor = getArguments().getBoolean(UserLogInActivity.IS_DOCTOR_KEY);
		isSingle = getArguments().getBoolean(UserLogInActivity.IS_SINGLE_KEY);
		
		View view = inflater.inflate(R.layout.fragment_register_user, container, false);
		
		mRegisterPresenter = new RegisterPresenter(this);
		
		firstNameWrapper = (ViewGroup) view.findViewById(R.id.firstname_wrapper);
		lastNameWrapper = (ViewGroup) view.findViewById(R.id.lastname_wrapper);
		centerNameWrapper = (ViewGroup) view.findViewById(R.id.centername_wrapper);
		editTextCenterName = (EditText) view.findViewById(R.id.editText_centername);
		editTextFirstName = (EditText) view.findViewById(R.id.editText_firstname);
		editTextLastName = (EditText) view.findViewById(R.id.editText_lastname);
		editTextEmail = (EditText) view.findViewById(R.id.editText_email);
		editTextPassword = (EditText) view.findViewById(R.id.editText_password);
		editTextConfirmPassword = (EditText) view.findViewById(R.id.editText_confirm_pwd);
		buttonRegister = (Button) view.findViewById(R.id.button_register);
		
		if (isDoctor) {
			setSingleLayout(isSingle);
		} else {
			setSingleLayout(true);
		}
		
		buttonRegister.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onRegisterRequest();
			}
		});
		return view;
	}

	private void setSingleLayout(boolean isSingle) {
		if (isSingle) {
			centerNameWrapper.setVisibility(View.GONE);
			firstNameWrapper.setVisibility(View.VISIBLE);
			lastNameWrapper.setVisibility(View.VISIBLE);
		} else {
			centerNameWrapper.setVisibility(View.VISIBLE);
			firstNameWrapper.setVisibility(View.GONE);
			lastNameWrapper.setVisibility(View.GONE);
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
	
	protected void onRegisterRequest() {
		// validate input and proceed
		String centerName = editTextCenterName.getText().toString();
		String firstName = editTextFirstName.getText().toString();
		String lastName = editTextLastName.getText().toString();
		String email = editTextEmail.getText().toString();
		String password = editTextPassword.getText().toString();
		String passwordConfirm = editTextConfirmPassword.getText().toString();
		String title = "M.D.";
		
		displayProgress();
		
//		if (isDoctor) {
//			if (isSingle)
//				mRegisterPresenter.registerPysician(email, password, passwordConfirm, getCHCApplication().getRegId(), firstName, lastName, title);
//			else
//				mRegisterPresenter.registerMedicalGroup(email, password, passwordConfirm, getCHCApplication().getRegId(), centerName);
//		} else
//			mRegisterPresenter.registerPatient(email, password, passwordConfirm, getCHCApplication().getRegId(), firstName, lastName);
	}
	
	@Override
	public CHCApplication getCHCApplication() {
        FragmentActivity activity = getActivity();
        if (activity == null) return null;
        return CHCApplication.getInstance(activity);
	}

	@Override
	public void onRegisterFailed(String errorMsg) {
		dismissProgress();
		Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onRegisterSucceeded() {
		dismissProgress();
		Toast.makeText(getActivity(), getString(R.string.register_success), Toast.LENGTH_SHORT).show();
		FoundSettings.getInstance(getActivity()).setUserRole(getActivity(), !isDoctor);
		mCallback.onRegisterSucceed();
	}
}
