package com.chc.dochoo.conversations;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.LruCache;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.chc.dochoo.CHCApplication;
import com.chc.dochoo.conversations.InstantMessage.InstantMessageType;
import com.chc.dochoo.splash.SplashActivity;
import com.chc.dochoo.userlogin.UserLogInActivity;
import com.chc.exceptions.InternalErrorException;
import com.chc.found.FoundSettings;
import com.chc.found.GcmIntentService;
import com.chc.found.models.EntityUser;
import com.chc.found.models.GroupMember;
import com.chc.found.network.NetworkRequestsUtil;
import com.chc.found.presenters.IRecordVoicePresenter;
import com.chc.found.presenters.InstantMessagePresenter;
import com.chc.found.presenters.RecordAmplitudePresenter;
import com.chc.found.presenters.RecordVoicePresenter;
import com.chc.found.utils.SharedPreferenceUtil;
import com.chc.found.views.IInstantMessageView;
import com.chc.found.views.IRecordVoiceView;
import com.chc.views.TypefacedTextView;
import com.chcgp.hpad.util.general.CHCGeneralUtil;
import com.test.found.R;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.security.acl.Group;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InstantMessageFragment extends Fragment
        implements IInstantMessageView, IRecordVoiceView,
        RecordAmplitudePresenter.IAmplitudeView {

    public static final String TAG = "InstantMessageFragment";
    private static final int REQUEST_CODE_PICK_IMAGE = 111;
    private static final int REQUEST_CODE_TAKE_PHOTO = 1111;
    private ListView mListView;
    private InstantMessageAdapter mAdapter;
    private InstantMessagePresenter mPresenter;
    private IRecordVoicePresenter recordPresenter;
    private RecordAmplitudePresenter amplitudePresenter;
    private IntentFilter intentFilter;

    private VoiceControl lastPlaying;

    private BroadcastReceiver receiver = new MyBroadcastReceiver();
    private volatile boolean receiverRegistered = false;
    private ImageButton mPostBtn;
    private ImageButton imageBtn;
    private ImageButton beginRecordBtn;
    private View stopRecordBtn;
    private Button cancelRecordBtn;
    private EditText mEditText;
    private View recordDialogLayer;

    private ImageView recordingAmplitude;
    private ProgressDialog mProgress;
    public ImageView lastClickedImageView;
    private Handler mHandler = new Handler();
    private Bitmap userImage;
    private View wrapperAttachmentSelector;
    private Button attachGallery, attachCamera;



    private LruCache<String, Bitmap> imageCache = new LruCache<String, Bitmap>((int) (Runtime.getRuntime().maxMemory() / 1024) / 16) {

        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getRowBytes() * value.getHeight() / 1024;
        }

    };
    private ProgressDialog progressDialog;
    private String entityId;
    private EntityUser entity;
    private Uri lastUriFromCamera;

    //Group chat parameter
    private String groupId;
    private Boolean isGroupchat = false;
    private Boolean inGroup = true;
    //special design for group chat(stillInGroup == false)
    private RelativeLayout messageInputLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new InstantMessagePresenter(this, getCHCApplication().getHelper());
        recordPresenter = new RecordVoicePresenter(this);
        amplitudePresenter = new RecordAmplitudePresenter(this, mHandler, recordPresenter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_instant_msg, container,
                false);
        intentFilter = new IntentFilter(getString(R.string.im_action));

        //noinspection ConstantConditions
        mListView = (ListView) root.findViewById(R.id.listView1);
        mEditText = (EditText) root.findViewById(R.id.instant_msg_edit_text);

        // clickables
        mPostBtn = (ImageButton) root.findViewById(R.id.post_message_btn);
        imageBtn = (ImageButton) root.findViewById(R.id.post_image_btn);
        beginRecordBtn = (ImageButton) root.findViewById(R.id.show_record_btn);
        stopRecordBtn = root.findViewById(R.id.frame_btn_stop_recording);
        cancelRecordBtn = (Button) root.findViewById(R.id.recording_cancel_btn);
        recordDialogLayer = root.findViewById(R.id.record_layer);
        recordingAmplitude = (ImageView) root.findViewById(R.id.instant_msg_recording_amplitude);
        wrapperAttachmentSelector = root.findViewById(R.id.im_attach_selector_wrapper);
        attachCamera = (Button) root.findViewById(R.id.buttonIMCamera);
        attachGallery = (Button) root.findViewById(R.id.buttonIMGallery);
        messageInputLayout = (RelativeLayout)root.findViewById(R.id.instant_msg_input_layout);

        Bundle args = getArguments();
        if (args != null){
            entityId = args.getString(PrivateConversationActivity.KEY_ENTITY_ID);
            if (entityId == null){
                groupId = args.getString(GroupConversationActivity.KEY_GROUP_ID);
                if(groupId != null){
                    isGroupchat = true;
                    GroupConversation groupchat = ConversationModel.getGroupConversationByGroupId(getCHCApplication().getHelper(),groupId);
                    if(groupchat != null){
                        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(groupchat.getTopic());
                        if(groupchat.getStillInGroup() == false){
                            inGroup = false;
                            messageInputLayout.setVisibility(View.GONE);
                        }
                    }
                }

            }
        }


        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        if (args == null) return;
        entityId = args.getString(PrivateConversationActivity.KEY_ENTITY_ID);
        if (entityId == null){
            groupId = args.getString(GroupConversationActivity.KEY_GROUP_ID);
            if(groupId == null) return;
            isGroupchat = true;
        }

        userImage = SharedPreferenceUtil.readUserImage(getActivity());
        entity = mPresenter.getEntityById(entityId);


        // set typeface for edit text
        mEditText.setTypeface(TypefacedTextView.getTypeface(getActivity().getAssets(), FoundSettings.TYPEFACE_OPEN_SANS));

        InstantMessageFragmentOnClickListener instantMessageFragmentOnClickListener = new InstantMessageFragmentOnClickListener();
        mPostBtn.setOnClickListener(instantMessageFragmentOnClickListener);
        imageBtn.setOnClickListener(instantMessageFragmentOnClickListener);
        beginRecordBtn.setOnClickListener(instantMessageFragmentOnClickListener);
        stopRecordBtn.setOnClickListener(instantMessageFragmentOnClickListener);
        cancelRecordBtn.setOnClickListener(instantMessageFragmentOnClickListener);
        attachCamera.setOnClickListener(instantMessageFragmentOnClickListener);
        attachGallery.setOnClickListener(instantMessageFragmentOnClickListener);

        mEditText.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    onMsgSendRequest();
                    return true;
                }
                return false;
            }
        });

        mEditText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onShowAttachmentWrapper(false);
            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                onEditTextHasNoText(length == 0);
            }
        });

        if (mListView != null) {
            mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    onListItemSelect(i);
                    return true;
                }
            });
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    onListItemSelect(i);
                }
            });
        }

    }

    private void onListItemSelect(int position) {
        Toast.makeText(getActivity(), "Index " + position + " Selected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onPause() {
        stopCurrentPlaying();
        onRecordCancel();
        if (receiverRegistered) {
            synchronized (this) {
                receiverRegistered = false;
            }
            getActivity().unregisterReceiver(receiver);
        }
        getCHCApplication().setCurrentChattingId("");

        super.onPause();
    }

    private void stopCurrentPlaying() {
        if (lastPlaying != null) {
            lastPlaying.stopPlaying();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
        CHCApplication chcApplication = getCHCApplication();
        if (chcApplication == null) return;
        if(isGroupchat){
            mPresenter.loadGroupData(chcApplication.getUserId(),groupId,chcApplication.getRegId());
            chcApplication.setCurrentChattingId(groupId);
            GroupConversation groupchat = ConversationModel.getGroupConversationByGroupId(getCHCApplication().getHelper(),groupId);
            if(groupchat != null){
                ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(groupchat.getTopic());
                if(groupchat.getStillInGroup() == false){
                    inGroup = false;
                    messageInputLayout.setVisibility(View.GONE);
                }
            }
        }
        else{
            mPresenter.loadData(chcApplication.getUserId(), entityId, chcApplication.getRegId());
            chcApplication.setCurrentChattingId(entityId);

            EntityUser user = mPresenter.getMsgEntityUser(entityId);
            if (user != null && StringUtils.isNotBlank(user.getFullname())) {
                Activity a = getActivity();
                if (a instanceof ActionBarActivity && ((ActionBarActivity) a).getSupportActionBar() != null) {
                    ((ActionBarActivity) a).getSupportActionBar().setTitle(user.getFullname());
                }
            }
        }

        NotificationManager nm = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(GcmIntentService.NOTIFICATION_ID_MSG);

        synchronized (this) {
            if (getActivity() != null && !receiverRegistered) {
                getActivity().registerReceiver(receiver, intentFilter);
                receiverRegistered = true;
            }
        }
    }

    @Override
    public void onStop() {
        getCHCApplication().setCurrentChattingId("");
        clearWakelock(getActivity());

        super.onStop();
    }

    public void close() {
        this.mPresenter.close();
        this.recordPresenter.release();
    }

    public static Fragment newInstance(String entityId) {
        Bundle args = new Bundle();
        args.putString(PrivateConversationActivity.KEY_ENTITY_ID, entityId);
        Fragment f = new InstantMessageFragment();
        f.setArguments(args);
        return f;
    }
    public static Fragment newInstance(String groupId, Boolean isGroup) {
        Bundle args = new Bundle();
        args.putString(GroupConversationActivity.KEY_GROUP_ID, groupId);
        Fragment f = new InstantMessageFragment();
        f.setArguments(args);
        return f;
    }

    public void restoreFragment(){
        FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
        Fragment newFragment = newInstance(groupId, true);
        fragTransaction.replace(R.id.root, newFragment);
        fragTransaction.commit();
    }

    @Override
    public void onAmplitudeUpdate(RecordAmplitudePresenter.AmplitudeLevel level,Long totalTime) {
        if(totalTime >= 60000){//60 seconds
            Log.e("Stopping",totalTime+"");
            onRecordFinish();
            clearWakelock(getActivity());
        }
        else {
            assert level != null;
            switch (level) {
                case VERY_LOW:
                    recordingAmplitude.setImageResource(R.drawable.vol_1);
                    break;
                case LOW:
                    recordingAmplitude.setImageResource(R.drawable.vol_2);
                    break;
                case MEDIUM:
                    recordingAmplitude.setImageResource(R.drawable.vol_3);
                    break;
                case HIGH:
                    recordingAmplitude.setImageResource(R.drawable.vol_4);
                    break;
            }
        }

    }

    void onImageDownloadClick(InstantMessage item, ImageView iv) {
        if (item.getMultimediaPath() == null)
            lastClickedImageView = iv;
        downloadContent(item);
    }

    void updateLastMsg(InstantMessage item) {
        if (getActivity() instanceof PrivateConversationActivity && item != null) {
            ((PrivateConversationActivity) getActivity()).updateLastMessage(item);
        }
        else if(getActivity() instanceof GroupConversationActivity && item != null){
            ((GroupConversationActivity)getActivity()).updateLastMessage(item);
        }
    }

    void onMsgResend(InstantMessage item) {
        CHCApplication chcApplication = getCHCApplication();
        if (chcApplication != null) {
            mPresenter.onMessageResend(
                    item, chcApplication.getUserId(), entityId, chcApplication.getRegId());
        }
    }

    public void showDownloadProgress() {
        progressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.msg_dialog_downloading), true, false);
    }

    private void dismissDownloadProgress() {
        if (progressDialog != null) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
            }
        }
        progressDialog = null;
    }

    public Bitmap getImageFromPath(String multimediaPath) {
        if (multimediaPath == null) return null;

        Bitmap bitmap = imageCache.get(multimediaPath);
        if (bitmap == null) {
            InputStream input;
            try {
                input = getActivity().getContentResolver().openInputStream(Uri.parse(multimediaPath));
                if (input == null) {
                    return null;
                }

                input = new BufferedInputStream(input);
                if (input.markSupported()) {
                    input.mark(Integer.MAX_VALUE);
                } else {
                    Log.e(TAG, "inputstream does not support marking");
                    return null;
                }

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(input, null, options);


                int width = getResources().getDimensionPixelSize(R.dimen.instant_msg_img_max_width);
                final double imgRatio = (double) options.outHeight / options.outWidth;
                final int height = (int) (width * imgRatio);

                options.inSampleSize = CHCGeneralUtil.calculateInSampleSize(options, width, height);
                options.inJustDecodeBounds = false;

                input.reset();
                bitmap = BitmapFactory.decodeStream(input, new Rect(), options);
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
            } catch (FileNotFoundException e) {
                return null;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                return null;
            }
            if (bitmap != null)
                imageCache.put(multimediaPath, bitmap);
        }
        return bitmap;
    }

    @Override
    public CHCApplication getCHCApplication() {
        FragmentActivity activity = getActivity();
        if (activity == null) return null;
        return CHCApplication.getInstance(activity);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onMessageLoaded(List<? extends FoundMessage> msgList) {
        List<InstantMessage> imList = (List<InstantMessage>) msgList;

        if (mAdapter == null) {
            if(isGroupchat){
                mAdapter = new InstantMessageAdapter(getActivity(), imList, this, mHandler, groupId, userImage);
            }
            else{
                mAdapter = new InstantMessageAdapter(getActivity(), (List<InstantMessage>) msgList, this, mHandler, entity, userImage);
            }
            mListView.setAdapter(mAdapter);

        } else {
            if (msgList == null) return;
            mAdapter.combineList((List<InstantMessage>) msgList);
        }
        //if (isVisible())
        if(!isGroupchat)
            mPresenter.examineInstantMessageStatus(imList);
    }

    public void onInstantMessageViewLongClick(final String msgId) {
        new AlertDialog.Builder(getActivity())
                .setMessage(getString(R.string.delete_message_confirm))
                .setPositiveButton(getString(R.string.confirm_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        doDeleteMessage(msgId);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }

    private void doDeleteMessage(final String msgId) {
        final CHCApplication chcApplication = getCHCApplication();
        if (chcApplication == null) {
            deleteMessageFailed();
            return;
        }

        new AsyncTask<String, Void, Boolean>() {
            ProgressDialog pd;

            @Override
            protected void onPreExecute() {
                pd = ProgressDialog.show(getActivity(), null, getString(R.string.network_wait), true, false);
            }

            @Override
            protected Boolean doInBackground(String... params) {
                return mPresenter.syncDeleteMessage(params);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                try {
                    pd.dismiss();
                } catch (Exception e) {
                    // ignore
                }
                if (aBoolean != null && aBoolean) {
                    mAdapter.deleteMsgById(msgId);
                    mPresenter.deleteMessageLocalDb(msgId);

                    deleteMessageSuccess();
                } else {
                    deleteMessageFailed();
                }
            }
        }.execute(chcApplication.getUserId(), chcApplication.getRegId(), msgId);
    }

    private void deleteMessageSuccess() {
        Toast.makeText(getActivity(), R.string.delete_single_message_success, Toast.LENGTH_SHORT).show();
        updateLastMsg(mPresenter.getLastMessage(entityId));
    }

    private void deleteMessageFailed() {
        Toast.makeText(getActivity(), R.string.delete_single_message_failed, Toast.LENGTH_SHORT).show();
    }

    public void onPlayingFinished() {
        clearWakelock(getActivity());
    }

    private boolean goodToShow(String fromId, String targetGroupId){
        if(StringUtils.isNotBlank(targetGroupId)){
            //group message
            return StringUtils.equals(targetGroupId, groupId);
        }
        else{
            //private message
            return StringUtils.equals(fromId, entityId);
        }
    }
    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String msgId = intent.getStringExtra(context.getString(R.string.broadcast_intent_extra_tag_im));
            String fromId = intent.getStringExtra(context.getString(R.string.broadcast_intent_extra_tag_im_entity));
            String targetgroupId = intent.getStringExtra(context.getString(R.string.broadcast_intent_extra_tag_im_group));
            boolean isNewMessage = intent.getBooleanExtra(context.getString(R.string.broadcast_intent_extra_tag_im_new), true);
            // if the entityId matches current conversation id, add it immediately into
            // the conversation
            if (fromId != null
                    && goodToShow(fromId, targetgroupId)
                    && msgId != null) {
                InstantMessage msg = mPresenter.getMessageById(msgId);
                if(StringUtils.isNotBlank(groupId))    msg = mPresenter.getGroupMessageById(msgId);
                if (msg == null) return;
                if (mAdapter != null) {
                    mAdapter.onMessageReceived(msg);
                }

                if (isNewMessage) updateLastMsg(msg); // update the conversation only if new message arrived but not status update

                if (    !msg.isFromPatient()
                        && msg.getMessageType() == InstantMessageType.TEXT
                        && msg.getStatus() != OutgoingMessageStatus.READ
                        && isVisible()
                        && receiverRegistered
                        && !isGroupchat) {
                    mPresenter.onMessageRead(msg);
                }
                if (msg.getMessageType() == InstantMessageType.GROUPNOTICE && StringUtils.isNotBlank(targetgroupId)){
                    GroupConversation groupchat = ConversationModel.getGroupConversationByGroupId(getCHCApplication().getHelper(),targetgroupId);
                    if(groupchat != null){
                        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(groupchat.getTopic());
                        ((GroupConversationActivity)getActivity()).updateGroupChat(groupchat);
                        //updateLastMsg(msg);
                        if(groupchat.getStillInGroup() != inGroup){
                            restoreFragment();
                        }

                    }
                }
            }
        }

    }

    private class InstantMessageFragmentOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.textView_login_newuser) {

                startRegisterActivity();

            } else if (id == R.id.textView_login_forgot_password) {

                handleForgotPassword();

            } else if (id == R.id.post_message_btn) {

                onMsgSendRequest();

            } else if (id == R.id.post_image_btn) {

                onShowAttachmentWrapper(true);

            } else if (id == R.id.show_record_btn) {

                onStartRecordBtnClick();

            } else if (id == R.id.frame_btn_stop_recording) {

                onStopRecordBtnClick();

            } else if (id == R.id.recording_cancel_btn) {

                onCancelRecordBtnClick();

            } else if (id == R.id.buttonIMGallery) {

                onPickImage();

            } else if (id == R.id.buttonIMCamera) {

                onCameraRequest();

            } else {
                // ignore
            }
        }

    }

    private void onCameraRequest() {

        onShowAttachmentWrapper(false);

        dispatchTakePictureIntent();

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                return;
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                lastUriFromCamera = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        lastUriFromCamera);
                startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PHOTO);
            }
        }
    }

    private void onShowAttachmentWrapper(boolean b) {
        if (b) {
            wrapperAttachmentSelector.setVisibility(View.VISIBLE);
        } else {
            wrapperAttachmentSelector.setVisibility(View.GONE);
        }
    }

    private void onStartRecordBtnClick() {
        onShowAttachmentWrapper(false);
        pauseCurrentPlaying();

        // acquire wakelock
        acquireWakelock(getActivity());

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
        onRecordDown();
    }

    private void acquireWakelock(FragmentActivity activity) {
        if (activity != null) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void pauseCurrentPlaying() {
        if (lastPlaying != null) {
            lastPlaying.pausePlaying();
        }
    }

    private void onStopRecordBtnClick() {
        onRecordFinish();

        clearWakelock(getActivity());
    }

    private void clearWakelock(FragmentActivity activity) {
        if (activity != null) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void onCancelRecordBtnClick() {
        onRecordCancel();

        clearWakelock(getActivity());
    }

    @Override
    public void onAddNewPostMessage(InstantMessage im) {
        this.mAdapter.addOutgoingMsg(im);
    }

    public void onRecordFinish() {
        showRecordLayer(false);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                stopRecording();
            }
        });
    }

    public void onRecordCancel() {
        showRecordLayer(false);
        cancelRecording();
    }

    public void onRecordDown() {
        showRecordLayer(true);

        startRecording();
    }

    private void startRecording() {
        try {
            recordPresenter.startRecording(FoundSettings.DOWNLOAD_PATH, getCHCApplication().getUserId() + UUID.randomUUID().toString());
            showAmplitude();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            onRecordFailed();
        }
    }

    private void showAmplitude() {
        assert recordingAmplitude != null : "amplitude imageview is null";
        assert amplitudePresenter != null;
        amplitudePresenter.startGettingAmplitude();
    }

    private void stopShowingAmplitude() {
        amplitudePresenter.stopGettingAmplitude();
    }

    private void cancelRecording() {
        recordPresenter.cancelRecording();
        stopShowingAmplitude();
    }

    private void stopRecording() {
        try {
            recordPresenter.stopRecordingAndSave();
            stopShowingAmplitude();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            onRecordFailed();
        }
    }

    private void showRecordLayer(boolean b) {
        recordDialogLayer.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * toggle showing state of the show voice button and send text button
     *
     * @param b is true if want to show voice button
     */
    private void onEditTextHasNoText(boolean b) {
        if (b) {
            // show show voice button
            beginRecordBtn.setVisibility(View.VISIBLE);
            mPostBtn.setVisibility(View.INVISIBLE);
        } else {
            // show send button
            beginRecordBtn.setVisibility(View.INVISIBLE);
            mPostBtn.setVisibility(View.VISIBLE);
        }
    }

    public void onPickImage() {
        onShowAttachmentWrapper(false);
        Intent intent = new Intent();
        intent.setType("image/jpeg");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.title_app_chooser_select_picture)), REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CODE_PICK_IMAGE:

                if (resultCode != Activity.RESULT_OK
                        || data == null) return;

                onImageResult(data);
                break;
            case REQUEST_CODE_TAKE_PHOTO:

                if (resultCode != Activity.RESULT_OK) return;

                onPhotoTaken();
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onPhotoTaken() {
        // Display image received on the view
        CHCApplication chcApplication = getCHCApplication();
        if (lastUriFromCamera != null && chcApplication != null) {
            mPresenter.postImageMessage(lastUriFromCamera,
                    entityId, groupId, chcApplication.getUserId(), chcApplication.getRegId());
        }

        lastUriFromCamera = null;
//        Bitmap pic = (Bitmap) b.get("data");
//
//        if (pic != null) {
//            CHCApplication chcApplication = getCHCApplication();
//            mPresenter.postImageMessage(pic, entityId, chcApplication.getUserId(), chcApplication.getRegId());
//        }

    }

    private void onImageResult(Intent data) {
        Uri imageUri = data.getData();
        // TODO let user preview data and confirm sending
        CHCApplication chcApplication = getCHCApplication();
        if (chcApplication != null) {
            mPresenter.postImageMessage(imageUri, entityId, groupId, chcApplication.getUserId(), chcApplication.getRegId());
        }
    }


    @SuppressWarnings("ConstantConditions")
    public void onMsgSendRequest() {

        CHCApplication chcApplication = getCHCApplication();
        if (chcApplication == null) return;
        mPresenter.postTextMessage(mEditText.getText().toString().trim(), entityId,
                    chcApplication.getUserId(), chcApplication.getRegId(), groupId);
        mEditText.clearComposingText();
        mEditText.setText("");

    }

    private void displayProgress() {
        if (mProgress == null) mProgress = ProgressDialog.show(
                getActivity(),
                null,
                getString(R.string.network_wait),
                true,
                false);
        else mProgress.show();
    }

    private void dismissProgress() {
        if (mProgress != null && mProgress.isShowing()) mProgress.dismiss();
    }

    public void handleForgotPassword() {
        Toast.makeText(getActivity(), getString(R.string.forgot_password), Toast.LENGTH_SHORT).show();
    }

    public void startRegisterActivity() {
        UserLogInActivity.startLogInActivity(getActivity());
    }

    @Override
    public void onUnsupportedFormatOrSize() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            Toast.makeText(activity, getString(R.string.message_invalid_image), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDownloadFailed() {
        dismissDownloadProgress();
        if (getActivity() == null) return;
        Toast.makeText(getActivity(), getString(R.string.toast_error_download_failed), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDownloadFinished(InstantMessage im) {
        dismissDownloadProgress();
        InstantMessageType messageType = im.getMessageType();
        switch (messageType) {
            case IMAGE:
                openImage(im);
                break;
            case VOICE:
                playVoice(im);
                break;
            case FILE:
            default:
                // TODO more cases
                break;
        }

    }

    private void playVoice(InstantMessage im) {
        if (im == null || lastPlaying == null) return;
        if (!im.getId().equals(lastPlaying.im.getId())) return;

        lastPlaying.replaceInstantMessage(im);

        int seekBarPosPercentage = lastPlaying.seekBarPosPercentage;
        if (seekBarPosPercentage >= VoiceControl.SEEKBAR_MAX - 1) {
            seekBarPosPercentage = 0;
        }
        lastPlaying.voicePresenter.startPlayingAtPercentage(seekBarPosPercentage);
        if (lastPlaying.holderWeakReference != null && lastPlaying.holderWeakReference.get() != null) {
            @SuppressWarnings("ConstantConditions") View unread = lastPlaying.holderWeakReference.get()
                    .unread;
            if (unread != null)
                unread.setVisibility(View.INVISIBLE);
        }
        if (!im.isFromPatient())
            mPresenter.onMessageRead(im);
    }

    private void openImage(InstantMessage im) {

        String uri = im.getMultimediaPath();
        if (StringUtils.isBlank(uri)) return;

        if (lastClickedImageView != null) {
            Bitmap bitmap = getImageFromPath(uri);
            if (bitmap != null)
                lastClickedImageView.setImageBitmap(bitmap);

            lastClickedImageView = null;
        }
       if (!im.isFromPatient()) {
            mPresenter.onMessageRead(im);
       }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(uri), "image/jpeg");
        startActivity(intent);
    }

    /**
     * Handles click event on voice view
     *
     * @param control
     * @param item
     */
    protected void onVoiceViewClicked(final VoiceControl control, final InstantMessage item) {

        acquireWakelock(getActivity());

        if (lastPlaying != control) {
            if (lastPlaying != null)
                lastPlaying.stopPlaying();
            lastPlaying = control;
        }

        if (control.voicePresenter.isPlaying()) {
            // playing
            control.voicePresenter.pause();
        } else {
            // begin to play
            downloadContent(item);
        }
        if (control.holderWeakReference != null && control.holderWeakReference.get() != null) {
            control.setUpButtonView(control.holderWeakReference.get());
        }

    }

    private void downloadContent(final InstantMessage item) {
        showDownloadProgress();
        mPresenter.downloadContent(item);
    }

    @Override
    public void onRecordSaved(final String absolutepath, final String filename, final long lengthMs) {
        final CHCApplication chcApplication = getCHCApplication();

        Activity activity = getActivity();
        if (activity == null || chcApplication == null) return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPresenter.postVoiceMessage(entityId,  chcApplication.getUserId(),groupId,
                        chcApplication.getRegId(), absolutepath, filename, lengthMs);
            }
        });

    }

    @Override
    public void onRecordFailed() {
        showRecordLayer(false);
        Toast.makeText(getActivity(), getString(R.string.toast_error_recording_failed), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecordTooShort() {
        Toast.makeText(getActivity(), getString(R.string.toast_error_record_too_short), Toast.LENGTH_SHORT).show();
    }

    void onVoiceMessageDamaged(InstantMessage im) {
        mPresenter.onVoiceMessageDamaged(im);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        if(getActivity() instanceof PrivateConversationActivity)
            inflater.inflate(R.menu.private_conversation, menu);
        else if(getActivity() instanceof GroupConversationActivity){
            ActionBar actionBar = ((GroupConversationActivity)getActivity()).getSupportActionBar();
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setCustomView(null);
            if(inGroup){
                inflater.inflate(R.menu.conversation, menu);
                menu.findItem(R.id.action_add_private_msg).setVisible(false);
            }
            else    inflater.inflate(R.menu.private_conversation, menu);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_export:
                onExportRequest();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void onExportRequest() {
        // ask confirmation
        CHCApplication chcApplication = getCHCApplication();
        if (chcApplication == null) return;
        EntityUser user = chcApplication.getUser();
        if (user == null) return;
        String userEmail = user.getLoginEmail();
        if (isStringNull(userEmail)) {
            userEmail = user.getUsername();
        }

        new AlertDialog.Builder(getActivity())
                .setMessage(getString(R.string.private_conversation_export_msg) + userEmail)
                .setPositiveButton(R.string.export_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        doExport();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }

    private void doExport() {
        CHCApplication chcApplication = getCHCApplication();
        if (chcApplication == null) return;
        final String userId = chcApplication.getUserId();
        final String pushId = chcApplication.getRegId();
        final String targetId = entityId;
        if (isStringNull(userId) || isStringNull(pushId) || isStringNull(targetId)) {
            SplashActivity.start(getActivity());
            getActivity().finish();
            return;
        }

        new AsyncTask<Void, Void, Boolean>() {
            ProgressDialog pd;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd = ProgressDialog.show(getActivity(), null, getString(R.string.network_wait), true, false);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (pd != null) {
                    try {
                        pd.dismiss();
                    } catch (Exception e) {
                        // ignored
                    }
                }

                if (aBoolean == null || !aBoolean) {
                    // failed
                    Toast.makeText(getActivity(), R.string.export_failed, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), R.string.export_success, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    NetworkRequestsUtil.postExportConversation(userId, pushId, targetId);
                } catch (InternalErrorException e) {
                    return false;
                } catch (IOException e) {
                    return false;
                }
                return true;
            }
        }.execute();
    }

    private boolean isStringNull(String s) {
        return s == null || StringUtils.isBlank(s) || s.equals("null");
    }

    public RecordAmplitudePresenter getAmplitudePresenter() {
        return amplitudePresenter;
    }
}
