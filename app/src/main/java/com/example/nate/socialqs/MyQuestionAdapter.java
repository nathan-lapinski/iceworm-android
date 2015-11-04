package com.example.nate.socialqs;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
    MyQuestionAdapter mySelf;
    public MyQuestionAdapter(Activity activity, List<ParseObject> data){
        //super(activity, R.layout.question_results_view, data);
        super(activity, R.layout.view_vote, data);
        inflater = activity.getWindow().getLayoutInflater();
        this.master_list = data;
        this.mySelf = this;
    }

    //**********

    public class MyCustomListener implements View.OnClickListener
    {

        ParseObject my_obj;//this should be a socialQ though. Pretty certain that it is now.
        ParseObject my_qjoin;
        int position;
        ViewGroup container_view; //this will hold the reference to the container, which will be used to update the ui

        public MyCustomListener(ParseObject row, int pos, ViewGroup m, ParseObject qJoin) {
            this.my_obj = row; this.position = pos; this.container_view = m; this.my_qjoin = qJoin;
        }

        @Override
        public void onClick(View v)
        {
            final View j = v;
            switch(v.getId()){
                case R.id.buttonChoice1:

                    Toast.makeText(getContext(), "Vote cast",
                            Toast.LENGTH_SHORT).show();
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("SocialQs");
                    //query.whereEqualTo("question", my_obj.get("question"));
                    //query.whereEqualTo("questionText",my_obj.getString("questionText")); //hmmm...
                    query.whereEqualTo("objectId",my_obj.getObjectId());
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> scoreList, ParseException e) {
                            if (e == null) {
                                //TODO: Error check this
                                ParseObject obj = scoreList.get(0);//SocialQ

                                //count the vote
                                int old_votes = obj.getInt("option1Stats");
                                obj.put("option1Stats", ++old_votes);
                                obj.saveInBackground();

                                //now lets grab the qjoin that corresponds to this question and this user
                                ParseQuery<ParseObject> q2 = ParseQuery.getQuery("QJoin");
                                // q2.whereEqualTo("question",obj.getObjectId());
                                q2.include("question");
                                q2.whereEqualTo("to", ParseUser.getCurrentUser());
                                q2.findInBackground(new FindCallback<ParseObject>() {
                                    public void done(List<ParseObject> scoreList, ParseException e) {
                                        if (e == null) {
                                            for (int i = 0; i < scoreList.size(); i++) {
                                                ParseObject tq = (ParseObject) scoreList.get(i).get("question");
                                                if (tq.getObjectId().equals(my_obj.getObjectId())) {
                                                    scoreList.get(i).put("vote", 1);
                                                    scoreList.get(i).saveInBackground();
                                                    break; // for optimize?
                                                }

                                            }

                                        } else {
                                            Toast.makeText(getContext(), "we're borked on vote update " + e,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }); // end the async call to update the votes

                                //TODO: Turn this into a function
                                //update the ui
                                int[] results = {0, 0};
                                results = getProgressStats(obj.getInt("option1Stats") + 1, obj.getInt("option2Stats"));
                                ImageView o1 = (ImageView) container_view.findViewById(R.id.choice1_results_image);
                                ImageView o2 = (ImageView) container_view.findViewById(R.id.choice2_results_image);
                                View b1 = container_view.findViewById(R.id.buttonChoice1);
                                View b2 = container_view.findViewById(R.id.buttonChoice2);
                                String text1 = ((Button) b1).getText().toString();
                                String text2 = ((Button) b2).getText().toString();
                                container_view.findViewById(R.id.hiddenLayout1).setVisibility(View.VISIBLE);
                                container_view.findViewById(R.id.hiddenLayout2).setVisibility(View.VISIBLE);
                                ImageView new1 = (ImageView) container_view.findViewById(R.id.hiddenImageview1);
                                ImageView new2 = (ImageView) container_view.findViewById(R.id.hiddenImageview2);
                                TextView nt1 = (TextView) container_view.findViewById(R.id.hiddenTextview1);
                                TextView nt2 = (TextView) container_view.findViewById(R.id.hiddenTextview2);
                                if(text1 != null) {
                                    nt1.setText(text1 + " " + results[0] + "%");
                                } else {
                                    nt1.setText(results[0] + "%");
                                }
                                if(text1 != null) {
                                    nt2.setText(text2 + " " + results[1] + "%");
                                } else {
                                    nt2.setText(results[1] + "%");
                                }
                                if (o1.getDrawable() != null) {
                                    new1.setImageBitmap(((BitmapDrawable) o1.getDrawable()).getBitmap());
                                }
                                if (o2.getDrawable() != null) {
                                    new2.setImageBitmap(((BitmapDrawable) o2.getDrawable()).getBitmap());
                                }
                                container_view.findViewById(R.id.linearLayout1).setVisibility(View.GONE);
                                container_view.findViewById(R.id.linearLayout2).setVisibility(View.GONE);
                                /*
                                View b1 = container_view.findViewById(R.id.buttonChoice1);
                                View b2 = container_view.findViewById(R.id.buttonChoice2);
                                String text1 = ((Button)b1).getText().toString();
                                String text2 = ((Button)b2).getText().toString();
                                b1.setVisibility(View.GONE);
                                b2.setVisibility(View.GONE);
                                View v1 = container_view.findViewById(R.id.hiddenText1);
                                View v2 = container_view.findViewById(R.id.hiddenText2);
                                TextView t1 = (TextView)v1;
                                t1.setText( text1 + " " + results[0] + "%");
                                TextView t2 = (TextView)v2;
                                t2.setText( text2 + " " + results[1] + "%");
                                v1.setVisibility(View.VISIBLE);
                                v2.setVisibility(View.VISIBLE);*/

                                //hit the adapter somehow?
                                //could just iterate through master_list...
                                for (int k = 0; k < mySelf.master_list.size(); k++) {
                                    if ((mySelf.master_list.get(k).getObjectId()).equals(my_qjoin.getObjectId())) {
                                        mySelf.master_list.get(k).put("vote", 1);
                                        mySelf.master_list.get(k).getParseObject("question").put("option1Stats", (obj.getInt("option1Stats") + 1));
                                        mySelf.master_list.get(k).getParseObject("question").put("option2Stats", obj.getInt("option2Stats"));
                                        break;
                                    }
                                }
                                container_view.refreshDrawableState();

                            } else {
                                Log.d("score", "Error: " + e.getMessage());
                            }
                        }
                    });
                    break;

                //---------------------
                //======================
                case R.id.buttonChoice2:
                    Toast.makeText(getContext(), "Vote cast",
                            Toast.LENGTH_SHORT).show();
                    ParseQuery<ParseObject> query2 = ParseQuery.getQuery("SocialQs");
                    // query2.whereEqualTo("question",q_text);
                    //query2.whereEqualTo("question",my_obj.get("question")); ...hmmm
                    //query2.whereEqualTo("questionText",my_obj.getString("questionText"));
                    query2.whereEqualTo("objectId",my_obj.getObjectId());
                    query2.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> scoreList, ParseException e) {
                            if (e == null) {
                                //business him!!
                                ParseObject obj = scoreList.get(0); //only one question should be returned. This is the question obj, so we can get it's id to put into the vote table.
                                //this does break if two or more people ask literally the same question..how this handles forwarding is unknown...
                                int old_votes = obj.getInt("option2Stats");
                                obj.put("option2Stats", ++old_votes);
                                obj.saveInBackground();

                                //now lets grab the qjoin that corresponds to this question and this user
                                ParseQuery<ParseObject> q2 = ParseQuery.getQuery("QJoin");

                                // q2.whereEqualTo("question",obj.getObjectId());
                                q2.include("question");
                                q2.whereEqualTo("to", ParseUser.getCurrentUser());
                                q2.findInBackground(new FindCallback<ParseObject>() {
                                    public void done(List<ParseObject> scoreList, ParseException e) {
                                        if (e == null) {
                                            for (int i = 0; i < scoreList.size(); i++) {
                                                ParseObject tq = (ParseObject) scoreList.get(i).get("question");
                                                if (tq.getObjectId().equals(my_obj.getObjectId())) {
                                                    scoreList.get(i).put("vote", 2);
                                                    scoreList.get(i).saveInBackground();
                                                }
                                            }

                                        } else {
                                            Toast.makeText(getContext(), "we're borked in vote " + e,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }); // end the async call to update the votes

                                int[] results = {0,0};
                                results = getProgressStats(obj.getInt("option1Stats"),obj.getInt("option2Stats")+1);
                                ImageView o1 = (ImageView)container_view.findViewById(R.id.choice1_results_image);
                                ImageView o2 = (ImageView)container_view.findViewById(R.id.choice2_results_image);
                                View b1 = container_view.findViewById(R.id.buttonChoice1);
                                View b2 = container_view.findViewById(R.id.buttonChoice2);
                                String text1 = ((Button)b1).getText().toString();
                                String text2 = ((Button)b2).getText().toString();
                                ImageView new1 = (ImageView)container_view.findViewById(R.id.hiddenImageview1);
                                ImageView new2 = (ImageView)container_view.findViewById(R.id.hiddenImageview2);
                                TextView nt1 = (TextView)container_view.findViewById(R.id.hiddenTextview1);
                                TextView nt2 = (TextView)container_view.findViewById(R.id.hiddenTextview2);
                                if(text1 != null) {
                                    nt1.setText(text1 + " " + results[0] + "%");
                                } else {
                                    nt1.setText(results[0] + "%");
                                }
                                if(text1 != null) {
                                    nt2.setText(text2 + " " + results[1] + "%");
                                } else {
                                    nt2.setText(results[1] + "%");
                                }
                                if(o1.getDrawable() != null){
                                    new1.setImageBitmap( ((BitmapDrawable)o1.getDrawable()).getBitmap() );
                                }
                                if(o2.getDrawable() != null){
                                    new2.setImageBitmap( ((BitmapDrawable)o2.getDrawable()).getBitmap() );
                                }
                                container_view.findViewById(R.id.linearLayout1).setVisibility(View.GONE);
                                container_view.findViewById(R.id.linearLayout2).setVisibility(View.GONE);
                                container_view.findViewById(R.id.hiddenLayout1).setVisibility(View.VISIBLE);
                                container_view.findViewById(R.id.hiddenLayout2).setVisibility(View.VISIBLE);
                                /*int[] results = {0,0};
                                results = getProgressStats(obj.getInt("option1Stats"),obj.getInt("option2Stats")+1);
                                View b1 = container_view.findViewById(R.id.buttonChoice1);
                                View b2 = container_view.findViewById(R.id.buttonChoice2);
                                String text1 = ((Button)b1).getText().toString();
                                String text2 = ((Button)b2).getText().toString();
                                b1.setVisibility(View.GONE);
                                b2.setVisibility(View.GONE);
                                View v1 = container_view.findViewById(R.id.hiddenText1);
                                View v2 = container_view.findViewById(R.id.hiddenText2);
                                TextView t1 = (TextView)v1;
                                t1.setText( text1 + " " + results[0] + "%");
                                TextView t2 = (TextView)v2;
                                t2.setText( text2 + " " + results[1] + "%");
                                v1.setVisibility(View.VISIBLE);
                                v2.setVisibility(View.VISIBLE);*/
                                //mySelf.notifyDataSetChanged();
                                for(int k = 0; k < mySelf.master_list.size(); k++){
                                    if((mySelf.master_list.get(k).getObjectId()).equals(my_qjoin.getObjectId())){
                                        mySelf.master_list.get(k).put("vote", 2);
                                        mySelf.master_list.get(k).getParseObject("question").put("option1Stats", obj.getInt("option1Stats"));
                                        mySelf.master_list.get(k).getParseObject("question").put("option2Stats",(obj.getInt("option2Stats")+1));
                                        break;
                                    }
                                }
                                container_view.refreshDrawableState();

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


    //**********

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final ParseObject obJoin = getItem(position); //QJoin
        final ParseObject obj = (ParseObject)obJoin.get("question"); //Should be a SocialQ
        //--
        if (obJoin.get("vote") != null && ( (obJoin.getInt("vote") ==  1) || (obJoin.getInt("vote") ==  2))) {
            //vote
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_no_vote, parent, false);
            }

            //check visibility:
            if(convertView.findViewById(R.id.hiddenLayout1).getVisibility() == View.GONE){
                //we know it's a vote that needs its layout mixed around
                convertView.findViewById(R.id.linearLayout1).setVisibility(View.GONE);
                convertView.findViewById(R.id.linearLayout2).setVisibility(View.GONE);
                convertView.findViewById(R.id.hiddenLayout1).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.hiddenLayout2).setVisibility(View.VISIBLE);
            }
            //business them??
            TextView q = (TextView) convertView.findViewById(R.id.textViewQuestionText);
            TextView c1 = (TextView)convertView.findViewById(R.id.hiddenTextview1);
            TextView c2 = (TextView)convertView.findViewById(R.id.hiddenTextview2);
            ImageView v1 = (ImageView)convertView.findViewById(R.id.hiddenImageview1);
            ImageView v2 = (ImageView)convertView.findViewById(R.id.hiddenImageview2);
            ImageView q1 = (ImageView)convertView.findViewById(R.id.question_results_image);
            //convertView = LayoutInflater.from(getContext()).inflate(R.layout.questions_result_more_images, parent, false);
            /*TextView q = (TextView)convertView.findViewById(R.id.question_results_text);
            TextView c1 = (TextView)convertView.findViewById(R.id.choice1_results_text);
            TextView c2 = (TextView)convertView.findViewById(R.id.choice2_results_text);
            ImageView v1 = (ImageView)convertView.findViewById(R.id.choice1_results_image);
            ImageView v2 = (ImageView)convertView.findViewById(R.id.choice2_results_image);
            ImageView q1 = (ImageView)convertView.findViewById(R.id.question_results_image);*/

            v1.setImageBitmap(null);
            v2.setImageBitmap(null);
            q1.setImageBitmap(null);
            //get fb image
            ParseUser fromThisUser = obJoin.getParseUser("from");
            //String userId = fromThisUser.getString("facebookId");
            ParseFile imageMap = fromThisUser.getParseFile("profilePicture");
            /*for(int i = 0; i < ViewQuestionsActivity.facebookFinal.size(); i++){
                if(userId.equals(ViewQuestionsActivity.facebookFinal.get(i).get("userData").getId())){
                    imageMap = ViewQuestionsActivity.facebookFinal.get(i).get("userData").getBitmap();

                }
            }*/

            ImageView pPic = (ImageView)convertView.findViewById(R.id.profilePicture);
            try {
                byte[] b = imageMap.getData();
                Bitmap bmp = BitmapFactory.decodeByteArray(b,0,b.length);
                pPic.setImageBitmap(bmp);
            }catch (ParseException e){

            }
            //pPic.setImageBitmap(imageMap);
            //
            int[] results = {0,0};
            results = getProgressStats(obj.getInt("option1Stats"),obj.getInt("option2Stats"));
            q.setText( obj.getString("questionText"));
            if(obj.getString("option1Text") != null) {
                c1.setText(obj.getString("option1Text") + " " + results[0] + "%");
            } else {
                c1.setText(results[0] + "%");
            }
            if(obj.getString("option2Text") != null){
                c2.setText(obj.getString("option2Text") + " " + results[1]+"%");
            }else {
                c2.setText(results[1]+"%");
            }

            if((obj.get("option1ImageThumb") != null)) {
                ParseFile image = (ParseFile) obj.getParseFile("option1ImageThumb");
                loadImages(image, v1);
            }
            if((obj.get("option2ImageThumb") != null)) {
                ParseFile image = (ParseFile) obj.getParseFile("option2ImageThumb");
                loadImages(image, v2);
            }
            if((obj.get("questionImageThumb") != null)) {
                ParseFile image = (ParseFile) obj.getParseFile("questionImageThumb");
                loadImages(image, q1);
            }
            //
        } else {
            //no vote
            //Then we have question and option images
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_no_vote, parent, false);
            }
            //detect if we need to flip things??
            if(convertView.findViewById(R.id.hiddenLayout1).getVisibility() == View.VISIBLE){
                convertView.findViewById(R.id.linearLayout1).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.linearLayout2).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.hiddenLayout1).setVisibility(View.GONE);
                convertView.findViewById(R.id.hiddenLayout2).setVisibility(View.GONE);
            }


            //convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_question_image_q_options_qs, parent, false);
            TextView question_text = (TextView) convertView.findViewById(R.id.textViewQuestionText);
            Button choice1 = (Button) convertView.findViewById(R.id.buttonChoice1);
            Button choice2 = (Button) convertView.findViewById(R.id.buttonChoice2);
            //display this users profile picture. This will be a little bit interesting
            //Extract the facebookId from the to field of the qjoin and use that to hit our facebook data strucute (use lds in future)
            //from there, we can extract their photo as well. Although this is going to be kinda ineffecient I thinks
            ParseUser fromThisUser = obJoin.getParseUser("from");
            //String userId = fromThisUser.getString("facebookId");
            //Bitmap imageMap = null;
            ParseFile imageMap = fromThisUser.getParseFile("profilePicture");
            /*for(int i = 0; i < ViewQuestionsActivity.facebookFinal.size(); i++){
                if(userId.equals(ViewQuestionsActivity.facebookFinal.get(i).get("userData").getId())){
                    imageMap = ViewQuestionsActivity.facebookFinal.get(i).get("userData").getBitmap();

                }
            }*/

            ImageView pPic = (ImageView)convertView.findViewById(R.id.profilePicture);
            try {
                byte[] b = imageMap.getData();
                Bitmap bmp = BitmapFactory.decodeByteArray(b,0,b.length);
                pPic.setImageBitmap(bmp);
            }catch (ParseException e){

            }
            //...
            View.OnClickListener my_test = new MyCustomListener(obj,position,(ViewGroup)convertView.findViewById(R.id.lowerContainer),obJoin);
            choice1.setOnClickListener(my_test);
            choice2.setOnClickListener(my_test);
            // Populate the data into the template view using the data object
            question_text.setText(obj.getString("questionText"));
            choice1.setText(obj.getString("option1Text"));
            choice2.setText(obj.getString("option2Text"));

            ImageView v1 = (ImageView)convertView.findViewById(R.id.choice1_results_image);
            ImageView v2 = (ImageView)convertView.findViewById(R.id.choice2_results_image);
            ImageView q1 = (ImageView)convertView.findViewById(R.id.question_results_image);
            v1.setImageBitmap(null);
            v2.setImageBitmap(null);
            q1.setImageBitmap(null);
            if((obj.get("option1ImageThumb") != null)) {
                ParseFile image = (ParseFile) obj.getParseFile("option1ImageThumb");
                loadImages(image, v1);
            }
            if((obj.get("option2ImageThumb") != null)) {
                ParseFile image = (ParseFile) obj.getParseFile("option2ImageThumb");
                loadImages(image, v2);
            }
            if((obj.get("questionImageThumb") != null)) {
                ParseFile image = (ParseFile) obj.getParseFile("questionImageThumb");
                loadImages(image, q1);
            }
            //
        }
        //-------------------------------
        /*
        TextView q = (TextView)convertView.findViewById(R.id.question_results_text);
        TextView c1 = (TextView)convertView.findViewById(R.id.choice1_results_text);
        TextView c2 = (TextView)convertView.findViewById(R.id.choice2_results_text);
        ImageView v1 = (ImageView)convertView.findViewById(R.id.choice1_results_image);
        ImageView v2 = (ImageView)convertView.findViewById(R.id.choice2_results_image);
        ImageView q1 = (ImageView)convertView.findViewById(R.id.question_results_image);
        ImageView pPic = (ImageView)convertView.findViewById(R.id.profilePicture);
        ParseFile myPic = ParseUser.getCurrentUser().getParseFile("profilePicture");
        byte[] bitmapdata = {};
        try {
            bitmapdata = myPic.getData();
        } catch(ParseException e){

        }
        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata , 0, bitmapdata.length);
        pPic.setImageBitmap(bitmap);
        int[] results = {0,0};
        results = getProgressStats(obj.getInt("option1Stats"),obj.getInt("option2Stats"));
        q.setText( obj.getString("questionText"));
        c1.setText(obj.getString("option1Text") + " " + results[0] + "%");
        c2.setText(obj.getString("option2Text") + " " + results[1]+"%");
        v1.setImageBitmap(null);
        v2.setImageBitmap(null);
        q1.setImageBitmap(null);
        if((obj.get("option1ImageThumb") != null)) {
            ParseFile image = (ParseFile) obj.getParseFile("option1ImageThumb");
            loadImages(image, v1);
        }
        if((obj.get("option2ImageThumb") != null)) {
            ParseFile image = (ParseFile) obj.getParseFile("option2ImageThumb");
            loadImages(image, v2);
        }
        if((obj.get("questionImageThumb") != null)) {
            ParseFile image = (ParseFile) obj.getParseFile("questionImageThumb");
            loadImages(image, q1);
        }*/

        return convertView;
    }
    /*
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        //final ParseObject obJoin = getItem(position);
        //final ParseObject obj = (ParseObject)obJoin.get("question");
        final ParseObject obj = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
      //  if (convertView == null) {
        //    convertView = LayoutInflater.from(getContext()).inflate(R.layout.question_results_view, parent, false);
        //}

        if( (obj.get("questionImageThumb") != null) || (obj.get("option1ImageThumb") != null) || (obj.get("option2ImageThumb") != null)  ){

            if(obj.get("questionImageThumb") != null){
                //then we know that the question has an image
                if( (obj.get("option1ImageThumb") != null) || (obj.get("option2ImageThumb") != null) ){
                    //image options
                    if (convertView == null) {
                     //   convertView = LayoutInflater.from(getContext()).inflate(R.layout.questions_result_one_image, parent, false);
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.questions_result_more_images, parent, false);
                    }
                    //convertView = LayoutInflater.from(getContext()).inflate(R.layout.questions_result_more_images, parent, false);
                    TextView q = (TextView)convertView.findViewById(R.id.question_results_text);
                    TextView c1 = (TextView)convertView.findViewById(R.id.choice1_results_text);
                    TextView c2 = (TextView)convertView.findViewById(R.id.choice2_results_text);
                    ImageView v1 = (ImageView)convertView.findViewById(R.id.choice1_results_image);
                    ImageView v2 = (ImageView)convertView.findViewById(R.id.choice2_results_image);
                    ImageView q1 = (ImageView)convertView.findViewById(R.id.question_results_image);
                    ImageView pPic = (ImageView)convertView.findViewById(R.id.profilePicture);
                    ParseFile myPic = ParseUser.getCurrentUser().getParseFile("profilePicture");
                    byte[] bitmapdata = {};
                    try {
                        bitmapdata = myPic.getData();
                    } catch(ParseException e){

                    }
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata , 0, bitmapdata.length);
                    pPic.setImageBitmap(bitmap);
                    int[] results = {0,0};
                    results = getProgressStats(obj.getInt("option1Stats"),obj.getInt("option2Stats"));
                    q.setText( obj.getString("questionText"));
                    c1.setText(obj.getString("option1Text") + " " + results[0]+"%");
                    c2.setText(obj.getString("option2Text") + " " + results[1]+"%");
                    if((obj.get("option1ImageThumb") != null)) {
                        ParseFile image = (ParseFile) obj.getParseFile("option1ImageThumb");
                        loadImages(image, v1);
                    }
                    if((obj.get("option2ImageThumb") != null)) {
                        ParseFile image = (ParseFile) obj.getParseFile("option2ImageThumb");
                        loadImages(image, v2);
                    }
                    if((obj.get("questionImageThumb") != null)) {
                        ParseFile image = (ParseFile) obj.getParseFile("questionImageThumb");
                        loadImages(image, q1);
                    }
                }else{
                    //no image options
                    if (convertView == null) {
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.questions_result_one_image, parent, false);
                    }
                    //convertView = LayoutInflater.from(getContext()).inflate(R.layout.questions_result_one_image, parent, false);
                    TextView q = (TextView)convertView.findViewById(R.id.question_results_text);
                    TextView c1 = (TextView)convertView.findViewById(R.id.choice1_results_text);
                    TextView c2 = (TextView)convertView.findViewById(R.id.choice2_results_text);
                    ImageView q1 = (ImageView)convertView.findViewById(R.id.question_results_image);
                    ImageView pPic = (ImageView)convertView.findViewById(R.id.profilePicture);
                    ParseFile myPic = ParseUser.getCurrentUser().getParseFile("profilePicture");
                    byte[] bitmapdata = {};
                    try {
                        bitmapdata = myPic.getData();
                    } catch(ParseException e){

                    }
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata , 0, bitmapdata.length);
                    pPic.setImageBitmap(bitmap);
                    int[] results = {0,0};
                    results = getProgressStats(obj.getInt("option1Stats"),obj.getInt("option2Stats"));
                    q.setText( obj.getString("questionText"));
                    c1.setText(obj.getString("option1Text") + " " + results[0]+"%");
                    c2.setText(obj.getString("option2Text") + " " + results[1]+"%");
                    if((obj.get("questionImageThumb") != null)) {
                        ParseFile image = (ParseFile) obj.getParseFile("questionImageThumb");
                        loadImages(image, q1);
                    }
                }
            } else {
                //no question image, but options image(s)
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.questions_results_view_image, parent, false);
                }
                //convertView = LayoutInflater.from(getContext()).inflate(R.layout.questions_results_view_image, parent, false);
                TextView q = (TextView)convertView.findViewById(R.id.question_results_text);
                TextView c1 = (TextView)convertView.findViewById(R.id.choice1_results_text);
                TextView c2 = (TextView)convertView.findViewById(R.id.choice2_results_text);
                ImageView v1 = (ImageView)convertView.findViewById(R.id.choice1_results_image);
                ImageView v2 = (ImageView)convertView.findViewById(R.id.choice2_results_image);
                ImageView pPic = (ImageView)convertView.findViewById(R.id.profilePicture);
                ParseFile myPic = ParseUser.getCurrentUser().getParseFile("profilePicture");
                byte[] bitmapdata = {};
                try {
                    bitmapdata = myPic.getData();
                } catch(ParseException e){

                }
                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata , 0, bitmapdata.length);
                pPic.setImageBitmap(bitmap);


                int[] results = {0,0};
                results = getProgressStats(obj.getInt("option1Stats"),obj.getInt("option2Stats"));
                q.setText( obj.getString("questionText"));
                c1.setText(obj.getString("option1Text") + " " + results[0]+"%");
                c2.setText(obj.getString("option2Text") + " " + results[1]+"%");
                if((obj.get("option1ImageThumb") != null)) {
                    ParseFile image = (ParseFile) obj.getParseFile("option1ImageThumb");
                    loadImages(image, v1);
                }
                if((obj.get("option2ImageThumb") != null)) {
                    ParseFile image = (ParseFile) obj.getParseFile("option2ImageThumb");
                    loadImages(image, v2);
                }
            }

        } else {
            //we have no images, so use the default template
            if (convertView == null) {
                //convertView = LayoutInflater.from(getContext()).inflate(R.layout.question_results_view, parent, false);
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.questions_results_view_test, parent, false);
            }
           // convertView = LayoutInflater.from(getContext()).inflate(R.layout.questions_results_view_test, parent, false);
            TextView q = (TextView)convertView.findViewById(R.id.question_results_text);
            TextView c1 = (TextView)convertView.findViewById(R.id.choice1_results_text);
            TextView c2 = (TextView)convertView.findViewById(R.id.choice2_results_text);
            int[] results = {0,0};
            results = getProgressStats(obj.getInt("option1Stats"), obj.getInt("option2Stats"));
            q.setText( obj.getString("questionText"));
            c1.setText(obj.getString("option1Text") + " " + results[0] + "%");
            c2.setText(obj.getString("option2Text") + " " + results[1] + "%");
            ImageView pPic = (ImageView)convertView.findViewById(R.id.profilePicture);
            ParseFile myPic = ParseUser.getCurrentUser().getParseFile("profilePicture");
            byte[] bitmapdata = {};
            try {
                bitmapdata = myPic.getData();
            } catch(ParseException e){

            }
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata , 0, bitmapdata.length);
            pPic.setImageBitmap(bitmap);

        }
        return convertView;
    } */

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
