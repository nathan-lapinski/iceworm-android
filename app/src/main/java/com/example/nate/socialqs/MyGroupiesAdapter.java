package com.example.nate.socialqs;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

/**
 * Created by nate on 8/17/15.
 */
public class MyGroupiesAdapter extends ArrayAdapter<HashMap<String,GroupiesActivity.GroupiesObject>>{
    private LayoutInflater inflater;
    List<HashMap<String,GroupiesActivity.GroupiesObject>> master_list;
    public MyGroupiesAdapter(Activity activity, List<HashMap<String,GroupiesActivity.GroupiesObject>> data){
        super(activity, R.layout.row_question, data);
        inflater = activity.getWindow().getLayoutInflater();
        this.master_list = data;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        HashMap<String,GroupiesActivity.GroupiesObject> gObj  = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.groupies_row, parent, false);
        }
        //simply display the user name for now
        TextView tt = (TextView) convertView.findViewById(R.id.textViewQuestionText);
        tt.setText(gObj.get("user0").getName());
        View.OnClickListener my_test = new MyCustomListener(gObj,position);
        tt.setOnClickListener(my_test);
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
            Toast.makeText(getContext(), "Added it, list now has: "+GroupiesActivity.myCurrentGroupies.size()+" groupies",
                    Toast.LENGTH_LONG).show();
        }

    };

}
