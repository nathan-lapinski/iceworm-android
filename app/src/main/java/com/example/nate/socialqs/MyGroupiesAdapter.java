package com.example.nate.socialqs;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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

import org.w3c.dom.Text;

import java.io.InputStream;
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
    View toggle_view;
    public MyGroupiesAdapter(Activity activity, List<HashMap<String,GroupiesActivity.GroupiesObject>> data, View masterRef){
        super(activity, R.layout.row_question, data);
        inflater = activity.getWindow().getLayoutInflater();
        this.master_list = data;
        this.toggle_view = masterRef;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        HashMap<String,GroupiesActivity.GroupiesObject> gObj  = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.groupies_row, parent, false);
        }
        //simply display the user name for now
        TextView tt = (TextView) convertView.findViewById(R.id.textViewQuestionText);
        tt.setText(gObj.get("userData").getName()); //userData should return a GroupiesObject.
        ImageView ii = (ImageView) convertView.findViewById(R.id.imageView2);
        ii.setImageBitmap(gObj.get("userData").getBitmap());
        View.OnClickListener my_test = new MyCustomListener(gObj,position,this.toggle_view);
        tt.setOnClickListener(my_test);
        return convertView;
    }

    public class MyCustomListener implements View.OnClickListener
    {
        HashMap<String,GroupiesActivity.GroupiesObject> my_obj;
        int position;
        View invisible_touch;
        public MyCustomListener(HashMap<String,GroupiesActivity.GroupiesObject> row, int pos, View invisibleTouch) {
            this.my_obj = row; this.position = pos; this.invisible_touch = invisibleTouch;
        }
        @Override
        public void onClick(View v)
        {
            final View j = v;
            GroupiesActivity.myCurrentGroupies.add(my_obj);
            Toast.makeText(getContext(), "Added it, list now has: "+GroupiesActivity.myCurrentGroupies.size()+" groupies",
                    Toast.LENGTH_LONG).show();
            this.invisible_touch.setVisibility(View.VISIBLE);
            TextView hole = (TextView)this.invisible_touch.findViewById(R.id.groupiesTextView);
            String tmp = hole.getText().toString();
            hole.setText(tmp + my_obj.get("userData").getName() + " ");
        }

    };

    //For pulling down fb images directly
    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
