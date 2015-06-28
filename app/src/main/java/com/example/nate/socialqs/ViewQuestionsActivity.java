package com.example.nate.socialqs;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class ViewQuestionsActivity extends ActionBarActivity {
    private ListView questionListView;
    private String[] stringArray ;
    private ArrayAdapter questionItemArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_questions);
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {

        } else {
            // show the signup or login screen
            Toast.makeText(getApplicationContext(), "User logged out?",
                    Toast.LENGTH_LONG).show();
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserQuestion");
        query.whereEqualTo("asker", currentUser.getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    Log.d("score", "Retrieved " + scoreList.size() + " thems"); //change to toast
                    QuestionAdapter adapter = new QuestionAdapter(ViewQuestionsActivity.this,scoreList);
                    ListView listView = (ListView) findViewById(R.id.questionList);
                    listView.setAdapter(adapter);

                } else {
                    Log.d("score", "Error: " + e.getMessage());
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
