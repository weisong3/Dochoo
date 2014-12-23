package com.chc.dochoo.contacts;

import android.util.Log;

import com.chc.found.config.Apis;
import com.chc.found.models.ColleagueCenterUser;
import com.chc.found.models.ColleagueDoctorUser;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HenryW on 2/24/14.
 */
public class ColleagueResponse {
    private static final String TAG = ColleagueResponse.class.getSimpleName();
    private final String responseString;

    public ColleagueResponse(String responseString) {
        this.responseString = responseString;
    }

    public List<ColleagueCenterUser> parse() {
        if (responseString == null) return null;
        if (StringUtils.isBlank(responseString)) return new ArrayList<>(0);

        List<ColleagueCenterUser> groups;
        try {
            JSONArray ja = new JSONArray(responseString);
            groups = new ArrayList<>();
            ColleagueCenterUser lastGroup = null;
            for (int i = 0, size = ja.length(); i < size; i++) {
                JSONObject jo = ja.getJSONObject(i);
                String type = jo.optString("type");
                if (type.equals("MEDICALGROUP")) {
                    lastGroup = new ColleagueCenterUser(jo.getJSONObject("data"));
                    groups.add(lastGroup);
                } else if (type.equals("PHYSICIAN")) {
                    if (lastGroup != null) {
                        ColleagueDoctorUser doctorUser = new ColleagueDoctorUser(jo.getJSONObject("data"), lastGroup);
                        lastGroup.addColleagueDoctor(doctorUser);
                    } else {
                        // error
                        if (Apis.DEBUG)
                            Log.e(TAG, "physician following no medicalgroup\n" + responseString);
                        return null;
                    }
                } else {
                    // error
                    return null;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return groups;
    }
}
