package com.example.nate.socialqs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupies);
        /*
        Link up the edittext and the button so that we can search for what the user has typed
         */
        _search = (EditText)findViewById(R.id.userSearchText);
        _submit = (Button)findViewById(R.id.buttonSearch);
        _trueSubmit = (Button)findViewById(R.id.buttonSubmit);

        _submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make sure that the user has actually typed in a search query

                /*
                String searchQuery = _search.getText().toString();
                if( searchQuery != null && searchQuery != ""){
                    ParseUser cur = ParseUser.getCurrentUser();
                    final String my_uname = cur.getString("username");
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("userString", searchQuery);
                    params.put("currentUser", my_uname);
                    //hit the cloud to find the user, or user suggestions
                    ParseCloud.callFunctionInBackground("findNewUser", params, new FunctionCallback<ArrayList<HashMap<String,ParseObject>>>() {
                        public void done(ArrayList<HashMap<String, ParseObject>> names, ParseException e) {
                            if (e == null) {
                                for (int i = 0; i < names.size(); i++) {
                                    HashMap<String,GroupiesObject> tempObj = new HashMap<String, GroupiesObject>();
                                    ParseObject v1 = names.get(i).get("userObject");
                                    GroupiesObject tempGroupie = new GroupiesObject(v1.getString("username"),0,(String)v1.getObjectId(),"SocialQs");
                                    tempObj.put("userData",tempGroupie);
                                    MainActivity.myGroupies.add(tempObj);
                                    Toast.makeText(GroupiesActivity.this, "Could be: " + v1.get("username"),
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(GroupiesActivity.this, "Err" + e,
                                        Toast.LENGTH_SHORT).show();
                            }
                            //update the ui
                            MyGroupiesAdapter adapter = new MyGroupiesAdapter(GroupiesActivity.this, MainActivity.myGroupies);
                            ListView listView = (ListView) findViewById(R.id.questionList2);
                            listView.setAdapter(adapter);
                        }
                    });*/
                    //TODO: Pull facebook friends and display them all here
                if(true){
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
                                    try {

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
                                            /*Toast.makeText(getApplicationContext(), "Pic " + i + " " + ur,
                                                    Toast.LENGTH_LONG).show();

                                            new DownloadImageTask((ImageView) findViewById(R.id.testView))
                                                    .execute(ur);*/
                                            //...
                                            //Bitmap profilePicture = pullImageFromFacebook(ur);

                                            //Let's try this...


                                           /* new AsyncTask<Void, Void, Void>() {
                                                Bitmap bmp;
                                                @Override
                                                protected Void doInBackground(Void... params) {
                                                    try {
                                                        InputStream in = new URL(ur).openStream();
                                                        bmp = BitmapFactory.decodeStream(in);
                                                    } catch (Exception e) {
                                                        // log error
                                                    }
                                                    return null;
                                                }

                                                @Override
                                                protected void onPostExecute(Void result) {
                                                    if (bmp != null){
                                                        //Create the object and write it to the adapter
                                                        HashMap<String,GroupiesObject> tempObj = new HashMap<String, GroupiesObject>();
                                                        GroupiesObject tempGroupie = new GroupiesObject("the username",0,"objecid","facebook",bmp);
                                                        tempObj.put("userData",tempGroupie);
                                                        MainActivity.myGroupies.add(tempObj);
                                                    }else{
                                                        Toast.makeText(getApplicationContext(), "Everything is broken...",
                                                                Toast.LENGTH_LONG).show();
                                                    }

                                                }

                                            }.execute();*/
                                            //:::::::

                                            Log.d("pic",ur);
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
                }
            }
        });
        _trueSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Send the goupies list in an intent to the AskActivity?? Or just
                //let it access it globally and then nukek it?
                onBackPressed();
            }
        });
        //uhhh
        MyGroupiesAdapter adapter = new MyGroupiesAdapter(GroupiesActivity.this, MainActivity.myGroupies);
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

    public class GroupiesObject {
        private String name;
        private int isSelected;
        private String id;
        private String type;
        private String profilePic;

        public GroupiesObject(String n, int s, String i, String t, String b){
            this.name = n;
            this.isSelected = s;
            this.id = i;
            this.type = t;
            this.profilePic = b;
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
