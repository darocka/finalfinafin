package com.example.parmiss.finalproject;

/**
 * Created by kleeborp on 2016-03-09.
 */

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import databaseubm.MyDatabase;
import my.bgmailer.lib.Mailer;
import my.bgmailer.lib.MailerBackgroundImpl;

public class MyServiceMoniter extends Service {
    int threshold = 0;
    int delay = 0;
    TelephonyManager telephony;
    int sleepTime = 50;
    int currentAmplitude;
    String numberPhone;
    String userEmailAddress;
    String userName;
    String alertType;
    Boolean ready =false;
    Boolean playSound=false;
    MediaPlayer mp;
    MyDatabase db;

    Calendar startTimer;
    Calendar endTimer;


    Intent ServIntent;

    // Call, SMS, Send Email
    String actionOnAlert;

    final String usrname = "ultimatebabymoniter";
    final String passwd = "pewpewpew";

    MediaRecorder recorder = new MediaRecorder();

    public MyServiceMoniter() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    boolean running = true;
    boolean paused = false;
    Thread backgroundThread;
    Thread checkThread;

    //we need to override onCreate() and onDestroy()
    //these methods contain the functionality of the service when started and stopped

    @Override
    public void onCreate() {
        super.onCreate();
        // Toast.makeText(this, "Listening", Toast.LENGTH_LONG).show();
        paused = false;
        running = true;

        db = new MyDatabase(this);
        startTimer = Calendar.getInstance();


//        PhoneCallListener phoneListener = new PhoneCallListener();
//        TelephonyManager telephonyManager = (TelephonyManager) this
//                .getSystemService(Context.TELEPHONY_SERVICE);
//        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);


        PhoneCallListener phoneListener = new PhoneCallListener();
        telephony = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

//        backgroundThread = new Thread(new Runnable() {
//            public void run() {
//                playMusicFromWeb();
//            }
//        });
//
//        backgroundThread.start();

        if (recorder == null) {
            recorder = new MediaRecorder();
            ready=true;
        }
        // Begin using the media recorder, the microphone is now being used.
        try {
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile("/dev/null");
            recorder.prepare();
            recorder.start();
            recorder.getMaxAmplitude();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public int onStartCommand(final Intent this_intent, final int flags, final int startId) {

        // Access Global Variables
        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        threshold = sharedPrefs.getInt("thresholdTime", 1000);
        delay = sharedPrefs.getInt("delay", 5000);
        numberPhone = sharedPrefs.getString("mobile", "X");
        userEmailAddress = sharedPrefs.getString("email", "X");
        alertType = sharedPrefs.getString("alertType","call");
        userName = sharedPrefs.getString("name","Guest");

        playSound = this_intent.getBooleanExtra("shouldPlay", false);

        ServIntent = this_intent;
        // threshold = intent.getIntExtra("threshold", 1000);
        // delay = intent.getIntExtra("delay", 5000);
        // numberPhone = intent.getStringExtra("mobile");

        //Toast.makeText(this, " " + String.valueOf(delay) + " " + String.valueOf(threshold), Toast.LENGTH_LONG).show();
        //Toast.makeText(this,numberPhone,Toast.LENGTH_SHORT).show();
//        while(recorder==null && ready==false){
//            Log.e("ERRRR","THISONE");
//        }


        checkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int x = 0;
                while (running) {
                    x++;

                    try {
                        Intent intent = new Intent();
                        intent.setAction("iat.ultimatebabymoniter");
                        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

                        while(recorder==null && ready==false){
                            Log.e("ERRRR","INSIDEO");
                        }
                        currentAmplitude = recorder.getMaxAmplitude();


                        boolean loopFlag = true;


                        long start_time = System.currentTimeMillis();
                        long time = 0;






                        if (currentAmplitude > threshold && loopFlag == true) {
                            while (currentAmplitude > threshold && loopFlag == true && running == true) {
                                if (currentAmplitude > threshold && time > delay && running == true) {
                                    //Call
                                    Log.d("DB", "Call");

                                    if(alertType.equals("call")){
                                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                                        callIntent.setData(Uri.parse("tel:" + numberPhone));

                                        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                            // TODO: Consider calling
                                            //    ActivityCompat#requestPermissions
                                            // here to request the missing permissions, and then overriding
                                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                            //                                          int[] grantResults)
                                            // to handle the case where the user grants the permission. See the documentation
                                            // for ActivityCompat#requestPermissions for more details.
                                            return;
                                        }
                                        endTimer = Calendar.getInstance();

                                        db.insertLogIntoLOGTable(
                                                userName,
                                                "CALL",
                                                getDate(),
                                                getTime(startTimer),
                                                getTime(endTimer),
                                                elapsedTime(startTimer, endTimer));

                                        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        callIntent.addFlags(Intent.FLAG_FROM_BACKGROUND);
                                        startActivity(callIntent);
                                        stopSelf();


                                        // int randomNum = 0 + (int)(Math.random() * 1000);
                                        //Toast.makeText(getBaseContext(), String.valueOf(randomNum)+ "  " + String.valueOf(playSound), Toast.LENGTH_SHORT).show();
                                        // stopSelf();


                                    } else if(alertType.equals("Email")){
                                        // Send Email and Alert
                                        Intent intentCam = new Intent(getBaseContext(),PhotoTakingService.class);
                                        intentCam.putExtra("email", userEmailAddress);
                                        startService(intentCam);
                                        Log.d("RWE", "executed?");

                                        endTimer = Calendar.getInstance();
                                        db.insertLogIntoLOGTable(
                                                userName,
                                                "EMAIL",
                                                getDate(),
                                                getTime(startTimer),
                                                getTime(endTimer),
                                                elapsedTime(startTimer,endTimer));

                                        Intent i = new Intent( new Intent(getBaseContext(), MoniterActivity.class));
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);



                                        if(playSound==true) {
                                            Intent mp = new Intent(getBaseContext(), MyServiceMP.class);
                                            mp.putExtra("play", 5);
                                            startService(mp);
                                        }

                                        stopService(this_intent);

                                    } else if(alertType.equals("sms")){
                                        // Send SMS
                                        String message = getBaseContext().getString(R.string.sms_msg);
                                        sendSMS(numberPhone,message);

                                        endTimer = Calendar.getInstance();
                                        db.insertLogIntoLOGTable(
                                                userName,
                                                "SMS",
                                                getDate(),
                                                getTime(startTimer),
                                                getTime(endTimer),
                                                elapsedTime(startTimer,endTimer));


                                        Intent i = new Intent( new Intent(getBaseContext(), MoniterActivity.class));
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);




                                        if(playSound==true) {
                                            Intent mp = new Intent(getBaseContext(), MyServiceMP.class);
                                            mp.putExtra("play", 5);
                                            startService(mp);
                                        }

                                        stopService(this_intent);

                                    } else if(alertType.equals("smsEmail")){
                                        // Send SMS
                                        String message = getBaseContext().getString(R.string.sms_msg);
                                        sendSMS(numberPhone,message);

                                        // Send Email
                                        Intent intentCam = new Intent(getBaseContext(),PhotoTakingService.class);
                                        intentCam.putExtra("email", userEmailAddress);
                                        startService(intentCam);

                                        endTimer = Calendar.getInstance();
                                        db.insertLogIntoLOGTable(
                                                userName,
                                                "SMS/EMAIL",
                                                getDate(),
                                                getTime(startTimer),
                                                getTime(endTimer),
                                                elapsedTime(startTimer,endTimer));


                                        Intent i = new Intent( new Intent(getBaseContext(), MoniterActivity.class));
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);

                                        if(playSound==true) {
                                            Intent mp = new Intent(getBaseContext(), MyServiceMP.class);
                                            mp.putExtra("play", 5);
                                            startService(mp);
                                        }


                                        stopService(this_intent);

                                    }

                                    loopFlag = false;
                                    break;


                                } else {
                                    time = System.currentTimeMillis() - start_time;

                                    int newAmp = recorder.getMaxAmplitude();
                                    if(newAmp !=0){
                                        currentAmplitude = newAmp;
                                    }


                                    intent.putExtra("X", newAmp);
                                    intent.putExtra("time", time);
                                    sendBroadcast(intent);
                                    Thread.sleep(sleepTime);
                                    Log.d("DB", "In Loop Waiting" + ": " + String.valueOf(currentAmplitude));
                                }
                            }
                            Log.d("DB", "Just Exited Loop Waiting");

                        }
                        else{
                            Thread.sleep(sleepTime);
                            intent.putExtra("time", 0);

                        }




                        intent.putExtra("X", recorder.getMaxAmplitude());
                        sendBroadcast(intent);



                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        checkThread.start();
//        Uri.parse("android.resource://com.example.parmiss.finalproject/" + R.raw.con);
//        playSoundForXSeconds(this,R.raw.con, 5);

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        telephony.listen(null, PhoneStateListener.LISTEN_NONE);

        running = false;
        checkThread.interrupt();

        // backgroundThread.interrupt();


        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }

