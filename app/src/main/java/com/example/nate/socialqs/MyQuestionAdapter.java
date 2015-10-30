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
        //super(activity, R.layout.question_results_view, data);
        super(activity, R.layout.view_vote, data);
        inflater = activity.getWindow().getLayoutInflater();
        this.master_list = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final ParseObject obj = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_vote, parent, false);
        }

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
