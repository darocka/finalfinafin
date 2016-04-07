package com.example.parmiss.finalproject;

/**
 * Created by Parmiss on 08/03/2016.
 */
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.Toast;

import databaseubm.MyDatabase;

public class Splash_Screen extends Activity {
    MyDatabase db;

    public static String str_login_test;

    public static SharedPreferences sh;
    public static SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        db = new MyDatabase(this);
        db.insertUserToUserTable("G", "1", "nnastili@sfu.ca", "7783163276");
        db.deleteUserFromUserTable("A");


        //Toast.makeText(this,db.getStringData(),Toast.LENGTH_SHORT).show();
        //Toast.makeText(this,db.getStringDataLOG(),Toast.LENGTH_SHORT).show();


        // here initializing the shared preference
        sh = getSharedPreferences("myprefe", 0);
        editor = sh.edit();



        // check here if user is login or not
        str_login_test = sh.getString("loginTest", null);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            return;
        }
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
				/*
				 * if user login test is true on oncreate then redirect the user
				 * to result page
				 */

//                if (str_login_test != null
//                        && !str_login_test.toString().trim().equals("")) {
//                    Intent send = new Intent(getApplicationContext(),
//                          Login_and_registration.class);
//                    startActivity(send);
//                }
				/*
				 * if user login test is false on oncreate then redirect the
				 * user to login & registration page
				 */

                {

                    Intent send = new Intent(getApplicationContext(),
                            Login_and_registration.class);
                    startActivity(send);

                }
            }

        }, 3000);

    }

}