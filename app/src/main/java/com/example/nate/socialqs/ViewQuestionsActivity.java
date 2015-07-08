package com.example.nate.socialqs;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ViewQuestionsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_questions);

        /*Inflate the menu -- put this in a function at some point*/
        ImageButton i_ask = (ImageButton) findViewById(R.id.image_button_ask);
        ImageButton i_in = (ImageButton) findViewById(R.id.image_button_incoming);
        ImageButton i_out = (ImageButton) findViewById(R.id.image_button_outgoing);
        ImageButton i_set = (ImageButton) findViewById(R.id.image_button_settings);
        //Assign the click functionality
        //i_ask is active
        i_ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewQuestionsActivity.this, AskQuestionActivity.class);
                startActivity(intent);
            }
        });
        i_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewQuestionsActivity.this, ViewMyQuestionsActivity.class);
                startActivity(intent);
            }
        });
        i_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewQuestionsActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        /*****************/

        final ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {

        } else {
            // show the signup or login screen
            Toast.makeText(getApplicationContext(), "User logged out?",
                    Toast.LENGTH_LONG).show();
        }


        ParseQuery<ParseObject> vote_query = ParseQuery.getQuery("UserQs");
        vote_query.whereEqualTo("objectId", currentUser.get("uQId")); //not sure if this works

        vote_query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> resList, ParseException e) {
                if (e == null) {

                    ArrayList<String> votes;
                    if(resList.get(0) == null){
                        votes = new ArrayList<String>();

                    } else {
                        votes = (ArrayList<String>) resList.get(0).get("myQsId"); //was votedOnId
                       /* Toast.makeText(getApplicationContext(), "It's alive!!: " + votes.get(0),
                                Toast.LENGTH_LONG).show();*/
                    }
                    final ArrayList<String> myvotes = votes;
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("SocialQs");
                    query.whereNotEqualTo("askername", currentUser.getUsername());
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> scoreList, ParseException e) {
                            if (e == null) {
                                for(int i = 0; i < scoreList.size(); i++){
                                    if(myvotes == null){//this is the case where the user hasn't voted yet...
                                        scoreList.get(i).put("temp_votes_array",new ArrayList<String>());
                                    }else {
                                        scoreList.get(i).put("temp_votes_array", myvotes);
                                    }
                                }
                                QuestionAdapter adapter = new QuestionAdapter(ViewQuestionsActivity.this,scoreList);
                                ListView listView = (ListView) findViewById(R.id.questionList);
                                listView.setAdapter(adapter);

                            } else {
                                Log.d("score", "Error: " + e.getMessage());
                            }
                        }
                    });
                } else {

                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_questions, menu);
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
