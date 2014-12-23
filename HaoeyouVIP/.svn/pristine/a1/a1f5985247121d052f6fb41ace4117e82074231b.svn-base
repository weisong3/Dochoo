package com.chc.found.presenters;

import android.os.AsyncTask;
import android.util.Log;

import com.chc.dochoo.splash.SplashActivity;
import com.chc.dochoo.userlogin.RegisterCenterInfo;
import com.chc.dochoo.userlogin.RegisterPatientInfo;
import com.chc.dochoo.userlogin.RegisterPhysicianInfo;
import com.chc.dochoo.userlogin.Role;
import com.chc.exceptions.InternalErrorException;
import com.chc.dochoo.CHCApplication;
import com.chc.found.FoundSettings;
import com.chc.found.config.Apis;
import com.chc.found.models.EntityUser;
import com.chc.found.network.NetworkRequestsUtil;
import com.chc.found.views.IRegisterView;
import com.chcgp.hpad.util.network.RestClientUtils;
import com.test.found.R;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;

public class RegisterPresenter extends AbstractAccountPresenter {
	private static final String TAG = "RegisterPresenter";
	
	private IRegisterView mView;
	
	public RegisterPresenter(IRegisterView mView) {
		super();
		this.mView = mView;
	}

    public void registerPatient(RegisterPatientInfo info, String pushId, String activateCode) {
        boolean isValidInput = checkPatientField(
                info.getEmail(),
                info.getPasswordUnhashed(),
                info.getPasswordConfirm(),
                info.getFirstName(),
                info.getLastName(),
                info.getGender(),
                info.getDob());
        if (isValidInput)
            registerUser(
                    info.getEmail(),
                    info.getPasswordUnhashed(),
                    pushId,
                    info.getFirstName(),
                    info.getLastName(),
                    "",
                    "",
                    Apis.PARAM_TYPE_STR_PATIENT,
                    info.getGender(),
                    info.getDob(),
                    activateCode
            );
    }
	
	public boolean checkPatientField(String email, String password, String passwordConfirm, String firstname, String lastname,
                                      String gender, String dob) {
		boolean result = true;
		
		CHCApplication chcApplication = mView.getCHCApplication();
		if (StringUtils.isBlank(firstname)
				|| StringUtils.isBlank(lastname)
				|| StringUtils.isBlank(email)
				|| StringUtils.isBlank(password)
				|| StringUtils.isBlank(passwordConfirm)
                || StringUtils.isBlank(gender)
                || StringUtils.isBlank(dob)) {
			
			mView.onRegisterFailed(chcApplication.getString(R.string.register_all_fields_required));
			result = false;
			
		} else if (!password.equals(passwordConfirm)) {
			
			mView.onRegisterFailed(chcApplication.getString(R.string.register_unmatched_pwd));
			result = false;
			
		} else if (password.length() < MIN_PSWD_LENGTH) {
			
			mView.onRegisterFailed(chcApplication.getString(R.string.register_password_too_short));
			result = false;
			
		} else if (!new EmailValidator().validate(email)) {
			mView.onRegisterFailed(chcApplication.getString(R.string.register_invalid_email_address));
			result = false;
		}
		
		return result;
	}

	public void registerPhysician(RegisterPhysicianInfo info, String pushId, String activateCode) {
		/*boolean isValid = checkPhysicianField(
                info.getEmail(),
                info.getPasswordUnhashed(),
                info.getPasswordConfirm(),
                info.getFirstName(),
                info.getLastName(),
                info.getDegree(),
                info.getGender()
        );
		if (isValid)*/
			registerUser(
                    info.getEmail(),
                    info.getPasswordUnhashed(),
                    pushId,
                    info.getFirstName(),
                    info.getLastName(),
                    "",
                    info.getDegree(),
                    Apis.PARAM_TYPE_STR_DOCTOR,
                    info.getGender(),
                    "",
                    activateCode
            );
	}

	public boolean checkPhysicianField(
            String email,
            String password,
            String passwordConfirm,
            String firstname,
            String lastname,
            String title,
            String gender
    ) {
		boolean result = true;
		
		CHCApplication chcApplication = mView.getCHCApplication();
		if (StringUtils.isBlank(firstname)
				|| StringUtils.isBlank(lastname)
				|| StringUtils.isBlank(email)
				|| StringUtils.isBlank(password)
				|| StringUtils.isBlank(passwordConfirm)
				|| StringUtils.isBlank(title)
                || StringUtils.isBlank(gender)) {
			
			mView.onRegisterFailed(chcApplication.getString(R.string.register_all_fields_required));
			result = false;
			
		} else if (!password.equals(passwordConfirm)) {
			
			mView.onRegisterFailed(chcApplication.getString(R.string.register_unmatched_pwd));
			result = false;
			
		} else if (password.length() < MIN_PSWD_LENGTH) {
			
			mView.onRegisterFailed(chcApplication.getString(R.string.register_password_too_short));
			result = false;
			
		} else if (!new EmailValidator().validate(email)) {
			mView.onRegisterFailed(chcApplication.getString(R.string.register_invalid_email_address));
			result = false;
		}
		
		return result;
	}

    public void registerMedicalGroup(RegisterCenterInfo info, String pushId, String activateCode) {
        /*boolean isValid = checkMedicalGroupField(
                info.getEmail(),
                info.getPasswordUnhashed(),
                info.getPasswordConfirm(),
                info.getCenterName()
        );
        if (isValid)*/
            registerUser(
                    info.getEmail(),
                    info.getPasswordUnhashed(),
                    pushId,
                    "",
                    "",
                    info.getCenterName(),
                    "",
                    Apis.PARAM_TYPE_STR_MEDICAL_GROUP,
                    "",
                    "",
                    activateCode
            );
    }

