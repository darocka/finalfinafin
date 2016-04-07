package com.example.parmiss.finalproject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import java.io.File;

public class MyServiceMP extends Service {
    public MyServiceMP() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        CountDownTimer timer = new CountDownTimer(2000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
                SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                final int playfor = sharedPrefs.getInt("durationSong", 10);

                Boolean recordingExists = sharedPrefs.getBoolean("RecExist", false);
                String defaultSong ="android.resource://com.example.parmiss.finalproject/" + R.raw.lull1;
                String pathOfRec = sharedPrefs.getString("PathToCustom", "X");

                shw("Da Path" + pathOfRec);
                shw("Da exist" + String.valueOf(recordingExists));

                // Initialize
                Uri song = Uri.parse(defaultSong);

                if(sharedPrefs.getString("alertSound", "sound1").equals("sound1")){
                    song = Uri.parse("android.resource://com.example.parmiss.finalproject/" + R.raw.lull1);
                } else if (sharedPrefs.getString("alertSound", "sound1").equals("sound2")) {
                    song = Uri.parse("android.resource://com.example.parmiss.finalproject/" + R.raw.lull2);
                } else if (sharedPrefs.getString("alertSound", "sound1").equals("sound3")){
                    song = Uri.parse("android.resource://com.example.parmiss.finalproject/" + R.raw.wn1);
                } else if (sharedPrefs.getString("alertSound", "sound1").equals("sound4") && recordingExists==true && !pathOfRec.equals("X")){

                    song = Uri.fromFile(new File(pathOfRec));
                }

                playSoundForXSeconds(getApplicationContext(), song, playfor);
            }
        };
        timer.start();




        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private void playSoundForXSeconds(Context cont,final Uri soundUri, int seconds) {
        if(soundUri!=null) {
            final MediaPlayer mp = new MediaPlayer();
            try {
                mp.setDataSource(cont, soundUri);
                mp.prepare();
                mp.start();
            }catch(Exception e) {
                e.printStackTrace();
            }

            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    try {
                        mp.stop();
                        mp.release();
                        stopSelf();
                    }catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }, seconds * 1000);
        }
    }

    public void shw(String m){
        Toast.makeText(getBaseContext(),m,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
