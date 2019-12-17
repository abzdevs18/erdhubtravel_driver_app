package com.sf_ert.ertdriver.Model;

import com.google.gson.annotations.SerializedName;

public class Messages {
    @SerializedName("reciever")
    private String reciever;
    @SerializedName("sender")
    private String sender;
    @SerializedName("content")
    private String content;
    @SerializedName("time")
    private String time;
    @SerializedName("senderImg")
    private String senderImg;

    public String getReciever() {
        return reciever;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    public String getSenderImg() {
        return senderImg;
    }
}
