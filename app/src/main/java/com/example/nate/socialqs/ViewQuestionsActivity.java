package com.example.nate.socialqs;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import java.util.HashMap;
import java.util.List;
import android.view.Menu;
import android.view.MenuItem;

/*
This activity is responsible for pulling down QJoins from the cloud, and passing them
off to QuestionAdapter for display.
 */
public class ViewQuestionsActivity extends ActionBarActivity {

    SwipeMenuListView listView;

    //TODO: Unused??
    public static ArrayList<HashMap<String,GroupiesActivity.GroupiesObject>> facebookFinal = new ArrayList<HashMap<String,GroupiesActivity.GroupiesObject>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_questions);


        /*************************************************
         * Inflate the top and bottom menus
         */
        /*Inflate the top menu*/
        ImageView ask = (ImageView) findViewById(R.id.ask);

        ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewQuestionsActivity.this, AskQuestionActivity.class);
                startActivity(intent);
            }
        });
        ImageView settings = (ImageView) findViewById(R.id.settings);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewQuestionsActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        /**/
        /*
        Set up the bottom menu. Again, make this shit modular
         */
        /*Inflate the bottom menu -- put this in a function at some point*/
        ImageView my = (ImageView) findViewById(R.id.myQs);
        ImageView their = (ImageView) findViewById(R.id.theirQs);
        ImageView global = (ImageView) findViewById(R.id.global);
        //Assign the click functionality
        //i_ask is active
        my.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewQuestionsActivity.this, ViewMyQuestionsActivity.class);
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
            }
        });
        /***
         * End menu setup
         * ********************************************************************/

        //TODO: Unneeded?
        //Comletely arbitrary and race condition prone. Can totally bork us.
        //Prepare the facebook data by merging the two lists
        for(int i = 0; i < MainActivity.facebookIds.size(); i++){
            MainActivity.StupidClass stupie = MainActivity.facebookIds.get(i).get("userData");
            String name = stupie.getName();
            String id = stupie.getId();
            for(int j = 0; j < MainActivity.facebookData.size(); j++){
                if(name.equals(MainActivity.facebookData.get(j).get("userData").getName())){
                    MainActivity.facebookData.get(j).get("userData").setId(id);
                    HashMap<String, GroupiesActivity.GroupiesObject> tmp = new HashMap<String, GroupiesActivity.GroupiesObject>();
                    tmp.put("userData",MainActivity.facebookData.get(j).get("userData"));
                    facebookFinal.add(tmp);
                }
            }
        }
        //;;;;


        //Begin querying parse to pull down the questions for this user.
        /*
        The algorithm for this is as such:
        TheirQs means that the asker/from fields are not equal to the current user,
        but the 'to' field is equal to the current user's facebookId.
        We also need to make sure that the question has not yet been deleted
         */

        ParseQuery<ParseObject> vote_query = ParseQuery.getQuery("QJoin");
        vote_query.include("question");
        vote_query.include("from");
        //vote_query.whereEqualTo("to", ParseUser.getCurrentUser().getString("facebookId") );
        vote_query.whereEqualTo("to", ParseUser.getCurrentUser() );
        vote_query.whereNotEqualTo("asker", ParseUser.getCurrentUser());
        vote_query.whereNotEqualTo("deleted",true);
        vote_query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> resList, ParseException e) {
                if (e == null) {

                    Toast.makeText(getApplicationContext(), "Found : " + resList.size(),
                            Toast.LENGTH_LONG).show();
                            if(resList.size() > 0) { //if we have results from the cloud
                                //This builds the open/delete buttons for swipe to delete
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

                                //
                                final QuestionAdapter adapter = new QuestionAdapter(ViewQuestionsActivity.this, resList);
                                listView = (SwipeMenuListView) findViewById(R.id.listView);
                                listView.setAdapter(adapter);
                                // set creator
                                listView.setMenuCreator(creator);
                                listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                                        switch (index) {
                                            case 0:
                                                //TODO: Unbork this code. ViewVotesActivity needs refactored to match new db structure
                                                Intent intent = new Intent(ViewQuestionsActivity.this, ViewVotesActivity.class);
                                                Bundle bundle = new Bundle();
                                                ParseObject tmpQ = (ParseObject) resList.get(position).get("question");
                                                bundle.putString("questionId", tmpQ.getObjectId());
                                                intent.putExtras(bundle);
                                                startActivity(intent);
                                                break;
                                            case 1:
                                                // delete
                                                //hit the server
                                                ParseObject qObj = resList.get(position).getParseObject("question");
                                                ParseObject delQj = resList.get(position); //QJoin
                                                delQj.put("deleted", true);
                                                delQj.saveInBackground();
                                                resList.remove(position);
                                                adapter.notifyDataSetChanged();
                                               // QuestionAdapter tempeh = new QuestionAdapter(ViewQuestionsActivity.this, resList);
                                                listView = (SwipeMenuListView) findViewById(R.id.listView);
                                                listView.setAdapter(adapter);
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
                            } else {
                                //TODO: resList was 0. So either error out or add the orignial welcome question if it is their first time using the app
                            }
                } else {
                    //TODO: Custom error reporting
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
        int id = item.getItemId();

        if (id == R.id.action_left) {
            listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
            return true;
        }
        if (id == R.id.action_right) {
            listView.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
