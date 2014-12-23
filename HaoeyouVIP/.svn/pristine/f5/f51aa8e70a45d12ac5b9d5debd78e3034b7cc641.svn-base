package com.chc.found;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import android.support.v7.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.MenuItem;

import com.chc.dochoo.CHCApplication;
import com.chc.found.adapters.DoctorDetailScrollableTabsAdapter;
import com.chc.found.models.AddEntityState;
import com.chc.found.models.DoctorUser;
import com.chc.found.models.EntityUser;
import com.chc.found.models.MedicalCenterUser;
import com.chc.found.presenters.EntityPresenter;
import com.chc.found.views.IEntityView;

public class DoctorDetailScrollableTabsActivity extends ScrollableTabsActivity implements IEntityView {

	private static final String TAG = DoctorDetailScrollableTabsActivity.class.getSimpleName();
	private static String KEY_CENTER_ID = "medicalCenterId";
	private static String KEY_DETAIL_ID = "id";
	private FragmentStatePagerAdapter adapter;
	private EntityPresenter presenter;
	private String detailDocId;
	private String centerId;
	private MedicalCenterUser medicalCenterUser;
	
	public static void startActivity(Context context, String medicalCenterId, String detailDocId) {
		Intent intent = new Intent(context, DoctorDetailScrollableTabsActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(KEY_CENTER_ID, medicalCenterId);
		bundle.putString(KEY_DETAIL_ID, detailDocId);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
		presenter = new EntityPresenter(this);

		Bundle extras = getIntent().getExtras();

        if (extras == null) return;
        centerId = extras.getString(KEY_CENTER_ID);
		detailDocId = extras.getString(KEY_DETAIL_ID);
		
		if (StringUtils.isEmpty(centerId)) {
			Log.e(TAG, "empty medical id");
			finish();
			return;
		}
		
		medicalCenterUser = presenter.getMedicalCenterUserById(centerId);
	}


	@Override
	protected void onResume() {
		super.onResume();
	}


	@Override
	protected void onStart() {
		super.onStart();
		
		int displayIndex = 0;
		
		for (int i = 0; i < presenter.getDoctorList(medicalCenterUser).size(); ++i) {
			if (presenter.getDoctorList(medicalCenterUser).get(i).getId().equals(detailDocId)) {
				displayIndex = i;
				break;
			}
		}
		mViewPager.setCurrentItem(displayIndex);
	}



	@Override
	FragmentStatePagerAdapter getPagerAdapter(FragmentManager fm) {
		if (adapter == null) {
			adapter = new DoctorDetailScrollableTabsAdapter(fm, presenter.getDoctorList(medicalCenterUser));
		}
		return adapter;
	}

	@Override
	public CHCApplication getCHCApplication() {
		return CHCApplication.getInstance(this);
	}

	@Override
	public void onEntityLoaded(EntityUser user) {
		
	}

	@Override
	public void getEntityFailed(AddEntityState state) {
		//TODO
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}



	public ArrayList<DoctorUser> getDoctorList() {
		return this.presenter.getDoctorList(medicalCenterUser);
	}



	@Override
	public void onEntityLoaded(List<EntityUser> user) {
		// TODO Auto-generated method stub
		
	}
}
