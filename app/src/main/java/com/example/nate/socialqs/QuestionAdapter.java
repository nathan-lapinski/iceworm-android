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
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.w3c.dom.Text;

import java.util.List;

public class QuestionAdapter extends ArrayAdapter<ParseObject> {
    private LayoutInflater inflater;
    public QuestionAdapter(Activity activity, List<ParseObject> data){
        super(activity, R.layout.row_question, data);
        inflater = activity.getWindow().getLayoutInflater();
    }

    private View.OnClickListener voteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LinearLayout l1 = (LinearLayout)v.getParent();
            LinearLayout l2 = (LinearLayout)l1.getParent();
            LinearLayout l3 = (LinearLayout)l2.getParent();
            final LinearLayout tl = l2;
            final LinearLayout tl1 = l1;
            final LinearLayout tl3 = l3;


            /**/
            //try to get the parent data

            TextView tv = (TextView)l2.findViewById(R.id.textViewQuestionText);
            String q_text = tv.getText().toString();
            Toast.makeText(v.getContext(), q_text,
                    Toast.LENGTH_LONG).show();
            //which button was clicked?
            final View j = v;
            switch(v.getId()){
                case R.id.buttonChoice1:
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("UserQuestion");
                    query.whereEqualTo("question",q_text);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> scoreList, ParseException e) {
                            if (e == null) {

                                ParseObject obj = scoreList.get(0); //only one
                                int old_votes = obj.getInt("choice1_votes");
                                obj.put("choice1_votes",++old_votes);
                                obj.put("answered",true);
                                obj.saveInBackground();
                                //let's pull down the scores for this question and display them dynamically in textviews.
                                //we already know choice2 votes lol
                                int c2_votes = obj.getInt("choice2_votes");
                                String c1f = "Choice 2 got " + c2_votes + " votes";
                                String c2f = "Choice 1 got " + old_votes + " votes";
                                //cool, now lets try to replace the existing linear layout with a new one
                                LayoutInflater inflater;
                                inflater = (LayoutInflater) j.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                                LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_results_view, null);
                                //at this point, we should have the new layout, so lets try to attach it
                                tl1.removeView(tl1.getChildAt(0));
                                tl3.removeView(tl3.getChildAt(0));
                                TextView inner1 = (TextView) layout.findViewById(R.id.choice1_results_text);
                                TextView inner2 = (TextView) layout.findViewById(R.id.choice2_results_text);
                                ProgressBar prog1 = (ProgressBar) layout.findViewById(R.id.choice1_results_progress);
                                ProgressBar prog2 = (ProgressBar) layout.findViewById(R.id.choice2_results_progress);
                                inner1.setText(obj.getString("choice1"));
                                inner2.setText(obj.getString("choice2"));
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
                    ParseQuery<ParseObject> query2 = ParseQuery.getQuery("UserQuestion");
                    query2.whereEqualTo("question",q_text);
                    query2.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> scoreList, ParseException e) {
                            if (e == null) {

                                ParseObject obj = scoreList.get(0); //only one
                                int old_votes = obj.getInt("choice2_votes");
                                obj.put("choice2_votes", ++old_votes);
                                obj.put("answered",true);
                                obj.saveInBackground();

                                //let's pull down the scores for this question and display them dynamically in textviews.
                                //we already know choice2 votes lol
                                int c1_votes = obj.getInt("choice1_votes");
                                String c1f = "Choice 1 got " + c1_votes + " votes";
                                String c2f = "Choice 2 got " + old_votes + " votes";




                                //cool, now lets try to replace the existing linear layout with a new one
                                LayoutInflater inflater;
                                inflater = (LayoutInflater) j.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                                LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_results_view, null);
                                //at this point, we should have the new layout, so lets try to attach it
                                tl1.removeView(tl1.getChildAt(0));
                                tl3.removeView(tl3.getChildAt(0));
                                TextView inner1 = (TextView) layout.findViewById(R.id.choice1_results_text);
                                TextView inner2 = (TextView) layout.findViewById(R.id.choice2_results_text);
                                ProgressBar prog1 = (ProgressBar) layout.findViewById(R.id.choice1_results_progress);
                                ProgressBar prog2 = (ProgressBar) layout.findViewById(R.id.choice2_results_progress);
                                inner1.setText(obj.getString("choice1"));
                                inner2.setText(obj.getString("choice2"));
                                int[] res = getProgressStats(c1_votes,old_votes);
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
                    Toast.makeText(v.getContext(), "Shit broke real badlike",
                            Toast.LENGTH_LONG).show();
            }

        }
    };

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ParseObject obj = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_question, parent, false);
        }
        if(obj.getBoolean("answered")){
            //load the progress bar view here
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.question_results_view, parent, false);
            //insert the values for the view here
            TextView q = (TextView)convertView.findViewById(R.id.question_results_text);
            TextView c1 = (TextView)convertView.findViewById(R.id.choice1_results_text);
            TextView c2 = (TextView)convertView.findViewById(R.id.choice2_results_text);
            ProgressBar p1 = (ProgressBar)convertView.findViewById(R.id.choice1_results_progress);
            ProgressBar p2 = (ProgressBar)convertView.findViewById(R.id.choice2_results_progress);
            q.setText(obj.getString("question"));
            c1.setText(obj.getString("choice1"));
            c2.setText(obj.getString("choice2"));
            int[] results = {0,0};
            results = getProgressStats(obj.getInt("choice1_votes"),obj.getInt("choice2_votes"));
            p1.setProgress(results[0]);
            p2.setProgress(results[1]);

        }else {
            // Lookup view for data population
            TextView question_text = (TextView) convertView.findViewById(R.id.textViewQuestionText);
            Button choice1 = (Button) convertView.findViewById(R.id.buttonChoice1);
            Button choice2 = (Button) convertView.findViewById(R.id.buttonChoice2);

            //set listeners
            choice1.setOnClickListener(voteClickListener);
            choice2.setOnClickListener(voteClickListener);
            // Populate the data into the template view using the data object
            question_text.setText(obj.getString("question"));
            choice1.setText(obj.getString("choice1"));
            choice2.setText(obj.getString("choice2"));
        }
       // return inflater.inflate(R.layout.row_question, parent, false);
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
