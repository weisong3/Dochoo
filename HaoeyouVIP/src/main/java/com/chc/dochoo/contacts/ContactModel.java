package com.chc.dochoo.contacts;

import android.provider.ContactsContract;
import android.util.Log;

import com.chc.dochoo.conversations.ConversationModel;
import com.chc.found.models.ColleagueCenterUser;
import com.chc.found.models.ColleagueDoctorUser;
import com.chc.found.models.DatabaseHelper;
import com.chc.found.models.DoctorUser;
import com.chc.found.models.EntityModel;
import com.chc.found.models.EntityUser;
import com.chc.found.models.MedicalCenterUser;
import com.chc.found.models.PatientUser;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;

import org.apache.commons.lang.StringUtils;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Lance on 3/28/14.
 */
public abstract class ContactModel {
    private static String TAG = ContactModel.class.getSimpleName();

    public static void deleteContact(DatabaseHelper dbhelper, String entityId){
        EntityUser target = EntityModel.getEntityById(dbhelper,entityId);
        if(target instanceof PatientUser){
            RuntimeExceptionDao<PatientUser,Long> patientUserLongRuntimeExceptionDao = dbhelper.getPatientUserRuntimeDao();
            patientUserLongRuntimeExceptionDao.delete((PatientUser) target);
        }
        else if(target instanceof DoctorUser){
            RuntimeExceptionDao<DoctorUser,Long> doctorUserRuntimeDao = dbhelper.getDoctorUserRuntimeDao();
            doctorUserRuntimeDao.delete((DoctorUser) target);
        }
        else if(target instanceof MedicalCenterUser){
            RuntimeExceptionDao<MedicalCenterUser,Long> medicalCenterUserRuntimeDao = dbhelper.getMedicalCenterUserRuntimeDao();
            medicalCenterUserRuntimeDao.delete((MedicalCenterUser) target);
            RuntimeExceptionDao<DoctorUser,Long> doctorUserRuntimeDao = dbhelper.getDoctorUserRuntimeDao();
            for(DoctorUser du:((MedicalCenterUser) target).getDoctors())    doctorUserRuntimeDao.delete(du);

        }
        else{
            Log.e(TAG, "user type error on contact deleted");
        }
    }

    /**
     * clear database:when deleted by others, contact of deleter need to be cleared from current user's database
     * userList: contact list from server
     */
    public static List<EntityUser> clearPatientContactDatabase(DatabaseHelper dbhelper, List<EntityUser> userList){
        if(userList != null && userList.size() > 0){
            RuntimeExceptionDao<PatientUser, Long> patientUserRuntimeDao = dbhelper.getPatientUserRuntimeDao();
            List<PatientUser> dbPatients = patientUserRuntimeDao.queryForAll();//because patients in parameter:userList have already been updated into database,this list should be cached patients(need to delete) AND real patient contacts
            if(userList.size() != dbPatients.size()){
                List<String> dbPatientsIds = new ArrayList<>();
                List<String> netPatientsIds = new ArrayList<>();
                for(PatientUser pu:dbPatients){
                    dbPatientsIds.add(pu.getId());
                }
                for(EntityUser eu:userList) netPatientsIds.add(eu.getId());
                if(dbPatientsIds.removeAll(netPatientsIds)){
                    for(String s:dbPatientsIds){
                        deleteContact(dbhelper, s);
                        ConversationModel.deletePrivateConversationIfEmpty(dbhelper,s);
                    }
                }
                else{
                    Log.e(TAG,"Error with clear Patient database clear.");
                }
            }
        }
        return userList;
    }

    /**
     * similar function with clear patient database
     */
    public static List<EntityUser> clearProfessionalContactDatabase(DatabaseHelper dbhelper, List<EntityUser> userList){
        if(userList != null && userList.size() > 0){
            RuntimeExceptionDao<DoctorUser, Long> doctorUserRuntimeDao = dbhelper.getDoctorUserRuntimeDao();
            List<DoctorUser> dbDoctors = doctorUserRuntimeDao.queryForAll();
            RuntimeExceptionDao<MedicalCenterUser, Long> medicalCenterUserRuntimeDao = dbhelper.getMedicalCenterUserRuntimeDao();
            List<MedicalCenterUser> dbMCs = medicalCenterUserRuntimeDao.queryForAll();

            List<String> netProfIds = new ArrayList<>();
            List<String> deleteTargets = new ArrayList<>();
            for(EntityUser eu:userList) netProfIds.add(eu.getId());
            for(MedicalCenterUser mcu:dbMCs){
               if(netProfIds.contains(mcu.getId()))continue;
                deleteTargets.add(mcu.getId());
            }
            for(DoctorUser du:dbDoctors){
                if(!netProfIds.contains(du.getId()) && du.getCenter() == null){
                    deleteTargets.add(du.getId());
                }

            }
            for(String s:deleteTargets){
                deleteContact(dbhelper,s);
                ConversationModel.deletePrivateConversationIfEmpty(dbhelper,s);
            }
        }
        return userList;
    }

