package com.example.parmiss.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Parmiss on 08/03/2016.
 */
public class Menu extends Activity implements View.OnClickListener {
    Button monitor, history, guide, logout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        monitor = (Button) findViewById(R.id.monitor);
        history = (Button) findViewById(R.id.history);
        guide = (Button) findViewById(R.id.guide);
        logout = (Button) findViewById(R.id.logout);

        monitor.setOnClickListener(this);
        history.setOnClickListener(this);
        guide.setOnClickListener(this);
        logout.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.monitor:

                // on login button click send to login activity

                Intent monitoring = new Intent(getApplicationContext(), MoniterActivity.class);
                startActivity(monitoring);

                break;

//            // on register button click send to register activity

            case R.id.history:
                Intent history = new Intent(getApplicationContext(),
                    HistoryActivity.class);

                startActivity(history);
                break;


            case  R.id.logout:

                Intent logout = new Intent(getApplicationContext(),
                        Login.class);

                startActivity(logout);
                break;

        }


    }

}
