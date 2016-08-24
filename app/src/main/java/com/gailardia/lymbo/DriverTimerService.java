package com.gailardia.lymbo;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Gailardia on 8/24/2016.
 */
public class DriverTimerService extends Service {
    public int time = 0;
    private Handler handler = new Handler();
    public static final String TRANSACTION_DONE = "com.gailardia.TRANSACTION_DONE";

    @Nullable
    @Override

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new GetRequestDetails().execute();
            }
        }, 5000);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(DriverTimerService.this, "Searching for customers.", Toast.LENGTH_SHORT).show();
        return 1;
    }

    public class GetRequestDetails extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String s = "1";
            String response = new Route().synchronousCall("http://www.lymbo.esy.es/getRequestDetails.php", "{\"Dname\":\"" + "a" + "\"}");
            Log.i("reponse", response);
            Log.i("timer", String.valueOf(time++));
            if (!response.equalsIgnoreCase("No Request found") && !response.toString().equalsIgnoreCase("No location")) {
                Log.i("request", "found request");
                Intent i = new Intent(TRANSACTION_DONE);
                i.putExtra("response", response);
                DriverTimerService.this.sendBroadcast(i);
                stopSelf();
                s = "2";
            }
            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("1")) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new GetRequestDetails().execute();
                    }
                }, 5000);
            }
        }
    }
}
