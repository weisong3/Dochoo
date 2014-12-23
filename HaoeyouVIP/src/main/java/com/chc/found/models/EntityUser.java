package com.chc.found.models;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.chc.found.network.NetworkRequestsUtil;
import com.j256.ormlite.field.DatabaseField;

public abstract class EntityUser {
	
	private static final String TYPE_MEDICAL_CENTER = "MEDICALGROUP";
	private static final String TYPE_DOCTOR = "PHYSICIAN";
	private static final String TYPE_PATIENT = "PATIENT";

	public static final String COLUMN_NAME_ID = "id";
	@DatabaseField(id = true)
	private String id;
	@DatabaseField
	private String fullname;
	@DatabaseField
	private String description;
	@DatabaseField
	private String profileIconUrl;
	@DatabaseField
	private String workingHours;
	@DatabaseField
	private String acceptedInsurances;
	@DatabaseField
	private int numUnread;
	@DatabaseField
	private long lastMsgTime;
	@DatabaseField
	private String pin;
    @DatabaseField
    private String username;
    @DatabaseField
    private String loginEmail;

    //for pinyin index
    @DatabaseField
    private String pinyinName;

	public EntityUser() {
		super();
	}
	
	public EntityUser(JSONObject jsonObject) throws JSONException {
		
		this.id = jsonObject.optString("id");
		this.profileIconUrl = NetworkRequestsUtil.getIconImageUrlString(getId());
		this.workingHours = jsonObject.optString("officeHours");
		this.acceptedInsurances = jsonObject.optString("acceptedInsurances");
		this.pin = jsonObject.optString("pin");
        this.username = jsonObject.optString("username");
        if (isStringNull(username)) username = "";
        loginEmail = jsonObject.optString("loginEmail");
        if (isStringNull(loginEmail)) loginEmail = "";
	}
	
	public static final EntityUser parseJson(String json) throws JSONException {
		JSONObject jo = new JSONObject(json);
		String type = jo.optString("type");
		if (StringUtils.isEmpty(type)) throw new IllegalArgumentException("empty type");
		EntityUser res;
		if (type.equals(TYPE_MEDICAL_CENTER)) {
			res = new MedicalCenterUser(jo.optJSONObject("data"));
		} else if (type.equals(TYPE_DOCTOR)) {
			res = new DoctorUser(jo.optJSONObject("data"));
		} else if (type.equals(TYPE_PATIENT)) {
			res = new PatientUser(jo.optJSONObject("data"));
		} else {
			throw new IllegalArgumentException("Wrong type: " + type);
		}
		
		return res;
	}
	 
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProfileIconUrl() {
		return profileIconUrl;
	}

	public void setProfileIconUrl(String profileIconUrl) {
		this.profileIconUrl = profileIconUrl;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getWorkingHours() {
		return workingHours;
	}

	public void setWorkingHours(String workingHours) {
		this.workingHours = workingHours;
	}

	public String getAcceptedInsurances() {
		return acceptedInsurances;
	}

	public void setAcceptedInsurances(String acceptedInsurances) {
		this.acceptedInsurances = acceptedInsurances;
	}

	public int getNumUnread() {
		return numUnread;
	}

	public void setNumUnread(int numUnread) {
		this.numUnread = numUnread;
	}

	public long getLastMsgTime() {
		return lastMsgTime;
	}

	public void setLastMsgTime(long lastMsgTime) {
		this.lastMsgTime = lastMsgTime;
	}

    public String getPinyinName() {
        return pinyinName;
    }

    public void setPinyinName(String pinyinName) {
        this.pinyinName = pinyinName;
    }

    @Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.id)
		.append(',')
		.append(this.fullname)
		.append(',')
		.append(this.description);
		
		return sb.toString();
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLoginEmail() {
        return loginEmail;
    }

    public void setLoginEmail(String loginEmail) {
        this.loginEmail = loginEmail;
    }

    private boolean isStringNull(String s) {
        return s == null || StringUtils.isBlank(s) || s.equals("null");
    }
}
