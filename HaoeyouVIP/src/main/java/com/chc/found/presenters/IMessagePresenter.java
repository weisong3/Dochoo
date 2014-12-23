package com.chc.found.presenters;

import java.util.List;

import com.chc.dochoo.conversations.FoundMessage;

public interface IMessagePresenter {
	void loadData(String userId, String targetId, String pushId);
	void loadGroupData(String userId, String targetGroupId, String pushId);

	@Deprecated
	void loadServerData(String userId, String targetId, String pushId);
	/**
	 * 
	 * @param msgList is null if error has occurred
	 */
	void onMessageLoaded(List<? extends FoundMessage> msgList);
	void writeData(FoundMessage msg);
	FoundMessage getMessageById(String id);
    FoundMessage getGroupMessageById(String id);
}
