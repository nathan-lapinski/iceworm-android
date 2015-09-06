package com.example.nate.socialqs;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class AskQuestionActivity extends ActionBarActivity {
    /*
    TODO:
    0!!: We can now access the current groupies! The cloud will handle populating the join table for us, so write cloud code to take the groupies
    and populate the join on submit. Then reset that groupie object.
    1. (major) Update AskQuestion to use the new database model
    2. (major) Update photo options to allow the user to take a photo with their phone's camera if it's present on the device, as well as uploading a photo from the gallery
     */

    //main ask question buttons
    Button _submit;
    Button _cancel;
    Button _groupies;
    Button _privacy;
    //used for adding images
    //TODO: use better creative for these images
    ImageButton _img_btn_q;
    ImageButton _img_btn_one;
    ImageButton _img_btn_two;

    //used for holding user input regarding the question and the options
    EditText _question;
    EditText _choice1;
    EditText _choice2;

    //internal use only. Used for tracking whether or not an image file has been uploaded
    int _b1_id; //for q
    int _b2_id; //for op1
    int _b3_id; //for op2

    //these will hold the actual image files, or will be null
    ParseFile file1;
    ParseFile file2;
    ParseFile file3;


    //This is used when choosing a photo from the gallery. Leave it alone for now.
    private static int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question_test);

        /*Inflate the menu that runs along the bottom of the screen
        *
        TODO: Abstract this shit into a function since it shows up in most of our activities.
        *
         */
        ImageButton i_ask = (ImageButton) findViewById(R.id.image_button_ask);
        ImageButton i_in = (ImageButton) findViewById(R.id.image_button_incoming);
        ImageButton i_out = (ImageButton) findViewById(R.id.image_button_outgoing);
        ImageButton i_set = (ImageButton) findViewById(R.id.image_button_settings);
        //Assign the click functionality
        //i_ask is active, so don't give it a listener.
        i_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AskQuestionActivity.this, ViewMyQuestionsActivity.class);
                startActivity(intent);
            }
        });
        i_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AskQuestionActivity.this, ViewQuestionsActivity.class);
                startActivity(intent);
            }
        });
        i_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AskQuestionActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        /**********************
         *
         * Move into the main codez
         * **********************/
        _question = (EditText) findViewById(R.id.fld_question_body);
        _choice1 = (EditText) findViewById(R.id.fld_choice1);
        _choice2 = (EditText) findViewById(R.id.fld_choice2);
        _submit = (Button) findViewById(R.id.btn_ask);
        _cancel = (Button) findViewById(R.id.btn_ask_cancel);
        _groupies = (Button) findViewById(R.id.btn_ask_groupies);
        _privacy = (Button) findViewById(R.id.btn_ask_privacy);
        _img_btn_q = (ImageButton) findViewById(R.id.img_btn_question);
        _img_btn_one = (ImageButton) findViewById(R.id.img_btn_one);
        _img_btn_two = (ImageButton) findViewById(R.id.img_btn_two);

        /*
        This button handles the task of submitting a user's question
        TODO: Refactor all of this shit. But namely:
        1. Make sure that we are using the new database model properly
        2. Make sure that questions are first getting cached into local storage via pinInBackground. This is related to #1
        3. See if/where cloud code can be used
         */
        _submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String q = _question.getText().toString();
                String c1 = _choice1.getText().toString();
                String c2 = _choice2.getText().toString();

                final ParseObject userQuestion = new ParseObject("SocialQs");
                ParseUser currentUser = ParseUser.getCurrentUser();

                final ParseACL acl = new ParseACL();
                acl.setPublicReadAccess(true);
                acl.setPublicWriteAccess(true);

                if (currentUser != null) {

                } else {
                    // show the signup or login screen
                    Toast.makeText(getApplicationContext(), "User logged out?",
                            Toast.LENGTH_LONG).show();
                }

                if((q.equals("")) || (c1.equals("")) || (c2.equals(""))){
                    Toast.makeText(getApplicationContext(), "Error: One or more required parameters is empty",
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AskQuestionActivity.this, AskQuestionActivity.class);
                    startActivity(intent);
                }

                userQuestion.put("question", q);
                userQuestion.put("option1", c1);
                userQuestion.put("option2", c2);
                userQuestion.put("stats1",0);
                userQuestion.put("stats2",0);
                userQuestion.put("asker",ParseUser.getCurrentUser());//store the actual user object
                userQuestion.put("askerId",currentUser.getObjectId());
                userQuestion.put("askerDeleted",false);
                userQuestion.setACL(acl);

                if(file1 != null){
                    userQuestion.put("questionPhoto", file1);
                }
                if(file2 != null){
                    userQuestion.put("option1Photo",file2);
                }
                if(file3 != null){
                    userQuestion.put("option2Photo",file3);
                }

                /*
                TODO: make #1 and #2 execute in the Parse cloud instead of on device
                 */
                userQuestion.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            //#1 Create the join
                            ParseObject userJoin = new ParseObject("QJoin");
                            userJoin.put("asker",ParseUser.getCurrentUser());
                            userJoin.put("to",ParseUser.getCurrentUser().getUsername());
                            userJoin.put("from",ParseUser.getCurrentUser());
                            userJoin.put("sender",ParseUser.getCurrentUser().getUsername());
                            userJoin.put("question",userQuestion);
                            userJoin.put("vote",0);
                            userJoin.put("askeeDeleted",false);
                            userJoin.setACL(acl);
                            userJoin.saveInBackground(new SaveCallback() {
                                public void done(ParseException e) {
                                    if (e == null) {
                                        //Let's write some client side code for this until the goddamned server side shit gets figured out
                                        /*for(int i = 0; i < GroupiesActivity.myCurrentGroupies.size(); i++){
                                            //create a new join object and send that bitch up
                                            ParseObject gJoin = new ParseObject("QJoin");
                                            gJoin.put("asker",ParseUser.getCurrentUser());
                                            if("ee".equals(GroupiesActivity.myCurrentGroupies.get(i).get("userData").getName())) {
                                                Toast.makeText(getApplicationContext(), "Fucked it",
                                                        Toast.LENGTH_LONG).show();
                                                gJoin.put("to", GroupiesActivity.myCurrentGroupies.get(i).get("userData").getName());
                                            }
                                            gJoin.put("from",ParseUser.getCurrentUser());
                                            gJoin.put("vote",0);
                                            gJoin.put("sender", ParseUser.getCurrentUser().getUsername());
                                            gJoin.put("question",userQuestion);
                                            gJoin.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if(e == null){
                                                        Toast.makeText(getApplicationContext(), "Gott it",
                                                                Toast.LENGTH_LONG).show();
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "Error creating join: " + e,
                                                                Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }*/
                                        //let's go ahead and update the groupies. I also don't think that we need a new activity here, just
                                        //clear out the groupies and the text fields. Although iOs is sending users to their myQs screen...
                                       /* ParseUser cur = ParseUser.getCurrentUser();
                                       // HashMap<String, GroupiesCloudWrapper> params = new HashMap<String, GroupiesCloudWrapper>();
                                        //GroupiesCloudWrapper tempWrap = new GroupiesCloudWrapper(ParseUser.getCurrentUser(),userQuestion,GroupiesActivity.myCurrentGroupies);
                                        HashMap<String,String> params = new HashMap<String,String>();
                                        params.put("userId", (String) ParseUser.getCurrentUser().getObjectId());
                                        params.put("questionId", (String) userQuestion.getObjectId());
                                        for(int i = 0; i < GroupiesActivity.myCurrentGroupies.size();i++){
                                            params.put("user"+i,GroupiesActivity.myCurrentGroupies.get(i).get("userData").getName());
                                        }
                                        params.put("count",String.valueOf(GroupiesActivity.myCurrentGroupies.size()));
                                        //hit the cloud to find the user, or user suggestions
                                        ParseCloud.callFunctionInBackground("askToGroupies", params, new FunctionCallback<String>() {
                                            public void done(String names, ParseException e) {
                                                if (e == null) {
                                                    Toast.makeText(AskQuestionActivity.this, "nah man, mad people was frontin",
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(AskQuestionActivity.this, "shitshitshit" + e,
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });*/
                                        //

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Error creating Qjoin: " + e,
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            for(int i = 0; i < GroupiesActivity.myCurrentGroupies.size(); i++){
                                //create a new join object and send that bitch up
                                ParseObject gJoin = new ParseObject("QJoin");
                                gJoin.put("asker",ParseUser.getCurrentUser());
                               // if("ee".equals(GroupiesActivity.myCurrentGroupies.get(i).get("userData").getName())) {
                                 //   Toast.makeText(getApplicationContext(), "Fucked it",
                                   //         Toast.LENGTH_LONG).show();
                                    gJoin.put("to", GroupiesActivity.myCurrentGroupies.get(i).get("userData").getName());
                                //}
                                gJoin.put("from",ParseUser.getCurrentUser());
                                gJoin.put("vote",0);
                                gJoin.put("sender", ParseUser.getCurrentUser().getUsername());
                                gJoin.put("question",userQuestion);
                                gJoin.put("askeeDeleted",false);
                                gJoin.setACL(acl);
                                gJoin.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e == null){
                                            Toast.makeText(getApplicationContext(), "Gott it",
                                                    Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Error creating join: " + e,
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Error Asking Question: " + e,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

                Toast.makeText(AskQuestionActivity.this, "Question Submitted",
                        Toast.LENGTH_SHORT).show();

            }
        });

        /*
        This should clear out the user text and any images
        TODO: Once groupies are integrated, make sure that this updates a group approptiately.
        It probably doesn't make sense to burn the group just because someone nixed the Q?
         */
        _cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Question Cancelled",
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AskQuestionActivity.this, AskQuestionActivity.class);
                startActivity(intent);
            }
        });

        /*
        TODO: Make all of this shit work.
         */
        _groupies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Make sure to juggle the intent stack properly here, so that we don't nuke
                //the current askquestion activity
                Intent intent = new Intent(AskQuestionActivity.this, GroupiesActivity.class);
                startActivity(intent);
            }
        });

        /*
        TODO: Eventually, figure out how to do all of this shit.
         */
        _privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Privacy settings will be available in a future release",
                        Toast.LENGTH_LONG).show();
            }

        });

        /*
        This shit is used for uploading images.
        TODO: Talk to Weezy to figure out how this is currently being done with thumbnails and high res images
         */
        _img_btn_q.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _b1_id = v.getId();
                loadImagefromGallery(v,_b1_id);
            }

        });
        _img_btn_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _b2_id = v.getId();
                loadImagefromGallery(v,_b2_id);
            }

        });
        _img_btn_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _b3_id = v.getId();
                loadImagefromGallery(v,_b3_id);
            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ask_question, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /*
    This is used along with a couple of other function for pulling an image from the gallery
    TODO: update this shit to use the phone's camera as well
     */
    public void loadImagefromGallery(View view, int k){
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    //used in conjunction with the above function to pull an image from the gallery/camera
    /*
    TODO: Refactor the shit out of this shit
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            //try to update the image that was passed via the intent
            if(_b1_id > 0){
                ImageButton _clk = (ImageButton) findViewById(_b1_id);
                _clk.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                // Convert it to byte
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Compress image to lower quality scale 1 - 100
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] image = stream.toByteArray();
                //update the parsefile
                file1 = new ParseFile("q1.png", image);
                // Upload the image into Parse Cloud
               // file1.saveInBackground();

                // Create a New Class called "ImageUpload" in Parse
                ParseObject imgupload = new ParseObject("ImageUpload");

                // Create a column named "ImageName" and set the string
                imgupload.put("ImageName", "Quesion_img");

                // Create a column named "ImageFile" and insert the image
                imgupload.put("ImageFile", file1);

                // Create the class and the columns
               // imgupload.saveInBackground();
                _b1_id = 0;
            } else if(_b2_id > 0) {
                ImageButton _clk = (ImageButton) findViewById(_b2_id);
                _clk.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                // Convert it to byte
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Compress image to lower quality scale 1 - 100
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] image = stream.toByteArray();
                //update the parsefile
                file2 = new ParseFile("o1.png", image);
                // Upload the image into Parse Cloud
             //   file2.saveInBackground();

                // Create a New Class called "ImageUpload" in Parse
                ParseObject imgupload = new ParseObject("ImageUpload");

                // Create a column named "ImageName" and set the string
                imgupload.put("ImageName", "Quesion_img");

                // Create a column named "ImageFile" and insert the image
                imgupload.put("ImageFile", file2);

                // Create the class and the columns
             //   imgupload.saveInBackground();
                _b2_id = 0;
            } else if (_b3_id > 0) {
                ImageButton _clk = (ImageButton) findViewById(_b3_id);
                _clk.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                // Convert it to byte
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Compress image to lower quality scale 1 - 100
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] image = stream.toByteArray();
                //update the parsefile
                file3 = new ParseFile("o2.png", image);
                // Upload the image into Parse Cloud
          //      file3.saveInBackground();

                // Create a New Class called "ImageUpload" in Parse
                ParseObject imgupload = new ParseObject("ImageUpload");

                // Create a column named "ImageName" and set the string
                imgupload.put("ImageName", "Quesion_img");

                // Create a column named "ImageFile" and insert the image
                imgupload.put("ImageFile", file3);

                // Create the class and the columns
           //     imgupload.saveInBackground();
                _b3_id = 0;
            } else {
                //uh oh...
                Toast.makeText(AskQuestionActivity.this, "Error with the intent",
                        Toast.LENGTH_SHORT).show();
            }

        }


    }

    public class GroupiesCloudWrapper{
        public ParseUser user;
        public ParseObject question;
        public ArrayList<HashMap<String,GroupiesActivity.GroupiesObject>> groupies;

        public GroupiesCloudWrapper(ParseUser u, ParseObject q, ArrayList<HashMap<String,GroupiesActivity.GroupiesObject>> g){
            this.user = u;
            this.question = q;
            this.groupies = g;
        }
    }
}
