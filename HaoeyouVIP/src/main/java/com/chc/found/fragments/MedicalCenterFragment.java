package com.chc.found.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chc.dochoo.CHCApplication;
import com.chc.found.DoctorDetailScrollableTabsActivity;
import com.chc.found.ImageViewerActivity;
import com.chc.found.MainActivity;
import com.chc.found.adapters.PatientMainPagerAdapter;
import com.chc.found.models.AddEntityState;
import com.chc.found.models.DoctorUser;
import com.chc.found.models.EntityUser;
import com.chc.found.models.MedicalCenterUser;
import com.chc.found.network.NetworkRequestsUtil;
import com.chc.found.presenters.EntityPresenter;
import com.chc.found.presenters.ExternalIntentPresenter;
import com.chc.found.views.ICenterHomeView;
import com.chc.found.views.ResizableImageView;
import com.chcgp.hpad.util.download.ImageDownloader;
import com.chcgp.hpad.util.fragment.v4.ChildFragmentManagerFixFragment;
import com.devsmart.android.ui.HorizontalListView;
import com.test.found.R;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class MedicalCenterFragment extends ChildFragmentManagerFixFragment implements ICenterHomeView {

    public static final String KEY_PHONE = "电话";
    public static final String KEY_FAX = "传真";
    public static final String KEY_ADDRESS = "地址";
    public static final String KEY_WEBSITE = "网址";

    private Collection<DoctorUser> doctorList;
    private ListView doctorListView;
    private View headerView;

    /*private SupportMapFragment mMapFragment;
    private GoogleMap mMap;*/
    private EntityPresenter presenter;
    private ExternalIntentPresenter externalIntentPresenter;
    private static final float ZOOM_LEVEL = 12;
    private static final String MAP_FRAGMENT_TAG = "mapFragment";
    private static final String TAG = "MedicalCenterFragment";
    private String entityId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_medical_center, container, false);
        presenter = new EntityPresenter(this);
        externalIntentPresenter = new ExternalIntentPresenter();

        doctorListView = (ListView)root.findViewById(R.id.doctors_listview);
        headerView = LayoutInflater.from(this.getActivity()).inflate(R.layout.center_homepage_listview_header, null);
        doctorListView.addHeaderView(headerView);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments == null) return; // error, no argument
        entityId = arguments.getString(PatientMainPagerAdapter.KEY_ENTITY_ID);
        if (entityId == null) return; // error, no entity id
        EntityUser user = presenter.getEntityById(entityId);
        displayEntity(user);
    }

    public static CharSequence getTabTitle(Context context) {
        return context.getString(R.string.tab_title_medical_center);
    }

    private void setupMap(MedicalCenterUser center) {
    /*if (mMap == null && mMapFragment != null) {
        // Try to obtain the map from the SupportMapFragment.
        mMap = mMapFragment.getMap();
        // Check if we were successful in obtaining the map.
        if (mMap != null) {
        	mMap.getUiSettings().setAllGesturesEnabled(false);
        	mMap.getUiSettings().setZoomControlsEnabled(false);
          setUpLocation(center);
        }
    }*/
    }

    private void setUpLocation(MedicalCenterUser center) {
        new AsyncTask<String, Void, List<Address>>() {

            @Override
            protected List<Address> doInBackground(String... params) {
                Activity activity = getActivity();
                if (activity == null) return null;
                Geocoder geoCoder = new Geocoder(activity,
                        Locale.getDefault());
                try {
                    return geoCoder.getFromLocationName(
                            params[0], 1);
                } catch (IOException e) {
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<Address> result) {
                if (result != null) onAddressesLoaded(result);
            }

        }.execute(center.getAddress());
    }

    private void onAddressesLoaded(List<Address> addresses) {
		/*LatLngBounds.Builder builder = new LatLngBounds.Builder();
		try {
			double lat = 0, lng = 0;

			if (addresses.size() > 0) {
				lat = addresses.get(0).getLatitude();
				lng = addresses.get(0).getLongitude();
			} else {
				// no address found, ignore and return
				return;
			}
			LatLng pos = new LatLng(lat, lng);
			builder.include(pos);
			mMap.addMarker(new MarkerOptions().position(pos)
					.title("Doctor"));
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			Toast.makeText(getActivity(), R.string.toast_error_google_service, Toast.LENGTH_SHORT).show();
			return;
		}
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int padding = (int) (50 * metrics.density);
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(builder.build()
				.getCenter(), ZOOM_LEVEL));
//			CameraUpdate upate = CameraUpdateFactory.newLatLngBounds(
//					builder.build(), padding);
//			mMap.animateCamera(upate);*/
    }

    @Override
    public void startDetailedActivity(String centerId, String id) {
        DoctorDetailScrollableTabsActivity.startActivity(getActivity(), centerId, id);
    }

    @Override
    public CHCApplication getCHCApplication() {
        FragmentActivity activity = getActivity();
        if (activity == null) return null;
        return CHCApplication.getInstance(activity);
    }

    @Override
    public void onEntityLoaded(EntityUser user) {
        displayEntity(user);
    }

    private void displayEntity(EntityUser user) {

        if (user == null) return;

        final MedicalCenterUser u = (MedicalCenterUser) user;

        ImageView headIcon = (ImageView) headerView.findViewById(R.id.center_head);
        String url = u.getProfileIconUrl();
        if (url != null && StringUtils.isNotBlank(url)) {
            ImageDownloader.getInstance().download(url, headIcon, getResources(), R.drawable.doc_default);
        } else {
            headIcon.setImageResource(R.drawable.grey_doctor);
        }

        if (u.getFullname() != null && !u.getFullname().equals("null")) {
            ((TextView) headerView.findViewById(R.id.center_name)).setText(u.getFullname());
        } else {
            headerView.findViewById(R.id.center_name).setVisibility(View.GONE);
        }

        if (u.getWorkingHours() != null && !u.getWorkingHours().equals("null")) {
            ((TextView) headerView.findViewById(R.id.center_hours)).setText(u.getWorkingHours());
        } else {
            headerView.findViewById(R.id.center_hours_wrapper).setVisibility(View.GONE);
        }

        if (u.getPin() != null && !u.getPin().equals("null")) {
            ((TextView) headerView.findViewById(R.id.center_pin)).setText(u.getPin());
        } else {
            headerView.findViewById(R.id.center_pin_wrapper).setVisibility(View.GONE);
        }

        ArrayList<Pair<String, String>> contactList = new ArrayList<Pair<String,String>>();
        if (u.getPhone() != null && !u.getPhone().equals("null"))
            contactList.add(new Pair<String, String>(KEY_PHONE, u.getPhone()));
        else
            contactList.add(new Pair<String, String>(KEY_PHONE, ""));

        if (u.getWebsite() != null && !u.getWebsite().equals("null"))
            contactList.add(new Pair<String, String>(KEY_WEBSITE, u.getWebsite()));
        else
            contactList.add(new Pair<String, String>(KEY_WEBSITE, ""));

        if (u.getAddress() != null && !u.getAddress().contains("null"))
            contactList.add(new Pair<String, String>(KEY_ADDRESS, u.getAddress()));
        else
            contactList.add(new Pair<String, String>(KEY_ADDRESS, ""));

        LinearLayout contactWrapper = (LinearLayout) headerView.findViewById(R.id.center_contact_info_wrapper);
        contactWrapper.removeAllViews();
        for (int i = 0; i < contactList.size(); ++i) {
            View item = LayoutInflater.from(getActivity()).inflate(R.layout.center_contact_item, contactWrapper, false);
            TextView title = (TextView) item.findViewById(R.id.contact_title);
            TextView label = (TextView) item.findViewById(R.id.contact_label);
            TextView symbol = (TextView) item.findViewById(R.id.contact_symbol);

            final String key = contactList.get(i).first;
            final String value = contactList.get(i).second;
            title.setText(contactList.get(i).first + ":");
            label.setText(contactList.get(i).second);
            symbol.setText(">");
            contactWrapper.addView(item);

            item.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (key.equals(KEY_PHONE)) {
                        externalIntentPresenter.startCallIntent(getActivity(), value);
                    } else if (key.equals(KEY_WEBSITE)) {
                        externalIntentPresenter.startOpenURLIntent(getActivity(), value);
                    } else if (key.equals(KEY_ADDRESS)) {
                        externalIntentPresenter.startGoogleMapIntent(getActivity(), u.getFullname(), value);
                    }
                }
            });

            if (i < contactList.size() - 1) {
                ImageView iv = new ImageView(getActivity());
                iv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
                iv.setBackgroundColor(0xffB1BCBE);
                contactWrapper.addView(iv);
            }
        }

        if (u.getDescription() != null && u.getDescription().length() > 0) {
            ((TextView) headerView.findViewById(R.id.center_about)).setText(u.getDescription());
        } else {
            headerView.findViewById(R.id.center_about_wrapper).setVisibility(View.GONE);
        }

        HorizontalListView galleryListView = (HorizontalListView) headerView.findViewById(R.id.center_gallery);
        ((MainActivity) getActivity()).mViewPager.setChildId(R.id.center_gallery);
        if (u.getGalleryArray() != null && u.getGalleryArray().length > 0) {
            GalleryAdapter adapter = new GalleryAdapter(u.getGalleryArray(), getActivity());
            galleryListView.setAdapter(adapter);
        } else {
            headerView.findViewById(R.id.center_gallery_wrapper).setVisibility(View.GONE);
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
                            doctorListView
                                    .requestDisallowInterceptTouchEvent(true);
                            // Disable touch on transparent view
                            return false;
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP:
                            // Allow ScrollView to intercept touch events.
                            doctorListView
                                    .requestDisallowInterceptTouchEvent(false);
                            return true;

                        case MotionEvent.ACTION_MOVE:
                            final float preX = x;
                            float nowX = event.getX();
                            if (Math.abs(preX - nowX) > 10) {
                                doctorListView.requestDisallowInterceptTouchEvent(true);
                                return false;
                            } else {
                                doctorListView
                                        .requestDisallowInterceptTouchEvent(false);
                                return true;
                            }

                        default:
                            doctorListView
                                    .requestDisallowInterceptTouchEvent(false);
                            return true;
                    }
                }
            });
        }
		
		/*mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentByTag(MAP_FRAGMENT_TAG);

        // Do a null check to confirm that we have not already instantiated the map.
        if (mMapFragment == null) {
        	mMapFragment = SupportMapFragment.newInstance();
        	
        	// Then we add it using a FragmentTransaction.
            FragmentTransaction fragmentTransaction =
                    getChildFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.center_address_map_container, mMapFragment, MAP_FRAGMENT_TAG);
            fragmentTransaction.commit();
        }*/

