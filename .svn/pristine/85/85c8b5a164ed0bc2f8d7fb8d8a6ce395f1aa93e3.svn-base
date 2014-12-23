package com.chc.found.adapters;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.chc.found.fragments.DoctorDetailFragment;
import com.chc.found.models.DoctorUser;

public class DoctorDetailScrollableTabsAdapter extends
		FragmentStatePagerAdapter {

	private ArrayList<DoctorUser> doctorList;
	
	public DoctorDetailScrollableTabsAdapter(FragmentManager fm, ArrayList<DoctorUser> doctorList) {
		super(fm);
		this.doctorList = doctorList;
	}

	@Override
	public Fragment getItem(int index) {
		return DoctorDetailFragment.newInstance(index);
	}

	@Override
	public int getCount() {
		return this.doctorList.size();
	}

}
