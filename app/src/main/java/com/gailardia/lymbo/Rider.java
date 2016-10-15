package com.gailardia.lymbo;
<<<<<<< HEAD

=======
>>>>>>> refs/remotes/origin/Ali
import android.*;
import android.annotation.TargetApi;
import android.Manifest;
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
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kosalgeek.asynctask.AsyncResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
    protected boolean driverMarkerIsRunning = false;
    protected Handler handler = new Handler();
    protected Runnable runnable;
    static String n[];
    static Double m[];
    public int rejectStatus = 0;
    public int driverRank = -1;
    public List<Polyline> polyLines = new ArrayList<Polyline>();
    public List<Route> routes = new ArrayList<>();
    protected GetRoute getRoute;
    protected DriverMarker driverMarkerTimer;
    protected Future<Integer> future;
    protected Marker driverMarker;
    protected GoogleMap mMap;
    ExecutorService executor = Executors.newFixedThreadPool(8);
    String vehicleType = "";
    String provider;
    View bottomsheet, coordinatorLayoutView, callSheet;
    ArrayList<String> name;
    ArrayList<Double> metars;
    Double getDriverdestLat, getDriverdestLong, getDrivercustLat, getDrivercustLong;
    int getDriverPrice, change, getDriverPhone;
    String getDriverType, getDriverFirstName, getDriverLastName, report;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager locationManager;
    private Marker destinationMarker;
    private Marker searchMarker;
    private PopupWindow popup;
    private RelativeLayout rel;
    private RadioGroup radio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapLayout);
        mapFragment.getMapAsync(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .addApi(AppIndex.API).build();
    }

    public void openpop() {
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.85);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);
        rel = (RelativeLayout) findViewById(R.id.relative);
        View container = getLayoutInflater().inflate(R.layout.pop, null);
        popup = new PopupWindow(container, width, height, true);
        popup.showAtLocation(rel, Gravity.CENTER, 0, 0);
        popup.setOutsideTouchable(false);
        Button close = (Button) container.findViewById(R.id.closepop);
        Button submit = (Button) container.findViewById(R.id.submit);
        radio = (RadioGroup) container.findViewById(R.id.rad);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (radio.getCheckedRadioButtonId()) {
                    case R.id.priceR:
                        report = "priceR";
                        break;
                    case R.id.far:
                        report = "farR";
                        break;
                    case R.id.personal:
                        report = "personalR";
                        break;
                    case R.id.rideR:
                        report = "driverR";
                        break;
                }
                $.ajax(new AjaxOptions().url("http://www.lymbo.esy.es/report.php")
                        .type("POST")
                        .data("{\"type\":\"" + report + "\"}")
                        .success(new Function() {
                            @Override
                            public void invoke($ droidQuery, Object... objects) {
                                Toast.makeText(getApplicationContext(), "Thank you", Toast.LENGTH_LONG).show();
                            }
                        })
                        .error(new Function() {
                            @Override
                            public void invoke($ $, Object... args) {
                            }
                        }));
                popup.dismiss();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });

    }

    public int hours() {
        String url = "http://www.timeapi.org/utc/now";
        final int[] hour = new int[1];
        new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        try {


                            String hr = String.valueOf(result.charAt(11)) + String.valueOf(result.charAt(12));
                            hour[0] = Integer.parseInt(hr);
                            Toast.makeText(Rider.this, String.valueOf(hour[0]), Toast.LENGTH_LONG).show();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        return hour[0];
    }

    public void openBottomSheet() {
        change = 0;
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
        mBottomSheetDialog.setCanceledOnTouchOutside(false);
        mBottomSheetDialog.onBackPressed();
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
                                        unanimateRoute();
                                        destinationMarker.remove();
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
                                    mBottomSheetDialog.cancel();
                                    if (n != null) {
                                        new GetDriversLocation().execute(params);
                                    }
                                }
                            }));
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openpop();
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
        final ImageButton accept = (ImageButton) bottomsheet.findViewById(R.id.accept);
        Animation outAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadeout);
        final Animation inAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
        accept.setTag(R.drawable.notchecked);
        outAnimation.setAnimationListener(new Animation.AnimationListener() {

            // Other callback methods omitted for clarity.

            @Override
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {

                // Modify the resource of the ImageButton
                accept.setImageResource(R.drawable.checked);
                // Create the new Animation to apply to the ImageButton.
                accept.startAnimation(inAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                animation.cancel();
            }
        });
        if (car != null && tuktuk != null && amjad != null) {
            if (change == 0) {
                System.out.println(change);
                accept.startAnimation(outAnimation);
                change = 1;
            }
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
        boolean less = false;
        int current_hour = hours();
        if (type.equalsIgnoreCase("car")) {
            if (distance < 3000) {
                price = 25;
                less = true;
            } else {
                if (current_hour >= 0 && current_hour < 13) {
                    GenehperKilo = 8;
                } else if (current_hour >= 13 && current_hour < 19) {
                    GenehperKilo = 10;
                } else if (current_hour >= 19 && current_hour <= 23) {
                    GenehperKilo = 9;
                }
            }
        } else if (type.equalsIgnoreCase("tuktuk")) {
            if (distance < 1000) {
                price = 5;
                less = true;
            } else {
                if (current_hour >= 0 && current_hour < 18) {
                    GenehperKilo = 7;
                } else if (current_hour >= 18 && current_hour < 23) {
                    GenehperKilo = 6;
                }
            }
        } else if (type.equalsIgnoreCase("amjad")) {
            if (distance < 3000) {
                price = 25;
                less = true;
            } else {
                if (current_hour >= 0 && current_hour < 13) {
                    GenehperKilo = 5;
                } else if (current_hour >= 13 && current_hour < 19) {
                    GenehperKilo = 6;
                } else if (current_hour >= 19 && current_hour <= 23) {
                    GenehperKilo = 6;
                }
            }
        }
        if (!less) {
            price = GenehperKilo * (distance / 1000);
        }
        // In the end the total price gets generated by multipying the Genehperkilo and the distance specified for us by Mohanned
        return price;
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                        getRoute = (GetRoute) new GetRoute().execute(params);
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
                        getRoute = (GetRoute) new GetRoute().execute(params);
                    }
                })
                .show();
    }

    @Override
    public void onLocationChanged(Location location) {
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
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        provider = locationManager.getBestProvider(new Criteria(), false);
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Rider Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient.connect();
        AppIndex.AppIndexApi.start(mGoogleApiClient, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(mGoogleApiClient, getIndexApiAction());
        mGoogleApiClient.disconnect();
    }

    public class GetRoute extends AsyncTask<String, String, String> {
        private ProgressDialog dialog = new ProgressDialog(Rider.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Downloading route, please wait.");
            dialog.setCancelable(true);
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
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

    public class GetDriversLocation extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(Rider.this);
            //Initialize progress dialog.
            super.onPreExecute();
            dialog.setTitle("Fetching Driver");
            dialog.setMessage("Rest back and chill while we fetch your ride.");
            dialog.setIndeterminate(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.onBackPressed();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(true);
                }
            });
            dialog.show();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (future != null) {
                future.cancel(true);
            }
            if (driverRank > -1) {
                new Route().asynchronousCall("http://www.lymbo.esy.es/cancelRequest.php", "{\"Dname\":\"" + n[driverRank] + "\"}");
                driverRank = -1;
                unanimateRoute();
                destinationMarker.remove();
            }
            dialog.cancel();
            dialog.dismiss();
        }

        @Override
        protected String doInBackground(String... strings) {
            int answer = -1;
            try {
                //repeat controls the loop
                boolean repeat = true;
                do {
                    //Notify the next driver, and wait for response.
                    future = executor.submit(new NotifyNextDriver());
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
                Log.i("onPostExecute", "Names array have finished");
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
            final boolean[] repeat = {true};
            Log.i("NotifyNextDriver", "Am in NotifyNextDriver");
            //Insert request to RequestTable.
            String response = new Route().synchronousCall("http://www.lymbo.esy.es/insertRequest.php",
                    "{\"Dname\":\"" + n[driverRank] + "\""
                            + ",\"type\":\"" + getDriverType + "\""
                            + ",\"price\":\"" + getDriverPrice + "\""
                            + ",\"destLatitude\":\"" + getDriverdestLat + "\""
                            + ",\"destLongitude\":\"" + getDriverdestLong + "\""
                            + ",\"custLatitude\":\"" + getDrivercustLat + "\""
                            + ",\"custLongitude\":\"" + getDrivercustLong + "\"}");
            Log.i("NotifyNextDriver", "After synchronous call");
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
            String driverInfo = new Route().synchronousCall("http://www.lymbo.esy.es/driverNumber.php", "{\"Dname\":\"" + n[driverRank] + "\"}");
            try {
                Log.i("DriverNumber", driverInfo);
                JSONArray responseJson = new JSONArray(driverInfo);
                getDriverFirstName = responseJson.getString(0);
                getDriverLastName = responseJson.getString(1);
                getDriverPhone = Integer.valueOf(responseJson.getInt(2));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String response = new Route().synchronousCall("http://www.lymbo.esy.es/driverAccept.php", "{\"Dname\":\"" + n[driverRank] + "\"}");
            Log.i("DriverAccept", response);
            while (response.contains("No location")) {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                response = new Route().synchronousCall("http://www.lymbo.esy.es/driverAccept.php", "{\"Dname\":\"" + n[driverRank] + "\"}");
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
            openCallSheet();
            driverMarkerTimer = (DriverMarker) new DriverMarker().execute();
        }

        public void openCallSheet() {
            callSheet = getLayoutInflater().inflate(R.layout.call_bottom_sheet, null);
            final Dialog callSheetDialog = new Dialog(Rider.this, R.style.MaterialDialogSheet);
            TextView call = (TextView) callSheet.findViewById(R.id.call);
            TextView callButton = (TextView) callSheet.findViewById(R.id.callButton);
            callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + String.valueOf(getDriverPhone)));
                    try {
                        startActivity(intent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Log.i("Call Activity", "Activity not found");
                    }
                    callSheetDialog.cancel();
                }
            });
            TextView cancel = (TextView) callSheet.findViewById(R.id.cancelRequest);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler.removeCallbacks(runnable);
                    driverMarkerTimer.cancel(true);
                    cancel(true);
                    callSheetDialog.cancel();
                    if (driverRank > -1) {
                        String response = new Route().asynchronousCall("http://www.lymbo.esy.es/cancelRequestOnAccept.php", "{\"Dname\":\"" + n[driverRank] + "\"}");
                        Log.i("OnCancelled", response);
                        driverRank = -1;
                        unanimateRoute();
                        if (driverMarker != null) {
                            driverMarker.remove();
                        }
                        if (destinationMarker != null) {
                            destinationMarker.remove();
                        }
                    }
                }
            });
            call.setText("Call " + getDriverFirstName + " " + getDriverLastName + "?");
            callSheetDialog.setContentView(callSheet);
            callSheetDialog.setCancelable(false);
            callSheetDialog.setCanceledOnTouchOutside(false);
            callSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            callSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
            callSheetDialog.show();
        }
    }

    public class DriverMarker extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            driverMarkerIsRunning = true;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            cancel(true);
            if (driverRank > -1) {
                String response = new Route().synchronousCall("http://www.lymbo.esy.es/cancelRequestOnAccept.php", "{\"Dname\":\"" + n[driverRank] + "\"}");
                Log.i("OnCancelled", response);
                driverRank = -1;
                unanimateRoute();
                if (driverMarker != null) {
                    driverMarker.remove();
                }
                if (destinationMarker != null) {
                    destinationMarker.remove();
                }
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            final String response = new Route().synchronousCall("http://www.lymbo.esy.es/driverAccept.php", "{\"Dname\":\"" + n[driverRank] + "\"}");
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                JSONArray responseJson = new JSONArray(response);
                Log.i("DriverMarker", response);
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
                /*if (location.distanceTo(destin) < 500) {
                    driverMarker.remove();
                    bounds = new LatLngBounds.Builder();
                    bounds.include(destinationMarker.getPosition());
                    bounds.include(new LatLng(location.getLatitude(), location.getLongitude()));
                    cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds.build(), 100);
                    mMap.animateCamera(cameraUpdate);
                }*/
            } catch (JSONException e) {
                e.printStackTrace();
            }
            driverMarkerIsRunning = false;
            runnable = new Runnable() {
                @Override
                public void run() {
                    driverMarkerTimer = (DriverMarker) new DriverMarker().execute();
                }
            };
            handler.postDelayed(runnable, 8000);
        }
    }
}