package com.example.nate.socialqs;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.DialogInterface;
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
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class AskQuestionActivity extends ActionBarActivity {
    /*
    TODO:
    0!!: We can now access the current groupies! The cloud will handle populating the join table for us, so write cloud code to take the groupies
    and populate the join on submit. Then reset that groupie object.
    1. (major) Update AskQuestion to use the new database model
    2. (major) Update photo options to allow the user to take a photo with their phone's camera if it's present on the device, as well as uploading a photo from the gallery
     */
    //If there are any errors that would prevent the question from being sent, we bump this to false
    boolean can_ask = true;

    //main ask question buttons
    Button _submit;
    Button _cancel;
    Button _privacy;
    //used for adding images
    //TODO: use better creative for these images
    ImageView _img_btn_q;
    ImageView _img_btn_one;
    ImageView _img_btn_two;
    ImageView _groupies;
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

    //used for detecting the active image
    int active_image = 0;

    //TODO: Remove or refactor this code
    //Testing code for the new cloud function for asking questions
    byte[] qFull;
    byte[] qThumb;
    byte[] o1Full;
    byte[] o1Thumb;
    byte[] o2Full;
    byte[] o2Thumb;

    //This is used when choosing a photo from the gallery. Leave it alone for now.
    private static int RESULT_LOAD_IMAGE = 1;
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question_test);

        _question = (EditText) findViewById(R.id.fld_question_body);
        _choice1 = (EditText) findViewById(R.id.fld_choice1);
        _choice2 = (EditText) findViewById(R.id.fld_choice2);
        _submit = (Button) findViewById(R.id.btn_ask);
        _cancel = (Button) findViewById(R.id.btn_ask_cancel);
        _img_btn_q = (ImageView) findViewById(R.id.img_btn_question);
        _img_btn_one = (ImageView) findViewById(R.id.img_btn_one);
        _img_btn_two = (ImageView) findViewById(R.id.img_btn_two);

        Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        _groupies = (ImageView)findViewById(R.id.groupies);
        _groupies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AskQuestionActivity.this, GroupiesActivity.class);
                startActivity(intent);
            }
        });
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
                can_ask = true;
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
                    FragmentManager manager = getFragmentManager();
                    Fragment frag = manager.findFragmentByTag("fragment_edit_name");
                    if (frag != null) {
                        manager.beginTransaction().remove(frag).commit();
                    }
                    ErrorFragment editNameDialog = new ErrorFragment();
                    editNameDialog.show(manager, "fragment_edit_name");
                    can_ask = false;
                }

                if(GroupiesActivity.myCurrentGroupies.size() <= 0){
                    FragmentManager manager = getFragmentManager();
                    Fragment frag = manager.findFragmentByTag("fragment_edit_name");
                    if (frag != null) {
                        manager.beginTransaction().remove(frag).commit();
                    }
                    ErrorFragment editNameDialog = new ErrorFragment();
                    editNameDialog.show(manager, "fragment_edit_name");
                    can_ask = false;
                }
                //Testing cloud
                /*
                final HashMap<String, Object> params = new HashMap<String, Object>();
                final HashMap<String,Object> optionImageThumbs = new HashMap<String, Object>();
                final HashMap<String,Object> optionImageFull = new HashMap<String, Object>();
                optionImageThumbs.put("q",qThumb);
                optionImageFull.put("q",qFull);
                optionImageThumbs.put("1",o1Thumb);
                optionImageThumbs.put("2",o2Thumb);
                optionImageFull.put("1",o1Full);
                optionImageFull.put("2",o2Full);
                params.put("optionImageThumbs",optionImageThumbs);
                params.put("optionImageFull",optionImageFull);
                final HashMap<String,String> strData = new HashMap<String, String>();
                strData.put("questionText","newtesttext");
                strData.put("option1Text","option1test");
                strData.put("option2Text", "option2test");
                params.put("stringData",strData);
                params.put("currentUserId",ParseUser.getCurrentUser().getObjectId());
                ArrayList<String> facebook = new ArrayList<String>();
                facebook.add(ParseUser.getCurrentUser().getString("facebookId"));
                for(int c = 0; c < GroupiesActivity.myCurrentGroupies.size(); c++){
                    facebook.add(GroupiesActivity.myCurrentGroupies.get(c).get("userData").getId());
                }
                HashMap<String,ArrayList<String>> to = new HashMap<String, ArrayList<String>>();
                to.put("facebook",facebook);
                params.put("to",to);
                ParseCloud.callFunctionInBackground("submitQuestion", params, new FunctionCallback<Map<String, Object>>() {
                    public void done(Map<String, Object> mapObject, ParseException e) {
                        if (e == null) {
                             Toast.makeText(getApplicationContext(), "Let's have a toast for the douchebags ", Toast.LENGTH_LONG).show();
                            //let's try to pull down the socialq
                            ParseQuery testQ = new ParseQuery("SocialQs");
                            testQ.include("imagesArray");
                            testQ.getFirstInBackground(new GetCallback<ParseObject>() {
                                public void done(ParseObject object, ParseException e) {
                                    if (object == null) {
                                        Log.d("score", "The getFirst request failed.");
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Let's have a toast for the assholes ", Toast.LENGTH_LONG).show();
                                        JSONArray imagesMy = object.get("imagesArray");
                                        Toast.makeText(getApplicationContext(), ""+object.get("imagesArray"), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(getApplicationContext(), "Cloud failed " + e,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });*/ //End testing cloud


                if(can_ask) {
                    userQuestion.put("questionText", q);
                    userQuestion.put("option1Text", c1);
                    userQuestion.put("option2Text", c2);
                    userQuestion.put("option1Stats", 0);
                    userQuestion.put("option2Stats", 0);
                    userQuestion.put("asker", ParseUser.getCurrentUser());//store the actual user object
                    userQuestion.put("askerId", currentUser.getObjectId());
                    userQuestion.setACL(acl);

                    if (file1 != null) {
                        userQuestion.put("questionImageFull", file1);
                        userQuestion.put("questionImageThumb", file1);
                    }
                    if (file2 != null) {
                        userQuestion.put("option1ImageFull", file2);
                        userQuestion.put("option1ImageThumb", file2);
                    }
                    if (file3 != null) {
                        userQuestion.put("option2ImageFull", file3);
                        userQuestion.put("option2ImageThumb", file3);
                    }

                    userQuestion.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                //#1 Create the join
                                ParseObject userJoin = new ParseObject("QJoin");
                                userJoin.put("asker", ParseUser.getCurrentUser());
                                userJoin.put("to", ParseUser.getCurrentUser()); //this is a *User now
                                userJoin.put("from", ParseUser.getCurrentUser());
                                // userJoin.put("sender",ParseUser.getCurrentUser().getUsername());
                                userJoin.put("question", userQuestion);
                                //userJoin.put("vote", 0);
                                userJoin.put("deleted", false);
                                userJoin.setACL(acl);
                                userJoin.saveInBackground(new SaveCallback() {
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            //TODO: Something here?

                                        } else {
                                            Toast.makeText(getApplicationContext(), "Error creating Qjoin: " + e,
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                //create an array to query
                                ArrayList<String> groupiesId = new ArrayList<String>();
                                final LinkedList<String> channels = new LinkedList<String>();
                                for (int i = 0; i < GroupiesActivity.myCurrentGroupies.size(); i++) {
                                    groupiesId.add(GroupiesActivity.myCurrentGroupies.get(i).get("userData").getId());
                                }
                                //find them
                                ParseQuery getGroupies = new ParseQuery("_User");
                                getGroupies.whereContainedIn("facebookId",groupiesId);
                                getGroupies.findInBackground(new FindCallback<ParseObject>() {
                                    public void done(final List<ParseObject> resList, ParseException e) {
                                        if (e == null) {
                                            //you know have objects for each of the groupies. Send up the qJoins
                                            ArrayList<ParseObject> theQJoins = new ArrayList<ParseObject>();
                                            for(int i = 0; i < resList.size(); i++){
                                                ParseObject gJoin = new ParseObject("QJoin");
                                                gJoin.put("asker", ParseUser.getCurrentUser());
                                                gJoin.put("to", resList.get(i));
                                                gJoin.put("from", ParseUser.getCurrentUser());
                                               // gJoin.put("vote",null); //TODO:Test this
                                                //  gJoin.put("sender", ParseUser.getCurrentUser().getUsername());
                                                gJoin.put("question", userQuestion);
                                                gJoin.put("deleted", false);
                                                gJoin.setACL(acl);
                                                theQJoins.add(gJoin);
                                                String new_channel = "user_" + resList.get(i).getObjectId();
                                                channels.add(new_channel);

                                            }
                                            ParseObject.saveAllInBackground(theQJoins);
                                            ParsePush push = new ParsePush();
                                            push.setChannels(channels); // Notice we use setChannels not setChannel
                                            JSONObject data = new JSONObject();
                                            try {
                                                data.put("alert", "New Q from Nate"); //TODO: Change
                                                data.put("badge", "Increment");
                                                data.put("content-available", "1");
                                                data.put("action", "newQ");
                                            }catch (JSONException ee){

                                            }
                                            push.setData(data);
                                            //push.setMessage("Nate sent you a new SocialQ!");
                                            push.sendInBackground();

                                        } else {
                                            Toast.makeText(getApplicationContext(), "faaack man", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                //
                               /* for (int i = 0; i < GroupiesActivity.myCurrentGroupies.size(); i++) {
                                    //create a new join object and send that bitch up
                                    ParseObject gJoin = new ParseObject("QJoin");
                                    gJoin.put("asker", ParseUser.getCurrentUser());
                                    gJoin.put("to", GroupiesActivity.myCurrentGroupies.get(i).get("userData").getId());
                                    gJoin.put("from", ParseUser.getCurrentUser());
                                    gJoin.put("vote", 0);
                                    //  gJoin.put("sender", ParseUser.getCurrentUser().getUsername());
                                    gJoin.put("question", userQuestion);
                                    gJoin.put("deleted", false);
                                    gJoin.setACL(acl);
                                    gJoin.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                //Toast.makeText(getApplicationContext(), "Gott it",
                                                //      Toast.LENGTH_LONG).show();
                                            } else {
                                                //Toast.makeText(getApplicationContext(), "Error creating join: " + e,
                                                //      Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }*/
                                //at this point, the q is asked. so clear the current groupies
                                GroupiesActivity.myCurrentGroupies.clear();
                                //TODO: Write a function that updates the ui here as well
                                //reset image views (re-inflate for now?)
                                //clear files
                                //clear text fields
                                _question.setText("");
                                _choice1.setText("");
                                _choice2.setText("");
                                file1 = file2 = file3 = null;
                                _img_btn_q.setImageResource(R.drawable.camera);
                                _img_btn_one.setImageResource(R.drawable.camera);
                                _img_btn_two.setImageResource(R.drawable.camera);

                            } else {
                                Toast.makeText(getApplicationContext(), "Error Asking Question: " + e,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    Toast.makeText(AskQuestionActivity.this, "Question Submitted",
                            Toast.LENGTH_SHORT).show();
                }else{
                    //can't ask :(
                    Toast.makeText(getApplicationContext(), "Failed to ask question. Please try again!",
                            Toast.LENGTH_LONG).show();
                }
            }//end onclick
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
                GroupiesActivity.myCurrentGroupies.clear();
                //TODO: Use the ui function from above here
                Intent intent = new Intent(AskQuestionActivity.this, AskQuestionActivity.class);
                startActivity(intent);
                finish();
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
                active_image = 1;
                selectImage();
               // loadImagefromGallery(v,_b1_id);
            }

        });
        _img_btn_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _b2_id = v.getId();
                active_image = 2;
                selectImage();
                //loadImagefromGallery(v,_b2_id);
            }

        });
        _img_btn_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _b3_id = v.getId();
                active_image = 3;
                selectImage();
                //loadImagefromGallery(v,_b3_id);
            }

        });

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

    //for displaying the popup dialog
    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library", "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(AskQuestionActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                //thumbnail is now the bitmap that you want. Do something with it.
                //find the active imageview
                switch(active_image){
                    case 1:
                        ImageView pic = (ImageView)findViewById(R.id.img_btn_question);
                        pic.setImageBitmap(thumbnail);
                        prepareImageForParse(bytes,"q1.png",1);
                        active_image = 0;
                        break;
                    case 2:
                        ImageView pic2 = (ImageView)findViewById(R.id.img_btn_one);
                        pic2.setImageBitmap(thumbnail);
                        prepareImageForParse(bytes, "o1.png", 2);
                        active_image = 0;
                        break;
                    case 3:
                        ImageView pic3 = (ImageView)findViewById(R.id.img_btn_two);
                        pic3.setImageBitmap(thumbnail);
                        prepareImageForParse(bytes,"o2.png",3);
                        active_image = 0;
                        break;
                    default:
                }
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                        null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);
                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);
                //bm is now the bitmap. Do something with it
                switch(active_image){
                    case 1:
                        ImageView pic = (ImageView)findViewById(R.id.img_btn_question);
                        pic.setImageBitmap(bm);
                        prepareImageForParse(bm, "q1.png", 1);
                        active_image = 0;
                        break;
                    case 2:
                        ImageView pic2 = (ImageView)findViewById(R.id.img_btn_one);
                        pic2.setImageBitmap(bm);
                        prepareImageForParse(bm, "o1.png", 2);
                        active_image = 0;
                        break;
                    case 3:
                        ImageView pic3 = (ImageView)findViewById(R.id.img_btn_two);
                        pic3.setImageBitmap(bm);
                        prepareImageForParse(bm, "o2.png", 3);
                        active_image = 0;
                        break;
                    default:
                }

            }
        } else {
            //handle the error
        }
    }//end function

    //TODO: May need to implement the previous compression algorithm here as well.
    public void prepareImageForParse(ByteArrayOutputStream b,String s, int i){
        byte[] image = b.toByteArray();
        switch(i) {
            case 1:
                file1 = new ParseFile(s, image);
                break;
            case 2:
                file2 = new ParseFile(s, image);
                break;
            case 3:
                file3 = new ParseFile(s, image);
                break;
            default:
        }
    }
    public void prepareImageForParse(Bitmap b,String s, int i){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Compress image to lower quality scale 1 - 100
        b.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] image = stream.toByteArray();

        //test cloud
        //-----
       /* final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("fileData", image);
        ParseCloud.callFunctionInBackground("testImageUpload", params, new FunctionCallback<Map<String, Object>>() {
            public void done(Map<String, Object> mapObject, ParseException e) {
                if (e == null) {
                      Toast.makeText(getApplicationContext(), "testing the cloud and looking good", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Cloud failed " + e,
                            Toast.LENGTH_LONG).show();
                }
            }
        });*/
        //

        switch(i) {
            case 1:
                file1 = new ParseFile(s, image);
                //testing
                qFull = image;
                qThumb = image;
                //
                break;
            case 2:
                file2 = new ParseFile(s, image);
                //testing
                o1Full = image;
                o1Thumb = image;
                //
                break;
            case 3:
                file3 = new ParseFile(s, image);
                //testing
                o2Full = image;
                o2Thumb = image;
                //
                break;
            default:
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
