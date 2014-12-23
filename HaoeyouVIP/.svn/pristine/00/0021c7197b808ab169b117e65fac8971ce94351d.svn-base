package com.chc.dochoo.userlogin;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.chc.found.FoundSettings;
import com.test.found.R;

import org.apache.commons.lang.StringUtils;

public class PatientInfoFragment extends Fragment {
    private static final int DEFAULT_YEAR = 1970;
    private static final int DEFAULT_MONTH = 6;
    private static final int DEFAULT_DAY = 14;
    private static final String TAG = PatientInfoFragment.class.getSimpleName();
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

//    private String mParam1;
//    private String mParam2;

    private OnRegisterFragmentInteractionListener mListener;
    private EditText editTextDob;
    private Button btnSubmit;
    private EditText editFirstName;
    private EditText editLastName;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editConfirmPassword;
    private RadioGroup radioGroup;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CenterInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PatientInfoFragment newInstance() {
        PatientInfoFragment fragment = new PatientInfoFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public PatientInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_patient_info, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initViewHandle();

        setUpHandle();
    }

    private void setUpHandle() {
        setUpDobClick();
        setUpButton();
    }

    private void setUpButton() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonPressed();
            }
        });
    }

    private void setUpDobClick() {
        editTextDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = DEFAULT_YEAR;
                int monthZeroBased = DEFAULT_MONTH;
                int dayOfMonth = DEFAULT_DAY;

                String dateStr = editTextDob.getText().toString();
                if (StringUtils.isNotBlank(dateStr)) {
                    try {
                        String[] dates = dateStr.split("-");
                        monthZeroBased = Integer.parseInt(dates[0]) - 1;
                        dayOfMonth = Integer.parseInt(dates[1]);
                        year = Integer.parseInt(dates[2]);
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }

                new DatePickerDialog(getActivity(), 0,
                        new  DateSetListener(), year, monthZeroBased, dayOfMonth)
                        .show();
            }
        });
    }

    private void initViewHandle() {
        View root = getView();
        if (root == null) return;

        // edit texts
        editFirstName = (EditText) root.findViewById(R.id.firstname);
        editLastName = (EditText) root.findViewById(R.id.lastname);
        editTextDob = (EditText) root.findViewById(R.id.edittext_dob);
        editEmail = (EditText) root.findViewById(R.id.email);
        editPassword = (EditText) root.findViewById(R.id.password);
        editConfirmPassword = (EditText) root.findViewById(R.id.confirm_password);

        radioGroup = (RadioGroup) root.findViewById(R.id.gender_radio_group);

        btnSubmit = (Button) root.findViewById(R.id.buttonSubmit);

    }

    public void onButtonPressed() {
        if (mListener != null) {
            RegisterPatientInfo info = new RegisterPatientInfo();
            info.setFirstName(editFirstName.getText().toString());
            info.setLastName(editLastName.getText().toString());
            info.setDob(editTextDob.getText().toString());
            info.setEmail(editEmail.getText().toString());
            info.setPasswordUnhashed(editPassword.getText().toString());
            info.setPasswordConfirm(editConfirmPassword.getText().toString());
            info.setGender(getGender());

            mListener.onRegisterRequest(info);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnRegisterFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private class DateSetListener implements DatePickerDialog.OnDateSetListener{

        @Override
        public void onDateSet(DatePicker view,
                              int year, int monthOfYear,
                              int dayOfMonth) {

            editTextDob.setText(formatDate(year, monthOfYear, dayOfMonth));

        }

    }

    private String formatDate(int year, int monthOfYear, int dayOfMonth) {
        StringBuilder sb = new StringBuilder();
        if (monthOfYear < 9) {
            sb.append('0');
        }
        sb.append(monthOfYear+1)
        .append('-');
        if (dayOfMonth < 10) {
            sb.append('0');
        }
        sb.append(dayOfMonth)
        .append('-')
        .append(year);

        return sb.toString();
    }

    private String getGender() {

        int checkedRadioButton = radioGroup.getCheckedRadioButtonId();

        String radioButtonSelected = "";

        switch (checkedRadioButton) {
            case R.id.gender_female : radioButtonSelected = FoundSettings.TEXT_FEMALE;
                break;
            case R.id.gender_male : radioButtonSelected = FoundSettings.TEXT_MALE;
                break;
        }
        return radioButtonSelected;
    }
}
