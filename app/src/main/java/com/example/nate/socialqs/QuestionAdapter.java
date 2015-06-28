package com.example.nate.socialqs;

/**
 * Created by nate on 6/27/15.
 */
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

public class QuestionAdapter extends ArrayAdapter<ParseObject> {
    private LayoutInflater inflater;
    public QuestionAdapter(Activity activity, List<ParseObject> data){
        super(activity, R.layout.row_question, data);
        inflater = activity.getWindow().getLayoutInflater();
    }

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
        // Populate the data into the template view using the data object
        question_text.setText(obj.getString("question"));
        choice1.setText(obj.getString("choice1"));
        choice2.setText(obj.getString("choice2"));
       // return inflater.inflate(R.layout.row_question, parent, false);
        return convertView;
    }
}
