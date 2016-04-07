package com.example.parmiss.finalproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class options_Activity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener,View.OnClickListener,AdapterView.OnItemSelectedListener,CompoundButton.OnCheckedChangeListener {
    RadioGroup selectAlertRadioGroup, selectAlertBabyRadioGroup, selectDurationRadioGroup;
    RadioButton callb,smsb,emailb,smsemailb;
    RadioButton sound1b,sound2b,sound3b,sound4b;
    RadioButton sec10b, sec20b,sec30b;
    Button rec;
    Button savePresetButton;

    MediaRecorder recorder;



    SharedPreferences sharedPrefs;
    String alertSelect, alertSound;
    Boolean alertPlayOnOff;
    int durationSong;

    String pathOfAudio;


    private Switch mySwitch;
    Spinner spinner;
    String currentPreset="Preset-1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options);

        selectAlertRadioGroup = (RadioGroup) findViewById(R.id.selectAlertRG);
        selectAlertRadioGroup.setOnCheckedChangeListener(this);

        callb = (RadioButton) findViewById(R.id.callAlert);
        smsb = (RadioButton) findViewById(R.id.smsAlert);
        emailb = (RadioButton) findViewById(R.id.EmailAlert);
        smsemailb = (RadioButton) findViewById(R.id.smsEmailAlert);

        selectAlertBabyRadioGroup = (RadioGroup) findViewById(R.id.selectAlertBabyRG);
        selectAlertBabyRadioGroup.setOnCheckedChangeListener(this);

        sound1b = (RadioButton) findViewById(R.id.sound1);
        sound2b = (RadioButton) findViewById(R.id.sound2);
        sound3b = (RadioButton) findViewById(R.id.sound3);
        sound4b = (RadioButton) findViewById(R.id.sound4);


        selectDurationRadioGroup = (RadioGroup) findViewById(R.id.selectSongDurationRG);
        selectDurationRadioGroup.setOnCheckedChangeListener(this);

        sec10b = (RadioButton) findViewById(R.id.sec10);
        sec20b = (RadioButton) findViewById(R.id.sec20);
        sec30b = (RadioButton) findViewById(R.id.sec30);


        mySwitch = (Switch) findViewById(R.id.PlaySongSwitch);
        mySwitch.setOnCheckedChangeListener(this);
        mySwitch.setChecked(false);

        Button savePresetButton = (Button) findViewById(R.id.savePreset);
        savePresetButton.setOnClickListener(this);

        Button loadPresetButton = (Button) findViewById(R.id.loadPreset);
        loadPresetButton.setOnClickListener(this);

        rec = (Button) findViewById(R.id.rec);

        initializeRecordingFile();

        rec.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Do something
                        Toast.makeText(getBaseContext(), "Start Rec", Toast.LENGTH_SHORT).show();
                        rec.setText("RECORDING");
                        rec.setTextColor(Color.RED);
                        recordAudio();


                        return true;
                    case MotionEvent.ACTION_UP:
                        stopRecord();
                        rec.setText("Saving");
                        rec.setEnabled(false);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                rec.setText("RECORD");
                                rec.setTextColor(Color.BLACK);
                                rec.setEnabled(true);
                            }
                        }, 4000);


                        if (DoesRecordingExist(pathOfAudio) == true) {
                            sound4b.setVisibility(View.VISIBLE);

                            SharedPreferences shared = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = shared.edit();
                            editor.putBoolean("RecExist", true);
                            editor.putString("PathToCustom",pathOfAudio);
                            editor.commit();

                        }




                        //sound4b.setVisibility(View.VISIBLE);

                        Toast.makeText(getBaseContext(), "Stop Rec", Toast.LENGTH_SHORT).show();
                        // stopRecord();
                        return true;
                }
                return false;
            }
        });


        spinner = (Spinner) findViewById(R.id.planets_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.preset_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

    }

    @Override
    protected void onResume() {
        sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        alertSelect = sharedPrefs.getString("alertType","call");
        alertSound = sharedPrefs.getString("alertSound", "sound1");
        alertPlayOnOff = sharedPrefs.getBoolean("alertPlay", false);
        durationSong = sharedPrefs.getInt("durationSong", 10);

        // editor.putString("alertType", "call");
        //editor.putString("alertSound", "sound1");
        //editor.putBoolean("alertPlay", false);
        //editor.commit();


        if(DoesRecordingExist(pathOfAudio)==false){
        sound4b.setVisibility(View.INVISIBLE);
        }

        // Updates Radio Values for Alert Type to Current Values
        String M = sharedPrefs.getString("alertType", "call");
        if(alertSelect.equalsIgnoreCase("call"))
        {
            callb.setChecked(true);
        }
        else if(alertSelect.equalsIgnoreCase("sms")){

            smsb.setChecked(true);
        }
        else if(alertSelect.equalsIgnoreCase("smsEmail"))
        {
            smsemailb.setChecked(true);
        }
        else if(alertSelect.equalsIgnoreCase("Email"))
        {
            emailb.setChecked(true);
        }

        // Updates Radio Values for Sound Selection, to current selected sound
        if(alertSound.equalsIgnoreCase("sound1"))
        {
            sound1b.setChecked(true);
        }
        else if(alertSound.equalsIgnoreCase("sound2")){

            sound2b.setChecked(true);
        }
        else if(alertSound.equalsIgnoreCase("sound3")) {
            sound3b.setChecked(true);
        }
        else if(alertSound.equalsIgnoreCase("sound4") && DoesRecordingExist(pathOfAudio)==true)
        {
            sound4b.setVisibility(View.VISIBLE);
            sound4b.setChecked(true);
        }



       // shw(String.valueOf(DoesRecordingExist(pathOfAudio)));

        // Update Switch for PLay OFF or ON
       if(alertPlayOnOff==true){
           mySwitch.setChecked(true);
           soundRadioEnable(true);
           secRadioEnable(true);
           AlertRadioEnable(true);
       } else{
           mySwitch.setChecked(false);
           soundRadioEnable(false);
           secRadioEnable(false);
           AlertRadioEnable(false);
       }

       // Update Time For Radio Buttons using current shared preferences
        if(durationSong==10){
            sec10b.setChecked(true);
        }
        else if (durationSong==20){
            sec20b.setChecked(true);
        }
        else if (durationSong==30){
            sec30b.setChecked(true);
        }


        super.onResume();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        //set color according to selected RadioButton
        int id = group.getId();

        if (R.id.selectAlertRG == id) {
            switch (checkedId) {
                case R.id.callAlert:
                    alertSelect = "call";

                    break;
                case R.id.smsAlert:
                    alertSelect = "sms";

                    break;
                case R.id.smsEmailAlert:
                    alertSelect = "smsEmail";

                    break;
                case R.id.EmailAlert:
                    alertSelect = "Email";
                    break;
            }
        }

        else if (R.id.selectAlertBabyRG == id) {
            switch (checkedId) {
                case R.id.sound1:
                    alertSound = "sound1";

                    break;
                case R.id.sound2:
                    alertSound = "sound2";

                    break;
                case R.id.sound3:
                    alertSound = "sound3";

                    break;
                case R.id.sound4:
                    alertSound = "sound4";
                    break;
            }
        }

        else if (R.id.selectSongDurationRG == id) {
            switch (checkedId) {
                case R.id.sec10:
                    durationSong = 10;

                    break;
                case R.id.sec20:
                    durationSong = 20;

                    break;
                case R.id.sec30:
                    durationSong = 30;
                    break;

            }
        }

        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("alertType", alertSelect);
        editor.putString("alertSound", alertSound);
        editor.putInt("durationSong", durationSong);
        editor.commit();

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //Toast.makeText(getBaseContext(), "False", Toast.LENGTH_SHORT);
        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        if(buttonView.getId()==R.id.PlaySongSwitch){
            if(isChecked==true){
                alertPlayOnOff = true;
                editor.putBoolean("alertPlay", alertPlayOnOff);
                soundRadioEnable(true);
                secRadioEnable(true);
                AlertRadioEnable(true);


                //Toast.makeText(getBaseContext(),"TRUE",Toast.LENGTH_SHORT).show();
            } else {
                alertPlayOnOff = false;
                soundRadioEnable(false);
                secRadioEnable(false);
                AlertRadioEnable(false);
                editor.putBoolean("alertPlay", alertPlayOnOff);
                //Toast.makeText(getBaseContext(), "False", Toast.LENGTH_SHORT).show();
            }
            editor.commit();
        }

        Toast.makeText(getBaseContext(),String.valueOf(sharedPrefs.getBoolean("alertPlay", false)),Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onPause() {
        String msg =
                "Alert Type" + " " + sharedPrefs.getString("alertType", "sound0") + "\n"+
                        "Alert Sound" + " " + sharedPrefs.getString("alertSound", "sound0") + "\n"+
                        "Play Music" + " " + sharedPrefs.getBoolean("alertPlay", false) + "\n"+
                "Play for" + " " + sharedPrefs.getInt("durationSong", 10) + "\n";

        Toast.makeText(getBaseContext(),msg,Toast.LENGTH_LONG).show();
        super.onPause();


    }


    public void recordAudio(){
        File SDCardpath = getFilesDir();
        File myDataPath = new File(SDCardpath.getAbsolutePath() + "/.My Recordings");

        // mydir = context.getDir("media", Context.MODE_PRIVATE);
        if (!myDataPath.exists()){
            myDataPath.mkdir();
        }

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(pathOfAudio);
        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void stopRecord(){
        recorder.stop();
        recorder.reset();
        recorder.release();
        //shw("IT " + pathOfAudio + " ..... \n" + String.valueOf(DoesRecordingExist(pathOfAudio)));
    }



    public void initializeRecordingFile(){
        SharedPreferences sharedPrefsUN = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String username = sharedPrefsUN.getString("name", "nnastili");

        File SDCardpath = getFilesDir();
        File myDataPath = new File(SDCardpath.getAbsolutePath() + "/.My Recordings");

        // mydir = context.getDir("media", Context.MODE_PRIVATE);
        if (!myDataPath.exists()){
            myDataPath.mkdir();
        }
        pathOfAudio = myDataPath + "/" + username+".m4a";
        Toast.makeText(this,pathOfAudio,Toast.LENGTH_SHORT).show();
    }


    public boolean DoesRecordingExist(String pathFile){
        File file = new File(pathFile);
        return file.exists();
    }

    public void showToast(String m){
        Toast.makeText(getBaseContext(),m,Toast.LENGTH_LONG).show();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(this,"Load Preset "+  spinner.getSelectedItem().toString(),Toast.LENGTH_SHORT).show();
        //Log.d("YOO", "?");
        currentPreset = spinner.getSelectedItem().toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void loadPreset(String preset){
        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);

        String uname = sharedPrefs.getString("name", "Drake");
        String delayPreset = uname + preset + "delay";
        String thresholdPresetName = uname + preset +   "thresholdTime";
        String alertTypePresetName = uname + preset +   "alertType";
        String alertSoundPresetName = uname + preset+   "alertSound";
        String alertPlayPresetName = uname + preset +   "alertPlay";
        String alertDurationPresetName = uname + preset + "durationSong";

//        sharedPrefs.getInt(delayPreset, 1000);
//        sharedPrefs.getInt(thresholdPresetName, 5000);
//        sharedPrefs.getString(alertTypePresetName, "call");
//        sharedPrefs.getString(alertSoundPresetName, "sound0");
//        sharedPrefs.getBoolean(alertPlayPresetName, false);
//        sharedPrefs.getInt(alertDurationPresetName, 10);


        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt("delay", sharedPrefs.getInt(delayPreset, 1000));
        editor.putInt("thresholdTime", sharedPrefs.getInt(thresholdPresetName, 5000));
        editor.putString("alertType", sharedPrefs.getString(alertTypePresetName, "call"));
        editor.putString("alertSound", sharedPrefs.getString(alertSoundPresetName, "sound0"));
        editor.putBoolean("alertPlay", sharedPrefs.getBoolean(alertPlayPresetName, false));
        editor.putInt("durationSong",sharedPrefs.getInt(alertDurationPresetName, 10));
        editor.commit();


//        sharedPrefs.getInt("delay", 1000);
//        sharedPrefs.getInt("thresholdTime", 5000);
//        sharedPrefs.getString("alertType", "sound0");
//        sharedPrefs.getString("alertSound", "sound0");
//        sharedPrefs.getBoolean("alertPlay", false);
//        sharedPrefs.getInt("durationSong", 10);

        if(DoesRecordingExist(pathOfAudio)==false){
            sound4b.setVisibility(View.INVISIBLE);
        }

        // Updates Radio Values for Alert Type to Current Values

        if(sharedPrefs.getString(alertTypePresetName, "call").equalsIgnoreCase("call"))
        {
            callb.setChecked(true);
        }
        else if(sharedPrefs.getString(alertTypePresetName, "call").equalsIgnoreCase("sms")){

            smsb.setChecked(true);
        }
        else if(sharedPrefs.getString(alertTypePresetName, "call").equalsIgnoreCase("smsEmail"))
        {
            smsemailb.setChecked(true);
        }
        else if(sharedPrefs.getString(alertTypePresetName, "call").equalsIgnoreCase("Email"))
        {
            emailb.setChecked(true);
        }

        // Updates Radio Values for Sound Selection, to current selected sound
        if(sharedPrefs.getString(alertSoundPresetName, "sound0").equalsIgnoreCase("sound1"))
        {
            sound1b.setChecked(true);
        }
        else if(sharedPrefs.getString(alertSoundPresetName, "sound0").equalsIgnoreCase("sound2")){

            sound2b.setChecked(true);
        }
        else if(sharedPrefs.getString(alertSoundPresetName, "sound0").equalsIgnoreCase("sound3")) {
            sound3b.setChecked(true);
        }
        else if(sharedPrefs.getString(alertSoundPresetName, "sound0").equalsIgnoreCase("sound4") && DoesRecordingExist(pathOfAudio)==true)
        {
            sound4b.setVisibility(View.VISIBLE);
            sound4b.setChecked(true);
        }





        // Update Switch for PLay OFF or ON
        if(sharedPrefs.getBoolean(alertPlayPresetName, false)==true){
            mySwitch.setChecked(true);
            soundRadioEnable(true);
            secRadioEnable(true);
            AlertRadioEnable(true);
        } else{
            mySwitch.setChecked(false);
            soundRadioEnable(false);
            secRadioEnable(false);
            AlertRadioEnable(false);
        }

        // Update Time For Radio Buttons using current shared preferences
        if(sharedPrefs.getInt(alertDurationPresetName, 10)==10){
            sec10b.setChecked(true);
        }
        else if (sharedPrefs.getInt(alertDurationPresetName, 10)==20){
            sec20b.setChecked(true);
        }
        else if (sharedPrefs.getInt(alertDurationPresetName, 10)==30){
            sec30b.setChecked(true);
        }


    }

    public void savedPreset(String preset){
        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        String uname = sharedPrefs.getString("name", "Drake");

        sharedPrefs.getInt("delay", 1000);
        sharedPrefs.getInt("thresholdTime", 5000);
        sharedPrefs.getString("alertType", "sound0");
        sharedPrefs.getString("alertSound", "sound0");
        sharedPrefs.getBoolean("alertPlay", false);
        sharedPrefs.getInt("durationSong", 10);

        String delayPreset = uname + preset + "delay";
        String thresholdPresetName = uname + preset +   "thresholdTime";
        String alertTypePresetName = uname + preset +   "alertType";
        String alertSoundPresetName = uname + preset+   "alertSound";
        String alertPlayPresetName = uname + preset +   "alertPlay";
        String alertDurationPresetName = uname + preset + "durationSong";

        editor.putInt(delayPreset,             sharedPrefs.getInt("delay", 1000));
        editor.putInt(thresholdPresetName, sharedPrefs.getInt("thresholdTime", 5000));
        editor.putString(alertTypePresetName, sharedPrefs.getString("alertType", "call"));
        editor.putString(alertSoundPresetName, sharedPrefs.getString("alertSound", "sound0"));
        editor.putBoolean(alertPlayPresetName, sharedPrefs.getBoolean("alertPlay", false));
        editor.putInt(alertDurationPresetName, sharedPrefs.getInt("durationSong", 10));
        editor.commit();





        //editor.putString("alertSound", alertSound);
        //editor.putInt("durationSong", durationSong);
        //editor.commit();



//
//        "Alert Type" + " " + sharedPrefs.getString("alertType", "sound0") + "\n"+
//                "Alert Sound" + " " + sharedPrefs.getString("alertSound", "sound0") + "\n"+
//                "Play Music" + " " + sharedPrefs.getBoolean("alertPlay", false) + "\n"+
//                "Play for" + " " + sharedPrefs.getInt("durationSong", 10) + "\n";

    }


    public void soundRadioEnable(Boolean enable){
        if(enable==true) {
            sound1b.setEnabled(enable);
            sound1b.setText(getResources().getString(R.string.sound1));

            sound2b.setEnabled(enable);
            sound2b.setText(getResources().getString(R.string.sound2));

            sound3b.setEnabled(enable);
            sound3b.setText(getResources().getString(R.string.sound3));

            sound4b.setEnabled(enable);
            sound4b.setText(getResources().getString(R.string.sound4));
        } else{
            sound1b.setEnabled(enable);
            sound1b.setText("");

            sound2b.setEnabled(enable);
            sound2b.setText("");

            sound3b.setEnabled(enable);
            sound3b.setText("");

            sound4b.setEnabled(enable);
            sound4b.setText("");
        }
    }

    public void secRadioEnable(Boolean enable){
        if(enable==true) {
            sec10b.setEnabled(enable);
            sec10b.setText(getResources().getString(R.string.seconds10));

            sec20b.setEnabled(enable);
            sec20b.setText(getResources().getString(R.string.seconds20));

            sec30b.setEnabled(enable);
            sec30b.setText(getResources().getString(R.string.seconds30));

        } else{
            sec10b.setEnabled(enable);
            sec10b.setText("");

            sec20b.setEnabled(enable);
            sec20b.setText("");

            sec30b.setEnabled(enable);
            sec30b.setText("");
        }
    }

    public void AlertRadioEnable(Boolean enable){
        if(enable==true) {
            callb.setEnabled(!enable);
            smsb.setEnabled(enable);
            emailb.setEnabled(enable);
            smsemailb.setEnabled(enable);

        } else{
            callb.setEnabled(!enable);
            smsb.setEnabled(!enable);
            emailb.setEnabled(!enable);
            smsemailb.setEnabled(!enable);
        }
    }




    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.savePreset){
            savedPreset(currentPreset);
            // showToast("Preset Saved");
        }
        if(v.getId()==R.id.loadPreset){
            loadPreset(currentPreset);
            // showToast(currentPreset + " Loaded");
        }
    }
}


// http://stackoverflow.com/questions/9389572/improve-audio-recording-quality-in-android
// http://stackoverflow.com/questions/16352081/android-saving-the-audio-files-into-internal-storage