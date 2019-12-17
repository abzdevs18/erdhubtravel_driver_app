package com.sf_ert.ertdriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.sf_ert.ertdriver.Adapter.MessageAdapter;
import com.sf_ert.ertdriver.Api.ApiClient;
import com.sf_ert.ertdriver.Api.ApiInterface;
import com.sf_ert.ertdriver.Model.BusRouteModel;
import com.sf_ert.ertdriver.Model.Messages;
import com.sf_ert.ertdriver.Model.SendMessage;

import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusLocation extends AppCompatActivity implements
        OnMapReadyCallback, PermissionsListener, SwipeRefreshLayout.OnRefreshListener {

    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 500L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 3;
    private MapboxMap mapboxMap;
    private MapView mapView;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private BusLocationCallback callback = new BusLocationCallback(this);
//    BottomSheet

    private BottomSheetBehavior bottomSheetBehavior, scheduleSheet;
//    Messaging

    String sender;

    TextView mReciever,mSendIcon;
    EditText mMsg;

    TextView mRouteSched, mRouteDestination;
    RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private List<Messages> messages;
    private MessageAdapter messageAdapter;

    private static String BUS_ID;
    private static String DEPART_TIME;
    private static String BUS_NUM;
    private static String ROUTE_NAME;
    private static String FROM_ROUTE;
    private static String TO_ROUTE;
    private static String NEW_FORM_ROUTE_TO;
    private static String NEW_FORM_ROUTE_FROM;

//    Socket
    private static Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://erdhubtravel.ga:3000");
        } catch (URISyntaxException e) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_bus_location);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        SwipeRefreshLayout mSwipeRefreshLayout;
//      websockets

        mSocket.connect();


        SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        sender = sharedPreferences.getString("p_k","");
        BUS_ID = sharedPreferences.getString("bus_id","");
        BUS_NUM = sharedPreferences.getString("bus_num","");
        DEPART_TIME = sharedPreferences.getString("depart_time","");
        ROUTE_NAME = sharedPreferences.getString("route_name","");
        FROM_ROUTE = sharedPreferences.getString("route_from","");
        TO_ROUTE = sharedPreferences.getString("route_to","");


        String[] part = FROM_ROUTE.split(", ");
        NEW_FORM_ROUTE_FROM = part[1] + ", " + part[0];

        String[] partTo = TO_ROUTE.split(", ");
        NEW_FORM_ROUTE_TO = partTo[1] + ", " + partTo[0];

        mRouteDestination = findViewById(R.id.routeDestination);
        mRouteDestination.setText(ROUTE_NAME);

        mRouteSched = findViewById(R.id.routeSched);
        mRouteSched.setText(DEPART_TIME);


        LinearLayoutManager transitActivity = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView = findViewById(R.id.msgs_recycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // TODO: 18/04/2019 The code below the setReverseLayout() is really cool..
//        ((LinearLayoutManager) layoutManager).setReverseLayout(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);


        final String user_id = "3";

        mMsg = findViewById(R.id.msg);
        mSendIcon = findViewById(R.id.send_icon);
        mSendIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String m = mMsg.getText().toString();
                sendMessage(sender,m);
//                sendMessage(sender,m);

            }
        });
//        message_recieve("3","1");
        message_recieve(sender);


