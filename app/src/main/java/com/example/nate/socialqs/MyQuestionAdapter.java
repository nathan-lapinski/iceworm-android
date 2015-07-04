package com.example.nate.socialqs;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by nate on 7/3/15.
 */
public class MyQuestionAdapter extends ArrayAdapter<ParseObject> {
    private LayoutInflater inflater;
    public MyQuestionAdapter(Activity activity, List<ParseObject> data){
        super(activity, R.layout.question_results_view, data);
        inflater = activity.getWindow().getLayoutInflater();
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
        q.setText(obj.getString("question"));
        c1.setText(obj.getString("choice1"));
        c2.setText(obj.getString("choice2"));
        int[] results = {0,0};
        results = getProgressStats(obj.getInt("choice1_votes"),obj.getInt("choice2_votes"));
        p1.setProgress(results[0]);
        p2.setProgress(results[1]);

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
