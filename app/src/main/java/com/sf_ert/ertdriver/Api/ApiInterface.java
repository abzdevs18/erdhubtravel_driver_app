package com.sf_ert.ertdriver.Api;


import com.sf_ert.ertdriver.Model.BusByUserId;
import com.sf_ert.ertdriver.Model.BusRouteModel;
import com.sf_ert.ertdriver.Model.Messages;
import com.sf_ert.ertdriver.Model.SendMessage;
import com.sf_ert.ertdriver.Model.SignIn;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("Api/appCoor")
    Call<List<BusRouteModel>> sendLocation(
            @Field("bus_id") String bus_id,
            @Field("app_coor") String app_coor);

    @FormUrlEncoded
    @POST("Api/driverMessageSender")
    Call<List<SendMessage>> sendMessage(@Field("sender") String sender,
                                        @Field("message") String message);
    @FormUrlEncoded
    @POST("Api/getMessagesDriver")
    Call<List<Messages>> getMessagesDriver(@Field("receiver") String receiver);

    @FormUrlEncoded
    @POST("Api/getBusByUserId")
    Call<List<BusByUserId>> getBusByUserId(@Field("p_k") String p_k);

    @FormUrlEncoded
    @POST("Api/getMessages")
    Call<List<Messages>> getMessages(@Field("receiver") String receiver,
                                     @Field("sender") String sender);


    @FormUrlEncoded
    @POST("Init/adminLogin")
        //user Call<List<[yourModel]>> if you are expecting array in response, use below code if expecting object response
    Call<SignIn> signIn(@Field("adminUserName") String email,
                        @Field("adminUserPass") String password);

}
