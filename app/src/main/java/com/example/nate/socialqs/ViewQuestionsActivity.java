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

/*
This activity is responsible for pulling down QJoins from the cloud, and passing them
off to QuestionAdapter for display.
 */
public class ViewQuestionsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_questions);

        /*
        Set up the bottom menu. Again, make this shit modular
         */
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
        /*********
         *
         *
         * ********/


        String currentUser = ParseUser.getCurrentUser().getUsername();
        ParseQuery<ParseObject> vote_query = ParseQuery.getQuery("QJoin");
        vote_query.include("question");
        vote_query.whereEqualTo("to", currentUser );
        vote_query.whereNotEqualTo("sender", currentUser);

        vote_query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> resList, ParseException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "Found " + resList.size() + " questions for this user",
                            Toast.LENGTH_LONG).show();
                            if(resList.size() > 0) {
                                QuestionAdapter adapter = new QuestionAdapter(ViewQuestionsActivity.this, resList);
                                //Now, create a listview from the R.id.questionList and use it to display the resList data
                                ListView listView = (ListView) findViewById(R.id.questionList);
                                //inside of the adapted, we will say how we want the data do be displayed in the questionList layout
                                listView.setAdapter(adapter);
                            } else {
                                Toast.makeText(getApplicationContext(), "Found " + resList.size() + " questions for this user",
                                        Toast.LENGTH_LONG).show();
                            }


                } else {
                    //There has been an error
                    Toast.makeText(getApplicationContext(), "Error accessing join table",
                            Toast.LENGTH_LONG).show();
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
