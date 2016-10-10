package com.gailardia.lymbo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import java.util.HashMap;

public class dlogin extends AppCompatActivity implements AsyncResponse {
    String U;
    String P;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dlogin);
    }

    public void backToChoices(View view) {
        Intent intent = new Intent(this, choices.class);
        startActivity(intent);
    }

    public void Dsignup(View view) {
        Intent intent = new Intent(this, dsignup.class);
        startActivity(intent);
    }

    public void signin(View view) {
        HashMap map=new HashMap();
        EditText user=(EditText)findViewById(R.id.loginUser);
        EditText pass=(EditText)findViewById(R.id.loginPass);
        U=user.getText().toString();
        P=pass.getText().toString();
        map.put("Dname",U);
        map.put("Dpassword",P);
        PostResponseAsyncTask task = new PostResponseAsyncTask(this,map);
        task.execute("http://lymbo.esy.es/signin.php");
    }
    public void finishlogin(){
        finish();
    }

    @Override
    public void processFinish(String s) {
        Toast.makeText(this,s,Toast.LENGTH_LONG).show();
        if(s.equalsIgnoreCase("success")){
            SharedPreferences shared = this.getSharedPreferences("com.gailardia.lymbo", Context.MODE_PRIVATE);
            shared.edit().putString("username", U).apply();
            shared.edit().putString("password", P).apply();
            shared.edit().putBoolean("signed", true).apply();
            Intent intent = new Intent(this, Driver.class);
            startActivity(intent);
        }
    }
}

