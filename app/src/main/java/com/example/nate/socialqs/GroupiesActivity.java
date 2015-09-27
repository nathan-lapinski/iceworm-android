package com.example.nate.socialqs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class GroupiesActivity extends ActionBarActivity {

    Button _submit;
    EditText _search;
    Button _trueSubmit;
    public static ArrayList<HashMap<String,GroupiesObject>> myCurrentGroupies = new ArrayList<HashMap<String,GroupiesObject>>();
    public static ArrayList<GroupiesObject> mySearchGroupies = new ArrayList<GroupiesObject>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupies);
        /*
        Link up the edittext and the button so that we can search for what the user has typed
         */
        _search = (EditText)findViewById(R.id.userSearchText);
        _search.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //TODO: perform the filters and update the view
                    String searchText = v.getText().toString();
                    if(!searchText.equals(null)){
                        //filter the groupies based on first name
                        for(int i = 0; i < MainActivity.myGroupies.size(); i++){
                            if(MainActivity.myGroupies.get(i).get("userData").getName().toLowerCase().contains(searchText.toLowerCase())){
                                mySearchGroupies.add(MainActivity.myGroupies.get(i).get("userData"));
                            }
                        }
                        Toast.makeText(getApplicationContext(), "Found: "+mySearchGroupies.size(),
                                Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
                return false;
            }
        });
        //TODO: Add this graphrequest somewhere else if possible...
        //this will require you to pull and store the images locally. Otherwise good luck updating
        //the non-existing listview
        //TODO: Pull facebook friends and display them all here
       /* if(true){
            //Hit the graph api to pull down all of this users friends
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/me/taggable_friends",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                                    /* handle the result */

                            //let's try to handle this and extract the name and profile pic:
           /*                 try {

                                JSONArray data = response.getJSONObject().getJSONArray("data");
                                for(int i = 0; i < data.length(); i++){
                                    //check the exact format here. things get a bit janky with the response object and it might actually
                                    //be a picture attr that we need to look at.
                                    JSONObject pPic = data.getJSONObject(i);
                                    //get your values
                                    String pic = pPic.getString("picture");
                                    JSONObject pics = pPic.getJSONObject("picture");
                                    final String uName  = pPic.getString("name");
                                    JSONObject datum = pics.getJSONObject("data");
                                    final String ur = datum.getString("url");
                                    HashMap<String,GroupiesObject> tempObj = new HashMap<String, GroupiesObject>();
                                    GroupiesObject tempGroupie = new GroupiesObject(uName,0,"objecid","facebook",ur);
                                    tempObj.put("userData",tempGroupie);
                                    MainActivity.myGroupies.add(tempObj);
                                    MyGroupiesAdapter adapter = new MyGroupiesAdapter(GroupiesActivity.this, MainActivity.myGroupies);
                                    ListView listView = (ListView) findViewById(R.id.questionList2);
                                    listView.setAdapter(adapter);
                                }

                            }catch(JSONException e){
                                Toast.makeText(getApplicationContext(), "LOSING THE GAME " + e,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
            ).executeAsync();

        }else{
            //issue a warning saying that search queries cannot be empty
            Toast.makeText(getApplicationContext(), "Search queries cannot be empty.",
                    Toast.LENGTH_LONG).show();
        }*/
        //MyGroupiesAdapter adapter = new MyGroupiesAdapter(GroupiesActivity.this, MainActivity.myGroupies);
        MyGroupiesAdapter adapter = new MyGroupiesAdapter(GroupiesActivity.this, MainActivity.facebookData);
        ListView listView = (ListView) findViewById(R.id.questionList2);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_groupies, menu);
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

    public void onDoneClick(View v){
        //return to the ask questions view, as the groupies have been selected
        onBackPressed();
    }

    public Bitmap pullImageFromFacebook(String url){
        String urldisplay = url;
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
           // Log.e("Error", e.getMessage());
           // e.printStackTrace();
            Toast.makeText(getApplicationContext(), ""+e,
                    Toast.LENGTH_LONG).show();
        }
        return mIcon11;
    }

    public static class GroupiesObject {
        private String name;
        private int isSelected;
        private String id;
        private String type;
        private String profilePic;
        private Bitmap profileBitmap;

        public GroupiesObject(String n, int s, String i, String t, String b){
            this.name = n;
            this.isSelected = s;
            this.id = i;
            this.type = t;
            this.profilePic = b;
        }

        public GroupiesObject(String n, int s, String i, String t, Bitmap b){
            this.name = n;
            this.isSelected = s;
            this.id = i;
            this.type = t;
            this.profileBitmap = b;
        }

        public String getName(){
            return name;
        }

        public int getIsSelected(){
            return isSelected;
        }

        public String getId(){
            return id;
        }

        public String getType(){
            return type;
        }

        public String getProfilePic() {return profilePic;}

        public Bitmap getBitmap() {return profileBitmap;}
    }

    //For pulling down fb images directly
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
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
            bmImage.setImageBitmap(result);
        }
    }
}
