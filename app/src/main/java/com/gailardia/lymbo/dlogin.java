package com.gailardia.lymbo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import java.util.HashMap;

public class dlogin extends AppCompatActivity implements AsyncResponse {
    //sssss
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
        String U=user.getText().toString();
        String P=pass.getText().toString();
        map.put("Dname",U);
        map.put("Dpassword",P);

        PostResponseAsyncTask task=new PostResponseAsyncTask(this,map);
        task.execute("http://lymbo.esy.es/signin.php");

    }
    public void finishlogin(){
        finish();
    }

    @Override
    public void processFinish(String s) {
        Toast.makeText(this,s,Toast.LENGTH_LONG).show();
    }
}