//        if (u.getAddress() != null && !u.getAddress().equals("null")) {
//        	new Handler().post(new Runnable() {
//
//        		@Override
//        		public void run() {
//        			setupMap(u);
//        		}
//        	});
//        } else {
        headerView.findViewById(R.id.center_address_visible_container).setVisibility(View.GONE);
//        }

        doctorList = u.getDoctors();
        if (doctorListView.getAdapter() == null) {
            CenterDoctorsListViewAdapter adapter = new CenterDoctorsListViewAdapter(doctorList, this.getActivity());
            doctorListView.setAdapter(adapter);
        }
    }

    @Override
    public void getEntityFailed(AddEntityState state) {
        // TODO Auto-generated method stub

    }

    private class CenterDoctorsListViewAdapter extends BaseAdapter {

        private List<DoctorUser> doctorList;
        private Context context;

        public CenterDoctorsListViewAdapter(Collection<DoctorUser> doctorList,
                                            Context context) {
            super();
            this.doctorList = new ArrayList<DoctorUser>(doctorList);
            this.context = context;
        }

        @Override
        public int getCount() {
            return doctorList.size();
        }

        @Override
        public Object getItem(int position) {
            return doctorList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.center_doctors_listview_item, parent, false);
            ResizableImageView docImage = (ResizableImageView)view.findViewById(R.id.doc_image);
            docImage.setResizeByHeight(true);
            TextView docName = (TextView)view.findViewById(R.id.doc_name);

            final DoctorUser doctor = doctorList.get(position);
            String url = doctor.getProfileIconUrl();
            if (StringUtils.isNotBlank(url))
                ImageDownloader.getInstance().download(url, docImage, context.getResources(), R.drawable.doc_default);
            if (doctor.getFullname() != null && !doctor.getFullname().equals("null"))
                docName.setText(doctor.getFullname());
            else
                docName.setText("");

            view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    startDetailedActivity(entityId, doctor.getId());
                }
            });
            return view;
        }

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
            ImageDownloader.getInstance().download(uri, imageView, getResources(), R.drawable.doc_default);

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

    @Override
    public void onEntityLoaded(List<EntityUser> user) {
        // TODO Auto-generated method stub

    }
}
