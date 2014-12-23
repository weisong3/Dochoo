package com.chc.found.models;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Entity;
import android.os.AsyncTask;
import android.util.Log;

import com.chc.dochoo.contacts.ColleagueResponse;
import com.chc.dochoo.contacts.ContactModel;
import com.chc.dochoo.conversations.ConversationModel;
import com.chc.dochoo.conversations.InstantMessage;
import com.chc.dochoo.conversations.InstantMessageModel;
import com.chc.dochoo.conversations.PrivateConversation;
import com.chc.exceptions.InternalErrorException;
import com.chc.found.config.Apis;
import com.chc.found.network.NetworkRequestsUtil;
import com.chc.found.presenters.IEntityPresenter;
import com.chc.found.utils.SharedPreferenceUtil;
import com.heartcenters.photoupload.dao.Doctor;
import com.heartcenters.photoupload.dao.Patient;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;

/**
 * Get entity user data from database
 * 
 * @author HenryW
 * 
 */
public class EntityModel {

    /**
     * write new data and erases old local colleague data
     * TODO: erase old data and associate records such as conversations
     * @param helper
     * @param colleagueList
     */
    public static void writeColleagueList(DatabaseHelper helper, List<ColleagueCenterUser> colleagueList) {
        if (helper == null || colleagueList == null) return;
        for (ColleagueCenterUser u : colleagueList) {
            createOrUpdate(helper, u);
        }
    }

    public static List<EntityUser> loadProRelationListOnWorkerThread(DatabaseHelper helper, String userId, String pushId) {
        List<EntityUser> result = new ArrayList<EntityUser>();
        Map<String, EntityUser> tempMap = new HashMap<String, EntityUser>();
        try {
            String response = NetworkRequestsUtil.getProRelationList(userId, pushId);
            if (StringUtils.isBlank(response)) {
                return null;
            }
            JSONArray ja = new JSONArray(response);
            for (int i = 0, size = ja.length(); i < size; i++) {
                String entityStr = NetworkRequestsUtil.getEntityByIdOrPin(ja.getString(i));
                // parse json
                EntityUser user;
                try {
                    user = EntityUser.parseJson(entityStr);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    continue;
                }

                if (user == null) continue;

                if (!tempMap.containsKey(user.getId()))
                    tempMap.put(user.getId(), user);

                // new user
                if (getEntityById(helper, user.getId()) == null) {
                    // does not have this user yet
//						user.setNumUnread(1);
                    user.setLastMsgTime(System.currentTimeMillis());
                }

                // anyway write to DB
                if (user instanceof DoctorUser) {
                    createOrUpdate(helper, (DoctorUser) user);
                } else if (user instanceof MedicalCenterUser) {
                    createOrUpdate(helper, (MedicalCenterUser) user);
                } else {
                    // patient user should not appear here
                    Log.e(TAG, "patient user in non-patient list");
                }

            }
        } catch (Exception e) {
            result = null; // on error, use null to indicate error
            tempMap.clear();
            Log.e(TAG, e.getMessage(), e);
        }
        if (result != null) {
            Set<Map.Entry<String, EntityUser>> entrySet = tempMap.entrySet();
            for (Map.Entry<String, EntityUser> entry : entrySet) {
                result.add(entry.getValue());
            }
            result = ContactModel.clearProfessionalContactDatabase(helper, result);
        }
        tempMap.clear();

        return result;
    }

    public static List<EntityUser> loadPatientListOnWorkerThread(DatabaseHelper helper, String userId, String pushId) {
        List<EntityUser> result = new ArrayList<EntityUser>();
        try {
            String response = NetworkRequestsUtil.getPatientList(userId, pushId); // TODO changing to V2 API
            if (StringUtils.isNotEmpty(response)) {
                JSONArray ja = new JSONArray(response);
                for (int i = 0, size = ja.length(); i < size; i++) {
                    result.add(PatientUser.parseSimplePatient(ja.optJSONObject(i))); // TODO change to normal patient view after V2 API
                }
            }

            for (EntityUser u : result) {
                createOrUpdateEntityUser(helper, u);
            }
            result = ContactModel.clearPatientContactDatabase(helper, result);
        } catch (Exception e) {
            result = null;
            Log.e(TAG, e.getMessage(), e);
        }
        return result;
    }