    public static List<EntityUser> getAllCachedContacts(DatabaseHelper dbhelper){
        List<EntityUser> result = new ArrayList<>();
        RuntimeExceptionDao<PatientUser, Long> patientUserRuntimeDao = dbhelper.getPatientUserRuntimeDao();
        result.addAll(patientUserRuntimeDao.queryForAll());
        RuntimeExceptionDao<DoctorUser, Long> doctorUserRuntimeDao = dbhelper.getDoctorUserRuntimeDao();
        for(DoctorUser du:doctorUserRuntimeDao.queryForAll()){
            if(du.getCenter() == null)result.add(du);
        }
        RuntimeExceptionDao<MedicalCenterUser, Long> medicalCenterUserRuntimeDao = dbhelper.getMedicalCenterUserRuntimeDao();
        result.addAll(medicalCenterUserRuntimeDao.queryForAll());
        RuntimeExceptionDao<ColleagueCenterUser, Long> colleagueCenterUserRuntimeDao = dbhelper.getColleagueCenterUserRuntimeDao();
        result.addAll(colleagueCenterUserRuntimeDao.queryForAll());
        RuntimeExceptionDao<ColleagueDoctorUser, Long> colleagueDoctorUserRuntimeDao = dbhelper.getColleagueDoctorUserDao();
        result.addAll(colleagueDoctorUserRuntimeDao.queryForAll());
        return result;
    }

    public static void sortContacts(List<EntityUser> user){
        Collections.sort(user, new Comparator<EntityUser>() {

            @Override
            public int compare(EntityUser lhs, EntityUser rhs) {
                String leftName = lhs.getFullname();
                String rightName = rhs.getFullname();
                if (leftName == null && rightName == null) return 0;
                if (leftName == null) return -1;
                if (rightName == null) return 1;
                return leftName.compareToIgnoreCase(rightName);
            }
        });

    }

    public static List<EntityUser> getRealContacts(DatabaseHelper dbhelper){
        List<EntityUser> result = new ArrayList<>();
        RuntimeExceptionDao<PatientUser, Long> patientUserRuntimeDao = dbhelper.getPatientUserRuntimeDao();
        result.addAll(patientUserRuntimeDao.queryForAll());
        RuntimeExceptionDao<DoctorUser, Long> doctorUserRuntimeDao = dbhelper.getDoctorUserRuntimeDao();
        QueryBuilder<DoctorUser, Long> queryBuilder = doctorUserRuntimeDao.queryBuilder();
        try{
            queryBuilder.where().isNull(DoctorUser.COLUMN_NAME_CENTER_ID);
            result.addAll(doctorUserRuntimeDao.query(queryBuilder.prepare()));
        }catch (SQLException e){
            for(DoctorUser du:doctorUserRuntimeDao.queryForAll()){
                if(du.getCenter() == null)result.add(du);
            }
        }

        RuntimeExceptionDao<MedicalCenterUser, Long> medicalCenterUserRuntimeDao = dbhelper.getMedicalCenterUserRuntimeDao();
        result.addAll(medicalCenterUserRuntimeDao.queryForAll());
        RuntimeExceptionDao<ColleagueCenterUser, Long> colleagueCenterUserRuntimeDao = dbhelper.getColleagueCenterUserRuntimeDao();
        result.addAll(colleagueCenterUserRuntimeDao.queryForAll());
        RuntimeExceptionDao<ColleagueDoctorUser, Long> colleagueDoctorUserRuntimeDao = dbhelper.getColleagueDoctorUserDao();
        result.addAll(colleagueDoctorUserRuntimeDao.queryForAll());
        return result;
    }
}
