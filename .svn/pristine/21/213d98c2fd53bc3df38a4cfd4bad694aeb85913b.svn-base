package com.chc.found.models;

import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chc.found.config.Apis;
import com.chc.found.network.NetworkRequestsUtil;
import com.chc.found.utils.PinyinUtils;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "medicalcenterusers")
public class MedicalCenterUser extends EntityUser {

	@ForeignCollectionField(eager = true)
	Collection<DoctorUser> doctors;
	@DatabaseField
	private String address;
	@DatabaseField
	private String phone;
	@DatabaseField
	private String website;
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	private String[] galleryArray;
	
	
	public MedicalCenterUser() {/* Blank constructor for ORM use */}
	
	public MedicalCenterUser(JSONObject jo) throws JSONException {
		super(jo.optJSONObject("basicInfo"));
		JSONObject basicInfo = jo.optJSONObject("basicInfo");
		this.setFullname(basicInfo.optString("name"));
		this.setPinyinName(PinyinUtils.getPingYin(basicInfo.optString("name")));
		this.setAddress(
                basicInfo.optString("officeLocations")
                + ", " +
                basicInfo.optString("city")
                + ", " +
                basicInfo.optString("state")
                + " " +
                basicInfo.optString("zip"));

		this.setPhone(basicInfo.optString("cellPhone"));
		this.setWebsite(basicInfo.optString("website"));
        this.setDescription(basicInfo.optString("introduction"));
		
		ArrayList<DoctorUser> doctorList = new ArrayList<DoctorUser>();
		JSONArray doctorArray = jo.getJSONArray("subDoctors");
		for (int i = 0; i < doctorArray.length(); ++i) {
			DoctorUser doctor = new DoctorUser();
			JSONObject jsonObject = doctorArray.optJSONObject(i);
			doctor.setId(jsonObject.optString("id"));
			doctor.setProfileIconUrl(NetworkRequestsUtil.getIconImageUrlStringByPhoto(jsonObject.optString("photo")));
			doctor.setFullname(jsonObject.optString("name"));
            doctor.setPinyinName(PinyinUtils.getPingYin(jsonObject.optString("name")));
			doctor.setDescription(jsonObject.optString("introduction"));
			doctor.setCenter(this);
			doctorList.add(doctor);
		}

        this.doctors = doctorList;
		
		JSONArray galleryJSONArray = jo.getJSONArray("logoList");
		galleryArray = new String[galleryJSONArray.length()];
		for (int i = 0; i < galleryArray.length; ++i) {
			galleryArray[i] = galleryJSONArray.optString(i);
		}
	}
	
	public Collection<DoctorUser> getDoctors() {
		return doctors;
	}

	public void setDoctors(Collection<DoctorUser> doctors) {
		this.doctors = doctors;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String[] getGalleryArray() {
		return galleryArray;
	}

	public void setGalleryArray(String[] galleryArray) {
		this.galleryArray = galleryArray;
	}

	@Override
	public String toString() {
		return super.toString() + " (Medical Group) " + this.address;
	}
	
	
}
