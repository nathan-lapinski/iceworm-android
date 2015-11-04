package com.example.nate.socialqs;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class SelectGroupActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group);
        //populate a listview of groups??
        //pull list of this user's groups from the server
        ParseQuery<ParseObject> user_query = ParseQuery.getQuery("_User");
        user_query.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        //.....
        user_query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> resList, ParseException e) {
                if (e == null) {
                    ArrayList<String> myGroups = (ArrayList<String>) resList.get(0).get("myGroups");
                    GroupListAdapter adapter = new GroupListAdapter(SelectGroupActivity.this,myGroups);
                    ListView listView = (ListView) findViewById(R.id.groupList);
                    listView.setAdapter(adapter);
                } else {
                    //There has been an error
                    Toast.makeText(getApplicationContext(), "Error finding it",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_group, menu);
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
