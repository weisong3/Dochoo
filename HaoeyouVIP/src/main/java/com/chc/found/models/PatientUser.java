package com.chc.found.models;

import org.json.JSONException;
import org.json.JSONObject;

import com.chc.found.network.NetworkRequestsUtil;
import com.chc.found.utils.PinyinUtils;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "patientuser")
public class PatientUser extends EntityUser {

	@DatabaseField
	private String phone;
	@DatabaseField
	private String firstName;
	@DatabaseField
	private String lastName;
	@DatabaseField
	private Long dob;
	@DatabaseField
	private String sex;
	@DatabaseField
	private String email;
	@DatabaseField
	private String medicalConditions;
	@DatabaseField
	private String surgeries;
	@DatabaseField
	private String medications;
	@DatabaseField
	private String allergies;
	@DatabaseField
	private String insurances;

	public PatientUser() {/* Blank constructor for ORM use */}
	
	public PatientUser(JSONObject jo) throws JSONException {
		super(jo.optJSONObject("basicInfo"));
		jo = jo.optJSONObject("basicInfo");
		this.firstName = jo.optString("firstName");
		this.lastName = jo.optString("lastName");
		this.setFullname(jo.optString("firstName") + " " + jo.optString("lastName"));
        this.setPinyinName(PinyinUtils.getPingYin(jo.optString("firstName") + " " + jo.optString("lastName")));
		this.setPhone(jo.optString("cellPhone"));
		this.dob = jo.optLong("dob");
		this.sex = jo.optString("sex");
		this.email = jo.optString("email");
		this.medicalConditions = jo.optString("medicalConditions");
		this.surgeries = jo.optString("surgeries");
		this.medications = jo.optString("medications");
		this.allergies = jo.optString("allergies");
		this.insurances = jo.optString("insurances");
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		return super.toString() + " (Patient) " + phone;
	}

	public static PatientUser parseSimplePatient(JSONObject jo) {
		PatientUser user = new PatientUser();
		JSONObject realJO = jo.optJSONObject("basicInfo");
		user.setId(realJO.optString("id"));
		user.setFullname(realJO.optString("firstName") + " " + realJO.optString("lastName"));
        user.setPinyinName(PinyinUtils.getPingYin(realJO.optString("firstName") + " " + realJO.optString("lastName")));
        user.setAllergies(realJO.optString("allergies"));
        user.setInsurances(realJO.optString("insurances"));
        user.setMedicalConditions(realJO.optString("medicalConditions"));
        user.setSurgeries(realJO.optString("surgeries"));
        user.setMedications(realJO.optString("medications"));
        user.setSex(realJO.optString("sex"));
        user.setDob(realJO.optLong("dob"));
        user.setPhone(realJO.optString("cellPhone"));
		user.setProfileIconUrl(NetworkRequestsUtil.getIconImageUrlString(user.getId()));
		
		return user;
	}

	public Long getDob() {
		return dob;
	}

	public void setDob(Long dob) {
		this.dob = dob;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMedicalConditions() {
		return medicalConditions;
	}

	public void setMedicalConditions(String medicalConditions) {
		this.medicalConditions = medicalConditions;
	}

	public String getSurgeries() {
		return surgeries;
	}

	public void setSurgeries(String surgeries) {
		this.surgeries = surgeries;
	}

	public String getMedications() {
		return medications;
	}

	public void setMedications(String medications) {
		this.medications = medications;
	}

	public String getAllergies() {
		return allergies;
	}

	public void setAllergies(String allergies) {
		this.allergies = allergies;
	}

	public String getInsurances() {
		return insurances;
	}

	public void setInsurances(String insurances) {
		this.insurances = insurances;
	}

}
