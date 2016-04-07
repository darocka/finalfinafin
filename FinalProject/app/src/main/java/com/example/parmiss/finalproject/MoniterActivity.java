package com.example.parmiss.finalproject;

/**
 * Created by kleeborp on 2016-03-09.
 */
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ServiceConfigurationError;
import java.util.Timer;
import java.util.TimerTask;

//import iat.ultimatebabymoniter.MyServiceMoniter.MyServiceMoniterBinder;

public class MoniterActivity extends Activity  {
    String name;


    ProgressBar amplitudeLevelAbove;
    ProgressBar amplitudeLevelBelow;

    SeekBar threshold;
    SeekBar delay;

    TextView thresholdTextView;
    TextView delayTextView;

    Button buttonStart;
    Button buttonStop;

    // Global Variables for Shared Preference.
    String number;
    String username;
    String email;
    String alert;



    float valueT=10000;
    float delayT=5;

    TextView countDown;
    boolean isStarted = false;
    long timepassed = 0;
    private IntentFilter filter = new IntentFilter("iat.ultimatebabymoniter");




    public void options(View v) {
        updateDelayTimeThresholdGlobal((int) (delayT*1000), (int) valueT);
        Intent login = new Intent(getApplicationContext(), options_Activity.class);
        startActivity(login);
    }

