package com.sf_ert.ertdriver.Model;

import com.google.gson.annotations.SerializedName;

public class BusByUserId {

    @SerializedName("bus_id")
    private String bus_id;

    @SerializedName("departTime")
    private String departTime;

    @SerializedName("busNum")
    private String busNum;

    @SerializedName("routeName")
    private String routeName;

    @SerializedName("fromRoute")
    private String fromRoute;

    @SerializedName("toRoute")
    private String toRoute;

    public String getBus_id() {
        return bus_id;
    }

    public String getDepartTime() {
        return departTime;
    }

    public String getBusNum() {
        return busNum;
    }

    public String getRouteName() {
        return routeName;
    }

    public String getFromRoute() {
        return fromRoute;
    }

    public String getToRoute() {
        return toRoute;
    }
}
