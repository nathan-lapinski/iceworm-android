package com.example.nate.socialqs;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;


public class CreateGroupActivity extends ActionBarActivity {

    Button cancel;
    Button create;
    EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        cancel = (Button) findViewById(R.id.cancelButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Just go back to the previous screen
                onBackPressed();
            }
        });
        create = (Button) findViewById(R.id.createButton);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the text entered is not empty, go ahead and start to create the database entry.
                name = (EditText)findViewById(R.id.groupNameText);
                final String groupName = name.getText().toString();
                if(groupName != null && !groupName.isEmpty()){
                    final ParseACL acl = new ParseACL();
                    acl.setPublicReadAccess(true);
                    acl.setPublicWriteAccess(true);
                    //update the database
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
                    query.whereEqualTo("objectId",ParseUser.getCurrentUser().getObjectId());
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> objects, ParseException e) {
                            //TODO: Pop out of these automatically after a group is selected.
                            if (e == null) {
                                ParseObject currentRow = objects.get(0);
                                if(currentRow.get("myGroups") != null){
                                    //myGroups exists, so business it
                                    //check to see if this group name already exists for this user though!
                                    currentRow.addAllUnique("myGroups", Arrays.asList(groupName));
                                    currentRow.saveInBackground();
                                    //business him
                                    for(int i = 0; i < GroupiesActivity.myCurrentGroupies.size(); i++){
                                        ParseObject tempGroup = new ParseObject("GroupJoin");
                                        tempGroup.put("groupName",groupName);
                                        tempGroup.put("facebookId",GroupiesActivity.myCurrentGroupies.get(i).get("userData").getId());
                                        tempGroup.put("name",GroupiesActivity.myCurrentGroupies.get(i).get("userData").getName());
                                        tempGroup.put("owner",ParseUser.getCurrentUser());
                                        tempGroup.setACL(acl);
                                        tempGroup.saveInBackground();
                                    }
                                } else {
                                    currentRow.addAllUnique("myGroups", Arrays.asList(groupName));
                                    currentRow.saveInBackground();
                                    //begin creating the groupJoin entries for this one
                                    for(int i = 0; i < GroupiesActivity.myCurrentGroupies.size(); i++){
                                        ParseObject tempGroup = new ParseObject("GroupJoin");
                                        tempGroup.put("groupName",groupName);
                                        tempGroup.put("facebookId",GroupiesActivity.myCurrentGroupies.get(i).get("userData").getId());
                                        tempGroup.put("name",GroupiesActivity.myCurrentGroupies.get(i).get("userData").getName());
                                        tempGroup.put("owner",ParseUser.getCurrentUser());
                                        tempGroup.setACL(acl);
                                        tempGroup.saveInBackground();
                                    }
                                }
                            } else {
                                // error
                            }
                        }
                    });

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_group, menu);
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
