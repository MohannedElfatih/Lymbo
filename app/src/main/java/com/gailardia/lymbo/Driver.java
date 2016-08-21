package com.gailardia.lymbo;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import self.philbrown.droidQuery.$;
import self.philbrown.droidQuery.AjaxOptions;
import self.philbrown.droidQuery.Function;

public class Driver extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    Location location;
    String provider;
    private GoogleMap mMap;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        createFloatingAction();
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
        findRider();
    }

    private void findRider() {
        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            int timeSpent = 0;
            @Override
            public void run() {
                $.ajax(new AjaxOptions().url("http://www.lymbo.esy.es/checkRequest.php")
                        .type("POST")
                        .data("{\"Dname\":\"" + getSharedPreferences("com.gailardia.lymbo", Context.MODE_PRIVATE).getString("username", "NULL") + "\"}")
                        .context(Driver.this)
                        .success(new Function() {

                            @Override
                            public void invoke($ droidQuery, Object... objects) {
                                if (objects[0].toString().equalsIgnoreCase("Found Request")) {
                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(Driver.this);

                                    // Setting Dialog Title
                                    alertDialog.setTitle("Found a customer!");

                                    // Setting Dialog Message
                                    alertDialog.setMessage("Do you want to accept this customer?");


                                    // On pressing Accept button
                                    alertDialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            respond(0);
                                            t.cancel();
                                        }
                                    });

                                    // on pressing cancel button
                                    alertDialog.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            respond(1);
                                            dialog.cancel();
                                        }
                                    });

                                    // Showing Alert Message
                                    alertDialog.show();
                                } else {
                                    Log.i("ajaxDriver", "Time spent is : " + timeSpent);
                                    timeSpent += 5;
                                }
                            }
                        })
                        .error(new Function() {
                            @Override
                            public void invoke($ $, Object... args) {
                                Toast.makeText(Driver.this, "Couldn't find php", Toast.LENGTH_LONG).show();
                            }
                        }));
            }
        }, 0, 5000);
    }

    private void respond(int response) {
        $.ajax(new AjaxOptions().url("http://www.lymbo.esy.es/driverResponse.php")
                .type("POST")
                .data("{\"Dname\":\"" + getSharedPreferences("com.gailardia.lymbo", Context.MODE_PRIVATE).getString("username", "NULL") + "\"" + ",\"latitude\":\"" + location.getLatitude() + "\"" + ",\"longitude\":\"" + location.getLongitude() + ",\"response\":\"" + response + "\"}")
                .context(Driver.this)
                .success(new Function() {

                    @Override
                    public void invoke($ droidQuery, Object... objects) {
                        Log.i("reponse", "Successful");

                    }
                })
                .error(new Function() {
                    @Override
                    public void invoke($ $, Object... args) {
                        Log.i("reponse", "Unsuccessful");
                    }
                }));
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(this);
    }

    public void createFloatingAction() {
        final ImageView itemIcon4;
        SharedPreferences prefs = getSharedPreferences("com.gailardia.lymbo", MODE_PRIVATE);
        final String restoredText = prefs.getString("username", null);
        final HashMap map=new HashMap();
        map.put("username",restoredText);





        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageDrawable(getResources().getDrawable(R.drawable.menu));

        final FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
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

                if (!actionMenu.isOpen() && actionMenu2.isOpen() ) {
                    actionMenu.toggle(true);
                    actionMenu2.toggle(true);
                }else if(!actionMenu.isOpen()&& actionMenu3.isOpen()){
                    actionMenu.toggle(true);
                    actionMenu3.toggle(true);
                }else if(!actionMenu.isOpen() && actionMenu4.isOpen()){
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
                if(actionMenu4.isOpen()){
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
                if(actionMenu4.isOpen()){
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
                if(actionMenu2.isOpen()){
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

    protected void signoutAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        SharedPreferences prefs = getSharedPreferences("com.gailardia.lymbo", MODE_PRIVATE);
        final String restoredText = prefs.getString("username", null);
        final HashMap map=new HashMap();
        map.put("username",restoredText);
        final Intent intent = new Intent(this, choices.class);


        alertDialog.setTitle("Confirm");

        alertDialog.setMessage("Are you sure you want to sign out?");

        alertDialog.setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
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
    protected void deletAccountAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        SharedPreferences prefs = getSharedPreferences("com.gailardia.lymbo", MODE_PRIVATE);
        final String restoredText = prefs.getString("username", null);
        final HashMap map=new HashMap();
        map.put("username",restoredText);
        final Intent intent = new Intent(this, choices.class);


        alertDialog.setTitle("Confirm");

        alertDialog.setMessage("Are you sure you want to delete your account?");

        alertDialog.setPositiveButton("Delete account", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
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
