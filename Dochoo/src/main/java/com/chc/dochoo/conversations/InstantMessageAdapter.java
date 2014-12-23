package com.chc.dochoo.conversations;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chc.found.models.EntityModel;
import com.chc.found.models.EntityUser;
import com.chc.found.models.GroupMember;
import com.chc.found.network.NetworkRequestsUtil;
import com.chcgp.hpad.util.download.ImageDownloader;
import com.makeramen.RoundedImageView;
import com.test.found.R;

import org.apache.commons.lang.StringUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * List adapter
 *
 * @author HenryW
 */
class InstantMessageAdapter extends BaseAdapter {

    private final Context mContext;
    private List<InstantMessage> msgList;
    // key: id of InstantMessage
    private SparseArray<VoiceControl> controlMap = new SparseArray<VoiceControl>();
    private final InstantMessageFragment mFragment;
    private final Handler mHandler;
    private EntityUser mEntity;
    private Bitmap mUserImage;
    private final Calendar cal = Calendar.getInstance();
    private SparseBooleanArray mSelected = new SparseBooleanArray();

    //Group Parameters
    private String groupId;
    private Boolean isGroupChat = false;

    public InstantMessageAdapter(Context context, List<InstantMessage> msgList, InstantMessageFragment fragment,
                                 Handler handler, EntityUser entity, Bitmap userImage) {
        this.mContext = context;
        this.msgList = msgList;
        this.mFragment = fragment;
        this.mHandler = handler;
        mEntity = entity;
        mUserImage = userImage;
    }

    public InstantMessageAdapter(Context context, List<InstantMessage> msgList, InstantMessageFragment fragment,
                                 Handler handler, String groupId, Bitmap userImage){
        this.mContext = context;
        this.msgList = msgList;
        this.mFragment = fragment;
        this.mHandler = handler;
        this.groupId = groupId;
        this.isGroupChat = true;
        mUserImage = userImage;
    }

    @Override
    public int getCount() {
        return msgList == null ? 0 : msgList.size();
    }