//        BOttom SHeet

        View view = findViewById(R.id.bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(view);

        View schedule = findViewById(R.id.driverSchedule);
        scheduleSheet = BottomSheetBehavior.from(schedule);

        FloatingActionButton logout = findViewById(R.id.signOut);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        FloatingActionButton schedFab = findViewById(R.id.scheduleFab);
        schedFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });


        FloatingActionButton fab = findViewById(R.id.messenger);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Snackbar.make(v,"Heres the snackbar",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        ImageView refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeRefreshLayout.setRefreshing(true);
                message_recieve(sender);
                mSwipeRefreshLayout.setRefreshing(false);

            }
        });
    }

    private void logOut() {
        /*We get this from/during we login*/
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        startActivity(new Intent(BusLocation.this,Login.class));
    }

    private void message_recieve(String reciever) {
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<Messages>> r_chats = apiInterface.getMessagesDriver(reciever);
        r_chats.enqueue(new Callback<List<Messages>>() {
            @Override
            public void onResponse(Call<List<Messages>> call, Response<List<Messages>> response) {
                messages = response.body();
                messageAdapter = new MessageAdapter(messages,getApplicationContext());
                recyclerView.setAdapter(messageAdapter);
//                Toast.makeText(getApplicationContext(),"Locaded",Toast.LENGTH_LONG).show();
                for (int i = 0; i < response.body().size(); i++){
                    Log.d("D", response.body().get(i).getContent());
                }


            }

            @Override
            public void onFailure(Call<List<Messages>> call, Throwable t) {
                Log.d("D", "Error");

            }
        });
    }

    private void sendMessage(final String sender, String message) {
        final String sSend = sender.toString();
        final String sMsg = message.toString();
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<SendMessage>> mChat = apiInterface.sendMessage(sSend,sMsg);
        mChat.enqueue(new Callback<List<SendMessage>>() {
            @Override
            public void onResponse(Call<List<SendMessage>> call, Response<List<SendMessage>> response) {
                Toast.makeText(getApplicationContext(),"Sent",Toast.LENGTH_SHORT).show();
                mMsg.setText("");
                message_recieve(sender);
//                message_recieve(sender,receiver);
            }

            @Override
            public void onFailure(Call<List<SendMessage>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Sent?",Toast.LENGTH_SHORT).show();
                mMsg.setText("");

            }
        });
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.TRAFFIC_NIGHT,
                new Style.OnStyleLoaded() {
                    @Override public void onStyleLoaded(@NonNull Style style) {
                        enableLocationComponent(style);
                    }
                });
    }

    /**
     * Initialize the Maps SDK's LocationComponent
     */
    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            LocationComponentOptions locationComponentOptions = LocationComponentOptions.builder(BusLocation.this)
//                    .elevation(5)
//                    .foregroundDrawable(R.drawable.origin)
//                    .accuracyAlpha(float)
                    .build();
            // Activate the MapboxMap LocationComponent to show user location
//         Adding in LocationComponentOptions is also an optional parameter
            LocationComponentActivationOptions locationComponentActivationOptions = LocationComponentActivationOptions
                    .builder(BusLocation.this,loadedMapStyle)
                    .locationComponentOptions(locationComponentOptions)
                    .build();

            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // Set the LocationComponent activation options
//            LocationComponentActivationOptions locationComponentActivationOptions =
//                    LocationComponentActivationOptions.builder(this)
////                            .useDefaultLocationEngine(false)
//                            .build();

            // Activate with the LocationComponentActivationOptions object
            locationComponent.activateLocationComponent(locationComponentActivationOptions);

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

            initLocationEngine();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }
    /**
     * Set up the LocationEngine and the parameters for querying the device's location
     */
    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "Need to enable GPS Location",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            if (mapboxMap.getStyle() != null) {
                enableLocationComponent(mapboxMap.getStyle());
            }
        } else {
            Toast.makeText(this, "Need to enable GPS Location", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onRefresh() {
        message_recieve(sender);
    }

    private static class BusLocationCallback implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<BusLocation> activityWeakReference;

        BusLocationCallback(BusLocation activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(LocationEngineResult result) {
            BusLocation activity = activityWeakReference.get();

            if (activity != null) {
                Location location = result.getLastLocation();

                if (location == null) {
                    return;
                }

                // Create a Toast which displays the new location's coordinates
//                Toast.makeText(activity, result.getLastLocation().getLatitude() + ":" + result.getLastLocation().getLongitude(),Toast.LENGTH_SHORT).show();
//                sendCoordinates("2",result.getLastLocation().getLatitude() + ", " + result.getLastLocation().getLongitude());
                JSONObject data = new JSONObject();
                try {
                    data.put("bus_id", BUS_ID);
                    data.put("latitude", result.getLastLocation().getLatitude());
                    data.put("longitude", result.getLastLocation().getLongitude());
                    data.put("bodyNum", BUS_NUM);
                    data.put("from_route", NEW_FORM_ROUTE_FROM);
                    data.put("to_route", NEW_FORM_ROUTE_TO);


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mSocket.emit("message", data);
                // Pass the new location to the Maps SDK's LocationComponent
                if (activity.mapboxMap != null && result.getLastLocation() != null) {
                    activity.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());
                }
            }
        }

        @Override
        public void onFailure(@NonNull Exception exception) {

        }

    }

    private static void sendCoordinates(String bus_id, String app_coor) {
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<BusRouteModel>> busRoute = apiInterface.sendLocation(bus_id,app_coor);
        busRoute.enqueue(new Callback<List<BusRouteModel>>() {
            @Override
            public void onResponse(Call<List<BusRouteModel>> call, Response<List<BusRouteModel>> response) {

            }

            @Override
            public void onFailure(Call<List<BusRouteModel>> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Prevent leaks
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    }
