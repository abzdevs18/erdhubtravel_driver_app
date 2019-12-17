package com.sf_ert.ertdriver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jgabrielfreitas.core.BlurImageView;
import com.sf_ert.ertdriver.Api.ApiClient;
import com.sf_ert.ertdriver.Api.ApiInterface;
import com.sf_ert.ertdriver.Model.BusByUserId;
import com.sf_ert.ertdriver.Model.SignIn;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Login extends AppCompatActivity {
    EditText mEmail,mPass;
    BlurImageView mBlur;
    Button mGetStart;

    private static String BUS_ID;
    private static String DEPART_TIME;
    private static String BUS_NUM;
    private static String ROUTE_NAME;
    private static String FROM_ROUTE;
    private static String TO_ROUTE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        mBlur = (BlurImageView)findViewById(R.id.blurSplash);

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.transition);
        mBlur.startAnimation(animation);

        mEmail = findViewById(R.id.lEmail);
        mPass = findViewById(R.id.lPass);

        mGetStart = findViewById(R.id.get_started);
        mGetStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        String sEmail = mEmail.getText().toString();
        String sPass = mPass.getText().toString();

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<SignIn> call = apiInterface.signIn(sEmail,sPass);
        call.enqueue(new Callback<SignIn>() {
            @Override
            public void onResponse(Call<SignIn> call, Response<SignIn> response) {

                String status = response.body().getData().getStatus();

                if (status.equals("1")){
                    String p_k = response.body().getRow().getUsr_id();

                    Call<List<BusByUserId>> busById = apiInterface.getBusByUserId(p_k);
                    busById.enqueue(new Callback<List<BusByUserId>>() {
                        @Override
                        public void onResponse(Call<List<BusByUserId>> call, Response<List<BusByUserId>> response) {
                            BUS_ID = response.body().get(0).getBus_id();
                            BUS_NUM = response.body().get(0).getBusNum();
                            DEPART_TIME = response.body().get(0).getDepartTime();
                            ROUTE_NAME = response.body().get(0).getRouteName();
                            FROM_ROUTE = response.body().get(0).getFromRoute();
                            TO_ROUTE = response.body().get(0).getToRoute();

//                            Redirect into the Map Activity
                            SharedPreferences sharedPref = Login.this.getSharedPreferences("user_info", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("p_k",p_k);
                            editor.putString("bus_id",BUS_ID);
                            editor.putString("bus_num",BUS_NUM);
                            editor.putString("depart_time",DEPART_TIME);
                            editor.putString("route_name",ROUTE_NAME);
                            editor.putString("route_from",FROM_ROUTE);
                            editor.putString("route_to",TO_ROUTE);
                            editor.apply();
                            startActivity(new Intent(Login.this,BusLocation.class));

                        }

                        @Override
                        public void onFailure(Call<List<BusByUserId>> call, Throwable t) {

                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(),"Seems something went wrong...",Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<SignIn> call, Throwable t) {

            }
        });
    }
}