	public boolean checkMedicalGroupField(String email, String password, String passwordConfirm, String centerName) {
		boolean result = true;
		
		CHCApplication chcApplication = mView.getCHCApplication();
		if (StringUtils.isBlank(centerName)
				|| StringUtils.isBlank(email)
				|| StringUtils.isBlank(password)
				|| StringUtils.isBlank(passwordConfirm)) {
			
			mView.onRegisterFailed(chcApplication.getString(R.string.register_all_fields_required));
			result = false;
			
		} else if (!password.equals(passwordConfirm)) {
			
			mView.onRegisterFailed(chcApplication.getString(R.string.register_unmatched_pwd));
			result = false;
			
		} else if (password.length() < MIN_PSWD_LENGTH) {
			
			mView.onRegisterFailed(chcApplication.getString(R.string.register_password_too_short));
			result = false;
			
		} else if (!new EmailValidator().validate(email)) {
			mView.onRegisterFailed(chcApplication.getString(R.string.register_invalid_email_address));
			result = false;
		}
		
		return result;
	}

	/**
	 * 
	 * @param email
	 * @param password
	 * @param pushId
	 * @param firstname
	 * @param lastname
	 * @param centerName
	 * @param physicianDegree
	 * @param role
	 */
	private void registerUser(
			
			String email,
			String password,
			String pushId,
			String firstname,
			String lastname,
			String centerName,
			String physicianDegree,
			String role,
            String gender,
            String dob,
            String activateCode
			
			) {

		
		new RegisterUserTask().execute(
				email, 
				RestClientUtils.encodePassword(password),
				pushId,
				firstname,
				lastname,
				centerName,
				physicianDegree,
				role,
                gender,
                dob,
                activateCode
				);
		
	}
	
	private class RegisterUserTask extends AsyncTask<String, Void, RegisterResult> {

		private String username;
		private String fullname;

		@Override
		protected RegisterResult doInBackground(String... params) {
			RegisterResult result;
			try {
				username = params[1];
				if (params[7].equals(Apis.PARAM_TYPE_STR_MEDICAL_GROUP)) {
					fullname = params[5];
				} else {
					fullname = params[3] + " " + params[4];
				}
				String response = NetworkRequestsUtil.postNewUser(params);
				if (StringUtils.isBlank(response)) {
					result = new RegisterResult(null, ResponseType.OTHER);
				} else {
					result = new RegisterResult(response, ResponseType.SUCCESS);
					result.typedJsonData = NetworkRequestsUtil.getEntityByIdOrPin(result.userId);
				}
			} catch (InternalErrorException e) {
				switch (e.getErrorCode()) {
				case 666:
					result = new RegisterResult(null, ResponseType.DUPLICATE_USERNAME);
					break;
				default:
					result = new RegisterResult(null, ResponseType.OTHER);
					Log.e(TAG, e.getMessage(), e);
				}
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
				result = new RegisterResult(null, ResponseType.OTHER);
			}
			return result;
		}

		@Override
		protected void onPostExecute(RegisterResult result) {
			super.onPostExecute(result);
			
			if (result == null) {
				onRegisterFailed(new RegisterResult(null, ResponseType.OTHER));
			} else if (result.type == ResponseType.SUCCESS) {
				onRegisterSucceeded(result, username, fullname);
			} else {
				onRegisterFailed(result);
			}
		}
		
	}
	
	private static enum ResponseType {
		SUCCESS(0),
		DUPLICATE_USERNAME(R.string.error_msg_duplicate_username),
		OTHER(R.string.error_msg_wrong_register_other);
		
		private ResponseType(int errorMsgId) {
			this.errorMsgId = errorMsgId;
		}
		
		public int getErrorMsgId() {
			return errorMsgId;
		}

		private final int errorMsgId;
	}
	
	private static class RegisterResult {
		String userId;
		ResponseType type;
		String typedJsonData;
		
		public RegisterResult(String resultStr, ResponseType type) {
			super();
			this.userId = resultStr;
			this.type = type;
		}
		
	}

	public void onRegisterFailed(RegisterResult result) {
		mView.onRegisterFailed(mView.getCHCApplication().getString((result.type.getErrorMsgId())));
	}

	public void onRegisterSucceeded(RegisterResult result, String username, String fullname) {
		if (StringUtils.isBlank(result.userId) || StringUtils.isBlank(username)
                || result == null || fullname == null || mView == null) {
			if (Apis.DEBUG) Log.e(TAG, "blank user id or username");
			onRegisterFailed(new RegisterResult(null, ResponseType.OTHER));
			return;
		}
		CHCApplication application = mView.getCHCApplication();
        application.getHelper().clearAllTables();
        try {
            EntityUser entityUser = EntityUser.parseJson(result.typedJsonData);
            if (entityUser != null)
            FoundSettings.getInstance(application).setUserRole(application,
                    Role.getRole(entityUser.getClass()) == Role.PATIENT);
            preSignInOrRegisterSucceeded(application, System.currentTimeMillis());

            writeAccountInfo(application, result.userId, username, fullname, result.typedJsonData);

            // load user profile updates from server
            AbstractAccountPresenter.loadUserProfile(application, result.userId);

            mView.onRegisterSucceeded();
        } catch (JSONException e) {
            mView.onRegisterHalfSucceed();
        }


		
	}
}
