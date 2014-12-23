package com.chc.found;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.chc.dochoo.CHCApplication;
import com.chc.dochoo.userlogin.UserLogInActivity;
import com.chc.found.adapters.PatientMainPagerAdapter;
import com.chc.found.models.AddEntityState;
import com.chc.found.models.EntityUser;
import com.chc.found.models.MedicalCenterUser;
import com.chc.found.models.PatientUser;
import com.chc.found.presenters.EntityPresenter;
import com.chc.found.presenters.SignOutPresenter;
import com.chc.found.utils.ImageUtil;
import com.chc.found.views.CustomViewPager;
import com.chc.found.views.IBaseView;
import com.chc.found.views.IEntityView;
import com.chc.found.views.ISwipeyTabsView;
import com.chcgp.hpad.util.download.ImageDownloader;
import com.makeramen.RoundedDrawable;
import com.test.found.R;

import java.util.List;

public class MainActivity extends ActionBarActivity implements ISwipeyTabsView, IBaseView {

	public static final int MESSAGE_PAGE_INDEX = 2;
  
	private static final int VIEWPAGER_OFFSCREEN_LIMIT = 3;
  private static final String TAG = "FoundMain";
  EntityPresenter presenter;
  private CharSequence mTitle;
  public CustomViewPager mViewPager;

  public static final String EXTRA_MESSAGE = "message";
  private int mEntranceItem = 0;
    private String entityId;

    public static void startActivity(Activity activity, String entityId, EntityUser user) {
        if(user instanceof PatientUser){
            startActivity(activity, entityId, false, FragmentTabInfo.FragmentTabClassInfo.PATIENT);
        }
        else{
            boolean isCenter = user instanceof MedicalCenterUser;
            if (isCenter) {
                startActivity(activity, entityId, isCenter, FragmentTabInfo.FragmentTabClassInfo.MEDICAL_CENTER);
            } else {
                startActivity(activity, entityId, isCenter, FragmentTabInfo.FragmentTabClassInfo.DOCTOR);
            }
        }
    }

  public static void startActivity(Activity activity, String entityId, boolean isCenter,
                                   FragmentTabInfo.FragmentTabClassInfo entranceItem) {
	  CHCApplication.getInstance(activity).setCenter(isCenter);
	  Intent intent = new Intent(activity, MainActivity.class);
      Bundle args = new Bundle();
      args.putString(PatientMainPagerAdapter.KEY_ENTITY_ID, entityId);
      args.putSerializable(FoundConstants.BUNDLE_KEY_ENTRANCE, entranceItem);
      intent.putExtras(args);
      activity.startActivity(intent);
      activity.overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main_no_drawer);
    
    mViewPager = (CustomViewPager) findViewById(R.id.viewpager);
    mViewPager.setOffscreenPageLimit(VIEWPAGER_OFFSCREEN_LIMIT);

    presenter = new EntityPresenter(new IEntityView() {
			
			@Override
			public CHCApplication getCHCApplication() {
				return CHCApplication.getInstance(MainActivity.this);
			}
			
			@Override
			public void onEntityLoaded(List<EntityUser> user) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onEntityLoaded(EntityUser user) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void getEntityFailed(AddEntityState state) {
				// TODO Auto-generated method stub
				
			}
		});

      Bundle args = getIntent().getExtras();
      if (args == null) return;
      entityId = args.getString(PatientMainPagerAdapter.KEY_ENTITY_ID);
      if (entityId == null) return;
    
    EntityUser user = presenter.getEntityById(entityId);
    if (user == null) return;
    if (mViewPager.getAdapter() != null)
      return; // if already set, ignore

    PatientMainPagerAdapter adapter = new PatientMainPagerAdapter(this,
       getSupportFragmentManager(), this, user, entityId);

      ActionBar actionBar = getSupportActionBar();
      if (actionBar != null)
          actionBar.setDisplayHomeAsUpEnabled(true);
    
    int dimen = getResources().getDimensionPixelSize(R.dimen.logo_size);
    ImageDownloader.getInstance().downloadWithCallback(this, user.getProfileIconUrl(), dimen,
    		new ImageDownloader.Callback() {
					
					@Override
					public void onImageLoaded(Bitmap bitmap) {
						try {
                            ActionBar actionBar = getSupportActionBar();
                            if (actionBar != null) {
                                Drawable logo = ImageUtil.createIconImageView(getResources(), bitmap);
                                if (logo instanceof RoundedDrawable) {
                                    ((RoundedDrawable) logo).setScaleType(ImageView.ScaleType.CENTER_CROP)
                                            .setCornerRadius(getResources().getDimensionPixelSize(R.dimen.icon_action_bar_radius))
                                            .setBorderWidth(getResources().getDimensionPixelSize(R.dimen.icon_border_size))
                                            .setBorderColors(getResources().getColorStateList(R.color.default_dochoo_orange))
                                            .setOval(true);
                                }
                                actionBar.setLogo(logo);
                            }
						} catch (Exception e) {}
					}
				});
    
    getEntranceItem(getIntent());
    
    mViewPager.setAdapter(adapter);
    mViewPager.setCurrentItem(mEntranceItem);
  }

  private void getEntranceItem(Intent intent) {
    if (intent == null || intent.getExtras() == null) {
      return;
    }
      Bundle args = intent.getExtras();
      FragmentTabInfo.FragmentTabClassInfo s = (FragmentTabInfo.FragmentTabClassInfo) args.getSerializable(FoundConstants.BUNDLE_KEY_ENTRANCE);
    mEntranceItem = FragmentTabInfo.getTabPosition(s);
  }
  
  @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.doctor_main_activity, menu);
		return super.onCreateOptionsMenu(menu);
	}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
  	int itemId = item.getItemId();
  	if (itemId == android.R.id.home) {
  		onBackPressed();
  		return true;
  	} /*else if (itemId == R.id.action_dochoo_card) {
  		DochooCardActivity.startActivity(this, presenter.getEntityById(entityId));
  	}*/
		return super.onOptionsItemSelected(item);
	}

	private void AskAndSignOut() {
		new AlertDialog.Builder(this)
		.setTitle(R.string.logout_title)
		.setMessage(R.string.logout_message)
		.setPositiveButton(R.string.logout_confirm, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				new SignOutPresenter(MainActivity.this).signOut();
				UserLogInActivity.startLogInActivity(MainActivity.this);
				finish();
			}
		})
		.setNegativeButton(R.string.cancel, null)
		.create()
		.show();
	}
  
	@Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    getEntranceItem(intent);

    if (mViewPager.getAdapter() != null) {
      mViewPager.setCurrentItem(mEntranceItem);
    }
  }

  @Override
  public void setCurrentPage(int position) {
    mViewPager.setCurrentItem(position);
  }

  @Override
  public void setTitle(CharSequence title) {
    mTitle = title;
      ActionBar actionBar = getSupportActionBar();
      if (actionBar != null)
          actionBar.setTitle(mTitle);
  }

	@Override
	public CHCApplication getCHCApplication() {
		return CHCApplication.getInstance(this);
	}

}
