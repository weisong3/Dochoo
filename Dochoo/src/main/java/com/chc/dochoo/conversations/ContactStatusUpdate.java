package com.chc.dochoo.conversations;

/**
 * Created by Lance on 3/28/14.
 */
public class ContactStatusUpdate extends FoundMessage {
    private short contactUpdateType;     //operation type:add or delete

    public ContactStatusUpdate(Short type){
        this.contactUpdateType = type;
    }

    public short getContactUpdateType() {
        return contactUpdateType;
    }
}
