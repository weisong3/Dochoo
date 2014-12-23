package com.chc.dochoo.profiles;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.chc.dochoo.CHCApplication;
import com.chc.found.models.EntityUser;
import com.chc.found.models.PatientUser;
import com.chc.found.utils.PinyinUtils;
import com.chc.found.utils.SharedPreferenceUtil;
import com.test.found.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PatientProfileFragment extends ProfileFragment {

	private static final String ICON_FILE_NAME = "icon.jpeg";
    private static final String TAG = PatientProfileFragment.class.getSimpleName();
    private ImageView profileImage;
	private EditText fname;
	private EditText lname;
	private EditText phone;

	private PatientUser mPatientUser;
	private RadioGroup sex;
	private RadioButton male;
	private RadioButton female;
	private EditText year;
	private EditText month;
	private EditText day;
	private EditText pEmail;
	private EditText sEmail;
	private EditText medicalConditions;
	private EditText surgery;
	private EditText medication;
	private EditText allergy;
	private EditText insurance;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_profile_patient, container, false);
		profileImage = (ImageView) view.findViewById(R.id.profile_patient_image);
		fname = (EditText) view.findViewById(R.id.profile_patient_firstname);
		lname = (EditText) view.findViewById(R.id.profile_patient_lastname);
		sex = (RadioGroup) view.findViewById(R.id.profile_patient_sex);
		male = (RadioButton) view.findViewById(R.id.profile_male);
		female = (RadioButton) view.findViewById(R.id.profile_female);
		year = (EditText) view.findViewById(R.id.profile_patient_year);
		month = (EditText) view.findViewById(R.id.profile_patient_month);
		day = (EditText) view.findViewById(R.id.profile_patient_day);
		pEmail = (EditText) view.findViewById(R.id.profile_patient_primary_email);
		sEmail = (EditText) view.findViewById(R.id.profile_patient_secondary_email);
		phone = (EditText) view.findViewById(R.id.profile_patient_phone);
		medicalConditions = (EditText) view.findViewById(R.id.profile_patient_medical_condition);
		surgery = (EditText) view.findViewById(R.id.profile_patient_surgeries);
		medication = (EditText) view.findViewById(R.id.profile_patient_medications);
		allergy = (EditText) view.findViewById(R.id.profile_patient_allergies);
		insurance = (EditText) view.findViewById(R.id.profile_patient_insurances);
		return view;
	}

    @Override
    protected Class<? extends EntityUser> getProfileTypeClass() {
        return PatientUser.class;
    }

    @Override
	public void setContent(EntityUser profile) {
        if (!(profile instanceof PatientUser)) {
            Log.e(TAG, "Expecting patient user at patient profile fragment");
            return;
        }
		mPatientUser = (PatientUser) profile;
		if (mPatientUser.getFirstName() != null && !mPatientUser.getFirstName().equals("null"))
			fname.setText(mPatientUser.getFirstName());
		else 
			fname.setText("");
		
		if (mPatientUser.getLastName() != null && !mPatientUser.getLastName().equals("null"))
			lname.setText(mPatientUser.getLastName());	
		else
			fname.setText("");
		
		if (mPatientUser.getPhone() != null && !mPatientUser.getPhone().equals("null"))
			phone.setText(mPatientUser.getPhone());
		else 
			phone.setText("");
		
		if (mPatientUser.getSex() != null && !mPatientUser.getSex().equals("null")) {
			if (mPatientUser.getSex().equals("male")) {
				male.setChecked(true);
				female.setChecked(false);
			} else {
				male.setChecked(false);
				female.setChecked(true);
			}
		} else {
			male.setChecked(false);
			female.setChecked(false);		
		}
		
		if (mPatientUser.getDob() != null) {
			Date dob = new Date(mPatientUser.getDob());
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);   
		    TimeZone tz = TimeZone.getTimeZone("PST"); 
		    sdf.setTimeZone(tz);
		    String[] dobString = sdf.format(dob).split("/");
		    month.setText(dobString[0]);
		    day.setText(dobString[1]);
			year.setText(dobString[2]);
		} else {
			month.setText("");
			day.setText("");
			year.setText("");
		}
		
		if (mPatientUser.getLoginEmail() != null && !mPatientUser.getLoginEmail().equals("null"))
			pEmail.setText(mPatientUser.getLoginEmail());
		else 
			pEmail.setText("");
		
		if (mPatientUser.getEmail() != null && !mPatientUser.getEmail().equals("null"))
			sEmail.setText(mPatientUser.getEmail());	
		else
			sEmail.setText("");
		
		if (mPatientUser.getMedicalConditions() != null && !mPatientUser.getMedicalConditions().equals("null"))
			medicalConditions.setText(mPatientUser.getMedicalConditions());
		else 
			medicalConditions.setText("");
		
		if (mPatientUser.getSurgeries() != null && !mPatientUser.getSurgeries().equals("null"))
			surgery.setText(mPatientUser.getSurgeries());
		else 
			surgery.setText("");
		
		if (mPatientUser.getMedications() != null && !mPatientUser.getMedications().equals("null"))
			medication.setText(mPatientUser.getMedications());	
		else
			medication.setText("");
		
		if (mPatientUser.getAllergies() != null && !mPatientUser.getAllergies().equals("null"))
			allergy.setText(mPatientUser.getAllergies());
		else 
			allergy.setText("");
		
		if (mPatientUser.getInsurances() != null && !mPatientUser.getInsurances().equals("null"))
			insurance.setText(mPatientUser.getInsurances());
		else 
			insurance.setText("");
		
		Bitmap image = SharedPreferenceUtil.readUserImage(getActivity());
		if (image != null) {
			profileImage.setImageBitmap(image);
		}
		profileImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getProfileCallback().onTakePicture();
			}
		});
	}

	@Override
	public void onPictureTaken(Bitmap image) {
		if (image != null) {
			profileImage.setImageBitmap(image);
			CHCApplication chcApplication = CHCApplication.getInstance(getActivity());
            displayProgress();
			getPresenter().uploadProfileIcon(image, chcApplication.getUserId(), chcApplication.getRegId(), ICON_FILE_NAME);
		}
	}

	@Override
	public void updateContent() {
        displayProgress();
		mPatientUser.setFirstName(fname.getText().toString());
		mPatientUser.setLastName(lname.getText().toString());
		mPatientUser.setFullname(fname.getText().toString() + " " + lname.getText().toString());
        mPatientUser.setPinyinName(PinyinUtils.getPingYin(fname.getText().toString() + " " + lname.getText().toString()));
		if (sex.getCheckedRadioButtonId() == R.id.profile_male) {
			mPatientUser.setSex("male");
		} else if (sex.getCheckedRadioButtonId() == R.id.profile_female) {
			mPatientUser.setSex("female");
		}
		if (month.getText().toString().length() > 0 && day.getText().toString().length() > 0 && year.getText().toString().length() > 0) {
			String dobString = month.getText().toString() + "/" + day.getText().toString() + "/" + year.getText().toString();
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
		    TimeZone tz = TimeZone.getTimeZone("PST");
		    sdf.setTimeZone(tz);
		    try {
				Date dob = sdf.parse(dobString);
				mPatientUser.setDob(dob.getTime());
			} catch (ParseException e) {
				Toast.makeText(getActivity(), getString(R.string.toast_error_birth_format), Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		}
		mPatientUser.setEmail(sEmail.getText().toString());
		mPatientUser.setPhone(phone.getText().toString());
		mPatientUser.setMedicalConditions(medicalConditions.getText().toString());
		mPatientUser.setSurgeries(surgery.getText().toString());
		mPatientUser.setMedications(medication.getText().toString());
		mPatientUser.setAllergies(allergy.getText().toString());
		mPatientUser.setInsurances(insurance.getText().toString());
		getPresenter().updateProfile(mPatientUser);
	}
	
}
