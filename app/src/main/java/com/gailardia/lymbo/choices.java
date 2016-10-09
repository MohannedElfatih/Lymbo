package com.gailardia.lymbo;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class choices extends AppCompatActivity {
    protected static final int REQUEST_PERMISSION = 10;
    final String[] permissionLocation = new String[]{
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION};
    boolean check = false;
    View coordinatorLayoutView;
    private android.app.Activity act;
    private Boolean exit = false;
    private int counter;
    public dsignup signupM=new dsignup();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choices);
        coordinatorLayoutView = findViewById(R.id.snackbarPosition);
        printnum();
        if(Build.VERSION.SDK_INT >= 23){
            counter = 0;
            requestPermission(choices.this, coordinatorLayoutView);
        }
        fade();
    }

    protected boolean requestPermission(final android.app.Activity choices, View view) {
        act = choices;
        final String[] permissionLocation = new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(act, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            check = true;
            return check;
        } else {
            if (ContextCompat.checkSelfPermission(act,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(act,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Snackbar.make(view, "Please enable permission for application to work.", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Enable", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ActivityCompat.requestPermissions(act,
                                            permissionLocation,
                                            REQUEST_PERMISSION);
                                }
                            })
                            .show();
                } else {
                    ActivityCompat.requestPermissions(act,
                            permissionLocation,
                            REQUEST_PERMISSION);
                }
            }
            return check;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Yay", Toast.LENGTH_SHORT).show();
                    check = true;
                } else {
                    if (counter == 0) {
                        requestPermission(act, coordinatorLayoutView);
                        counter++;
                    } else {
                        Toast.makeText(this, "Permission denied, application will close", Toast.LENGTH_SHORT).show();
                        this.finishAffinity();
                    }
                }
            }
            return;
        }
    }


    public  void fade(){
        LinearLayout choice = (LinearLayout) findViewById(R.id.choice);
        final ImageView logo = (ImageView) findViewById(R.id.imageView2);

        choice.setTranslationY(-1000f);
        choice.setVisibility(View.VISIBLE);
        choice.animate().translationYBy(1000f).setDuration(1000);
        new CountDownTimer(1400,1400){
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                logo.animate().alpha(1f).setDuration(300);
            }
        }.start();
    }

    public void Dsignin(View view){
        Intent intent = new Intent(this, dlogin.class);
        if(isOnline()) {
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this,"No Internet access",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }

        return false;
    }

    public void openMap(View view) {
        if (isOnline()) {
            if (ContextCompat.checkSelfPermission(act,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(view, "Please enable permission for application to work.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Enable", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(act,
                                        permissionLocation,
                                        REQUEST_PERMISSION);
                            }
                        })
                        .show();
            } else {
                Intent intent = new Intent(this, Rider.class);
                startActivity(intent);
            }
        }
        else{
            Toast.makeText(this,"No Internet access",Toast.LENGTH_LONG).show();
        }
    }
    public void printnum()
    {
        AccountManager am = AccountManager.get(this);
        Account[] accounts = am.getAccounts();

        for (Account ac : accounts) {
            String acname = ac.name;
            String actype = ac.type;
            // Take your time to look at all available accounts
            System.out.println("Accounts : " + acname + ", " + actype);
            if(actype.equals("com.whatsapp")){
                String phoneNumber = ac.name;
                Toast.makeText(choices.this,phoneNumber,Toast.LENGTH_LONG).show();
            }
        }
    }
}
