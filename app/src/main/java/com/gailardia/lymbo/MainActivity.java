package com.gailardia.lymbo;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Handler handler=new Handler();
    //ne
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_CANCELED) {

            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new CountDownTimer(2000,1000){
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
            nextactivity();
            }
        }.start();
    }
    public void nextactivity(){
        Intent intent=new Intent(this,choices.class);
        startActivity(intent);
        finish();

    }

}
