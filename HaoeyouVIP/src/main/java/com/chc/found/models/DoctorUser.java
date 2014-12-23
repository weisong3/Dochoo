package com.chc.found.models;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chc.found.utils.PinyinUtils;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "doctorusers")
public class DoctorUser extends EntityUser {

    public static final String COLUMN_NAME_CENTER_ID = "center_id";
    public static final String COLUMN_NAME_COLLEAGUE_CENTER_ID = "colleague_center_id";
    private static final String COLUMN_NAME_COLLEAGUE_CENTER_SUBDOCTORS_ID = "colleague_center_subdoctors_id";

    @DatabaseField
    private String speciality;
    @DatabaseField
    private String languages;
    @DatabaseField
    private String degree;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private String[] locations;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private String[] phones;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private String[] logoList;
    @DatabaseField
    private String firstName;
    @DatabaseField
    private String lastName;
    @DatabaseField
    private String hospitalAffiliations;
    @DatabaseField
    private String gender;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = COLUMN_NAME_CENTER_ID)
    private transient MedicalCenterUser center;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = COLUMN_NAME_COLLEAGUE_CENTER_ID)
    private transient ColleagueCenterUser colleagueCenterUser;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = COLUMN_NAME_COLLEAGUE_CENTER_SUBDOCTORS_ID)
    private transient ColleagueCenterUser colleagueCenterSubdoctorUser;


    public DoctorUser() {/* Blank constructor for ORM use */}

    public DoctorUser(JSONObject jsonObject) throws JSONException {
        this(jsonObject, null);
    }

    public DoctorUser(JSONObject jsonObject, MedicalCenterUser center)
            throws JSONException {
        super(jsonObject.optJSONObject("basicInfo"));
        JSONObject basicInfo = jsonObject.optJSONObject("basicInfo");
        this.setFullname(buildFullName(basicInfo.optString("firstName"),
                basicInfo.optString("lastName")));
        this.setPinyinName(PinyinUtils.getPingYin(buildFullName(basicInfo.optString("firstName"),
                basicInfo.optString("lastName"))));
        this.firstName = basicInfo.optString("firstName");
        this.lastName = basicInfo.optString("lastName");
        this.speciality = basicInfo.optString("specialty");
        this.languages = basicInfo.optString("languagesSpoken");
        this.degree = basicInfo.optString("title");
        this.center = center;

        this.setDescription(basicInfo.optString("personalIntroduction"));
        this.hospitalAffiliations = basicInfo.optString("hospitalAffiliations");
        this.gender = basicInfo.optString("sex");
        if (gender.equals("null")) gender = "";

        JSONArray locations = jsonObject.optJSONArray("locations");
        if (locations != null) {
            String locationArray[] = new String[locations.length()];
            for (int i = 0; i < locations.length(); ++i) {
                locationArray[i] = locations.optString(i);
            }
            this.setLocations(locationArray);
        }
        JSONArray phones = jsonObject.optJSONArray("phones");
        if (phones != null) {
            String phoneArray[] = new String[phones.length()];
            for (int i = 0; i < phones.length(); ++i) {
                phoneArray[i] = phones.optString(i);
            }
            this.setPhones(phoneArray);
        }

        JSONArray logos = jsonObject.optJSONArray("logoList");
        if (logos != null) {
            String[] logoarray = new String[logos.length()];
            for (int i = 0; i < logos.length(); i++) {
                logoarray[i] = logos.optString(i);
            }
            this.setLogoList(logoarray);
        }
    }

    private String buildFullName(String firstName, String lastName) {
        String realFirst = (StringUtils.isBlank(firstName)|| firstName.equals("null"))?"":firstName;
        String realLast = (StringUtils.isBlank(lastName)|| lastName.equals("null"))?"":lastName;
        return realLast + " " + realFirst;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public MedicalCenterUser getCenter() {
        return center;
    }

    public void setCenter(MedicalCenterUser center) {
        this.center = center;
    }

    public String[] getLocations() {
        return locations;
    }

    public void setLocations(String[] locations) {
        this.locations = locations;
    }

    public String[] getPhones() {
        return phones;
    }

    public void setPhones(String[] phones) {
        this.phones = phones;
    }

    @Override
    public String toString() {
        return super.toString() + " (doctor) " + this.degree;
    }

    public String[] getLogoList() {
        return logoList;
    }

    public void setLogoList(String[] logoList) {
        this.logoList = logoList;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        this.setFullname(buildFullName(this.firstName, this.lastName));
        this.setPinyinName(PinyinUtils.getPingYin(buildFullName(this.firstName, this.lastName)));
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        this.setFullname(buildFullName(this.firstName, this.lastName));
        this.setPinyinName(PinyinUtils.getPingYin(buildFullName(this.firstName, this.lastName)));
    }

    public String getHospitalAffiliations() {
        return hospitalAffiliations;
    }

    public void setHospitalAffiliations(String hospitalAffiliations) {
        this.hospitalAffiliations = hospitalAffiliations;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public ColleagueCenterUser getColleagueCenter() {
        return colleagueCenterUser;
    }

    public void setColleagueCenter(ColleagueCenterUser center) {
        this.colleagueCenterUser = center;
    }

    public ColleagueCenterUser getColleagueCenterUser() {
        return colleagueCenterUser;
    }

    public ColleagueCenterUser getColleagueCenterSubdoctorUser() {
        return colleagueCenterSubdoctorUser;
    }

    public void setColleagueCenterSubdoctorUser(ColleagueCenterUser colleagueCenterSubdoctorUser) {
        this.colleagueCenterSubdoctorUser = colleagueCenterSubdoctorUser;
    }
}
