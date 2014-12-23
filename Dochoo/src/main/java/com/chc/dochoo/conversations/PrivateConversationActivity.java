package com.chc.dochoo.conversations;

import android.support.v7.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.ImageView;

import com.chc.dochoo.CHCApplication;
import com.chc.found.models.AddEntityState;
import com.chc.found.models.EntityUser;
import com.chc.found.presenters.EntityPresenter;
import com.chc.found.views.IEntityView;
import com.chcgp.hpad.util.download.ImageDownloader;
import com.chcgp.hpad.util.general.CHCGeneralUtil;
import com.makeramen.RoundedDrawable;
import com.test.found.R;

import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Created by HenryW on 1/13/14.
 */
public class PrivateConversationActivity extends ActionBarActivity implements IEntityView {
    public static final String KEY_CONVERSATION_ID = "bundle_conversation_id";
    public static final String KEY_ENTITY_ID = "bundle_entity_id";
    private static final String KEY_WITHIN_APP = "within_app";
    private PrivateConversation mConversation;
    private String entityId;
    private PrivateConversationPresenter mPresenter;
    private boolean isWithinApp = false;

    public static final void startActivity(Activity a, String targetId, String conversationId) {
        Intent intent = new Intent(a, PrivateConversationActivity.class);
        Bundle args = new Bundle();

        args.putString(KEY_ENTITY_ID, targetId);
        args.putString(KEY_CONVERSATION_ID, conversationId);
        args.putBoolean(KEY_WITHIN_APP, true);

        intent.putExtras(args);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        a.startActivity(intent);

        a.overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.mPresenter.onActivityPause(this.mConversation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mPresenter = new PrivateConversationPresenter(this, getCHCApplication().getHelper());

        setContentView(R.layout.activity_empty);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle args = getIntent().getExtras();
        doCreate(args);
    }

    private void doCreate(Bundle args) {
        if (args == null) return;
        entityId = args.getString(KEY_ENTITY_ID);
        if (entityId == null) {
            finish();
            return;
        }
        String conversationId = args.getString(KEY_CONVERSATION_ID, "");
        if (StringUtils.isBlank(conversationId)) {
            this.mConversation = mPresenter.getPrivateConversationByEntityId(entityId);
            // new conversation
            if (this.mConversation == null) {
                this.mConversation = mPresenter.createNewPrivateConversation(CHCApplication.getInstance(this).getUserId(), entityId);
            }
        } else {
            this.mConversation = mPresenter.getPrivateConversationById(conversationId);
        }
        isWithinApp = args.getBoolean(KEY_WITHIN_APP, false);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setLogo(R.drawable.ic_launcher);
        }

        EntityPresenter presenter = new EntityPresenter(this);

        EntityUser user = presenter.getEntityById(entityId);

        if (user == null) {
            finish();
            return;
        }

        int dimen = getResources().getDimensionPixelSize(R.dimen.logo_size);
        ImageDownloader.getInstance().downloadWithCallback(this, user.getProfileIconUrl(), dimen,
                new ImageDownloader.Callback() {

                    @Override
                    public void onImageLoaded(Bitmap bitmap) {
                        try {
                            ActionBar actionBar = getSupportActionBar();
                            if (actionBar != null && bitmap != null) {
                                bitmap = CHCGeneralUtil.cropSquareBitmap(bitmap);
                                int radius = getResources().getDimensionPixelSize(R.dimen.icon_action_bar_radius);
                                RoundedDrawable logo = new RoundedDrawable(bitmap);
                                logo.setScaleType(ImageView.ScaleType.CENTER_INSIDE)
                                        .setCornerRadius(radius)
                                        .setBorderWidth(getResources().getDimensionPixelSize(R.dimen.icon_border_size))
                                        .setBorderColors(getResources().getColorStateList(R.color.default_dochoo_orange))
                                        .setOval(true);
                                actionBar.setLogo(logo);
                            }
                        } catch (Exception e) {}
                    }
                });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.root, InstantMessageFragment.newInstance(entityId));
        ft.commitAllowingStateLoss();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle args = intent.getExtras();

        doCreate(args);
    }

    @Override
    public void onBackPressed() {
//        mPresenter.onActivityPause(this.mConversation);
        android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.root);
        if (fragment instanceof InstantMessageFragment) {
            ((InstantMessageFragment) fragment).close();
        }

        if (isWithinApp) {
            super.onBackPressed();
        } else {
            goToParentActivity();
            finish();
        }

        this.overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToParentActivity() {
        ConversationActivity.start(this);
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

    public void updateLastMessage(InstantMessage msg) {
        if (this.mConversation != null && msg != null) {
            mConversation.setLastMsg(msg);
            mConversation.setLastMsgId(msg.getId());
        }
    }
}
