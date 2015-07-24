package com.example.nate.socialqs;

/**
 * Created by nate on 6/27/15.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
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

    //testing this out
    List<ParseObject> master_list;
    public QuestionAdapter(Activity activity, List<ParseObject> data){
        super(activity, R.layout.row_question, data);
        inflater = activity.getWindow().getLayoutInflater();
        this.master_list = data;
    }

    public class MyDeleteListener implements View.OnClickListener
    {
        final ParseObject my_obj;
        int position;
        public MyDeleteListener(ParseObject row, int pos){
            this.my_obj = row;
            this.position = pos;
        }
        @Override
        public void onClick(View v){
            String user_q_id = ParseUser.getCurrentUser().getString("uQId");
            ParseQuery<ParseObject> q = ParseQuery.getQuery("UserQs");
            q.whereEqualTo("objectId", user_q_id);
            q.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> scoreList, ParseException e) {
                    if (e == null) {
                        //remove it from active qs
                        scoreList.get(0).removeAll("theirQsId", Arrays.asList(my_obj.getObjectId()));
                        //add it to deleted qs
                        scoreList.get(0).addAllUnique("deletedTheirQsId", Arrays.asList(my_obj.getObjectId()));
                        scoreList.get(0).saveInBackground();
                    } else {
                        //fail
                    }
                }

            }); // end the async call to update the votes
            //Now let's update the UI by removing this row from the list view
            master_list.remove(position); //should maybe pop it from the array??
            //now to update the actual list.
            notifyDataSetChanged();
            notifyDataSetInvalidated();

        }
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
           /* LinearLayout l1 = (LinearLayout)v.getParent();
            LinearLayout l2 = (LinearLayout)l1.getParent();
            LinearLayout l3 = (LinearLayout)l2.getParent();
            final LinearLayout tl = l2;
            final LinearLayout tl1 = l1;
            final LinearLayout tl3 = l3;*/
            //??


            /**/
            //try to get the parent data

           // TextView tv = (TextView)l2.findViewById(R.id.textViewQuestionText);
           // String q_text = tv.getText().toString();

            //which button was clicked?

            final View j = v;
            switch(v.getId()){
                case R.id.buttonChoice1:
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("SocialQs");
                    //query.whereEqualTo("question",q_text);
                    query.whereEqualTo("question",my_obj.get("question"));
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
                                            x.addAllUnique("votedOn1Id",Arrays.asList(q_id));
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
                               // LayoutInflater convertView;
                                //convertView = (LayoutInflater) j.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View convertView;

                                /*
                                * TODO: Same deal, determine the layout based on the obj parameter.
                                * */
                                if( (obj.get("questionPhoto") != null) || (obj.get("option1Photo") != null) || (obj.get("option2Photo") != null)  ){
            /*
            In this case, we have at least one image. There are 3 templating options to choose from at this point:
            1: No image question, image for one or more options
            2: image question, no image options
            3: image question and one or more image options
             */
                                    if(obj.get("questionPhoto") != null){
                                        //then we know that the question has an image
                                        if( (obj.get("option1Photo") != null) || (obj.get("option2Photo") != null) ){
                                            //image options
                                            //convertView = LayoutInflater.from(getContext()).inflate(R.layout.questions_result_more_images, parent, false);
                                            convertView = (LinearLayout) inflater.inflate(R.layout.questions_result_more_images, null);
                                            TextView q = (TextView)convertView.findViewById(R.id.question_results_text);
                                            TextView c1 = (TextView)convertView.findViewById(R.id.choice1_results_text);
                                            TextView c2 = (TextView)convertView.findViewById(R.id.choice2_results_text);
                                            ImageView v1 = (ImageView)convertView.findViewById(R.id.choice1_results_image);
                                            ImageView v2 = (ImageView)convertView.findViewById(R.id.choice2_results_image);
                                            ImageView q1 = (ImageView)convertView.findViewById(R.id.question_results_image);
                                            int[] results = {0,0};
                                            results = getProgressStats(obj.getInt("stats1"),obj.getInt("stats2"));
                                            q.setText( obj.getString("question"));
                                            c1.setText(obj.getString("option1") + " " + old_votes +"%");
                                            c2.setText(obj.getString("option2") + " " + c2_votes+"%");
                                            if((obj.get("option1Photo") != null)) {
                                                ParseFile image = (ParseFile) obj.getParseFile("option1Photo");
                                                loadImages(image, v1);
                                            }
                                            if((obj.get("option2Photo") != null)) {
                                                ParseFile image = (ParseFile) obj.getParseFile("option2Photo");
                                                loadImages(image, v2);
                                            }
                                            if((obj.get("questionPhoto") != null)) {
                                                ParseFile image = (ParseFile) obj.getParseFile("questionPhoto");
                                                loadImages(image, q1);
                                            }
                                        }else{
                                            //no image options
                                           // convertView = LayoutInflater.from(getContext()).inflate(R.layout.questions_result_one_image, parent, false);
                                            convertView = (LinearLayout) inflater.inflate(R.layout.questions_result_one_image, null);
                                            TextView q = (TextView)convertView.findViewById(R.id.question_results_text);
                                            TextView c1 = (TextView)convertView.findViewById(R.id.choice1_results_text);
                                            TextView c2 = (TextView)convertView.findViewById(R.id.choice2_results_text);
                                            ImageView q1 = (ImageView)convertView.findViewById(R.id.question_results_image);
                                            int[] results = {0,0};
                                            results = getProgressStats(obj.getInt("stats1"),obj.getInt("stats2"));
                                            q.setText( obj.getString("question"));
                                           // c1.setText(obj.getString("option1") + " " + results[0]+"%");
                                           // c2.setText(obj.getString("option2") + " " + results[1]+"%");
                                            c1.setText(obj.getString("option1") + " " + old_votes +"%");
                                            c2.setText(obj.getString("option2") + " " + c2_votes+"%");
                                            if((obj.get("questionPhoto") != null)) {
                                                ParseFile image = (ParseFile) obj.getParseFile("questionPhoto");
                                                loadImages(image, q1);
                                            }
                                        }
                                    } else {
                                        //no question image, but options image(s)
                                        //convertView = LayoutInflater.from(getContext()).inflate(R.layout.questions_results_view_image, parent, false);
                                        convertView = (LinearLayout) inflater.inflate(R.layout.questions_results_view_image, null);
                                        TextView q = (TextView)convertView.findViewById(R.id.question_results_text);
                                        TextView c1 = (TextView)convertView.findViewById(R.id.choice1_results_text);
                                        TextView c2 = (TextView)convertView.findViewById(R.id.choice2_results_text);
                                        ImageView v1 = (ImageView)convertView.findViewById(R.id.choice1_results_image);
                                        ImageView v2 = (ImageView)convertView.findViewById(R.id.choice2_results_image);


                                        int[] results = {0,0};
                                        results = getProgressStats(obj.getInt("stats1"),obj.getInt("stats2"));
                                        q.setText( obj.getString("question"));
                                        c1.setText(obj.getString("option1") + " " + old_votes +"%");
                                        c2.setText(obj.getString("option2") + " " + c2_votes+"%");
                                        if((obj.get("option1Photo") != null)) {
                                            ParseFile image = (ParseFile) obj.getParseFile("option1Photo");
                                            loadImages(image, v1);
                                        }
                                        if((obj.get("option2Photo") != null)) {
                                            ParseFile image = (ParseFile) obj.getParseFile("option2Photo");
                                            loadImages(image, v2);
                                        }
                                    }

                                } else {
                                    //we have no images, so use the default template
                                   // convertView = LayoutInflater.from(getContext()).inflate(R.layout.questions_results_view_test, parent, false);
                                    convertView = (LinearLayout) inflater.inflate(R.layout.questions_results_view_test, null);
                                    TextView q = (TextView)convertView.findViewById(R.id.question_results_text);
                                    TextView c1 = (TextView)convertView.findViewById(R.id.choice1_results_text);
                                    TextView c2 = (TextView)convertView.findViewById(R.id.choice2_results_text);
                                    int[] results = {0,0};
                                    results = getProgressStats(obj.getInt("stats1"),obj.getInt("stats2"));
                                    q.setText( obj.getString("question"));
                                    c1.setText(obj.getString("option1") + " " + old_votes +"%");
                                    c2.setText(obj.getString("option2") + " " + c2_votes+"%");
                                }

                                //????
                               /* LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_results_view, null);
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


                                prog1.setProgress(res[0]);
                                prog2.setProgress(res[1]);
                                //let
                                tl3.addView(layout);
                                tl.refreshDrawableState();
                                tl1.refreshDrawableState();
                                    // p.refreshDrawableState();*/
                            } else {
                                Log.d("score", "Error: " + e.getMessage());
                            }
                        }
                    });
                    break;
                case R.id.buttonChoice2:
                    ParseQuery<ParseObject> query2 = ParseQuery.getQuery("SocialQs");
                   // query2.whereEqualTo("question",q_text);
                    query2.whereEqualTo("question",my_obj.get("question"));
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
                                            x.addAllUnique("votedOn2Id",Arrays.asList(q_id));
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
                                View convertView;
                                //cool, now lets try to replace the existing linear layout with a new one
                                if( (obj.get("questionPhoto") != null) || (obj.get("option1Photo") != null) || (obj.get("option2Photo") != null)  ){
            /*
            In this case, we have at least one image. There are 3 templating options to choose from at this point:
            1: No image question, image for one or more options
            2: image question, no image options
            3: image question and one or more image options
             */
                                    if(obj.get("questionPhoto") != null){
                                        //then we know that the question has an image
                                        if( (obj.get("option1Photo") != null) || (obj.get("option2Photo") != null) ){
                                            //image options
                                            //convertView = LayoutInflater.from(getContext()).inflate(R.layout.questions_result_more_images, parent, false);
                                            convertView = (LinearLayout) inflater.inflate(R.layout.questions_result_more_images, null);
                                            TextView q = (TextView)convertView.findViewById(R.id.question_results_text);
                                            TextView c1 = (TextView)convertView.findViewById(R.id.choice1_results_text);
                                            TextView c2 = (TextView)convertView.findViewById(R.id.choice2_results_text);
                                            ImageView v1 = (ImageView)convertView.findViewById(R.id.choice1_results_image);
                                            ImageView v2 = (ImageView)convertView.findViewById(R.id.choice2_results_image);
                                            ImageView q1 = (ImageView)convertView.findViewById(R.id.question_results_image);
                                            int[] results = {0,0};
                                            results = getProgressStats(obj.getInt("stats1"),obj.getInt("stats2"));
                                            q.setText( obj.getString("question"));
                                            c1.setText(obj.getString("option1") + " " + c2_votes+"%");
                                            c2.setText(obj.getString("option2") + " " + old_votes+"%");
                                            if((obj.get("option1Photo") != null)) {
                                                ParseFile image = (ParseFile) obj.getParseFile("option1Photo");
                                                loadImages(image, v1);
                                            }
                                            if((obj.get("option2Photo") != null)) {
                                                ParseFile image = (ParseFile) obj.getParseFile("option2Photo");
                                                loadImages(image, v2);
                                            }
                                            if((obj.get("questionPhoto") != null)) {
                                                ParseFile image = (ParseFile) obj.getParseFile("questionPhoto");
                                                loadImages(image, q1);
                                            }
                                        }else{
                                            //no image options
                                            // convertView = LayoutInflater.from(getContext()).inflate(R.layout.questions_result_one_image, parent, false);
                                            convertView = (LinearLayout) inflater.inflate(R.layout.questions_result_one_image, null);
                                            TextView q = (TextView)convertView.findViewById(R.id.question_results_text);
                                            TextView c1 = (TextView)convertView.findViewById(R.id.choice1_results_text);
                                            TextView c2 = (TextView)convertView.findViewById(R.id.choice2_results_text);
                                            ImageView q1 = (ImageView)convertView.findViewById(R.id.question_results_image);
                                            int[] results = {0,0};
                                            results = getProgressStats(obj.getInt("stats1"),obj.getInt("stats2"));
                                            q.setText( obj.getString("question"));
                                            c1.setText(obj.getString("option1") + " " + c2_votes+"%");
                                            c2.setText(obj.getString("option2") + " " + old_votes+"%");
                                            if((obj.get("questionPhoto") != null)) {
                                                ParseFile image = (ParseFile) obj.getParseFile("questionPhoto");
                                                loadImages(image, q1);
                                            }
                                        }
                                    } else {
                                        //no question image, but options image(s)
                                        //convertView = LayoutInflater.from(getContext()).inflate(R.layout.questions_results_view_image, parent, false);
                                        convertView = (LinearLayout) inflater.inflate(R.layout.questions_results_view_image, null);
                                        TextView q = (TextView)convertView.findViewById(R.id.question_results_text);
                                        TextView c1 = (TextView)convertView.findViewById(R.id.choice1_results_text);
                                        TextView c2 = (TextView)convertView.findViewById(R.id.choice2_results_text);
                                        ImageView v1 = (ImageView)convertView.findViewById(R.id.choice1_results_image);
                                        ImageView v2 = (ImageView)convertView.findViewById(R.id.choice2_results_image);


                                        int[] results = {0,0};
                                        results = getProgressStats(obj.getInt("stats1"),obj.getInt("stats2"));
                                        q.setText( obj.getString("question"));
                                        c1.setText(obj.getString("option1") + " " + c2_votes+"%");
                                        c2.setText(obj.getString("option2") + " " + old_votes+"%");
                                        if((obj.get("option1Photo") != null)) {
                                            ParseFile image = (ParseFile) obj.getParseFile("option1Photo");
                                            loadImages(image, v1);
                                        }
                                        if((obj.get("option2Photo") != null)) {
                                            ParseFile image = (ParseFile) obj.getParseFile("option2Photo");
                                            loadImages(image, v2);
                                        }
                                    }

                                } else {
                                    //we have no images, so use the default template
                                    // convertView = LayoutInflater.from(getContext()).inflate(R.layout.questions_results_view_test, parent, false);
                                    convertView = (LinearLayout) inflater.inflate(R.layout.questions_results_view_test, null);
                                    TextView q = (TextView)convertView.findViewById(R.id.question_results_text);
                                    TextView c1 = (TextView)convertView.findViewById(R.id.choice1_results_text);
                                    TextView c2 = (TextView)convertView.findViewById(R.id.choice2_results_text);
                                    int[] results = {0,0};
                                    results = getProgressStats(obj.getInt("stats1"),obj.getInt("stats2"));
                                    q.setText( obj.getString("question"));
                                    c1.setText(obj.getString("option1") + " " + c2_votes+"%");
                                    c2.setText(obj.getString("option2") + " " + old_votes+"%");
                                }
                               /* LayoutInflater inflater;
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

                                prog1.setProgress(res[0]);
                                prog2.setProgress(res[1]);
                                //let
                                tl3.addView(layout);
                                tl.refreshDrawableState();
                                tl1.refreshDrawableState();
                                // p.refreshDrawableState();*/
                            } else {
                                Log.d("score", "Error: " + e.getMessage());
                            }
                        }
                    });
                    break;
                default:

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
            //here we are viewing results...so we need to differentiate whether or not it has an image. Check obj for this.
            /*

            NO BUTTONS, ALREADY VOTED ON. JUST FILTER BY IMAGES. This should be the same as for myqs.
             */
            //????
            if( (obj.get("questionPhoto") != null) || (obj.get("option1Photo") != null) || (obj.get("option2Photo") != null)  ){
            /*
            In this case, we have at least one image. There are 3 templating options to choose from at this point:
            1: No image question, image for one or more options
            2: image question, no image options
            3: image question and one or more image options
             */
                if(obj.get("questionPhoto") != null){
                    //then we know that the question has an image
                    if( (obj.get("option1Photo") != null) || (obj.get("option2Photo") != null) ){
                        //image options
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.questions_result_more_images, parent, false);
                        TextView q = (TextView)convertView.findViewById(R.id.question_results_text);
                        TextView c1 = (TextView)convertView.findViewById(R.id.choice1_results_text);
                        TextView c2 = (TextView)convertView.findViewById(R.id.choice2_results_text);
                        ImageView v1 = (ImageView)convertView.findViewById(R.id.choice1_results_image);
                        ImageView v2 = (ImageView)convertView.findViewById(R.id.choice2_results_image);
                        ImageView q1 = (ImageView)convertView.findViewById(R.id.question_results_image);
                        int[] results = {0,0};
                        results = getProgressStats(obj.getInt("stats1"),obj.getInt("stats2"));
                        q.setText( obj.getString("question"));
                        c1.setText(obj.getString("option1") + " " + results[0]+"%");
                        c2.setText(obj.getString("option2") + " " + results[1]+"%");
                        if((obj.get("option1Photo") != null)) {
                            ParseFile image = (ParseFile) obj.getParseFile("option1Photo");
                            loadImages(image, v1);
                        }
                        if((obj.get("option2Photo") != null)) {
                            ParseFile image = (ParseFile) obj.getParseFile("option2Photo");
                            loadImages(image, v2);
                        }
                        if((obj.get("questionPhoto") != null)) {
                            ParseFile image = (ParseFile) obj.getParseFile("questionPhoto");
                            loadImages(image, q1);
                        }
                    }else{
                        //no image options
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.questions_result_one_image, parent, false);
                        TextView q = (TextView)convertView.findViewById(R.id.question_results_text);
                        TextView c1 = (TextView)convertView.findViewById(R.id.choice1_results_text);
                        TextView c2 = (TextView)convertView.findViewById(R.id.choice2_results_text);
                        ImageView q1 = (ImageView)convertView.findViewById(R.id.question_results_image);
                        int[] results = {0,0};
                        results = getProgressStats(obj.getInt("stats1"),obj.getInt("stats2"));
                        q.setText( obj.getString("question"));
                        c1.setText(obj.getString("option1") + " " + results[0]+"%");
                        c2.setText(obj.getString("option2") + " " + results[1]+"%");
                        if((obj.get("questionPhoto") != null)) {
                            ParseFile image = (ParseFile) obj.getParseFile("questionPhoto");
                            loadImages(image, q1);
                        }
                    }
                } else {
                    //no question image, but options image(s)
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.questions_results_view_image, parent, false);
                    TextView q = (TextView)convertView.findViewById(R.id.question_results_text);
                    TextView c1 = (TextView)convertView.findViewById(R.id.choice1_results_text);
                    TextView c2 = (TextView)convertView.findViewById(R.id.choice2_results_text);
                    ImageView v1 = (ImageView)convertView.findViewById(R.id.choice1_results_image);
                    ImageView v2 = (ImageView)convertView.findViewById(R.id.choice2_results_image);


                    int[] results = {0,0};
                    results = getProgressStats(obj.getInt("stats1"),obj.getInt("stats2"));
                    q.setText( obj.getString("question"));
                    c1.setText(obj.getString("option1") + " " + results[0]+"%");
                    c2.setText(obj.getString("option2") + " " + results[1]+"%");
                    if((obj.get("option1Photo") != null)) {
                        ParseFile image = (ParseFile) obj.getParseFile("option1Photo");
                        loadImages(image, v1);
                    }
                    if((obj.get("option2Photo") != null)) {
                        ParseFile image = (ParseFile) obj.getParseFile("option2Photo");
                        loadImages(image, v2);
                    }
                }

            } else {
                //we have no images, so use the default template
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.questions_results_view_test, parent, false);
                TextView q = (TextView)convertView.findViewById(R.id.question_results_text);
                TextView c1 = (TextView)convertView.findViewById(R.id.choice1_results_text);
                TextView c2 = (TextView)convertView.findViewById(R.id.choice2_results_text);
                int[] results = {0,0};
                results = getProgressStats(obj.getInt("stats1"),obj.getInt("stats2"));
                q.setText( obj.getString("question"));
                c1.setText(obj.getString("option1") + " " + results[0]+"%");
                c2.setText(obj.getString("option2") + " " + results[1]+"%");
            }
            //????
            //***************************************************
            /*
            if( (obj.get("questionPhoto") != null) || (obj.get("option1Photo") != null) || (obj.get("option2Photo") != null)  ){
                //then we have at least one image
                Toast.makeText(getContext(), "images",
                        Toast.LENGTH_SHORT).show();
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.questions_results_view_image, parent, false);

            } else {
                //we have no images, so use the default template

                convertView = LayoutInflater.from(getContext()).inflate(R.layout.questions_results_view_test, parent, false);
            }

          //  convertView = LayoutInflater.from(getContext()).inflate(R.layout.question_results_view, parent, false);

            //load the progress bar view here
            //insert the values for the view here
            TextView q = (TextView) convertView.findViewById(R.id.question_results_text);
            TextView c1 = (TextView) convertView.findViewById(R.id.choice1_results_text);
            TextView c2 = (TextView) convertView.findViewById(R.id.choice2_results_text);
            ProgressBar p1 = (ProgressBar) convertView.findViewById(R.id.choice1_results_progress);
            ProgressBar p2 = (ProgressBar) convertView.findViewById(R.id.choice2_results_progress);
            TextView per1 = (TextView)convertView.findViewById(R.id.choice1_percent);
            TextView per2 = (TextView)convertView.findViewById(R.id.choice2_percent);
            ImageButton del = (ImageButton)convertView.findViewById(R.id.btn_delete);
            View.OnClickListener my_del = new MyDeleteListener(obj,position);
            del.setOnClickListener(my_del);
            q.setText(obj.getString("question"));
            c1.setText(obj.getString("option1"));
            c2.setText(obj.getString("option2"));
            int[] results = {0, 0};
            results = getProgressStats(obj.getInt("stats1"), obj.getInt("stats2"));
            p1.setProgress(results[0]);
            p2.setProgress(results[1]);
            per1.setText(results[0]+"%");
            per2.setText(results[1]+"%");
            */
            //****************************************************
        } else {
            // This user has not yet voted on this question, so display buttons.
            /*

            UPDATE THE UIs with buttons based on the format of the question.

            1. Check if images are present. If not, just load the default question template
            2. If images are present, then once again we have 3 options:
            3. Image question, no images in options
            4. Image question, images in options
            5. No image question, images in options.

             */

            if( (obj.get("questionPhoto") != null) || (obj.get("option1Photo") != null) || (obj.get("option2Photo") != null)  ){
                //Cases 2-5
                if( (obj.get("questionPhoto") != null) ){
                    //Then we have an image question
                    if( (obj.get("option1Photo") != null) || (obj.get("option2Photo") != null) ){
                        //Then we have question and option images
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_question_image_q_options_qs, parent, false);
                        TextView question_text = (TextView) convertView.findViewById(R.id.textViewQuestionText);
                        Button choice1 = (Button) convertView.findViewById(R.id.buttonChoice1);
                        Button choice2 = (Button) convertView.findViewById(R.id.buttonChoice2);
                        // ImageButton del = (ImageButton)convertView.findViewById(R.id.btn_delete);
                        // View.OnClickListener my_del = new MyDeleteListener(obj,position);
                        // del.setOnClickListener(my_del);

                        View.OnClickListener my_test = new MyCustomListener(obj);
                        choice1.setOnClickListener(my_test);
                        choice2.setOnClickListener(my_test);
                        // Populate the data into the template view using the data object
                        question_text.setText(obj.getString("question"));
                        choice1.setText(obj.getString("option1"));
                        choice2.setText(obj.getString("option2"));

                        ImageView v1 = (ImageView)convertView.findViewById(R.id.choice1_results_image);
                        ImageView v2 = (ImageView)convertView.findViewById(R.id.choice2_results_image);
                        ImageView q1 = (ImageView)convertView.findViewById(R.id.question_results_image);

                        if((obj.get("option1Photo") != null)) {
                            ParseFile image = (ParseFile) obj.getParseFile("option1Photo");
                            loadImages(image, v1);
                        }
                        if((obj.get("option2Photo") != null)) {
                            ParseFile image = (ParseFile) obj.getParseFile("option2Photo");
                            loadImages(image, v2);
                        }
                        if((obj.get("questionPhoto") != null)) {
                            ParseFile image = (ParseFile) obj.getParseFile("questionPhoto");
                            loadImages(image, q1);
                        }
                    } else {
                        //It was just an image in the question
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_question_image_q, parent, false);
                        TextView question_text = (TextView) convertView.findViewById(R.id.textViewQuestionText);
                        Button choice1 = (Button) convertView.findViewById(R.id.buttonChoice1);
                        Button choice2 = (Button) convertView.findViewById(R.id.buttonChoice2);
                        // ImageButton del = (ImageButton)convertView.findViewById(R.id.btn_delete);
                        // View.OnClickListener my_del = new MyDeleteListener(obj,position);
                        // del.setOnClickListener(my_del);

                        View.OnClickListener my_test = new MyCustomListener(obj);
                        choice1.setOnClickListener(my_test);
                        choice2.setOnClickListener(my_test);
                        // Populate the data into the template view using the data object
                        question_text.setText(obj.getString("question"));
                        choice1.setText(obj.getString("option1"));
                        choice2.setText(obj.getString("option2"));

                        ImageView q1 = (ImageView)convertView.findViewById(R.id.question_results_image);

                        if((obj.get("questionPhoto") != null)) {
                            ParseFile image = (ParseFile) obj.getParseFile("questionPhoto");
                            loadImages(image, q1);
                        }
                    }
                } else {
                    //The question doesn't contain an image, but the options must
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_questions_new_images, parent, false);
                    TextView question_text = (TextView) convertView.findViewById(R.id.textViewQuestionText);
                    Button choice1 = (Button) convertView.findViewById(R.id.buttonChoice1);
                    Button choice2 = (Button) convertView.findViewById(R.id.buttonChoice2);
                    // ImageButton del = (ImageButton)convertView.findViewById(R.id.btn_delete);
                    // View.OnClickListener my_del = new MyDeleteListener(obj,position);
                    // del.setOnClickListener(my_del);

                    View.OnClickListener my_test = new MyCustomListener(obj);
                    choice1.setOnClickListener(my_test);
                    choice2.setOnClickListener(my_test);
                    // Populate the data into the template view using the data object
                    question_text.setText(obj.getString("question"));
                    choice1.setText(obj.getString("option1"));
                    choice2.setText(obj.getString("option2"));

                    ImageView v1 = (ImageView)convertView.findViewById(R.id.choice1_results_image);
                    ImageView v2 = (ImageView)convertView.findViewById(R.id.choice2_results_image);

                    if((obj.get("option1Photo") != null)) {
                        ParseFile image = (ParseFile) obj.getParseFile("option1Photo");
                        loadImages(image, v1);
                    }
                    if((obj.get("option2Photo") != null)) {
                        ParseFile image = (ParseFile) obj.getParseFile("option2Photo");
                        loadImages(image, v2);
                    }

                }

            } else {
                //Cases 1

                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_questions_new, parent, false);

                TextView question_text = (TextView) convertView.findViewById(R.id.textViewQuestionText);
                Button choice1 = (Button) convertView.findViewById(R.id.buttonChoice1);
                Button choice2 = (Button) convertView.findViewById(R.id.buttonChoice2);
               // ImageButton del = (ImageButton)convertView.findViewById(R.id.btn_delete);
               // View.OnClickListener my_del = new MyDeleteListener(obj,position);
               // del.setOnClickListener(my_del);

                View.OnClickListener my_test = new MyCustomListener(obj);
                choice1.setOnClickListener(my_test);
                choice2.setOnClickListener(my_test);
                // Populate the data into the template view using the data object
                question_text.setText(obj.getString("question"));
                choice1.setText(obj.getString("option1"));
                choice2.setText(obj.getString("option2"));
            }

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
    private void loadImages(ParseFile thumbnail, final ImageView img) {

        if (thumbnail != null) {
            thumbnail.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                        img.setImageBitmap(bmp);
                    } else {
                    }
                }
            });
        } else {
            Toast.makeText(getContext(), "Error with the image decoding",
                    Toast.LENGTH_SHORT).show();
        }
    }// load image
}