        public void start(View v) {
            Intent intent = new Intent (this, MyServiceMoniter.class);

        //intent.putExtra("threshold",(int) valueT);
        //intent.putExtra("delay",(int) (delayT*1000));
        //intent.putExtra("mobile", number);

            updateDelayTimeThresholdGlobal((int) (delayT*1000), (int) valueT);

            SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
            alert = sharedPrefs.getString("alertType","call");


            threshold.setEnabled(false);
            delay.setEnabled(false);


            if(isStarted==false){
                intent.putExtra("AT",alert);
                intent.putExtra("shouldPlay",sharedPrefs.getBoolean("alertPlay", false));
                startService(intent);  //the service is started
                isStarted = true;

                buttonStart.setEnabled(false);
                buttonStop.setEnabled(false);
                Timer buttonTimer = new Timer();
                buttonTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                buttonStop.setEnabled(true);
                            }
                        });
                    }
                }, 3000);


            }




    }

    public void stop(View v) {

        //the service is started
        if(isStarted==true) {


            Intent intent = new Intent (this, MyServiceMoniter.class);
            stopService(intent); //the service is stopped

            threshold.setEnabled(true);
            delay.setEnabled(true);
            isStarted = false;

            buttonStop.setEnabled(false);
            Timer buttonTimer = new Timer();
            buttonTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            buttonStart.setEnabled(true);
                        }
                    });
                }
            }, 3000);
            buttonStart.setEnabled(false);
            buttonStop.setEnabled(false);



            //stopService(intent);
        }

        Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        countDown.setText("");
                        amplitudeLevelAbove.setProgress(0);
                        amplitudeLevelBelow.setProgress(0);

                    }
                }, 1000);




    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moniter);
        Intent i = new Intent(this,MyServiceMoniter.class);


        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStop = (Button) findViewById(R.id.buttonStop);
        buttonStop.setEnabled(false);

        amplitudeLevelAbove = (ProgressBar) findViewById(R.id.progressBar2);
        amplitudeLevelBelow = (ProgressBar) findViewById(R.id.progressBar1);
        threshold = (SeekBar) findViewById(R.id.seekBarThresholdVolume);


        SharedPreferences sh = getSharedPreferences("myprefe", MODE_PRIVATE);
        number = sh.getString("mobile","1234567");

        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        username = sharedPrefs.getString("name", "Drake");
        number = sharedPrefs.getString("mobile", "7783163276");
        email = sharedPrefs.getString("email", "nnastili@sfu.ca");

        alert = sharedPrefs.getString("alertType","call");

        Toast.makeText(this,"Hello " + username + " with " + "email "+email + " with " + "phone "+number + " with alert "+ alert,Toast.LENGTH_SHORT).show();

        Drawable draw = getResources().getDrawable(R.drawable.custom_progressbar);
        Drawable drawRed = getResources().getDrawable(R.drawable.custom_redprogressbar);



        // set the drawable as progress drawable
        amplitudeLevelAbove.setProgressDrawable(drawRed);
        amplitudeLevelAbove.setMax(32767-10000 );

        LinearLayout.LayoutParams paramsAbove = (LinearLayout.LayoutParams) amplitudeLevelAbove.getLayoutParams();
        paramsAbove.weight = 32767-10000;
        amplitudeLevelAbove.setLayoutParams(paramsAbove);



        amplitudeLevelBelow.setProgressDrawable(draw);
        amplitudeLevelBelow.setMax(10000);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) amplitudeLevelBelow.getLayoutParams();
        params.weight = valueT;
        amplitudeLevelBelow.setLayoutParams(params);

        threshold.setMax(32767);
        threshold.setProgress(10000);
        threshold.setOnSeekBarChangeListener(examSeekBarListener);


        delay = (SeekBar) findViewById(R.id.seekBarDelayThreshold);
        delay.setMax(27);
        delay.setProgress(5);
        delay.setOnSeekBarChangeListener(delaySeekBarListener);

        thresholdTextView = (TextView) findViewById(R.id.selectThreshold);
        thresholdTextView.setText("Volume Threshold : " + String.valueOf((int) valueT));

        delayTextView = (TextView) findViewById(R.id.selectDelayThreshold);
        delayTextView.setText("Delay Threshold : " + String.valueOf((int) 5));

        countDown = (TextView) findViewById(R.id.countDownTextView);
        countDown.setTextColor(Color.RED);

    }

    @Override
    protected void onResume() {
        this.registerReceiver(receiver, filter);

        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        int currentValue = sharedPrefs.getInt("thresholdTime", 10000);
        updateThresholdVALUE(currentValue);

        int currentDelayValue = sharedPrefs.getInt("delay", 5000);
        updateDelayValue((int) currentDelayValue/1000);

        super.onResume();

    }

    @Override
    protected void onPause() {
        this.unregisterReceiver(receiver);
        super.onPause();
    }




    private SeekBar.OnSeekBarChangeListener examSeekBarListener = new SeekBar.OnSeekBarChangeListener(){

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int x = seekBar.getProgress();
            valueT = x;
            float upperValue = 32767 - valueT;

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) amplitudeLevelBelow.getLayoutParams();

            float scale=1.10f;

            params.weight = scale*valueT;
            amplitudeLevelBelow.setLayoutParams(params);
            amplitudeLevelBelow.setMax((int) valueT);


            LinearLayout.LayoutParams paramsAbove = (LinearLayout.LayoutParams) amplitudeLevelAbove.getLayoutParams();
            paramsAbove.weight = upperValue;
            amplitudeLevelAbove.setLayoutParams(paramsAbove);
            amplitudeLevelAbove.setMax((int) upperValue);
            amplitudeLevelAbove.setProgress(500);

            // wd.setText( String.valueOf(upperValue)+ " " + String.valueOf(valueT));
            thresholdTextView.setText("Volume Threshold : " + String.valueOf((int) valueT));




        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private SeekBar.OnSeekBarChangeListener delaySeekBarListener = new SeekBar.OnSeekBarChangeListener(){

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int x = seekBar.getProgress();
            delayT = x + 3;
            // wd.setText( String.valueOf(upperValue)+ " " + String.valueOf(valueT));
            delayTextView.setText("Delay Threshold : " + String.valueOf((int) delayT));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private BroadcastReceiver receiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            MoniterActivity.this.receivedBroadcast(intent);

        }

    };

    private void receivedBroadcast(Intent ri) {

        // Put your receive handling code here
        int level = ri.getExtras().getInt("X");
        timepassed = ri.getExtras().getLong("time");

        if(level > valueT){
            amplitudeLevelAbove.setProgress(level - (int) valueT);
            amplitudeLevelBelow.setProgress(amplitudeLevelBelow.getMax());

            if(0>((long) (delayT*1000)-timepassed)){
                countDown.setText("ALERT");

//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    public void run() {
//                        countDown.setText("");
//                        amplitudeLevelAbove.setProgress(0);
//                        amplitudeLevelBelow.setProgress(amplitudeLevelBelow.getMax());
//
//                    }
//                }, 2000);

            }
            else {
                countDown.setText(String.valueOf((new SimpleDateFormat("mm:ss")).format(new Date((long) (delayT * 1000) - timepassed))));
            }

            //int we = (new SimpleDateFormat("mm:ss")).format(new Date(timepassed)) - (new SimpleDateFormat("mm:ss")).format(new Date(timepassed));
        }
        else{
            amplitudeLevelAbove.setProgress(0);
            amplitudeLevelBelow.setProgress(level);
            countDown.setText("");
        }

    }


    public void sendSMS(String phoneNo, String msg){
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }



    public void updateDelayTimeThresholdGlobal(int delayValue, int threshold){
        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt("delay", delayValue);
        editor.putInt("thresholdTime", threshold);
        editor.commit();
    }

    public void updateThresholdVALUE(int x){
        threshold.setProgress(x);
        valueT = x;
        float upperValue = 32767 - valueT;

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) amplitudeLevelBelow.getLayoutParams();

        float scale=1.10f;

        params.weight = scale*valueT;
        amplitudeLevelBelow.setLayoutParams(params);
        amplitudeLevelBelow.setMax((int) valueT);


        LinearLayout.LayoutParams paramsAbove = (LinearLayout.LayoutParams) amplitudeLevelAbove.getLayoutParams();
        paramsAbove.weight = upperValue;
        amplitudeLevelAbove.setLayoutParams(paramsAbove);
        amplitudeLevelAbove.setMax((int) upperValue);
        amplitudeLevelAbove.setProgress(500);

        // wd.setText( String.valueOf(upperValue)+ " " + String.valueOf(valueT));
        thresholdTextView.setText("Volume Threshold : " + String.valueOf((int) valueT));
    }


    public void updateDelayValue(int x){
        delay.setProgress(x-3);
        delayT = x;
        // wd.setText( String.valueOf(upperValue)+ " " + String.valueOf(valueT));
        delayTextView.setText("Delay Threshold : " + String.valueOf((int) delayT));
    }


}
