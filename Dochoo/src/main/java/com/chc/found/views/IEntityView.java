package com.chc.found.views;

import java.util.List;

import com.chc.found.models.AddEntityState;
import com.chc.found.models.EntityUser;

public interface IEntityView extends IBaseView {
	
	void onEntityLoaded(EntityUser user);
	
	void onEntityLoaded(List<EntityUser> user);

	void getEntityFailed(AddEntityState state);
}
