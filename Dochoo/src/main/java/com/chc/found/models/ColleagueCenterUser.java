package com.chc.found.models;

import com.chc.found.utils.PinyinUtils;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by HenryW on 2/18/14.
 */
@DatabaseTable(tableName = "colleaguecenterusers")
public class ColleagueCenterUser extends MedicalCenterUser {

    @ForeignCollectionField(eager = true, foreignFieldName = "colleagueCenterUser")
    private Collection<ColleagueDoctorUser> colleagueDoctorUsers;
    @ForeignCollectionField(eager = true, foreignFieldName = "colleagueCenterSubdoctorUser")
    private Collection<DoctorUser> subDoctors;

    public ColleagueCenterUser() {}

    public ColleagueCenterUser(JSONObject jo) throws JSONException {
        super(jo);
        this.subDoctors = this.doctors;
        if (subDoctors != null) {
            for (DoctorUser du : subDoctors) {
                du.setColleagueCenterSubdoctorUser(this);
            }
        }
    }

    public List<ColleagueDoctorUser> getColleagueDoctors() {
        if (colleagueDoctorUsers == null) return new ArrayList<>(0);
        return new ArrayList<>(colleagueDoctorUsers);
    }

    public void addColleagueDoctor(ColleagueDoctorUser doctorUser) {
        if (colleagueDoctorUsers == null) colleagueDoctorUsers = new ArrayList<>();

        colleagueDoctorUsers.add(doctorUser);
    }

    @Override
    public Collection<DoctorUser> getDoctors() {
        return subDoctors;
    }

    @Override
    public void setDoctors(Collection<DoctorUser> doctors) {
        this.subDoctors = doctors;
    }

    public static ColleagueCenterUser clone(ColleagueCenterUser ccu){
        ColleagueCenterUser result = new ColleagueCenterUser();
        result.setAcceptedInsurances(ccu.getAcceptedInsurances());
        result.setAddress(ccu.getAddress());
        result.setDescription(ccu.getDescription());
        result.setFullname(ccu.getFullname());
        result.setPinyinName(PinyinUtils.getPingYin(ccu.getFullname()));
        result.setGalleryArray(ccu.getGalleryArray());
        result.setId(ccu.getId());
        result.setWorkingHours(ccu.getWorkingHours());
        result.setDoctors(ccu.getDoctors());
        result.setLastMsgTime(ccu.getLastMsgTime());
        result.setLoginEmail(ccu.getLoginEmail());
        result.setPhone(ccu.getPhone());
        result.setProfileIconUrl(ccu.getProfileIconUrl());
        result.setWebsite(ccu.getWebsite());
        result.setPin(ccu.getPin());
        result.setUsername(ccu.getUsername());
        //copy everything but colleague doctors
        return result;
    }
}
