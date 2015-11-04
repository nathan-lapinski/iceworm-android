package com.example.nate.socialqs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by nate on 10/2/15.
 */
public class GroupListAdapter extends ArrayAdapter<String> {
    private LayoutInflater inflater;
    ArrayList<String> master_list;
    Activity callingActivity;
    public GroupListAdapter(Activity activity, ArrayList<String> data){
        super(activity, R.layout.row_question, data);
        inflater = activity.getWindow().getLayoutInflater();
        this.master_list = data;
        this.callingActivity = activity;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        String gObj  = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grouplist, parent, false);
        }
        //simply display the user name for now
        TextView tt = (TextView) convertView.findViewById(R.id.groupName);
        tt.setText(gObj); //userData should return a GroupiesObject.
        View.OnClickListener my_test = new MyCustomListener(gObj,position);
        tt.setOnClickListener(my_test);
        return convertView;
    }

    public class MyCustomListener implements View.OnClickListener
    {
        String my_obj;
        int position;
        public MyCustomListener(String row, int pos) {
            this.my_obj = row; this.position = pos;
        }
        @Override
        public void onClick(View v)
        {
            final View j = v;
            //go ahead and pull down that list of groupies from the server for now. Do it from LDS ASAP.
            ParseQuery<ParseObject> vote_query = ParseQuery.getQuery("GroupJoin");
            vote_query.whereEqualTo("groupName", my_obj );
            vote_query.whereEqualTo("owner", ParseUser.getCurrentUser());
            //.....
            vote_query.findInBackground(new FindCallback<ParseObject>() {
                public void done(final List<ParseObject> resList, ParseException e) {
                    if (e == null) {
                        //create a groupies object and add it to myCurrentGroupies
                        for(int i = 0; i < resList.size(); i++){
                            //what do I populate this with??
                            ParseObject tObj = resList.get(i);//this is a groupjoin
                            //we need to populate our user table betterer. id or something needs to be facebookId.
                            GroupiesActivity.GroupiesObject tmpObj = new GroupiesActivity.GroupiesObject(tObj.getString("facebookId"),tObj.getString("name"));
                            HashMap<String,GroupiesActivity.GroupiesObject> tmp = new HashMap<String, GroupiesActivity.GroupiesObject>();
                            tmp.put("userData",tmpObj);
                            GroupiesActivity.myCurrentGroupies.add(tmp);
                        }
                        //TODO:don't really want to throw a new intent here...but onBackPressed isn't really workings
                        callingActivity.onBackPressed();
                    } else {
                        //There has been an error
                        Toast.makeText(getContext(), "Error accessing join table",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
            //.....
            /*GroupiesActivity.myCurrentGroupies.add(my_obj);
            Toast.makeText(getContext(), "Added it, list now has: " + GroupiesActivity.myCurrentGroupies.size() + " groupies",
                    Toast.LENGTH_LONG).show();*/

    };
}
