package com.example.nate.socialqs;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.widget.ProfilePictureView;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class UserDetailsActivity extends Activity {

    private ProfilePictureView userProfilePictureView;
    private TextView userNameView;
    private TextView userGenderView;
    private TextView userEmailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.userdetails);

        userProfilePictureView = (ProfilePictureView) findViewById(R.id.userProfilePicture);
        userNameView = (TextView) findViewById(R.id.userName);
        userGenderView = (TextView) findViewById(R.id.userGender);
        userEmailView = (TextView) findViewById(R.id.userEmail);

        //test
        Button askButton;
        askButton = (Button)findViewById(R.id.goToAsk);
        askButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserDetailsActivity.this, ViewMyQuestionsActivity.class);
                startActivity(intent);
            }
        });
        //

        //Fetch Facebook user info if it is logged
        ParseUser currentUser = ParseUser.getCurrentUser();
        if ((currentUser != null) && currentUser.isAuthenticated()) {
            makeMeRequest();
        }

        Button testLaunch = (Button) findViewById(R.id.testApp);
        testLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserDetailsActivity.this, CloudTestingActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // Check if the user is currently logged
            // and show any cached content
            updateViewsWithProfileInfo();
        } else {
            // If the user is not logged in, go to the
            // activity showing the login view.
            startLoginActivity();
        }
    }

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

                                Toast.makeText(getApplicationContext(), "GOT HERE WITH: " + jsonObject.getString("name"),
                                        Toast.LENGTH_LONG).show();

                                // Show the user info
                                updateViewsWithProfileInfo();
                            } catch (JSONException e) {
                                Log.d("sqs",
                                        "Error parsing returned user data. " + e);
                                Toast.makeText(getApplicationContext(), "Didn't GET HER DONE: "+e,
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
                    ImageView fbImage = ((ImageView) userProfilePictureView.getChildAt(0));
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
                    currentUser.saveInBackground();

                } else {
                    // Show the default, blank user profile picture
                    userProfilePictureView.setProfileId(null);
                }

                if (userProfile.has("name")) {
                    userNameView.setText(userProfile.getString("name"));
                } else {
                    userNameView.setText("");
                }

                if (userProfile.has("gender")) {
                    userGenderView.setText(userProfile.getString("gender"));
                } else {
                    userGenderView.setText("");
                }

                if (userProfile.has("email")) {
                    userEmailView.setText(userProfile.getString("email"));
                } else {
                    userEmailView.setText("");
                }

            } catch (JSONException e) {
                Log.d("sqs", "Error parsing saved user data.");
            }
        }
    }

    public void onLogoutClick(View v) {
        logout();
    }

    private void logout() {
        // Log the user out
        ParseUser.logOut();

        // Go to the login view
        startLoginActivity();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
