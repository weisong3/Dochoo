package com.chc.dochoo.conversations;

/**
 * Created by HenryW on 1/20/14.
 */
public class StatusUpdateMessage extends FoundMessage {


    private OutgoingMessageStatus statusUpdate;

    public StatusUpdateMessage() {
        // default empty constructor
    }

    public void setStatus(int status) {
        OutgoingMessageStatus statusByCode = OutgoingMessageStatus.getStatusByCode(status);
        if (statusByCode == null) throw new IllegalArgumentException();
        this.statusUpdate = statusByCode;
    }

    public OutgoingMessageStatus getStatusUpdate() {
        return this.statusUpdate;
    }
}
