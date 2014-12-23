package com.chc.dochoo.userlogin;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.test.found.R;

public class CenterInfoFragment extends Fragment {

    private OnRegisterFragmentInteractionListener mListener;
    private EditText editCenterName;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editConfirmPassword;
    private Button btnSubmit;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CenterInfoFragment.
     */
    public static CenterInfoFragment newInstance() {
        CenterInfoFragment fragment = new CenterInfoFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public CenterInfoFragment() {
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
        return inflater.inflate(R.layout.fragment_center_info, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initViewHandle();

        setUpHandle();
    }

    private void setUpHandle() {
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

    private void initViewHandle() {
        View root = getView();
        if (root == null) return;

        // edit texts
        editCenterName = (EditText) root.findViewById(R.id.center_name);
        editEmail = (EditText) root.findViewById(R.id.email);
        editPassword = (EditText) root.findViewById(R.id.password);
        editConfirmPassword = (EditText) root.findViewById(R.id.confirm_password);

        btnSubmit = (Button) root.findViewById(R.id.buttonSubmit);
    }

    public void onButtonPressed() {
        if (mListener != null) {
            RegisterCenterInfo info = new RegisterCenterInfo();
            info.setCenterName(editCenterName.getText().toString());
            info.setEmail(editEmail.getText().toString());
            info.setPasswordUnhashed(editPassword.getText().toString());
            info.setPasswordConfirm(editConfirmPassword.getText().toString());

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

}
