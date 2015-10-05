package com.example.nate.socialqs;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class ViewMyQuestionsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_questions);

        /*Inflate the top menu*/
        ImageView ask = (ImageView) findViewById(R.id.ask);
        ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewMyQuestionsActivity.this, AskQuestionActivity.class);
                startActivity(intent);
            }
        });
        ImageView settings = (ImageView) findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewMyQuestionsActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        /**/

        /*Inflate the bottom menu -- put this in a function at some point*/
        ImageView my = (ImageView) findViewById(R.id.myQs);
        ImageView their = (ImageView) findViewById(R.id.theirQs);
        ImageView global = (ImageView) findViewById(R.id.global);
        //Assign the click functionality
        //i_ask is active
        their.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewMyQuestionsActivity.this, ViewQuestionsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        global.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Global Qs Coming Soon!",
                        Toast.LENGTH_LONG).show();
                //TODO: Implement global Qs
                // Intent intent = new Intent(ViewMyQuestionsActivity.this, SettingsActivity.class);
                // startActivity(intent);
            }
        });
        /*****************/

        ParseUser currentUser = ParseUser.getCurrentUser();
        String currUser = ParseUser.getCurrentUser().getUsername();
        //ParseQuery<ParseObject> vote_query = ParseQuery.getQuery("SocialQs");
        //vote_query.whereEqualTo("asker",ParseUser.getCurrentUser());
        //vote_query.whereNotEqualTo("deleted",true);
        ParseQuery<ParseObject> vote_query = ParseQuery.getQuery("QJoin");
        vote_query.whereEqualTo("asker",ParseUser.getCurrentUser());
        vote_query.whereNotEqualTo("deleted", true);
        vote_query.whereEqualTo("to", ParseUser.getCurrentUser().getString("facebookId"));
        vote_query.include("question");
        vote_query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> resList, ParseException e) {
                if (e == null) {
                    //deleted
                    SwipeMenuCreator creator = new SwipeMenuCreator() {

                        @Override
                        public void create(SwipeMenu menu) {
                            // create "open" item
                            SwipeMenuItem openItem = new SwipeMenuItem(
                                    getApplicationContext());
                            // set item background
                            openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                                    0xCE)));
                            // set item width
                            openItem.setWidth(dp2px(90));
                            // set item title
                            openItem.setTitle("Open");
                            // set item title fontsize
                            openItem.setTitleSize(18);
                            // set item title font color
                            openItem.setTitleColor(Color.WHITE);
                            // add to menu
                            menu.addMenuItem(openItem);

                            // create "delete" item
                            SwipeMenuItem deleteItem = new SwipeMenuItem(
                                    getApplicationContext());
                            // set item background
                            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                                    0x3F, 0x25)));
                            // set item width
                            deleteItem.setWidth(dp2px(90));
                            // set a icon
                            deleteItem.setIcon(R.drawable.ic_delete);
                            // add to menu
                            menu.addMenuItem(deleteItem);
                        }
                    };
                    //Now, create a listview from the R.id.questionList and use it to display the resList data
                    //ListView listView = (ListView) findViewById(R.id.questionList);
                    //TODO: Update reslist here to be the questions??
                    final List<ParseObject> parseList = new ArrayList<ParseObject>();
                    for(int i = 0; i < resList.size(); i++){
                        parseList.add(resList.get(i).getParseObject("question"));
                    }
                    final MyQuestionAdapter adapter = new MyQuestionAdapter(ViewMyQuestionsActivity.this, parseList);//was res
                    //ListView listView = (ListView) findViewById(R.id.questionList2);
                    SwipeMenuListView listView = (SwipeMenuListView) findViewById(R.id.questionList2);
                    // set creator
                    listView.setMenuCreator(creator);
                    listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                            switch (index) {
                                case 0:
                                    // open
                                    //spawn the ViewVotesActivity...pass the SocialQs id in the intent?
                                    Intent intent = new Intent(ViewMyQuestionsActivity.this, ViewVotesActivity.class);
                                    Bundle bundle = new Bundle();
                                    ParseObject currentQuestion = resList.get(position).getParseObject("question");
                                    bundle.putString("questionId",currentQuestion.getObjectId());
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    break;
                                case 1:
                                    // delete
                                    //hit the server
                                    ParseObject sQ = resList.get(position); //QJoin
                                    sQ.put("deleted",true);
                                    sQ.saveInBackground();
                                    resList.remove(position);
                                    parseList.remove(position);
                                    adapter.notifyDataSetChanged();
                                    break;
                            }
                            // false : close the menu; true : not close the menu
                            return false;
                        }
                    });
                    //inside of the adapted, we will say how we want the data do be displayed in the questionList layout
                    // set SwipeListener
                    listView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

                        @Override
                        public void onSwipeStart(int position) {
                            // swipe start
                        }

                        @Override
                        public void onSwipeEnd(int position) {
                            // swipe end
                        }
                    });

                    // set MenuStateChangeListener
                    listView.setOnMenuStateChangeListener(new SwipeMenuListView.OnMenuStateChangeListener() {
                        @Override
                        public void onMenuOpen(int position) {
                        }

                        @Override
                        public void onMenuClose(int position) {
                        }
                    });
                    listView.setAdapter(adapter);

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
        getMenuInflater().inflate(R.menu.menu_view_my_questions, menu);
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

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}