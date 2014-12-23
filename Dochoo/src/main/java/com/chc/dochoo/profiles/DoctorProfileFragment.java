package com.chc.dochoo.profiles;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.chc.dochoo.CHCApplication;
import com.chc.found.models.DoctorUser;
import com.chc.found.models.EntityUser;
import com.chc.found.utils.SharedPreferenceUtil;
import com.test.found.R;

import org.apache.commons.lang.StringUtils;

/**
 * Created by HenryW on 1/2/14.
 */
public class DoctorProfileFragment extends ProfileFragment {

    private static final String ICON_FILE_NAME = "profile_icon.jpg";
    private static final String TAG = DoctorProfileFragment.class.getSimpleName();
    private ImageView headIv;
    private Spinner occupationSpinner;

    private View wrapper_degree_others;

    /* edit buttons */
    private ImageButton btnEditFirstName;
    private ImageButton btnEditLastName;
    private ImageButton btnEditSpecialty;
    private ImageButton btnEditLanguage;
    private ImageButton btnEditInsurance;
    private ImageButton btnEditIntro;
    private ImageButton btnEditHours;
    private ImageButton btnEditAffiliation;

    /* edit texts */
    private EditText editFirstName;
    private EditText editLastName;
    private EditText editDegreeOther;
    private EditText editSpecialty;
    private EditText editLanguage;
    private EditText editInsurance;
    private EditText editIntro;
    private EditText editHours;
    private EditText editAffiliation;
    private DoctorUser profile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_doctor, container, false);
        assert view != null;
        headIv = (ImageView) view.findViewById(R.id.profile_head);
        occupationSpinner = (Spinner) view.findViewById(R.id.profile_spinner_degree);
        wrapper_degree_others = view.findViewById(R.id.profile_degree_other_wrapper);

        btnEditFirstName = (ImageButton) view.findViewById(R.id.profile_btn_edit_firstname);
        btnEditLastName = (ImageButton) view.findViewById(R.id.profile_btn_edit_lastname);
        btnEditSpecialty = (ImageButton) view.findViewById(R.id.profile_btn_edit_specialty);
        btnEditLanguage = (ImageButton) view.findViewById(R.id.profile_btn_edit_languages);
        btnEditInsurance = (ImageButton) view.findViewById(R.id.profile_btn_edit_insurances);
        btnEditIntro = (ImageButton) view.findViewById(R.id.profile_btn_edit_intro);
        btnEditHours = (ImageButton) view.findViewById(R.id.profile_btn_edit_hours);
        btnEditAffiliation = (ImageButton) view.findViewById(R.id.profile_btn_edit_affiliation);

        editFirstName = (EditText) view.findViewById(R.id.profile_edittext_firstname);
        editLastName = (EditText) view.findViewById(R.id.profile_edittext_lastname);
        editDegreeOther = (EditText) view.findViewById(R.id.profile_degree_other_specify);
        editSpecialty = (EditText) view.findViewById(R.id.profile_edittext_specialty);
        editLanguage = (EditText) view.findViewById(R.id.profile_edittext_languages);
        editInsurance = (EditText) view.findViewById(R.id.profile_edittext_insurance);
        editIntro = (EditText) view.findViewById(R.id.profile_edittext_intro);
        editHours = (EditText) view.findViewById(R.id.profile_edittext_hours);
        editAffiliation = (EditText) view.findViewById(R.id.profile_edittext_affiliation);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
                if (pos == occupationSpinner.getCount()-1) {
//                    Toast.makeText(getActivity(), "Last item selected", Toast.LENGTH_SHORT).show();
                    wrapper_degree_others.setVisibility(View.VISIBLE);
                    showKeyboardFor(editDegreeOther);
                } else {
                    wrapper_degree_others.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Bitmap userImage = SharedPreferenceUtil.readUserImage(getActivity());
        if (userImage != null)
            headIv.setImageBitmap(userImage);
        else
            headIv.setImageResource(R.drawable.default_head);

        View.OnClickListener listener = new DoctorProfileOnClickListener();
        btnEditFirstName.setOnClickListener(listener);
        btnEditLastName.setOnClickListener(listener);
        btnEditSpecialty.setOnClickListener(listener);
        btnEditLanguage.setOnClickListener(listener);
        btnEditInsurance.setOnClickListener(listener);
        btnEditIntro.setOnClickListener(listener);
        btnEditHours.setOnClickListener(listener);
        btnEditAffiliation.setOnClickListener(listener);

        headIv.setOnClickListener(listener);
    }

    @Override
    protected Class<? extends EntityUser> getProfileTypeClass() {
        return DoctorUser.class;
    }

    @Override
    public void setContent(EntityUser entity) {
        if (entity instanceof DoctorUser == false) return;
        this.profile = (DoctorUser) entity;
        editFirstName.setText(sanitizeString(profile.getFirstName()));
        editLastName.setText(sanitizeString(profile.getLastName()));
        editDegreeOther.setText(sanitizeString(profile.getDegree()));
        editSpecialty.setText(sanitizeString(profile.getSpeciality()));
        editLanguage.setText(sanitizeString(profile.getLanguages()));
        editInsurance.setText(sanitizeString(profile.getAcceptedInsurances()));
        editIntro.setText(sanitizeString(profile.getDescription()));
        editHours.setText(sanitizeString(profile.getWorkingHours()));
        editAffiliation.setText(sanitizeString(profile.getHospitalAffiliations()));

        occupationSpinner.setSelection(getSpinnerItemPosition(occupationSpinner, sanitizeString(profile.getDegree())));

    }

    private int getSpinnerItemPosition(Spinner spinner, String string) {
        int result = 0;

        if (spinner != null && !string.isEmpty()) {
            String[] stringArray = getResources().getStringArray(R.array.degree_array);
            boolean found = false;
            for (int i = 0; i < stringArray.length; i++) {
                String s = stringArray[i];
                if (s.equals(string)) {
                    // found
                    result = i;
                    found = true;
                    break;
                }
            }
            if (!found) {
                result = stringArray.length - 1;
            }
        }

        return result;
    }

    private String sanitizeString(String s) {
        if (s == null || StringUtils.isBlank(s) || s.equals("null")) return "";
        return s;
    }

    @Override
    public void onPictureTaken(Bitmap pic) {
        if (pic != null) {
            this.headIv.setImageBitmap(pic);
            CHCApplication chcApplication = CHCApplication.getInstance(getActivity());
            displayProgress();
            getPresenter().uploadProfileIcon(
                    pic, chcApplication.getUserId(), chcApplication.getRegId(), ICON_FILE_NAME);
        }
    }

    @Override
    public void updateContent() {
        displayProgress();

        if (this.profile == null) {
            onUpdateProfileFailed();
            return;
        }

        this.profile.setFirstName(editFirstName.getText().toString());
        this.profile.setLastName(editLastName.getText().toString());
        this.profile.setDegree(getDegreeString(occupationSpinner, editDegreeOther));
        this.profile.setSpeciality(editSpecialty.getText().toString());
        this.profile.setLanguages(editLanguage.getText().toString());
        this.profile.setDescription(editIntro.getText().toString());
        this.profile.setWorkingHours(editHours.getText().toString());
        this.profile.setHospitalAffiliations(editAffiliation.getText().toString());
        this.profile.setAcceptedInsurances(editInsurance.getText().toString());

        getPresenter().updateProfile(this.profile);
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

    private class DoctorProfileOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.profile_btn_edit_firstname) {
                showKeyboardFor(editFirstName);
            } else if (id == R.id.profile_btn_edit_lastname) {
                showKeyboardFor(editLastName);
            } else if (id == R.id.profile_btn_edit_specialty) {
                showKeyboardFor(editSpecialty);
            } else if (id == R.id.profile_btn_edit_languages) {
                showKeyboardFor(editLanguage);
            } else if (id == R.id.profile_btn_edit_insurances) {
                showKeyboardFor(editInsurance);
            } else if (id == R.id.profile_btn_edit_intro) {
                showKeyboardFor(editIntro);
            } else if (id == R.id.profile_btn_edit_hours) {
                showKeyboardFor(editHours);
            } else if (id == R.id.profile_btn_edit_affiliation) {
                showKeyboardFor(editAffiliation);
            } else if (id == R.id.profile_head) {
                getProfileCallback().onTakePicture();
            }
        }
    }

    private void showKeyboardFor(EditText editText) {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

}
