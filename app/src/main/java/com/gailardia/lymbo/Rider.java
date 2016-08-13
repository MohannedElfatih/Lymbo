package com.gailardia.lymbo;

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
import android.os.AsyncTask;
import android.support.annotation.NonNull;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class Rider extends AppCompatActivity implements OnMapReadyCallback, LocationListener, AsyncResponse, GoogleApiClient.OnConnectionFailedListener {

    public List<Polyline> polyLines = new ArrayList<Polyline>();
    public List<Route> routes = new ArrayList<>();
    Location location;
    String provider;
    View coordinatorLayoutView;
    View bottomsheet;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager locationManager;
    private Marker destinationMarker;
    private Marker searchMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider);
        coordinatorLayoutView = findViewById(R.id.snackbarPosition);
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
        //createFloatingAction();
        autoCompleteListener();
        coordinatorLayoutView = findViewById(R.id.snackbarPosition);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
    }

    public void openBottomSheet() {
        bottomsheet = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        ImageButton car = (ImageButton) findViewById(R.id.car);
        ImageButton tuktuk = (ImageButton) findViewById(R.id.tuktuk);
        ImageView amjad = (ImageView) findViewById(R.id.amjad);
        ImageButton accept = (ImageButton) bottomsheet.findViewById(R.id.accept);
        ImageButton cancel = (ImageButton) bottomsheet.findViewById(R.id.cancel);
        TextView price = (TextView) bottomsheet.findViewById(R.id.price);
        TextView duration = (TextView) bottomsheet.findViewById(R.id.duration);
        TextView distance = (TextView) bottomsheet.findViewById(R.id.distance);
        Log.wtf("ErrorsMan", String.valueOf(routes.size()));
        duration.setText(routes.get(routes.size() - 1).durationText);
        distance.setText(routes.get(routes.size() - 1).distanceText);
        price.setText("Price is 100 SDG");
        final Dialog mBottomSheetDialog = new Dialog(Rider.this, R.style.MaterialDialogSheet);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateRoute();
                mBottomSheetDialog.dismiss();
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
        String type = "";
        ImageButton car = (ImageButton) bottomsheet.findViewById(R.id.car);
        ImageButton tuktuk = (ImageButton) bottomsheet.findViewById(R.id.tuktuk);
        ImageButton amjad = (ImageButton) bottomsheet.findViewById(R.id.amjad);
        if (car != null && tuktuk != null && amjad != null) {
            switch (view.getId()) {

                case R.id.car:
                    //Inform the user te button1 has been clicked
                    car.setImageResource(R.drawable.redcar);
                    amjad.setImageResource(R.drawable.amjad);
                    tuktuk.setImageResource(R.drawable.tuktuk);
                    type = "car";
                    break;

                case R.id.tuktuk:
                    //Inform the user the button1 has been clicked
                    car.setImageResource(R.drawable.choicecar);
                    amjad.setImageResource(R.drawable.amjad);
                    tuktuk.setImageResource(R.drawable.redraksha);
                    type = "tuktuk";
                    break;

                case R.id.amjad:
                    //Inform the user the button1 has been clicked
                    car.setImageResource(R.drawable.choicecar);
                    amjad.setImageResource(R.drawable.redamjad);
                    tuktuk.setImageResource(R.drawable.tuktuk);
                    type = "amjad";
                    break;
            }
        }
    }

    private void createFloatingAction() {
        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageDrawable(getResources().getDrawable(R.drawable.amjad));

        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);

        ImageView itemIcon = new ImageView(this);
        itemIcon.setImageDrawable(getResources().getDrawable(R.drawable.car));
        SubActionButton button1 = itemBuilder.setContentView(itemIcon).build();
        ImageView itemIcon2 = new ImageView(this);
        itemIcon2.setImageDrawable(getResources().getDrawable(R.drawable.boy));
        SubActionButton button2 = itemBuilder.setContentView(itemIcon2).build();

        ImageView itemIcon3 = new ImageView(this);
        itemIcon3.setImageDrawable(getResources().getDrawable(R.drawable.search));
        SubActionButton button3 = itemBuilder.setContentView(itemIcon3).build();

        ImageView itemIcon4 = new ImageView(this);
        itemIcon4.setImageDrawable(getResources().getDrawable(R.drawable.boy));
        SubActionButton button4 = itemBuilder.setContentView(itemIcon4).build();


        final FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(button1)
                .addSubActionView(button2)
                .attachTo(actionButton)
                .build();

        final FloatingActionMenu actionMenu2 = new FloatingActionMenu.Builder(this)
                .addSubActionView(button3)
                .addSubActionView(button4)
                .attachTo(button1)
                .build();

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionMenu.toggle(true);

                if (!actionMenu.isOpen() && actionMenu2.isOpen()) {
                    actionMenu.toggle(true);
                    actionMenu2.toggle(true);
                }
            }
        });
        autoCompleteListener();
    }

    private void autoCompleteListener() {
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(final Place place) {
                Log.i("Place", "Place: " + place.getName());
                Log.i("place location", String.valueOf(place.getLatLng()));
                if (searchMarker != null) {
                    searchMarker.remove();
                }
                searchMarker = mMap.addMarker(new MarkerOptions()
                        .position(place.getLatLng())
                        .draggable(false)
                        .icon(imageType(place.getPlaceTypes())));
                destinationMarker(place.getLatLng());
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
                icon = BitmapDescriptorFactory.fromResource(R.drawable.firestation);
                break;
            case 41:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.gasstationpoint);
                break;
            case 44:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.gympin);
                break;
            case 50:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.doctorpin);
                break;
            case 2:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.markerairport);
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
                icon = BitmapDescriptorFactory.fromResource(R.drawable.atmpin);
                break;
        }
        return icon;
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        if (location != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
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
                        new getRoute().execute(params);
                    }
                })
                .show();
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(Rider.this, "Connection to places database failed.", Toast.LENGTH_SHORT).show();
    }

    public class getRoute extends AsyncTask<String, String, String> {
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
                StringBuffer buffer = new StringBuffer();
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while ((result = bufferedReader.readLine()) != null) {
                    buffer.append(result + "\n");
                }
                String data = buffer.toString();
                JSONObject jsonObject = new JSONObject(data);
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
            } catch (MalformedURLException e) {
                e.printStackTrace();
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

    public class getLocations extends AsyncTask<String, String, String> {

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
}
