package com.gailardia.lymbo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Gailardia on 8/24/2016.
 */
public class DriverTimerCancelRequest extends Service {
    public int time = 0;
    private Handler handler = new Handler();
    public static final String TRANSACTION_DONE = "com.gailardia.TRANSACTION_DONE";
    private PowerManager.WakeLock mWakeLock;

    @Nullable
    @Override

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My tag");
        mWakeLock.acquire();
        new GetRequestDetails().execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWakeLock.release();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Check Cancellation", "Check Cancellation Again.");
        return START_STICKY;
    }

    public class GetRequestDetails extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            String response = new Route().synchronousCall("http://www.lymbo.esy.es/checkRequestCancel.php", "{\"Dname\":\"" + getSharedPreferences("com.gailardia.lymbo", Context.MODE_PRIVATE).getString("username", "NULL") + "\"}");
            Log.i("reponse", response);
            Log.i("timer", String.valueOf(time++));
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response.contains("cancelled")) {
                Log.i("RequestCancel", "request has been cancelled");
                Log.i("RequestCancel", response);
            }
            Intent i = new Intent(TRANSACTION_DONE);
            i.putExtra("response", response);
            stopSelf();
            DriverTimerCancelRequest.this.sendBroadcast(i);
        }
    }
}
