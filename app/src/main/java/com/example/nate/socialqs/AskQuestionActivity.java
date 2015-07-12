package com.example.nate.socialqs;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;


public class AskQuestionActivity extends ActionBarActivity {

    Button _submit;
    Button _cancel;
    Button _groupies;
    Button _privacy;

    EditText _question;
    EditText _choice1;
    EditText _choice2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question_test);
        /*Inflate the menu -- put this in a function at some point*/
        ImageButton i_ask = (ImageButton) findViewById(R.id.image_button_ask);
        ImageButton i_in = (ImageButton) findViewById(R.id.image_button_incoming);
        ImageButton i_out = (ImageButton) findViewById(R.id.image_button_outgoing);
        ImageButton i_set = (ImageButton) findViewById(R.id.image_button_settings);
        //Assign the click functionality
        //i_ask is active
        i_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AskQuestionActivity.this, ViewMyQuestionsActivity.class);
                startActivity(intent);
            }
        });
        i_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AskQuestionActivity.this, ViewQuestionsActivity.class);
                startActivity(intent);
            }
        });
        i_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AskQuestionActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        /*****************/
        _question = (EditText) findViewById(R.id.fld_question_body);
        _choice1 = (EditText) findViewById(R.id.fld_choice1);
        _choice2 = (EditText) findViewById(R.id.fld_choice2);
        _submit = (Button) findViewById(R.id.btn_ask);
        _cancel = (Button) findViewById(R.id.btn_ask_cancel);
        _groupies = (Button) findViewById(R.id.btn_ask_groupies);
        _privacy = (Button) findViewById(R.id.btn_ask_privacy);


        _submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String q = _question.getText().toString();
                String c1 = _choice1.getText().toString();
                String c2 = _choice2.getText().toString();

                final ParseObject userQuestion = new ParseObject("SocialQs");
                ParseUser currentUser = ParseUser.getCurrentUser();

                if (currentUser != null) {

                } else {
                    // show the signup or login screen
                    Toast.makeText(getApplicationContext(), "User logged out?",
                            Toast.LENGTH_LONG).show();
                }

                if((q.equals("")) || (c1.equals("")) || (c2.equals(""))){
                    Toast.makeText(getApplicationContext(), "Error: One or more required parameters is empty",
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AskQuestionActivity.this, AskQuestionActivity.class);
                    startActivity(intent);
                }

            //    userQuestion.put("asker",currentUser.getUsername());
                userQuestion.put("question", q);
                userQuestion.put("option1", c1);
                userQuestion.put("option2", c2);
                userQuestion.put("stats1",0);
                userQuestion.put("stats2",0);
                userQuestion.put("askername",currentUser.getUsername());
                userQuestion.put("askerId",currentUser.getObjectId());
                //userQuestion.put("privacyOptions",1);

                userQuestion.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {

                            final ParseObject vote = new ParseObject("Votes");
                            vote.saveInBackground(new SaveCallback() {
                                public void done(ParseException e) {
                                    if (e == null) {
                                        //find the user..?
                                        userQuestion.put("votesId", vote.getObjectId());
                                        userQuestion.saveInBackground();
                                        //start the success activity, put this in a callback
                                        Intent intent = new Intent(AskQuestionActivity.this, AskQuestionActivity.class);
                                        startActivity(intent);

                                    } else {
                                        //fail
                                    }
                                }
                            });
                            //not sure if questionObject needs to be saved and requeried first...
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("UserQs");
                            // query.whereNotEqualTo("objectId", ParseUser.getCurrentUser().get("uQId"));
                            query.findInBackground(new FindCallback<ParseObject>() {
                                public void done(List<ParseObject> scoreList, ParseException e) {
                                    if (e == null) {
                                        for (int i = 0; i < scoreList.size(); i++) {
                                            if (scoreList.get(i).getObjectId().equals(ParseUser.getCurrentUser().get("uQId"))) {
                                                scoreList.get(i).addUnique("myQsId", userQuestion.getObjectId());
                                                scoreList.get(i).saveInBackground();
                                            } else {
                                                scoreList.get(i).addUnique("theirQsId", userQuestion.getObjectId());
                                                scoreList.get(i).saveInBackground();
                                            }
                                        }

                                    } else {
                                        Log.d("score", "Error: " + e.getMessage());
                                    }
                                }
                            });

                            /*//now add it to mine...
                            ParseQuery<ParseObject> query2 = ParseQuery.getQuery("UserQs");
                            query.whereEqualTo("objectId", ParseUser.getCurrentUser().get("uQId"));
                            query.findInBackground(new FindCallback<ParseObject>() {
                                public void done(List<ParseObject> scoreList, ParseException e) {
                                    if (e == null) {
                                        scoreList.get(0).addUnique("myQsId",userQuestion.getObjectId());
                                        scoreList.get(0).saveInBackground();
                                    } else {
                                        Log.d("score", "Error: " + e.getMessage());
                                    }
                                }
                            });*/

                        } else {
                            Toast.makeText(getApplicationContext(), "Error Creating User: " + e,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

        _cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Question Cancelled",
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AskQuestionActivity.this, AskQuestionActivity.class);
                startActivity(intent);
            }
        });

        _groupies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Group functionality will be available in a future release",
                        Toast.LENGTH_LONG).show();
            }
        });

        _privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Privacy settings will be available in a future release",
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ask_question, menu);
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
