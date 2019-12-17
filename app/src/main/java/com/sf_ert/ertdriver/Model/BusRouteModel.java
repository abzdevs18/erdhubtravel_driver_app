package com.sf_ert.ertdriver.Model;

import com.google.gson.annotations.SerializedName;

public class BusRouteModel {

    @SerializedName("bus_id")
    private String bus_id;

    @SerializedName("app_coor")
    private String app_coor;

    public String getBus_id() {
        return bus_id;
    }

    public String getApp_coor() {
        return app_coor;
    }
}
