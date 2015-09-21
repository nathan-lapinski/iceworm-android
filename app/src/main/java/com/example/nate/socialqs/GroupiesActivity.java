package com.example.nate.socialqs;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import org.json.JSONObject;

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
                            "/me/friends?fields=name,id,picture&limit=1000",
                            null,
                            HttpMethod.GET,
                            new GraphRequest.Callback() {
                                public void onCompleted(GraphResponse response) {
                                    /* handle the result */
                                    //JSONObject d = response.getJSONObject();
                                  //  JSONObject a  = d.getJSONObject("data");
                                    Toast.makeText(getApplicationContext(), "mayde it: " + response,
                                            Toast.LENGTH_LONG).show();
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

    public class GroupiesObject {
        private String name;
        private int isSelected;
        private String id;
        private String type;

        public GroupiesObject(String n, int s, String i, String t){
            this.name = n;
            this.isSelected = s;
            this.id = i;
            this.type = t;
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
    }
}
