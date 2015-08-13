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
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.util.List;


public class AskQuestionActivity extends ActionBarActivity {

    Button _submit;
    Button _cancel;
    Button _groupies;
    Button _privacy;
    ImageButton _img_btn_q;
    ImageButton _img_btn_one;
    ImageButton _img_btn_two;

    EditText _question;
    EditText _choice1;
    EditText _choice2;

    int _b1_id;
    int _b2_id;
    int _b3_id;

    ParseFile file1;
    ParseFile file2;
    ParseFile file3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question_test);
        /*Inflate the menu -- put this in a function at some point*/
        ImageButton i_ask = (ImageButton) findViewById(R.id.image_button_ask);
        ImageButton i_in = (ImageButton) findViewById(R.id.image_button_incoming);
        ImageButton i_out = (ImageButton) findViewById(R.id.image_button_outgoing);
        ImageButton i_set = (ImageButton) findViewById(R.id.image_button_settings);
        //Assign the click functionality
        //i_ask is active
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
        /*****************/
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

        _submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String q = _question.getText().toString();
                String c1 = _choice1.getText().toString();
                String c2 = _choice2.getText().toString();

                final ParseObject userQuestion = new ParseObject("SocialQs");
                ParseUser currentUser = ParseUser.getCurrentUser();

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

            //    userQuestion.put("asker",currentUser.getUsername());
                userQuestion.put("question", q);
                userQuestion.put("option1", c1);
                userQuestion.put("option2", c2);
                userQuestion.put("stats1",0);
                userQuestion.put("stats2",0);
                userQuestion.put("askername",currentUser.getUsername());
                userQuestion.put("askerId",currentUser.getObjectId());
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
                            //#1create a new entry in the votes table that
                            //corresponds to this question
                            final ParseObject vote = new ParseObject("Votes");
                            vote.saveInBackground(new SaveCallback() {
                                public void done(ParseException e) {
                                    if (e == null) {
                                        //find the user..?
                                        userQuestion.put("votesId", vote.getObjectId());
                                        userQuestion.saveInBackground();
                                        //start the success activity, put this in a callback
                                        Intent intent = new Intent(AskQuestionActivity.this, AskQuestionActivity.class);
                                        startActivity(intent);

                                    } else {
                                        //fail
                                    }
                                }
                            });
                            //get your userId and add it there
                            //then, get an array of userIds of groupies and add it there
                            //#2Similarily, create a corresponding userqs entry.
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("UserQs");
                            // query.whereNotEqualTo("objectId", ParseUser.getCurrentUser().get("uQId"));
                            query.findInBackground(new FindCallback<ParseObject>() {
                                public void done(List<ParseObject> scoreList, ParseException e) {
                                    if (e == null) {
                                        for (int i = 0; i < scoreList.size(); i++) {
                                            if (scoreList.get(i).getObjectId().equals(ParseUser.getCurrentUser().get("uQId"))) {
                                                scoreList.get(i).addUnique("myQsId", userQuestion.getObjectId());
                                                scoreList.get(i).saveInBackground();
                                            } else {
                                                scoreList.get(i).addUnique("theirQsId", userQuestion.getObjectId());
                                                scoreList.get(i).saveInBackground();
                                            }
                                        }

                                    } else {
                                        Log.d("score", "Error: " + e.getMessage());
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(getApplicationContext(), "Error Creating User: " + e,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

                Toast.makeText(AskQuestionActivity.this, "Question Submitted",
                        Toast.LENGTH_SHORT).show();

            }
        });

        _cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Question Cancelled",
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AskQuestionActivity.this, AskQuestionActivity.class);
                startActivity(intent);
            }
        });

        _groupies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Group functionality will be available in a future release",
                        Toast.LENGTH_LONG).show();
            }
        });

        _privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Privacy settings will be available in a future release",
                        Toast.LENGTH_LONG).show();
            }

        });

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

    //-----------------
    private static int RESULT_LOAD_IMAGE = 1;
    public void loadImagefromGallery(View view, int k){
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }
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
}
