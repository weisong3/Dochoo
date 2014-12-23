package com.chc.dochoo.profiles;

import com.chc.found.models.DoctorUser;
import com.chc.found.models.EntityUser;
import com.chc.found.models.MedicalCenterUser;
import com.chc.found.models.PatientUser;

public class AbstractProfilePresenterFactory {
	public static IProfilePresenterFactory getFactory(Class<? extends EntityUser> cls) {
		IProfilePresenterFactory f = null;
		if (cls == PatientUser.class) {
			f = new PatientProfilePresenterFactory();
		} else if (cls == DoctorUser.class) {
            f = new DoctorProfilePresenterFactory();
        } else if (cls == MedicalCenterUser.class) {
            f = new MedicalCenterProfilePresenterFactory();
        }
		
		return f;
	}
}
