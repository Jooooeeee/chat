package com.example.chatapp.Bean;



public class Type {
    private int messageType;
    private int whereType;
    public static final int MESSAGE_TYPE_TEXT=2;
    public static final int MESSAGE_TYPE_IMAGE=1;
    public static final int WHERE_TYPE_OWN=3;
    public static final int WHERE_TYPE_OTHERS=4;

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public int getWhereType() {
        return whereType;
    }

    public void setWhereType(int whereType) {
        this.whereType = whereType;
    }
}
