package com.sf_ert.ertdriver.Model;

import com.google.gson.annotations.SerializedName;

public class Data {
    // TODO: 10/25/2019 Result for Login

    @SerializedName("status")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
