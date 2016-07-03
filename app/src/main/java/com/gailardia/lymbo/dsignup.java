package com.gailardia.lymbo;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.HashMap;

import self.philbrown.droidQuery.$;
import self.philbrown.droidQuery.AjaxOptions;
import self.philbrown.droidQuery.Function;

public class dsignup extends AppCompatActivity implements AsyncResponse {
    private final int SELECT_PHOTO = 1;
    RelativeLayout scnd;
    LinearLayout first;
    int carType=0;
    String Dname,Dpassword1,Dpassword2,DIMEI,type,OnlineState,Dphone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dsignup);
        type="";
        CircularImageView pickImage = (CircularImageView) findViewById(R.id.selected);
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
        RelativeLayout scnd=(RelativeLayout) findViewById(R.id.scndSignup);
        LinearLayout first=(LinearLayout) findViewById(R.id.firstSignup);
        scnd.setVisibility(View.INVISIBLE);
        first.setVisibility(View.VISIBLE);
    }
    public void Scndsignup(View view){
        EditText userName=(EditText) findViewById(R.id.name);
        EditText password1=(EditText) findViewById(R.id.password1);
        EditText password2=(EditText) findViewById(R.id.password2);
        EditText phoneNumber=(EditText) findViewById(R.id.phone);

        String user = userName.getText().toString();
        String pass1 = password1.getText().toString();
        String pass2 = password2.getText().toString();
        String phoneNum = phoneNumber.getText().toString();

        if(user.isEmpty() || pass1.isEmpty() || pass2.isEmpty() || phoneNum == null){
            Toast.makeText(getApplicationContext(), "Fill all the fields!", Toast.LENGTH_SHORT).show();

        } else {
                 if(!(pass1.equals(pass2))) {
                     Toast.makeText(getApplicationContext(), "Passwords don't match!!", Toast.LENGTH_SHORT).show();

                 } else {
                     $.ajax(new AjaxOptions().url("http://www.lymbo.esy.es/validateUser.php")
                             .type("POST")
                             .data("{\"Dname\":\""+user+"\"}")
                             .context(this)
                             .success(new Function() {

                                 @Override
                                 public void invoke($ droidQuery, Object... objects) {
                                     if(((String)objects[0]).equalsIgnoreCase("false")) {
                                         Toast.makeText(dsignup.this,"Username is used !!",Toast.LENGTH_LONG).show();
                                     }
                                     else
                                     if(((String)objects[0]).equalsIgnoreCase("true")){
                                         scnd=(RelativeLayout) findViewById(R.id.scndSignup);
                                         first=(LinearLayout) findViewById(R.id.firstSignup);
                                         first.animate().translationXBy(-1000f).setDuration(700);
                                         scnd.setAlpha(1f);
                                         scnd.setVisibility(View.VISIBLE);
                                         first.postDelayed(new Runnable() {
                                             @Override
                                             public void run() {
                                                 first.setVisibility(View.GONE);
                                             }
                                         }, 700);
                                     }
                                 }
                             })
                             .error(new Function() {
                                 @Override
                                 public void invoke($ $, Object... args) {
                                     Toast.makeText(dsignup.this,"error",Toast.LENGTH_LONG).show();
                                 }
                             }));
                    /* */
            }
        }

    }
    public void goDlogin(View view){
        dsignup s=new dsignup();
        dlogin d=new dlogin();
        d.finishlogin();
        Intent intent=new Intent(this,dlogin.class);
        startActivity(intent);
        finish();
    }
    Bitmap selectedImage;
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        CircularImageView selectphoto=(CircularImageView) findViewById(R.id.selected);
        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    try {
                        final Uri imageUri = imageReturnedIntent.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                         selectedImage = BitmapFactory.decodeStream(imageStream);
                        selectphoto.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
        }
    }
    public void type(View view) {
        type = "";
        ImageButton car = (ImageButton) findViewById(R.id.car);
        ImageButton tuktuk = (ImageButton) findViewById(R.id.tuktuk);
        ImageButton amjad = (ImageButton) findViewById(R.id.amjad);
        TextView car2 = (TextView) findViewById(R.id.car2);
        TextView tuktuk2 = (TextView) findViewById(R.id.tuktuk2);
        TextView amjad2 = (TextView) findViewById(R.id.amjad2);
        type = "";
        if (car != null && car2 != null && tuktuk != null && tuktuk2 != null && amjad2 != null && amjad != null) {
            switch (view.getId()) {

                case R.id.car:
                    //Inform the user te button1 has been clicked
                    car.setImageResource(R.drawable.redcar);
                    amjad.setImageResource(R.drawable.amjad);
                    tuktuk.setImageResource(R.drawable.tuktuk);
                    car2.setTextColor(Color.parseColor("#fa9684"));
                    tuktuk2.setTextColor(Color.parseColor("#d7d7d7"));
                    amjad2.setTextColor(Color.parseColor("#d7d7d7"));
                    type = "car";
                    animateCarChoice();
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
                    animateCarChoice();
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
                    unanimateCarChoice();
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
                    unanimateCarChoice();
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
                    unanimateCarChoice();
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
                    unanimateCarChoice();
                    break;
            }
        }
    }

    protected void animateCarChoice(){
        LinearLayout linear = (LinearLayout) findViewById(R.id.linearLayout);
        final CircularImageView selectImage = (CircularImageView) findViewById(R.id.selected);
        if (selectImage.getVisibility() != View.VISIBLE) {
            linear.animate().translationYBy(-400f).setDuration(500);
            selectImage.animate().alpha(1f).setDuration(1100);
            selectImage.setVisibility(View.VISIBLE);
            return;
        }
    }
    protected void unanimateCarChoice(){
        LinearLayout linear = (LinearLayout) findViewById(R.id.linearLayout);
        final CircularImageView selectImage = (CircularImageView) findViewById(R.id.selected);
        if (selectImage.getVisibility() == View.VISIBLE) {
            linear.animate().translationYBy(400f).setDuration(600);
            selectImage.animate().alpha(0f).setDuration(300);
            selectImage.postDelayed(new Runnable() {
                @Override
                public void run() {
                    selectImage.setVisibility(View.INVISIBLE);
                }
            }, 500);
            return;
        }
    }

    public  void finishsignup(View view) throws MalformedURLException, UnsupportedEncodingException {
        EditText name=(EditText)findViewById(R.id.name);
        EditText password1=(EditText)findViewById(R.id.password1);
        EditText password2=(EditText)findViewById(R.id.password2);
        EditText phone=(EditText)findViewById(R.id.phone);
        EditText firstName = (EditText)findViewById(R.id.firstName);
        EditText lastName = (EditText)findViewById(R.id.lastName);
        TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);

        Dname=name.getText().toString();
        Dpassword1=password1.getText().toString();
        Dpassword2=password2.getText().toString();
        Dphone=phone.getText().toString();

        //String image=getStringImage(selectedImage);
        DIMEI="00971503468518";

        final HashMap post = new HashMap();
        post.put("Dname", Dname);
        post.put("Dpassword", Dpassword1);
        post.put("DIMEI",DIMEI);
        post.put("phone",Dphone);
        post.put("type",type);
        post.put("firstName", firstName.getText().toString());
        post.put("lastName", lastName.getText().toString());
        //post.put("image",image);

        PostResponseAsyncTask task = new PostResponseAsyncTask(this, post);


        if(type==""){
            Toast.makeText(getApplicationContext(), "Please choose your type of car!!", Toast.LENGTH_LONG).show();
        }else {
            task.execute("http://www.lymbo.esy.es/signup.php");
        }


    }
    /*public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }*/
    public boolean isOnline() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }

        return false;
    }
    @Override
    public void processFinish(String s) {
        Log.i("Database result", s);
        if(s.equalsIgnoreCase("success")){
            Firstsignup();
            Intent intent = new Intent(this, dlogin.class);
            startActivity(intent);
        }
        else {
            if(!isOnline())
                Toast.makeText(this,"No Internet access",Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this,"Try Again",Toast.LENGTH_LONG).show();

        }
    }
}



