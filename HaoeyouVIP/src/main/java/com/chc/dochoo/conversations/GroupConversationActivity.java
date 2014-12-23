package com.chc.dochoo.conversations;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.chc.dochoo.CHCApplication;
import com.chc.found.models.AddEntityState;
import com.chc.found.models.EntityUser;
import com.chc.found.views.IEntityView;
import com.test.found.R;

import org.apache.commons.lang.StringUtils;

import java.security.acl.Group;
import java.util.List;

/**
 * Created by Lance on 5/29/14.
 */
public class GroupConversationActivity extends ActionBarActivity implements IEntityView{
    public static final String KEY_GROUPCHAT_CONVERSATION_ID = "bundle_conversation_id";
    public static final String KEY_GROUP_ID = "bundle_group_id";
    private static final String KEY_WITHIN_APP = "within_app";
    private static final String KEY_GROUP_CREATE_MSG_ID = "groupcreatemsgid";

    private GroupConversationPresenter gPresenter;
    private GroupConversation groupchatConversation;
    private String groupId;
    //private String leaderId;
    //private String targets;
    private Boolean isWithinApp = false;


    public static void startActivity(Activity a, String groupId, String conversationId, String groupCreateMsgId){
        Intent intent = new Intent(a, GroupConversationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_GROUPCHAT_CONVERSATION_ID,conversationId);
        bundle.putString(KEY_GROUP_ID,groupId);
        bundle.putBoolean(KEY_WITHIN_APP, true);
        bundle.putString(KEY_GROUP_CREATE_MSG_ID,groupCreateMsgId);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        a.startActivity(intent);
        a.overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gPresenter = new GroupConversationPresenter(getCHCApplication().getHelper());
        setContentView(R.layout.activity_empty);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        doCreate(bundle);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle args = intent.getExtras();

        doCreate(args);
    }

    @Override
    public void onResume() {
        super.onResume();
        CHCApplication.getInstance(this).showNetworkStatusToast();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.gPresenter.onActivityPause(this.groupchatConversation);
    }

    private void doCreate(Bundle bundle){
        if (bundle == null) return;
        groupId = bundle.getString(KEY_GROUP_ID);
        if (groupId == null) {
            finish();
            return;
        }
        String conversationId = bundle.getString(KEY_GROUPCHAT_CONVERSATION_ID, "");
        if(StringUtils.isBlank(conversationId)){
            groupchatConversation = gPresenter.getGroupConversationByGroupId(groupId);
        }
        else    groupchatConversation = gPresenter.getGroupConversationById(conversationId);
        if(groupchatConversation == null) return;
        isWithinApp = bundle.getBoolean(KEY_WITHIN_APP, false);
        String groupCreateMsgId = bundle.getString(KEY_GROUP_CREATE_MSG_ID, "");
        if(StringUtils.isNotBlank(groupCreateMsgId)){
            groupchatConversation.setLastMsgId(groupCreateMsgId);
            InstantMessage groupCreateMsg = InstantMessageModel.getMessageById(getCHCApplication().getHelper(), groupCreateMsgId);
            if(groupCreateMsg != null)  groupchatConversation.setLastMsg(groupCreateMsg);
            updateGroupChat(groupchatConversation);
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.root, InstantMessageFragment.newInstance(groupId,true));
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onEntityLoaded(EntityUser user) {

    }

    @Override
    public void onEntityLoaded(List<EntityUser> user) {

    }

    @Override
    public void getEntityFailed(AddEntityState state) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.conversation, menu);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        else if(item.getItemId() == R.id.action_group_info){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.root, GroupInfoFragment.newInstance(groupId, false), "groupInfoFrag");
            ft.commitAllowingStateLoss();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.root);
        if (fragment instanceof InstantMessageFragment) {
            ((InstantMessageFragment) fragment).close();
            goToParentActivity();
            finish();
        }
        if(fragment instanceof GroupInfoFragment){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.root, InstantMessageFragment.newInstance(groupId, true));
            ft.commitAllowingStateLoss();
        }
        this.overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    @Override
    public CHCApplication getCHCApplication() {
        return CHCApplication.getInstance(this);
    }

    private void goToParentActivity() {
        ConversationActivity.start(this);
    }

    public void updateLastMessage(InstantMessage im){
        if(this.groupchatConversation != null && im != null){
            groupchatConversation.setLastMsg(im);
            groupchatConversation.setLastMsgId(im.getId());
        }
    }

    public void updateGroupChat(GroupConversation groupChat){
        if(this.groupchatConversation != null && groupChat != null){
            this.groupchatConversation = groupChat;
            ConversationModel.updateGroupConversation(getCHCApplication().getHelper(), groupchatConversation);
        }
    }

}
