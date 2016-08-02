package com.gailardia.lymbo;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kosalgeek.asynctask.PostResponseAsyncTask;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.jar.*;

public class DriverActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, AsyncResponse {
    private GoogleMap mMap;
    private LocationManager locationManager;
    Location location;
    String provider;
    final LatLng sydney = new LatLng(-34, 151);
    final LatLng test = new LatLng(-34, 150);
    final LatLng burj = new LatLng(25.197525, 55.274288);
    private View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapLayout);
        mapFragment.getMapAsync(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        provider = locationManager.getBestProvider(new Criteria(), false);
        /*RelativeLayout mapLayout = (RelativeLayout) findViewById(R.id.relative);
        mapLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
            @Override
            public void onGlobalLayout() {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                ArrayList<Marker> markers = new ArrayList<Marker>();
                markers.add(mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));
                markers.add(mMap.addMarker(new MarkerOptions().position(test).title("Test").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))));
                for(Marker marker : markers){
                    builder.include(marker.getPosition());
                }
                mMap.addMarker(new MarkerOptions().position(burj).title("Burj Khalifa").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                LatLngBounds bounds = builder.build();
                int padding = 75;
                CameraUpdate cameraUpdate= CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.animateCamera(cameraUpdate);
            }
        });*/
        location = locationManager.getLastKnownLocation(provider);
        if (location == null) {
            Log.i("Last Known Location", "Unsuccessful");
        } else {
            Log.i("Last Known Location", "Successful");
        }
        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageDrawable( getResources().getDrawable(R.drawable.amjad) );

        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);

        ImageView itemIcon = new ImageView(this);
        itemIcon.setImageDrawable( getResources().getDrawable(R.drawable.car) );
        SubActionButton button1 = itemBuilder.setContentView(itemIcon).build();

        ImageView itemIcon2 = new ImageView(this);
        itemIcon2.setImageDrawable( getResources().getDrawable(R.drawable.boy) );
        SubActionButton button2 = itemBuilder.setContentView(itemIcon2).build();

        ImageView itemIcon3 = new ImageView(this);
        itemIcon3.setImageDrawable( getResources().getDrawable(R.drawable.car) );
        SubActionButton button3 = itemBuilder.setContentView(itemIcon3).build();

        ImageView itemIcon4 = new ImageView(this);
        itemIcon4.setImageDrawable( getResources().getDrawable(R.drawable.boy) );
        SubActionButton button4 = itemBuilder.setContentView(itemIcon4).build();


        final FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(button1)
                .addSubActionView(button2)
                // ...
                .attachTo(actionButton)
                .build();

        final FloatingActionMenu actionMenu2 = new FloatingActionMenu.Builder(this)
                .addSubActionView(button3)
                .addSubActionView(button4)
                // ...
                .attachTo(button1)
                .build();

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionMenu.toggle(true);

                if(!actionMenu.isOpen()&&actionMenu2.isOpen()){
                    actionMenu.toggle(true);
                    actionMenu2.toggle(true);
                }
            }
        });
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                boolean gpsStatus = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
                if(!gpsStatus){
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

    protected void alert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is disabled");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
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
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        provider = locationManager.getBestProvider(new Criteria(), false);
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(this);
    }

    public void accept(View view){
        final LatLng sydney = new LatLng(-34, 151);
        final LatLng test = new LatLng(-34, 152);
        final Intent intent;
        /*intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://maps.google.com/maps?" + "daddr=" + burj.latitude + "," + burj.longitude));
        intent.setPackage("com.google.android.apps.maps");
        if(intent.resolveActivity(getPackageManager()) != null){
            intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
            startActivity(intent);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Map unavailable")
                    .setMessage("This application requires google maps to be installed!")
                    .show();
        }*/
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.gailardia.lymbo", Context.MODE_PRIVATE);
        HashMap map=new HashMap();
        Double latitude = burj.latitude;
        Double longitude = burj.longitude;
        map.put("latitude",String.valueOf(latitude));
        map.put("longitude",String.valueOf(longitude));
        map.put("Dname", sharedPreferences.getString("username", null));
        PostResponseAsyncTask task=new PostResponseAsyncTask(this, map);
        task.execute("http://lymbo.esy.es/locationsarray.php");
        new getLocations().execute();
    }

    @Override
    public void processFinish(String s) {
    }

    public class getLocations extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            try {
                URL url = new URL("http://lymbo.esy.es/locationsarray.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                result = bufferedReader.readLine();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject;
                JSONArray jsonArray = new JSONArray(s);
                ArrayList<LatLng> latLng = new ArrayList<LatLng>();
                for(int i = 0; i < jsonArray.length(); i++){
                    jsonObject = (JSONObject) jsonArray.get(i);
                    latLng.add(new LatLng(jsonObject.getDouble("latitude"), jsonObject.getDouble("longitude")));
                    Log.i("hala", String.valueOf(latLng.get(i).latitude));
                    Log.i("hala", String.valueOf(latLng.get(i).longitude));
                }
                for(int i = 0; i < latLng.size(); i++){
                    mMap.addMarker(new MarkerOptions()
                    .position(latLng.get(i))
                    .draggable(false)
                    .title("Driver" + i)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("Locationsaha", s);
        }
    }
}
