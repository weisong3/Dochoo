package com.chc.found.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chc.dochoo.CHCApplication;
import com.chc.found.adapters.PatientMainPagerAdapter;
import com.chc.found.models.AddEntityState;
import com.chc.found.models.EntityUser;
import com.chc.found.models.PatientUser;
import com.chc.found.presenters.EntityPresenter;
import com.chc.found.views.IEntityView;
import com.chcgp.hpad.util.download.ImageDownloader;
import com.test.found.R;

import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Lance on 2/28/14.
 */
public class PatientInfoFragment extends Fragment implements IEntityView {
    private ImageView patientPhoto;
    private TextView patientInfoName;
    private TextView patientInfoDob;
    private TextView patientInfoGender;
    private TextView patientInfoPhone;
    private TextView patientInfoConditions;
    private TextView patientInfoMedications;
    private TextView patientInfoAllergies;
    private TextView patientInfoInsurances;
    private String entityId;
    private EntityPresenter presenter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View patientViewFragment = inflater.inflate(
                R.layout.activity_patient_view, container, false);
        patientPhoto = (ImageView)patientViewFragment.findViewById(R.id.activity_patient_view_profilehead);
        patientInfoName = (TextView)patientViewFragment.findViewById(R.id.activity_patient_view_name);
        patientInfoDob = (TextView)patientViewFragment.findViewById(R.id.activity_patient_view_dob);
        patientInfoGender = (TextView)patientViewFragment.findViewById(R.id.activity_patient_view_gender);
        patientInfoPhone = (TextView)patientViewFragment.findViewById(R.id.activity_patient_view_phone);
        patientInfoConditions = (TextView)patientViewFragment.findViewById(R.id.activity_patient_view_conditions);
        patientInfoMedications = (TextView)patientViewFragment.findViewById(R.id.activity_patient_view_medications);
        patientInfoAllergies = (TextView)patientViewFragment.findViewById(R.id.activity_patient_view_allergies);
        patientInfoInsurances = (TextView)patientViewFragment.findViewById(R.id.activity_patient_view_insurances);
        presenter = new EntityPresenter(this);
        return patientViewFragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments == null) return; // error, no argument
        entityId = arguments.getString(PatientMainPagerAdapter.KEY_ENTITY_ID);
        if (entityId == null) return; // error, no entity id
        if (StringUtils.isBlank(entityId))
            return;
    }

    @Override
    public void onResume() {
        super.onResume();
        setContent((PatientUser)presenter.getEntityById(entityId));
    }

    private void setContent(final PatientUser patient){
        if(patient == null || getActivity() == null)
            return;
        if(StringUtils.isNotBlank(patient.getProfileIconUrl())){
            ImageDownloader.getInstance().download(patient.getProfileIconUrl(),
                    patientPhoto, getResources(), R.drawable.default_head);
        }
        else{
            patientPhoto.setImageResource(R.drawable.default_head);
        }

        if(StringUtils.isNotBlank(patient.getFullname())){
            patientInfoName.setText(patient.getFullname());
        }
        else{
            patientInfoName.setVisibility(View.GONE);
        }

        try{
            if(patient.getDob() != null){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                patientInfoDob.setText(sdf.format(new Date(patient.getDob())));
            }
            else    patientInfoDob.setVisibility(View.GONE);
        }catch(Exception e){
            Log.e("PatientInfoFragment","Exception: " + e.getMessage());
            Log.w("PatientInfoFragment","Set patient dob to invisible");
            patientInfoDob.setVisibility(View.GONE);
        }

        if(StringUtils.isNotBlank(patient.getSex())){
            patientInfoGender.setText(StringUtils.equals("male",patient.getSex())?R.string.gender_male:R.string.gender_female);
        }
        else    patientInfoGender.setVisibility(View.GONE);

        if(StringUtils.isNotBlank(patient.getPhone())){
            patientInfoPhone.setText(patient.getPhone());
        }
        else    patientInfoPhone.setVisibility(View.GONE);

        if(StringUtils.isNotBlank(patient.getMedicalConditions())){
            patientInfoConditions.setText(patient.getMedicalConditions());
        }
        else    patientInfoConditions.setVisibility(View.GONE);

        if(StringUtils.isNotBlank(patient.getMedications())){
            patientInfoMedications.setText(patient.getMedications());
        }
        else    patientInfoMedications.setVisibility(View.GONE);

        if(StringUtils.isNotBlank(patient.getAllergies())){
            patientInfoAllergies.setText(patient.getAllergies());
        }
        else    patientInfoAllergies.setVisibility(View.GONE);

        if(StringUtils.isNotBlank(patient.getInsurances())){
            patientInfoInsurances.setText(patient.getInsurances());
        }
        else    patientInfoInsurances.setVisibility(View.GONE);
    }

    @Override
    public void onEntityLoaded(EntityUser user) {
        setContent((PatientUser)user);
    }

    @Override
    public void onEntityLoaded(List<EntityUser> user) {

    }

    @Override
    public void getEntityFailed(AddEntityState state) {

    }

    @Override
    public CHCApplication getCHCApplication() {
        FragmentActivity activity = getActivity();
        if (activity == null) return null;
        return CHCApplication.getInstance(activity);
    }
}
