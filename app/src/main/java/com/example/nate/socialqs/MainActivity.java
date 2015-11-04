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
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    //testing for global groupies
    public static ArrayList<HashMap<String,GroupiesActivity.GroupiesObject>> myGroupies = new ArrayList<HashMap<String,GroupiesActivity.GroupiesObject>>();
    public static ArrayList<HashMap<String,GroupiesActivity.GroupiesObject>> facebookData = new ArrayList<HashMap<String,GroupiesActivity.GroupiesObject>>();
    public static ArrayList<HashMap<String,MainActivity.StupidClass>> facebookIds = new ArrayList<HashMap<String,MainActivity.StupidClass>>(); //used for  holding ids that we pull from /me/friends
    private Dialog progressDialog;
    private ProfilePictureView userProfilePictureView;//hidden
    CallbackManager callbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Parse.enableLocalDatastore(getApplicationContext());
        //ParseCrashReporting.enable(getApplicationContext());
        FacebookSdk.sdkInitialize(getApplicationContext());
        // Parse.initialize(this, "RMtJAmKZBf5qwRZ9UvbZmTOETF2xZv9FSgYpXrFw", "TZXgbmdUVzHuKRh7z1U3luPO43EDvCwreeNNPMKk"); //ham
        Parse.initialize(this, "ihGBifEgzvdXCfGKTcj8BmttbCZL9ZW7ggu3xdUQ", "MXMkHm3TOzttdicWqklYmco4li1NQSoZJJp5aazf"); //dev
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseFacebookUtils.initialize(this);
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true);


        //image loader bootstrapping
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).build();
        ImageLoader.getInstance().init(config);

        //test subscription
        //ParsePush.subscribeInBackground("AndroidQs");
        setContentView(R.layout.activity_main); //load up the proper xml screen
        //subscribe the user to channel
        //TODO: This can be moved into the splash screen or something.
        PushService.setDefaultPushCallback(this, SettingsActivity.class);


        //set up the packages accordingly
        //TODO: May have to update this for releases
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
            //TODO
        } catch (NoSuchAlgorithmException e) {
            //TODO
        }

        /*
        You don't have to look above here for FB login. This is all just app set up above this point
         */

        /*What is the algorithm for logging in a user??
          1.Check if the current parse user is null?
        */


        /*
        callbackManager = CallbackManager.Factory.create();
        final ParseUser currentUser = ParseUser.getCurrentUser();
        LoginButton buttonLogin = (LoginButton) findViewById(R.id.login_button);
        //buttonLogin.setReadPermissions("user_friends");
        buttonLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //hi
                Toast.makeText(getApplicationContext(), "good" + loginResult.getAccessToken(),
                        Toast.LENGTH_LONG).show();
                //DUMB THINGS
                //do the parse user thing
                ParseUser cUser = ParseUser.getCurrentUser();
                if(cUser.getString("firstName") == null){
                    Toast.makeText(getApplicationContext(), "we have a new user" + loginResult.getAccessToken(),
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "EXISTING USR!" + loginResult.getAccessToken(),
                            Toast.LENGTH_LONG).show();
                }
                //cUser.put("firstName","testingNames");
                //
                //Graph attack!
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/me/friends",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {


                                //let's try to handle this and extract the name and profile pic:
                                try {
                                    JSONArray data = response.getJSONObject().getJSONArray("data");
                                    for(int i = 0; i < data.length(); i++) {
                                        JSONObject vals = data.getJSONObject(i);
                                        String id = vals.getString("id");
                                        String name = vals.getString("name");
                                        MainActivity.StupidClass stupie = new MainActivity.StupidClass(name,id);
                                        HashMap<String,MainActivity.StupidClass> temp = new HashMap<String, MainActivity.StupidClass>();
                                        temp.put("userData", stupie);
                                        Toast.makeText(getApplicationContext(), "good1",
                                                Toast.LENGTH_LONG).show();
                                        facebookIds.add(temp);
                                    }

                                }catch(JSONException e){
                                    Toast.makeText(getApplicationContext(), "" + e,
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


                                //let's try to handle this and extract the name and profile pic:
                                try {

                                    JSONArray data = response.getJSONObject().getJSONArray("data");
                                    for(int i = 0; i < data.length(); i++) {
                                        JSONObject pPic = data.getJSONObject(i);
                                        String id = pPic.getString("id");
                                        Log.d("DS", "" + pPic);
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
                                                HashMap<String, GroupiesActivity.GroupiesObject> tempObj = new HashMap<String, GroupiesActivity.GroupiesObject>();
                                                GroupiesActivity.GroupiesObject tempGroupie = new GroupiesActivity.GroupiesObject(uName, 0, "objecid", "facebook", loadedImage);
                                                tempObj.put("userData", tempGroupie);
                                                Toast.makeText(getApplicationContext(), "good2",
                                                        Toast.LENGTH_LONG).show();
                                                facebookData.add(tempObj);
                                            }
                                        });
                                        //nevam
                                    }

                                }catch(JSONException e){
                                    Toast.makeText(getApplicationContext(), "" + e,
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                ).executeAsync();
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/me",//"/me",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {


                                //let's try to handle this and extract the name and profile pic:
                                try {

                                    JSONObject data = response.getJSONObject();

                                    String fbId = data.getString("id");
                                    currentUser.put("facebookId", fbId);
                                    currentUser.saveInBackground();
                                    Toast.makeText(getApplicationContext(), "part3",
                                            Toast.LENGTH_LONG).show();
                                }catch(JSONException e){
                                    Toast.makeText(getApplicationContext(), "" + e,
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                ).executeAsync();
                //GGGGGG
                //
                Intent intent = new Intent(MainActivity.this, SplashScreenActivity.class);
                startActivity(intent);
            }

            @Override
            public void onCancel() {
                //lo
                Toast.makeText(getApplicationContext(), "cancel" + AccessToken.getCurrentAccessToken(),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException e) {
                //ho
                Toast.makeText(getApplicationContext(), "fuck"+AccessToken.getCurrentAccessToken() ,
                        Toast.LENGTH_LONG).show();
            }
        });*/


        /*
        if((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)){
            //Go directly to the user info activity
            //First time only, execute a graph request to collect the users id if it is not already present
            //in the database
            //if(currentUser.getString("facebookId") == null || currentUser.getString("facebookId").length() < 1 || currentUser.get("profilePicture") == null){

                //let's pull the facebook data
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/me/friends",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {


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
                                    Toast.makeText(getApplicationContext(), "" + e,
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
                                                HashMap<String, GroupiesActivity.GroupiesObject> tempObj = new HashMap<String, GroupiesActivity.GroupiesObject>();
                                                GroupiesActivity.GroupiesObject tempGroupie = new GroupiesActivity.GroupiesObject(uName, 0, "objecid", "facebook", loadedImage);
                                                tempObj.put("userData", tempGroupie);
                                                facebookData.add(tempObj);
                                            }
                                        });
                                        //nevam
                                    }

                                }catch(JSONException e){
                                    Toast.makeText(getApplicationContext(), "" + e,
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                ).executeAsync();
                //;;;;;;;;;;;;;;;;;;
            if(currentUser.getString("facebookId") == null || currentUser.getString("facebookId").length() < 1 || currentUser.get("profilePicture") == null){
                //hit the db and store this
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/me",//"/me",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {


                                //let's try to handle this and extract the name and profile pic:
                               try {

                                    JSONObject data = response.getJSONObject();

                                    String fbId = data.getString("id");
                                    currentUser.put("facebookId", fbId);
                                    currentUser.saveInBackground();

                                }catch(JSONException e){
                                    Toast.makeText(getApplicationContext(), "" + e,
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                ).executeAsync();
            }
            //>><><<><><><><><><><><><><><><><><><>><><<
            //TODO: This is part of the FB tutorial. Proceed to the AskQuestions activity
            //TODO: Figure out if they already has a username
            if(currentUser.getString("username") == null || currentUser.getString("username").length() > 15){
                //they need to enter their username for the firsttime. FB autopopulates this with something greater than lenght 15
                Intent intent = new Intent(MainActivity.this, HandleScreenActivity.class);
                startActivity(intent);
            }else {
                //Currently, only use this activity for testing (especially cloud testing)
                showUserDetailsActivity();
                //Intent intent = new Intent(MainActivity.this, SplashScreenActivity.class);
                //startActivity(intent);
            }
        }*/

    }//end onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //callbackManager.onActivityResult(requestCode, resultCode, data);
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
                    Toast.makeText(getApplicationContext(),"Please click the login again " ,
                            Toast.LENGTH_LONG).show();
                    //TODO: Let's log them out of here then
                    ParseUser.logOut();
                    //TODO: They can simply try to login in again, and it will work, but lets try to make this a little cleaner
                } else if (user.isNew()) {
                    Log.d("socialQsFacebook", "User signed up and logged in through Facebook!");
                    //sign them up here and handle the graph requests?
                    Toast.makeText(getApplicationContext(),"Logging in" ,
                            Toast.LENGTH_LONG).show();
                    //We have a new user object at this point, so lets populate the shizz out of it.
                    //TODO: Signup screen and populate through graph request.
                    Intent intent = new Intent(MainActivity.this,HandleScreenActivity.class);
                    startActivity(intent);
                    //showUserDetailsActivity();//go to the username selection?
                } else {
                    Log.d("socialQsFacebook", "User logged in through Facebook!");
                    Toast.makeText(getApplicationContext(),"Logging in" ,
                            Toast.LENGTH_LONG).show();
                    if(ParseUser.getCurrentUser().get("name") == null){
                        Intent intent = new Intent(MainActivity.this,HandleScreenActivity.class);
                        startActivity(intent);
                    }else {
                        //showUserDetailsActivity();
                        Intent intent = new Intent(MainActivity.this,SplashScreenActivity.class);
                        startActivity(intent);
                    }
                    //go straight to splash screen?
                }
            }
        });
    }
    private void showUserDetailsActivity() {
        Intent intent = new Intent(this, UserDetailsActivity.class);
        //Intent intent = new Intent(this,SplashScreenActivity.class);
        startActivity(intent);
    }

    //custom code for returning a Bitmap from a URL
    /*public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
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
    }*/

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


    /*
    private void makeMeRequest() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        if (jsonObject != null) {
                            JSONObject userProfile = new JSONObject();

                            try {
                                userProfile.put("facebookId", jsonObject.getLong("id"));
                                userProfile.put("name", jsonObject.getString("name"));

                                if (jsonObject.getString("gender") != null)
                                    userProfile.put("gender", jsonObject.getString("gender"));

                                if (jsonObject.getString("email") != null)
                                    userProfile.put("email", jsonObject.getString("email"));

                                // Save the user profile info in a user property
                                ParseUser currentUser = ParseUser.getCurrentUser();
                                currentUser.put("profile", userProfile);
                                currentUser.saveInBackground();

                                // Show the user info
                                updateViewsWithProfileInfo();
                            } catch (JSONException e) {
                                Log.d("sqs",
                                        "Error parsing returned user data. " + e);

                            }
                        } else if (graphResponse.getError() != null) {
                            switch (graphResponse.getError().getCategory()) {
                                case LOGIN_RECOVERABLE:
                                    Log.d("sqs",
                                            "Authentication error: " + graphResponse.getError());
                                    break;

                                case TRANSIENT:
                                    Log.d("sqs",
                                            "Transient error. Try again. " + graphResponse.getError());
                                    break;

                                case OTHER:
                                    Log.d("sqs",
                                            "Some other error: " + graphResponse.getError());
                                    break;
                            }
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,gender,name");
        request.setParameters(parameters);
        request.executeAsync();
    }*/

    /*
    private void updateViewsWithProfileInfo() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser.has("profile")) {
            JSONObject userProfile = currentUser.getJSONObject("profile");
            try {

                if (userProfile.has("facebookId")) {
                    userProfilePictureView.setProfileId(userProfile.getString("facebookId"));
                    ImageView fbImage = ((ImageView) userProfilePictureView.getChildAt(0));
                    Bitmap bitmap = ((BitmapDrawable) fbImage.getDrawable()).getBitmap();
                    if(bitmap == null){

                    }
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    // Compress image to lower quality scale 1 - 100
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] image = stream.toByteArray();
                    ParseFile x = new ParseFile("profilePicture.png", image);
                    currentUser.put("profilePicture.png", x);
                    currentUser.saveInBackground();

                    showUserDetailsActivity();

                } else {
                    // Show the default, blank user profile picture
                    userProfilePictureView.setProfileId(null);
                }


            } catch (JSONException e) {
                Log.d("sqs", "Error parsing saved user data.");
            }
        }
    }*/
}
