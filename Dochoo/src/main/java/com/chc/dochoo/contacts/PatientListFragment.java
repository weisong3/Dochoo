package com.chc.dochoo.contacts;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AlphabetIndexer;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chc.dochoo.CHCApplication;
import com.chc.found.config.Apis;
import com.chc.found.models.AddEntityState;
import com.chc.found.models.EntityUser;
import com.chc.found.models.PatientUser;
import com.chc.found.presenters.EntityPresenter;
import com.chc.found.utils.PinyinUtils;
import com.chc.found.views.IEntityView;
import com.chcgp.hpad.util.download.ImageDownloader;
import com.test.found.R;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PatientListFragment extends AbstractContactListFragment implements IEntityView {
	private static final String TAG = PatientListFragment.class.getSimpleName();
	private EntityPresenter entityPresenter;
	private OnPatientOptionListener mCallback;
	private AlphaDoctorPatientListAdapter adapter;
    private ListView contactsListView;
    private TextView sectionToastText;
    private RelativeLayout sectionToastLayout;
    private SideBar sidebar;

	public interface OnPatientOptionListener {
		void onPatientClicked(String id);
		void onPatientImageClicked(PatientUser user);
        void onPatientLongClicked(String id);
	}
	
	
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnPatientOptionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPatientOptionListener");
        }
    }

	@Override
	public void onResume() {
		super.onResume();
		if (entityPresenter == null) {
			entityPresenter = new EntityPresenter(this);
		}
		List<PatientUser> patientUserList = entityPresenter.getPatientUserList();
		reloadContent(patientUserList);
        CHCApplication application = getCHCApplication();
        if (application != null && getActivity() instanceof ContactActivity) {
            entityPresenter.refreshPatientUserList(application.getHelper(), application.getUserId(), application.getRegId());

            //alphabetization
            sortPatient(patientUserList);

            List<EntityUser> tempList = new ArrayList<>();
            for(PatientUser pu:patientUserList) tempList.add(pu);
            MyCursor myCursor = new MyCursor(tempList);
            adapter = new AlphaDoctorPatientListAdapter(getActivity(),R.layout.alphabet_fragment_contact_list,patientUserList,mCallback);

            contactsListView = (ListView) getView().findViewById(R.id.alphabet_listview);
            sectionToastLayout = (RelativeLayout) getView().findViewById(R.id.section_toast_layout);
            sectionToastText = (TextView) getView().findViewById(R.id.section_toast_text);
            if(patientUserList.size()>0){
                contactsListView.setAdapter(adapter);
            }
            ((ContactActivity)getActivity()).setPatientAdapter((AlphaDoctorPatientListAdapter)getListAdapter());
            sidebar = (SideBar) getView().findViewById(R.id.sidebar);
            sidebar.setTextView(sectionToastText);
            sidebar.setSectionToastLayout(sectionToastLayout);
            sidebar.setListView(contactsListView);
        }
	}


	@Override
	public CHCApplication getCHCApplication() {
        FragmentActivity activity = getActivity();
        if (activity == null) return null;
		return CHCApplication.getInstance(activity);
	}

	@Override
	public void onEntityLoaded(EntityUser user) {
	}

	@Override
	public void getEntityFailed(AddEntityState state) {
	}
	
	@Override
	public void onEntityLoaded(List<EntityUser> user) {
        if (user == null) return; // fetch error
		List<PatientUser> patientList = new ArrayList<PatientUser>();
		for (EntityUser u : user) {
			if (u instanceof PatientUser) {
				patientList.add((PatientUser) u);
			} else {
				// weird bug, log
				Log.e(TAG, "non-patient object in patient list, why: \n" + u.toString());
			}
		}
		reloadContent(patientList);
	}
	
	private void reloadContent(List<PatientUser> patientList) {
		if (patientList == null) return;

        sortPatient(patientList);
		if (getListAdapter() == null) {
			setListAdapter(new AlphaDoctorPatientListAdapter(getActivity(), R.layout.alphabet_fragment_contact_list,patientList, mCallback));
		} else {
			((AlphaDoctorPatientListAdapter)getListAdapter()).updateList(patientList);
		}
	}

    public static void sortPatient(List<PatientUser> patientList){
        Collections.sort(patientList, new Comparator<EntityUser>() {

            @Override
            public int compare(EntityUser lhs, EntityUser rhs) {
                String leftName = lhs.getFullname();
                String rightName = rhs.getFullname();
                if (leftName == null && rightName == null) return 0;
                if (leftName == null) return -1;
                if (rightName == null) return 1;
                return PinyinUtils.getPingYin(leftName).compareToIgnoreCase(PinyinUtils.getPingYin(rightName));
            }
        });
    }

}
