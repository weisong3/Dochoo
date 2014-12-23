package com.chc.found.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.chc.found.FragmentTabInfo;
import com.chc.found.models.EntityUser;
import com.chc.found.models.MedicalCenterUser;
import com.chc.found.models.PatientUser;
import com.chc.found.views.ISwipeyTabsView;

public class PatientMainPagerAdapter extends FragmentPagerAdapter {

    public static final String KEY_ENTITY_ID = "bundle_entity_id";
    private final String entityId;

    public PatientMainPagerAdapter(Context context, FragmentManager fm, ISwipeyTabsView iview, EntityUser user, String entityId) {
		super(fm);

		this.mContext = context;
        if(user instanceof PatientUser){
            this.tabInfo = FragmentTabInfo.getTabInfoArr(context);
        }
        else{
            this.tabInfo = FragmentTabInfo.getTabInfoArr(context, user instanceof MedicalCenterUser);
        }
        this.entityId = entityId;
	}

	@Override
	public Fragment getItem(int position) {
        Bundle args = new Bundle();
        args.putString(KEY_ENTITY_ID, this.entityId);
		return Fragment.instantiate(mContext, tabInfo[position].getClassName(), args);
	}

	@Override
	public int getCount() {
		return tabInfo.length;
	}



	@Override
	public CharSequence getPageTitle(int position) {
		return tabInfo[position].getTabTitle();
	}



	private final Context mContext;
	private FragmentTabInfo[] tabInfo;

}