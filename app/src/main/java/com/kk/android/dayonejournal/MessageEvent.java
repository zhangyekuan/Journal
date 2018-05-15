package com.kk.android.dayonejournal;

import java.util.UUID;

public class MessageEvent{
    private UUID mUUID;
    public  MessageEvent(UUID uuid){
        mUUID = uuid;
    }
    public UUID getDayUUID() {
        return mUUID;
    }
}
