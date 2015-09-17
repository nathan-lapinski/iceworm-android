package com.example.nate.socialqs;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
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
        //final ParseObject obJoin = getItem(position);
        //final ParseObject obj = (ParseObject)obJoin.get("question");
        final ParseObject obj = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.question_results_view, parent, false);
        }

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
