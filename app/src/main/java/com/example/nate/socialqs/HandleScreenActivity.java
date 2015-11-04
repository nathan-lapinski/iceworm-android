package com.example.nate.socialqs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.widget.ProfilePictureView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;


public class HandleScreenActivity extends ActionBarActivity {

    private ProfilePictureView userProfilePictureView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_screen);
        userProfilePictureView = (ProfilePictureView) findViewById(R.id.userProfilePicture);
        final EditText name = (EditText) findViewById(R.id.userHandle);
        Button submit = (Button) findViewById(R.id.submitButton);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().length() > 0 && name.getText().toString().length() < 16) {
                    ParseUser.getCurrentUser().put("username", name.getText().toString());
                    //TODO: do a /me request to get the rest, and populate the installation
                    if(ParseUser.getCurrentUser() != null){
                        String channel = "user_" + ParseUser.getCurrentUser().getObjectId();
                        ParsePush.subscribeInBackground(channel);
                    }else {
                        ParsePush.subscribeInBackground("android_test");
                    }

                    //nuevoes rancheros
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
                                            //Let's play the blame game...
                                            new GraphRequest(
                                                    AccessToken.getCurrentAccessToken(),
                                                    "/me",//"/me",
                                                    null,
                                                    HttpMethod.GET,
                                                    new GraphRequest.Callback() {
                                                        public void onCompleted(GraphResponse response) {

                                                            if(response == null){
                                                                int x = 1;
                                                            }
                                                            //let's try to handle this and extract the name and profile pic:
                                                            try {

                                                                JSONObject data = response.getJSONObject();


                                                                //TODO: Sanitize this
                                                                String fbId = data.getString("id");
                                                                String name = data.getString("name");
                                                                /*String[] namae = name.split("\\s+");
                                                                String firstname = namae[0];
                                                                String lastname = namae[namae.length-1];*/
                                                                ParseUser.getCurrentUser().put("facebookId", fbId);
                                                                ParseUser.getCurrentUser().put("firstName",data.getString("first_name"));
                                                                ParseUser.getCurrentUser().put("lastName",data.getString("last_name"));
                                                                ParseUser.getCurrentUser().put("name",name);
                                                                //Process the picture
                                                                //TODO: extract it from the image view
                                                                 ImageView profileImageView = ((ImageView)userProfilePictureView.getChildAt(0));
                                                                Bitmap bitmap  = ((BitmapDrawable)profileImageView.getDrawable()).getBitmap();
                                                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                                                byte[] byteArray = stream.toByteArray();
                                                                ParseFile profile = new ParseFile("profilePicture.png",byteArray);
                                                                ParseUser.getCurrentUser().put("profilePicture",profile);
                                                                ParseUser.getCurrentUser().put("email", data.getString("email"));
                                                                ParseUser.getCurrentUser().saveInBackground();
                                                                ParseInstallation.getCurrentInstallation().put("user", ParseUser.getCurrentUser());
                                                                ParseInstallation.getCurrentInstallation().saveInBackground();
                                                                Intent intent = new Intent(HandleScreenActivity.this, SplashScreenActivity.class);
                                                                startActivity(intent);
                                                                finish();

                                                                //String pic = data.getString("picture");
                                                                /*JSONObject pics = data.getJSONObject("picture");
                                                                JSONObject datum = pics.getJSONObject("data");
                                                                final String ur = datum.getString("url");
                                                                ImageLoader imageLoader = ImageLoader.getInstance();
                                                                imageLoader.loadImage(ur, new SimpleImageLoadingListener() {
                                                                    @Override
                                                                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                                                        // Do whatever you want with Bitmap
                                                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                                                        loadedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                                                        byte[] byteArray = stream.toByteArray();
                                                                        ParseFile profile = new ParseFile("profilePicture.png",byteArray);
                                                                        ParseUser.getCurrentUser().put("profilePicture",profile);
                                                                        ParseUser.getCurrentUser().saveInBackground();
                                                                        Intent intent = new Intent(HandleScreenActivity.this, SplashScreenActivity.class);
                                                                        startActivity(intent);
                                                                        finish();
                                                                    }
                                                                });*/

                                                            }catch(JSONException e){
                                                                Toast.makeText(getApplicationContext(), "" + e,
                                                                        Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    }
                                            ).executeAsync();
                                            //flmdkfdksfnksdnfsdfsd
                                        } catch (JSONException e) {
                                            Log.d("sqs",
                                                    "Error parsing returned user data. " + e);
                                            Toast.makeText(getApplicationContext(), ""+e,
                                                    Toast.LENGTH_LONG).show();
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


                    //vmkdfkdsngdsgsd
                    /*new GraphRequest(
                            AccessToken.getCurrentAccessToken(),
                            "/me",//"/me",
                            null,
                            HttpMethod.GET,
                            new GraphRequest.Callback() {
                                public void onCompleted(GraphResponse response) {

                                    if(response == null){
                                        int x = 1;
                                    }
                                    //let's try to handle this and extract the name and profile pic:
                                    try {

                                        JSONObject data = response.getJSONObject();
                                        Toast.makeText(getApplicationContext(), "Got Data : " + data,
                                                Toast.LENGTH_LONG).show();

                                        //TODO: Sanitize this
                                        String fbId = data.getString("id");
                                        String name = data.getString("name");
                                        String[] namae = name.split("\\s+");
                                        String firstname = namae[0];
                                        String lastname = namae[namae.length-1];
                                        ParseUser.getCurrentUser().put("facebookId", fbId);
                                        ParseUser.getCurrentUser().put("firstName",firstname);
                                        ParseUser.getCurrentUser().put("lastName",lastname);
                                        ParseUser.getCurrentUser().put("name",name);
                                        //Process the picture
                                        //String pic = data.getString("picture");
                                        JSONObject pics = data.getJSONObject("picture");
                                        JSONObject datum = pics.getJSONObject("data");
                                        final String ur = datum.getString("url");
                                        ImageLoader imageLoader = ImageLoader.getInstance();
                                        imageLoader.loadImage(ur, new SimpleImageLoadingListener() {
                                            @Override
                                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                                // Do whatever you want with Bitmap
                                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                                loadedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                                byte[] byteArray = stream.toByteArray();
                                                ParseFile profile = new ParseFile("profilePicture.png",byteArray);
                                                ParseUser.getCurrentUser().put("profilePicture",profile);
                                                ParseUser.getCurrentUser().saveInBackground();
                                                Intent intent = new Intent(HandleScreenActivity.this, SplashScreenActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });

                                    }catch(JSONException e){
                                        Toast.makeText(getApplicationContext(), "" + e,
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                    ).executeAsync();*/

                } else {
                    //error
                    Toast.makeText(getApplicationContext(), "Error: Please enter a valid username",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_handle_screen, menu);
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

    //
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
                                Toast.makeText(getApplicationContext(), ""+e,
                                        Toast.LENGTH_LONG).show();
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
    }

    private void updateViewsWithProfileInfo() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser.has("profile")) {
            JSONObject userProfile = currentUser.getJSONObject("profile");
            try {

                if (userProfile.has("facebookId")) {
                    userProfilePictureView.setProfileId(userProfile.getString("facebookId"));
                    //try the things?
                    /*ImageView fbImage = ((ImageView) userProfilePictureView.getChildAt(0));
                    Bitmap bitmap = ((BitmapDrawable) fbImage.getDrawable()).getBitmap();
                    if(bitmap == null){
                        Toast.makeText(UserDetailsActivity.this, "Error with the bitmap",
                                Toast.LENGTH_SHORT).show();
                    }
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    // Compress image to lower quality scale 1 - 100
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] image = stream.toByteArray();
                    ParseFile x = new ParseFile("profilePicture.png", image);
                    currentUser.put("profilePicture", x);
                    currentUser.saveInBackground();*/

                } else {
                    // Show the default, blank user profile picture
                    userProfilePictureView.setProfileId(null);
                }

            } catch (JSONException e) {
                Log.d("sqs", "Error parsing saved user data.");
            }
        }
    }
}
