package com.chc.found.presenters;

import android.content.Entity;
import android.os.AsyncTask;
import android.util.Log;

import com.chc.dochoo.CHCApplication;
import com.chc.dochoo.contacts.ColleagueResponse;
import com.chc.dochoo.contacts.ContactModel;
import com.chc.dochoo.conversations.ConversationModel;
import com.chc.exceptions.InternalErrorException;
import com.chc.found.config.Apis;
import com.chc.found.models.AddEntityState;
import com.chc.found.models.ColleagueCenterUser;
import com.chc.found.models.DatabaseHelper;
import com.chc.found.models.DoctorUser;
import com.chc.found.models.EntityModel;
import com.chc.found.models.EntityUser;
import com.chc.dochoo.conversations.InstantMessage;
import com.chc.found.models.MedicalCenterUser;
import com.chc.found.models.PatientUser;
import com.chc.found.network.NetworkRequestsUtil;
import com.chc.found.views.IEntityView;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EntityPresenter implements IEntityPresenter {
	private static final String TAG = EntityPresenter.class.getSimpleName();
	private final EntityModel mModel;
	private final IEntityView mView;

	public EntityPresenter(IEntityView mView) {
		super();
		this.mView = mView;
		this.mModel = new EntityModel(this);
	}

	public void onAddEntityRequested(String idOrPin, String userId, String pushId) {
		this.mModel.subscribeNewEntityFromServer(
				mView.getCHCApplication().getHelper(),
				idOrPin,
				userId,
				pushId);
	}

	@Override
	public void onLoadNewEntityFailed(AddEntityState state) {
		this.mView.getEntityFailed(state);
	}

    @Override
    public void addContact(boolean isPatient, String idOrPin, String userId, String pushId) {
        if (isPatient) {
            onAddEntityRequested(idOrPin, userId, pushId);
        } else {
            onAddProRelation(idOrPin, userId, pushId);
        }
    }

    @Override
    public void deleteContact(String targetId, String userId, String pushId) {

        this.mModel.deleteContactFromServer(mView.getCHCApplication().getHelper(), userId, pushId, targetId);

    }

    @Override
    public void blockContact(String targetId, String userId, String pushId) {
        this.mModel.blockContactFromServer(mView.getCHCApplication().getHelper(), userId, pushId, targetId);
    }

    @Override
    public void onContactDeleted(EntityUser user) {
        if(user instanceof PatientUser || user instanceof DoctorUser || user instanceof MedicalCenterUser){
            ContactModel.deleteContact(mView.getCHCApplication().getHelper(),user.getId());
            ConversationModel.deletePrivateConversationIfEmpty(mView.getCHCApplication().getHelper(),user.getId());
            this.mView.onEntityLoaded(getEntityList(mView.getCHCApplication().getHelper()));
        }
        else{
            Log.e(TAG,"user type error on contact deleted");
        }
}

    @Override
    public void onContactDeleteFailed() {
        Log.e(TAG, "Contact delete failed");
    }

    public List<EntityUser> getEntityList(DatabaseHelper dbhelper) {
		List<EntityUser> entityList = new ArrayList<EntityUser>();
		entityList.addAll(mModel.getMedicalCenterUserList(dbhelper));
		entityList.addAll(mModel.getCachedDoctorUserList(dbhelper));
		
		return entityList;
	}

    public void loadProfessionalList(DatabaseHelper dbhelper, String userId, String pushId) {
        if (StringUtils.isNotEmpty(userId) && StringUtils.isNotEmpty(pushId)) {
            mModel.loadProfessionalList(dbhelper, userId, pushId);
        } else {
            if (Apis.DEBUG) Log.w(TAG, "refresh entity list failed due to empty user id or push id");
        }
    }
	
	public void refreshRelationList(DatabaseHelper dbhelper, String userId, String pushId) {
		if (StringUtils.isNotEmpty(userId) && StringUtils.isNotEmpty(pushId)) {
			mModel.loadProRelationList(dbhelper, userId, pushId);
		} else {
			if (Apis.DEBUG) Log.w(TAG, "refresh entity list failed due to empty user id or push id");
		}
	}

	@Override
	public void onEntityUserLoaded(EntityUser user) {
        if (mView != null)
		    mView.onEntityLoaded(user);
	}

//	@Override
//	public void loadEntity(Class<? extends EntityUser> cls) {
//		
//		
//		if (cls == MedicalCenterUser.class) {
//			loadMedicalCenterUser();			
//		} else if (cls == DoctorUser.class) {
//			loadDoctorUser();
////		} else if (cls == PatientUser.class) {
////			loadPatientUserList();
//		} else {
//			throw new IllegalArgumentException("No such entity class accepted");
//		}
//		
//	}
	
	public List<PatientUser> getPatientUserList() {
		CHCApplication chcApplication = mView.getCHCApplication();
    DatabaseHelper helper = chcApplication.getHelper();
    List<PatientUser> patientUserList = mModel.getPatientUserList(helper);
    
    return patientUserList;
	}
	
	public void refreshPatientUserList(DatabaseHelper helper, String userId, String pushId) {
    	mModel.loadPatientListFromServer(helper, userId, pushId);
	}

//	private void loadMedicalCenterUser() {
//		CHCApplication chcApplication = mView.getCHCApplication();
//		EntityUser user = null;
//		DatabaseHelper helper = chcApplication.getHelper();
//		List<MedicalCenterUser> list = mModel.getMedicalCenterUserList(helper);
//		if (list != null && !list.isEmpty()) {
//			user = list.get(0); 
//		}
//		mModel.loadMedicalCenterUser(helper, chcApplication.getTargetId());
//
//		if (user != null) {
//			mView.onEntityLoaded(user);
//		}
//	}

//	private void loadDoctorUser() {
//		CHCApplication chcApplication = mView.getCHCApplication();
//		EntityUser user = null;
//		DatabaseHelper helper = chcApplication.getHelper();
//		List<DoctorUser> list = mModel.getCachedDoctorUserList(helper);
//		if (list != null && !list.isEmpty()) {
//  		user = list.get(0);
//		}
//		mModel.loadDoctorUserFromServer(helper, chcApplication.getTargetId());
//
//		if (user != null) {
//			mView.onEntityLoaded(user);
//		}
//	}

	public ArrayList<DoctorUser> getDoctorList(MedicalCenterUser cachedMedicalCenterUser) {
		return new ArrayList<DoctorUser>(cachedMedicalCenterUser.getDoctors());
	}
	
	public MedicalCenterUser getMedicalCenterUserById(String id) {
		return this.mModel.getMedicalCenterUserById(mView.getCHCApplication().getHelper(), id);
	}

//	@Override
//	public void loadEntity() {
//		CHCApplication chcApplication = mView.getCHCApplication();
//		if (chcApplication == null) {
//			return;
//		}
//		
//		if (chcApplication.isCenter()) {
//			loadEntity(MedicalCenterUser.class);
//		} else {
//			loadEntity(DoctorUser.class);
//		}
//	}

	@Override
	public void onEntityUserListLoaded(List<EntityUser> userlist) {
		mView.onEntityLoaded(userlist);
	}
	
	/**
	 * Add professional relation asynchronously
     * @param targetIdOrPin
     * @param userId
     * @param pushId
     */
	public void onAddProRelation(String targetIdOrPin, String userId, String pushId) {
		mModel.addProfessionalRelation(mView.getCHCApplication().getHelper(), userId, targetIdOrPin, pushId);
	}
	
	/**
	 * Returns the last message in chat with targetId
	 * @param targetId
	 * @return
	 */
	public InstantMessage getLastMsg(String targetId) {
		return mModel.getLastChat(mView.getCHCApplication().getHelper(), targetId);
	}

	@Override
	public EntityUser getEntityById(String id) {
		return mModel.getEntityById(mView.getCHCApplication().getHelper(), id);
	}


    public void refreshAllContactList(DatabaseHelper helper, String userId, String regId, boolean isPatient) {
        if (isPatient) {
            loadProfessionalList(helper, userId, regId);
        } else {
            refreshRelationList(helper, userId, regId);
            refreshPatientUserList(helper, userId, regId);
            refreshColleagueList(helper, userId, regId);
        }
    }

    private void refreshColleagueList(final DatabaseHelper helper, String userId, String regId) {
        mModel.loadColleagueListFromServer(helper, userId, regId);
    }

    public void loadColleagueData() {
        CHCApplication chcApplication = mView.getCHCApplication();
        if (chcApplication == null) return;
        List<ColleagueCenterUser> allColleagueCenterUsers = EntityModel.getAllColleagueCenterUsers(chcApplication.getHelper());
        mView.onEntityLoaded(EntityModel.toEntityUserList(allColleagueCenterUsers));

        mModel.loadColleagueListFromServer(chcApplication.getHelper(), chcApplication.getUserId(), chcApplication.getRegId());
    }

}
