package com.sf_ert.ertdriver.Model;

import com.google.gson.annotations.SerializedName;

public class SendMessage {
    @SerializedName("receiver")
    private String receiver;
    @SerializedName("sender")
    private String sender;
    @SerializedName("message")
    private String message;

    public String getReceiver() {
        return receiver;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }
}
