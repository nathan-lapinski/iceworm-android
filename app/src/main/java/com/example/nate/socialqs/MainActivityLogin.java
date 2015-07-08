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
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class MainActivityLogin extends ActionBarActivity {

    Button _loginBtn;
    EditText _username;
    EditText _password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_login);
        _loginBtn = ( Button ) findViewById(R.id.btn_login);
        _username = ( EditText ) findViewById(R.id.fld_username);
        _password = ( EditText ) findViewById(R.id.fld_pwd);
        //Parse.initialize(this, "unu1viESNa3YMwTEYtG0ZOMzCF2IZXLkPsOTUdjj", "tqp6GFOoP3vhGcHKigZRomZFwSETwQj6uOAiSssA");
        Parse.initialize(this, "7aEu2aiPHAun7HWnN42hWJ4eQuZueBiHZoGq7GZb", "FU38Qh4hHo0LDGLAQP8PKB8wtjzwhPFGArpwqj7t");
        _loginBtn.setOnClickListener(new View.OnClickListener() {
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

                ParseUser.logInInBackground(uname, pword, new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            // Hooray! The user is logged in.
                            Intent intent = new Intent(MainActivityLogin.this, AskQuestionActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Signup failed. Look at the ParseException to see what happened
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity_login, menu);
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
