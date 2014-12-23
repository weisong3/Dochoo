package com.chc.dochoo.contacts;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.chc.dochoo.CHCApplication;
import com.chc.found.models.AddEntityState;
import com.chc.found.models.ColleagueCenterUser;
import com.chc.found.models.EntityUser;
import com.chc.found.presenters.EntityPresenter;
import com.chc.found.views.IEntityView;
import com.test.found.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by HenryW on 2/20/14.
 */
public class ColleagueListFragment extends AbstractContactListFragment implements IEntityView {
    private static final String TAG = ColleagueListFragment.class.getSimpleName();
    private EntityPresenter mPresenter;
    private OnContactOptionListener mCallback;
    private ExpandableListView mExpandableListView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnContactOptionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnContactOptionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_colleague_contact_list, container, false);

        this.mExpandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        CHCApplication chcApplication = CHCApplication.getInstance(getActivity());
        if (chcApplication == null) return;
        if (mPresenter == null) {
            mPresenter = new EntityPresenter(this);
        }
        mPresenter.loadColleagueData();
        if(getActivity() instanceof ContactActivity){
            ((ContactActivity)getActivity()).setColleagueAdapter((ColleagueExpandableListAdapter)getExpandableListAdapter());
            if(this.mExpandableListView != null && this.mExpandableListView.getAdapter() !=null && this.mExpandableListView.getAdapter().getCount() >= 1) this.mExpandableListView.expandGroup(0);
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
        // ignored
    }

    @Override
    public void getEntityFailed(AddEntityState state) {
        // ignored
    }

    @Override
    public void onEntityLoaded(List<EntityUser> user) {
        reloadContent(user);
    }

    private void reloadContent(List<EntityUser> entityUsers) {
        List<ColleagueCenterUser> colleagueCenterUsers = castToColleagueCenters(entityUsers);

        if (colleagueCenterUsers == null) return;

        Collections.sort(colleagueCenterUsers, new Comparator<EntityUser>() {

            @Override
            public int compare(EntityUser lhs, EntityUser rhs) {
                String leftName = lhs.getFullname();
                String rightName = rhs.getFullname();
                if (leftName == null && rightName == null) return 0;
                if (leftName == null) return -1;
                if (rightName == null) return 1;
                return leftName.compareTo(rightName);
            }
        });

        ExpandableListAdapter expandableListAdapter = getExpandableListAdapter();
        if (expandableListAdapter == null) {
            setExpandableListAdapter(new ColleagueExpandableListAdapter(colleagueCenterUsers, mCallback));
        } else {
            ((ColleagueExpandableListAdapter) expandableListAdapter).updateList(colleagueCenterUsers);
        }
    }

    private void setExpandableListAdapter(ExpandableListAdapter adapter) {
        if (adapter == null) return;
        if (mExpandableListView == null) throw new IllegalStateException();
        mExpandableListView.setAdapter(adapter);
    }

    private ExpandableListAdapter getExpandableListAdapter() {
        if (mExpandableListView == null) return null;
        return mExpandableListView.getExpandableListAdapter();
    }

    private List<ColleagueCenterUser> castToColleagueCenters(List<EntityUser> users) {
        if (users == null) return null;
        List<ColleagueCenterUser> result = new ArrayList<>();
        for (EntityUser u : users) {
            if (u instanceof ColleagueCenterUser) {
                result.add((ColleagueCenterUser) u);
            } else {
                return null;
            }
        }

        return result;
    }

}
