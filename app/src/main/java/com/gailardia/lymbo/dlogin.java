package com.gailardia.lymbo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import self.philbrown.droidQuery.$;
import self.philbrown.droidQuery.AjaxOptions;
import self.philbrown.droidQuery.Function;

public class dlogin extends AppCompatActivity implements AsyncResponse {
    String U;
    String P;
    private float priceR,priceD,farR,farD,driverR,carErrorD,personalR,personalD,sumR,sumD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dlogin);
    }

    public void Dsignup(View view) {
        Intent intent = new Intent(this, dsignup.class);
        startActivity(intent);
    }
    public void getDate() {
        final Intent intent = new Intent(this,admin.class);
        $.ajax
                (
                        new AjaxOptions().url("http://www.lymbo.esy.es/getReport.php")
                                .type("POST")
                                .success(new Function() {
                                             @Override
                                             public void invoke($ droidQuery, Object... objects) {
                                                 try {
                                                     JSONObject jsonObject;
                                                     JSONArray jsonArray = new JSONArray(objects[0].toString());
                                                     jsonObject = (JSONObject) jsonArray.get(0);
                                                     sumD= jsonObject.getInt("sumD");
                                                     sumR= jsonObject.getInt("sumR");
                                                     priceR=(jsonObject.getInt("priceR")/sumR)*100;
                                                     farR=(jsonObject.getInt("farR")/sumR)*100;
                                                     personalR=(jsonObject.getInt("personalR")/sumR)*100;
                                                     driverR=(jsonObject.getInt("driverR")/sumR)*100;
                                                     priceD=(jsonObject.getInt("priceD")/sumD)*100;
                                                     farD=(jsonObject.getInt("farD")/sumD)*100;
                                                     personalD=(jsonObject.getInt("personalD")/sumD)*100;
                                                     carErrorD=(jsonObject.getInt("carErrorD")/sumD)*100;
                                                     System.out.println("222222222222"+carErrorD);
                                                 } catch (JSONException e) {
                                                     e.printStackTrace();
                                                 }
                                             }
                                         }
                                ).complete(new Function() {
                            @Override
                            public void invoke($ $, Object... objects) {
                                SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                shared.edit().putFloat("priceR", priceR).apply();
                                shared.edit().putFloat("priceD", priceD).apply();
                                shared.edit().putFloat("farR", farR).apply();
                                shared.edit().putFloat("farD", farD).apply();
                                shared.edit().putFloat("personalR", personalR).apply();
                                shared.edit().putFloat("personalD", personalD).apply();
                                shared.edit().putFloat("driverR", driverR).apply();
                                shared.edit().putFloat("carErrorD", carErrorD).apply();
                                shared.edit().putFloat("sumD", sumD).apply();
                                shared.edit().putFloat("sumR", sumR).apply();
                                startActivity(intent);
                                finish();
                            }
                        })
                );
    }
    public void signin(View view) {
        HashMap map=new HashMap();
        EditText user=(EditText)findViewById(R.id.loginUser);
        EditText pass=(EditText)findViewById(R.id.loginPass);
        U=user.getText().toString();
        P=pass.getText().toString();
        if(U.equalsIgnoreCase("mohaned")&&P.equalsIgnoreCase("admin")){
            getDate();
        }
        else
        {
            map.put("Dname",U);
            map.put("Dpassword",P);
            PostResponseAsyncTask task = new PostResponseAsyncTask(this,map);
            task.execute("http://lymbo.esy.es/signin.php");
        }
    }

    @Override
    public void processFinish(String s) {
        Log.i("Response in Login", s);
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
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

