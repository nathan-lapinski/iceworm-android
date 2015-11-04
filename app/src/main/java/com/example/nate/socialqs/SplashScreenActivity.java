package com.example.nate.socialqs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class SplashScreenActivity extends ActionBarActivity {
    private static int SPLASH_TIME_OUT = 7000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        //keep the splash alive for 4 seconds
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreenActivity.this, ViewMyQuestionsActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
        //--------
        //TODO: Make the timeout dependent on theses graphs
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
                                temp.put("userData",stupie);
                                MainActivity.facebookIds.add(temp);
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
                                        MainActivity.facebookData.add(tempObj);
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
        //
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash_screen, menu);
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
