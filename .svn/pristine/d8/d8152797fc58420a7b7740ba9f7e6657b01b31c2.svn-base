package com.chc.found.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.chc.dochoo.CHCApplication;
import com.chc.found.ImageViewerActivity;
import com.chc.found.MainActivity;
import com.chc.found.adapters.PatientMainPagerAdapter;
import com.chc.found.models.AddEntityState;
import com.chc.found.models.DoctorUser;
import com.chc.found.models.EntityUser;
import com.chc.found.network.NetworkRequestsUtil;
import com.chc.found.presenters.EntityPresenter;
import com.chc.found.presenters.ExternalIntentPresenter;
import com.chc.found.views.IEntityView;
import com.chcgp.hpad.util.download.ImageDownloader;
import com.chcgp.hpad.util.fragment.v4.ChildFragmentManagerFixFragment;
import com.devsmart.android.ui.HorizontalListView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.test.found.R;

import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Locale;

public class DoctorHomepageFragment extends ChildFragmentManagerFixFragment
		implements IEntityView {

	private static final String TAG = "DoctorHomepageFragment";
	private static final String MAP_FRAGMENT_TAG = "mapFragment";
	private static final float ZOOM_LEVEL = 8;
	private static final LatLng DEFAULT_LL = new LatLng(34.05, -118.25);
	private TextView doctorName;
	private EntityPresenter presenter;
	private TextView doctorInsurance;
	private TextView doctorDegree;
	private TextView doctorSpeciality;
	private ImageView doctorPhoto;
	private TextView doctorResume;
	private LinearLayout doctorAddressListWrapper;
	private GoogleMap mMap;
	private SupportMapFragment mMapFragment;
	private ScrollView mainScrollView;
	private ViewGroup doctorResumeWrapper;
	
	private ExternalIntentPresenter externalIntentPresenter;
	private TextView doctorPIN;
	private HorizontalListView galleryListView;
	private ViewGroup galleryWrapper;
    private String entityId;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View doctorHomepageFragment = inflater.inflate(
				R.layout.doctor_homepage_fragment, container, false);

		mainScrollView = (ScrollView) doctorHomepageFragment
				.findViewById(R.id.doctor_scrollview);
		doctorName = (TextView) doctorHomepageFragment
				.findViewById(R.id.doctor_name);
		doctorPhoto = (ImageView) doctorHomepageFragment
				.findViewById(R.id.doctor_photo);
		doctorInsurance = (TextView) doctorHomepageFragment
				.findViewById(R.id.doctor_insurance);
		doctorAddressListWrapper = (LinearLayout) doctorHomepageFragment
				.findViewById(R.id.doctor_address_list_wrapper);
		doctorDegree = (TextView) doctorHomepageFragment
				.findViewById(R.id.doctor_degree);
		doctorSpeciality = (TextView) doctorHomepageFragment
				.findViewById(R.id.doctor_specialty);
		doctorPIN = (TextView) doctorHomepageFragment.findViewById(R.id.doctor_pin);
		doctorResume = (TextView) doctorHomepageFragment
				.findViewById(R.id.doctor_resume);
		doctorResumeWrapper = (ViewGroup) doctorHomepageFragment
				.findViewById(R.id.doctor_resume_wrapper);
		galleryListView = (HorizontalListView) doctorHomepageFragment.findViewById(R.id.doctor_gallery);
		((MainActivity) getActivity()).mViewPager.setChildId(R.id.doctor_gallery);
		galleryWrapper = (ViewGroup) doctorHomepageFragment.findViewById(R.id.doctor_gallery_wrapper);
		presenter = new EntityPresenter(this);

		externalIntentPresenter = new ExternalIntentPresenter();
		return doctorHomepageFragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);


        Bundle arguments = getArguments();
        if (arguments == null) return; // error, no argument
        entityId = arguments.getString(PatientMainPagerAdapter.KEY_ENTITY_ID);
        if (entityId == null) return; // error, no entity id
		if (StringUtils.isBlank(entityId))
			return;

	}

	@Override
	public void onResume() {
		super.onResume();

		mMapFragment = (SupportMapFragment) getChildFragmentManager()
				.findFragmentByTag(MAP_FRAGMENT_TAG);

		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMapFragment == null) {
			mMapFragment = SupportMapFragment.newInstance();

			// Then we add it using a FragmentTransaction.
			FragmentTransaction fragmentTransaction = getChildFragmentManager()
					.beginTransaction();
			fragmentTransaction.replace(R.id.doctor_address_map_container,
					mMapFragment, MAP_FRAGMENT_TAG);
			fragmentTransaction.commit();
		}

		if (getActivity() != null)
