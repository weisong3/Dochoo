package com.chc.found.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chc.found.DoctorDetailScrollableTabsActivity;
import com.chc.found.models.DoctorUser;
import com.chcgp.hpad.util.download.ImageDownloader;
import com.test.found.R;

public class DoctorDetailFragment extends Fragment {

	private static final String KEY_INDEX = "index";

	private ImageView photo;
	private TextView name;
	private TextView specialty;
	private TextView occupation;
	private TextView insurance;
	private TextView phone;
	private TextView resume;
	private ViewGroup resumeWrapper;
	private ViewGroup phoneWrapper;
	private TextView pin;
	
	public static DoctorDetailFragment newInstance(int index) {
		DoctorDetailFragment f = new DoctorDetailFragment();
		Bundle args = new Bundle();
		
		args.putInt(KEY_INDEX, index);
		
		f.setArguments(args);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_doctor_detail, container, false);
		photo = (ImageView) root.findViewById(R.id.doctor_detail_photo);
		name = (TextView) root.findViewById(R.id.doctor_detail_name);
		specialty = (TextView) root.findViewById(R.id.doctor_detail_specialty);
		occupation = (TextView) root.findViewById(R.id.doctor_detail_occupation);
		insurance = (TextView) root.findViewById(R.id.doctor_detail_insurance);
		phone = (TextView) root.findViewById(R.id.doctor_detail_phone);
		pin = (TextView) root.findViewById(R.id.doctor_detail_pin);
		phoneWrapper = (ViewGroup) root.findViewById(R.id.doctor_detail_phone_wrapper);
		resume = (TextView) root.findViewById(R.id.doctor_detail_resume);
		resumeWrapper = (ViewGroup) root.findViewById(R.id.doctor_detail_resume_wrapper);
		
		return root;
	}

	@Override
	public void onResume() {
		super.onResume();
		int index = this.getArguments().getInt(KEY_INDEX);
		DoctorUser doctor = ((DoctorDetailScrollableTabsActivity) getActivity()).getDoctorList().get(index);
		if (doctor.getProfileIconUrl() != null && !doctor.getProfileIconUrl().equals("null")) {
			ImageDownloader.getInstance().download(doctor.getProfileIconUrl(), photo, getResources(), R.drawable.doc_default);
		} else {
			photo.setVisibility(View.GONE);
		}
		if (doctor.getFullname() != null && !doctor.getFullname().equals("null")) {
			name.setText(doctor.getFullname());			
		} else {
			name.setVisibility(View.GONE);
		}
		if (doctor.getSpeciality() != null && !doctor.getSpeciality().equals("null")) {
			specialty.setText(doctor.getSpeciality());			
		} else {
			specialty.setVisibility(View.GONE);
		}
		if (doctor.getDegree() != null && !doctor.getDegree().equals("null")) {
			occupation.setText(doctor.getDegree());			
		} else {
			occupation.setVisibility(View.GONE);
		}
		if (doctor.getAcceptedInsurances() != null && !doctor.getAcceptedInsurances().equals("null")) {
			insurance.setText(doctor.getAcceptedInsurances());			
		}  else {
			insurance.setVisibility(View.GONE);
		}
		if (doctor.getPhones() != null && doctor.getPhones().length > 0 && !doctor.getPhones().equals("null")) {
			phone.setText(doctor.getPhones()[0]);
		} else {
			phoneWrapper.setVisibility(View.GONE);
		}
		if (doctor.getDescription() != null && !doctor.getDescription().equals("null")) {
			resume.setText(doctor.getDescription());			
		} else {
			resumeWrapper.setVisibility(View.GONE);
		}
		if (doctor.getPin() != null && !doctor.getPin().equals("null")) {
			pin.setText(doctor.getPin());
		} else {
			pin.setVisibility(View.GONE);
		}
	}
	
	

}
