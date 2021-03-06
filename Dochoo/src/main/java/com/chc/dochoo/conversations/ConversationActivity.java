package com.chc.dochoo.conversations;

import android.app.Activity;
;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.chc.dochoo.CHCApplication;
import com.chc.dochoo.contacts.ContactActivity;
import com.chc.dochoo.contacts.NewGroupChatMemberActivity;
import com.chc.dochoo.navidrawer.AbstractNaviDrawerActivity;
import com.chc.dochoo.navidrawer.NavigationDrawerFragment;
import com.chc.dochoo.splash.SplashActivity;
import com.chc.found.FoundSettings;
import com.chc.found.network.NetworkRequestsUtil;
import com.test.found.R;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.List;

public class ConversationActivity extends AbstractNaviDrawerActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    final public static int GROUPCHAT_REQUEST_CODE = 36;
    public static void start(Activity a) {
        Intent intent = new Intent(a, ConversationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        a.startActivity(intent);

        a.overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPushIdEmpty()) {
            restartApp();
            return;
        }
    }

    private void restartApp() {
        SplashActivity.start(this);
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        finish();
    }

    private boolean isPushIdEmpty() {
        return StringUtils.isBlank(CHCApplication.getInstance(this).getRegId());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView is called in super class
        loadFragment(new ConversationAllFragment());

        checkMessageLifeTime();
    }

    private void checkMessageLifeTime() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Long msgDeleteInterval = FoundSettings.getInstance(ConversationActivity.this)
                        .getMsgDeleteInterval(ConversationActivity.this);
                Long currentTime = NetworkRequestsUtil.getBestCurrentTime();
                List<InstantMessage> messageList = InstantMessageModel.getInstantMessagesBeforeTime(
                        CHCApplication.getInstance(ConversationActivity.this).getHelper(),
                        currentTime - msgDeleteInterval);
                if (messageList != null && !messageList.isEmpty()) {
                    InstantMessageModel.deleteMultimediaContentFromMsg(messageList);
                    InstantMessageModel.deleteMessages(
                            CHCApplication.getInstance(ConversationActivity.this).getHelper(),
                            messageList);
                }
            }
        }) {

        }.start();
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_conversation;
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
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.conversation, menu);
            menu.findItem(R.id.action_group_info).setVisible(false);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.add_private_msg) {
            ContactActivity.startDefaultContactList(this);
            return true;
        }
        if (id == R.id.add_group_msg) {
            Intent intent = new Intent(this, NewGroupChatMemberActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivityForResult(intent, GROUPCHAT_REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case GROUPCHAT_REQUEST_CODE:
                if(resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(this,R.string.create_group_cancelled, Toast.LENGTH_SHORT).show();
                    Log.e("test","create groupchat cancelled");
                }
                if (resultCode != Activity.RESULT_OK
                        || data == null) return;
                Toast.makeText(this,R.string.create_group_success, Toast.LENGTH_SHORT).show();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