//			presenter.loadEntity(DoctorUser.class);
			setContent((DoctorUser) presenter.getEntityById(entityId));
	}

	private void setMapVisibility(boolean visible) {
		View mapView = getView()
				.findViewById(R.id.doctor_map_visible_container);
		if (mapView != null)
			mapView.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	private void setupMap(DoctorUser doctor) {

		if (mMap == null && mMapFragment != null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = mMapFragment.getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
            	mMap.getUiSettings().setAllGesturesEnabled(false);
				mMap.getUiSettings().setZoomControlsEnabled(false);
				setUpLocation(doctor);
			}
		}

		View view = getView();
		if (view != null) {
			View transparentView = view
					.findViewById(R.id.doctor_map_transparent_layer);
//			if (transparentView != null) {
//				transparentView.setOnTouchListener(new OnTouchListener() {
//
//					@Override
//					public boolean onTouch(View view, MotionEvent event) {
//						int action = event.getAction();
//						switch (action) {
//						case MotionEvent.ACTION_DOWN:
//							// Disallow ScrollView to intercept touch events.
//							mainScrollView
//									.requestDisallowInterceptTouchEvent(true);
//							// Disable touch on transparent view
//							return false;
//
//						case MotionEvent.ACTION_UP:
//							// Allow ScrollView to intercept touch events.
//							mainScrollView
//									.requestDisallowInterceptTouchEvent(false);
//							return true;
//
//						case MotionEvent.ACTION_MOVE:
//							mainScrollView
//									.requestDisallowInterceptTouchEvent(true);
//							return false;
//
//						default:
//							return true;
//						}
//					}
//				});
//			}
		}

	}

	private void setUpLocation(DoctorUser doctor) {
		Geocoder geoCoder = new Geocoder(getActivity().getApplicationContext(),
				Locale.getDefault());
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		if (doctor.getLocations() != null && doctor.getLocations().length > 0) {
			for (int i = 0; i < doctor.getLocations().length; ++i) {
				try {
					List<Address> addresses = geoCoder.getFromLocationName(
							doctor.getLocations()[i], 1);
					// TODO change address according to data

					double lat = 0, lng = 0;

					if (addresses.size() > 0) {
						lat = addresses.get(0).getLatitude();
						lng = addresses.get(0).getLongitude();
					} else {
						// TODO use other method to inform user about address error
						// AlertDialog.Builder adb = new
						// AlertDialog.Builder(getActivity());
						// adb.setTitle("Google Map");
						// adb.setMessage("Cannot get location from address");
						// adb.setPositiveButton("Close",null);
						// adb.show();
					}
					LatLng pos = new LatLng(lat, lng);
					builder.include(pos);
					mMap.addMarker(new MarkerOptions().position(pos)
							.title(getString(R.string.marker_map_title_doctor)));
				} catch (Exception e) {
					Log.e(TAG, e.getMessage(), e);
					Toast.makeText(getActivity(), getString(R.string.toast_error_google_service), Toast.LENGTH_SHORT).show();
					return;
				}
			}
            try {
			    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(builder.build()
				    	.getCenter(), ZOOM_LEVEL));
            } catch (Exception ignored) {}
			if (doctor.getLocations().length > 1) {
				DisplayMetrics metrics = getResources().getDisplayMetrics();
				int padding = (int) (50 * metrics.density);
				CameraUpdate upate = CameraUpdateFactory.newLatLngBounds(
						builder.build(), padding);
                try {
				    mMap.animateCamera(upate);
                } catch (Exception ignored) {}
			}
		}
	}

	private void setContent(final DoctorUser doctor) {

		if (doctor == null || getActivity() == null)
			return;

		if (StringUtils.isNotBlank(doctor.getProfileIconUrl()))
			ImageDownloader.getInstance().download(doctor.getProfileIconUrl(),
					doctorPhoto, getResources(), R.drawable.doc_default);

		else
			doctorPhoto.setImageResource(R.drawable.grey_doctor);
		if (doctor.getFullname() == null || doctor.getFullname().equals("null")) {
			doctorName.setVisibility(View.GONE);
		} else {
			doctorName.setText(doctor.getFullname());
		}
		if (doctor.getAcceptedInsurances() == null || doctor.getAcceptedInsurances().equals("null")) {
			doctorInsurance.setVisibility(View.GONE);
		} else {
			doctorInsurance.setText(doctor.getAcceptedInsurances());
		}
		if (doctor.getDegree() == null || doctor.getDegree().equals("null")) {
			doctorDegree.setVisibility(View.GONE);
		} else {
			doctorDegree.setText(doctor.getDegree());
		}
		if (doctor.getSpeciality() == null || doctor.getSpeciality().equals("null")) {
			doctorSpeciality.setVisibility(View.GONE);
		} else {
			doctorSpeciality.setText(doctor.getSpeciality());
		}
		if (doctor.getDescription() == null || doctor.getDescription().equals("null")) {
			doctorResumeWrapper.setVisibility(View.GONE);
		} else {
			doctorResume.setText(doctor.getDescription());
		}
		if (doctor.getPin() == null || doctor.getPin().equals("null")) {
			doctorPIN.setVisibility(View.GONE);
		} else {
			doctorPIN.setText(doctor.getPin());
		}
		doctorAddressListWrapper.removeAllViews();
		LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (doctor.getPhones() != null) {
			for (int i = 0; i < doctor.getLocations().length; ++i) {
				View view = inflater.inflate(R.layout.doctor_address_list_item, null, false);
				TextView officeHours = (TextView) view.findViewById(R.id.address_item_hours);
				final TextView officeName = (TextView) view.findViewById(R.id.address_item_officename);
				final TextView address = ((TextView) view.findViewById(R.id.address_item_textview));
				final TextView phone = (TextView) view.findViewById(R.id.address_phone_textview);
				if (doctor.getWorkingHours() != null && !doctor.getWorkingHours().equals("null"))
					officeHours.setText(doctor.getWorkingHours());
				else
					officeHours.setVisibility(View.GONE);
				
				if (doctor.getLocations()[i] != null && !doctor.getLocations()[i].contains("null"))
					address.setText(doctor.getLocations()[i].replace(",", ", "));
				else 
					address.setText("");
				
				officeName.setText("Office No." + i);
				
				if (doctor.getPhones()[i] != null && !doctor.getPhones()[i].contains("null"))
					phone.setText(doctor.getPhones()[i]);
				else 
					phone.setText("");
				
				address.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						externalIntentPresenter.startGoogleMapIntent(getActivity(), officeName.getText().toString(), address.getText().toString());
					}
				});
				
				phone.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						externalIntentPresenter.startCallIntent(getActivity(), phone.getText().toString());
					}
				});
				doctorAddressListWrapper.addView(view);
			}
		}

		if (doctor.getLogoList() != null && doctor.getLogoList().length > 0) {
			GalleryAdapter adapter = new GalleryAdapter(doctor.getLogoList(), getActivity());
			galleryListView.setAdapter(adapter);
		} else {
			galleryWrapper.setVisibility(View.GONE);
		}
		
		if (galleryListView != null) {
			galleryListView.setOnTouchListener(new OnTouchListener() {
				private float x;

				@Override
				public boolean onTouch(View view, MotionEvent event) {
					int action = event.getAction();
					switch (action) {
					case MotionEvent.ACTION_DOWN:
						// Disallow ScrollView to intercept touch events.
						x = event.getX();
						mainScrollView
								.requestDisallowInterceptTouchEvent(true);
						// Disable touch on transparent view
						return false;
                    case MotionEvent.ACTION_CANCEL:
					case MotionEvent.ACTION_UP:
						// Allow ScrollView to intercept touch events.
						mainScrollView
								.requestDisallowInterceptTouchEvent(false);
						return true;

					case MotionEvent.ACTION_MOVE:
						final float preX = x;
						float nowX = event.getX();
						if (Math.abs(preX - nowX) > 10)
							mainScrollView.requestDisallowInterceptTouchEvent(true);
						else
							mainScrollView
							.requestDisallowInterceptTouchEvent(false);
						return false;

					default:
                        mainScrollView
                                .requestDisallowInterceptTouchEvent(false);
						return true;
					}
				}
			});
		}
		
