package com.sf_ert.ertdriver.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignIn {

    @SerializedName("adminUserName")
    private String adminUserName;

    @SerializedName("adminUserPass")
    private String adminUserPass;

    //Due to the nested json response we need to create new class/model for: data & row
    @SerializedName("data")
    @Expose
    private Data data;

    @SerializedName("row")
    @Expose
    private Row row;

    public Data getData() {
        return data;
    }

    public Row getRow() {
        return row;
    }

    public String getAdminUserName() {
        return adminUserName;
    }

    public String getAdminUserPass() {
        return adminUserPass;
    }
}
