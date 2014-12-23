package com.chc.found;

import android.content.Context;

import com.chc.found.fragments.ActivitiesFragment;
import com.chc.found.fragments.DoctorHomepageFragment;
import com.chc.found.fragments.MedicalCenterFragment;
import com.chc.found.fragments.PatientInfoFragment;
import com.test.found.R;

/**
 * Stores information about which tabs should be displayed 
 * under the main acvitity
 * @author HenryW
 *
 */
public class FragmentTabInfo implements IFragmentTab {
	
	private static final int NUM_TABS = 2;
	private static final int PATIENT_NUM_INFO = 1;
	private final Class<?> cls;
	private final CharSequence title;
	
	/**
	 * Configures which classes are used for each tab
	 * @author HenryW
	 *
	 */
	public enum FragmentTabClassInfo {
        PATIENT(PatientInfoFragment.class,R.string.tab_title_patient,0),
		MEDICAL_CENTER(MedicalCenterFragment.class, R.string.tab_title_medical_center, 0),
		DOCTOR(DoctorHomepageFragment.class, R.string.tab_title_doctor_home, 0),
		NEWSPAGE(ActivitiesFragment.class, R.string.tab_title_news, 1);
//		MESSAGEPAGE(InstantMessageFragment.class, R.string.tab_title_messages, 2);

		private FragmentTabClassInfo(Class<?> cls, int titleResId, int index) {
			this.cls = cls;
			this.titleResId = titleResId;
			this.index = index;
		}
		
		public Class<?> getClassClass() {
			return this.cls;
		}
		
		public int getTitleResId() {
			return this.titleResId;
		}
		
		public int getIndex() {
			return index;
		}

		private final Class<?> cls;
		private final int titleResId;
		private final int index;
	}

	FragmentTabInfo(Class<?> cls, CharSequence title) {
		this.cls = cls;
		this.title = title;
	}
	
	@Override
	public String getClassName() {
		return cls.getName();
	}

	@Override
	public CharSequence getTabTitle() {
		return title;
	}

	public static FragmentTabInfo[] getTabInfoArr(Context context, boolean isCenter) {
		FragmentTabInfo[] res = new FragmentTabInfo[NUM_TABS];
		
		FragmentTabClassInfo info = isCenter ? FragmentTabClassInfo.MEDICAL_CENTER : FragmentTabClassInfo.DOCTOR;
		
		res[0] = new FragmentTabInfo(info.getClassClass(),
				context.getString(info.getTitleResId()));
		
		info = FragmentTabClassInfo.NEWSPAGE;
		res[1] = new FragmentTabInfo(info.getClassClass(),
				context.getString(info.getTitleResId()));
		
//		info = FragmentTabClassInfo.MESSAGEPAGE;
//		res[2] = new FragmentTabInfo(info.getClassClass(),
//				context.getString(info.getTitleResId()));
		
		return res;
	}

    //new function for patient fragment tab info
    public static FragmentTabInfo[] getTabInfoArr(Context context) {
        FragmentTabInfo[] res = new FragmentTabInfo[PATIENT_NUM_INFO];
        FragmentTabClassInfo info = FragmentTabClassInfo.PATIENT;
        res[0] = new FragmentTabInfo(info.getClassClass(),
                context.getString(info.getTitleResId()));
        return res;
    }
	public static FragmentTabClassInfo getNewsType() {
		return FragmentTabClassInfo.NEWSPAGE;
	}

	public static int getTabPosition(FragmentTabClassInfo s) {
        return s.getIndex();
	}

}
