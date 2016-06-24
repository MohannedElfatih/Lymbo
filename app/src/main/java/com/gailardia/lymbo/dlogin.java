package com.gailardia.lymbo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class dlogin extends AppCompatActivity {
//sssss
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dlogin);
    }
    public void backToChoices(View view){
        Intent intent=new Intent(this,choices.class);
        startActivity(intent);
    }
    public void Dsignup(View view){
        Intent intent=new Intent(this,dsignup.class);
        startActivity(intent);
    }
    public void  finishlogin(){
        finish();
    }
}
