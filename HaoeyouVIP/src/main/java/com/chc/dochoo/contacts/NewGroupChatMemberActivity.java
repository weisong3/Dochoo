package com.chc.dochoo.contacts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AlphabetIndexer;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chc.dochoo.CHCApplication;
import com.chc.dochoo.conversations.ConversationActivity;
import com.chc.dochoo.conversations.ConversationModel;
import com.chc.dochoo.conversations.GroupChatTask;
import com.chc.dochoo.conversations.GroupChatTaskType;
import com.chc.dochoo.conversations.GroupConversationActivity;
import com.chc.dochoo.conversations.GroupMemberModel;
import com.chc.found.models.AddEntityState;
import com.chc.found.models.DoctorUser;
import com.chc.found.models.EntityUser;
import com.chc.found.models.GroupMember;
import com.chc.found.views.IEntityView;
import com.chc.views.HorizontalListView;
import com.chcgp.hpad.util.download.ImageDownloader;
import com.makeramen.RoundedDrawable;
import com.makeramen.RoundedImageView;
import com.test.found.R;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class NewGroupChatMemberActivity extends ActionBarActivity implements IEntityView, OnContactOptionListener, OnContactGroupOptionListener{

    private static final String TAG = NewGroupChatMemberActivity.class.getSimpleName();

    private String groupId;
    private List<String> newMemberIdList;


    private OnContactOptionListener mCallback;
    private OnContactGroupOptionListener groupCallback;

    private ListView contactsListView;
    private GroupchatAdapter adapter;
    //alphabetization

    private TextView sectionToastText;
    private RelativeLayout sectionToastLayout;
    private SideBar sidebar;
    private Button confirmButton;
    private Handler addHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupchat_add);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setLogo(R.drawable.ic_launcher);
        }

        try {
            mCallback = (OnContactOptionListener) this;
            groupCallback = (OnContactGroupOptionListener)this;
        } catch (ClassCastException e) {
            throw new ClassCastException(this.toString()
                    + " must implement OnContactOptionListener");
        }

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                groupId = null;
            } else {
                groupId = extras.getString("groupId");
            }
        } else {
            groupId= (String) savedInstanceState.getSerializable("groupId");
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cancelAction();
    }

    @Override
    protected void onResume() {
        super.onResume();

        CHCApplication.getInstance(this).showNetworkStatusToast();

        CHCApplication chcApplication = getCHCApplication();
        if (chcApplication != null){
            newMemberIdList = new ArrayList<>();
            List<EntityUser> entityList = sortAndRemoveDuplicatedContacts(ContactModel.getRealContacts(chcApplication.getHelper()));
            contactsListView = (ListView)findViewById(R.id.groupchat_listview);

            List<GroupMember> currentMembers = null;
            if(StringUtils.isNotBlank(groupId)) currentMembers = GroupMemberModel.GetMemberByGroupId(chcApplication.getHelper(), groupId);
            adapter = new GroupchatAdapter(mCallback, groupCallback, this, entityList, currentMembers);

            contactsListView.setAdapter(adapter);

            MyCursor myCursor = new MyCursor(entityList);

            sectionToastLayout = (RelativeLayout) findViewById(R.id.section_toast_layout);
            sectionToastText = (TextView) findViewById(R.id.section_toast_text);
            LayoutInflater li = LayoutInflater.from(this);
            View customView = li.inflate(R.layout.custom_actionbar_button, null);
            confirmButton = (Button)customView.findViewById(R.id.confirm_add_group_member);

            if(entityList.size()>0){
                contactsListView.setAdapter(adapter);
            }
            sidebar = (SideBar) findViewById(R.id.sidebar);
            sidebar.setTextView(sectionToastText);
            sidebar.setSectionToastLayout(sectionToastLayout);
            sidebar.setListView(contactsListView);
            addHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if(msg.getData() != null){
                       /* String addNoticeId = msg.getData().getString("addNoticeId");
                        if(StringUtils.isNotBlank(addNoticeId)){
                            GroupConversationActivity.startActivity(NewGroupChatMemberActivity.this, groupId, null, addNoticeId);
                        }
                        else    */
                        finish();
                    }
                }
            };
        }
    }

    public void restoreActionBar(View custom) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.title_activity_new_group_chat_member);
        if(custom == null){
            actionBar.setCustomView(R.layout.custom_actionbar_button);
        }
        else{
            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                    android.app.ActionBar.LayoutParams.WRAP_CONTENT, android.app.ActionBar.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER_VERTICAL|Gravity.RIGHT);
            layoutParams.setMargins(
                    getResources().getDimensionPixelSize(R.dimen.new_group_member_lien_left_margin),
                    getResources().getDimensionPixelSize(R.dimen.new_group_member_lien_left_margin),
                    getResources().getDimensionPixelSize(R.dimen.new_group_member_lien_left_margin),
                    getResources().getDimensionPixelSize(R.dimen.new_group_member_lien_left_margin)
            );
            actionBar.setCustomView(custom,layoutParams);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.new_group_chat_member, menu);
        restoreActionBar(null);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            /*case R.id.action_settings:
                return true;*/
            case android.R.id.home:
                cancelAction();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
    public CHCApplication getCHCApplication() {
        return CHCApplication.getInstance(this);
    }

    @Override
    public void onContactClicked(String id) {
        Log.e(TAG,"Contact Clicked");
    }

    @Override
    public void onContactImageClicked(EntityUser user) {
        Log.e(TAG,"Contact Image Clicked");
    }

    @Override
    public void onContactLongClicked(String id) {
        Log.e(TAG,"Contact Long Clicked");
    }

    private List<EntityUser> sortAndRemoveDuplicatedContacts(List<EntityUser> userList){
        List<String> userIdList = new ArrayList<>();
        List<EntityUser> result = new ArrayList<>();
        for(EntityUser user:userList){
            if(userIdList.contains(user.getId()))continue;

            userIdList.add(user.getId());
            result.add(user);
        }
        ContactModel.sortContacts(result);
        return result;
    }

    private void cancelAction(){
        Intent data = new Intent();
        setResult(Activity.RESULT_CANCELED, data);
        finish();
    }

    @Override
    public void onContactAddToGroup(EntityUser user, int newMemberSize) {
        LinearLayout gallery = (LinearLayout)findViewById(R.id.group_member_image);
        RoundedImageView pView = new RoundedImageView(getApplicationContext());
        String url = user.getProfileIconUrl();
        if (url != null && StringUtils.isNotBlank(url)) {
            int size = getResources().getDimensionPixelSize(R.dimen.doctor_collabrator_listview_item_height);
            ImageDownloader.getInstance().download(url, pView,size,size,
                    getResources(), R.drawable.doc_default);
        }
        pView.setCornerRadius(getResources().getDimensionPixelSize(R.dimen.doctor_collabrator_icon_radius));
        pView.setBorderWidth(getResources().getDimensionPixelSize(R.dimen.rounded_img_border_width));
        pView.setBorderColor(getResources().getColor(R.color.default_dochoo_orange));
        pView.setRoundBackground(true);
        pView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        pView.setTag("groupAdding_" + user.getId());

        pView.setOval(false);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.doctor_collabrator_icon_radius),
                getResources().getDimensionPixelSize(R.dimen.doctor_collabrator_icon_radius),
                Gravity.CENTER);
        layoutParams.setMargins(
                getResources().getDimensionPixelSize(R.dimen.new_group_member_lien_left_margin),
                getResources().getDimensionPixelSize(R.dimen.new_group_member_lien_top_margin),
                0,
                0);
        pView.setLayoutParams(layoutParams);
        gallery.addView(pView);
        newMemberIdList.add(user.getId());
        LayoutInflater li = LayoutInflater.from(this);
        View customView = li.inflate(R.layout.custom_actionbar_button, null);
        confirmButton = (Button)customView.findViewById(R.id.confirm_add_group_member);
        confirmButton.setText(getString(R.string.ok_button) + "(" + newMemberSize + ")");
        if(newMemberSize>0)  {
            confirmButton.setClickable(true);
            confirmButton.setBackgroundColor(getResources().getColor(R.color.default_dochoo_orange));
            confirmButton.setTextColor(getResources().getColor(R.color.white));
        }
        confirmButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                CHCApplication chcApplication = getCHCApplication();
                if(StringUtils.isNotBlank(groupId)){
                    GroupChatTask newTask = new GroupChatTask(
                                                                NewGroupChatMemberActivity.this,
                                                                getCHCApplication().getHelper(),
                                                                GroupChatTaskType.Add,
                                                                chcApplication.getUserId(),
                                                                chcApplication.getRegId(),
                                                                groupId,
                                                                true
                                                                );
                    newTask.setMyHandler(addHandler);
                    newTask.execute(newMemberIdList);
                }
                else{
                    new GroupChatTask(
                            NewGroupChatMemberActivity.this,
                            getCHCApplication().getHelper(),
                            GroupChatTaskType.Create,
                            chcApplication.getUserId(),
                            chcApplication.getRegId(),
                            null,
                            true
                    ).execute(newMemberIdList);
                }

            }
        });
        restoreActionBar(customView);

    }

    @Override
    public void onContactRemoveFromGroup(EntityUser user, int newMemberSize) {
        LinearLayout gallery = (LinearLayout)findViewById(R.id.group_member_image);
        RoundedImageView pView = (RoundedImageView)gallery.findViewWithTag("groupAdding_" + user.getId());
        gallery.removeView(pView);
        newMemberIdList.remove(user.getId());
        confirmButton.setText(getString(R.string.ok_button) + "(" + newMemberSize + ")");

        if(newMemberSize>0){
            confirmButton.setTextColor(getResources().getColor(R.color.white));
            confirmButton.setClickable(true);
            confirmButton.setBackgroundColor(getResources().getColor(R.color.default_dochoo_orange));
        }
        else{
            confirmButton.setTextColor(getResources().getColor(R.color.dark_grey));
            confirmButton.setClickable(false);
            confirmButton.setBackgroundColor(getResources().getColor(R.color.chc_default_grey));
        }
    }

}
