package com.chc.dochoo.conversations;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.makeramen.RoundedImageView;

/**
 * Created by HenryW on 2/12/14.
 */

class InstantMessageViewHolder {
    String ownerId;

    boolean isFromPatient;
    boolean isNotice;
    TextView tv;
    TextView time;
    ImageButton voiceImageBtn;
    SeekBar voiceSeekBar;
    View voiceWrapper;
    TextView voiceTime;
    ImageView ivImage;
    View unread;
    View messageHolderBg;
    TextView nameOfSender;
    TextView status;
    RoundedImageView icon;
    View failed;
    View imSelectedLayer;
}

