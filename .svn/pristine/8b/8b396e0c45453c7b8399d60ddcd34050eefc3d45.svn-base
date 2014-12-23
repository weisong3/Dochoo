package com.chc.dochoo.userlogin;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.chc.found.FoundSettings;
import com.test.found.R;

public class PhysicianInfoFragment extends Fragment {
    private static final String TAG = PhysicianInfoFragment.class.getSimpleName();

    private OnRegisterFragmentInteractionListener mListener;
    private Spinner occupationSpinner;
    private EditText editDegreeOther;
    private EditText editFirstName;
    private EditText editLastName;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editConfirmPassword;
    private RadioGroup radioGroup;
    private TextView textViewDegreeOther;
    private Button btnSubmit;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CenterInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PhysicianInfoFragment newInstance() {
        PhysicianInfoFragment fragment = new PhysicianInfoFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public PhysicianInfoFragment() {
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
        return inflater.inflate(R.layout.fragment_physician_info, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initViewHandle();

        setUpHandle();
    }

    private void setUpHandle() {
        setUpSpinner();

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

    private void setUpSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.degree_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        assert occupationSpinner != null;
        occupationSpinner.setAdapter(adapter);
        occupationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
                Log.w(TAG, String.valueOf(occupationSpinner.getCount()));
                if (pos == occupationSpinner.getCount() - 1) {
//                    Toast.makeText(getActivity(), "Last item selected", Toast.LENGTH_SHORT).show();
                    setOtherSpecifyVisible(true);
                    showKeyboardFor(editDegreeOther);
                } else {
                    setOtherSpecifyVisible(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initViewHandle() {
        View root = getView();
        if (root == null) return;

        occupationSpinner = (Spinner) root.findViewById(R.id.register_spinner_degree);

        textViewDegreeOther = (TextView) root.findViewById(R.id.other_specify_left);

        // edit texts
        editFirstName = (EditText) root.findViewById(R.id.firstname);
        editLastName = (EditText) root.findViewById(R.id.lastname);
        editDegreeOther = (EditText) root.findViewById(R.id.other_specify_right);
        editEmail = (EditText) root.findViewById(R.id.email);
        editPassword = (EditText) root.findViewById(R.id.password);
        editConfirmPassword = (EditText) root.findViewById(R.id.confirm_password);

        radioGroup = (RadioGroup) root.findViewById(R.id.gender_radio_group);

        btnSubmit = (Button) root.findViewById(R.id.buttonSubmit);
    }

    private void setOtherSpecifyVisible(boolean b) {
        textViewDegreeOther.setVisibility(b ? View.VISIBLE : View.GONE);
        editDegreeOther.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    public void onButtonPressed() {
        if (mListener != null) {
            RegisterPhysicianInfo info = new RegisterPhysicianInfo();
            info.setFirstName(editFirstName.getText().toString());
            info.setLastName(editLastName.getText().toString());
            info.setDegree(getDegreeString(occupationSpinner, editDegreeOther));
            info.setEmail(editEmail.getText().toString());
            info.setPasswordUnhashed(editPassword.getText().toString());
            info.setPasswordConfirm(editConfirmPassword.getText().toString());
            info.setGender(getGender());

            mListener.onRegisterRequest(info);
        }
    }

    private String getDegreeString(Spinner occupationSpinner, EditText editDegreeOther) {
        assert occupationSpinner != null && editDegreeOther != null;
        int total = occupationSpinner.getCount();
        boolean isOthers = occupationSpinner.getSelectedItemPosition() == total-1;
        String result;
        if (isOthers) {
            result = editDegreeOther.getText().toString();
        } else {
            result = occupationSpinner.getSelectedItem().toString();
        }
        return result;
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

    private void showKeyboardFor(EditText editText) {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
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
