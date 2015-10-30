package com.example.nate.socialqs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class ViewVotesActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_votes);
        /*Might need to move this code into the actual ViewQuestions Activities, otherwise there might be some ui hiccups*/
        Intent intentExtras = getIntent();
        Bundle extrasBundle = intentExtras.getExtras();
        if(!extrasBundle.isEmpty()){
            if(extrasBundle.containsKey("questionId")) {
                String id = extrasBundle.getString("questionId");
                Toast.makeText(getApplicationContext(), "Got: "+id,
                        Toast.LENGTH_LONG).show();
                //Let's pull the relevant QJoins down. First pull the question. This sucks, make this
                //better by passing the question from the previous activity, or from the local data store
                ParseQuery qQuery = new ParseQuery("SocialQs");
                qQuery.whereEqualTo("objectId",id);
                qQuery.findInBackground(new FindCallback<ParseObject>() {
                    public void done(final List<ParseObject> resList, ParseException e) {
                        if (e == null) {
                            //update the ui...
                            ParseFile q = resList.get(0).getParseFile("questionImageFull");
                            ParseFile o1 = resList.get(0).getParseFile("option1ImageFull");
                            ParseFile o2 = resList.get(0).getParseFile("option2ImageFull");
                            //check if these are null, if not update the image views
                            if(q != null){
                                //draw the image
                                ImageView pPic = (ImageView)findViewById(R.id.questionImage);
                                byte[] bitmapdata = {};
                                try {
                                    bitmapdata = q.getData();
                                } catch(ParseException e1){

                                }
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
                                pPic.setImageBitmap(bitmap);
                            }
                            if(o1 != null){
                                ImageView pPic = (ImageView)findViewById(R.id.option1Image);
                                byte[] bitmapdata = {};
                                try {
                                    bitmapdata = o1.getData();
                                } catch(ParseException e1){

                                }
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
                                pPic.setImageBitmap(bitmap);

                            }
                            if(o2 != null){
                                ImageView pPic = (ImageView)findViewById(R.id.option2Image);
                                byte[] bitmapdata = {};
                                try {
                                    bitmapdata = o2.getData();
                                } catch(ParseException e1){

                                }
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
                                pPic.setImageBitmap(bitmap);

                            }
                            if(resList.get(0).getString("questionText") != null){
                                TextView qt = (TextView)findViewById(R.id.questionText);
                                qt.setText(resList.get(0).getString("questionText"));
                            }
                            if(resList.get(0).getString("option1Text") != null){
                                TextView qt = (TextView)findViewById(R.id.optionOneText);
                                qt.setText(resList.get(0).getString("option1Text"));
                            }
                            if(resList.get(0).getString("option2Text") != null){
                                TextView qt = (TextView)findViewById(R.id.optionTwoText);
                                qt.setText(resList.get(0).getString("option2Text"));
                            }
                                    //found the question, now find the QJoins
                            ParseQuery qjQuery = new ParseQuery("QJoin");
                            qjQuery.whereEqualTo("question",resList.get(0));
                            qjQuery.include("question");//this is dumb
                            qjQuery.include("to"); // get the user obj
                            qjQuery.whereNotEqualTo("vote",0); //update this to use undefined/null
                            qjQuery.findInBackground(new FindCallback<ParseObject>() {
                                public void done(final List<ParseObject> resList, ParseException e) {
                                    if (e == null) {
                                        //throw the qjoins into the list adapter
                                        Toast.makeText(getApplicationContext(), "Got: "+resList.size(),
                                                Toast.LENGTH_LONG).show();
                                        //let's go ahead and load up the adapter
                                        //Create two new lists, based on which vote was cast
                                        List<ParseObject> v1 = new ArrayList<ParseObject>();
                                        List<ParseObject> v2 = new ArrayList<ParseObject>();
                                        for(int i = 0; i < resList.size(); i++){
                                            ParseObject tmp = resList.get(i);
                                            if(tmp.getInt("vote") == 1){
                                                v1.add(resList.get(i));
                                            } else if(tmp.getInt("vote") == 2){
                                                v2.add(resList.get(i));
                                            } else {
                                                //they haven't voted yet
                                            }
                                        }
                                        MyVotesAdapter adapter1 = new MyVotesAdapter(ViewVotesActivity.this, v1);
                                        MyVotesAdapter adapter2 = new MyVotesAdapter(ViewVotesActivity.this, v2);
                                        ListView listView = (ListView) findViewById(R.id.listView1); //update this to a new listview layout!!
                                        listView.setAdapter(adapter1);
                                        ListView listView2 = (ListView) findViewById(R.id.listView2); //update this to a new listview layout!!
                                        listView2.setAdapter(adapter2);
                                    }else{
                                        //probably bad news, no qjoins?? No votes?
                                        Toast.makeText(getApplicationContext(), "Real badlike ",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }});
                        } else {
                        }
                    }
                });
            }else{
                Toast.makeText(getApplicationContext(), "oh shazbot",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "oh shazbot",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_votes, menu);
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