    @Override
    public InstantMessage getItem(int position) {
        return this.msgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final InstantMessage item = getItem(position);
        InstantMessage.InstantMessageType msgType = item.getMessageType();
        if (msgType == null) {
            msgType = InstantMessage.InstantMessageType.TEXT;
            item.setContent("");
        }
        View view;
        switch (msgType) {
            case IMAGE:
                view = getImageView(position, convertView, parent);
                break;
            case VOICE:
                view = getVoiceView(position, convertView, parent);
                break;
            case GROUPNOTICE:
                view = getNoticeView(position, convertView, parent);
                break;
            case TEXT:
            default:
                view = getTextView(position, convertView, parent);
                setupTextViewListener(view);
        }

        // debug
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return onInstantMessageViewLongClick(position);
            }
        });

        return view;
    }

    private boolean onInstantMessageViewLongClick(int position) {
        InstantMessage item = getItem(position);
        if (item != null) mFragment.onInstantMessageViewLongClick(item.getId());
        return true;
    }

    private void setupTextViewListener(View v) {
        // TODO Auto-generated method stub

    }

    private View getNoticeView(int position, View convertView, ViewGroup parent) {
        View view =getTextView(position, convertView, parent);
        final InstantMessageViewHolder holder = (InstantMessageViewHolder) view.getTag();
        final InstantMessage item = getItem(position);
        //all notice messages are set as from others
        holder.nameOfSender.setVisibility(View.GONE);
        holder.time.setVisibility(View.GONE);
        holder.icon.setVisibility(View.GONE);
        holder.messageHolderBg.setBackgroundColor(mFragment.getResources().getColor(R.color.default_bg_color));
        holder.tv.setText(item.getContent());
        holder.tv.setGravity(Gravity.CENTER);
        holder.tv.setBackgroundColor(mFragment.getResources().getColor(R.color.chc_default_grey));
        holder.tv.setTextColor(mFragment.getResources().getColor(R.color.contents_text));
        return view;
    }

    private View getVoiceView(int position, View convertView, ViewGroup parent) {
        View view = getTextView(position, convertView, parent);
        final InstantMessageViewHolder holder = (InstantMessageViewHolder) view.getTag();
        final InstantMessage item = getItem(position);
        // show voice view
        holder.voiceWrapper.setVisibility(View.VISIBLE);
        holder.tv.setText("");
        // disable old on click listener
//			holder.messageHolderBg.setOnClickListener(new OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    if (holder.unread != null)
//                        holder.unread.setVisibility(View.INVISIBLE);
//                    onVoiceViewClicked(playbackHolder, item);
//                }
//            });

        VoiceControl control = controlMap.get(position);
        if (control != null) {
            control.setHolderWeakReference(holder);
        } else {
            control = new VoiceControl(mFragment, mContext, mHandler, item, holder);
            controlMap.put(position, control);
        }
        control.setControls();

        if (item.isUnread()) {
            if (holder.unread != null)
                holder.unread.setVisibility(View.VISIBLE);
        }

        return view;
    }

    private View getImageView(int position, View convertView, ViewGroup parent) {
        View view = getTextView(position, convertView, parent);
        final InstantMessageViewHolder holder = (InstantMessageViewHolder) view.getTag();
        final InstantMessage item = getItem(position);
        holder.ivImage.setVisibility(View.VISIBLE);
        holder.tv.setText("");

        // image resource
        Bitmap bitmap = mFragment.getImageFromPath(item.getMultimediaPath());
        if (bitmap != null) {
            // downloaded
            holder.ivImage.setImageBitmap(bitmap);
        } else {
            // not yet
            holder.ivImage.setImageResource(R.drawable.download);
        }

        holder.ivImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mFragment.onImageDownloadClick(item, holder.ivImage);
            }
        });

        return view;
    }

    private View getTextView(final int position, View convertView, ViewGroup parent) {
        final InstantMessage item = getItem(position);
        //TODO:make it reusable
        // if touser is blank then it is from doctor
        /*if (convertView == null) {
            convertView = buildNewLayoutForMessage(item);
        } else {
            InstantMessageViewHolder holder = (InstantMessageViewHolder) convertView
                    .getTag();
            if (holder.isFromPatient != isFromPatient(item) || holder.isNotice) {
                convertView = buildNewLayoutForMessage(item);
            } else {
                // can reuse
            }
        }*/

        convertView = buildNewLayoutForMessage(item);

        InstantMessageViewHolder holder = (InstantMessageViewHolder) convertView.getTag();
        // reset views
        resetHolderViews(item.getId(), holder);

        final WeakReference<View> viewRef = new WeakReference<View>(convertView);
        // if is sending
        switch (item.getStatus()) {
            case READ:
                onMsgRead(holder);
                break;
            case DELIVERED:
                onMsgDelivered(holder);
                break;
            case FAILED:
                onSendMsgFailed(holder, item);
                // continue to next part
            case SENDING:
                item.registerStatusObserver(new Observer() {

                    @Override
                    public void update(Observable observable, Object data) {

                        View v = viewRef.get();

                        if (v == null) return;

                        InstantMessageViewHolder holder = (InstantMessageViewHolder) v.getTag();

                        // poll data
                        switch (item.getStatus()) {
                            case SENT:
                                updateOwnerId(item, controlMap.get(position));
                                onSendMsgSent(holder, item);
                                observable.deleteObserver(this);
                                break;
                            case FAILED:
                                onSendMsgFailed(holder, item);
                                break;
                            case SENDING:
                                onSendMsgSending(holder, item);
                                break;
                            default:
                                // ignore
                        }
                    }

                });
                break;
            default:
                // default status will be sent for safety reason
                if (holder.status != null) {
                    holder.status.setText(R.string.status_im_send_sent);
                }
        }
        if (holder.time != null) holder.time.setText(parseTime(item.getTime()));

        String curFullname = "";
        //group message from others
        if(isGroupChat && StringUtils.isNotBlank(item.getEntityId()) && !StringUtils.equals("group",item.getEntityId())){
            curFullname = item.getGroupDisplyName();
            if(StringUtils.isBlank(curFullname)){
                Log.e("GroupChat Display error","No display name found ");
            }
            int size = mFragment.getResources().getDimensionPixelSize(R.dimen.icon_instant_msg_size);
            ImageDownloader.getInstance().download(NetworkRequestsUtil.getIconImageUrlString(item.getEntityId()),
                    holder.icon, size, size, mFragment.getResources(), R.drawable.default_head);
        }
        else{
            if(mEntity!= null)  curFullname = mEntity.getFullname();
        }
        if (holder.nameOfSender != null && isValidFullname(curFullname))    {
            holder.nameOfSender.setText(curFullname);
        }
        /*if (holder.nameOfSender != null && mEntity != null && isValidFullname(mEntity.getFullname())) {
            holder.nameOfSender.setText(mEntity.getFullname());
        }*/

        if (holder.icon != null) {
            if (item.isFromPatient()) {
                if (mUserImage != null)
                    holder.icon.setImageBitmap(mUserImage);
            } else {
                if (mEntity != null) {
                    int size = mFragment.getResources().getDimensionPixelSize(R.dimen.icon_instant_msg_size);
                    ImageDownloader.getInstance().download(mEntity.getProfileIconUrl(),
                            holder.icon, size, size, mFragment.getResources(), R.drawable.default_head);
                }
            }
        }

        // regardless of the status of message, set its content
        holder.tv.setText(item.getContent());

        // display selected overlay if selected
        if (holder.imSelectedLayer != null) {

            holder.imSelectedLayer.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    // TODO: this is to be used to intercept event
                    // when doing multi-select
                    return mSelected.size() != 0;
                }
            });
        }

        return convertView;
    }

    private void onMsgRead(InstantMessageViewHolder holder) {
        if (holder.status != null) {
            holder.status.setVisibility(View.VISIBLE);
            holder.status.setText(R.string.status_im_send_is_read);
        }
    }

    private void onMsgDelivered(InstantMessageViewHolder holder) {
        if (holder.status != null) {
            holder.status.setVisibility(View.VISIBLE);
            holder.status.setText(R.string.status_im_send_delivered);
        }
    }


    private void updateOwnerId(InstantMessage item, VoiceControl voiceControl) {
        if (voiceControl != null
                && voiceControl.holderWeakReference != null
                && voiceControl.holderWeakReference.get() != null) {
            //noinspection ConstantConditions
            voiceControl.holderWeakReference.get().ownerId = item.getId();
        }
    }

    /**
     * *************************************************************
     */
        /* Begin */
        /* handles corresponding status of view of the sending message */
    protected void onSendMsgSending(InstantMessageViewHolder holder,
                                    InstantMessage item) {
        if (holder.status != null) {
            holder.status.setVisibility(View.VISIBLE);
            holder.status.setText(R.string.status_im_send_sending);
        }
        if (holder.failed != null) holder.failed.setVisibility(View.INVISIBLE);
    }

    protected void onSendMsgSent(InstantMessageViewHolder holder,
                                 InstantMessage item) {
        if (holder.status != null) {
            holder.status.setVisibility(View.VISIBLE);
            holder.status.setText(R.string.status_im_send_sent);
        }
        if (holder.time != null) holder.time.setText(parseTime(item.getTime()));
        mFragment.updateLastMsg(item);
    }

    private void onSendMsgFailed(InstantMessageViewHolder holder, final InstantMessage item) {
        if (holder.status != null) {
            holder.status.setVisibility(View.VISIBLE);
            holder.status.setText(R.string.status_im_send_failed);
        }
        if (holder.time != null) holder.time.setText(parseTime(item.getTime()));
        if (holder.failed != null) {
            holder.failed.setVisibility(View.VISIBLE);
            holder.failed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFragment.onMsgResend(item);
                    view.setVisibility(View.INVISIBLE);
                }
            });
        }
    }
		/* End */
    /*****************************************************************/

    /**
     * Reset the default properties of views in a holder for re-use
     * After reset, the view should appear as if it is text message
     *
     * @param holder
     */
    private void resetHolderViews(String newOwnerId, InstantMessageViewHolder holder) {
        holder.ownerId = newOwnerId;

        // remove the on click listener
        if (holder.messageHolderBg != null) holder.messageHolderBg.setOnClickListener(null);

        if (holder.time != null) {
            holder.time.setText("");
            holder.time.setVisibility(View.VISIBLE);
        }
        if (holder.ivImage != null) holder.ivImage.setVisibility(View.GONE);
        if (holder.tv != null) holder.tv.setText(""); // does not handle text color here
        if (holder.unread != null) holder.unread.setVisibility(View.INVISIBLE);
//            if (holder.voiceImageView != null) {
        // does not handle voice image button here
//            }
        // does not handle seekbar color here

        if (holder.voiceWrapper != null) holder.voiceWrapper.setVisibility(View.GONE);
        if (holder.nameOfSender != null) holder.nameOfSender.setText("");
        if (holder.status != null) {
            holder.status.setVisibility(View.VISIBLE);
            holder.status.setText(R.string.status_im_send_sending);
        }
        if (holder.voiceTime != null) holder.voiceTime.setText("");
        if (holder.failed != null) holder.failed.setVisibility(View.INVISIBLE);
//        if (holder.imSelectedLayer != null) holder.imSelectedLayer.setVisibility(View.INVISIBLE);
    }

    /**
     * Builds a new layout {@code View} for this specific
     * message. There are two kinds: in-bound and out-bound
     *
     * @param item
     * @return inflated layout {@code View}
     */
    @SuppressWarnings("ConstantConditions")
    private View buildNewLayoutForMessage(InstantMessage item) {
        View view;
        InstantMessageViewHolder holder = new InstantMessageViewHolder();
        boolean fromPatient = isFromPatient(item);
        if (fromPatient) {
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.item_im_out, null, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.item_im_in, null, false);
        }
        holder.tv = (TextView) view.findViewById(R.id.instant_msg_content);
        holder.isFromPatient = fromPatient;
        holder.isNotice = isNotice(item);
        holder.time = (TextView) view.findViewById(R.id.instant_msg_out_time);
        holder.ivImage = (ImageView) view.findViewById(R.id.instant_msg_image);
        holder.voiceImageBtn = (ImageButton) view.findViewById(R.id.instant_msg_voice_img_btn);
        holder.voiceSeekBar = (SeekBar) view.findViewById(R.id.instant_msg_voice_seekBar);
        holder.voiceWrapper = view.findViewById(R.id.instant_msg_voice_wrapper);
        holder.nameOfSender = (TextView) view.findViewById(R.id.instant_msg_from);
        holder.unread = view.findViewById(R.id.im_unread);
        holder.messageHolderBg = view.findViewById(R.id.instant_msg_container);
        holder.status = (TextView) view.findViewById(R.id.instant_msg_status);
        holder.voiceTime = (TextView) view.findViewById(R.id.instant_msg_voice_time);
        holder.icon = (RoundedImageView) view.findViewById(R.id.icon_im_user);
        holder.failed = view.findViewById(R.id.instant_msg_out_ivfailed);
        holder.imSelectedLayer = view.findViewById(R.id.im_selected);

        holder.ownerId = item.getId();
        view.setTag(holder);

        return view;
    }

    /**
     * Adds a new message into the list
     * and notifies data changed
     *
     * @param im
     */
    void onMessageReceived(InstantMessage im) {
        List<InstantMessage> msgList = new ArrayList<InstantMessage>(1);
        msgList.add(im);
        combineList(msgList);
    }

    void addOutgoingMsg(InstantMessage im) {
        this.msgList.add(im);
        this.notifyDataSetChanged();
    }

    public void combineList(List<InstantMessage> list) {
        synchronized (this) {
            // merge the list with current one
            Map<String, InstantMessage> idMap = new HashMap<String, InstantMessage>();
            for (InstantMessage m : this.msgList) {
                idMap.put(m.getId(), m);
            }
            for (InstantMessage m : list) {
                if (!idMap.containsKey(m.getId())) {
                    // if not in set, then it is new msg
                    this.msgList.add(m);
                } else {
                    // already has this element. only update its status
                    InstantMessage msgAlreadyIn = idMap.get(m.getId());
                    msgAlreadyIn.setStatus(m.getStatus());
                }
            }

            Collections.sort(this.msgList, new Comparator<InstantMessage>() {

                @Override
                public int compare(InstantMessage lhs, InstantMessage rhs) {
                    return ((Long) lhs.getTime()).compareTo(rhs.getTime());
                }
            });
        }
        this.notifyDataSetChanged();
    }

    private boolean isValidFullname(String fullname) {
        boolean res = StringUtils.isNotBlank(fullname);

        res &= !fullname.equals("null");
        res &= !fullname.equals("null null");

        return res;
    }

    public CharSequence parseTime(long time) {
        if (time == 0) return "";

        cal.setTimeInMillis(time);
        StringBuilder sb = new StringBuilder();

        sb
                .append(cal.get(Calendar.HOUR) == 0 ? 12 : cal.get(Calendar.HOUR))
                .append(':');
        int minute = cal.get(Calendar.MINUTE);
        if (minute < 10) {
            sb.append('0');
        }
        sb
                .append(cal.get(Calendar.MINUTE))
                .append(' ')
                .append(cal.get(Calendar.AM_PM) == Calendar.AM
                        ? mContext.getString(R.string.time_am)
                        : mContext.getString(R.string.time_pm))
                .append(' ')
                .append(cal.get(Calendar.MONTH) + 1)
                .append('-')
                .append(cal.get(Calendar.DAY_OF_MONTH))
                .append('-')
                .append(cal.get(Calendar.YEAR));

        return sb.toString();
    }

    public boolean isFromPatient(InstantMessage item) {
        return item.isFromPatient();
    }

    public boolean isNotice(InstantMessage item){
        return item.getMessageType() == InstantMessage.InstantMessageType.GROUPNOTICE;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelected.get(position));
    }

    public void removeSelection() {
        mSelected.clear();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelected.put(position, value);
        else
            mSelected.delete(position);

        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelected.size();
    }

    public void deleteMsgById(String msgId) {
        synchronized (this) {
            List<InstantMessage> msgList1 = this.msgList;
            for (int i = 0; i < msgList1.size(); i++) {
                InstantMessage im = msgList1.get(i);
                if (im.getId().equals(msgId)) {
                    this.msgList.remove(i);
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }
}

