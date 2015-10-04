package com.example.nate.socialqs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
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
    public static ArrayList<HashMap<String,GroupiesActivity.GroupiesObject>> facebookData = new ArrayList<HashMap<String,GroupiesActivity.GroupiesObject>>();
    public static ArrayList<HashMap<String,MainActivity.StupidClass>> facebookIds = new ArrayList<HashMap<String,MainActivity.StupidClass>>(); //used for  holding ids that we pull from /me/friends
    private Dialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //load up the proper xml screen

        /*
        TODO: Move all of this into a bootstrap activity.
        Initialize all of the things
         */
        //Parse.enableLocalDatastore(getApplicationContext());
       // ParseCrashReporting.enable(getApplicationContext());
        FacebookSdk.sdkInitialize(getApplicationContext());
        Parse.initialize(this, "RMtJAmKZBf5qwRZ9UvbZmTOETF2xZv9FSgYpXrFw", "TZXgbmdUVzHuKRh7z1U3luPO43EDvCwreeNNPMKk");
        ParseFacebookUtils.initialize(this);
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true);
        //image loader??
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).build();
        ImageLoader.getInstance().init(config);

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

        //let's pull the facebook data
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                                    /* handle the result */

                        //let's try to handle this and extract the name and profile pic:
                        try {
                           JSONArray data = response.getJSONObject().getJSONArray("data");
                            for(int i = 0; i < data.length(); i++) {
                                JSONObject vals = data.getJSONObject(i);
                                String id = vals.getString("id");
                                String name = vals.getString("name");
                                StupidClass stupie = new StupidClass(name,id);
                                HashMap<String,StupidClass> temp = new HashMap<String, StupidClass>();
                                temp.put("userData",stupie);
                                facebookIds.add(temp);
                            }

                        }catch(JSONException e){
                            Toast.makeText(getApplicationContext(), "LOSING THE GAME " + e,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
        ).executeAsync();
        //::::
        //get from taggable as well
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/taggable_friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                                    /* handle the result */

                        //let's try to handle this and extract the name and profile pic:
                        try {

                            JSONArray data = response.getJSONObject().getJSONArray("data");
                            for(int i = 0; i < data.length(); i++) {
                                JSONObject pPic = data.getJSONObject(i);
                                String id = pPic.getString("id");
                                Log.d("DS",""+pPic);
                                String pic = pPic.getString("picture");
                                JSONObject pics = pPic.getJSONObject("picture");
                                final String uName  = pPic.getString("name");
                                JSONObject datum = pics.getJSONObject("data");
                                final String ur = datum.getString("url");

                                //maven
                                ImageLoader imageLoader = ImageLoader.getInstance();
                                imageLoader.loadImage(ur, new SimpleImageLoadingListener() {
                                    @Override
                                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                        // Do whatever you want with Bitmap
                                        HashMap<String,GroupiesActivity.GroupiesObject> tempObj = new HashMap<String, GroupiesActivity.GroupiesObject>();
                                        GroupiesActivity.GroupiesObject tempGroupie = new GroupiesActivity.GroupiesObject(uName,0,"objecid","facebook",loadedImage);
                                        tempObj.put("userData",tempGroupie);
                                        facebookData.add(tempObj);
                                    }
                                });
                                //nevam
                            }

                        }catch(JSONException e){
                            Toast.makeText(getApplicationContext(), "LOSING THE GAME " + e,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
        ).executeAsync();
        //;;;;;;;;;;;;;;;;;;


        final ParseUser currentUser = ParseUser.getCurrentUser();
        if((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)){
            //Go directly to the user info activity
            //First time only, execute a graph request to collect the users id if it is not already present
            //in the database
            if(currentUser.getString("facebookId") == null || currentUser.getString("facebookId").length() < 1){
                //hit the db and store this
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/me",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                    /* handle the result */

                                //let's try to handle this and extract the name and profile pic:
                                try {

                                    JSONObject data = response.getJSONObject();
                                    String fbId = data.getString("id");
                                    currentUser.put("facebookId",fbId);
                                    currentUser.saveInBackground();

                                }catch(JSONException e){
                                    Toast.makeText(getApplicationContext(), "LOSING THE GAME " + e,
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                ).executeAsync();
            }
            //>><><<><><><><><><><><><><><><><><><>><><<
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

    //custom code for returning a Bitmap from a URL
    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        Bitmap bmImage;

        public DownloadImageTask(Bitmap bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage = result;
        }
    }

    public static class StupidClass{
        String name;
        String id;
        public StupidClass(String n, String i){
            this.name = n;
            this.id = i;
        }
        public String getName(){
            return this.name;
        }
        public String getId(){
            return this.id;
        }
    }
}
