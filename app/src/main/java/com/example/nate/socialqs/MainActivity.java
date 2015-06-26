package com.example.nate.socialqs;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {

    Button _loginBtn;
    Button _signupBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //load up the proper xml screen

      /*
      TODO: CHECK FOR A LOGIN TOKEN TO SEE IF THE USER IS ALREADY LOGGED IN. BYPASS THE REST OF THIS SCREEN IF SO.
      */

        //find the buttons
        _loginBtn = (Button) findViewById(R.id.btn_login);
        _signupBtn = (Button) findViewById(R.id.btn_signup);

        //assign click listeners for these buttons to proceed to the proper screens
        _loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, MainActivityLogin.class);
                startActivity(intent);
            }

        });

        _signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, MainActivitySignup.class);
                startActivity(intent);
            }

        });

    }//end onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
