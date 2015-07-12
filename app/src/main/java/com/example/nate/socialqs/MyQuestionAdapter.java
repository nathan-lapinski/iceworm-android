package com.example.nate.socialqs;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

/**
 * Created by nate on 7/3/15.
 */
public class MyQuestionAdapter extends ArrayAdapter<ParseObject> {
    private LayoutInflater inflater;
    List<ParseObject> master_list;
    public MyQuestionAdapter(Activity activity, List<ParseObject> data){
        super(activity, R.layout.question_results_view, data);
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
                        scoreList.get(0).removeAll("myQsId", Arrays.asList(my_obj.getObjectId()));
                        //add it to deleted qs
                        scoreList.get(0).addAllUnique("deletedMyQsId", Arrays.asList(my_obj.getObjectId()));
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


    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ParseObject obj = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.question_results_view, parent, false);
        }

        //load the progress bar view here
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.question_results_view, parent, false);
        //insert the values for the view here
        TextView q = (TextView)convertView.findViewById(R.id.question_results_text);
        TextView c1 = (TextView)convertView.findViewById(R.id.choice1_results_text);
        TextView c2 = (TextView)convertView.findViewById(R.id.choice2_results_text);
        ProgressBar p1 = (ProgressBar)convertView.findViewById(R.id.choice1_results_progress);
        ProgressBar p2 = (ProgressBar)convertView.findViewById(R.id.choice2_results_progress);
        TextView per1 = (TextView)convertView.findViewById(R.id.choice1_percent);
        TextView per2 = (TextView)convertView.findViewById(R.id.choice2_percent);
        ImageButton del = (ImageButton)convertView.findViewById(R.id.btn_delete);
        View.OnClickListener my_del = new MyDeleteListener(obj,position);
        del.setOnClickListener(my_del);

        q.setText(obj.getString("question"));
        c1.setText(obj.getString("option1"));
        c2.setText(obj.getString("option2"));
        int[] results = {0,0};
        results = getProgressStats(obj.getInt("stats1"),obj.getInt("stats2"));
        p1.setProgress(results[0]);
        p2.setProgress(results[1]);
        per1.setText(results[0]+"%");
        per2.setText(results[1]+"%");

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
