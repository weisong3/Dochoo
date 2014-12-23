package com.chc.dochoo.navidrawer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;

import com.chc.dochoo.CHCApplication;
import com.chc.found.DochooCardActivity;
import com.chc.found.FoundSettings;
import com.test.found.R;

public abstract class AbstractNaviDrawerActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public NavigationDrawerFragment getmNavigationDrawerFragment() {
        return mNavigationDrawerFragment;
    }

    public CharSequence getmTitle() {
        return mTitle;
    }

    public void setmTitle(CharSequence mTitle) {
        this.mTitle = mTitle;
    }

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set content view using layout id provided by concrete implementation
        setContentView(getLayoutId()); // the subclass must ensure the layout id is proper
                                        // and inside the layout there is a navigation fragment

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        updateSelectedItem(getIntent().getExtras());
    }

    @Override
    public void onResume() {
        super.onResume();
        CHCApplication.getInstance(this).showNetworkStatusToast();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        updateSelectedItem(intent.getExtras());
    }

    private void updateSelectedItem(Bundle args) {

        // Set up the drawer.
        NaviItems item;
        if (args != null) {
            item = (NaviItems) args.getSerializable(FoundSettings.BUNDLE_KEY_NAVI_ITEM);
            mNavigationDrawerFragment.setSelectedItem(item);
        }
    }

    protected abstract int getLayoutId();

    @Override
    public void onNavigationDrawerItemSelected(NaviItems item) {
        Intent intent = new Intent(this, item.getActivityClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle args = new Bundle();
        args.putSerializable(FoundSettings.BUNDLE_KEY_NAVI_ITEM, item);
        intent.putExtras(args);
        if(item == NaviItems.MY_QR_CODE){
            DochooCardActivity.startActivity(this,CHCApplication.getInstance(this).getUser());
        }
        else this.startActivity(intent);
        this.overridePendingTransition(0, 0);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    public boolean isDrawerOpen() {
        if (mNavigationDrawerFragment == null) return false;
        return mNavigationDrawerFragment.isDrawerOpen();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

}
