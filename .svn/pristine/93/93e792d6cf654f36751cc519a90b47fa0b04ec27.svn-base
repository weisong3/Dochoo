//package com.chc.found;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.ActionBarActivity;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.view.PagerTabStrip;
//import android.support.v4.view.ViewPager;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.chc.dialog.EditTextDialogFactory;
//import com.chc.dialog.EditTextDialogHandler;
//import com.chc.dialog.EditTextDialogResult;
//import com.chc.dialog.ListViewDialogFactory;
//import com.chc.dialog.ListViewDialogHandler;
//import com.chc.dialog.ListViewDialogResult;
//import com.chc.dochoo.CHCApplication;
//import com.chc.dochoo.contacts.ContactListType;
//import com.chc.dochoo.contacts.DoctorContactListAdapter;
//import com.chc.dochoo.contacts.OnContactOptionListener;
//import com.chc.dochoo.profiles.ProfileActivity;
//import com.chc.dochoo.userlogin.UserLogInActivity;
//import com.chc.dochoo.contacts.AbstractContactListFragment;
//import com.chc.dochoo.contacts.PatientListFragment.OnPatientOptionListener;
//import com.chc.found.models.AddEntityState;
//import com.chc.found.models.EntityUser;
//import com.chc.found.models.MedicalCenterUser;
//import com.chc.found.models.PatientUser;
//import com.chc.found.presenters.EntityPresenter;
//import com.chc.found.presenters.SignOutPresenter;
//import com.chc.found.utils.QrcodeUtil;
//import com.chc.found.views.IEntityView;
//import com.chc.views.TypefacedTextView;
//import com.chcgp.scan.client.android.CaptureActivity;
//import com.test.found.R;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class DoctorMainActivity extends ActionBarActivity
//        implements IEntityView, OnContactOptionListener, OnPatientOptionListener {
//
//	private static final int REQUESTCODE = 1;
//	private EntityPresenter entityPresenter;
//
//	DoctorContactListAdapter doctorAdapter;
//	ViewPager mViewPager;
//    PagerTabStrip mTabStrip;
//
//    @Override
//    public void onBackPressed() {
//        finish();
//        this.overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
//    }
//
//    @Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//		setContentView(R.layout.activity_doctor_main);
//
//		mViewPager = (ViewPager) findViewById(R.id.pager);
////		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//		mViewPager.setAdapter(new ProfessionalViewPagerAdapter(getSupportFragmentManager()));
//        mTabStrip = (PagerTabStrip) findViewById(R.id.pager_tabstrip);
//		mTabStrip.setTabIndicatorColor(getResources().getColor(R.color.default_dochoo_orange));
//        mTabStrip.setTextColor(getResources().getColor(R.color.dochoo_default_text_color));
//
//        // for each sub-view of the pager strip, change typeface if is text view
//        for (int i = 0; i < mTabStrip.getChildCount(); ++i) {
//            View nextChild = mTabStrip.getChildAt(i);
//            if (nextChild instanceof TextView) {
//                TextView textViewToConvert = (TextView) nextChild;
//                textViewToConvert.setTypeface(
//                        TypefacedTextView.getTypeface(
//                                getAssets(),
//                                FoundSettings.TYPEFACE_OPEN_SANS));
//            }
//        }
//
//		entityPresenter = new EntityPresenter(this);
//
//	}
//
//	private void showAddingSelectionDialog() {
//		final int PIN_INDEX = 0;
//		final int QR_INDEX = 1;
//		ArrayList<String> addUserMethod = new ArrayList<String>();
//		addUserMethod.add(PIN_INDEX, getString(R.string.button_doctormainactivity_enter_pin));
//		addUserMethod.add(QR_INDEX, getString(R.string.button_doctormainactivity_scan));
//		ListViewDialogHandler handler = (ListViewDialogHandler) new ListViewDialogFactory().createDialogHandler(this, "add entity user dialog", new ListViewDialogResult() {
//
//			@Override
//			public void onItemClicked(int position) {
//				if (position == PIN_INDEX) {
//					EditTextDialogHandler editTextDialogHandler = (EditTextDialogHandler) new EditTextDialogFactory().createDialogHandler(DoctorMainActivity.this, getString(R.string.title_dialog_doctormainactivity_input_pin), new EditTextDialogResult() {
//
//						@Override
//						public void onTextEntered(String text, int id) {
////							entityPresenter.onAddEntityRequested(text, CHCApplication.getInstance(DoctorMainActivity.this).getUserId(), CHCApplication.getInstance(DoctorMainActivity.this).getRegId());
//							onAddRelation(text, CHCApplication.getInstance(DoctorMainActivity.this).getUserId());
//						}
//
//						@Override
//						public void onTextCancelled(int id) {
//							// TODO Auto-generated method stub
//
//						}
//					});
//					EditTextDialogFactory.showDialog(editTextDialogHandler, getString(R.string.title_dialog_doctormainactivity_input_pin), null, "", 0);
//				} else if (position == QR_INDEX) {
//					startCaptureActivityForResult();
//				}
//			}
//		});
//		ListViewDialogFactory.showDialog(handler, getString(R.string.title_dialog_doctormainactivity_add), null, addUserMethod, R.layout.entity_listview_item_add_menu);
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.doctor_main_activity, menu);
//		return super.onCreateOptionsMenu(menu);
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		int id = item.getItemId();
//		switch (id) {
//		case R.id.action_dochoo_card:
//			DochooCardActivity.startActivity(this, CHCApplication.getInstance(this).getUser());
//			return true;
//        case R.id.action_profile:
//            ProfileActivity.startActivity(this);
//            return true;
//		case R.id.action_logout:
//			askAndSignOut();
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
//
//	private void askAndSignOut() {
//		new AlertDialog.Builder(this)
//		.setTitle(R.string.logout_title)
//		.setMessage(R.string.logout_message)
//		.setPositiveButton(R.string.logout_confirm, new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				new SignOutPresenter(DoctorMainActivity.this).signOut();
//				UserLogInActivity.startLogInActivity(DoctorMainActivity.this);
//				finish();
//			}
//		})
//		.setNegativeButton(R.string.cancel, null)
//		.create()
//		.show();
//	}
//
//	private class ProfessionalViewPagerAdapter extends FragmentPagerAdapter {
//
//		public ProfessionalViewPagerAdapter(FragmentManager fm) {
//			super(fm);
//		}
//
//		@Override
//		public Fragment getItem(int position) {
//			return AbstractContactListFragment.newInstance(ContactListType.values()[position]);
//		}
//
//		@Override
//		public int getCount() {
//			return ContactListType.values().length;
//		}
//
//		@Override
//		public CharSequence getPageTitle(int position) {
//			int resId = ContactListType.values()[position].getResId();
//			return getString(resId);
//		}
//
//	}
//
//	private void startCaptureActivityForResult() {
//		Intent intent = new Intent(this, CaptureActivity.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//		startActivityForResult(intent, REQUESTCODE);
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (requestCode != REQUESTCODE) {
//			return;
//		}
//
//		if (resultCode == CaptureActivity.RESULTCODE) {
//			String resultStr = data.getStringExtra("code");
//
////			entityPresenter.onAddEntityRequested(QrcodeUtil.getIdOrPin(resultStr), CHCApplication.getInstance(DoctorMainActivity.this).getUserId(), CHCApplication.getInstance(DoctorMainActivity.this).getRegId());
//			onAddRelation(QrcodeUtil.getIdOrPin(resultStr), CHCApplication.getInstance(DoctorMainActivity.this).getUserId());
//		} else {
//		}
//
//	}
//
//	private void onAddRelation(String targetIdOrPin, String userId) {
//		this.entityPresenter.onAddProRelation(targetIdOrPin, userId);
//	}
//
//	public static final void startActivity(Activity a) {
//		Intent intent = new Intent(a, DoctorMainActivity.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		a.startActivity(intent);
//	}
//
//	@Override
//	public CHCApplication getCHCApplication() {
//		return CHCApplication.getInstance(this);
//	}
//
//	@Override
//	public void onEntityLoaded(EntityUser user) {
//		this.doctorAdapter.updateList(entityPresenter.getEntityList(getCHCApplication().getHelper()));
//		Toast.makeText(this, getString(R.string.contact_add_success), Toast.LENGTH_SHORT).show();
//	}
//
//	@Override
//	public void getEntityFailed(AddEntityState state) {
//		switch (state) {
//		case DUPLICATE:
//			Toast.makeText(this, getString(R.string.toast_doctormainactivity_pin_already_added), Toast.LENGTH_SHORT).show();
//			break;
//		case WRONG_ID_PIN:
//			Toast.makeText(this, getString(R.string.toast_doctormainactivity_pin_no_exists), Toast.LENGTH_SHORT).show();
//			break;
//		default:
//			Toast.makeText(this, getString(R.string.toast_doctormainactivity_other), Toast.LENGTH_SHORT).show();
//		}
//	}
//
//	@Override
//	public void onEntityLoaded(List<EntityUser> user) {
//		this.doctorAdapter.updateList(entityPresenter.getEntityList(getCHCApplication().getHelper()));
//	}
//
//	@Override
//	public void onPatientClicked(String id) {
//		InstantMessageActivity.startActivity(this, id);
//	}
//	@Override
//	public void onContactClicked(String id) {
//		InstantMessageActivity.startActivity(this, id);
//	}
//
//	@Override
//	public void onContactImageClicked(EntityUser user) {
//		MainActivity.startActivity(this, user.getId(), user instanceof MedicalCenterUser);
//	}
//
//	@Override
//	public void onPatientImageClicked(PatientUser user) {
//        // not supported
//	}
//
//}
