//package com.chc.found;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.support.v4.app.ActionBarActivity;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.OnTouchListener;
//import android.view.ViewGroup;
//import android.widget.AbsListView;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.ListView;
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
//import com.chc.dochoo.profiles.ProfileActivity;
//import com.chc.dochoo.userlogin.UserLogInActivity;
//import com.chc.found.FragmentTabInfo.FragmentTabClassInfo;
//import com.chc.found.models.AddEntityState;
//import com.chc.found.models.DatabaseHelper;
//import com.chc.found.models.DoctorUser;
//import com.chc.found.models.EntityUser;
//import com.chc.found.models.MedicalCenterUser;
//import com.chc.found.models.PatientUser;
//import com.chc.dochoo.profiles.AbstractProfilePresenterFactory;
//import com.chc.found.presenters.EntityPresenter;
//import com.chc.dochoo.profiles.IProfilePresenter;
//import com.chc.dochoo.profiles.IProfilePresenterFactory;
//import com.chc.found.presenters.PushServicePresenter;
//import com.chc.found.presenters.PushServicePresenter.IPushServiceView;
//import com.chc.found.presenters.SignOutPresenter;
//import com.chc.found.utils.QrcodeUtil;
//import com.chc.found.utils.SharedPreferenceUtil;
//import com.chc.found.views.IEntityView;
//import com.chc.found.views.IProfileView;
//import com.chcgp.hpad.util.download.ImageDownloader;
//import com.chcgp.scan.client.android.CaptureActivity;
//import com.test.found.R;
//
//import org.apache.commons.lang.StringUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class EntityListActivity extends ActionBarActivity implements
//		IPushServiceView, IEntityView, IProfileView{
//
//	private static final int PICTURE_RESULT = 0;
//	private static final int REQUESTCODE = 1;
//
//	private View headerView;
//	private ListView entityListView;
//	private EntityListViewAdapter adapter;
//	private EntityPresenter presenter;
//	private IProfilePresenter profilePresenter;
//	private PushServicePresenter pushPresenter;
//	private volatile boolean receiverRegistered = false;
//	private IntentFilter intentFilter;
//	private ImageView imageView;
//	private BroadcastReceiver receiver = new MyBroadcastReceiver();
//
//    @Override
//    public void onBackPressed() {
//        finish();
//    }
//
//    @Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_entity_list);
//		entityListView = (ListView) findViewById(R.id.entity_list);
//		presenter = new EntityPresenter(this);
//		pushPresenter = new PushServicePresenter(this);
//		if (profilePresenter == null) {
//			IProfilePresenterFactory factory = AbstractProfilePresenterFactory.getFactory(PatientUser.class);
//			profilePresenter = factory.getPresenter(this);
//		}
//		intentFilter = new IntentFilter(getString(R.string.im_action));
//
//
//        checkPlayServices();
//
//        CHCApplication chcApplication = CHCApplication.getInstance(this);
//
//        DatabaseHelper helper = chcApplication.getHelper();
//
//        List<EntityUser> entityList = presenter.getEntityList(helper);
//        if (adapter == null) {
//            this.adapter = new EntityListViewAdapter(this, entityList);
//        } else {
//            this.adapter.setEntityList(entityList);
//        }
//
//        if (headerView == null) {
//            headerView = new View(this);
//            headerView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 15));
//            entityListView.addHeaderView(headerView);
//        }
//
//        entityListView.setAdapter(adapter);
//
//        presenter.loadProfessionalList(helper, chcApplication.getUserId(), chcApplication.getRegId());
//	}
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//	}
//
//	@Override
//	public void onPause() {
//		super.onPause();
//		if (receiverRegistered) {
//			this.unregisterReceiver(receiver);
//			synchronized (this) {
//				receiverRegistered = false;
//			}
//		}
//	}
//
//	/**
//	 * Check the device to make sure it has the Google Play Services APK. If it
//	 * doesn't, display a dialog that allows users to download the APK from the
//	 * Google Play Store or enable it in the device's system settings.
//	 */
//	private void checkPlayServices() {
//		pushPresenter.checkPlayServices();
//	}
//
//    public static void start(Activity activity) {
//        Intent intent = new Intent(activity, EntityListActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        activity.startActivity(intent);
//    }
//
//    public class EntityListViewAdapter extends BaseAdapter {
//
//		private Context context;
//		private List<EntityUser> entityList;
//
//		public EntityListViewAdapter(Context context,
//				List<EntityUser> entityList) {
//			super();
//			this.context = context;
//			this.entityList = entityList;
//		}
//
//		@Override
//		public int getCount() {
//			return entityList.size() + 1;
//		}
//
//		@Override
//		public EntityUser getItem(int position) {
//			return entityList.get(position-1);
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return position;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			View view;
//			if (position == 0) {
//				view = LayoutInflater.from(context).inflate(
//						R.layout.entity_list_item_userprofile, parent, false);
//				final ViewGroup wrapper = (ViewGroup) view.findViewById(R.id.item_list_profile_wrapper);
//				ViewGroup signInWrapper = (ViewGroup) view.findViewById(R.id.item_list_profile_signin);
//				ViewGroup notSignInWrapper = (ViewGroup) view.findViewById(R.id.item_list_profile_not_signin);
//				if (CHCApplication.getInstance(EntityListActivity.this).isSignedIn()) {
//					notSignInWrapper.setVisibility(View.INVISIBLE);
//					wrapper.setOnTouchListener(new OnTouchListener() {
//
//						@Override
//						public boolean onTouch(View v, MotionEvent event) {
//							if (event.getAction() == MotionEvent.ACTION_DOWN) {
//								wrapper.setAlpha(0.8f);
//								return true;
//							} else if (event.getAction() == MotionEvent.ACTION_UP) {
//								wrapper.setAlpha(1f);
//								ProfileActivity.startActivity(EntityListActivity.this);
//								return true;
//							} else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
//								wrapper.setAlpha(1f);
//								return true;
//							}
//							return false;
//						}
//					});
//					imageView = (ImageView) view.findViewById(R.id.entity_list_item_profile_image);
//					Bitmap image = SharedPreferenceUtil.readUserImage(EntityListActivity.this);
//					if (image != null) {
//						imageView.setImageBitmap(image);
//					}
//					imageView.setOnClickListener(new View.OnClickListener() {
//
//						@Override
//						public void onClick(View v) {
//							Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//					        EntityListActivity.this.startActivityForResult(camera, PICTURE_RESULT);
//						}
//					});
//					TextView username = (TextView) view.findViewById(R.id.entity_list_item_profile_username);
//					if (CHCApplication.getInstance(EntityListActivity.this).getUserName() != null && !CHCApplication.getInstance(EntityListActivity.this).getUserName().equals("null")) {
//						username.setText(CHCApplication.getInstance(EntityListActivity.this).getUserFullname());
//					} else {
//						username.setVisibility(View.GONE);
//					}
//
//					TextView fullname = (TextView) view.findViewById(R.id.entity_list_item_profile_fullname);
//					if (CHCApplication.getInstance(EntityListActivity.this).getUserFullname() != null && !CHCApplication.getInstance(EntityListActivity.this).getUserFullname().equals("null")) {
//						fullname.setText(CHCApplication.getInstance(EntityListActivity.this).getUserFullname());
//					} else {
//						fullname.setVisibility(View.GONE);
//					}
//				} else {
//					signInWrapper.setVisibility(View.INVISIBLE);
//					wrapper.setOnTouchListener(new OnTouchListener() {
//
//
//						@Override
//						public boolean onTouch(View v, MotionEvent event) {
//							if (event.getAction() == MotionEvent.ACTION_DOWN) {
//								wrapper.setAlpha(0.8f);
//								return true;
//							} else if (event.getAction() == MotionEvent.ACTION_UP) {
//								wrapper.setAlpha(1f);
//								UserLogInActivity.startLogInActivity(EntityListActivity.this);
//								return true;
//							} else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
//								wrapper.setAlpha(1f);
//								return true;
//							}
//							return false;
//						}
//					});
//				}
////			} else if (position == getCount() - 1) {
////				view = LayoutInflater.from(context).inflate(R.layout.entity_listview_item_add, parent, false);
////				final View wrapper = view.findViewById(R.id.entity_list_item_add_wrapper);
////				wrapper.setOnTouchListener(new OnTouchListener() {
////
////
////					@Override
////					public boolean onTouch(View v, MotionEvent event) {
////						if (event.getAction() == MotionEvent.ACTION_DOWN) {
////							wrapper.setAlpha(0.8f);
////							return true;
////						} else if (event.getAction() == MotionEvent.ACTION_UP) {
////							wrapper.setAlpha(1f);
////							showAddingSelectionDialog();
////							return true;
////						} else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
////							wrapper.setAlpha(1f);
////							return true;
////						}
////						return false;
////					}
////				});
//			} else {
//				view = LayoutInflater.from(context).inflate(
//						R.layout.entity_list_item_doctor, parent, false);
//				final ViewGroup wrapper = (ViewGroup) view.findViewById(R.id.entity_list_item_wrapper);
//
//				ImageView imageView = (ImageView) view
//						.findViewById(R.id.entity_list_item_doctor_image);
//				ViewGroup imageWrapper = (ViewGroup) view.findViewById(R.id.entity_list_item_doctor_image_wrapper);
//				TextView name = (TextView) view
//						.findViewById(R.id.entity_list_item_doctor_name);
//				TextView hour = (TextView) view
//						.findViewById(R.id.entity_list_item_doctorl_hours);
//				TextView addressOrSpecialty = (TextView) view
//						.findViewById(R.id.entity_list_item_doctor_addressOrSpecialty);
//				final TextView newMessage = (TextView) view.findViewById(R.id.entity_list_item_doctor_new_message);
//				final EntityUser entityUser = getItem(position);
//				wrapper.setOnTouchListener(new OnTouchListener() {
//
//					@Override
//					public boolean onTouch(View v, MotionEvent event) {
//						if (event.getAction() == MotionEvent.ACTION_DOWN) {
//							wrapper.setAlpha(0.8f);
//							return true;
//						} else if (event.getAction() == MotionEvent.ACTION_UP) {
//							wrapper.setAlpha(1f);
////							if (entityUser.getNumUnread() == 0) {
//								MainActivity.startActivity(EntityListActivity.this, entityUser.getId(), entityUser instanceof MedicalCenterUser);
////							} else {
////								MainActivity.startActivity(EntityListActivity.this, entityUser.getId(), entityUser instanceof MedicalCenterUser, FragmentTabClassInfo.MESSAGEPAGE);
////							}
//							return true;
//						} else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
//							wrapper.setAlpha(1f);
//							return true;
//						}
//						return false;
//					}
//				});
//				if (entityUser.getFullname() != null && !entityUser.getFullname().equals("null")) {
//					name.setText(entityUser.getFullname());
//				} else {
//					name.setVisibility(View.GONE);
//				}
//
//				if (entityUser.getWorkingHours() != null && !entityUser.getWorkingHours().equals("null")) {
//					hour.setText(entityUser.getWorkingHours());
//				} else {
//					hour.setVisibility(View.GONE);
//				}
//
//				String url = entityUser.getProfileIconUrl();
//				if (url != null && StringUtils.isNotBlank(url)) {
//					ImageDownloader.getInstance().download(url, imageView,
//							getResources(), R.drawable.doc_default);
//				}
//
//				if (entityUser instanceof DoctorUser) {
//					if ((((DoctorUser) entityUser).getSpeciality() != null && !(((DoctorUser) entityUser)
//							.getSpeciality()).equals("null"))) {
//						addressOrSpecialty.setText(((DoctorUser) entityUser)
//								.getSpeciality());
//					} else {
//						addressOrSpecialty.setVisibility(View.GONE);
//					}
//				} else if (entityUser instanceof MedicalCenterUser) {
//					if (((MedicalCenterUser) entityUser).getAddress() != null && !((MedicalCenterUser) entityUser)
//							.getAddress().equals("null")) {
//						addressOrSpecialty.setText(((MedicalCenterUser) entityUser)
//								.getAddress());
//					}
//				}
//
//				if (entityUser.getNumUnread() == 0) {
//					newMessage.setVisibility(View.INVISIBLE);
//				} else {
//					newMessage.setVisibility(View.VISIBLE);
//				}
//			}
//
//			return view;
//		}
//
//		public void setEntityList(List<EntityUser> entityList) {
//			this.entityList = entityList;
//			this.notifyDataSetChanged();
//		}
//
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.main, menu);
//		return super.onCreateOptionsMenu(menu);
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		int id = item.getItemId();
//		switch (id) {
//		case R.id.action_scan:
//			showAddingSelectionDialog();
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
//
//	private void AskAndSignOut() {
//		new AlertDialog.Builder(this)
//		.setTitle(R.string.logout_title)
//		.setMessage(R.string.logout_message)
//		.setPositiveButton(R.string.logout_confirm, new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				new SignOutPresenter(EntityListActivity.this).signOut();
//				UserLogInActivity.startLogInActivity(EntityListActivity.this);
//				finish();
//			}
//		})
//		.setNegativeButton(R.string.cancel, null)
//		.create()
//		.show();
//	}
//
//	private void showAddingSelectionDialog() {
//		final int PIN_INDEX = 0;
//		final int QR_INDEX = 1;
//		ArrayList<String> addUserMethod = new ArrayList<String>();
//		addUserMethod.add(PIN_INDEX, getString(R.string.button_doctormainactivity_enter_pin));
//		addUserMethod.add(QR_INDEX, getString(R.string.button_doctormainactivity_scan));
//		ListViewDialogHandler handler
//        = (ListViewDialogHandler) new ListViewDialogFactory().createDialogHandler(
//                this,
//                "Add Entity User Dialog",
//                new ListViewDialogResult() {
//
//			@Override
//			public void onItemClicked(int position) {
//				if (position == PIN_INDEX) {
//					EditTextDialogHandler editTextDialogHandler
//                    = (EditTextDialogHandler) new EditTextDialogFactory().createDialogHandler(
//                            EntityListActivity.this,
//                            "Input PIN Number",
//                            new EditTextDialogResult() {
//
//						@Override
//						public void onTextEntered(String text, int id) {
//							presenter.onAddEntityRequested(text, getCHCApplication().getUserId(), getCHCApplication().getRegId());
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
//		ListViewDialogFactory.showDialog(handler, null, null, addUserMethod, R.layout.entity_listview_item_add_menu);
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
//		if (requestCode == PICTURE_RESULT) {
//			if (resultCode == Activity.RESULT_OK) {
//                // Display image received on the view
//                 Bundle b = data.getExtras(); // Kept as a Bundle to check for other things in my actual code
//                 Bitmap pic = (Bitmap) b.get("data");
//
//                 if (pic != null) { // Display your image in an ImageView in your layout (if you want to test it)
//                	 SharedPreferenceUtil.writeUserImage(pic, EntityListActivity.this);
//                	 profilePresenter.uploadProfileIcon(pic, getCHCApplication().getUserId(), getCHCApplication().getRegId(), "icon.jpg");
//                	 this.adapter.notifyDataSetChanged();
//                 }
//             } else if (resultCode == Activity.RESULT_CANCELED) {
//
//             }
//		} else if (resultCode == CaptureActivity.RESULTCODE) {
//			String resultStr = data.getStringExtra("code");
//			Toast.makeText(this, resultStr, Toast.LENGTH_LONG).show();
//
//			presenter.onAddEntityRequested(QrcodeUtil.getIdOrPin(resultStr), getCHCApplication().getUserId(), getCHCApplication().getRegId());
//		} else {
//			Toast.makeText(this, getString(R.string.toast_entitylistactivity_scan_failed), Toast.LENGTH_LONG)
//					.show();
//		}
//
//	}
//
//	private void askAboutNotifications() {
//		new AlertDialog.Builder(this)
//				.setMessage(R.string.ask_user_notification)
//				.setPositiveButton(R.string.confirm_yes, new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						FoundSettings.getInstance(EntityListActivity.this).setShowNotification(
//								EntityListActivity.this, true);
//						Toast.makeText(EntityListActivity.this,
//								R.string.notification_enabled,
//								Toast.LENGTH_LONG).show();
//					}
//				})
//				.setNegativeButton(R.string.confirm_no, new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						FoundSettings.getInstance(EntityListActivity.this).setShowNotification(
//								EntityListActivity.this, false);
//						Toast.makeText(EntityListActivity.this,
//								R.string.notification_disabled,
//								Toast.LENGTH_LONG).show();
//					}
//				}).create().show();
//	}
//
//	@Override
//	public void onRegisterPushIdFailed() {
//		// TODO
//		// here somehow the registration failed
//		// tell the user that we need to register again
//		// let the user choose to try again
//
//	}
//
//	@Override
//	public Activity getContextActivity() {
//		return this;
//	}
//
//    @Override
//    public void onRegisterPushIdSucceeded() {
//
//    }
//
//    @Override
//	public CHCApplication getCHCApplication() {
//		return CHCApplication.getInstance(this);
//	}
//
//	@Override
//	public void onEntityLoaded(EntityUser user) {
//		Toast.makeText(this, getString(R.string.toast_entitylistactivity_add_new_success), Toast.LENGTH_SHORT).show();
//		List<EntityUser> entityList = presenter.getEntityList(getCHCApplication().getHelper());
//		this.adapter.setEntityList(entityList);
//		this.adapter.notifyDataSetChanged();
//		synchronized (this) {
//			if (!receiverRegistered) {
//				registerReceiver(receiver, intentFilter);
//				receiverRegistered = true;
//			}
//		}
//	}
//
//	@Override
//	public void getEntityFailed(AddEntityState state) {
//		switch (state) {
//		case DUPLICATE:
//			Toast.makeText(this, getString(R.string.toast_entitylistactivity_pin_duplicate), Toast.LENGTH_SHORT).show();
//			break;
//		case WRONG_ID_PIN:
//			Toast.makeText(this, getString(R.string.toast_entitylistactivity_pin_no_exists), Toast.LENGTH_SHORT).show();
//			break;
//		default:
//			Toast.makeText(this, getString(R.string.toast_entitylistactivity_other), Toast.LENGTH_SHORT).show();
//		}
//	}
//
//	@Override
//	public void onEntityLoaded(List<EntityUser> list) {
//        if (list == null) return;
//        if (adapter == null) {
//            this.adapter = new EntityListViewAdapter(this, list);
//        } else {
//            this.adapter.setEntityList(list);
//        }
//	}
//
//	private class MyBroadcastReceiver extends BroadcastReceiver {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			EntityListActivity.this.adapter.notifyDataSetChanged();
//		}
//
//	}
//
//    @Override
//    public void onUpdateProfileFinished(EntityUser profile) {
//
//    }
//
//    @Override
//	public void onUpdateProfileFailed() {
//		// TODO Auto-generated method stub
//	}
//
//	@Override
//	public void onUploadProfileIconFinished() {
//		Toast.makeText(this, getString(R.string.toast_entitylistactivity_upload_profile_icon_success), Toast.LENGTH_SHORT).show();
//	}
//
//	@Override
//	public void onUploadProfileIconFailed() {
//		Toast.makeText(this, getString(R.string.toast_entitylistactivity_upload_profile_icon_failed), Toast.LENGTH_SHORT).show();
//	}
//}
