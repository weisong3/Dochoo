//package com.chc.found.fragments;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//
//import com.test.found.R;
//
//public class RoleSelectionFragment extends Fragment {
//
//	private ViewGroup rolePatient;
//	private ViewGroup roleDoctor;
//
//	public OnRoleSelectedListener mCallback;
//
//    // Container Activity must implement this interface
//    public interface OnRoleSelectedListener {
//        public void onPatientSelected();
//        public void onDoctorSelected();
//    }
//
//	@Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//
//        // This makes sure that the container activity has implemented
//        // the callback interface. If not, it throws an exception
//        try {
//            mCallback = (OnRoleSelectedListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnRoleSelectedListener");
//        }
//    }
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		View view = inflater.inflate(R.layout.fragment_role_selection, container, false);
//		rolePatient = (ViewGroup) view.findViewById(R.id.role_selection_patient);
//		roleDoctor = (ViewGroup) view.findViewById(R.id.role_selection_doctor);
//
//		rolePatient.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				mCallback.onPatientSelected();
//			}
//		});
//
//		roleDoctor.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				mCallback.onDoctorSelected();
//			}
//		});
//		return view;
//	}
//
//}
