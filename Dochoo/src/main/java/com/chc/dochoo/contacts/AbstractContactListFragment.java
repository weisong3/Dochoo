package com.chc.dochoo.contacts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.chc.found.config.Apis;
import com.test.found.R;

public abstract class AbstractContactListFragment extends Fragment {

    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);
        listView = (ListView) view.findViewById(R.id.listview);
        view = inflater.inflate(R.layout.alphabet_fragment_contact_list,container,false);
        listView = (ListView) view.findViewById(R.id.alphabet_listview);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = getListView();
        if (listView != null) {
            listView.setSelector(android.R.drawable.list_selector_background);
            listView.setVerticalFadingEdgeEnabled(true);
        }
    }

    public static Fragment newInstance(ContactListType contactListType) {
		switch (contactListType) {
		case PATIENTS:
			return new PatientListFragment();
		case PROFESSIONALS:
			return new DoctorContactListFragment();
        case COLLEAGUE:
            return new ColleagueListFragment();
		default:
			throw new IllegalArgumentException("Wrong type");
		}
	}


    protected ListView getListView() {
        return listView;
    }

    protected ListAdapter getListAdapter() {
        if (listView == null) return null;
        return this.listView.getAdapter();
    }

    protected void setListAdapter(ListAdapter adapter) {
        if (listView == null) throw new IllegalStateException("ListView is null");
        listView.setAdapter(adapter);
    }

}
