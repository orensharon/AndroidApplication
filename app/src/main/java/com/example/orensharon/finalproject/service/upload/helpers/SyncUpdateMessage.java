package com.example.orensharon.finalproject.service.upload.helpers;

/**
 * Created by orensharon on 1/17/15.
 */
public final class SyncUpdateMessage {

    // message codes
    public static final int NO_CONNECTION = 1;
    public static final int SYNC_SUCCESSFUL = 2;
    public static final int SYNC_STARTED = 3;
    public static final int SYNC_CUSTOM_ERROR = 4;

    private int messageCode;

    // Data will contain the result data
    private Object data;

    private int messageType;

    public SyncUpdateMessage(int messageCode, int type, Object data){
        this.messageCode = messageCode;
        this.messageType = type;
        this.data = data;
    }

    public int getMessageCode(){
        return this.messageCode;
    }

    public int getMessageType(){
        return this.messageType;
    }

    public Object getData(){
        return this.data;
    }

    @Override
    public String toString() {
        return "Message: [code='" + messageCode + "']";
    }
}
