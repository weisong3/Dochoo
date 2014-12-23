package com.chc.dochoo.contacts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.SearchView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chc.dialog.EditTextDialogFactory;
import com.chc.dialog.EditTextDialogHandler;
import com.chc.dialog.EditTextDialogResult;
import com.chc.dialog.ListViewDialogFactory;
import com.chc.dialog.ListViewDialogHandler;
import com.chc.dialog.ListViewDialogResult;
import com.chc.dochoo.CHCApplication;
import com.chc.dochoo.conversations.PrivateConversationActivity;
import com.chc.dochoo.navidrawer.AbstractNaviDrawerActivity;
import com.chc.dochoo.navidrawer.NaviItems;
import com.chc.dochoo.navidrawer.NavigationDrawerFragment;
import com.chc.found.DochooCardActivity;
import com.chc.found.FoundSettings;
import com.chc.found.MainActivity;
import com.chc.found.models.AddEntityState;
import com.chc.found.models.ColleagueCenterUser;
import com.chc.found.models.ColleagueDoctorUser;
import com.chc.found.models.EntityModel;
import com.chc.found.models.EntityUser;
import com.chc.found.models.MedicalCenterUser;
import com.chc.found.models.PatientUser;
import com.chc.found.presenters.EntityPresenter;
import com.chc.found.utils.QrcodeUtil;
import com.chc.found.utils.SharedPreferenceUtil;
import com.chc.found.views.IEntityView;
import com.chc.views.TypefacedTextView;
import com.google.zxing.client.android.CaptureActivity;
import com.test.found.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ContactActivity extends AbstractNaviDrawerActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        IEntityView, OnContactOptionListener, PatientListFragment.OnPatientOptionListener, SearchView.OnQueryTextListener{

    private static final int REQUEST_CODE = 432;

    public static void startForPrivateConversation(Activity a) {
        Intent intent = new Intent(a, ContactActivity.class);
        Bundle extras = new Bundle();
//        extras.putBoolean(FLAG_IS_PRIVATE_CONVERSATION_PICK, true);
        intent.putExtras(extras);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        a.startActivity(intent);

        a.overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    public static void startDefaultContactList(Activity a) {
        Intent intent = new Intent(a, ContactActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(FoundSettings.BUNDLE_KEY_NAVI_ITEM, NaviItems.CONTACT_PROFESSIONALS);
        intent.putExtras(extras);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        a.startActivity(intent);

        a.overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    private EntityPresenter entityPresenter;
    private String mUserId;

    //DoctorContactListAdapter doctorAdapter;
    AlphaDoctorContactListAdapter doctorAdapter;
    //PatientListFragment.DoctorPatientListAdapter patientAdapter;
    AlphaDoctorPatientListAdapter patientAdapter;
    ColleagueExpandableListAdapter colleagueAdapter;

    SearchView searchView;
    ViewPager mViewPager;
    PagerTabStrip mTabStrip;

    @Override
    public void onBackPressed() {
        finish();
        this.overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
       /* if(mViewPager.getCurrentItem() == 0){
            List<EntityUser> cached = entityPresenter.getEntityList(getCHCApplication().getHelper());
            doctorAdapter.updateList(cached);
        }
        else if(mViewPager.getCurrentItem() == 1){
            List<ColleagueCenterUser> cached = EntityModel.getAllColleagueCenterUsers(getCHCApplication().getHelper());
            colleagueAdapter.updateList(cached);
        }
        else if(mViewPager.getCurrentItem() == 2){
            List<PatientUser> cached = entityPresenter.getPatientUserList();
            patientAdapter.updateList(cached);
        }*/
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        doCreate(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView has been called in super class
        doCreate(getIntent());
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if(doctorAdapter != null){
            //Professionals
            List<EntityUser> cached = entityPresenter.getEntityList(getCHCApplication().getHelper());
            List<EntityUser> result = new ArrayList<>();
            for(EntityUser eu:cached){
                if(Pattern.compile(Pattern.quote(s), Pattern.CASE_INSENSITIVE).matcher(eu.getFullname()).find()){
                    result.add(eu);
                }
            }
            doctorAdapter.updateList(result);
        }
        if(colleagueAdapter != null){
            List<ColleagueCenterUser> result = new ArrayList<>();
            List<ColleagueCenterUser> cached = EntityModel.getAllColleagueCenterUsers(getCHCApplication().getHelper());
            for(ColleagueCenterUser ccu:cached){
                List<ColleagueDoctorUser> cachedDoctors = ccu.getColleagueDoctors();
                ColleagueCenterUser temp = ColleagueCenterUser.clone(ccu);
                for(int index=0;index<cachedDoctors.size();index++){
                    ColleagueDoctorUser cdu = cachedDoctors.get(index);
                    if(Pattern.compile(Pattern.quote(s), Pattern.CASE_INSENSITIVE).matcher(cdu.getFullname()).find()){
                        temp.addColleagueDoctor(cdu);
                    }
                }
                result.add(temp);
            }
            colleagueAdapter.updateList(result);
        }
        if(patientAdapter != null){
            List<PatientUser> cached = entityPresenter.getPatientUserList();
            List<PatientUser> result = new ArrayList<>();
            for(PatientUser pu:cached){
                if(Pattern.compile(Pattern.quote(s), Pattern.CASE_INSENSITIVE).matcher(pu.getFullname()).find()){
                    result.add(pu);
                }
            }
            patientAdapter.updateList(result);
        }
        else{
            Log.e("ContactActivity","Contact Search ERROR: unknown fragment index when query text change");
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }



    private void doCreate(Intent intent) {
        if (entityPresenter == null)
            entityPresenter = new EntityPresenter(this);

        boolean isPatient = FoundSettings.getInstance(this).isPatient();
        mUserId = getCHCApplication().getUserId();

//        if (intent != null && intent.getExtras() != null) {
//            isPrivatePick = intent.getExtras().getBoolean(FLAG_IS_PRIVATE_CONVERSATION_PICK, false);
//        } else {
//            isPrivatePick = false;
//        }

        if (mViewPager == null)
            mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(3);
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (mViewPager.getAdapter() == null) {
            if (isPatient) {
                mViewPager.setAdapter(new PatientViewPagerAdapter(getSupportFragmentManager()));
            } else {
                mViewPager.setAdapter(new ProfessionalViewPagerAdapter(getSupportFragmentManager()));
            }
        }
        if (mTabStrip == null) {
            mTabStrip = (PagerTabStrip) findViewById(R.id.pager_tabstrip);
            mTabStrip.setTabIndicatorColor(getResources().getColor(R.color.default_dochoo_orange));
            mTabStrip.setTextColor(getResources().getColor(R.color.dochoo_default_text_color));
            // for each sub-view of the pager strip, change typeface if is text view
            for (int i = 0; i < mTabStrip.getChildCount(); ++i) {
                View nextChild = mTabStrip.getChildAt(i);
                if (nextChild instanceof TextView) {
                    TextView textViewToConvert = (TextView) nextChild;
                    textViewToConvert.setTypeface(
                            TypefacedTextView.getTypeface(
                                    getAssets(),
                                    FoundSettings.TYPEFACE_OPEN_SANS));
                }
            }
        }
        // hard coded logic for tab indices
        if (intent != null && intent.getExtras() != null) {
            Serializable serializable = intent.getExtras().getSerializable(FoundSettings.BUNDLE_KEY_NAVI_ITEM);
            if (serializable instanceof NaviItems) {
                switch ((NaviItems) serializable) {
                case CONTACT_PROFESSIONALS:
                    mViewPager.setCurrentItem(0);
                    break;
                case CONTACT_COLLEAGUES:
                    mViewPager.setCurrentItem(1);
                    break;
                case CONTACT_PATIENTS:
                    mViewPager.setCurrentItem(2);
                    break;
                }
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_contact;
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                setmTitle(getString(R.string.title_section1));
                break;
            case 2:
                setmTitle(getString(R.string.title_section2));
                break;
            case 3:
                setmTitle(getString(R.string.title_section3));
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getmTitle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.contact, menu);
            restoreActionBar();
            searchView = (SearchView)menu.findItem(R.id.action_search_contact).getActionView();
            searchView.setOnQueryTextListener(this);
            MenuItem menuItem = menu.findItem(R.id.action_search_contact);
            //menu.findItem(R.id.action_dochoo_card).setVisible(false);
            menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    if (mViewPager.getCurrentItem() == 0) {
                        //Professionals
                        List<EntityUser> cached = entityPresenter.getEntityList(getCHCApplication().getHelper());
                        doctorAdapter.updateList(cached);

                        Log.i("ContactSearchTest", "pro search closed");
                    } else if (mViewPager.getCurrentItem() == 1) {
                        colleagueAdapter.updateList(EntityModel.getAllColleagueCenterUsers(getCHCApplication().getHelper()));
                    } else if (mViewPager.getCurrentItem() == 2) {
                        List<PatientUser> cached = entityPresenter.getPatientUserList();
                        patientAdapter.updateList(cached);
                    } else {
                        Log.e("ContactActivity", "Contact Search ERROR:unknown fragment index when close search view");
                    }
                    return true; // Return true to collapse action view
                }

                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    return true; // Return true to expand action view
                }
            });
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            /*case R.id.action_dochoo_card:
                DochooCardActivity.startActivity(this, CHCApplication.getInstance(this).getUser());
                return true;*/
            case R.id.action_scan:
                showAddingSelectionDialog();
                return true;
            case R.id.action_search_contact:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEntityLoaded(EntityUser user) {
        if (this.doctorAdapter != null) {
            SharedPreferenceUtil.setRefreshIdle(this,false);
            this.doctorAdapter.updateList(entityPresenter.getEntityList(getCHCApplication().getHelper()));
            Toast.makeText(this, getString(R.string.contact_add_success), Toast.LENGTH_SHORT).show();
        } else {
            getEntityFailed(AddEntityState.OTHER);
        }
    }

    @Override
    public void getEntityFailed(AddEntityState state) {
        SharedPreferenceUtil.setRefreshIdle(this,false);
        switch (state) {
            case DUPLICATE:
                Toast.makeText(this, getString(R.string.toast_doctormainactivity_pin_already_added), Toast.LENGTH_SHORT).show();
                break;
            case WRONG_ID_PIN:
                Toast.makeText(this, getString(R.string.toast_doctormainactivity_pin_no_exists), Toast.LENGTH_SHORT).show();
                break;
            case BLOCKED:
                Toast.makeText(this, getString(R.string.toast_doctormainactivity_blocked), Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, getString(R.string.toast_doctormainactivity_other), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEntityLoaded(List<EntityUser> user) {
        if (this.doctorAdapter != null && this.patientAdapter != null) {
           List<PatientUser> patientlist = new ArrayList<PatientUser>();
           for(EntityUser u:user){
               if(u instanceof PatientUser)
                   patientlist.add((PatientUser)u);
           }
           this.patientAdapter.updateList(patientlist);
           this.doctorAdapter.updateList(entityPresenter.getEntityList(getCHCApplication().getHelper()));
        }
        else {
            getEntityFailed(AddEntityState.OTHER);
        }
    }

    @Override
    public void onPatientClicked(String id) {
        if (!id.equals(mUserId)) {
            PrivateConversationActivity.startActivity(this, id, null);
        }
    }

    @Override
    public void onPatientLongClicked(String id) {
        showDeleteDialog(id);
    }

    @Override
    public void onContactClicked(String id) {
        if (!id.equals(mUserId)) {
            PrivateConversationActivity.startActivity(this, id, null);
        }
    }

    @Override
    public void onContactLongClicked(String id) {
        showDeleteDialog(id);
    }

    @Override
    public void onContactImageClicked(EntityUser user) {
        MainActivity.startActivity(this, user.getId(), user);
    }

    @Override
    public void onPatientImageClicked(PatientUser user) {
        MainActivity.startActivity(this, user.getId(), user);
    }

    @Override
    public CHCApplication getCHCApplication() {
        return CHCApplication.getInstance(this);
    }


    private class PatientViewPagerAdapter extends FragmentPagerAdapter {

        public PatientViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return new PatientContactProfessionalFragment();
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            int resId = PatientContactListType.values()[position].getResId();
            return getString(resId);
        }
    }

    private void confirmDialogAction( final int type, final String id){
        String confirmMessage = "";
        EntityUser user = EntityModel.getEntityById(getCHCApplication().getHelper(),id);
        switch (type){
            case 1:
                //delete
                confirmMessage = getString(R.string.delete_contact_final_confirm);
                break;
            case 2:
                //block
                confirmMessage = getString(R.string.block_contact_final_confirm);
                break;
        }
        new AlertDialog.Builder(this)
                .setMessage(confirmMessage + " " +user.getFullname() + " ?")
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton(R.string.confirm_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (type) {
                            case 1:
                                //delete
                                entityPresenter.deleteContact(id, getCHCApplication().getUserId(), getCHCApplication().getRegId());
                                break;
                            case 2:
                                //block
                                entityPresenter.blockContact(id, getCHCApplication().getUserId(), getCHCApplication().getRegId());
                                break;
                        }
                    }
                })
                .create()
                .show();
    }

    private void showDeleteDialog(final String id){
        if(!id.equals(mUserId)){


            new AlertDialog.Builder(this)
                    .setMessage(R.string.delete_contact_confirm)
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setNeutralButton(R.string.block_contact_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            confirmDialogAction(2, id);
                        }
                    })
                    .setPositiveButton(R.string.delete_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            confirmDialogAction(1, id);
                        }
                    })
                    .create()
                    .show();

        }
    }

    private class ProfessionalViewPagerAdapter extends FragmentPagerAdapter {

        public ProfessionalViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return AbstractContactListFragment.newInstance(ContactListType.values()[position]);
        }

        @Override
        public int getCount() {
            return ContactListType.values().length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            int resId = ContactListType.values()[position].getResId();
            return getString(resId);
        }

    }

    private void startCaptureActivityForResult() {
        Intent intent = new Intent(this, CaptureActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String resultStr = data.getStringExtra("code");
            onAddContact(resultStr);
        } else {
            Toast.makeText(this, getString(R.string.toast_entitylistactivity_scan_failed), Toast.LENGTH_LONG)
                    .show();
        }

    }

    private void onAddContact(String resultStr) {
        SharedPreferenceUtil.setRefreshIdle(this,true);
        entityPresenter.addContact(FoundSettings.getInstance(this).isPatient(), QrcodeUtil.getIdOrPin(resultStr), getCHCApplication().getUserId(), getCHCApplication().getRegId());
    }

    private void showAddingSelectionDialog() {
        final int PIN_INDEX = 0;
        final int QR_INDEX = 1;
        ArrayList<String> addUserMethod = new ArrayList<String>();
        addUserMethod.add(PIN_INDEX, getString(R.string.button_doctormainactivity_enter_pin));
        addUserMethod.add(QR_INDEX, getString(R.string.button_doctormainactivity_scan));
        ListViewDialogHandler handler
                = (ListViewDialogHandler) new ListViewDialogFactory().createDialogHandler(
                this,
                "Add Entity User Dialog",
                new ListViewDialogResult() {

                    @Override
                    public void onItemClicked(int position) {
                        if (position == PIN_INDEX) {
                            final EditText input = new EditText(ContactActivity.this);
                            new AlertDialog.Builder(ContactActivity.this)
                                            .setTitle(getString(R.string.title_dialog_doctormainactivity_input_pin))
                                            .setView(input)
                                    .setPositiveButton(getString(R.string.ok_button), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            onAddContact(input.getText().toString());
                                        }
                                    })
                                    .setNegativeButton(getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).show();
                        } else if (position == QR_INDEX) {
                            startCaptureActivityForResult();
                        }
                    }
                });
        ListViewDialogFactory.showDialog(handler, null, null, addUserMethod, R.layout.entity_listview_item_add_menu);
    }

    /*public void setDoctorAdapter(DoctorContactListAdapter doctorAdapter) {
        this.doctorAdapter = doctorAdapter;
    }*/
    public void setAlphaDoctorAdapter(AlphaDoctorContactListAdapter adapter){
        this.doctorAdapter = adapter;
    }
    /*public void setPatientAdapter(PatientListFragment.DoctorPatientListAdapter patientAdapter){
        this.patientAdapter = patientAdapter;
    }*/

    public void setPatientAdapter(AlphaDoctorPatientListAdapter patientAdapter) {
        this.patientAdapter = patientAdapter;
    }

    public void setColleagueAdapter(ColleagueExpandableListAdapter colleagueAdapter) {
        this.colleagueAdapter = colleagueAdapter;
    }
}