//		if (doctor.getLocations() == null || doctor.getLocations().length == 0)
			getView().findViewById(R.id.doctor_map_visible_container).setVisibility(View.GONE);
//		else {
//			new Handler().post(new Runnable() {
//
//				@Override
//				public void run() {
//					setupMap(doctor);
//				}
//			});
//		}
	}

	public static CharSequence getTabTitle(Context context) {
		return context.getString(R.string.tab_title_doctor_home);
	}

	@Override
	public CHCApplication getCHCApplication() {
        FragmentActivity activity = getActivity();
        if (activity == null) return null;
		return CHCApplication.getInstance(activity);
	}

	@Override
	public void onEntityLoaded(EntityUser user) {
		setContent((DoctorUser) user);
	}

	@Override
	public void getEntityFailed(AddEntityState state) {
		// TODO add ***
	}

	@Override
	public void onEntityLoaded(List<EntityUser> user) {
		// TODO Auto-generated method stub
		
	}
	
	private class GalleryAdapter extends BaseAdapter {
		private String[] galleryArray;
		Context context;
		
		public GalleryAdapter(String[] galleryArray, Context context) {
			super();
			this.galleryArray = galleryArray;
			this.context = context;
		}

		@Override
		public int getCount() {
			return galleryArray.length;
		}

		@Override
		public String getItem(int position) {
			return galleryArray[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = LayoutInflater.from(context).inflate(R.layout.center_gallery_item, parent, false);
			ImageView imageView = (ImageView) view.findViewById(R.id.center_gallery_item_imageview);
			final String uri = NetworkRequestsUtil.getGalleryImageUrlString(getItem(position));
			int dimen = getResources().getDimensionPixelSize(R.dimen.center_gallery_item);
			ImageDownloader.getInstance().download(uri, imageView, dimen, dimen, getResources(), R.drawable.doc_default);
			
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), ImageViewerActivity.class);
					Bundle bundle = new Bundle();
					bundle.putStringArray(ImageViewerActivity.GALLARY_ARRAY, galleryArray);
					bundle.putString(ImageViewerActivity.ENTRANCE_ID, uri);
					intent.putExtras(bundle);
					getActivity().startActivity(intent);
					
				}
			});
			return view;
		}
		
	}
}
