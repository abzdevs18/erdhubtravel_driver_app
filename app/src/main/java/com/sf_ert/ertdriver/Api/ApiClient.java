package com.sf_ert.ertdriver.Api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static final String BASE_URL = "http://erdhubtravel.ga";
    public static Retrofit retrofit;

    public static Retrofit getApiClient(){
        if (retrofit == null){

            Gson gson = new GsonBuilder().setLenient().create();

            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20,TimeUnit.SECONDS)
                    .readTimeout(20,TimeUnit.SECONDS).build();

            retrofit =  new Retrofit.Builder().client(okHttpClient)
                    .baseUrl(BASE_URL).
                    addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
