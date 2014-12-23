package com.chc.dochoo.userlogin;

import android.util.SparseArray;

import com.chc.found.models.DoctorUser;
import com.chc.found.models.MedicalCenterUser;
import com.chc.found.models.PatientUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HenryW on 1/8/14.
 */

public enum Role {
    DOCTOR(0, DoctorUser.class),
    CENTER(1, MedicalCenterUser.class),
    PATIENT(2, PatientUser.class);

    private static final SparseArray<Role> map = new SparseArray<Role>();
    private static final Map<Class<?>, Role> clsMap = new HashMap<Class<?>, Role>();
    private static final ArrayList<String> stringRole = new ArrayList<>(Arrays.asList("PHYSICIAN","MEDICALGROUP", "GENERALUSER"));
    static {
        for (Role r : values()) {
            map.put(r.index, r);
            clsMap.put(r.cls, r);
        }
    }


    Role(int index, Class<?> cls) {
        this.index = index;
        this.cls = cls;
    }

    public static Role getRole(String role){
        return getRole(stringRole.indexOf(role));
    }
    public static Role getRole(int index) {
        return map.get(index);
    }

    public static Role getRole(Class<?> cls) {
        return clsMap.get(cls);
    }

    public int getIndex() {
        return index;
    }

    public Class<?> getCls() {
        return cls;
    }

    private final int index;
    private final Class<?> cls;
}
