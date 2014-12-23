package com.chc.dochoo.conversations;

import android.util.SparseArray;

/**
 * Created by HenryW on 1/20/14.
 */
public enum OutgoingMessageStatus {
    SENDING(FoundMessage.MESSAGE_STATUS_UPDATE_SENDING),
    FAILED(FoundMessage.MESSAGE_STATUS_UPDATE_FAILED),
    SENT(FoundMessage.MESSAGE_STATUS_UPDATE_SENT),
    DELIVERED(FoundMessage.MESSAGE_STATUS_UPDATE_DELIVERED),
    READ(FoundMessage.MESSAGE_STATUS_UPDATE_READ);

    private static final SparseArray<OutgoingMessageStatus> map
    = new SparseArray<OutgoingMessageStatus>();
    static {
        for (OutgoingMessageStatus status : values()) {
            map.put(status.getStatusCode(), status);
        }
    }

    public static OutgoingMessageStatus getStatusByCode(int code) {
        return map.get(code);
    }

    private OutgoingMessageStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    private final int statusCode;

}