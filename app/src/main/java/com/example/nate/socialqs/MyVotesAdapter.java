package com.example.nate.socialqs;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by nate on 9/8/15.
 */
public class MyVotesAdapter extends ArrayAdapter<ParseObject> {
    private LayoutInflater inflater;
    List<ParseObject> master_list;
    public MyVotesAdapter(Activity activity, List<ParseObject> data){
        super(activity, R.layout.row_question, data);
        inflater = activity.getWindow().getLayoutInflater();
        this.master_list = data;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        //bombs away...we only need to display the question and options once, which is interesting...
        //should the sorting be done up top? Or is this a nested listview situation?? (probably)

        //inflate the view that you actually want within the adapter
        ParseObject obj  = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.votes_row, parent, false);
        }
        //simply display the user name for now
        TextView tt = (TextView) convertView.findViewById(R.id.voterName);
        tt.setText(obj.getString("to"));//obj.getString("to")); //userData should return a GroupiesObject.
        Toast.makeText(getContext(), "Writing things",
                Toast.LENGTH_LONG).show();
       // View.OnClickListener my_test = new MyCustomListener(obj,position);
       // tt.setOnClickListener(my_test);
        return convertView;
    }

    public class MyCustomListener implements View.OnClickListener
    {
        HashMap<String,GroupiesActivity.GroupiesObject> my_obj;
        int position;
        public MyCustomListener(HashMap<String,GroupiesActivity.GroupiesObject> row, int pos) {
            this.my_obj = row; this.position = pos;
        }
        @Override
        public void onClick(View v)
        {
            final View j = v;
            GroupiesActivity.myCurrentGroupies.add(my_obj);
            Toast.makeText(getContext(), "Added it, list now has: " + GroupiesActivity.myCurrentGroupies.size() + " groupies",
                    Toast.LENGTH_LONG).show();
        }

    };

}