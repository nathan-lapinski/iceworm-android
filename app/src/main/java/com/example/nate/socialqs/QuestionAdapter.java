package com.example.nate.socialqs;

/**
 * Created by nate on 6/27/15.
 */
import android.app.Activity;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

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
            final LinearLayout tl = l2;
            final LinearLayout tl1 = l1;


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
                                Toast.makeText(j.getContext(), "Button 1 good",
                                        Toast.LENGTH_LONG).show();
                                ParseObject obj = scoreList.get(0); //only one
                                int old_votes = obj.getInt("choice1_votes");
                                obj.put("choice1_votes",++old_votes);
                                obj.saveInBackground();
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
                                Toast.makeText(j.getContext(), "Button 2 good",
                                        Toast.LENGTH_LONG).show();
                                ParseObject obj = scoreList.get(0); //only one
                                int old_votes = obj.getInt("choice2_votes");
                                obj.put("choice2_votes", ++old_votes);
                                obj.saveInBackground();

                                //let's pull down the scores for this question and display them dynamically in textviews.
                                //we already know choice2 votes lol
                                int c1_votes = obj.getInt("choice1_votes");
                                String c1f = "Choice 1 got " + c1_votes + " votes";
                                String c2f = "Choice 2 got " + old_votes + " votes";

                                //figure out how to add view elements
                                //remove the buttons
                                View b = tl1.findViewById(R.id.buttonChoice1);
                                ViewGroup p = (ViewGroup)b.getParent();
                                ((ViewGroup)b.getParent()).removeView(b);
                                View b2 = tl1.findViewById(R.id.buttonChoice2);
                                ViewGroup p2 = (ViewGroup)b2.getParent();
                                ((ViewGroup)b2.getParent()).removeView(b2);

                                //try creating a new view
                                ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                TextView tv=new TextView(j.getContext());
                                tv.setLayoutParams(lparams);
                                tv.setText(c1f);
                                p.addView(tv);
                                ViewGroup.LayoutParams lparams2 = new ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                TextView tv2=new TextView(j.getContext());
                                tv2.setLayoutParams(lparams2);
                                tv2.setText(c2f);
                                p.addView(tv2);
                                //--
                                tl.refreshDrawableState();
                                p.refreshDrawableState();

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

       // return inflater.inflate(R.layout.row_question, parent, false);
        return convertView;
    }

}
