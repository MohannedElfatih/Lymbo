package com.gailardia.lymbo;

import android.*;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kosalgeek.asynctask.AsyncResponse;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import self.philbrown.droidQuery.$;
import self.philbrown.droidQuery.AjaxOptions;
import self.philbrown.droidQuery.Function;

public class Rider extends AppCompatActivity implements OnMapReadyCallback, LocationListener, AsyncResponse, GoogleApiClient.OnConnectionFailedListener {
    public static Location location;
    static String n[];
    static Double m[];
    public int rejectStatus = 0;
    public int driverRank = -1;
    public List<Polyline> polyLines = new ArrayList<Polyline>();
    public List<Route> routes = new ArrayList<>();
    protected Marker driverMarker;
    protected GoogleMap mMap;
    ExecutorService executor = Executors.newFixedThreadPool(8);
    String vehicleType = "";
    String provider;
    View bottomsheet, driversheet;
    View coordinatorLayoutView;
    ArrayList<String> name;
    ArrayList<Double> metars;
    Double getDriverdestLat, getDriverdestLong, getDrivercustLat, getDrivercustLong;
    int getDriverPrice;
    String getDriverType;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager locationManager;
    private Marker destinationMarker;
    private Marker searchMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapLayout);
        mapFragment.getMapAsync(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        provider = locationManager.getBestProvider(new Criteria(), false);
        location = locationManager.getLastKnownLocation(provider);
        if (location == null) {
            Log.i("Last Known Location", "Unsuccessful");
        } else {
            Log.i("Last Known Location", "Successful");
        }
        autoCompleteListener();
        coordinatorLayoutView = findViewById(R.id.snackbarPosition);
        Snackbar.make(coordinatorLayoutView, "Choose a destination.", Snackbar.LENGTH_INDEFINITE)
                .setAction("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                })
                .show();
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
    }

    public void openBottomSheet() {
        bottomsheet = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        ImageButton accept = (ImageButton) bottomsheet.findViewById(R.id.accept);
        ImageButton cancel = (ImageButton) bottomsheet.findViewById(R.id.cancel);
        TextView price = (TextView) bottomsheet.findViewById(R.id.price);
        TextView duration = (TextView) bottomsheet.findViewById(R.id.duration);
        TextView distance = (TextView) bottomsheet.findViewById(R.id.distance);
        Log.wtf("Route length", String.valueOf(routes.size()));
        duration.setText(routes.get(routes.size() - 1).durationText);
        distance.setText(routes.get(routes.size() - 1).distanceText);
        price.setText(" Choose vehicle type.");
        final Dialog mBottomSheetDialog = new Dialog(Rider.this, R.style.MaterialDialogSheet);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateRoute();
                if (vehicleType.equalsIgnoreCase("")) {
                    Toast.makeText(Rider.this, "Please choose a vehicle type.", Toast.LENGTH_LONG).show();
                } else {
                    final String[] params = {vehicleType,
                            String.valueOf(location.getLatitude()),
                            String.valueOf(location.getLongitude()),
                            String.valueOf(destinationMarker.getPosition().latitude),
                            String.valueOf(destinationMarker.getPosition().longitude),
                            String.valueOf(getDriverPrice)};
                    $.ajax(new AjaxOptions().url("http://www.lymbo.esy.es/tst.php")
                            .type("POST")
                            .data("{\"type\":\"" + vehicleType + "\"" + ",\"latitude\":\"" + location.getLatitude() + "\"" + ",\"longitude\":\"" + location.getLongitude() + "\"}")
                            .context(Rider.this)
                            .async(false)
                            .success(new Function() {
                                @Override
                                public void invoke($ droidQuery, Object... objects) {
                                    System.out.println("success");
                                    if ((objects[0]).toString().equalsIgnoreCase("No drivers")) {
                                        Toast.makeText(Rider.this, "No drivers close :(", Toast.LENGTH_LONG).show();
                                    } else {
                                        try {
                                            System.out.println("getArray");
                                            JSONObject jsonObject;
                                            System.out.println(objects[0].toString());
                                            JSONArray jsonArray = new JSONArray(objects[0].toString());
                                            name = new ArrayList<>();
                                            metars = new ArrayList<>();
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                jsonObject = (JSONObject) jsonArray.get(i);
                                                name.add(jsonObject.getString("Dname"));
                                                metars.add(jsonObject.getDouble("metar"));
                                            }
                                            Rider.n = new String[name.size()];
                                            Rider.m = new Double[name.size()];
                                            for (int x = 0; x < name.size(); x++) {
                                                n[x] = name.get(x);
                                                m[x] = metars.get(x);
                                                Log.i("Arrays", "Driver name is : " + name.get(x) + " And distance from location is : " + String.valueOf(metars.get(x)));
                                            }
                                            System.out.println("insertion");
                                            Double[] arr = m;
                                            String[] ar = n;
                                            for (int i = 1; i < arr.length; i++) {
                                                Double valueToSort = arr[i];
                                                String sValue = ar[i];
                                                int j = i;
                                                while (j > 0 && arr[j - 1] > valueToSort) {
                                                    arr[j] = arr[j - 1];
                                                    ar[j] = ar[j - 1];
                                                    j--;
                                                }
                                                arr[j] = valueToSort;
                                                ar[j] = sValue;
                                            }
                                            Rider.n = ar;
                                            Rider.m = arr;
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            })
                            .error(new Function() {
                                @Override
                                public void invoke($ $, Object... args) {
                                    System.out.println("failed");
                                }
                            })
                            .complete(new Function() {
                                @Override
                                public void invoke($ $, Object... objects) {
                                    mBottomSheetDialog.dismiss();
                                    System.out.println("Inside handler");
                                    new GetDriversLocation().execute(params);
                                }
                            }));
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unanimateRoute();
                mBottomSheetDialog.cancel();
            }
        });
        mBottomSheetDialog.setContentView(bottomsheet);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.setCanceledOnTouchOutside(false);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();
    }

    protected void unanimateRoute() {
        for (int i = 0; i < polyLines.size(); i++) {
            polyLines.get(i).remove();
        }
        polyLines.clear();
        destinationMarker.remove();
    }

    protected void animateRoute() {
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
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
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds.build(), 100);
        mMap.animateCamera(cameraUpdate);
    }

    public void type(View view) {
        String type = "", textprice;
        ImageButton car = (ImageButton) bottomsheet.findViewById(R.id.car);
        ImageButton tuktuk = (ImageButton) bottomsheet.findViewById(R.id.tuktuk);
        ImageButton amjad = (ImageButton) bottomsheet.findViewById(R.id.amjad);
        TextView text = (TextView) bottomsheet.findViewById(R.id.price);
        if (car != null && tuktuk != null && amjad != null) {
            switch (view.getId()) {
                case R.id.car:
                    //Inform the user te button1 has been clicked
                    car.setImageResource(R.drawable.carsheet);
                    amjad.setImageResource(R.drawable.amjadchoice);
                    tuktuk.setImageResource(R.drawable.tuktukchoice);
                    type = "car";
                    textprice = String.valueOf(getPrice(type, routes.get(routes.size() - 1).distance));
                    getDriverPrice = Integer.parseInt(textprice);
                    text.setText("The price is: " + textprice + "SDG");
                    break;

                case R.id.tuktuk:
                    //Inform the user the button1 has been clicked
                    car.setImageResource(R.drawable.carchoice);
                    amjad.setImageResource(R.drawable.amjadchoice);
                    tuktuk.setImageResource(R.drawable.tuktuksheet);
                    type = "tuktuk";
                    textprice = String.valueOf(getPrice(type, routes.get(routes.size() - 1).distance));
                    getDriverPrice = Integer.parseInt(textprice);
                    text.setText("The price is: " + textprice + " SDG");
                    break;

                case R.id.amjad:
                    //Inform the user the button1 has been clicked
                    car.setImageResource(R.drawable.carchoice);
                    amjad.setImageResource(R.drawable.amjadsheet);
                    tuktuk.setImageResource(R.drawable.tuktukchoice);
                    type = "amjad";
                    textprice = String.valueOf(getPrice(type, routes.get(routes.size() - 1).distance));
                    getDriverPrice = Integer.parseInt(textprice);
                    text.setText("The price is: " + textprice + "SDG");
                    break;
            }
        }
        vehicleType = type;
        Log.i("DistanceVal", String.valueOf(routes.get(routes.size() - 1).distance));
    }

    public int getPrice(String type, int distance) {
        //The method takes the type or vehicle and the distance which should be provided by Mohanned later
        int GenehperKilo = 0, price = 0;

        //Genehperkilo int is  the price that the vehicle takes per kilometer, could be adjusted to meters if you guys want to
        Calendar calendar = Calendar.getInstance();
        //Calender is the class that gets the time from the machine, could be adjusted later if we could get time from internet
        TextView text = (TextView) bottomsheet.findViewById(R.id.price);
        int current_hour = calendar.get(Calendar.HOUR_OF_DAY);
        //Calender.HOUR_OF_DAY gets the time in the 24 hour format
        if (type.equalsIgnoreCase("car")) {
            GenehperKilo = 20;
            // Each vehicle has an initial Genehperkilo value that can be increased or decreased depending on the time
            //In these if clauses are the times specified for me by Omran, at each time the Genehperkilo for a vehicle changes depending on the trafic state
            // time and how Genehperkilo increase or decrease can be adjusted later to things we see fit

            if (current_hour >= 0 && current_hour < 13) {

                GenehperKilo += 2;
            } else if (current_hour >= 13 && current_hour < 19) {
                GenehperKilo += 4;
            } else if (current_hour >= 19 && current_hour <= 23) {
                GenehperKilo += 6;
            }
        } else if (type.equalsIgnoreCase("tuktuk")) {
            GenehperKilo = 10;
            if (current_hour >= 0 && current_hour < 18) {
                GenehperKilo += 2;
            } else if (current_hour >= 18 && current_hour < 23) {
                GenehperKilo += 6;
            }
        } else if (type.equalsIgnoreCase("amjad")) {
            //          text.setText("amjad clicked!!");
            GenehperKilo = 25;
            if (current_hour >= 0 && current_hour < 13) {
                GenehperKilo += 2;
            } else if (current_hour >= 13 && current_hour < 19) {
                GenehperKilo += 4;
            } else if (current_hour >= 19 && current_hour <= 23) {
                GenehperKilo += 6;
            }
        }
        price = GenehperKilo * distance;
        // In the end the total price gets generated by multipying the Genehperkilo and the distance specified for us by Mohanned
        return price / 1000;
        // And don't forget to embrace my amazing programming skills and please like and subscribe!!!!!
    }

    private void autoCompleteListener() {
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(final Place place) {
                Log.i("Place", "Place: " + place.getName());
                Log.i("place location", String.valueOf(place.getLatLng()));
                setSearchMarker(place);
            }

            @Override
            public void onError(Status status) {
                Log.i("Place Error", "An error occurred: " + status);
            }
        });
    }

    private BitmapDescriptor imageType(List<Integer> placeTypes) {
        BitmapDescriptor icon;
        switch (placeTypes.get(0)) {
            case 6:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.atmpin);
                break;
            case 7:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.bakerypin);
                break;
            case 8:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.bankpin);
                break;
            case 15:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.coffeeshoppin);
                break;
            case 30:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.doctorpin);
                break;
            case 33:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.embassypin);
                break;
            case 36:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.firestationpin);
                break;
            case 41:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.gasstationpin);
                break;
            case 44:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.gympin);
                break;
            case 50:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.doctorpin);
                break;
            case 2:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.airportpin);
                break;
            case 66:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.museumpin);
                break;
            case 72:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.pharmacypin);
                break;
            case 76:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.policepin);
                break;
            case 79:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.restaurantpin);
                break;
            case 1020:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.roadpin);
                break;
            case 84:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.shoplocation);
                break;
            case 94:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.universitypin);
                break;
            default:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.roadpin);
                break;
        }
        return icon;
    }

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
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                destinationMarker(latLng);
            }
        });
    }

    protected void setSearchMarker(final Place place) {
        if (searchMarker != null) {
            searchMarker.remove();
            if (destinationMarker != null) {
                destinationMarker.remove();
            }
        }
        searchMarker = mMap.addMarker(new MarkerOptions()
                .title("Search Result")
                .icon(imageType(place.getPlaceTypes()))
                .position(place.getLatLng()));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
        Snackbar.make(coordinatorLayoutView, "Is this your destination?", Snackbar.LENGTH_INDEFINITE)
                .setAction("Yes!", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String[] params = {String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), String.valueOf(place.getLatLng().latitude), String.valueOf(place.getLatLng().longitude)};
                        new GetRoute().execute(params);
                        if (searchMarker != null) {
                            searchMarker.remove();
                        }
                        if (destinationMarker == null) {
                            destinationMarker = mMap.addMarker(new MarkerOptions()
                                    .title("Destination")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.userdestination))
                                    .position(place.getLatLng()));
                        } else {
                            destinationMarker.remove();
                            destinationMarker = mMap.addMarker(new MarkerOptions()
                                    .title("Destination")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.userdestination))
                                    .draggable(true)
                                    .position(place.getLatLng()));
                        }
                    }
                })
                .show();
    }

    protected void destinationMarker(final LatLng latLng) {
        if (searchMarker != null) {
            searchMarker.remove();
        }
        if (destinationMarker == null) {
            destinationMarker = mMap.addMarker(new MarkerOptions()
                    .title("Destination")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.userdestination))
                    .position(latLng));
        } else {
            destinationMarker.remove();
            destinationMarker = mMap.addMarker(new MarkerOptions()
                    .title("Destination")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.userdestination))
                    .draggable(true)
                    .position(latLng));
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        Snackbar.make(coordinatorLayoutView, "Is this your destination?", Snackbar.LENGTH_INDEFINITE)
                .setAction("Yes!", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String[] params = {String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), String.valueOf(latLng.latitude), String.valueOf(latLng.longitude)};
                        new GetRoute().execute(params);
                    }
                })
                .show();
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
        /*if (destinationMarker != null) {
            destin.setLongitude(destinationMarker.getPosition().longitude);
            destin.setLatitude(destinationMarker.getPosition().latitude);
            if (location.distanceTo(destin) < 500) {
                if (driverMarker != null) {
                    driverMarker.remove();
                    unanimateRoute();
                }
            }
        }*/
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
        Log.i("OnResume", "Hey it's me");
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

    @Override
    public void processFinish(String s) {
    }

    public void onConnected(@Nullable Bundle bundle) {
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
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            location.setLatitude(mLastLocation.getLatitude());
            location.setLongitude(mLastLocation.getLongitude());
            onMapReady(mMap);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(Rider.this, "Connection to places database failed.", Toast.LENGTH_SHORT).show();
    }

    protected void driverMarkerTimer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new driverMarker().execute();
            }
        }, 5000);
    }

    public class GetRoute extends AsyncTask<String, String, String> {
        private ProgressDialog dialog = new ProgressDialog(Rider.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Downloading route, please wait.");
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
            return null;
        }

        @Override
        protected void onPostExecute(String data) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            openBottomSheet();
        }
    }

    public class GetLocations extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            return new Route().synchronousCall("http://lymbo.esy.es/locationsarray.php", "");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject;
                JSONArray jsonArray = new JSONArray(s);
                ArrayList<LatLng> latLng = new ArrayList<LatLng>();
                ArrayList<Integer> carTypes = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = (JSONObject) jsonArray.get(i);
                    latLng.add(new LatLng(jsonObject.getDouble("latitude"), jsonObject.getDouble("longitude")));
                    switch (jsonObject.getString("type")) {
                        case "car":
                            carTypes.add(1);
                            break;
                        case "amjad":
                            carTypes.add(2);
                            break;
                        case "tuktuk":
                            carTypes.add(3);
                            break;
                    }
                    Log.i("hala", String.valueOf(latLng.get(i).latitude));
                    Log.i("hala", String.valueOf(latLng.get(i).longitude));
                }
                for (int i = 0; i < latLng.size(); i++) {
                    switch (carTypes.get(i)) {
                        case 1:
                            mMap.addMarker(new MarkerOptions()
                                    .position(latLng.get(i))
                                    .draggable(false)
                                    .title("Driver" + i)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.carmarker)));
                            break;
                        case 2:
                            mMap.addMarker(new MarkerOptions()
                                    .position(latLng.get(i))
                                    .draggable(false)
                                    .title("Driver" + i)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.amjadmarker)));
                            break;
                        case 3:
                            mMap.addMarker(new MarkerOptions()
                                    .position(latLng.get(i))
                                    .draggable(false)
                                    .title("Driver" + i)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.tuktukmarker)));
                            break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("Locationsaha", s);
        }
    }

    public class GetDriversLocation extends AsyncTask<String, String, String> {
        String[] test;
        private ProgressDialog dialog = new ProgressDialog(Rider.this);

        @Override
        protected void onPreExecute() {
            //Initialize progress dialog.
            super.onPreExecute();
            dialog.setTitle("Fetching Driver");
            dialog.setMessage("Rest back and chill while we fetch your ride.");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            int answer = -1;
            try {
                //repeat controls the loop
                boolean repeat = true;
                do {
                    //Notify the next driver, and wait for response.
                    final Future<Integer> future = executor.submit(new NotifyNextDriver());
                    //initial rejectStatus for the current driver is 0.
                    rejectStatus = 0;
                    Log.i("rejectStatus", "RejectStatus in PostExecute is : " + String.valueOf(rejectStatus));
                    int result;
                    getDriverType = strings[0];
                    getDrivercustLat = Double.valueOf(strings[1]);
                    getDrivercustLong = Double.valueOf(strings[2]);
                    getDriverdestLat = Double.valueOf(strings[3]);
                    getDriverdestLong = Double.valueOf(strings[4]);
                    driverRank++;
                    //Check if the Driver names array is done.
                    if(n.length < 0){
                        Thread.sleep(5000);
                    } else {
                        if (driverRank < n.length) {
                            result = future.get();
                            Log.i("onPostExecute", "result = " + String.valueOf(result));
                            answer = result;
                            if (result == 2) {
                                //An error happened, do not repeat the loop.
                                driverRank = -1;
                                repeat = false;
                            } else if (result == 1) {
                                //Driver accepted the request, do not repeat the loop.
                                repeat = false;
                            } else if (result == 0) {
                                //Driver rejected request, or he already exists in table. notify next driver and repeat the loop.
                                Log.i("Postexecute", "going to next driver");
                                repeat = true;
                            }
                        } else {
                            //if result is outside expected parameters, don't repeat loop.
                            Log.i("onPostExecute", "Names array have finished.");
                            answer = 3;
                            driverRank = -1;
                            repeat = false;
                        }
                    }
                } while (repeat);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return String.valueOf(answer);
        }

        @Override
        protected void onPostExecute(String data) {
            //Close dialog after results.
            if (dialog.isShowing()) {
                dialog.cancel();
            }
            if (data.contains("2")) {
                //An error happened, do not repeat the loop.
                driverRank = -1;
                Toast.makeText(Rider.this, "An error happened", Toast.LENGTH_LONG).show();
            } else if (data.contains("1")) {
                //Driver accepted the request, do not repeat the loop.
                Toast.makeText(Rider.this, "Driver Accepted", Toast.LENGTH_LONG).show();
                new DriverAccept().execute();
            } else if (data.contains("0")) {
                //Driver rejected request, or he already exists in table. notify next driver and repeat the loop.
                Log.i("Postexecute", "going to next driver");
            } else if (data.contains("3")) {
                //if result is outside expected parameters, don't repeat loop.
                Log.i("onPostExecute", "Names array have finished.");
                Toast.makeText(Rider.this, "No driver accepted.", Toast.LENGTH_LONG).show();
                unanimateRoute();
                driverRank = -1;
            }
        }
    }

    public class NotifyNextDriver implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            final int[] result = new int[1];
            final int rank = driverRank;
            final boolean[] repeat = {true};
            //Insert request to RequestTable.
            String response = new Route().synchronousCall("http://www.lymbo.esy.es/insertRequest.php",
                    "{\"Dname\":\"" + n[driverRank] + "\""
                            + ",\"type\":\"" + getDriverType + "\""
                            + ",\"price\":\"" + getDriverPrice + "\""
                            + ",\"destLatitude\":\"" + getDriverdestLat + "\""
                            + ",\"destLongitude\":\"" + getDriverdestLong + "\""
                            + ",\"custLatitude\":\"" + getDrivercustLat + "\""
                            + ",\"custLongitude\":\"" + getDrivercustLong + "\"}");
            if (response.equals("Failure to connect.")) {
                Log.i("NotifyDriver", "Failure to connect");
                repeat[0] = false;
                result[0] = 2;
            } else {
                Log.i("Object Notify", response);
                if (response.contains("Already exists")) {
                    repeat[0] = false;
                    result[0] = 0;
                }
                while (repeat[0]) {
                    //Call timer and wait for respond after a delay.
                    timer();
                    Log.i("rejectStatus", "RejectStatus in notifyDriver run is : " + String.valueOf(rejectStatus));
                    if (rejectStatus != 0) {
                        //Get out of loop when rejectStatus has changed.
                        repeat[0] = false;
                    } else {
                        Thread.sleep(7000);
                    }
                }
                Log.i("rejectStatus", "RejectStatus in notifyDriver after loop is : " + String.valueOf(rejectStatus));
                if (rejectStatus == 1) {
                    //Driver has rejected the request.
                    result[0] = 0;
                } else if (rejectStatus == 2) {
                    //Driver has accepted the request.
                    result[0] = 1;
                } else if (rejectStatus == 3) {
                    //An error happened.
                    Log.i("timer", "Timer error");
                    result[0] = 2;
                }
            }
            Log.i("result", "Result in notify driver before returning is : " + String.valueOf(result[0]));
            return result[0];
        }

        protected void timer() {
            String response = new Route().synchronousCall("http://www.lymbo.esy.es/checkRequestStatus.php", "{\"Dname\":\"" + n[driverRank] + "\"}");
            if (response.equals("Failure to connect.")) {
                Log.i("php", "Couldn't find php.");
            } else {
                Log.i("Object Timer", response);
                if (response.contains("0")) {
                    Log.i("timer", "Request hasn't been responded to.");
                } else if (response.contains("1")) {
                    rejectStatus = 1;
                    Log.i("timer", "Request has been rejected.");
                } else if (response.contains("2")) {
                    rejectStatus = 2;
                    Log.i("timer", "Request has been accepted");
                } else {
                    Log.i("Object Timer", response);
                    Log.i("php", "Error in SQL.");
                    rejectStatus = 3;
                }
            }
            Log.i("rejectStatus", "RejectStatus in Timer is : " + String.valueOf(rejectStatus));
            Log.i("Timer", "I was in timer method.");
        }
    }

    public class DriverAccept extends AsyncTask<String, String, String> {
        private ProgressDialog dialog = new ProgressDialog(Rider.this);

        @Override
        protected void onPreExecute() {
            //Initialize progress dialog.
            super.onPreExecute();
            dialog.setTitle("Fetching driver data");
            dialog.setMessage("Driver accepted, showing driver location.");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String response = new Route().synchronousCall("http://www.lymbo.esy.es/driverAccept.php", "{\"Dname\":\"" + n[driverRank] + "\"}");
            Log.i("DriverAccept", response);
            while (response.contains("No location")) {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                response = new Route().asynchronousCall("http://www.lymbo.esy.es/driverAccept.php", "{\"Dname\":\"" + n[driverRank] + "\"}");
            }
            Log.i("DriverAccept", response);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (dialog.isShowing()) {
                dialog.cancel();
            }
            driverMarkerTimer();
        }
    }

    public class driverMarker extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            final String response = new Route().synchronousCall("http://www.lymbo.esy.es/driverAccept.php", "{\"Dname\":\"" + n[driverRank] + "\"}");
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                JSONArray responseJson = new JSONArray(response);
                driverMarker = mMap.addMarker(new MarkerOptions()
                        .title("Driver location")
                        .position(new LatLng(Double.valueOf(responseJson.getString(0)), Double.valueOf(responseJson.getString(1))))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.drivert)));
                LatLngBounds.Builder bounds = new LatLngBounds.Builder();
                bounds.include(destinationMarker.getPosition());
                bounds.include(new LatLng(location.getLatitude(), location.getLongitude()));
                bounds.include(driverMarker.getPosition());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds.build(), 100);
                mMap.animateCamera(cameraUpdate);
                Location destin = new Location(location);
                destin.setLatitude(driverMarker.getPosition().latitude);
                destin.setLongitude(driverMarker.getPosition().longitude);
                if (location.distanceTo(destin) < 500) {
                    driverMarker.remove();
                    bounds = new LatLngBounds.Builder();
                    bounds.include(destinationMarker.getPosition());
                    bounds.include(new LatLng(location.getLatitude(), location.getLongitude()));
                    cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds.build(), 100);
                    mMap.animateCamera(cameraUpdate);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            driverMarkerTimer();
        }
    }
}