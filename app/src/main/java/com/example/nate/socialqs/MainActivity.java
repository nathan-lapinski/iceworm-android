package com.example.nate.socialqs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/*
****Author: Nathan Lapinski
* Purpose: The MainActivity is responsible for setting up the parse and facebook(soon) authentication. It is also responsible
* for providing the user with the choice of logging in, signing up for a socialQs account, or signing in/up via facebook(soon).
* A method needs to be added for checking if the user is already logged in, so as not to force a login every time.
 */


public class MainActivity extends ActionBarActivity {

    //testing for global groupies
    public static ArrayList<HashMap<String,GroupiesActivity.GroupiesObject>> myGroupies = new ArrayList<HashMap<String,GroupiesActivity.GroupiesObject>>();
    private Dialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //load up the proper xml screen

        /*
        TODO: Move all of this into a bootstrap activity.
        Initialize all of the things
         */
     //   Parse.enableLocalDatastore(getApplicationContext());
        FacebookSdk.sdkInitialize(getApplicationContext());
        Parse.initialize(this, "RMtJAmKZBf5qwRZ9UvbZmTOETF2xZv9FSgYpXrFw", "TZXgbmdUVzHuKRh7z1U3luPO43EDvCwreeNNPMKk");
        ParseFacebookUtils.initialize(this);
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true);

        //find the buttons

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.nate.socialqs",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }



        ParseUser currentUser = ParseUser.getCurrentUser();
        if((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)){
            //Go directly to the user info activity
            //TODO: This is part of the FB tutorial. Proceed to the AskQuestions activity
            showUserDetailsActivity();
        }

    }//end onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
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

    /*
    private void onLoginUserClicked ()
    {
        final List<String> permissions = new ArrayList<String>();

        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                user = ParseUser.getCurrentUser();
                if (user == null) {
                    Toast.makeText(getApplicationContext(), "User logged out?",
                            Toast.LENGTH_LONG).show();
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Toast.makeText(getApplicationContext(), "New user?", Toast.LENGTH_LONG).show();
                    Log.d("MyApp", "User signed up and logged in through Facebook!");
                    Intent intent = new Intent(MainActivity.this, AskQuestionActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "uhhhh?", Toast.LENGTH_LONG).show();
                    Log.d("MyApp", "User logged in through Facebook!");
                    Intent intent = new Intent(MainActivity.this, AskQuestionActivity.class);
                    startActivity(intent);
                }
            }
        });
    }*/

    public void onLoginClick(View v) {
        progressDialog = ProgressDialog.show(MainActivity.this, "", "Logging in...", true);

        List<String> permissions = Arrays.asList("public_profile", "email", "user_friends");
        // NOTE: for extended permissions, like "user_about_me", your app must be reviewed by the Facebook team
        // (https://developers.facebook.com/docs/facebook-login/permissions/)

        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                progressDialog.dismiss();
                if (user == null) {
                    Log.d("socialQsFacebook", "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Log.d("socialQsFacebook", "User signed up and logged in through Facebook!");
                    showUserDetailsActivity();
                } else {
                    Log.d("socialQsFacebook", "User logged in through Facebook!");
                    showUserDetailsActivity();
                }
            }
        });
    }
    private void showUserDetailsActivity() {
        Intent intent = new Intent(this, UserDetailsActivity.class);
        startActivity(intent);
    }
}
