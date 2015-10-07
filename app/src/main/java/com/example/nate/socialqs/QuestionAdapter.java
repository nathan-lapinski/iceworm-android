package com.example.nate.socialqs;

/**
 * Created by nate on 6/27/15.
 */
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestionAdapter extends ArrayAdapter<ParseObject> {
    private LayoutInflater inflater;

    //testing this out
    List<ParseObject> master_list;
    public QuestionAdapter(Activity activity, List<ParseObject> data){
        super(activity, R.layout.row_question, data);
        inflater = activity.getWindow().getLayoutInflater();
        this.master_list = data;
    }

    /*
    * This click handler is used for registering votes.
    * */
    public class MyCustomListener implements View.OnClickListener
    {

        ParseObject my_obj;//QJoin
        int position;
        ViewGroup container_view; //this will hold the reference to the container, which will be used to update the ui
        public MyCustomListener(ParseObject row, int pos, ViewGroup m) {
            this.my_obj = row; this.position = pos; this.container_view = m;
        }
        @Override
        public void onClick(View v)
        {
            final View j = v;
            switch(v.getId()){
                case R.id.buttonChoice1:
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("SocialQs");
                    query.whereEqualTo("question", my_obj.get("question"));
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> scoreList, ParseException e) {
                            if (e == null) {
                                ParseObject obj = scoreList.get(0);//SocialQ

                                //count the vote
                                int old_votes = obj.getInt("option1stats");
                                obj.put("option1stats", ++old_votes);
                                obj.saveInBackground();

                                //now lets grab the qjoin that corresponds to this question and this user
                                ParseQuery<ParseObject> q2 = ParseQuery.getQuery("QJoin");
                                // q2.whereEqualTo("question",obj.getObjectId());
                                q2.include("question");
                                q2.whereEqualTo("to", ParseUser.getCurrentUser().getString("facebookId"));
                                q2.findInBackground(new FindCallback<ParseObject>() {
                                    public void done(List<ParseObject> scoreList, ParseException e) {
                                        if (e == null) {
                                            for (int i = 0; i < scoreList.size(); i++) {
                                                ParseObject tq = (ParseObject) scoreList.get(i).get("question");
                                                if (tq.getObjectId().equals(my_obj.getObjectId())) {
                                                    scoreList.get(i).put("vote", 1);
                                                    scoreList.get(i).saveInBackground();
                                                }
                                            }

                                        } else {
                                            Toast.makeText(getContext(), "we're borked on vote update " + e,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }); // end the asyn call to update the votes

                                //handle the ui at this point
                                int c2_votes = obj.getInt("stats2");
                                String c1f = "Choice 2 got " + c2_votes + " votes";
                                String c2f = "Choice 1 got " + old_votes + " votes";



                                int[] results = {0,0};
                                results = getProgressStats(obj.getInt("option1stats")+1,obj.getInt("option2stats"));

                                /*
                                final ProgressBar view1 = new ProgressBar(getContext(),null,android.R.attr.progressBarStyleHorizontal);
                                view1.setProgress(results[0]);
                                view1.setMax(100);
                                view1.setBackground(getContext().getResources().getDrawable(R.drawable.custom_progressbar));

                                final ProgressBar view2 = new ProgressBar(getContext(),null,android.R.attr.progressBarStyleHorizontal);
                                view2.setProgress(results[1]);
                                view2.setMax(100);
                                view2.setBackground(getContext().getResources().getDrawable(R.drawable.custom_progressbar));

                                int index1 = container_view.indexOfChild(b1);
                                int index2 = container_view.indexOfChild(b2);

                                container_view.removeView(b1);
                                container_view.addView(view1, index1);
                                container_view.removeView(b2);
                                container_view.addView(view2, index2);
                                */
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
                                v2.setVisibility(View.VISIBLE);
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
                    ParseQuery<ParseObject> query2 = ParseQuery.getQuery("SocialQs");
                   // query2.whereEqualTo("question",q_text);
                    query2.whereEqualTo("question",my_obj.get("question"));
                    query2.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> scoreList, ParseException e) {
                            if (e == null) {
                                //business him!!
                                ParseObject obj = scoreList.get(0); //only one question should be returned. This is the question obj, so we can get it's id to put into the vote table.
                                //this does break if two or more people ask literally the same question..how this handles forwarding is unknown...
                                int old_votes = obj.getInt("option2stats");
                                obj.put("option2stats", ++old_votes);
                                obj.saveInBackground();

                                //now lets grab the qjoin that corresponds to this question and this user
                                ParseQuery<ParseObject> q2 = ParseQuery.getQuery("QJoin");

                                // q2.whereEqualTo("question",obj.getObjectId());
                                q2.include("question");
                                q2.whereEqualTo("to", ParseUser.getCurrentUser().getString("facebookId"));
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

                                }); // end the asyn call to update the votes

                                //hit the ui
                                //handle the ui at this point
                                int c2_votes = obj.getInt("option1stats");
                                String c1f = "Choice 2 got " + c2_votes + " votes";
                                String c2f = "Choice 1 got " + old_votes + " votes";

                                View b1 = container_view.findViewById(R.id.buttonChoice1);
                                View b2 = container_view.findViewById(R.id.buttonChoice2);

                                int[] results = {0,0};
                                results = getProgressStats(obj.getInt("option1stats"),obj.getInt("option2stats")+1);

                              /*  final ProgressBar view1 = new ProgressBar(getContext(),null,android.R.attr.progressBarStyleHorizontal);
                                view1.setProgress(results[0]);
                                view1.setMax(100);
                                view1.setBackground(getContext().getResources().getDrawable(R.drawable.custom_progressbar));

                                final ProgressBar view2 = new ProgressBar(getContext(),null,android.R.attr.progressBarStyleHorizontal);
                                view2.setProgress(results[1]);
                                view2.setMax(100);
                                view2.setBackground(getContext().getResources().getDrawable(R.drawable.custom_progressbar));

                                int index1 = container_view.indexOfChild(b1);
                                int index2 = container_view.indexOfChild(b2);

                                container_view.removeView(b1);
                                container_view.addView(view1, index1);
                                container_view.removeView(b2);
                                container_view.addView(view2, index2);
                                container_view.refreshDrawableState();*/
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
                                v2.setVisibility(View.VISIBLE);
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

    //This is what handles the images from the ViewQuestions activity.
    //We are displaying correctly, but updating the ui onclick is still borked.
    //HACK: Simply reload the activity on vote...don't do this.
    //TODO: Refactor the shit out of this shit. This thing has soooo much duplicate code it's abysmal.
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final ParseObject obJoin = getItem(position); //QJoin
        final ParseObject obj = (ParseObject)obJoin.get("question"); //Should be a SocialQ

        // Check if an existing view is being reused, otherwise inflate the view
        //this probably is not necessary
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_question, parent, false);
        }
        //For each join object we've received, check to see if it has it's vote property set or not.
        if (obJoin.get("vote") != null && (obJoin.getInt("vote") !=  0)) {
            //They have already voted on this question
            // here we are viewing results...so we need to differentiate whether or not it has an image. Check obj for this.
            /*
            TODO: may need to figure out how to extract this from the local data store at some point.
            TODO: Also, how do we handle updating this to reflect other people who have voted on it?
             */


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
                        //get fb image
                        ParseUser fromThisUser = obJoin.getParseUser("from");
                        String userId = fromThisUser.getString("facebookId");
                        Bitmap imageMap = null;
                        for(int i = 0; i < ViewQuestionsActivity.facebookFinal.size(); i++){
                            if(userId.equals(ViewQuestionsActivity.facebookFinal.get(i).get("userData").getId())){
                                imageMap = ViewQuestionsActivity.facebookFinal.get(i).get("userData").getBitmap();

                            }
                        }

                        ImageView pPic = (ImageView)convertView.findViewById(R.id.profilePicture);
                        pPic.setImageBitmap(imageMap);
                        //
                        int[] results = {0,0};
                        results = getProgressStats(obj.getInt("option1stats"),obj.getInt("option2stats"));
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
                        //get fb
                        ParseUser fromThisUser = obJoin.getParseUser("from");
                        String userId = fromThisUser.getString("facebookId");
                        Bitmap imageMap = null;
                        for(int i = 0; i < ViewQuestionsActivity.facebookFinal.size(); i++){
                            if(userId.equals(ViewQuestionsActivity.facebookFinal.get(i).get("userData").getId())){
                                imageMap = ViewQuestionsActivity.facebookFinal.get(i).get("userData").getBitmap();

                            }
                        }

                        ImageView pPic = (ImageView)convertView.findViewById(R.id.profilePicture);
                        pPic.setImageBitmap(imageMap);
                        int[] results = {0,0};
                        results = getProgressStats(obj.getInt("option1stats"),obj.getInt("option2stats"));
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
                    //fb
                    ParseUser fromThisUser = obJoin.getParseUser("from");
                    String userId = fromThisUser.getString("facebookId");
                    Bitmap imageMap = null;
                    for(int i = 0; i < ViewQuestionsActivity.facebookFinal.size(); i++){
                        if(userId.equals(ViewQuestionsActivity.facebookFinal.get(i).get("userData").getId())){
                            imageMap = ViewQuestionsActivity.facebookFinal.get(i).get("userData").getBitmap();

                        }
                    }

                    ImageView pPic = (ImageView)convertView.findViewById(R.id.profilePicture);
                    pPic.setImageBitmap(imageMap);


                    int[] results = {0,0};
                    results = getProgressStats(obj.getInt("option1stats"),obj.getInt("option2stats"));
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
                //fb
                ParseUser fromThisUser = obJoin.getParseUser("from");
                String userId = fromThisUser.getString("facebookId");
                Bitmap imageMap = null;
                for(int i = 0; i < ViewQuestionsActivity.facebookFinal.size(); i++){
                    if(userId.equals(ViewQuestionsActivity.facebookFinal.get(i).get("userData").getId())){
                        imageMap = ViewQuestionsActivity.facebookFinal.get(i).get("userData").getBitmap();

                    }
                }

                ImageView pPic = (ImageView)convertView.findViewById(R.id.profilePicture);
                pPic.setImageBitmap(imageMap);
                int[] results = {0,0};
                results = getProgressStats(obj.getInt("option1stats"),obj.getInt("option2stats"));
                q.setText( obj.getString("question"));
                c1.setText(obj.getString("option1") + " " + results[0]+"%");
                c2.setText(obj.getString("option2") + " " + results[1]+"%");
            }

        } else {
            // This user has not yet voted on this question, so display buttons.
            /*

            UPDATE THE UIs with buttons based on the format of the question.

            1. Check if images are present. If not, just load the default question template
            2. If images are present, then once again we have 3 options:
            3. Image question, no images in options
            4. Image question, images in options
            5. No image question, images in options.

             */

            if( (obj.get("questionPhoto") != null) || (obj.get("option1Photo") != null) || (obj.get("option2Photo") != null)  ){
                //Cases 2-5
                if( (obj.get("questionPhoto") != null) ){
                    //Then we have an image question
                    if( (obj.get("option1Photo") != null) || (obj.get("option2Photo") != null) ){
                        //Then we have question and option images
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_question_image_q_options_qs, parent, false);
                        TextView question_text = (TextView) convertView.findViewById(R.id.textViewQuestionText);
                        Button choice1 = (Button) convertView.findViewById(R.id.buttonChoice1);
                        Button choice2 = (Button) convertView.findViewById(R.id.buttonChoice2);
                        //display this users profile picture. This will be a little bit interesting
                        //Extract the facebookId from the to field of the qjoin and use that to hit our facebook data strucute (use lds in future)
                        //from there, we can extract their photo as well. Although this is going to be kinda ineffecient I thinks
                        ParseUser fromThisUser = obJoin.getParseUser("from");
                        String userId = fromThisUser.getString("facebookId");
                        Bitmap imageMap = null;
                        for(int i = 0; i < ViewQuestionsActivity.facebookFinal.size(); i++){
                            if(userId.equals(ViewQuestionsActivity.facebookFinal.get(i).get("userData").getId())){
                                imageMap = ViewQuestionsActivity.facebookFinal.get(i).get("userData").getBitmap();

                            }
                        }

                        ImageView pPic = (ImageView)convertView.findViewById(R.id.profilePicture);
                        pPic.setImageBitmap(imageMap);
                        //...
                        View.OnClickListener my_test = new MyCustomListener(obj,position,(ViewGroup)convertView.findViewById(R.id.lowerContainer));
                        choice1.setOnClickListener(my_test);
                        choice2.setOnClickListener(my_test);
                        // Populate the data into the template view using the data object
                        question_text.setText(obj.getString("question"));
                        choice1.setText(obj.getString("option1"));
                        choice2.setText(obj.getString("option2"));

                        ImageView v1 = (ImageView)convertView.findViewById(R.id.choice1_results_image);
                        ImageView v2 = (ImageView)convertView.findViewById(R.id.choice2_results_image);
                        ImageView q1 = (ImageView)convertView.findViewById(R.id.question_results_image);

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
                    } else {
                        //It was just an image in the question
                        //TODO Refactor this nizzle
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_question_image_q, parent, false);
                        //pass this as a reference into the click handler
                        View referenceView = convertView.findViewById(R.id.lowerContainer);
                        TextView question_text = (TextView) convertView.findViewById(R.id.textViewQuestionText);
                        Button choice1 = (Button) convertView.findViewById(R.id.buttonChoice1);
                        Button choice2 = (Button) convertView.findViewById(R.id.buttonChoice2);
                        // ImageButton del = (ImageButton)convertView.findViewById(R.id.btn_delete);
                        // View.OnClickListener my_del = new MyDeleteListener(obj,position);
                        // del.setOnClickListener(my_del);
                        ParseUser fromThisUser = obJoin.getParseUser("from");
                        String userId = fromThisUser.getString("facebookId");
                        Bitmap imageMap = null;
                        for(int i = 0; i < ViewQuestionsActivity.facebookFinal.size(); i++){
                            if(userId.equals(ViewQuestionsActivity.facebookFinal.get(i).get("userData").getId())){
                                imageMap = ViewQuestionsActivity.facebookFinal.get(i).get("userData").getBitmap();

                            }
                        }

                        ImageView pPic = (ImageView)convertView.findViewById(R.id.profilePicture);
                        pPic.setImageBitmap(imageMap);
                        //...

                        View.OnClickListener my_test = new MyCustomListener(obj,position,(ViewGroup)convertView.findViewById(R.id.lowerContainer));
                        choice1.setOnClickListener(my_test);
                        choice2.setOnClickListener(my_test);
                        // Populate the data into the template view using the data object
                        question_text.setText(obj.getString("question"));
                        choice1.setText(obj.getString("option1"));
                        choice2.setText(obj.getString("option2"));

                        ImageView q1 = (ImageView)convertView.findViewById(R.id.question_results_image);

                        if((obj.get("questionPhoto") != null)) {
                            ParseFile image = (ParseFile) obj.getParseFile("questionPhoto");
                            loadImages(image, q1);
                        }
                    }
                } else {
                    //The question doesn't contain an image, but the options must
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_questions_new_images, parent, false);
                    TextView question_text = (TextView) convertView.findViewById(R.id.textViewQuestionText);
                    Button choice1 = (Button) convertView.findViewById(R.id.buttonChoice1);
                    Button choice2 = (Button) convertView.findViewById(R.id.buttonChoice2);
                    // ImageButton del = (ImageButton)convertView.findViewById(R.id.btn_delete);
                    // View.OnClickListener my_del = new MyDeleteListener(obj,position);
                    // del.setOnClickListener(my_del);
                    ParseUser fromThisUser = obJoin.getParseUser("from");
                    String userId = fromThisUser.getString("facebookId");
                    Bitmap imageMap = null;
                    for(int i = 0; i < ViewQuestionsActivity.facebookFinal.size(); i++){
                        if(userId.equals(ViewQuestionsActivity.facebookFinal.get(i).get("userData").getId())){
                            imageMap = ViewQuestionsActivity.facebookFinal.get(i).get("userData").getBitmap();

                        }
                    }

                    ImageView pPic = (ImageView)convertView.findViewById(R.id.profilePicture);
                    pPic.setImageBitmap(imageMap);
                    //...
                    View.OnClickListener my_test = new MyCustomListener(obj,position,(ViewGroup)convertView.findViewById(R.id.lowerContainer));
                    choice1.setOnClickListener(my_test);
                    choice2.setOnClickListener(my_test);
                    // Populate the data into the template view using the data object
                    question_text.setText(obj.getString("question"));
                    choice1.setText(obj.getString("option1"));
                    choice2.setText(obj.getString("option2"));

                    ImageView v1 = (ImageView)convertView.findViewById(R.id.choice1_results_image);
                    ImageView v2 = (ImageView)convertView.findViewById(R.id.choice2_results_image);

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
                //Cases 1

                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_questions_new, parent, false);

                TextView question_text = (TextView) convertView.findViewById(R.id.textViewQuestionText);
                Button choice1 = (Button) convertView.findViewById(R.id.buttonChoice1);
                Button choice2 = (Button) convertView.findViewById(R.id.buttonChoice2);
               // ImageButton del = (ImageButton)convertView.findViewById(R.id.btn_delete);
               // View.OnClickListener my_del = new MyDeleteListener(obj,position);
               // del.setOnClickListener(my_del);
                ParseUser fromThisUser = obJoin.getParseUser("from");
                String userId = fromThisUser.getString("facebookId");
                Bitmap imageMap = null;
                for(int i = 0; i < ViewQuestionsActivity.facebookFinal.size(); i++){
                    if(userId.equals(ViewQuestionsActivity.facebookFinal.get(i).get("userData").getId())){
                        imageMap = ViewQuestionsActivity.facebookFinal.get(i).get("userData").getBitmap();

                    }
                }

                ImageView pPic = (ImageView)convertView.findViewById(R.id.profilePicture);
                pPic.setImageBitmap(imageMap);
                //...

                View.OnClickListener my_test = new MyCustomListener(obj,position,(ViewGroup)convertView.findViewById(R.id.lowerContainer));
                choice1.setOnClickListener(my_test);
                choice2.setOnClickListener(my_test);
                // Populate the data into the template view using the data object
                question_text.setText(obj.getString("question"));
                choice1.setText(obj.getString("option1"));
                choice2.setText(obj.getString("option2"));
            }

        }

        return convertView;
    }


    /*
    Used for normalizing vote results.
     */
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
    /*
    Used for pulling/decoding an image from a parsefile
     */
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
