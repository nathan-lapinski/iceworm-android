package com.example.nate.socialqs;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.parse.ParseUser;


public class SettingsActivity extends ActionBarActivity {

    Button _logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        /*Inflate the menu -- put this in a function at some point*/
        ImageButton i_ask = (ImageButton) findViewById(R.id.image_button_ask);
        ImageButton i_in = (ImageButton) findViewById(R.id.image_button_incoming);
        ImageButton i_out = (ImageButton) findViewById(R.id.image_button_outgoing);
        ImageButton i_set = (ImageButton) findViewById(R.id.image_button_settings);
        //Assign the click functionality
        i_ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, AskQuestionActivity.class);
                startActivity(intent);
            }
        });
        i_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ViewMyQuestionsActivity.class);
                startActivity(intent);
            }
        });
        i_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ViewQuestionsActivity.class);
                startActivity(intent);
            }
        });
        /*****************/
        _logout = (Button) findViewById(R.id.btn_logout);
        _logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
