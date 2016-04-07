package com.example.parmiss.finalproject;

/**
 * Created by Parmiss on 08/03/2016.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import databaseubm.MyDatabase;


public class Registration extends Activity implements OnClickListener {
    MyDatabase db;


    Button register;
    String url;

    String str_Name, str_Password, str_RePassword,str_Mobile, str_Email;


    EditText edt_Name, edt_Password, edt_RePassword, edt_Mobile,edt_Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.registration);
        db = new MyDatabase(this);

        register = (Button) findViewById(R.id.btn_register);
        edt_Name = (EditText) findViewById(R.id.edt_Rname);
        edt_Password = (EditText) findViewById(R.id.edt_Rpassword);
        edt_RePassword = (EditText) findViewById(R.id.edt_RRepassword);
        edt_Mobile = (EditText) findViewById(R.id.edt_Rmobile);
        edt_Email = (EditText) findViewById(R.id.edt_Remail);


        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        str_Name = edt_Name.getText().toString();
        str_Password = edt_Password.getText().toString();
        str_RePassword = edt_RePassword.getText().toString();
        str_Mobile = edt_Mobile.getText().toString();
        str_Email =  edt_Email.getText().toString();


        if (str_Name.length() == 0 & str_Password.length() == 0
                & str_RePassword.length() == 0) {
            Toast.makeText(getApplicationContext(),
                    "All fields are mandatory to fill", Toast.LENGTH_LONG)
                    .show();
        } else if (str_Name.length() == 0) {
            Toast.makeText(getApplicationContext(), "Please enter your Name",
                    Toast.LENGTH_LONG).show();
        } else if (str_Password.length() == 0) {
            Toast.makeText(getApplicationContext(),
                    "Please enter your Password", Toast.LENGTH_LONG).show();
        } else if (str_RePassword.length() == 0) {
            Toast.makeText(getApplicationContext(),
                    "Please Re-enter your Password", Toast.LENGTH_LONG).show();
        }


//        else if (str_Password.contains(str_RePassword) != str_RePassword
//                .contains(str_Password)) {
//            Toast.makeText(getApplicationContext(),
//                    "Confirm Password does not match", Toast.LENGTH_LONG)
//                    .show();
//        }

        else if (!(str_Password.equals(str_RePassword))) {
            Toast.makeText(getApplicationContext(),
                    "Confirm Password does not match", Toast.LENGTH_LONG)
                    .show();
        }


        else if (str_Mobile.length() == 0) {

            Toast.makeText(getApplicationContext(),
                    "Please enter your mobile number", Toast.LENGTH_LONG)
                    .show();

        } else if (str_Email.length() == 0) {

            Toast.makeText(getApplicationContext(),
                    "Please enter your Email Address", Toast.LENGTH_LONG)
                    .show();
        }

        else {
            //Splash_Screen.editor.putString("name", str_Name);
            //Splash_Screen.editor.putString("password", str_RePassword);
            //Splash_Screen.editor.putString("mobile", str_Mobile);


            //Splash_Screen.editor.commit();

            db.insertUserToUserTable(str_Name, str_RePassword, str_Email, str_Mobile);

            Intent sendtoLogin = new Intent(getApplicationContext(),
                    Login_and_registration.class);

            Toast.makeText(getApplicationContext(),
                    "You have successfully registered", Toast.LENGTH_LONG)
                    .show();

            startActivity(sendtoLogin);


        }

    }

    // on back key press exit the application.

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Registration.this, Splash_Screen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

}
