package com.chc.dochoo.profiles;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.chc.dochoo.CHCApplication;
import com.chc.found.models.DoctorUser;
import com.chc.found.models.EntityUser;
import com.chc.found.models.MedicalCenterUser;
import com.chc.found.utils.PinyinUtils;
import com.chc.found.utils.SharedPreferenceUtil;
import com.test.found.R;

import org.apache.commons.lang.StringUtils;

/**
 * Created by HenryW on 1/9/14.
 */
public class CenterProfileFragment extends ProfileFragment {

    private static final String ICON_FILE_NAME = "profile_icon.jpg";
    private static final String TAG = CenterProfileFragment.class.getSimpleName();
    private ImageView headIv;

    /* edit buttons */
    private ImageButton btnEditCenterName;
    private ImageButton btnEditInsurance;
    private ImageButton btnEditIntro;
    private ImageButton btnEditHours;

    /* edit texts */
    private EditText editCenterName;
    private EditText editInsurance;
    private EditText editIntro;
    private EditText editHours;

    private MedicalCenterUser profile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_center, container, false);
        assert view != null;
        headIv = (ImageView) view.findViewById(R.id.profile_head);

        btnEditCenterName = (ImageButton) view.findViewById(R.id.profile_btn_edit_center_name);
        btnEditInsurance = (ImageButton) view.findViewById(R.id.profile_btn_edit_insurances);
        btnEditIntro = (ImageButton) view.findViewById(R.id.profile_btn_edit_intro);
        btnEditHours = (ImageButton) view.findViewById(R.id.profile_btn_edit_hours);

        editCenterName = (EditText) view.findViewById(R.id.profile_edittext_center_name);
        editInsurance = (EditText) view.findViewById(R.id.profile_edittext_insurance);
        editIntro = (EditText) view.findViewById(R.id.profile_edittext_intro);
        editHours = (EditText) view.findViewById(R.id.profile_edittext_hours);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bitmap userImage = SharedPreferenceUtil.readUserImage(getActivity());
        if (userImage != null)
            headIv.setImageBitmap(userImage);
        else
            headIv.setImageResource(R.drawable.default_head);

        View.OnClickListener listener = new DoctorProfileOnClickListener();
        btnEditCenterName.setOnClickListener(listener);
        btnEditInsurance.setOnClickListener(listener);
        btnEditIntro.setOnClickListener(listener);
        btnEditHours.setOnClickListener(listener);

        headIv.setOnClickListener(listener);
    }

    @Override
    protected Class<? extends EntityUser> getProfileTypeClass() {
        return DoctorUser.class;
    }

    @Override
    public void setContent(EntityUser entity) {
        if (!(entity instanceof MedicalCenterUser)) return;
        this.profile = (MedicalCenterUser) entity;

        editCenterName.setText(sanitizeString(profile.getFullname()));
        editInsurance.setText(sanitizeString(profile.getAcceptedInsurances()));
        editIntro.setText(sanitizeString(profile.getDescription()));
        editHours.setText(sanitizeString(profile.getWorkingHours()));
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
        if (this.profile == null) {
            onUpdateProfileFailed();
            return;
        }
        displayProgress();

        this.profile.setFullname(editCenterName.getText().toString());
        this.profile.setPinyinName(PinyinUtils.getPingYin(editCenterName.getText().toString()));
        this.profile.setDescription(editIntro.getText().toString());
        this.profile.setWorkingHours(editHours.getText().toString());
        this.profile.setAcceptedInsurances(editInsurance.getText().toString());

        getPresenter().updateProfile(this.profile);
    }

    private class DoctorProfileOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.profile_btn_edit_center_name) {
                showKeyboardFor(editCenterName);
            } else if (id == R.id.profile_btn_edit_insurances) {
                showKeyboardFor(editInsurance);
            } else if (id == R.id.profile_btn_edit_intro) {
                showKeyboardFor(editIntro);
            } else if (id == R.id.profile_btn_edit_hours) {
                showKeyboardFor(editHours);
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