        paused = true;

        //checkThread.interrupt();
        //backgroundThread.interrupt();

        running = false;
        // Toast.makeText(this, "Stop Listening", Toast.LENGTH_LONG).show();
        Log.d("CALLD","??");

    }

    public void playMusicFromWeb() {
        if (recorder == null) {
            recorder = new MediaRecorder();
            ready=true;
        }
        // Begin using the media recorder, the microphone is now being used.
        try {
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile("/dev/null");
            recorder.prepare();
            recorder.start();
            recorder.getMaxAmplitude();
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void send(String subject, String FilePath, String FileName){
        Mailer m = new MailerBackgroundImpl(this);
        String[] address = { "nnastili@gmail.com" };
        m.addRecepients(address);
        m.setUserName(usrname);
        m.setPassword(passwd);
        m.setSubject(subject);
        m.setBody("<Body>");
        // m.addAttachment(file.getAbsolutePath(), "<filename>");
        m.send();
    }

    // SEND TXT FUNCTION
    public void sendSMS(String phoneNo, String msg){
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////
    /////// FUNCTION FOR TIMING

    public String getTime(Calendar C){
        SimpleDateFormat hh = new SimpleDateFormat("hh");
        SimpleDateFormat mm = new SimpleDateFormat("mm");
        SimpleDateFormat ss = new SimpleDateFormat("ss");


        String hours = hh.format(C.getTime());
        String minutes =  mm.format(C.getTime());
        String seconds = ss.format(C.getTime());


        return hours+":"+minutes+":"+seconds;
    }

    public String elapsedTime(Calendar startDate, Calendar endDate){
        long end = endDate.getTimeInMillis();
        long start = startDate.getTimeInMillis();
        String hours = String.valueOf(TimeUnit.MILLISECONDS.toHours(Math.abs(end - start)));
        String minutes = String.valueOf(TimeUnit.MILLISECONDS.toMinutes(Math.abs(end - start)));
        String seconds = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(Math.abs(end - start)));

        return "HR: "+hours+", Min: "+minutes+", Secs: "+seconds;

    }

    public String getDate(){
        SimpleDateFormat dateF = new SimpleDateFormat("dd/MMM/yyyy");
        // SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy" + " hh:mm:ss");
        Calendar c = Calendar.getInstance();
        return dateF.format(c.getTime());
    }


//////////////////////////////////////////////////////////////////////////////////////////////////

    //monitor phone call activities
    private class PhoneCallListener extends PhoneStateListener {

        private boolean isPhoneCalling = false;

        String LOG_TAG = "LOGGING 123";

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            if (TelephonyManager.CALL_STATE_RINGING == state) {
                // phone ringing
                Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
            }

            if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                // active
                Log.i(LOG_TAG, "OFFHOOK");

                isPhoneCalling = true;
            }



            if (TelephonyManager.CALL_STATE_IDLE == state) {
                // run when class initial and phone call ended,
                // need detect flag from CALL_STATE_OFFHOOK
                Log.i(LOG_TAG, "IDLE");

                if (isPhoneCalling) {

                    Log.i("XV", "restart app");

                    // restart app

                    //Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                    //Intent i = new Intent( new Intent(getBaseContext(), MoniterActivity.class));

                    //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //startActivity(i);
//
//                    if(playSound==true) {
//                        Intent mp = new Intent(getBaseContext(), MyServiceMP.class);
//                        mp.putExtra("play", 5);
//                        startService(mp);
//                    }




                    // Toast.makeText(getBaseContext(), String.valueOf(randomNum)+ "  " + String.valueOf(playSound), Toast.LENGTH_SHORT).show();
                    //stopSelf();

                    isPhoneCalling = false;
                }else {

                }

            }
        }
    }


}