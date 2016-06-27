package com.gailardia.lymbo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;

public class dsignup extends AppCompatActivity implements AsyncResponse {
    private final int SELECT_PHOTO = 1;
    private ImageView imageView;
    LinearLayout scnd;
    LinearLayout first;
    int carType=0;
    String Dname;
    String Dpassword1;
    String Dpassword2;
    String DIMEI;
    String type;
    String Dphone;
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dsignup);

        Button pickImage = (Button) findViewById(R.id.picked);
        pickImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });
    }
    public void Firstsignup(){
        LinearLayout scnd=(LinearLayout) findViewById(R.id.scndSignup);
        LinearLayout first=(LinearLayout) findViewById(R.id.firstSignup);
        scnd.setVisibility(View.INVISIBLE);
        first.setVisibility(View.VISIBLE);
    }
    public void Scndsignup(View view){
        LinearLayout scnd=(LinearLayout) findViewById(R.id.scndSignup);
        LinearLayout first=(LinearLayout) findViewById(R.id.firstSignup);
        scnd.setVisibility(View.VISIBLE);
        first.setVisibility(View.INVISIBLE);
    }
    public void goDlogin(View view){
        dsignup s=new dsignup();
        dlogin d=new dlogin();
        d.finishlogin();
        Intent intent=new Intent(this,dlogin.class);
        startActivity(intent);
        finish();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    try {
                        final Uri imageUri = imageReturnedIntent.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        imageView.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
        }
    }
    public void type(View view){
        ImageButton car=(ImageButton) findViewById(R.id.car);
        ImageButton tuktuk=(ImageButton) findViewById(R.id.tuktuk);
        ImageButton amjad=(ImageButton) findViewById(R.id.amjad);
        TextView car2=(TextView)findViewById(R.id.car2);
        TextView tuktuk2=(TextView)findViewById(R.id.tuktuk2);
        TextView amjad2=(TextView)findViewById(R.id.amjad2);
        if(car!=null && tuktuk!=null && car2!=null && tuktuk2!=null && amjad != null && amjad2 !=null) {
            switch (view.getId()) {

                case R.id.car:
                    //Inform the user the button1 has been clicked
                    car.setImageResource(R.drawable.redcar);
                    amjad.setImageResource(R.drawable.amjad);
                    tuktuk.setImageResource(R.drawable.tuktuk);
                    car2.setTextColor(Color.parseColor("#fa9684"));
                    tuktuk2.setTextColor(Color.parseColor("#d7d7d7"));
                    amjad2.setTextColor(Color.parseColor("#d7d7d7"));
                    type = "car";


                    break;
                case R.id.car2:
                    //Inform the user the button2 has been clicked
                    car.setImageResource(R.drawable.redcar);
                    amjad.setImageResource(R.drawable.amjad);
                    tuktuk.setImageResource(R.drawable.tuktuk);
                    car2.setTextColor(Color.parseColor("#fa9684"));
                    tuktuk2.setTextColor(Color.parseColor("#d7d7d7"));
                    amjad2.setTextColor(Color.parseColor("#d7d7d7"));
                    type = "car";

                    break;
                case R.id.tuktuk:
                    //Inform the user the button1 has been clicked
                    car.setImageResource(R.drawable.choicecar);
                    amjad.setImageResource(R.drawable.amjad);
                    tuktuk.setImageResource(R.drawable.redraksha);
                    car2.setTextColor(Color.parseColor("#d7d7d7"));
                    tuktuk2.setTextColor(Color.parseColor("#fa9684"));
                    amjad2.setTextColor(Color.parseColor("#d7d7d7"));
                    type = "tuktuk";
//
                    break;
                case R.id.tuktuk2:
                    //Inform the user the button1 has been clicked
                    car.setImageResource(R.drawable.choicecar);
                    amjad.setImageResource(R.drawable.amjad);
                    tuktuk.setImageResource(R.drawable.redraksha);
                    car2.setTextColor(Color.parseColor("#d7d7d7"));
                    tuktuk2.setTextColor(Color.parseColor("#fa9684"));
                    amjad2.setTextColor(Color.parseColor("#d7d7d7"));
                    type = "tuktuk";

                    break;
                case R.id.amjad:
                    //Inform the user the button1 has been clicked
                    car.setImageResource(R.drawable.choicecar);
                    amjad.setImageResource(R.drawable.redamjad);
                    tuktuk.setImageResource(R.drawable.tuktuk);
                    car2.setTextColor(Color.parseColor("#d7d7d7"));
                    tuktuk2.setTextColor(Color.parseColor("#d7d7d7"));
                    amjad2.setTextColor(Color.parseColor("#fa9684"));
                    type = "amjad";

                    break;
                case R.id.amjad2:
                    //Inform the user the button1 has been clicked
                    car.setImageResource(R.drawable.choicecar);
                    amjad.setImageResource(R.drawable.redamjad);
                    tuktuk.setImageResource(R.drawable.tuktuk);
                    car2.setTextColor(Color.parseColor("#d7d7d7"));
                    tuktuk2.setTextColor(Color.parseColor("#d7d7d7"));
                    amjad2.setTextColor(Color.parseColor("#fa9684"));
                    type = "amjad";

                    break;
            }
        }
    }
    public  void finishsignup(View view) throws MalformedURLException {
        EditText name=(EditText)findViewById(R.id.name);
        EditText password1=(EditText)findViewById(R.id.password1);
        EditText password2=(EditText)findViewById(R.id.password2);
        EditText phone=(EditText)findViewById(R.id.phone);
        TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);


        Dname=name.getText().toString();
        Dpassword1=password1.getText().toString();
        Dpassword2=password2.getText().toString();
        Dphone=phone.getText().toString();

        DIMEI=tm.getDeviceId();

        if(Dpassword1.equals(Dpassword2)) {
            HashMap post = new HashMap();
            post.put("Dname", Dname);
            post.put("Dpassword", Dpassword1);
            post.put("DIMEI",DIMEI);
            post.put("phone",Dphone);
            post.put("type",type);

            PostResponseAsyncTask task = new PostResponseAsyncTask(this, post);
            Firstsignup();

            task.execute("http://www.lymbo.esy.es/php.php");

            Intent intent = new Intent(this, dlogin.class);
            startActivity(intent);

        }
        else{
            Toast.makeText(this,"Password mismatch",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void processFinish(String s) {
        Toast.makeText(this,s,Toast.LENGTH_LONG).show();
    }
}



