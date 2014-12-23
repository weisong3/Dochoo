package com.chc.dochoo.conversations;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.chc.dochoo.contacts.ContactListType;
import com.chc.dochoo.contacts.DoctorContactListFragment;
import com.chc.dochoo.contacts.PatientListFragment;
import com.test.found.R;

public abstract class AbstractConversationListFragment extends Fragment {

	private IntentFilter intentFilter;
	private BroadcastReceiver receiver = new MyBroadcastReceiver();
	private volatile boolean receiverRegistered = false;
    private ListView mListView;
    private ListAdapter mAdapter;
    private View mEmptyListContainer;

    protected void registerReceiver() {
		synchronized (this) {
			if (getActivity() != null && !receiverRegistered) {
				getActivity().registerReceiver(receiver, intentFilter);
				receiverRegistered = true;
			}
		}
	}
	
	protected abstract void messageReceived();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intentFilter = new IntentFilter(getString(R.string.im_action));
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation_list, container, false);
        mListView = (ListView) view.findViewById(R.id.listview);
        mEmptyListContainer = view.findViewById(R.id.empty_list_container);
        return view;
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setSelector(android.R.drawable.list_selector_background);
//        getListView().setBackgroundColor(getResources().getColor(R.color.white));
    }

    public static Fragment newInstance(ContactListType contactListType) {
		switch (contactListType) {
		case PATIENTS:
			return new PatientListFragment();
		case PROFESSIONALS:
			return new DoctorContactListFragment();
		default:
			throw new IllegalArgumentException("Wrong type");
		}
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			messageReceived();
		}
		
	}

    @Override
    public void onResume() {
        registerReceiver();
        super.onResume();
    }

    @Override
	public void onPause() {
		super.onPause();
		unregister();
	}

	private void unregister() {
		if (receiverRegistered)
		{
			getActivity().unregisterReceiver(receiver);
			synchronized (this) {
				receiverRegistered = false;
			}
		}
	}

	@Override
	public void onDestroyView() {
		unregister();
		super.onDestroyView();
	}
	
	public String getMessageContentOrDescription(InstantMessage im) {
		switch (im.getMessageType()) {
		case IMAGE:
			return getString(R.string.instant_message_content_desc_image);
		case FILE:
			return getString(R.string.instant_message_content_desc_file);
		case VOICE:
			return getString(R.string.instant_message_content_desc_voice);
		default:
			return im.getContent();
		}
	}

    ListView getListView() {
        return this.mListView;
    }

    ListAdapter getListAdapter() {
        return this.mAdapter;
    }

    void setListAdapter(ListAdapter adapter) {
        this.mAdapter = adapter;
        this.mListView.setAdapter(adapter);
    }

    void onEmptyConversationList(boolean b) {
        this.mEmptyListContainer.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
    }

}