    public void loadColleagueListFromServer(final DatabaseHelper helper, String userId, String regId) {
        new LoadColleagueAsyncTask(helper, userId, regId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class AddRelationAsyncTask extends AsyncTask<Void, Void, EntityUser> {

        private final String initiatorIdOrPin, recipientIdOrPin, pushId;
		private final DatabaseHelper dbhelper;
		private AddEntityState state = AddEntityState.OTHER;
		
		

		public AddRelationAsyncTask(DatabaseHelper dbhelper, String initiatorIdOrPin, String recipientIdOrPin, String pushId) {
			super();
			this.dbhelper = dbhelper;
			this.initiatorIdOrPin = initiatorIdOrPin;
			this.recipientIdOrPin = recipientIdOrPin;
            this.pushId = pushId;
		}

		@Override
		protected EntityUser doInBackground(Void... params) {
			EntityUser result = null;
			
			try {
				
				String response = NetworkRequestsUtil.getAddRelation(initiatorIdOrPin, recipientIdOrPin);
				if (response.contains("success")) {
					// successfully added relation. now get the recipient info
					String recipientjson = NetworkRequestsUtil.getEntityByIdOrPin(recipientIdOrPin);
					if (StringUtils.isNotEmpty(recipientjson)) {
						// successfully get info
						// parse the info and return
						EntityUser user = EntityUser.parseJson(recipientjson);
						if (getEntityById(dbhelper, user.getId()) != null) {
							// already has user, duplicate
							state = AddEntityState.DUPLICATE;
							return null;
						}
						result = user;
						
					} else {
						// get info failed. 
						result = null;
					}
				} else if(response.contains("block")){
                    state = AddEntityState.BLOCKED;
                    result = null;
                }
                else {
					// failed.
					state = AddEntityState.WRONG_ID_PIN;
					result = null;
				}
				
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
				result = null;
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(EntityUser result) {
			if (result == null) onLoadNewEntityFailed(state);
			else {
				createOrUpdateEntityUser(dbhelper, result);
				onLoadNewEntitySuccess(result);
			}
		}
		
	}

    private class DeleteContactAsyncTask extends AsyncTask<String, Void, List<EntityUser>> {
        private DatabaseHelper dbhelper;

        public DeleteContactAsyncTask(DatabaseHelper dbhelper){
            this.dbhelper = dbhelper;
        }

        /**
         * params[0] userId
         * params[1] pushId
         * params[2] targetId
         * params[3] block  ONLY for block api
         * @return
         */
        @Override
        protected List<EntityUser> doInBackground(String... params) {
            List<EntityUser> result = new ArrayList<>();
            List<String> resultIndex = new ArrayList<>();
            List<PatientUser> cachedPatients = null;
            List<DoctorUser> cachedProContacts = null;
            EntityUser target = getEntityById(dbhelper,params[2]);
            if(target instanceof PatientUser){
                cachedPatients = dbhelper.getPatientUserRuntimeDao().queryForAll();
                for(PatientUser pu:cachedPatients) {
                    result.add(pu);
                    resultIndex.add(pu.getId());
                }
            }
            else if(target instanceof DoctorUser){
                cachedProContacts = dbhelper.getDoctorUserRuntimeDao().queryForAll();
                for(DoctorUser du:cachedProContacts) {
                    result.add(du);
                    resultIndex.add(du.getId());
                }
            }
            else{
                Log.e(TAG,"IMPOSSIBLE TARGET USER TYPE");
            }
            try{
                //same api with different params for block and delete
                String response = NetworkRequestsUtil.postDeleteContact(params);
                if(response.contains("success")){
                    // successfully delete target user from current user's contact list
                    result.remove(resultIndex.indexOf(params[2]));
                    ConversationModel.deletePrivateConversationIfEmpty(dbhelper, params[2]);
                    List<InstantMessage> msgsToDelete = InstantMessageModel.getInstantMessagesByEntityId(
                            this.dbhelper, params[2]);
                    InstantMessageModel.deleteMultimediaContentFromMsg(msgsToDelete);
                    InstantMessageModel.deleteMessages(dbhelper, msgsToDelete);
                    ContactModel.deleteContact(dbhelper,params[2]);
                }
            }catch(Exception e){
                Log.e(TAG,e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<EntityUser> result) {
            super.onPostExecute(result);
            if(result != null)  mPresenter.onEntityUserListLoaded(result);

            else mPresenter.onContactDeleteFailed();
        }
    }
	private class LoadPatientListAsyncTask extends AsyncTask<Void, Void, List<EntityUser>> {

		private DatabaseHelper dbhelper;
		private String userId;
		private String pushId;

		public LoadPatientListAsyncTask(DatabaseHelper dbhelper, String userId,
				String pushId) {
			this.dbhelper = dbhelper;
			this.userId = userId;
			this.pushId = pushId;
		}

		@Override
		protected List<EntityUser> doInBackground(Void... params) {
            return loadPatientListOnWorkerThread(dbhelper, userId, pushId);
		}

		@Override
		protected void onPostExecute(List<EntityUser> result) {
			super.onPostExecute(result);
			if (result == null) {
                mPresenter.onEntityUserListLoaded(result);
                return;
            }

			// after written to database, re-read to ensure order
			List<PatientUser> patientUserList = getPatientUserList(dbhelper);
			result.clear();
			
			for (PatientUser u : patientUserList) {
				result.add(u);
			}
			
			mPresenter.onEntityUserListLoaded(result);
		}
		
	}


    private class LoadProfessionalListAsyncTask extends AsyncTask<Void, Void, List<EntityUser>> {

        private DatabaseHelper dbhelper;
        private String userId;
        private String pushId;

        public LoadProfessionalListAsyncTask(DatabaseHelper dbhelper, String userId, String pushId) {
            this.dbhelper = dbhelper;
            this.userId = userId;
            this.pushId = pushId;
        }

        @Override
        protected List<EntityUser> doInBackground(Void... params) {
            return loadProfessionalListForPatientOnWorkerThread(dbhelper, userId, pushId);
        }

        @Override
        protected void onPostExecute(List<EntityUser> result) {
            mPresenter.onEntityUserListLoaded(result);
        }
    }

    public static List<EntityUser> loadProfessionalListForPatientOnWorkerThread(
            DatabaseHelper dbhelper, String userId, String pushId) {
        List<EntityUser> result = new ArrayList<EntityUser>();

        Map<String, EntityUser> tempMap = new HashMap<String, EntityUser>(); // because there may be duplicates on server
        try {
            String response = NetworkRequestsUtil.getProfessionalList(userId, pushId);
            JSONArray ja = new JSONArray(response);
            for (int i = 0, size = ja.length(); i < size; i++) {
                String entityStr = NetworkRequestsUtil.getEntityByIdOrPin(ja.getString(i));
                // parse json
                EntityUser user = null;
                try {
                    user = EntityUser.parseJson(entityStr);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    continue;
                }

                if (user == null) continue;

                if (!tempMap.containsKey(user.getId())) {
                    tempMap.put(user.getId(), user);
                }

                // anyway write to DB
                if (user instanceof DoctorUser) {
                    createOrUpdate(dbhelper, (DoctorUser) user);
                } else if (user instanceof MedicalCenterUser) {
                    createOrUpdate(dbhelper, (MedicalCenterUser) user);
                } else {
                    // patient user should not appear here
                    Log.e(TAG, "patient user in non-patient list");
                }
            }
        } catch (Exception e) {
            result = null; // on error, use null to indicate error
            Log.e(TAG, e.getMessage(), e);
        }

        if (result != null) {
            Set<Map.Entry<String, EntityUser>> entrySet = tempMap.entrySet();
            for (Map.Entry<String, EntityUser> entry : entrySet) {
                result.add(entry.getValue());
            }
            result = ContactModel.clearProfessionalContactDatabase(dbhelper, result);
        }
        tempMap.clear();
        return result;
    }

    private class LoadProfessionalRelationAsyncTask extends AsyncTask<Void, Void, List<EntityUser>> {
		
		private DatabaseHelper dbhelper;
		private String userId;
		private String pushId;

		public LoadProfessionalRelationAsyncTask(DatabaseHelper dbhelper, String userId, String pushId) {
			this.dbhelper = dbhelper;
			this.userId = userId;
			this.pushId = pushId;
		}

		@Override
		protected List<EntityUser> doInBackground(Void... params) {
			return loadProRelationListOnWorkerThread(dbhelper, userId, pushId);
		}

		@Override
		protected void onPostExecute(List<EntityUser> result) {
			mPresenter.onEntityUserListLoaded(result);
		}
	}

    private class LoadColleagueAsyncTask extends AsyncTask<Void, Void, List<EntityUser>> {

        private DatabaseHelper dbhelper;
        private String userId;
        private String pushId;

        public LoadColleagueAsyncTask(DatabaseHelper dbhelper, String userId, String pushId) {
            this.dbhelper = dbhelper;
            this.userId = userId;
            this.pushId = pushId;
        }

        @Override
        protected List<EntityUser> doInBackground(Void... params) {
            return loadColleagueListOnWorkerThread(dbhelper, userId, pushId);
        }

        @Override
        protected void onPostExecute(List<EntityUser> result) {
            mPresenter.onEntityUserListLoaded(result);
        }
    }

    public static List<EntityUser> loadColleagueListOnWorkerThread(DatabaseHelper dbhelper, String userId, String pushId) {
        try {
            String responseString = NetworkRequestsUtil.getColleagueList(userId, pushId);
            ColleagueResponse response = new ColleagueResponse(responseString);
            List<ColleagueCenterUser> colleagueList = response.parse();
            if (colleagueList != null) {
                EntityModel.writeColleagueList(dbhelper, colleagueList);
            }
            return toEntityUserList(colleagueList);
        } catch (Exception anyException) {
            Log.e(TAG, anyException.getMessage(), anyException);
            return null;
        }
    }


    public static List<EntityUser> toEntityUserList(List<ColleagueCenterUser> allColleagueCenterUsers) {
        List<EntityUser> list = new ArrayList<>();
        for (ColleagueCenterUser u : allColleagueCenterUsers) list.add(u);
        return list;
    }

    /**
	 * get message list server, will do nothing if the params number not equals to
	 * 1
	 * 
	 * params[0] is centerId
	 * @return
	 */
	private class LoadMedicalCenter extends AsyncTask<String, Void, String> {

		private DatabaseHelper dbhelper;

		public LoadMedicalCenter(DatabaseHelper dbhelper) {
			super();
			this.dbhelper = dbhelper;
		}

		@Override
		protected String doInBackground(String... params) {
			String center = null;
			if (params.length != 1)
				return center;

			String centerId = params[0];

			try {
				center = NetworkRequestsUtil.getMedicalCenterById(centerId);
			} catch (IOException e) {
				Log.e(TAG, e.getMessage(), e);
			} catch (InternalErrorException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return center;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			MedicalCenterUser center = null;
			if (result == null)
				Log.e(TAG, "center result is null!");
			else {
				if (Apis.DEBUG)
					Log.w(TAG, result);
				try {
					JSONObject jsonObject = new JSONObject(result);
					center = new MedicalCenterUser(jsonObject);
					createOrUpdate(dbhelper, center);
				} catch (Exception e) {
					Log.e(TAG, e.getMessage(), e);
				}
			}
			mPresenter.onEntityUserLoaded(center);
		}
	}

	private class SubscribeNewEntityUserAsyncTask extends
			AsyncTask<String, Void, EntityUser> {

		private final DatabaseHelper dbhelper;
		private AddEntityState state = AddEntityState.OTHER;

		public SubscribeNewEntityUserAsyncTask(DatabaseHelper dbhelper) {
			this.dbhelper = dbhelper;
		}

		@Override
		protected EntityUser doInBackground(String... params) {
			if (params == null || params.length == 0) {
				return null;
			}
			EntityUser user = null;
			String json = null;
			try {
				json = NetworkRequestsUtil.getEntityByIdOrPin(params[0]);
				if (StringUtils.isEmpty(json)) {
					// failed
					return null;
				} else {
					// parse json
					try {
						user = EntityUser.parseJson(json);
					} catch (Exception e) {
						Log.e(TAG, e.getMessage(), e);
						return null;
					}
					
					// if already have in db, return
					if (getEntityById(dbhelper, user.getId()) != null) {
						state = AddEntityState.DUPLICATE;
						return null;
					}
					
					// new entry, write to DB
					if (user instanceof DoctorUser) {
						createOrUpdate(dbhelper, (DoctorUser) user);
					} else if (user instanceof MedicalCenterUser) {
						createOrUpdate(dbhelper, (MedicalCenterUser) user);
					} else {
						return null;
					}

					// subscribe to it
					boolean b = NetworkRequestsUtil.postSubscribeNewDoctor(user.getId(), params[1], params[2]);
					if (!b) return null;
				}

			} catch (Exception e) {
				// error occurs from the json string,
				// can only abort this task
				Log.e(TAG, e.getMessage(), e);
			}
			return user;
		}

		@Override
		protected void onPostExecute(EntityUser user) {
			if (user == null) {
				onLoadNewEntityFailed(state);
			} else {
				onLoadNewEntitySuccess(user);
			}
		}

	}

	public static final String TAG = EntityModel.class.getSimpleName();

	private final IEntityPresenter mPresenter;

	public EntityModel(IEntityPresenter mPresenter) {
		super();
		this.mPresenter = mPresenter;
	}

	/**
	 * Get from database the doctor user list where doctor is not part of a
	 * medical group
	 * 
	 * @param dbhelper
	 * @return
	 */
	public List<DoctorUser> getCachedDoctorUserList(final DatabaseHelper dbhelper) {
		RuntimeExceptionDao<DoctorUser, Long> doctorUserRuntimeDao = dbhelper
				.getDoctorUserRuntimeDao();
		QueryBuilder<DoctorUser, Long> queryBuilder = doctorUserRuntimeDao
				.queryBuilder();
		List<DoctorUser> list = null;
		try {
			queryBuilder.where().isNull(DoctorUser.COLUMN_NAME_CENTER_ID);
			list = doctorUserRuntimeDao.query(queryBuilder.prepare());
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage(), e);
			list = new ArrayList<DoctorUser>(0);
		}
		return list;
	}

	/**
	 * 
	 * @param dbhelper
	 * @param id
	 * @return null if not found
	 */
	public static MedicalCenterUser getMedicalCenterUserById(DatabaseHelper dbhelper, String id) {
		RuntimeExceptionDao<MedicalCenterUser, Long> dao = dbhelper
				.getMedicalCenterUserRuntimeDao();
		QueryBuilder<MedicalCenterUser, Long> queryBuilder = dao
				.queryBuilder();
		MedicalCenterUser res = null;
		try {
			queryBuilder.where().eq(MedicalCenterUser.COLUMN_NAME_ID, id);
			List<MedicalCenterUser> resList = dao.query(queryBuilder
					.prepare());
			if (resList != null && !resList.isEmpty())
				res = resList.get(0);
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage(), e);
		}

		return res;
	}
	
	/**
	 * 
	 * @param dbhelper
	 * @param id
	 * @return null if not found
	 */
	public static DoctorUser getDoctorUserById(DatabaseHelper dbhelper, String id) {
		RuntimeExceptionDao<DoctorUser, Long> dao = dbhelper
				.getDoctorUserRuntimeDao();
		QueryBuilder<DoctorUser, Long> queryBuilder = dao
				.queryBuilder();
		DoctorUser res = null;
		try {
			queryBuilder.where().eq(DoctorUser.COLUMN_NAME_ID, id);
			List<DoctorUser> resList = dao.query(queryBuilder
					.prepare());
			if (resList != null && !resList.isEmpty())
				res = resList.get(0);
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage(), e);
		}

		return res;		
	}
	
	/**
	 * 
	 * @param dbhelper
	 * @param id
	 * @return null if not found
	 */
	public static PatientUser getPatientUserById(DatabaseHelper dbhelper, String id) {
		RuntimeExceptionDao<PatientUser, Long> dao = dbhelper
				.getPatientUserRuntimeDao();
		QueryBuilder<PatientUser, Long> queryBuilder = dao
				.queryBuilder();
		PatientUser res = null;
		try {
			queryBuilder.where().eq(PatientUser.COLUMN_NAME_ID, id);
			List<PatientUser> resList = dao.query(queryBuilder
					.prepare());
			if (resList != null && !resList.isEmpty())
				res = resList.get(0);
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage(), e);
		}

		return res;		
	}

    public static ColleagueDoctorUser getColleagueDoctorUserById(DatabaseHelper helper, String id) {
        RuntimeExceptionDao<ColleagueDoctorUser, Long> dao = helper.getColleagueDoctorUserDao();
        QueryBuilder<ColleagueDoctorUser, Long> queryBuilder = dao.queryBuilder();
        ColleagueDoctorUser user = null;
        try {
            queryBuilder.where().eq(ColleagueDoctorUser.COLUMN_NAME_ID, id);
            List<ColleagueDoctorUser> resList = dao.query(queryBuilder
                    .prepare());
            if (resList != null && !resList.isEmpty())
                user = resList.get(0);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return user;
    }

    public static ColleagueCenterUser getColleagueCenterUserById(DatabaseHelper helper, String id) {
        RuntimeExceptionDao<ColleagueCenterUser, Long> dao = helper.getColleagueCenterUserRuntimeDao();
        QueryBuilder<ColleagueCenterUser, Long> queryBuilder = dao.queryBuilder();
        ColleagueCenterUser user = null;
        try {
            queryBuilder.where().eq(ColleagueCenterUser.COLUMN_NAME_ID, id);
            List<ColleagueCenterUser> resList = dao.query(queryBuilder
                    .prepare());
            if (resList != null && !resList.isEmpty())
                user = resList.get(0);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return user;
    }

	/**
	 * Get from database all the medical group user list
	 * 
	 * @param dbhelper
	 * @return
	 */
	public List<MedicalCenterUser> getMedicalCenterUserList(
			DatabaseHelper dbhelper) {
		RuntimeExceptionDao<MedicalCenterUser, Long> medicalCenterUserRuntimeDao = dbhelper
				.getMedicalCenterUserRuntimeDao();
		return medicalCenterUserRuntimeDao.queryForAll();
	}
	
	/**
	 * Get from database all the patient user list
	 * @param dbhelper
	 * @return
	 */
	public List<PatientUser> getPatientUserList(DatabaseHelper dbhelper) {
		RuntimeExceptionDao<PatientUser, Long> patientUserRuntimeDao = dbhelper.getPatientUserRuntimeDao();
		return patientUserRuntimeDao.queryForAll();
	}

    public static List<ColleagueCenterUser> getAllColleagueCenterUsers(DatabaseHelper helper) {
        return helper.getColleagueCenterUserRuntimeDao().queryForAll();
    }

    public static List<ColleagueDoctorUser> getAllColleagueDoctorUsers(DatabaseHelper helper) {
        return helper.getColleagueDoctorUserDao().queryForAll();
    }

	public void loadMedicalCenterUser(final DatabaseHelper dbhelper,
			final String entityId) {

		if (Apis.DEBUG)
			Log.d(TAG, "loading medical user from server");

		if (StringUtils.isEmpty(entityId)) {
			if (Apis.DEBUG)
				Log.d(TAG, "Empty entity id");
			return;
		}

		new LoadMedicalCenter(dbhelper).execute(entityId);
	}

    /**
     *  Block an entity object async
     *  same api with delete, but with one additional param: block
     */
    public void blockContactFromServer(DatabaseHelper dbhelper, String userId, String pushId, String targetId){
        String block = "block";
        new DeleteContactAsyncTask(dbhelper).execute(userId, pushId, targetId, block);
    }
    /**
     * Delete an entity object asynchronously
     */
    public void deleteContactFromServer(DatabaseHelper dbhelper, String userId, String pushId, String targetId){
        new DeleteContactAsyncTask(dbhelper).execute(userId, pushId, targetId);
    }
	/**
	 * Load asynchronously an entity object given an id or pin number
	 * 
	 * @param dbhelper
	 * @param idOrPin
	 */
	public void subscribeNewEntityFromServer(DatabaseHelper dbhelper, String idOrPin, String userId, String pushId) {
		new SubscribeNewEntityUserAsyncTask(dbhelper).execute(idOrPin, userId, pushId);
	}

	public void onLoadNewEntityFailed(AddEntityState state) {
		this.mPresenter.onLoadNewEntityFailed(state);
	}

	public void onLoadNewEntitySuccess(EntityUser user) {
		this.mPresenter.onEntityUserLoaded(user);
	}

    public static void createOrUpdate(DatabaseHelper dbhelper, DoctorUser user) {
        RuntimeExceptionDao<DoctorUser, Long> doctorUserRuntimeDao = dbhelper
                .getDoctorUserRuntimeDao();

        doctorUserRuntimeDao.createOrUpdate(user);
    }

    public static void createOrUpdate(DatabaseHelper dbhelper, ColleagueDoctorUser user) {
        RuntimeExceptionDao<ColleagueDoctorUser, Long> dao = dbhelper
                .getColleagueDoctorUserDao();

        dao.createOrUpdate(user);
    }

    public static void createOrUpdate(DatabaseHelper dbhelper, MedicalCenterUser user) {
        RuntimeExceptionDao<MedicalCenterUser, Long> medicalCenterUserRuntimeDao = dbhelper
                .getMedicalCenterUserRuntimeDao();
        RuntimeExceptionDao<DoctorUser, Long> doctorUserRuntimeDao = dbhelper
                .getDoctorUserRuntimeDao();
        for (DoctorUser du : user.getDoctors()) {
            doctorUserRuntimeDao.createOrUpdate(du);
        }
        medicalCenterUserRuntimeDao.createOrUpdate(user);
    }

    public static void createOrUpdate(DatabaseHelper dbhelper, ColleagueCenterUser user) {
        RuntimeExceptionDao<DoctorUser, Long> doctorUserRuntimeDao = dbhelper
                .getDoctorUserRuntimeDao();
        for (DoctorUser du : user.getDoctors()) {
            doctorUserRuntimeDao.createOrUpdate(du);
        }

        RuntimeExceptionDao<ColleagueDoctorUser, Long> colleagueDoctorUserDao = dbhelper.getColleagueDoctorUserDao();
        for (ColleagueDoctorUser cdu : user.getColleagueDoctors()) {
            colleagueDoctorUserDao.createOrUpdate(cdu);
        }

        RuntimeExceptionDao<ColleagueCenterUser, Long> colleagueCenterUsersDao = dbhelper
                .getColleagueCenterUserRuntimeDao();
        colleagueCenterUsersDao.createOrUpdate(user);
    }

    public static void createOrUpdate(DatabaseHelper dbhelper, PatientUser user) {
		RuntimeExceptionDao<PatientUser, Long> patientUserRuntimeDao = dbhelper
				.getPatientUserRuntimeDao();

		patientUserRuntimeDao.createOrUpdate(user);
	}

	public void loadPatientListFromServer(DatabaseHelper dbhelper, String userId, String regId) {
		new LoadPatientListAsyncTask(dbhelper, userId, regId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	/**
	 * 
	 * @param dbhelper
	 * @param entityId
	 * @return null if not found
	 */
	public static EntityUser getEntityById(DatabaseHelper dbhelper, String entityId) {
		if (StringUtils.isEmpty(entityId)) return null;

		EntityUser user = getDoctorUserById(dbhelper, entityId);
		if (user != null) return user;

		user = getMedicalCenterUserById(dbhelper, entityId);
		if (user != null) return user;
		
		user = getPatientUserById(dbhelper, entityId);
        if (user != null) return user;

        user = getColleagueDoctorUserById(dbhelper, entityId);
        if (user != null) return user;

        user = getColleagueCenterUserById(dbhelper, entityId);
		return user;
	}

	public static void createOrUpdateEntityUser(DatabaseHelper dbhelper, EntityUser user) {
        if (user instanceof ColleagueCenterUser) {
            createOrUpdate(dbhelper, (ColleagueCenterUser) user);
        } else if (user instanceof ColleagueDoctorUser) {
            createOrUpdate(dbhelper, (ColleagueDoctorUser) user);
        } else if (user instanceof MedicalCenterUser) {
			createOrUpdate(dbhelper, (MedicalCenterUser) user);
		} else if (user instanceof DoctorUser) {
			createOrUpdate(dbhelper, (DoctorUser) user);
		} else if (user instanceof PatientUser) {
			createOrUpdate(dbhelper, (PatientUser) user);
		} else {
			throw new IllegalArgumentException("no such type of user");
		}
	}

	public void loadProRelationList(DatabaseHelper dbhelper, String userId, String pushId) {
		new LoadProfessionalRelationAsyncTask(dbhelper, userId, pushId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}


    public void loadProfessionalList(DatabaseHelper dbhelper, String userId, String pushId) {
        new LoadProfessionalListAsyncTask(dbhelper, userId, pushId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    public InstantMessage getLastChat(DatabaseHelper dbhelper, String targetId) {
		RuntimeExceptionDao<InstantMessage, Long> dao = dbhelper.getInstantMsgRuntimeDao();
		QueryBuilder<InstantMessage, Long> queryBuilder
		  = dao.queryBuilder();
		InstantMessage result = null;
		try {
		  queryBuilder
		    .orderBy(InstantMessage.COLUMN_NAME_TIME, false)
		    .limit(1l)
	  		.where().eq(InstantMessage.COLUMN_NAME_ENTITY_ID, targetId);
			List<InstantMessage> query = dao.query(queryBuilder.prepare());
			if (!query.isEmpty())
				result = query.get(0);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * Add relation between two professional user and load the entity information from server
	 * @param dbhelper
	 * @param initIdOrPin
	 * @param recipIdOrPin
	 */
	public void addProfessionalRelation(DatabaseHelper dbhelper, String initIdOrPin, String recipIdOrPin, String pushId) {
		new AddRelationAsyncTask(dbhelper, initIdOrPin, recipIdOrPin, pushId).execute();
	}

	public void updateEntityUser(DatabaseHelper dbhelper, EntityUser user) {
		if (user instanceof MedicalCenterUser) {
			updateDB(dbhelper, (MedicalCenterUser) user);
		} else if (user instanceof DoctorUser) {
			updateDB(dbhelper, (DoctorUser) user);
		} else if (user instanceof PatientUser) {
			updateDB(dbhelper, (PatientUser) user);
		} else {
			throw new IllegalArgumentException("no such type of user");
		}
	}

	public void updateDB(DatabaseHelper dbhelper, PatientUser user) {
		if (Apis.DEBUG) Log.d(TAG, "updating patient user to database");
		RuntimeExceptionDao<PatientUser, Long> patientUserRuntimeDao = dbhelper
				.getPatientUserRuntimeDao();

		patientUserRuntimeDao.update(user);
	}

	public void updateDB(DatabaseHelper dbhelper, DoctorUser user) {
		if (Apis.DEBUG) Log.d(TAG, "updating doctor user to database");
		RuntimeExceptionDao<DoctorUser, Long> doctorUserRuntimeDao = dbhelper
				.getDoctorUserRuntimeDao();
		
		doctorUserRuntimeDao.update(user);
	}

	public void updateDB(DatabaseHelper dbhelper, MedicalCenterUser user) {
		if (Apis.DEBUG) Log.d(TAG, "updating medical center user to database");
		RuntimeExceptionDao<MedicalCenterUser, Long> medicalCenterUserRuntimeDao = dbhelper
				.getMedicalCenterUserRuntimeDao();
		RuntimeExceptionDao<DoctorUser, Long> doctorUserRuntimeDao = dbhelper
				.getDoctorUserRuntimeDao();
		for (DoctorUser du : user.getDoctors()) {
			doctorUserRuntimeDao.update(du);
		}
		
		medicalCenterUserRuntimeDao.update(user);
	}
}
