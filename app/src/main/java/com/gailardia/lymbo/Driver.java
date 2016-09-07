package com.gailardia.lymbo;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import self.philbrown.droidQuery.$;
import self.philbrown.droidQuery.AjaxOptions;
import self.philbrown.droidQuery.Function;

public class Driver extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    Snackbar snackbar;
    public List<Polyline> polyLines = new ArrayList<Polyline>();
    public List<Route> routes = new ArrayList<>();
    View driverSheet;
    Location location;
    String provider;
    int tripPrice;
    Marker destinationMarker;
    int lastRequestId;
    View coordinatorLayoutView;
    private JSONArray responseJson;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private boolean isInForeground = false;
    private GoogleApiClient mGoogleApiClient;
    Dialog mBottomSheetDialog;
    FloatingActionButton actionButton;
    int counter = 0;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            counter +=1;
            Log.i("Counter", "counter is : " + String.valueOf(counter));
            Log.e("service", "Received intent");
            String response = intent.getStringExtra("response");
            Log.i("Response", response);
            try {
                responseJson = new JSONArray(response);
                if(responseJson == null){
                    Toast.makeText(Driver.this, "startAgain()", Toast.LENGTH_SHORT).show();
                    Log.i("fucker", "startAgain()");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            findRider();
                        }
                    }, 10000);
                } else if (responseJson.getInt(6) == lastRequestId || response.equals("no")) {
                    Log.i("Response", response);
                    Log.i("lastRequest", "Same requestId");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            findRider();
                        }
                    }, 1000);
                } else {
                    Toast.makeText(getApplicationContext(), "Found a customer.", Toast.LENGTH_SHORT).show();
                    ringtone();
                    PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
                    PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
                    wakeLock.acquire();
                    KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
                    KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
                    keyguardLock.disableKeyguard();
                    if (Build.VERSION.SDK_INT >= 11) {
                        ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
                        List<ActivityManager.RunningTaskInfo> rt = am.getRunningTasks(Integer.MAX_VALUE);

                        for (int i = 0; i < rt.size(); i++) {
                            // bring to front
                            if (rt.get(i).baseActivity.toShortString().indexOf("com.gailardia.lymbo") > -1) {
                                Intent not = new Intent(getApplicationContext(), Driver.class);
                                Notification notification = new Notification.Builder(getApplicationContext())
                                        .setContentTitle("Request arrived")
                                        .setSmallIcon(R.drawable.human)
                                        .build();
                                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                notificationManager.notify(1, notification);
                                am.moveTaskToFront(rt.get(i).id, ActivityManager.MOVE_TASK_WITH_HOME);
                            }
                        }
                    }
                    tripPrice = responseJson.getInt(5);
                    if (location == null) {
                        if (ActivityCompat.checkSelfPermission(Driver.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Driver.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        location = locationManager.getLastKnownLocation(provider);
                    }
                    String[] params = {String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), String.valueOf(responseJson.get(2)), String.valueOf(responseJson.get(3)), String.valueOf(1)};
                    new GetRoute().execute(params);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        coordinatorLayoutView = findViewById(R.id.snackbarPosition);
        snackbar = Snackbar.make(coordinatorLayoutView, "Searching for requests.", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createFloatingAction();
            }
        })
                .show();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location = locationManager.getLastKnownLocation(provider);
        if (location == null) {
            Log.i("Last Known Location", "Unsuccessful");
        } else {
            Log.i("Last Known Location", "Unsuccessful");
        }
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DriverTimerService.TRANSACTION_DONE);
        registerReceiver(broadcastReceiver, intentFilter);
        findRider();
    }

    private void findRider() {
        Intent i = new Intent(Driver.this, DriverTimerService.class);
        //i.setComponent(new ComponentName("com.gailardia.lymbo", "DriverTimerService.java"));
        startService(i);
    }

    public void openDriverSheet() {
        driverSheet = getLayoutInflater().inflate(R.layout.driver_sheet, null);
        ImageButton accept = (ImageButton) driverSheet.findViewById(R.id.accept);
        ImageButton cancel = (ImageButton) driverSheet.findViewById(R.id.cancel);
        TextView price = (TextView) driverSheet.findViewById(R.id.price);
        TextView duration = (TextView) driverSheet.findViewById(R.id.duration);
        TextView distance = (TextView) driverSheet.findViewById(R.id.distance);
        final TextView countDown = (TextView) driverSheet.findViewById(R.id.countDownTimer);
        Log.wtf("Route length", String.valueOf(routes.size()));
        duration.setText("Duration to destination : " + routes.get(routes.size() - 1).durationText);
        distance.setText("Distance to destination : " + routes.get(routes.size() - 1).distanceText);
        price.setText("Trip price : " + tripPrice + " SDG");
        mBottomSheetDialog = new Dialog(Driver.this, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(driverSheet);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.setCanceledOnTouchOutside(false);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();
        final CountDownTimer countDownTimer = new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //TextView countDown = (TextView) driverSheet.findViewById(R.id.countDownTimer);
                Log.i("timer", String.valueOf(millisUntilFinished));
                int show = (int) TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                countDown.setText(String.valueOf(show));
            }

            @Override
            public void onFinish() {
                mBottomSheetDialog.cancel();
                try {
                    lastRequestId = responseJson.getInt(6);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new Respond().execute(String.valueOf(1));
                findRider();
            }
        }.start();
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    lastRequestId = responseJson.getInt(6);
                    Intent intent = new Intent(Driver.this, DriverTimerService.class);
                    stopService(intent);
                    mBottomSheetDialog.cancel();
                    countDownTimer.cancel();
                    String[] params = new String[]{String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), String.valueOf(responseJson.get(0)), String.valueOf(responseJson.get(1)), String.valueOf(2)};
                    new GetRoute().execute(params);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new Respond().execute(String.valueOf(2));
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    lastRequestId = responseJson.getInt(6);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new Respond().execute(String.valueOf(1));
                countDownTimer.cancel();
                mBottomSheetDialog.cancel();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        findRider();
                    }
                }, 5000);

            }
        });
    }

    protected void unanimateRoute() {
        for (int i = 0; i < polyLines.size(); i++) {
            polyLines.get(i).remove();
        }
        polyLines.clear();
        if (destinationMarker != null) {
            destinationMarker.remove();
        }
    }

    protected void destinationMarker(final LatLng latLng) {
        if (destinationMarker == null) {
            destinationMarker = mMap.addMarker(new MarkerOptions()
                    .title("Destination")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.human))
                    .position(latLng));
        } else {
            destinationMarker.remove();
            destinationMarker = mMap.addMarker(new MarkerOptions()
                    .title("Destination")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.human))
                    .draggable(true)
                    .position(latLng));
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    protected void animateRoute() {
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        Log.i("route", String.valueOf(routes.size()));
        bounds.include(routes.get(routes.size() - 1).getStartLocation());
        bounds.include(routes.get(routes.size() - 1).getEndLocation());
        PolylineOptions polylineOptions = new PolylineOptions()
                .geodesic(true)
                .color(Color.BLUE)
                .width(10);
        polylineOptions.add(routes.get(routes.size() - 1).startLocation);
        for (int i = 0; i < routes.get(routes.size() - 1).points.size(); i++) {
            polylineOptions.add(routes.get(routes.size() - 1).points.get(i));
        }
        Log.i("Distance", "Distance is : " + routes.get(routes.size() - 1).getDistanceText() + " Duration is : " + routes.get(routes.size() - 1).getDurationText());
        polylineOptions.add(routes.get(routes.size() - 1).endLocation);
        for (int i = 0; i < polyLines.size(); i++) {
            polyLines.get(i).remove();
        }
        polyLines.clear();
        polyLines.add(mMap.addPolyline(polylineOptions));
        //destinationMarker(routes.get(routes.size() - 1).getEndLocation());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds.build(), 100);
        mMap.animateCamera(cameraUpdate);
    }

    private void respond(int response) {
        new Route().asynchronousCall("http://www.lymbo.esy.es/driverResponse.php", "{\"Dname\":\"" + getSharedPreferences("com.gailardia.lymbo", Context.MODE_PRIVATE).getString("username", "NULL") + "\""
                + ",\"latitude\":\"" + location.getLatitude() + "\""
                + ",\"longitude\":\"" + location.getLongitude()
                + ",\"response\":\"" + response + "\"}");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        onResume();
        mMap.setMyLocationEnabled(true);
        if (location != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .bearing(90)
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(15)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                boolean gpsStatus = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
                if (!gpsStatus) {
                    alert();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location destin = new Location(provider);
        if (!routes.isEmpty()) {
            destin.setLongitude(routes.get(routes.size() - 1).getEndLocation().longitude);
            destin.setLatitude(routes.get(routes.size() - 1).getEndLocation().latitude);
        }
        if (location.distanceTo(destin) < 500) {
            unanimateRoute();
            routes.clear();
            Intent intent = null;
            try {
                intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr="
                                + responseJson.get(2) + "," + responseJson.get(3)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(intent);
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
        this.location = location;

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        Toast.makeText(getApplicationContext(), "GPS enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String s) {
        alert();
    }

    protected void alert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is disabled");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    protected void onResume() {
        super.onResume();
        isInForeground = false;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        provider = locationManager.getBestProvider(new Criteria(), false);
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, DriverTimerService.class);
        stopService(intent);
        unregisterReceiver(broadcastReceiver);
    }

    protected void onPause() {
        super.onPause();
        Intent i = new Intent(Driver.this, DriverTimerService.class);
        if (mBottomSheetDialog != null) {
            if (!mBottomSheetDialog.isShowing()) {
                stopService(i);
                startService(i);
            }
        } else {
            stopService(i);
            startService(i);
        }

        isInForeground = true;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(this);
    }

    public void ringtone() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createFloatingAction() {
        final ImageView itemIcon4;
        SharedPreferences prefs = getSharedPreferences("com.gailardia.lymbo", MODE_PRIVATE);
        final String restoredText = prefs.getString("username", null);
        final HashMap map = new HashMap();
        map.put("username", restoredText);
        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageDrawable(getResources().getDrawable(R.drawable.menu));
        actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();
        final SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);


        final ImageView itemIcon = new ImageView(this);
        itemIcon.setImageDrawable(getResources().getDrawable(R.drawable.online));
        final SubActionButton button1 = itemBuilder.setContentView(itemIcon).build();
        final ImageView itemIcon2 = new ImageView(this);
        itemIcon2.setImageDrawable(getResources().getDrawable(R.drawable.map));
        final SubActionButton button2 = itemBuilder.setContentView(itemIcon2).build();

        final ImageView itemIcon3 = new ImageView(this);
        itemIcon3.setImageDrawable(getResources().getDrawable(R.drawable.online));
        SubActionButton button3 = itemBuilder.setContentView(itemIcon3).build();

        itemIcon4 = new ImageView(this);
        itemIcon4.setImageDrawable(getResources().getDrawable(R.drawable.busy));
        SubActionButton button4 = itemBuilder.setContentView(itemIcon4).build();

        ImageView itemIcon5 = new ImageView(this);
        itemIcon5.setImageDrawable(getResources().getDrawable(R.drawable.map));
        SubActionButton button5 = itemBuilder.setContentView(itemIcon5).build();

        ImageView itemIcon6 = new ImageView(this);
        itemIcon6.setImageDrawable(getResources().getDrawable(R.drawable.sat));
        final SubActionButton button6 = itemBuilder.setContentView(itemIcon6).build();

        ImageView itemIcon7 = new ImageView(this);
        itemIcon7.setImageDrawable(getResources().getDrawable(R.drawable.account2));
        final SubActionButton button7 = itemBuilder.setContentView(itemIcon7).build();

        ImageView itemIcon8 = new ImageView(this);
        itemIcon8.setImageDrawable(getResources().getDrawable(R.drawable.signout));
        final SubActionButton button8 = itemBuilder.setContentView(itemIcon8).build();

        ImageView itemIcon9 = new ImageView(this);
        itemIcon9.setImageDrawable(getResources().getDrawable(R.drawable.delete));
        final SubActionButton button9 = itemBuilder.setContentView(itemIcon9).build();


        final FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(button7)
                .addSubActionView(button1)
                .addSubActionView(button2)
                .attachTo(actionButton)
                .build();

        final FloatingActionMenu actionMenu2 = new FloatingActionMenu.Builder(this)
                .addSubActionView(button3)
                .addSubActionView(button4)
                .setEndAngle(240)
                .attachTo(button1)
                .build();

        final FloatingActionMenu actionMenu3 = new FloatingActionMenu.Builder(this)
                .addSubActionView(button5)
                .addSubActionView(button6)
                .attachTo(button2)
                .setStartAngle(225)
                .build();

        final FloatingActionMenu actionMenu4 = new FloatingActionMenu.Builder(this)
                .addSubActionView(button9)
                .addSubActionView(button8)
                .attachTo(button7)
                .setEndAngle(220)
                .build();

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionMenu.toggle(true);

                if (!actionMenu.isOpen() && actionMenu2.isOpen()) {
                    actionMenu.toggle(true);
                    actionMenu2.toggle(true);
                } else if (!actionMenu.isOpen() && actionMenu3.isOpen()) {
                    actionMenu.toggle(true);
                    actionMenu3.toggle(true);
                } else if (!actionMenu.isOpen() && actionMenu4.isOpen()) {
                    actionMenu.toggle(true);
                    actionMenu4.toggle(true);
                }
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionMenu2.toggle(true);

                if (actionMenu3.isOpen()) {
                    actionMenu3.toggle(true);
                }
                if (actionMenu4.isOpen()) {
                    actionMenu4.toggle(true);
                }
            }

        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionMenu3.toggle(true);

                if (actionMenu2.isOpen()) {
                    actionMenu2.toggle(true);
                }
                if (actionMenu4.isOpen()) {
                    actionMenu4.toggle(true);
                }
            }

        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemIcon.setImageDrawable(getResources().getDrawable(R.drawable.busy));
                actionMenu2.toggle(true);
                PostResponseAsyncTask readTask = new PostResponseAsyncTask(Driver.this, map, false, new AsyncResponse() {
                    @Override
                    public void processFinish(String s) {
                        Toast.makeText(getApplicationContext(), "You are now in busy state", Toast.LENGTH_LONG).show();
                    }
                });
                readTask.execute("http://lymbo.esy.es/set_offline.php");

            }

        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemIcon.setImageDrawable(getResources().getDrawable(R.drawable.online));
                actionMenu2.toggle(true);
                PostResponseAsyncTask readTask = new PostResponseAsyncTask(Driver.this, map, false, new AsyncResponse() {
                    @Override
                    public void processFinish(String s) {
                        Toast.makeText(getApplicationContext(), "You are now in online state", Toast.LENGTH_LONG).show();
                    }
                });
                readTask.execute("http://lymbo.esy.es/set_Online.php");

            }

        });
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemIcon2.setImageDrawable(getResources().getDrawable(R.drawable.sat));
                actionMenu3.toggle(true);

                Toast.makeText(getApplicationContext(), "You are now on satellite map view", Toast.LENGTH_LONG).show();
            }

        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemIcon2.setImageDrawable(getResources().getDrawable(R.drawable.map));
                actionMenu3.toggle(true);
                Toast.makeText(getApplicationContext(), "You are now on normal map view", Toast.LENGTH_LONG).show();
            }

        });
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionMenu4.toggle(true);

                if (actionMenu3.isOpen()) {
                    actionMenu3.toggle(true);
                }
                if (actionMenu2.isOpen()) {
                    actionMenu2.toggle(true);
                }
            }

        });

        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signoutAlert();

            }

        });
        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deletAccountAlert();

            }

        });

    }

    protected void signoutAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        SharedPreferences prefs = getSharedPreferences("com.gailardia.lymbo", MODE_PRIVATE);
        final String restoredText = prefs.getString("username", null);
        final HashMap map = new HashMap();
        map.put("username", restoredText);
        final Intent intent = new Intent(this, choices.class);


        alertDialog.setTitle("Confirm");

        alertDialog.setMessage("Are you sure you want to sign out?");

        alertDialog.setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                PostResponseAsyncTask readTask = new PostResponseAsyncTask(Driver.this, map, false, new AsyncResponse() {
                    @Override
                    public void processFinish(String s) {
                        Toast.makeText(getApplicationContext(), "Signed out", Toast.LENGTH_LONG).show();
                    }
                });
                readTask.execute("http://lymbo.esy.es/set_offline.php");
                SharedPreferences settings = getSharedPreferences("com.gailardia.lymbo", Context.MODE_PRIVATE);
                settings.edit().clear().commit();

                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    protected void deletAccountAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        SharedPreferences prefs = getSharedPreferences("com.gailardia.lymbo", MODE_PRIVATE);
        final String restoredText = prefs.getString("username", null);
        final HashMap map = new HashMap();
        map.put("username", restoredText);
        final Intent intent = new Intent(this, choices.class);


        alertDialog.setTitle("Confirm");

        alertDialog.setMessage("Are you sure you want to delete your account?");

        alertDialog.setPositiveButton("Delete account", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                PostResponseAsyncTask readTask = new PostResponseAsyncTask(Driver.this, map, false, new AsyncResponse() {
                    @Override
                    public void processFinish(String s) {
                        Toast.makeText(getApplicationContext(), "Account successfully deleted!", Toast.LENGTH_LONG).show();
                    }
                });
                readTask.execute("http://lymbo.esy.es/Delete_account.php");
                SharedPreferences settings = getSharedPreferences("com.gailardia.lymbo", Context.MODE_PRIVATE);
                settings.edit().clear().commit();

                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            location.setLatitude(mLastLocation.getLatitude());
            location.setLongitude(mLastLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public class GetRoute extends AsyncTask<String, String, String> {
        private ProgressDialog dialog = new ProgressDialog(Driver.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("routes", "reached preExecute");
            routes.clear();
            dialog.setMessage("Downloading info, please wait.");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            try {
                URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin="
                        + strings[0] + "," + strings[1] + "&"
                        + "destination=" + strings[2] + "," + strings[3] + "&key"
                        + "AIzaSyDtYl3HYOjjLLbyEkISc4jiy9KG4rUDrms");
                JSONObject jsonObject = new JSONObject(new Route().synchronousCall(String.valueOf(url), ""));
                JSONArray jsonArray = jsonObject.getJSONArray("routes");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonRoute = jsonArray.getJSONObject(i);
                    Route route = new Route();
                    JSONObject overviewPoly = jsonRoute.getJSONObject("overview_polyline");
                    JSONArray legs = jsonRoute.getJSONArray("legs");
                    JSONObject leg = legs.getJSONObject(0);
                    route.distance = leg.getJSONObject("distance").getInt("value");
                    route.distanceText = leg.getJSONObject("distance").getString("text");
                    route.duration = leg.getJSONObject("duration").getInt("value");
                    route.durationText = leg.getJSONObject("duration").getString("text");
                    route.endAddress = leg.getString("end_address");
                    route.startAddress = leg.getString("start_address");
                    route.endLocation = new LatLng(leg.getJSONObject("end_location").getDouble("lat"), leg.getJSONObject("end_location").getDouble("lng"));
                    route.startLocation = new LatLng(leg.getJSONObject("start_location").getDouble("lat"), leg.getJSONObject("start_location").getDouble("lng"));
                    route.points = new Route().decodePolyLine(overviewPoly.getString("points"));
                    routes.add(route);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return strings[strings.length - 1];
        }

        @Override
        protected void onPostExecute(String data) {
            Log.i("routes", "reached after Execute");
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (data.contains("1")) {
                openDriverSheet();
            }
            if (data.contains("2")) {
                animateRoute();
            }
        }
    }

    public class Respond extends AsyncTask<String, String, String> {
        private ProgressDialog dialog = new ProgressDialog(Driver.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {
            System.out.println("Done it");
            new Route().synchronousCall("http://www.lymbo.esy.es/driverResponse.php", "{\"Dname\":\"" + getSharedPreferences("com.gailardia.lymbo", Context.MODE_PRIVATE).getString("username", "NULL") + "\""
                    + ",\"latitude\":\"" + location.getLatitude() + "\""
                    + ",\"longitude\":\"" + location.getLongitude() + "\""
                    + ",\"response\":\"" + strings[0] + "\"}");
            return null;
        }

        @Override
        protected void onPostExecute(String data) {

        }
    }
    /*try {
        JSONArray responseJson = new JSONArray(response);
        URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin="
                + responseJson.get(0) + "," + responseJson.get(1) + "&"
                + "destination=" + location.getLatitude() + "," + location.getLongitude() + "&key"
                + "AIzaSyDtYl3HYOjjLLbyEkISc4jiy9KG4rUDrms");
        JSONObject jsonObject = new JSONObject(new Route().synchronousCall(String.valueOf(url), ""));
        JSONArray jsonArray = jsonObject.getJSONArray("routes");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonRoute = jsonArray.getJSONObject(i);
            Route route = new Route();
            JSONObject overviewPoly = jsonRoute.getJSONObject("overview_polyline");
            JSONArray legs = jsonRoute.getJSONArray("legs");
            JSONObject leg = legs.getJSONObject(0);
            route.distance = leg.getJSONObject("distance").getInt("value");
            route.distanceText = leg.getJSONObject("distance").getString("text");
            route.duration = leg.getJSONObject("duration").getInt("value");
            route.durationText = leg.getJSONObject("duration").getString("text");
            route.endAddress = leg.getString("end_address");
            route.startAddress = leg.getString("start_address");
            route.endLocation = new LatLng(leg.getJSONObject("end_location").getDouble("lat"), leg.getJSONObject("end_location").getDouble("lng"));
            route.startLocation = new LatLng(leg.getJSONObject("start_location").getDouble("lat"), leg.getJSONObject("start_location").getDouble("lng"));
            route.points = new Route().decodePolyLine(overviewPoly.getString("points"));
            driverRoutes.add(route);
        }
    } catch (IOException e) {
        e.printStackTrace();
    } catch (JSONException e) {
        e.printStackTrace();
    }
    return null;
}

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        bounds.include(routes.get(routes.size() - 1).getStartLocation());
        bounds.include(routes.get(routes.size() - 1).getEndLocation());
        bounds.include(driverRoutes.get(driverRoutes.size() - 1).getStartLocation());
        bounds.include(driverRoutes.get(driverRoutes.size() - 1).getEndLocation());
        PolylineOptions polylineOptions = new PolylineOptions()
                .geodesic(true)
                .color(Color.RED)
                .width(10);
        polylineOptions.add(driverRoutes.get(driverRoutes.size() - 1).startLocation);
        for (int i = 0; i < driverRoutes.get(driverRoutes.size() - 1).points.size(); i++) {
            polylineOptions.add(driverRoutes.get(driverRoutes.size() - 1).points.get(i));
        }
        Log.i("Distance", "Distance is : " + driverRoutes.get(driverRoutes.size() - 1).getDistanceText() + " Duration is : " + driverRoutes.get(driverRoutes.size() - 1).getDurationText());
        polylineOptions.add(driverRoutes.get(driverRoutes.size() - 1).endLocation);
        for (int i = 0; i < driverPolylines.size(); i++) {
            driverPolylines.get(i).remove();
        }
        driverPolylines.clear();
        driverPolylines.add(mMap.addPolyline(polylineOptions));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds.build(), 100);
        mMap.animateCamera(cameraUpdate);
        dialog.cancel();
    }*/
}
