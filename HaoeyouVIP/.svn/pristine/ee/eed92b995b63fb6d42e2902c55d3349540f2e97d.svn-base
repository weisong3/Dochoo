package com.chc.found.presenters;

import java.util.List;

import com.chc.found.models.AddEntityState;
import com.chc.found.models.DatabaseHelper;
import com.chc.found.models.EntityUser;


public interface IEntityPresenter {
	EntityUser getEntityById(String id);
	void onEntityUserLoaded(EntityUser user);
	void onEntityUserListLoaded(List<EntityUser> userlist);
	void onLoadNewEntityFailed(AddEntityState state);
    void onContactDeleted(EntityUser user);
    void onContactDeleteFailed();

    void addContact(boolean isPatient, String idOrPin, String userId, String pushId);
    void deleteContact(String targetId, String userId, String pushId);
    void blockContact(String targetId, String userId, String pushId);
}
