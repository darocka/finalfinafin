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
import android.widget.TextView;
import android.widget.Toast;

public class LogoutActivity extends Activity implements OnClickListener {

    String str_getName, str_getPassword;


    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.logout);

		/*
		 * fetching the all data corresponding to each key from shared
		 * preference in order to show the user profile
		 */

        str_getName = Splash_Screen.sh.getString("name", null);
        str_getPassword = Splash_Screen.sh.getString("password", null);



        logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(this);

		/* set fetch data to textview, textview show the user complete profile */


    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

		/*
		 * logout button click does not delete the shared preference all data
		 * but it only delete the user login test key, you can do the login
		 * again with same credentials. In order to delete the complete data
		 * call editor.clear(); followed by editor.commit(); you can edit your
		 * complete profile by doing registration once again, it will overwrite
		 * your previous data.
		 */
        Toast.makeText(getApplicationContext(), "You have successfully logout",
                Toast.LENGTH_LONG).show();
        Splash_Screen.editor.remove("loginTest");

        Splash_Screen.editor.commit();

        Intent sendToLoginandRegistration = new Intent(getApplicationContext(),
                Login.class);

        startActivity(sendToLoginandRegistration);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(LogoutActivity.this,
                    Splash_Screen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }
}