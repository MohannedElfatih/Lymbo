package com.gailardia.lymbo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class dsignup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dsignup);
    }
    public void backToLogin(View view){
        Intent intent=new Intent(this,dlogin.class);
        startActivity(intent);
    }
    public void Scndsignup(View view){
        Intent intent=new Intent(this,scndsignup.class);
        startActivity(intent);
    }
    public  void finishsignup(){
    finish();
    }
}
