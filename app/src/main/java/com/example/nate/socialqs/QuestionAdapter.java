package com.example.nate.socialqs;

/**
 * Created by nate on 6/27/15.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestionAdapter extends ArrayAdapter<ParseObject> {
    private LayoutInflater inflater;


    public QuestionAdapter(Activity activity, List<ParseObject> data){
        super(activity, R.layout.row_question, data);
        inflater = activity.getWindow().getLayoutInflater();
    }

    /*77777*/
    public class MyCustomListener implements View.OnClickListener
    {

        ParseObject my_obj;
        public MyCustomListener(ParseObject row) {
            this.my_obj = row;
        }

        @Override
        public void onClick(View v)
        {
            //read your lovely variable
            //we should be able to access the obj now...
            //??
            LinearLayout l1 = (LinearLayout)v.getParent();
            LinearLayout l2 = (LinearLayout)l1.getParent();
            LinearLayout l3 = (LinearLayout)l2.getParent();
            final LinearLayout tl = l2;
            final LinearLayout tl1 = l1;
            final LinearLayout tl3 = l3;
            //??


            /**/
            //try to get the parent data

            TextView tv = (TextView)l2.findViewById(R.id.textViewQuestionText);
            String q_text = tv.getText().toString();

            //which button was clicked?

            //FIX VOTING TO WORK WITH NEW DATABASE
            final View j = v;
            switch(v.getId()){
                case R.id.buttonChoice1:
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("SocialQs");
                    query.whereEqualTo("question",q_text);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> scoreList, ParseException e) {
                            if (e == null) {
                                //business him!!
                                ParseObject obj = scoreList.get(0); //only one question should be returned. This is the question obj, so we can get it's id to put into the vote table.
                                //this does break if two or more people ask literally the same question..how this handles forwarding is unknown...
                                int old_votes = obj.getInt("stats1");
                                obj.put("stats1", ++old_votes);
                                obj.saveInBackground();

                                //make some async calls to update the db properly
                                String votes_id = obj.getString("votesId");
                                ParseQuery<ParseObject> q2 = ParseQuery.getQuery("Votes");
                                q2.whereEqualTo("objectId",votes_id);
                                q2.findInBackground(new FindCallback<ParseObject>() {
                                    public void done(List<ParseObject> scoreList, ParseException e) {
                                        if (e == null){
                                           ArrayList<String> x =  (ArrayList<String>) scoreList.get(0).get("option1VoterName");
                                           if(x == null){
                                               scoreList.get(0).addAllUnique("option1VoterName",Arrays.asList(ParseUser.getCurrentUser().get("username")));
                                           } else {
                                               scoreList.get(0).addAllUnique("option1VoterName",Arrays.asList(ParseUser.getCurrentUser().get("username")));
                                           }
                                            scoreList.get(0).saveInBackground();
                                        } else {
                                            //fail
                                        }
                                    }

                                }); // end the asyn call to update the votes
                                //The Votes table should be updated correctly at this point

                                //Now, we need to update the userQs table to that this user knows that they have voted on this particular question.
                                //So, get this question's id, get this user's qId, and then in that userQ row, add this questions id to that column
                                final String q_id = obj.getObjectId();
                                String userq_id = ParseUser.getCurrentUser().get("uQId").toString(); //curious what this is??
                                ParseQuery<ParseObject> q3 = ParseQuery.getQuery("UserQs");
                                q3.whereEqualTo("objectId",userq_id);
                                q3.findInBackground(new FindCallback<ParseObject>() {
                                    public void done(List<ParseObject> scoreList, ParseException e) {
                                        if(e == null){
                                            ParseObject x = scoreList.get(0);
                                            x.addAllUnique("myQsId",Arrays.asList(q_id));
                                            x.saveInBackground();
                                        } else {
                                            //failure
                                        }
                                    }
                                });


                                //From here, this should all be UI stuff independent of the DB update. We should also
                                //figure out a better way of batching these db updates.
                                //let's pull down the scores for this question and display them dynamically in textviews.
                                //we already know choice2 votes lol
                                int c2_votes = obj.getInt("stats2");
                                String c1f = "Choice 2 got " + c2_votes + " votes";
                                String c2f = "Choice 1 got " + old_votes + " votes";
                                //cool, now lets try to replace the existing linear layout with a new one
                                LayoutInflater inflater;
                                inflater = (LayoutInflater) j.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                                LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_results_view, null);
                                //at this point, we should have the new layout, so lets try to attach it
                                tl1.removeView(tl1.getChildAt(0));
                                tl3.removeView(tl3.getChildAt(0));
                                TextView question_text = (TextView) layout.findViewById(R.id.question_results_text);
                                TextView inner1 = (TextView) layout.findViewById(R.id.choice1_results_text);
                                TextView inner2 = (TextView) layout.findViewById(R.id.choice2_results_text);
                                ProgressBar prog1 = (ProgressBar) layout.findViewById(R.id.choice1_results_progress);
                                ProgressBar prog2 = (ProgressBar) layout.findViewById(R.id.choice2_results_progress);
                                question_text.setText(obj.getString("question"));
                                inner1.setText(obj.getString("option1"));
                                inner2.setText(obj.getString("option2"));
                                int[] res = getProgressStats(old_votes,c2_votes);
                                Toast.makeText(j.getContext(), "Prog1 is " + res[0] + " prog2 is " + res[1],
                                        Toast.LENGTH_LONG).show();
                                prog1.setProgress(res[0]);
                                prog2.setProgress(res[1]);
                                //let
                                tl3.addView(layout);
                                tl.refreshDrawableState();
                                tl1.refreshDrawableState();
                                // p.refreshDrawableState();
                            } else {
                                Log.d("score", "Error: " + e.getMessage());
                            }
                        }
                    });
                    break;
                case R.id.buttonChoice2:
                    ParseQuery<ParseObject> query2 = ParseQuery.getQuery("SocialQs");
                    query2.whereEqualTo("question",q_text);
                    query2.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> scoreList, ParseException e) {
                            if (e == null) {
                                //business him!!
                                ParseObject obj = scoreList.get(0); //only one question should be returned. This is the question obj, so we can get it's id to put into the vote table.
                                //this does break if two or more people ask literally the same question..how this handles forwarding is unknown...
                                int old_votes = obj.getInt("stats2");
                                obj.put("stats2", ++old_votes);
                                obj.saveInBackground();

                                //make some async calls to update the db properly
                                String votes_id = obj.getString("votesId");
                                ParseQuery<ParseObject> q2 = ParseQuery.getQuery("Votes");
                                q2.whereEqualTo("objectId",votes_id);
                                q2.findInBackground(new FindCallback<ParseObject>() {
                                    public void done(List<ParseObject> scoreList, ParseException e) {
                                        if (e == null){
                                            ArrayList<String> x =  (ArrayList<String>) scoreList.get(0).get("option2VoterName");
                                            if(x == null){
                                                scoreList.get(0).addAllUnique("option2VoterName",Arrays.asList(ParseUser.getCurrentUser().get("username")));
                                            } else {
                                                scoreList.get(0).addAllUnique("option2VoterName",Arrays.asList(ParseUser.getCurrentUser().get("username")));
                                            }
                                            scoreList.get(0).saveInBackground();
                                        } else {
                                            //fail
                                        }
                                    }

                                }); // end the asyn call to update the votes
                                //The Votes table should be updated correctly at this point

                                //Now, we need to update the userQs table to that this user knows that they have voted on this particular question.
                                //So, get this question's id, get this user's qId, and then in that userQ row, add this questions id to that column
                                final String q_id = obj.getObjectId();
                                String userq_id = ParseUser.getCurrentUser().get("uQId").toString(); //curious what this is??
                                ParseQuery<ParseObject> q3 = ParseQuery.getQuery("UserQs");
                                q3.whereEqualTo("objectId",userq_id);
                                q3.findInBackground(new FindCallback<ParseObject>() {
                                    public void done(List<ParseObject> scoreList, ParseException e) {
                                        if(e == null){
                                            ParseObject x = scoreList.get(0);
                                            x.addAllUnique("myQsId",Arrays.asList(q_id));
                                            x.saveInBackground();
                                        } else {
                                            //failure
                                        }
                                    }
                                });


                                //From here, this should all be UI stuff independent of the DB update. We should also
                                //figure out a better way of batching these db updates.
                                //let's pull down the scores for this question and display them dynamically in textviews.
                                //we already know choice2 votes lol
                                int c2_votes = obj.getInt("stats1");
                                String c1f = "Choice 1 got " + c2_votes + " votes";
                                String c2f = "Choice 2 got " + old_votes + " votes";
                                //cool, now lets try to replace the existing linear layout with a new one
                                LayoutInflater inflater;
                                inflater = (LayoutInflater) j.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                                LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_results_view, null);
                                //at this point, we should have the new layout, so lets try to attach it
                                tl1.removeView(tl1.getChildAt(0));
                                tl3.removeView(tl3.getChildAt(0));
                                TextView question_text = (TextView) layout.findViewById(R.id.question_results_text);
                                TextView inner1 = (TextView) layout.findViewById(R.id.choice1_results_text);
                                TextView inner2 = (TextView) layout.findViewById(R.id.choice2_results_text);
                                ProgressBar prog1 = (ProgressBar) layout.findViewById(R.id.choice1_results_progress);
                                ProgressBar prog2 = (ProgressBar) layout.findViewById(R.id.choice2_results_progress);
                                question_text.setText(obj.getString("question"));
                                inner1.setText(obj.getString("option1"));
                                inner2.setText(obj.getString("option2"));
                                int[] res = getProgressStats(c2_votes,old_votes);
                                Toast.makeText(j.getContext(), "Prog1 is " + res[0] + " prog2 is " + res[1],
                                        Toast.LENGTH_LONG).show();
                                prog1.setProgress(res[0]);
                                prog2.setProgress(res[1]);
                                //let
                                tl3.addView(layout);
                                tl.refreshDrawableState();
                                tl1.refreshDrawableState();
                                // p.refreshDrawableState();
                            } else {
                                Log.d("score", "Error: " + e.getMessage());
                            }
                        }
                    });
                    break;
                default:
                    Toast.makeText(v.getContext(), "It broke real badlike",
                            Toast.LENGTH_LONG).show();
            }

        }

    };
    ////////////////////

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final ParseObject obj = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_question, parent, false);
        }

        //this gets the list of votes, if they exist
        ArrayList<String> votes = (ArrayList<String>) obj.get("temp_votes_array");

        boolean is_found = false;


        for(int i = 0; i < votes.size(); i++){

            if(votes.get(i).equals(obj.getObjectId())){
                is_found = true;

            }
        }

        //now we need to look through that array (if it is > 0) and see if this object id is in there. If so,
        //it means we've already voted on it and we need to display the results view.
        if ((votes.size() > 0) && is_found) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.question_results_view, parent, false);
            Toast.makeText(getContext(), "I'm in it!",
                    Toast.LENGTH_LONG).show();
            //load the progress bar view here
            //insert the values for the view here
            TextView q = (TextView) convertView.findViewById(R.id.question_results_text);
            TextView c1 = (TextView) convertView.findViewById(R.id.choice1_results_text);
            TextView c2 = (TextView) convertView.findViewById(R.id.choice2_results_text);
            ProgressBar p1 = (ProgressBar) convertView.findViewById(R.id.choice1_results_progress);
            ProgressBar p2 = (ProgressBar) convertView.findViewById(R.id.choice2_results_progress);
            q.setText(obj.getString("question"));
            c1.setText(obj.getString("option1"));
            c2.setText(obj.getString("option2"));
            int[] results = {0, 0};
            results = getProgressStats(obj.getInt("stats1"), obj.getInt("stats2"));
            p1.setProgress(results[0]);
            p2.setProgress(results[1]);


        } else {
            // This user has not yet voted on this question, so display buttons.
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_question, parent, false);
            TextView question_text = (TextView) convertView.findViewById(R.id.textViewQuestionText);
            Button choice1 = (Button) convertView.findViewById(R.id.buttonChoice1);
            Button choice2 = (Button) convertView.findViewById(R.id.buttonChoice2);

            View.OnClickListener my_test = new MyCustomListener(obj);
            choice1.setOnClickListener(my_test);
            choice2.setOnClickListener(my_test);
            // Populate the data into the template view using the data object
            question_text.setText(obj.getString("question"));
            choice1.setText(obj.getString("option1"));
            choice2.setText(obj.getString("option2"));
        }

        return convertView;
    }







    private int[] getProgressStats(int choice_1_votes, int choice_2_votes){
        int[] results = {0,0}; //set both results to zero, initially.
        //Normalize the results.
        int sum = choice_1_votes + choice_2_votes;
        int v1 = (int)(100.0 * (((float)choice_1_votes)/sum));
        int v2 = (int)(100.0 * (((float)choice_2_votes)/sum));
        results[0] = v1;
        results[1] = v2;
        return results;
    }
}
