package com.chc.found.models;

import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by HenryW on 2/18/14.
 */
@DatabaseTable(tableName = "colleaguedoctorusers")
public class ColleagueDoctorUser extends DoctorUser {

    public ColleagueDoctorUser() {}

    public ColleagueDoctorUser(JSONObject jsonObject, ColleagueCenterUser center) throws JSONException {
        super(jsonObject, center);
        setColleagueCenter(center);
    }

}
