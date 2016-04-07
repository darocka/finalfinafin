package com.example.parmiss.finalproject;

/**
 * Created by Parmiss on 08/03/2016.
 */
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import databaseubm.MyDatabase;


public class Login extends Activity implements OnClickListener {
    MyDatabase db;


    String str_UserName, str_Password, str_getID, str_getPass;

    EditText edt_UName, edt_Password;

    Button login;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

		/* fetching the data from shared preference in order to make user login */
		/* data are saved in application through SplashActivity */
		/* only name and password is sufficient to make login */

        // Get DB
        db = new MyDatabase(this);

        Toast.makeText(this,"jj" + db.getStringData(),Toast.LENGTH_SHORT).show();


        //str_getID = Splash_Screen.sh.getString("name", null);
        //str_getPass = Splash_Screen.sh.getString("password", null);



        login = (Button) findViewById(R.id.btn_login);
        edt_UName = (EditText) findViewById(R.id.edt_userName);
        edt_Password = (EditText) findViewById(R.id.edt_password);

        login.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        str_UserName = edt_UName.getText().toString();
        str_Password = edt_Password.getText().toString();

		/* make edittext condition for empty, input etc match */

        if (str_UserName.length() == 0 & str_Password.length() == 0) {
            Toast.makeText(getApplicationContext(),
                    "Please enter your login User Name and Password",
                    Toast.LENGTH_LONG).show();
        } else if (str_UserName.length() == 0) {
            Toast.makeText(getApplicationContext(),
                    "Please enter your User Name", Toast.LENGTH_LONG).show();
        } else if (str_Password.length() == 0) {
            Toast.makeText(getApplicationContext(),
                    "Please enter your Password", Toast.LENGTH_LONG).show();
        }

//        else if (str_getID.matches("") && str_getPass.matches("")) {
//            Toast.makeText(getApplicationContext(),
//                    "Details does not belongs to any account",
//                    Toast.LENGTH_LONG).show();
//        }

        else if (db.DoesuserNameMatchPassword(str_UserName, str_Password)==false) {
            Toast.makeText(getApplicationContext(),
                    "Either login/password is incorrect", Toast.LENGTH_LONG)
                    .show();
        }
        else{

            String[] userData = db.getUserDataStringData(str_UserName);

            updateGlobalUser(userData[0],userData[1],userData[2],userData[3]);

            Toast.makeText(getApplicationContext(),
                   "You have successfuly login", Toast.LENGTH_LONG).show();
            Intent sendtoMenu = new Intent(getApplicationContext(), Menu.class);
            startActivity(sendtoMenu);


//            SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
//            String username = sharedPrefs.getString("name", "drake");
//            String password = sharedPrefs.getString("pwd", "lonely");

//            Toast.makeText(this, username + " " + password, Toast.LENGTH_LONG).show();
//
//            startActivity(sendtoMenu);
        }
//        else if ((str_getID.matches(str_UserName))
//                && (str_getPass.matches(str_Password))) {
//
//			/*
//			 * dont forget to commit after doing the operation with shared
//			 * preference
//			 */
//			/* without commit data will not saved to shared preference */
//            Splash_Screen.editor.putString("loginTest", "true");
//            Splash_Screen.editor.commit();
//
//            Toast.makeText(getApplicationContext(),
//                    "You have successfuly login", Toast.LENGTH_LONG).show();
//
//            Intent sendtoMenu = new Intent(getApplicationContext(),
//                    Menu.class);
//
//            startActivity(sendtoMenu);
//        }

    }

    // on back key press exit the application.

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Login.this, Splash_Screen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

    // Update global user information
    public void updateGlobalUser(String uname,String upass,String uemail,String unum){
        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("name", uname);
        editor.putString("pwd", upass);
        editor.putString("email", uemail);
        editor.putString("mobile", unum);
        // Toast.makeText(this, "FLoat "+String.valueOf(DEFAULT_FontSize), Toast.LENGTH_LONG).show();
        //Toast.makeText(this, "Username and password saved to Preferences", Toast.LENGTH_LONG).show();
        editor.commit();
    }



}
