package com.chc.found.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.chc.dochoo.conversations.GroupConversation;
import com.chc.dochoo.conversations.InstantMessage;
import com.chc.dochoo.conversations.NewsMessage;
import com.chc.dochoo.conversations.PrivateConversation;
import com.chc.found.config.Apis;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.test.found.R;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
	
	private static final String TAG = DatabaseHelper.class.getSimpleName();
	private static final String DATABASE_NAME = "found.db";
	/**
	 * DATABASE_VERSION 1
	 * <p/>
	 * DatabaseHelper created. Added InstantMessage and NewsMessage table
	 * <p/>
	 * DATABASE_VERSION 2
	 * <p/>
	 * Added DoctorUser and MedicalCenterUser table
	 * <p/>
	 * DATABASE_VERSION 15
	 * <p/>
	 * Added PatientUser table
	 * <p/>
	 * DATABASE_VERSION 20
	 * Added columns in DoctorUser and PatientUser table
	 * <p/>
	 * DATABASE_VERSION 24
	 * Added columns in PatientUser table
     * <p/>
     * DATABASE_VERSION 28
     * Added columns in DoctorUser table
     * <p/>
     * ver. 29
     * Added gender field in DoctorUser
     * <p/>
     * ver. 30
     * Added username field in all user table
     * <p/>
     * ver. 31
     * Added conversations table
     * <p/>
     * ver. 32
     * Added status field in InstantMessage table
     *<p/>
     * ver. 33
     * Added numOfUnread field in PrivateConversation and GroupConversation table
     * <p/>
     * ver. 34
     * Added mVersion field in instantmessage table
     * <p/>
     * ver. 35
     * Added loginEmail field in DoctorUser, MedicalCenterUser tables
     * <p/>
     * ver. 36
     * Added ColleagueDoctorUser and ColleagueCenterUser tables, and foreign field colleague_center_id
     * in DoctorUser table
     * <p/>
     * ver. 37
     * Added colleague_center_subdoctors_id field in DoctorUser table
     * <p/>
     * ver. 38
     * Fixed error caused in ver. 37 by adding colleague_center_subdoctors_id field in ColleagueDoctorUser table
     * <p/>
     * ver. 39
     * Add function of group chatting. Add table GroupMember, edit table GroupConversation
	 */
	private static final int DATABASE_VERSION = 40;

	private RuntimeExceptionDao<InstantMessage, Long> instantMsgDao;
	private RuntimeExceptionDao<NewsMessage, Long> newsMsgDao;
	private RuntimeExceptionDao<DoctorUser, Long> doctorUserDao;
	private RuntimeExceptionDao<MedicalCenterUser, Long> medicalCenterUserDao;
	private RuntimeExceptionDao<PatientUser, Long> patientUserDao;
    private RuntimeExceptionDao<PrivateConversation, Long> privateConversationDao;
    private RuntimeExceptionDao<GroupConversation, Long> groupConversationDao;
    private RuntimeExceptionDao<ColleagueCenterUser, Long> colleagueCenterUserDao;
    private RuntimeExceptionDao<ColleagueDoctorUser, Long> colleagueDoctorUserDao;

    private RuntimeExceptionDao<GroupMember, Long> groupMemberDao;

    public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
	}

	/**
	 * This is called when the database is first created. Usually you should call createTable statements here to create
	 * the tables that will store your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
		createTables();
	}

    private void createTables() {
        try {
            if (Apis.DEBUG) Log.i(TAG, "onCreate");
            TableUtils.createTable(connectionSource, InstantMessage.class);
            TableUtils.createTable(connectionSource, NewsMessage.class);
            TableUtils.createTable(connectionSource, MedicalCenterUser.class);
            TableUtils.createTable(connectionSource, ColleagueCenterUser.class);
            TableUtils.createTable(connectionSource, DoctorUser.class);
            TableUtils.createTable(connectionSource, ColleagueDoctorUser.class);
            TableUtils.createTable(connectionSource, PatientUser.class);
            TableUtils.createTable(connectionSource, PrivateConversation.class);

            TableUtils.createTable(connectionSource, GroupConversation.class);
            TableUtils.createTable(connectionSource, GroupMember.class);
        } catch (java.sql.SQLException e) {
            Log.e(TAG, "Can't create database ", e);
            throw new RuntimeException(e);
        }
    }

    /**
	 * Drop all table in database to delete all user data
	 */
	private final void dropAllTables() {
		try {
			if (Apis.DEBUG) Log.i(TAG, "dropping tables");
			TableUtils.dropTable(connectionSource, InstantMessage.class, true);
			TableUtils.dropTable(connectionSource, NewsMessage.class, true);
			TableUtils.dropTable(connectionSource, MedicalCenterUser.class, true);
			TableUtils.dropTable(connectionSource, DoctorUser.class, true);
            TableUtils.dropTable(connectionSource, PatientUser.class, true);
            TableUtils.dropTable(connectionSource, PrivateConversation.class, true);
            TableUtils.dropTable(connectionSource, GroupConversation.class, true);
            TableUtils.dropTable(connectionSource, ColleagueCenterUser.class, true);
            TableUtils.dropTable(connectionSource, ColleagueDoctorUser.class, true);

            TableUtils.dropTable(connectionSource, GroupMember.class, true);
		} catch (java.sql.SQLException e) {
			Log.e(TAG, "Can't drop all table ", e);
			throw new RuntimeException(e);
		}
	}
	
	public final void clearAllTables() {
		dropAllTables();
        createTables();
	}
	
	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
	 * the various data to match the new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase arg0, ConnectionSource arg1, int oldVer,
			int newVer) {
		
		if (oldVer < 26) {
			
			try {
				Dao<InstantMessage, ?> dao = getDao(InstantMessage.class);
			// change the table to add a new column named "age" 
				dao.executeRaw("ALTER TABLE `instantmessage` ADD COLUMN unread BOOLEAN DEFAULT 0;");
                oldVer = 27;
			} catch (SQLException e) {
				Log.e(TAG, "Can't upgrade from version 26", e);
			}
			
		}

        if (oldVer == 27) {

            try {
                Dao<DoctorUser, ?> dao = getDao(DoctorUser.class);
                // change the table to add a new column named "age"
                dao.executeRaw("ALTER TABLE `doctorusers` ADD COLUMN hospitalAffiliations VARCHAR;");
                oldVer = 28;
            } catch (SQLException e) {
                Log.e(TAG, "Can't upgrade from version 27", e);
            }

        }

        if (oldVer == 28) {

            try {
                Dao<DoctorUser, ?> dao = getDao(DoctorUser.class);
                // change the table to add a new column named "age"
                dao.executeRaw("ALTER TABLE `doctorusers` ADD COLUMN gender VARCHAR;");
                oldVer = 29;
            } catch (SQLException e) {
                Log.e(TAG, "Can't upgrade from version 28", e);
            }

        }

        if (oldVer == 29) {

            try {
                Dao<DoctorUser, ?> dao = getDao(DoctorUser.class);
                // change the table to add a new column named "age"
                dao.executeRaw("ALTER TABLE `doctorusers` ADD COLUMN username VARCHAR;");
                dao.executeRaw("ALTER TABLE `medicalcenterusers` ADD COLUMN username VARCHAR;");
                dao.executeRaw("ALTER TABLE `patientuser` ADD COLUMN username VARCHAR;");
                oldVer = 30;
            } catch (SQLException e) {
                Log.e(TAG, "Can't upgrade from version 29", e);
            }

        }

        if (oldVer == 30) {

            try {
                TableUtils.createTable(connectionSource, PrivateConversation.class);
                TableUtils.createTable(connectionSource, GroupConversation.class);
                oldVer = 31;
            } catch (SQLException e) {
                Log.e(TAG, "Can't upgrade from version 30", e);
            }

        }

        if (oldVer == 31) {
            try {
                Dao<InstantMessage, ?> dao = getDao(InstantMessage.class);
                dao.executeRaw("ALTER TABLE `instantmessage` ADD COLUMN status VARBINARY;");
                oldVer = 32;
            } catch (SQLException e) {
                Log.e(TAG, "Can't upgrade from version 31", e);
            }
        }

        if (oldVer == 32) {
            try {
                Dao<PrivateConversation, ?> dao = getDao(PrivateConversation.class);
                dao.executeRaw("ALTER TABLE `private_conversation` ADD COLUMN numOfUnread INTEGER DEFAULT 0;");
                Dao<GroupConversation, ?> dao2 = getDao(GroupConversation.class);
                dao2.executeRaw("ALTER TABLE `group_conversation` ADD COLUMN numOfUnread INTEGER DEFAULT 0;");
            } catch (SQLException e) {
                Log.e(TAG, "Can't upgrade from version 32", e);
            }
            oldVer = 33;
        }

        if (oldVer == 33) {
            try {
                Dao<InstantMessage, ?> dao = getDao(InstantMessage.class);
                dao.executeRaw("ALTER TABLE `instantmessage` ADD COLUMN mVersion INTEGER DEFAULT 0;");
                oldVer = 34;
            } catch (SQLException e) {
                Log.e(TAG, "Can't upgrade from version 33", e);
            }
        }

        if (oldVer == 34) {
            try {
                Dao<DoctorUser, ?> dao = getDao(DoctorUser.class);
                dao.executeRaw("ALTER TABLE `doctorusers` ADD COLUMN loginEmail VARCHAR;");
                dao.executeRaw("ALTER TABLE `medicalcenterusers` ADD COLUMN loginEmail VARCHAR;");

                oldVer = 35;
            } catch (SQLException e) {
                Log.e(TAG, "Can't upgrade from version 34", e);
            }
        }

        if (oldVer == 35) {
            try {
                TableUtils.createTable(connectionSource, ColleagueCenterUser.class);
                TableUtils.createTable(connectionSource, ColleagueDoctorUser.class);

                Dao<DoctorUser, ?> dao = getDao(DoctorUser.class);
                dao.executeRaw("ALTER TABLE `doctorusers` ADD COLUMN colleague_center_id VARCHAR;");

                oldVer = 36;
            } catch (SQLException e) {
                Log.e(TAG, "Can't upgrade from version 35", e);
            }
        }

        if (oldVer == 36) {
            try {
                Dao<DoctorUser, ?> dao = getDao(DoctorUser.class);
                dao.executeRaw("ALTER TABLE `doctorusers` ADD COLUMN colleague_center_subdoctors_id VARCHAR;");
                oldVer = 37;

                dao.executeRaw("ALTER TABLE `colleaguedoctorusers` ADD COLUMN colleague_center_subdoctors_id VARCHAR;");

            } catch (SQLException e) {
                Log.e(TAG, "Can't upgrade from version 36", e);
            }
        }

        if (oldVer == 37) {
            try {
                Dao<DoctorUser, ?> dao = getDao(DoctorUser.class);

                    // undo damage caused by previous update
                dao.executeRaw("ALTER TABLE `colleaguedoctorusers` ADD COLUMN colleague_center_subdoctors_id VARCHAR;");
            } catch (SQLException e) {
                Log.e(TAG, "Can't upgrade from version 37", e);
            }
            oldVer = 38;
        }
        if( oldVer == 38) {
            try{
                TableUtils.dropTable(connectionSource, GroupConversation.class, true);
                TableUtils.createTable(connectionSource, GroupMember.class);
                TableUtils.createTable(connectionSource, GroupConversation.class);
                Dao<InstantMessage, ?> dao = getDao(InstantMessage.class);
                dao.executeRaw("ALTER TABLE `instantmessage` ADD COLUMN group_id VARCHAR;");
                dao.executeRaw("ALTER TABLE `instantmessage` ADD COLUMN groupDisplyName VARCHAR;");
                /*Dao<GroupConversation,?> groupDao = getDao(GroupConversation.class);
                groupDao.executeRaw("ALTER TABLE `group_conversation` ADD COLUMN targetGroupchatId VARCHAR");
                groupDao.executeRaw("ALTER TABLE `group_conversation` ADD COLUMN leader VARCHAR");
                groupDao.executeRaw("ALTER TABLE `group_conversation` ADD COLUMN stillInGroup BOOLEAN");
                groupDao.executeRaw("ALTER TABLE `group_conversation` ADD COLUMN id VARCHAR");
                groupDao.executeRaw("ALTER TABLE `group_conversation` ADD COLUMN userId VARCHAR");
                groupDao.executeRaw("ALTER TABLE `group_conversation` ADD COLUMN lastMsgId VARCHAR");
                groupDao.executeRaw("ALTER TABLE `group_conversation` ADD COLUMN topic VARCHAR");
                groupDao.executeRaw("ALTER TABLE `group_conversation` ADD COLUMN numOfUnread INTEGER DEFAULT 0");*/
            }catch (SQLException e) {
                Log.e(TAG, "Can't upgrade from version 38", e);
            }
            oldVer = 39;
        }
        if( oldVer == 39) {
            try{
                Dao<DoctorUser, ?> dao = getDao(DoctorUser.class);
                // change the table to add a new column named "age"
                dao.executeRaw("ALTER TABLE `doctorusers` ADD COLUMN pinyinName VARCHAR;");
                dao.executeRaw("ALTER TABLE `medicalcenterusers` ADD COLUMN pinyinName VARCHAR;");
                dao.executeRaw("ALTER TABLE `patientuser` ADD COLUMN pinyinName VARCHAR;");
            }catch (SQLException e) {
                Log.e(TAG, "Can't upgrade from version 38", e);
            }
            oldVer = 40;
        }
        if (oldVer != newVer) {
            dropAllTables();
            onCreate(arg0, arg1);
        }
	}
	
	/**
	 * Returns the Database Access Object (DAO) for our {@link InstantMessage} class. It will create it or just give the cached
	 * value.
	 * @throws java.sql.SQLException 
	 */
	public RuntimeExceptionDao<InstantMessage, Long> getInstantMsgRuntimeDao() {
		if (instantMsgDao == null) {
			instantMsgDao = getRuntimeExceptionDao(InstantMessage.class);
		}
		return instantMsgDao;
	}
	
	/**
	 * Returns the Database Access Object (DAO) for our {@link NewsMessage} class. It will create it or just give the cached
	 * value.
	 * @throws java.sql.SQLException 
	 */
	public RuntimeExceptionDao<NewsMessage, Long> getNewsMsgRuntimeDao() {
		if (newsMsgDao == null) {
			newsMsgDao = getRuntimeExceptionDao(NewsMessage.class);
		}
		return newsMsgDao;
	}
	
	/**
	 * Returns the Database Access Object (DAO) for our {@link DoctorUser} class. It will create it or just give the cached
	 * value.
	 * @throws java.sql.SQLException 
	 */
	public RuntimeExceptionDao<DoctorUser, Long> getDoctorUserRuntimeDao() {
		if (doctorUserDao == null) {
			doctorUserDao = getRuntimeExceptionDao(DoctorUser.class);
		}
		return doctorUserDao;
	}
	
	/**
	 * Returns the Database Access Object (DAO) for our {@link MedicalCenterUser} class. It will create it or just give the cached
	 * value.
	 * @throws java.sql.SQLException 
	 */
	public RuntimeExceptionDao<MedicalCenterUser, Long> getMedicalCenterUserRuntimeDao() {
		if (medicalCenterUserDao == null) {
			medicalCenterUserDao = getRuntimeExceptionDao(MedicalCenterUser.class);
		}
		return medicalCenterUserDao;
	}
	
	/**
	 * Returns the Database Access Object (DAO) for our {@link PatientUser} class. It will create it or just give the cached
	 * value.
	 * @throws java.sql.SQLException 
	 */
	public RuntimeExceptionDao<PatientUser, Long> getPatientUserRuntimeDao() {
		if (patientUserDao == null) {
			patientUserDao = getRuntimeExceptionDao(PatientUser.class);
		}
		return patientUserDao;
	}

    public RuntimeExceptionDao<ColleagueCenterUser, Long> getColleagueCenterUserRuntimeDao() {
        if (colleagueCenterUserDao == null) {
            colleagueCenterUserDao = getRuntimeExceptionDao(ColleagueCenterUser.class);
        }
        return colleagueCenterUserDao;
    }

    public RuntimeExceptionDao<ColleagueDoctorUser, Long> getColleagueDoctorUserDao() {
        if (colleagueDoctorUserDao == null) {
            colleagueDoctorUserDao = getRuntimeExceptionDao(ColleagueDoctorUser.class);
        }
        return colleagueDoctorUserDao;
    }

    /**
     * @return Database Access Object (DAO) for our {@link com.chc.dochoo.conversations.GroupConversation} class.
     * @throws java.sql.SQLException
     */
    public RuntimeExceptionDao<GroupConversation, Long> getGroupConversationRuntimeDao() {
        if (groupConversationDao == null) {
            groupConversationDao = getRuntimeExceptionDao(GroupConversation.class);
        }
        return groupConversationDao;
    }

    /**
     * @return Database Access Object (DAO) for our {@link com.chc.dochoo.conversations.PrivateConversation} class.
     * @throws java.sql.SQLException
     */
    public RuntimeExceptionDao<PrivateConversation, Long> getPrivateConversationRuntimeDao() {
        if (privateConversationDao == null) {
            privateConversationDao = getRuntimeExceptionDao(PrivateConversation.class);
        }
        return privateConversationDao;
    }



    public RuntimeExceptionDao<GroupMember, Long> getGroupMemberDao(){
        if(groupMemberDao == null){
            groupMemberDao = getRuntimeExceptionDao(GroupMember.class);
        }
        return groupMemberDao;
    }
    /**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
		instantMsgDao = null;
		newsMsgDao = null;
	}

}
