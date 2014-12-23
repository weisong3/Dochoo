package com.chc.dochoo.profiles;

import android.graphics.Bitmap;

import com.chc.found.models.EntityUser;

public interface IProfilePresenter {
	void updateProfile(EntityUser profile);
	void uploadProfileIcon(Bitmap bitmap, String userid, String pushid, String filename);
}
