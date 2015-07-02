package com.example.nate.socialqs;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseUser;


public class AskQuestionActivity extends ActionBarActivity {

    Button _submit;
    Button _cancel;
    EditText _question;
    EditText _choice1;
    EditText _choice2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question);

        _question = (EditText) findViewById(R.id.fld_question_body);
        _choice1 = (EditText) findViewById(R.id.fld_choice1);
        _choice2 = (EditText) findViewById(R.id.fld_choice2);
        _submit = (Button) findViewById(R.id.btn_ask);
        _cancel = (Button) findViewById(R.id.btn_ask_cancel);


        _submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String q = _question.getText().toString();
                String c1 = _choice1.getText().toString();
                String c2 = _choice2.getText().toString();
                ParseObject userQuestion = new ParseObject("UserQuestion");
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {

                } else {
                    // show the signup or login screen
                    Toast.makeText(getApplicationContext(), "User logged out?",
                            Toast.LENGTH_LONG).show();
                }
                userQuestion.put("asker",currentUser.getUsername());
                userQuestion.put("question", q);
                userQuestion.put("choice1", c1);
                userQuestion.put("choice2", c2);
                userQuestion.put("choice1_votes",0);
                userQuestion.put("choice2_votes",0);
                userQuestion.put("answered",false);
                userQuestion.saveInBackground();
                Toast.makeText(getApplicationContext(), "Success "+ q + c1 + c2,
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AskQuestionActivity.this, HomeScreenActivity.class);
                startActivity(intent);
            }
        });

        _cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Question Cancelled",
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AskQuestionActivity.this, HomeScreenActivity.class);
                startActivity(intent);
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
