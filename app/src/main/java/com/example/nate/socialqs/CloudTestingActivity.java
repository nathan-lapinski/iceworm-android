package com.example.nate.socialqs;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.Map;


public class CloudTestingActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_testing);
        Button downloadStats = (Button)findViewById(R.id.downloadStats);
        downloadStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hit the parse cloud with a question
                ParseObject imageObject = new ParseObject("QImages");

                //TODO: Finish building the question object

                final ParseObject statsObject = new ParseObject("Stats");
                final ParseObject questionObject = new ParseObject("SocialQs");
                questionObject.put("questionText","test question");
                questionObject.put("option1Text","test option 1");
                questionObject.put("option2Text","test option 2");
                questionObject.put("option3Text","test option 3");
                questionObject.put("images",imageObject);
                questionObject.put("stats",statsObject);

                statsObject.put("option1Stats",0);
                statsObject.put("option2Stats",4);
                statsObject.put("option3Stats",7);

                questionObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getApplicationContext(), "Success",
                                    Toast.LENGTH_LONG).show();
                            statsObject.put("question",questionObject);
                            statsObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Toast.makeText(getApplicationContext(), "Success2",
                                                Toast.LENGTH_LONG).show();
                                        //test the cloud code
                                        final HashMap<String, Object> params = new HashMap<String, Object>();
                                        params.put("questionId", questionObject.getObjectId());
                                        ParseCloud.callFunctionInBackground("downloadStats", params, new FunctionCallback<Map<String, Object>>() {
                                            public void done(Map<String, Object> mapObject, ParseException e) {
                                                if (e == null) {
                                                    Toast.makeText(getApplicationContext(), "op3 got: " + mapObject.get("option3Stats").toString(), Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Cloud failed " + e ,
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });


                                    } else {
                                        Toast.makeText(getApplicationContext(), "Error Asking Question2: " + e,
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "Error Asking Question: " + e,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cloud_testing, menu);
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
