package com.gailardia.lymbo;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class choices extends AppCompatActivity {
    private static final int REQUEST_PERMISSION = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choices);
        if(Build.VERSION.SDK_INT >= 23){
            requestPermission();
        }
        fade();
    }

    private void requestPermission() {
        String[] permissionLocation = new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION};
        ActivityCompat.requestPermissions(this,
                permissionLocation,
                REQUEST_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Yay", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Boohoo", Toast.LENGTH_SHORT).show();
                }
                return;
            }
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
        /*SharedPreferences shared = this.getSharedPreferences("com.gailardia.lymbo", Context.MODE_PRIVATE);
        if(shared.getBoolean("signed", false)){
            Intent intent = new Intent(this, DriverActivity.class);
            startActivity(intent);

        } else {
            Intent intent = new Intent(this, dlogin.class);
            startActivity(intent);
        }*/
        Intent intent = new Intent(this, dlogin.class);
        startActivity(intent);
    }
    private Boolean exit = false;
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

    public void openMap(View view){
        Intent intent = new Intent(this, DriverActivity.class);
        startActivity(intent);
    }

}
