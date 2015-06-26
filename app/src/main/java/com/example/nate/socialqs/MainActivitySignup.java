package com.example.nate.socialqs;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.*;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class MainActivitySignup extends ActionBarActivity {
    Button _signupBtn;
    EditText _username;
    EditText _password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_signup);
        _signupBtn = ( Button ) findViewById(R.id.btn_signup);
        _username = ( EditText ) findViewById(R.id.fld_username);
        _password = ( EditText ) findViewById(R.id.fld_pwd);
        Parse.initialize(this, "unu1viESNa3YMwTEYtG0ZOMzCF2IZXLkPsOTUdjj", "tqp6GFOoP3vhGcHKigZRomZFwSETwQj6uOAiSssA");
        _signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          /*
           Parse Jank
           TODO: Check to make sure that they aren't already logged in...ish??
          */

                //validate input first
                String uname = _username.getText().toString();
                String pword = _password.getText().toString();

                if ((uname == null) || (pword == null)) {
                    //error state

                }

                ParseUser user = new ParseUser();
                user.setUsername(uname);
                user.setPassword(pword);

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getApplicationContext(), "WIN",
                                    Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(MainActivitySignup.this, HomeScreenActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "FKD" + e,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity_signup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
